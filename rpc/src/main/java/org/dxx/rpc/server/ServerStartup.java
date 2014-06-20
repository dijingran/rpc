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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.registry.RegistryStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStartup implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);
	private int port;

	public Lock lock = new ReentrantLock();
	public Condition done = lock.newCondition();

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
			logger.info("rpc server is running on port : {}", port);
			// call registry and init channels for rpc clients

			if (Loader.getRpcConfig().getRegistry() != null) {
				RegistryStartup.startupSync();
			}
			lock.lock();
			try {
				done.signal();
			} finally {
				lock.unlock();
			}

			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RpcException(e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
