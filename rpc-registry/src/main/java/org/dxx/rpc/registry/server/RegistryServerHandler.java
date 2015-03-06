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
import org.dxx.rpc.monitor.HttpUtils;
import org.dxx.rpc.monitor.MonitorResponse;
import org.dxx.rpc.registry.Channels;
import org.dxx.rpc.registry.GetServerRequest;
import org.dxx.rpc.registry.GetServerResponse;
import org.dxx.rpc.registry.RegisterRequest;
import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.AbstractCommand;
import org.dxx.rpc.registry.cmd.CommandFactory;
import org.dxx.rpc.registry.monitor.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryServerHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(RegistryServerHandler.class);

	/** 记录Channel和URL的对应关系，unregister时用来卸载该URL下的所有服务 */
	private static ConcurrentHashMap<Channel, String> channelAndUrl = new ConcurrentHashMap<Channel, String>();

	/** To avoid remove avaliable services. */
	private static ConcurrentHashMap<String, Channel> urlAndLatestChannel = new ConcurrentHashMap<String, Channel>();

	private static ServiceRepository repository = ServiceRepository.getInstance();

	private CommandFactory commandFactory = new CommandFactory();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpUtils.handleRequest(ctx, msg);
		} else if (msg instanceof MonitorResponse) {
			Invoker.receive((MonitorResponse) msg);
		} else if (msg instanceof GetServerRequest) {
			GetServerResponse resp = repository.getServer((GetServerRequest) msg);
			ctx.channel().writeAndFlush(resp);
			logger.debug("Wrote : {}", resp);
		} else if (msg instanceof RegisterRequest) {
			ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).set(true);
			RegisterRequest request = (RegisterRequest) msg;
			Channels.add(ctx.channel(), request.getPort());
			String url = remoteUrl(ctx, request);

			channelAndUrl.putIfAbsent(ctx.channel(), url);
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

	private String remoteUrl(ChannelHandlerContext ctx, RegisterRequest request) {
		InetSocketAddress sa = (InetSocketAddress) ctx.channel().remoteAddress();
		return sa.getAddress().getHostAddress() + ":" + request.getPort();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.WRITER_IDLE) {
				Boolean needHeartbeat = ctx.attr(RpcConstants.ATTR_NEED_HEARTBEAT).get();
				if (needHeartbeat != null && needHeartbeat) {
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
