/**
 * RpcUtils.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dxx.rpc.client.Clients;
import org.dxx.rpc.config.RpcConfig;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
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

	private static ExecutorService es = Executors.newFixedThreadPool(1);

	/**
	 * 启动（异步）
	 * <p>
	 *
	 * @return
	 */
	public static void startup() {
		logger.info("starting rpc ...");

		RpcConfig rpcConfig = Loader.getRpcConfig();
		Servers.init(rpcConfig.getRpcServerConfig().getPackages());
		es.submit(new ServerStartup(rpcConfig.getRpcServerConfig().getPort()));
	}

	/**
	 * 启动，阻塞直到启动完成
	 * <p>
	 *
	 * @return
	 */
	public static void startupSync() {
		logger.info("starting rpc ...");
		RpcConfig conf = Loader.getRpcConfig();
		Servers.init(conf.getRpcServerConfig().getPackages());

		ServerStartup ss = new ServerStartup(conf.getRpcServerConfig().getPort());
		ss.lock.lock();
		try {
			es.submit(ss);
			ss.done.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RpcException(e);
		} finally {
			ss.lock.unlock();
		}
	}

	/**
	 * 获取接口的代理类
	 * <p>
	 *
	 * @param interfaceClass
	 * @return 
	 */
	public static <T> T get(Class<T> interfaceClass) {
		return Clients.getRpcProxy(interfaceClass);
	}
}
