package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管理所有服务
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class ServiceRepository {
	static Logger logger = LoggerFactory.getLogger(ServiceRepository.class);

	private static Balancer balancer = new DefaultBalancer();

	private static ServiceRepository instance = new ServiceRepository();

	public static ServiceRepository getInstance() {
		return instance;
	}

	private ServiceRepository() {
	}

	/** url, services */
	private ConcurrentHashMap<String, List<Service>> services = new ConcurrentHashMap<String, List<Service>>();

	/** interfaceClass, "host:port..." */
	private ConcurrentHashMap<String, Set<String>> interAndUrl = new ConcurrentHashMap<String, Set<String>>();

	/** 192.168.1.78:50020|xxx.ccc.XxServic */
	private Set<String> pausedInterfaces = new CopyOnWriteArraySet<String>();

	public RegisterResponse register(String url, RegisterRequest request) {
		logger.debug("Registering : {}", url);
		try {
			for (Service s : request.getServices()) {
				interAndUrl.putIfAbsent(s.getInterfaceClass(), new CopyOnWriteArraySet<String>());
				interAndUrl.get(s.getInterfaceClass()).add(url);
				logger.debug("Register : {}", s);
			}
			services.put(url, request.getServices());
			logger.debug("Registered : {}", url);
			pushServices();
			return new RegisterResponse();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return new RegisterResponse(e.getMessage());
		}
	}

	private static boolean isDeactive(GetServerLocationRequest request, String url) {
		return request.getDeactiveUrl() != null && url.equals(request.getDeactiveUrl());
	}

	public GetServerLocationResponse getServer(GetServerLocationRequest request) {
		GetServerLocationResponse response = new GetServerLocationResponse();
		response.setId(request.getId());
		try {
			Set<String> urls = interAndUrl.get(request.getInterfaceClass());
			if (urls == null || urls.isEmpty()) {
				logger.warn("Service [{}] not found!", request.getInterfaceClass());
				response.setErrorMessage("Service [" + request.getInterfaceClass() + "] not found!");
				return response;
			}
			String finalUrl = null;
			String secondChance = null;
			for (String url : urls) {
				if (isDeactive(request, url)) {
					secondChance = request.getDeactiveUrl();
					break;
				}
			}

			List<String> optionalUrls = new ArrayList<String>();
			for (String url : urls) {
				if (!isDeactive(request, url) && !isPaused(url, request.getInterfaceClass())) {
					optionalUrls.add(url);
				}
			}

			finalUrl = balancer.select(optionalUrls);

			if (finalUrl == null && secondChance != null) {
				logger.warn("Return the deactive channel : {}", secondChance);
				finalUrl = secondChance;
			}
			if (finalUrl != null) {
				response.setHost(finalUrl.split(":")[0]);
				response.setPort(Integer.valueOf(finalUrl.split(":")[1]));
				response.setServices(services.get(finalUrl));
				return response;
			}

			logger.warn("Service [{}] not found !! May be paused!", request.getInterfaceClass());
			return null;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			response.setErrorMessage(e.getMessage());
		}
		return response;

	}

	/**
	 * channel中断后，移除此channel下的所有服务
	 */
	public void unregister(String url) {
		if (url == null) {
			return;
		}
		List<Service> serviceList = services.remove(url);
		if (serviceList != null) {
			for (Service s : serviceList) {
				for (String interfaceClass : interAndUrl.keySet()) {
					if (s.getInterfaceClass().equals(interfaceClass)) {
						Set<String> urls = interAndUrl.get(interfaceClass);
						if (urls != null) {
							urls.remove(url);
						}
					}
				}
			}

		}
		logger.debug("Unregistered services provided by : {}", url);
		pushServices();
		balancer.reset(url);
	}

	public Map<String, List<Service>> getServices() {
		return services;
	}

	public void pause(Map<String, List<String>> interfaces) {
		for (String url : interfaces.keySet()) {
			for (String inter : interfaces.get(url)) {
				pausedInterfaces.add(url + "|" + inter);
			}
		}
		logger.debug("Paused interfaces (after pause) : {}", pausedInterfaces);
		pushServices();
	}

	public void resume(Map<String, List<String>> interfaces) {
		for (String url : interfaces.keySet()) {
			for (String inter : interfaces.get(url)) {
				pausedInterfaces.remove(url + "|" + inter);
			}
		}
		logger.debug("Paused interfaces (after resume) : {}", pausedInterfaces);
		pushServices();
	}

	public static boolean isPaused(String url, String interfaceClass) {
		return getInstance().pausedInterfaces.contains(url + "|" + interfaceClass);
	}

	/**
	 * While avaliable services changed, push them to clients.
	*/
	private static void pushServices() {
		long s = System.currentTimeMillis();
		UpdateServerLocationRequest request = new UpdateServerLocationRequest();
		request.setInterAndUrl(getAvaliableInterAndUrl());
		Channels.writeAndFlush(request);
		logger.debug("Push services to clients cost : {} ms.", System.currentTimeMillis() - s);

	}

	/**
	 * Return interAndUrl without paused url.
	 * @return
	 */
	private static Map<String, Set<String>> getAvaliableInterAndUrl() {
		Map<String, Set<String>> services = new ConcurrentHashMap<String, Set<String>>();
		ServiceRepository repository = getInstance();
		if (repository.pausedInterfaces.size() > 0) {
			for (Iterator<Entry<String, Set<String>>> iter = repository.interAndUrl.entrySet().iterator(); iter
					.hasNext();) {
				Entry<String, Set<String>> e = iter.next();
				Set<String> urls = new HashSet<String>();
				String interfaceClass = e.getKey();
				for (String url : e.getValue()) {
					if (!isPaused(url, interfaceClass)) {
						urls.add(url);
					}
				}

				services.put(interfaceClass, urls);
			}
		} else {
			services = repository.interAndUrl;
		}
		return services;
	}

}
