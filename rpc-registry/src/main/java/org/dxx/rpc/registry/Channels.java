/**
 * ChannelContext.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.util.Iterator;

/**
 * Use {@link ChannelGroup} to hold all client channels.
 *  
 * @author   dixingxing
 * @Date	 2014年7月15日
 */

public class Channels {
	private static ChannelGroup cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private static final AttributeKey<Integer> ATTR_SERVER_PORT = AttributeKey.valueOf("attr_server_port");

	/**
	 * Write {@link UpdateServersRequest} to all clients.
	 * @see ServiceRepository#pushServices()
	 */
	public static void writeAndFlush(UpdateServersRequest request) {
		cg.writeAndFlush(request);
	}

	public static void add(Channel c, int port) {
		if (cg.add(c)) {
			c.attr(ATTR_SERVER_PORT).set(port);
		}
	}

	public static Channel getByUrl(String url) {
		String host = url.split(":")[0];
		String port = url.split(":")[1];
		for (Iterator<Channel> it = cg.iterator(); it.hasNext();) {
			Channel c = it.next();
			InetSocketAddress sa = (InetSocketAddress) c.remoteAddress();
			if (!sa.getAddress().getHostAddress().equals(host)) {
				continue;
			}
			if (!String.valueOf(c.attr(ATTR_SERVER_PORT)).equals(port)) {
				continue;
			}
			return c;
		}
		return null;
	}
}
