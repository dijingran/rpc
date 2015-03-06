package org.dxx.rpc.registry.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.codec.DexnCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryServerStartup {
	static final Logger logger = LoggerFactory.getLogger(RegistryServerStartup.class);
	private int port;

	public RegistryServerStartup() {
		this.port = RpcConstants.REGISTRY_DEFUALT_PORT;
	}

	public void run() throws Exception {
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() { // (4)
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new IdleStateHandler(0, RpcConstants.HEAT_BEAT, 0, TimeUnit.MILLISECONDS));
				ch.pipeline().addLast(new DexnCodec());
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
				ch.pipeline().addLast(new RegistryServerHandler());
			}
		};
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(channelInitializer);
			ChannelFuture f = b.bind(port).sync();
			logger.info("Registry is running on port {}!", port);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
