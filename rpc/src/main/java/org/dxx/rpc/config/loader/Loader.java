package org.dxx.rpc.config.loader;

import java.util.Set;

import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {
	static Logger logger = LoggerFactory.getLogger(Loader.class);

	private static RpcConfig rpcConfig;
	private static RpcClientConfigs rpcClientConfigs;

	public static Set<Class<?>> getRpcServices(String packages) {
		return new ScanUtils().getPackageAllClasses(packages, true);
	}

	public static RpcConfig getRpcConfig() {
		if (rpcConfig == null) {
			rpcConfig = JaxbMapper.fromClasspathXmlFile("RpcConfig.xml", RpcConfig.class);
			logger.debug("RpcConfig : {}", rpcConfig);
		}
		return rpcConfig;
	}

	public static RpcClientConfigs getRpcClientConfigs() {
		if (Thread.currentThread().getContextClassLoader().getResource("RpcClient.xml") == null) {
			return new RpcClientConfigs();
		}
		if (rpcClientConfigs == null) {
			rpcClientConfigs = JaxbMapper.fromClasspathXmlFile("RpcClient.xml", RpcClientConfigs.class);
			logger.debug("RpcClientConfigs : {}", rpcClientConfigs);
		}
		return rpcClientConfigs;
	}

}
