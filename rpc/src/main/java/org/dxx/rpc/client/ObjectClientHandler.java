package org.dxx.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.dxx.rpc.Response;
import org.dxx.rpc.ResponseFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectClientHandler extends ChannelInboundHandlerAdapter {

	Logger logger = LoggerFactory.getLogger(ObjectClientHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ResponseFuture.receive((Response) msg);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		ChannelContext.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.debug(cause.getMessage());
		ctx.close();
	}
}