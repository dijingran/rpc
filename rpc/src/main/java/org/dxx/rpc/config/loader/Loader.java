package org.dxx.rpc.config.loader;

import java.util.Set;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.WebUtils;
import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.RpcConfig;
import org.dxx.rpc.config.RpcMockConfigs;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {
	static Logger logger = LoggerFactory.getLogger(Loader.class);

	private static RpcConfig rpcConfig;
	private static RpcClientConfigs rpcClientConfigs;
	private static RpcMockConfigs rpcMockConfigs;

	public static Set<Class<?>> getRpcServices(String packages) {
		String r = RpcConstants.class.getName().replace('.', '/') + ".class";
		if (Thread.currentThread().getContextClassLoader().getResource(r) != null) {
			return new ScanUtils().getPackageAllClasses(packages, true);
		}
		if (WebUtils.getSc() == null) {
			throw new RpcException("Can not resolve servlet context, please use RpcListener!");
		}
		return new WebClassDetector(WebUtils.getWebroot()).getClasses(packages);
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

	public static RpcMockConfigs getMockConfigs() {
		if (Thread.currentThread().getContextClassLoader().getResource("RpcMock.xml") == null) {
			return new RpcMockConfigs();
		}
		if (rpcMockConfigs == null) {
			rpcMockConfigs = JaxbMapper.fromClasspathXmlFile("RpcMock.xml", RpcMockConfigs.class);
			logger.debug("RpcMockConfigs : {}", rpcMockConfigs);
		}
		return rpcMockConfigs;
	}

}
