package org.dxx.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import org.dxx.rpc.common.Awakeable;
import org.dxx.rpc.registry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStartup extends Awakeable {
	static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);
	private int port;

	public ServerStartup(int port) {
		super();
		this.port = port;
	}

	@Override
	public void run() {
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() { // (4)
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				final ObjectDecoder decoder = new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(Thread
						.currentThread().getContextClassLoader()));
				ch.pipeline().addLast("encoder", new ObjectEncoder());
				ch.pipeline().addLast("decoder", decoder);
				ch.pipeline().addLast(new ObjectServerHandler());
			}
		};

		EventLoopGroup bossGroup = new NioEventLoopGroup();

		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(channelInitializer)
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync();
			logger.info("Rpc server is running on port : {}", port);
			// call registry and init channels for rpc clients
			RegistryUtils.createRegistryChannelSync();
			awake();

			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			awake();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}