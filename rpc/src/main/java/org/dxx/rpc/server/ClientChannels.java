/**
 * ClientChannels.java
 * org.dxx.rpc.server
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 
 * 和客户端保持的连接
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
public class ClientChannels {
	private static final ChannelGroup cg = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public static void add(Channel c) {
		cg.add(c);
	}

	public static ChannelGroup getCg() {
		return cg;
	}

}
