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
 * @author dijingran
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

	/** 192.168.1.78:8080|xxx.ccc.XxServic */
	private Set<String> pausedInterfaces = new CopyOnWriteArraySet<String>();

	public RegisterResponse register(String host, int port, RegisterRequest request) {
		logger.debug("registry : {}:{}", host, port);
		try {
			String url = host + ":" + port;
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
					logger.debug("register : {}", s);
				}
			}

			services.put(url, request.getServices());
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
			logger.warn("service [{}] not found!", interfaceClass);
			return null;
		}
		for (String url : urls) {// TODO load balance here
			if (!isPaused(url, interfaceClass)) {
				return new Service(interfaceClass, url.split(":")[0], Integer.valueOf(url.split(":")[1]));
			}
		}
		logger.warn("service [{}] not found !!", interfaceClass);
		return null;
		//		throw new RegistryException("service [" + interfaceClass + "] is paused!");
	}

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

		logger.debug("pausedInterfaces (after pause) : {}", pausedInterfaces);
	}

	public void resume(Map<String, List<String>> interfaces) {
		for (String url : interfaces.keySet()) {
			for (String inter : interfaces.get(url)) {
				pausedInterfaces.remove(url + "|" + inter);
			}
		}

		logger.debug("pausedInterfaces (after resume) : {}", pausedInterfaces);
	}

	public static boolean isPaused(String url, String interfaceClass) {
		return getInstance().pausedInterfaces.contains(url + "|" + interfaceClass);
	}

}
