package org.dxx.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.dxx.rpc.EventLoops;
import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.codec.DexnCodec;
import org.dxx.rpc.registry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStartup {
	static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);
	private int port;

	public ServerStartup(int port) {
		super();
		this.port = port;
	}

	public void startup() {
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new DexnCodec());
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));

				ch.pipeline().addLast(new IdleStateHandler(0, RpcConstants.HEAT_BEAT, 0, TimeUnit.MILLISECONDS));
				ch.pipeline().addLast(new ServerHandler());
			}
		};

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, EventLoops.workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(channelInitializer).option(ChannelOption.SO_BACKLOG, 128);
			b.childOption(ChannelOption.SO_KEEPALIVE, true);
			b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

			b.bind(port).sync();
			logger.info("Rpc server is running on port : {}", port);
			// call registry and init channels for rpc clients
			RegistryUtils.createChannel();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			RegistryUtils.scheduleRegistry();
		}
	}

}
