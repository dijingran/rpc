package org.dxx.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.dxx.rpc.HeartbeatRequest;
import org.dxx.rpc.Request;
import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.monitor.HttpUtils;
import org.dxx.rpc.server.exec.RpcChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter { // (1)
	RpcChannelHandler channelHandler = new RpcChannelHandler();

	Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		if (msg instanceof HttpRequest) {
			HttpUtils.handleRequest(ctx, msg);

		} else if (msg instanceof String) {
			ctx.channel().writeAndFlush("Not implement yet!\r\n");
		} else {
			ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).set(true);
			channelHandler.handle(ctx.channel(), (Request) msg);
		}
	}

	/**
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.WRITER_IDLE) {
				Boolean needHeartbeat = ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).get();
				if (needHeartbeat != null && needHeartbeat == true) {
					ctx.writeAndFlush(new HeartbeatRequest());
				}
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ClientChannels.add(ctx.channel());
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
		logger.info(ctx.channel() + ":" + cause.getMessage(), cause);
		ctx.close();
	}
}