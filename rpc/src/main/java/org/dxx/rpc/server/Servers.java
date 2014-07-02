package org.dxx.rpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.config.RpcServiceConfig;
import org.dxx.rpc.config.annotation.RpcNutzService;
import org.dxx.rpc.config.annotation.RpcService;
import org.dxx.rpc.config.annotation.RpcSpringService;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Servers {
	static Logger logger = LoggerFactory.getLogger(Servers.class);

	static DefaultBeanFactoy beanFactoy = new DefaultBeanFactoy();

	/**  */
	public static final Map<Class<?>, Class<?>> interAndImpl = new ConcurrentHashMap<Class<?>, Class<?>>();
	public static final Map<Class<?>, Object> interAndInstance = new ConcurrentHashMap<Class<?>, Object>();

	/** 短暂持有 接口的{@link RpcServiceConfig} 定义，向注册中心发布服务时用到*/
	public static final Map<Class<?>, RpcServiceConfig> serviceConfigs = new HashMap<Class<?>, RpcServiceConfig>();

	public static Object getRpcService(Class<?> interfaceClass) {
		Object instance = interAndInstance.get(interfaceClass);
		if (instance == null) {
			Class<?> implClass = interAndImpl.get(interfaceClass);
			if (implClass == null) {
				throw new RpcException("I don't provide the service : " + interfaceClass.getName());
			}
			try {
				if (implClass.isAnnotationPresent(RpcSpringService.class)) {
					instance = beanFactoy.getSpringBean(implClass);
				} else if (implClass.isAnnotationPresent(RpcNutzService.class)) {
					instance = beanFactoy.getNutzBean(implClass);
				} else {
					instance = beanFactoy.get(implClass);
				}
			} catch (Exception e) {
				throw new RpcException(e.getMessage(), e);
			}
			interAndInstance.put(interfaceClass, instance);
		}

		return instance;
	}

	public static void init(String packages) {
		Set<Class<?>> allClasses = Loader.getRpcServices(packages);

		for (Class<?> c : allClasses) {
			if (!c.isAnnotationPresent(RpcService.class) && !c.isAnnotationPresent(RpcSpringService.class)
					&& !c.isAnnotationPresent(RpcNutzService.class)) {
				continue;
			}
			for (Class<?> i : c.getInterfaces()) {
				try {
					interAndImpl.put(i, c);
					if (c.isAnnotationPresent(RpcSpringService.class)) {
						serviceConfigs.put(i, new RpcServiceConfig(c.getAnnotation(RpcSpringService.class).value()));
					} else if (c.isAnnotationPresent(RpcNutzService.class)) {
						serviceConfigs.put(i, new RpcServiceConfig(c.getAnnotation(RpcNutzService.class).value()));
					} else {
						serviceConfigs.put(i, new RpcServiceConfig(c.getAnnotation(RpcService.class).value()));
					}
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	public static RpcServiceConfig getDef(Class<?> interfaceClass) {
		return serviceConfigs.get(interfaceClass);
	}
}
