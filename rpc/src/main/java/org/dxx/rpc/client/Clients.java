package org.dxx.rpc.client;

import java.util.HashMap;
import java.util.Map;

import org.dxx.rpc.config.RpcClientConfig;
import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clients {
	static Logger logger = LoggerFactory.getLogger(Clients.class);

	private static Map<Class<?>, RpcClientConfig> interAndConfigs;
	private static Map<Class<?>, Object> interAndProxies = new HashMap<Class<?>, Object>();

	private static RpcClientConfig getConfig(Class<?> interfaceClass) {
		if (interAndConfigs == null) {
			interAndConfigs = new HashMap<Class<?>, RpcClientConfig>();
			RpcClientConfigs configs = Loader.getRpcClientConfigs();
			for (RpcClientConfig c : configs.getClients()) {
				interAndConfigs.put(c.getInter(), c);
			}
		}
		return interAndConfigs.get(interfaceClass);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getRpcProxy(Class<T> interfaceClass) {
		if (!ClientStartup.isInitialized()) {
			startupClient();
		}

		T proxy = (T) interAndProxies.get(interfaceClass);
		if (proxy == null) {
			proxy = (T) ProxyFactory.get(interfaceClass, getConfig(interfaceClass));
			interAndProxies.put(interfaceClass, proxy);
		}
		logger.trace("return proxy for interface: {}", interfaceClass);
		return proxy;
	}

	static void startupClient() {
		// call registry and init channels for rpc clients
		ClientStartup.startup();

		// block until registry and client done
		while (true) {
			if (ClientStartup.isInitialized()) {
				break;
			}
			try {
				Thread.sleep(30L);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.debug("client init completed!!!");
	}

}
