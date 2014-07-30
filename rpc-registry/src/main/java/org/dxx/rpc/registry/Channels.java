/**
 * ChannelContext.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Use {@link ChannelGroup} to hold all client channels.
 *  
 * @author   dixingxing
 * @Date	 2014年7月15日
 */

public class Channels {
	private static ChannelGroup cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/**
	 * Write {@link UpdateServersRequest} to all clients.
	 * @see ServiceRepository#pushServices()
	 */
	public static void writeAndFlush(UpdateServersRequest request) {
		cg.writeAndFlush(request);
	}

	public static void add(Channel c) {
		cg.add(c);
	}
}
