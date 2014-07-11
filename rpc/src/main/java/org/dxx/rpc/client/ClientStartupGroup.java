/**
 * ClientStartupGroup.java
 * org.dxx.rpc.client
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private Map<String, Set<Class<?>>> urlAndInterfaces = new HashMap<String, Set<Class<?>>>();
	List<ClientStartup> startups = new ArrayList<ClientStartup>();

	/**
	 * <p>
	 * Create channels, blocking until all channels finished(include not connected).
	 * </p>
	 * <li>Synchronized, avoid same channel be created multi times.
	 */
	public synchronized void createChannelsSync() {
		logger.debug("Begin to create channel.");
		urlAndInterfaces.clear();
		startups.clear();

		long start = System.currentTimeMillis();
		startups = calculateChannels();
		if (startups.isEmpty()) {
			logger.trace("No need to create channels.");
			return;
		}

		for (ClientStartup s : startups) {
			s.setInterfaces(urlAndInterfaces.get(s.getUrl()));
			s.startup();
		}

		logger.debug("Create channels complete! cost : {} ms.", System.currentTimeMillis() - start);
	}

	/**
	 * 返回所有需要建立的channels
	 * <p>
	 *
	 * @return
	*/
	private List<ClientStartup> calculateChannels() {
		RpcClientConfigs configs = Loader.getRpcClientConfigs();
		final List<ClientStartup> list = new ArrayList<ClientStartup>();

		List<String> intersWithoutUrl = new ArrayList<String>();
		// 指定了URL，不用经过注册中心
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
				list.add(new ClientStartup(c.getHost(), c.getPort()));
			} else {
				urlAndInterfaces.get(url).add(c.getInter());
			}
		}

		try {
			if (intersWithoutUrl.size() > 0) {
				if (Loader.getRpcConfig().getRegistry() == null) {
					logger.warn("没有配置注册中心，并且以下接口没有指定服务提供者的URL ：{}", intersWithoutUrl);
				}
				appendServers(list, RegistryUtils.locateServer(intersWithoutUrl));
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 把 {@link LocateRpcServerResponse} 中的服务端url追加到启动列表中
	 * <p>
	 *
	 * @param startupList
	 * @param response 
	*/

	private void appendServers(final List<ClientStartup> startupList, LocateRpcServerResponse response) {
		if (response == null) {
			logger.warn("尚未连接到注册中心");
			return;
		}
		if (!response.isSuccess()) {
			logger.warn("从注册中心获取服务端地址失败:" + response.getErrorMessage());
			return;
		}
		for (Service s : response.getServices()) {
			String url = s.getHost() + ":" + s.getPort();
			if (!urlAndInterfaces.containsKey(url)) {
				Set<Class<?>> interfaces = new HashSet<Class<?>>();
				interfaces.add(RegistryUtils.getInter(s.getInterfaceClass()));
				urlAndInterfaces.put(url, interfaces);
				startupList.add(new ClientStartup(s.getHost(), s.getPort()));
			} else {
				urlAndInterfaces.get(url).add(RegistryUtils.getInter(s.getInterfaceClass()));
			}
		}
	}
}
