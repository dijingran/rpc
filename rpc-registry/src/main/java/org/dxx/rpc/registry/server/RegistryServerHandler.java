package org.dxx.rpc.registry.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.HeartbeatRequest;
import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.http.HttpRequestUtils;
import org.dxx.rpc.registry.Channels;
import org.dxx.rpc.registry.GetServerLocationRequest;
import org.dxx.rpc.registry.GetServerLocationResponse;
import org.dxx.rpc.registry.RegisterRequest;
import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.AbstractCommand;
import org.dxx.rpc.registry.cmd.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryServerHandler extends ChannelInboundHandlerAdapter {

	/** 记录Channel和URL的对应关系，unregister时用来卸载该URL下的所有服务 */
	private static ConcurrentHashMap<Channel, String> channelAndUrl = new ConcurrentHashMap<Channel, String>();

	/** To avoid remove avaliable services. */
	private static ConcurrentHashMap<String, Channel> urlAndLatestChannel = new ConcurrentHashMap<String, Channel>();

	Logger logger = LoggerFactory.getLogger(RegistryServerHandler.class);

	private static ServiceRepository repository = ServiceRepository.getInstance();

	CommandFactory commandFactory = new CommandFactory();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequestUtils.handle(ctx, msg);

		} else if (msg instanceof GetServerLocationRequest) {

			GetServerLocationResponse resp = repository.getServer((GetServerLocationRequest) msg);
			ctx.channel().writeAndFlush(resp);
			logger.debug("Wrote GetServerLocationResponse : {}", resp);

		} else if (msg instanceof RegisterRequest) {
			Channels.add(ctx.channel());
			ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).set(true);

			RegisterRequest request = (RegisterRequest) msg;
			// remote address
			InetSocketAddress sa = (InetSocketAddress) ctx.channel().remoteAddress();
			String url = sa.getAddress().getHostAddress() + ":" + request.getPort();
			if (!channelAndUrl.containsKey(ctx.channel())) {
				channelAndUrl.put(ctx.channel(), url);
			}
			urlAndLatestChannel.put(url, ctx.channel());
			ctx.channel().writeAndFlush(repository.register(url, request));

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
					ctx.writeAndFlush(new HeartbeatRequest());
				}
			}
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("Channel unregistered : {}", ctx.channel());
		String url = channelAndUrl.remove(ctx.channel());

		if (url != null) {
			Channel c = urlAndLatestChannel.get(url);
			if (c == ctx.channel()) {
				repository.unregister(url);
			} else {
				logger.debug("Channel is overdue when unregistered, do not unregister services.");
			}
		}
		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.info(ctx.channel() + ":" + cause.getMessage(), cause);
		ctx.close();
	}
}