/**
 * HashMapTets.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import io.netty.channel.socket.nio.NioSocketChannel;

import org.dxx.rpc.client.ChannelContext;
import org.junit.Test;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-12
 */
public class ChannelContextTest {

	@Test
	public void testHeartbeat() {
		final NioSocketChannel c1 = new NioSocketChannel();
		final NioSocketChannel c2 = new NioSocketChannel();
		final NioSocketChannel c3 = new NioSocketChannel();
		final NioSocketChannel c4 = new NioSocketChannel();
		final NioSocketChannel c5 = new NioSocketChannel();
		final NioSocketChannel c6 = new NioSocketChannel();
		final NioSocketChannel c7 = new NioSocketChannel();
		final NioSocketChannel c8 = new NioSocketChannel();
		final NioSocketChannel c9 = new NioSocketChannel();
		final NioSocketChannel c10 = new NioSocketChannel();
		ChannelContext.add("c1", c1);
		ChannelContext.add("c2", c2);
		ChannelContext.add("c3", c3);
		ChannelContext.add("c4", c4);
		ChannelContext.add("c5", c5);
		ChannelContext.add("c6", c6);
		ChannelContext.add("c7", c7);
		ChannelContext.add("c8", c8);
		ChannelContext.add("c9", c9);
		ChannelContext.add("c10", c10);

		Tracer.doTrace("heartbeat", 10000 * 10, new Tracer() {
			@Override
			public void exec() {
				ChannelContext.updateActiveTime(c2);
			}
		});
	}

}
