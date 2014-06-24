/**
 * RpcUtils.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import org.dxx.rpc.client.Clients;
import org.dxx.rpc.config.RpcConfig;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.server.ServerStartup;
import org.dxx.rpc.server.Servers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具类
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

public class RpcUtils {
	private static final Logger logger = LoggerFactory.getLogger(RpcUtils.class);

	/**
	 * 启动，阻塞直到启动完成
	 * <p>
	 *
	 * @return
	 */
	public static void startup() {
		long start = System.currentTimeMillis();
		logger.info("Starting rpc ...");
		RpcConfig conf = Loader.getRpcConfig();
		Servers.init(conf.getRpcServerConfig().getPackages());

		new ServerStartup(conf.getRpcServerConfig().getPort()).submitAndWait(8000);
		logger.info("Rpc is running! cost {} ms", System.currentTimeMillis() - start);
	}

	/**
	 * 获取接口的代理类，由代理类负责远程调用并返回结果。
	 * <p>
	 *
	 * @param interfaceClass
	 * @return 
	 */
	public static <T> T get(Class<T> interfaceClass) {
		return Clients.getRpcProxy(interfaceClass);
	}

}
