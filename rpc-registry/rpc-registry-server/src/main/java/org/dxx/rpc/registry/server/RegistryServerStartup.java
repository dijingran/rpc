package org.dxx.rpc.registry.server;

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

import org.dxx.rpc.registry.RegistryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryServerStartup {
	static final Logger logger = LoggerFactory.getLogger(RegistryServerStartup.class);
	private int port;

	public RegistryServerStartup() {
		this.port = RegistryConstants.DEFUALT_PORT;
	}

	public RegistryServerStartup(int port) {
		super();
		this.port = port;
	}

	public void run() throws Exception {
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() { // (4)
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				final ObjectDecoder decoder = new ObjectDecoder(Integer.MAX_VALUE,
						ClassResolvers.softCachingConcurrentResolver(Thread.currentThread().getContextClassLoader()));
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

			ChannelFuture f = b.bind(port).sync();
			logger.info("Registy is running on port {}!", port);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
