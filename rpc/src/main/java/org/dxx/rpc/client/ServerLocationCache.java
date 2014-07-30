/**
 * ServerLocationCache.java
 * org.dxx.rpc.client
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.registry.GetServerResponse;
import org.dxx.rpc.registry.UpdateServersRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 缓存注册中心中所有服务及服务提供者，仅当注册中心不可用时，才从此类中获取服务提供者的地址。
 * <p>
 * 以下情况注册中心会将最新的服务列表推送给客户端：
 * </p>
 * <ol>
 * <li>与注册中心建立连接时
 * <li>当注册中心注册新服务或卸载老服务时
 * <li>注册中心暂停和恢复服务时
 * </ol> 
 * @author   dixingxing
 * @Date	 2014年7月15日
 */
public class ServerLocationCache {
	private static final Logger logger = LoggerFactory.getLogger(ServerLocationCache.class);

	/** interfaceClass, "host:port..." */
	private static Map<String, Set<String>> interAndUrl = new ConcurrentHashMap<String, Set<String>>();

	/**
	 * 从本地缓存中获取服务提供者地址
	 * 
	 * @param deactiveUrl 心跳失败的服务端地址
	 */
	public static GetServerResponse getServerLocation(String interfaceClass, String deactiveUrl) {
		Set<String> urls = interAndUrl.get(interfaceClass);
		if (urls == null) {
			logger.debug("Can't find service from local cache for interface : {}", interfaceClass);
			return null;
		}

		List<String> firstOptions = new ArrayList<String>();
		for (String url : urls) {
			if (!url.equals(deactiveUrl)) {
				firstOptions.add(url);
			}
		}
		GetServerResponse response = new GetServerResponse();
		String url = firstOptions.size() > 0 ? firstOptions.get(new Random().nextInt(firstOptions.size()))
				: deactiveUrl;
		if (url == null) {
			return null;
		}
		response.setHost(url.split(":")[0]);
		response.setPort(Integer.valueOf(url.split(":")[1]));
		return response;
	}

	public static void update(UpdateServersRequest request) {
		logger.debug("Update ServerLocationCache : {}", request.getInterAndUrl());
		ServerLocationCache.interAndUrl = request.getInterAndUrl();
	}

}
