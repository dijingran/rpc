package org.dxx.rpc.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelContext {
	private static final Logger logger = LoggerFactory.getLogger(ChannelContext.class);

	static Map<String, Channel> interAndChannels = new ConcurrentHashMap<String, Channel>();

	/**
	 * 检查是否已有channel，避免重复创建
	 */
	static boolean isChannelExist(String interfaceClass) {
		Channel c = interAndChannels.get(interfaceClass);
		return c != null && isActive(c);
	}

	/**
	 * 根据接口名获得channel。 TODO
	 */
	public static Channel getOrCreateChannel(String interfaceClass) {
		Channel c = interAndChannels.get(interfaceClass);
		if (c != null && !isActive(c)) {
			deactive(c);
			c = null;
		}

		if (c == null) {
			// TODO 多次为空时，将接口放到失败列表中，避免每次都尝试创建连接
			new ClientStartup(interfaceClass, getDeactiveUrl(interfaceClass)).startup();
			c = interAndChannels.get(interfaceClass);
		}
		if (c == null) {
			throw new RpcException("No channel found for interface : " + interfaceClass);
		}
		return c;
	}

	public static void add(String interfaceName, Channel channel) {
		interAndChannels.put(interfaceName, channel);
		activeMap.put(channel, System.currentTimeMillis());
	}

	public static void remove(Channel c) {
		for (Iterator<Entry<String, Channel>> iter = interAndChannels.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, Channel> entry = iter.next();
			if (entry.getValue() == c) {
				logger.debug("Remove channel for interface  : {}, {}", entry.getKey(), c);
				iter.remove();
			}
		}
	}

	public static boolean exists(String interName) {
		return interAndChannels.containsKey(interName);
	}

	// =============== heartbeat S ===============
	/**
	 * Channel是否可用（长时间{@link RpcConstants#INVALID_THRESHOLD}没有接受到数据，视为不可用）
	 *
	 * @param c
	 * @return
	 */
	private static boolean isActive(Channel c) {
		return (System.currentTimeMillis() - activeMap.get(c)) <= RpcConstants.INVALID_THRESHOLD;
	}

	private static void deactive(Channel c) {
		for (Iterator<Entry<String, Channel>> iter = interAndChannels.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, Channel> entry = iter.next();
			if (entry.getValue() == c) {
				logger.debug("Deactive (Remove) channel for interface  : {}, {}", entry.getKey(), c);
				deactiveChannels.put(entry.getKey(), c);
				iter.remove();
			}
		}
		logger.debug("Channel is not active, just close it : {}", c);
		c.close();
	}

	public static void updateActiveTime(Channel c) {
		activeMap.put(c, System.currentTimeMillis());
	}

	private static String getDeactiveUrl(String interfaceClass) {
		Channel c = deactiveChannels.get(interfaceClass);
		if (c == null) {
			return null;
		}
		InetSocketAddress sa = (InetSocketAddress) c.remoteAddress();
		return sa.getAddress().getHostAddress() + ":" + sa.getPort();
	}

	/** channel, last access time(System.currentTimeMillis()) */
	static Map<Channel, Long> activeMap = new HashMap<Channel, Long>();

	/** interfaceName, channel  : Invalid channels, already closed, can't be reused.*/
	// TODO map list ; remove
	static Map<String, Channel> deactiveChannels = new ConcurrentHashMap<String, Channel>();
	// =============== heartbeat E===============

}
