package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.dxx.rpc.registry.LocateRpcServerResponse.Service;
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
	private Map<String, List<RegisterRequest.Service>> services = new ConcurrentHashMap<String, List<RegisterRequest.Service>>();

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
				for (RegisterRequest.Service s : request.getServices()) {
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

	public LocateRpcServerResponse locateRpcServer(LocateRpcServerRequest request) {
		try {
			LocateRpcServerResponse response = new LocateRpcServerResponse();
			List<Service> list = new ArrayList<LocateRpcServerResponse.Service>(request.getInterfaceClasses().size());
			for (String interfaceClass : request.getInterfaceClasses()) {
				Service s = locateOne(interfaceClass);
				if (s != null) {
					list.add(s);
				}
			}
			response.setServices(list);
			return response;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return new LocateRpcServerResponse(e.getMessage());
		}

	}

	private Service locateOne(String interfaceClass) {
		List<String> urls = null;
		synchronized (interAndUrl) {
			urls = interAndUrl.get(interfaceClass);
		}
		if (urls == null || urls.isEmpty()) {
			logger.warn("Service [{}] not found!", interfaceClass);
			return null;
		}
		for (String url : urls) {// TODO load balance here
			if (!isPaused(url, interfaceClass)) {
				return new Service(interfaceClass, url.split(":")[0], Integer.valueOf(url.split(":")[1]));
			}
		}
		logger.warn("Service [{}] not found !! May be paused!", interfaceClass);
		return null;
	}

	/**
	 * channel中断后，移除此channel下的所有服务
	 */
	public void unregister(String url) {
		if (url == null) {
			return;
		}
		List<RegisterRequest.Service> serviceList = services.remove(url);

		if (serviceList != null && !serviceList.isEmpty()) {
			for (RegisterRequest.Service s : serviceList) {
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

	public Map<String, List<RegisterRequest.Service>> getServices() {
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
