package org.dxx.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.dxx.rpc.Request;
import org.dxx.rpc.exec.RpcChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 */
public class ObjectServerHandler extends ChannelInboundHandlerAdapter { // (1)
	RpcChannelHandler channelHandler = new RpcChannelHandler();

	Logger logger = LoggerFactory.getLogger(ObjectServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		channelHandler.handle(ctx.channel(), (Request) msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		logger.debug("channelRegistered : {}", ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		ctx.channel().close();
		logger.debug("channelUnregistered : {}", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.debug(cause.getMessage());
		ctx.close();
	}
}