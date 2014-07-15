package org.dxx.rpc.registry.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.HeartbeatRequest;
import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.registry.ClientChannelContext;
import org.dxx.rpc.registry.GetServerLocationRequest;
import org.dxx.rpc.registry.GetServerLocationResponse;
import org.dxx.rpc.registry.RegisterRequest;
import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.AbstractCommand;
import org.dxx.rpc.registry.cmd.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 */
public class RegistryServerHandler extends ChannelInboundHandlerAdapter {

	/** 记录Channel和URL的对应关系，unregister时用来卸载该URL下的所有服务 */
	private Map<Channel, String> channelAndUrl = new ConcurrentHashMap<Channel, String>();

	Logger logger = LoggerFactory.getLogger(RegistryServerHandler.class);

	private static ServiceRepository repository = ServiceRepository.getInstance();

	CommandFactory commandFactory = new CommandFactory();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.debug("Received : {}", msg);
		if (msg instanceof GetServerLocationRequest) {
			GetServerLocationResponse resp = repository.getServer((GetServerLocationRequest) msg);
			ctx.channel().writeAndFlush(resp);
			logger.debug("Wrote GetServerLocationResponse : {}", resp);
		} else if (msg instanceof RegisterRequest) {
			ClientChannelContext.add(ctx.channel());
			ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).set(true);

			RegisterRequest request = (RegisterRequest) msg;
			// remote address
			InetSocketAddress sa = (InetSocketAddress) ctx.channel().remoteAddress();
			if (!channelAndUrl.containsKey(ctx.channel())) {
				channelAndUrl.put(ctx.channel(), sa.getAddress().getHostAddress() + ":" + request.getPort());
			}
			ctx.channel().writeAndFlush(
					repository.register(sa.getAddress().getHostAddress(), request.getPort(), request));

		} else { // String from telnet
			AbstractCommand c = commandFactory.get(msg.toString());
			if (c != null) {
				c.setChannel(ctx.channel());
				c.exec();
			}
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.WRITER_IDLE) {
				Boolean needHeartbeat = ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).get();
				if (needHeartbeat != null && needHeartbeat == true) {
					logger.trace("Send heatbeat Request : {}", ctx.channel());
					ctx.writeAndFlush(new HeartbeatRequest());
				}
			}
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ClientChannelContext.remove(ctx.channel());
		repository.unregister(channelAndUrl.get(ctx.channel()));
		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.info(cause.getMessage());
		ctx.close();
	}
}