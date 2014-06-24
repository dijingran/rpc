package org.dxx.rpc.client;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelContext {
	private static final Logger logger = LoggerFactory.getLogger(ChannelContext.class);

	static Map<Class<?>, Channel> channels = new ConcurrentHashMap<Class<?>, Channel>();
	static Map<String, Channel> interNameAndchannels = new ConcurrentHashMap<String, Channel>();

	public static Channel getChannel(Class<?> interfaceClass) {
		Channel c = channels.get(interfaceClass);
		if (c == null) {
			new ClientStartupGroup().createChannelsSync();
			// 没有服务类时，尝试重连 TODO 无须一直重连，缓存？
			c = channels.get(interfaceClass);
		}
		if (c == null) {
			throw new RpcException("No channel found for interface : " + interfaceClass.getName());
		}
		return c;
	}

	public static void add(Class<?> interfaceClass, Channel channel) {
		channels.put(interfaceClass, channel);
		interNameAndchannels.put(interfaceClass.getName(), channel);
	}

	public static void remove(Channel c) {
		for (Class<?> inter : channels.keySet()) {
			if (c == channels.get(inter)) {
				logger.debug("Remove channel for interface  : {}", inter.getName());
				channels.remove(inter);
			}
		}
		for (String inter : interNameAndchannels.keySet()) {
			if (c == interNameAndchannels.get(inter)) {
				interNameAndchannels.remove(inter);
			}
		}
	}

	public static boolean exists(String interName) {
		return interNameAndchannels.containsKey(interName);
	}
}
