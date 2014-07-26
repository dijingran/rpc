package org.dxx.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.dxx.rpc.HeartbeatRequest;
import org.dxx.rpc.Response;
import org.dxx.rpc.ResponseFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ChannelContext.updateAccessTime(ctx.channel());
		if (msg instanceof HeartbeatRequest) {
		} else {
			logger.trace("Receive : {}", msg);
			ResponseFuture.receive((Response) msg);
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		ChannelContext.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.info(cause.getMessage(), cause);
		ctx.close();
	}
}