package org.dxx.rpc.client;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelContext {
	private static final Logger logger = LoggerFactory.getLogger(ChannelContext.class);

	static Map<String, Channel> interAndChannels = new ConcurrentHashMap<String, Channel>();

	/**
	 * 根据接口名获得channel。 TODO
	 */
	public static Channel getChannel(Class<?> interfaceClass) {
		Channel c = interAndChannels.get(interfaceClass.getName());
		if (c == null) {
			// TODO 多次为空时，将接口放到失败列表中，避免每次都尝试创建连接
			new ClientStartup(interfaceClass.getName()).startup();
			c = interAndChannels.get(interfaceClass.getName());
		}
		if (c == null) {
			throw new RpcException("No channel found for interface : " + interfaceClass.getName());
		}
		return c;
	}

	public static void add(String interfaceName, Channel channel) {
		interAndChannels.put(interfaceName, channel);
	}

	public static void remove(Channel c) {
		for (String inter : interAndChannels.keySet()) {
			if (c == interAndChannels.get(inter)) {
				interAndChannels.remove(inter);
				logger.debug("Remove channel for interface  : {}, {}", inter, c);
			}
		}
	}

	public static boolean exists(String interName) {
		return interAndChannels.containsKey(interName);
	}

	// =============== heartbeat S ===============
	// TODO use this
	private boolean isInvalid(Channel c) {
		return invalidChannels.contains(c);
	}

	public static void heartbeat(Channel c) {
		logger.trace("Hearbeating : {}", c);
		invalidChannels.add(c);
	}

	static Set<Channel> invalidChannels = new CopyOnWriteArraySet<Channel>();
	// =============== heartbeat E===============
}
