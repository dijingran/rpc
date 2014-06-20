/**
 * ClientStartupGroup.java
 * org.dxx.rpc.client
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dxx.rpc.config.RpcClientConfig;
import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.registry.LocateRpcServerResponse;
import org.dxx.rpc.registry.LocateRpcServerResponse.Service;
import org.dxx.rpc.registry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一组 {@link ClientStartup}
 * @author   dixingxing
 * @Date	 2014-6-20
 */

public class ClientStartupGroup {
	static Logger logger = LoggerFactory.getLogger(ClientStartupGroup.class);
	private static ExecutorService es = Executors.newCachedThreadPool();

	private Map<String, Set<Class<?>>> urlAndInterfaces = new ConcurrentHashMap<String, Set<Class<?>>>();
	final List<ClientStartup> startups = new ArrayList<ClientStartup>();

	public void createChannels() {
		final List<ClientStartup> startups = calculateChannels();
		for (ClientStartup s : startups) {
			s.setInterfaces(urlAndInterfaces.get(s.getUrl()));
			es.submit(s);
		}
	}

	/**
	 * 返回所有需要建立的channels
	 * <p>
	 *
	 * @return
	*/

	private List<ClientStartup> calculateChannels() {
		RpcClientConfigs configs = Loader.getRpcClientConfigs();
		final List<ClientStartup> startups = new ArrayList<ClientStartup>();

		List<String> intersWithoutUrl = new ArrayList<String>();
		// 指定了host 和 port，不用经过注册中心
		for (RpcClientConfig c : configs.getClients()) {
			if (ChannelContext.exists(c.getInterfaceClass())) {
				continue;
			}
			if (c.getUrl() == null || c.getUrl().isEmpty()) {
				intersWithoutUrl.add(c.getInterfaceClass());
				continue;
			}

			String url = c.getUrl();
			if (!urlAndInterfaces.containsKey(url)) {
				Set<Class<?>> interfaces = new HashSet<Class<?>>();
				interfaces.add(c.getInter());
				urlAndInterfaces.put(url, interfaces);
				startups.add(new ClientStartup(c.getHost(), c.getPort()));
			} else {
				urlAndInterfaces.get(url).add(c.getInter());
			}
		}

		try {
			if (intersWithoutUrl.size() > 0) {
				if (Loader.getRpcConfig().getRegistry() == null) {
					logger.warn("没有配置注册中心，并且以下接口没有指定服务提供者的URL ：{}", intersWithoutUrl);
				}
				appendServers(startups, RegistryUtils.locateServer(intersWithoutUrl));
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return startups;
	}

	/**
	 * 把 {@link LocateRpcServerResponse} 中的服务端url追加到启动列表中
	 * <p>
	 *
	 * @param startups
	 * @param response 
	*/

	private void appendServers(final List<ClientStartup> startups, LocateRpcServerResponse response) {
		if (response == null) {
			logger.warn("尚未连接到注册中心");
			return;
		}
		if (response == null || !response.isSuccess()) {
			logger.warn("从注册中心获取服务端地址失败:" + response.getErrorMessage());
			return;
		}
		for (Service s : response.getServices()) {
			String url = s.getHost() + ":" + s.getPort();
			if (!urlAndInterfaces.containsKey(url)) {
				Set<Class<?>> interfaces = new HashSet<Class<?>>();
				interfaces.add(RegistryUtils.getInter(s.getInterfaceClass()));
				urlAndInterfaces.put(url, interfaces);
				startups.add(new ClientStartup(s.getHost(), s.getPort()));
			} else {
				urlAndInterfaces.get(url).add(RegistryUtils.getInter(s.getInterfaceClass()));
			}
		}
	}
}
