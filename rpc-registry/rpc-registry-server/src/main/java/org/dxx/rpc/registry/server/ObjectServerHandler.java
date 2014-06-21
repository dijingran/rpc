package org.dxx.rpc.registry.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.registry.LocateRpcServerRequest;
import org.dxx.rpc.registry.RegisterRequest;
import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a server-side channel.
 */
public class ObjectServerHandler extends ChannelInboundHandlerAdapter {

	/** 记录Channel和URL的对应关系，unregister时用来卸载该URL下的所有服务 */
	private Map<Channel, String> channelAndUrl = new ConcurrentHashMap<Channel, String>();

	Logger logger = LoggerFactory.getLogger(ObjectServerHandler.class);

	private static ServiceRepository repository = ServiceRepository.getInstance();

	CommandFactory commandFactory = new CommandFactory();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.debug("Received : {}", msg);
		if (msg instanceof LocateRpcServerRequest) {
			ctx.channel().writeAndFlush(repository.locateRpcServer((LocateRpcServerRequest) msg));
		} else if (msg instanceof RegisterRequest) {
			RegisterRequest request = (RegisterRequest) msg;
			// remote address
			InetSocketAddress sa = (InetSocketAddress) ctx.channel().remoteAddress();
			if (!channelAndUrl.containsKey(ctx.channel())) {
				channelAndUrl.put(ctx.channel(), sa.getAddress().getHostAddress() + ":" + request.getPort());
			}
			ctx.channel().writeAndFlush(
					repository.register(sa.getAddress().getHostAddress(), request.getPort(), request));
		} else {
			//
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		repository.unregister(channelAndUrl.get(ctx.channel()));
		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.warn(cause.getMessage());
		ctx.close();
	}
}