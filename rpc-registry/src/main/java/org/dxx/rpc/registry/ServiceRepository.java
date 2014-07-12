package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	Logger logger = LoggerFactory.getLogger(ServiceRepository.class);

	private static ServiceRepository instance = new ServiceRepository();

	public static ServiceRepository getInstance() {
		return instance;
	}

	private ServiceRepository() {
	}

	/** url, services */
	private Map<String, List<Service>> services = new ConcurrentHashMap<String, List<Service>>();

	/** interfaceClass, "host:port..." */
	private Map<String, List<String>> interAndUrl = new ConcurrentHashMap<String, List<String>>();

	/** 192.168.1.78:50020|xxx.ccc.XxServic */
	private Set<String> pausedInterfaces = new CopyOnWriteArraySet<String>();

	public RegisterResponse register(String host, int port, RegisterRequest request) {
		String url = host + ":" + port;
		logger.debug("Registering : {}", url);
		try {
			if (services.containsKey(url)) {
				return new RegisterResponse("URL : " + url + " can be only registered one time!");
			}
			synchronized (interAndUrl) {
				for (Service s : request.getServices()) {
					List<String> urls = interAndUrl.get(s.getInterfaceClass());
					if (urls == null) {
						urls = new ArrayList<String>();
						interAndUrl.put(s.getInterfaceClass(), urls);
					}
					urls.add(url);
					logger.debug("Register : {}", s);
				}
			}
			services.put(url, request.getServices());
			logger.debug("Registered : {}", url);
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
			List<String> urls = null;
			synchronized (interAndUrl) {
				urls = interAndUrl.get(request.getInterfaceClass());
			}
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
			for (String url : urls) {// TODO load balance here
				if (!isDeactive(request, url) && !isPaused(url, request.getInterfaceClass())) {
					finalUrl = url;
					break;
				}
			}
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

		if (serviceList != null && !serviceList.isEmpty()) {
			for (Service s : serviceList) {
				for (String interfaceClass : interAndUrl.keySet()) {
					if (s.getInterfaceClass().equals(interfaceClass)) {
						List<String> urls = interAndUrl.get(interfaceClass);
						if (urls != null) {
							urls.remove(url);
						}
					}
				}
			}

		}

		logger.debug("Unregistered services provided by : {}", url);

	}

	public Map<String, List<Service>> getServices() {
		return services;
	}

	public Map<String, List<String>> getInterAndUrl() {
		return interAndUrl;
	}

	/**
	 * 暂停服务，下次客户端不能注册
	 * <p>
	 *
	 * @param interfaces 
	 */
	public void pause(Map<String, List<String>> interfaces) {
		for (String url : interfaces.keySet()) {
			for (String inter : interfaces.get(url)) {
				pausedInterfaces.add(url + "|" + inter);
			}
		}

		logger.debug("Paused interfaces (after pause) : {}", pausedInterfaces);
	}

	/**
	 * 
	 */
	public void resume(Map<String, List<String>> interfaces) {
		for (String url : interfaces.keySet()) {
			for (String inter : interfaces.get(url)) {
				pausedInterfaces.remove(url + "|" + inter);
			}
		}

		logger.debug("Paused interfaces (after resume) : {}", pausedInterfaces);
	}

	public static boolean isPaused(String url, String interfaceClass) {
		return getInstance().pausedInterfaces.contains(url + "|" + interfaceClass);
	}

}
