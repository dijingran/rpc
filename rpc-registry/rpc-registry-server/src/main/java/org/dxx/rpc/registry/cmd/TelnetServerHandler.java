package org.dxx.rpc.registry.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a server-side channel.
 */
public class TelnetServerHandler extends ChannelInboundHandlerAdapter { // (1)

	CommandFactory commandFactory = new CommandFactory();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		AbstractCommand c = commandFactory.get(msg.toString());
		if (c != null) {
			c.setChannel(ctx.channel());
			c.exec();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		cause.printStackTrace();
		ctx.close();
	}
}