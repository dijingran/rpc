package org.dxx.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientStartup implements Runnable {
	static Logger logger = LoggerFactory.getLogger(ClientStartup.class);

	private String host;
	private int port;

	private Set<Class<?>> interfaces;

	public ClientStartup(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		logger.debug("try create channel : {}:{}", host, port);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		final ObjectDecoder decoder = new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(Thread
				.currentThread().getContextClassLoader()));
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectEncoder(), decoder, new ObjectClientHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync();

			Channel c = f.channel();

			// store the relation between interface class and channel
			for (Class<?> i : this.interfaces) {
				ChannelContext.add(i, c);
			}
			logger.debug("channel created : {}:{}", host, port);
			c.closeFuture().sync();
			// Wait until the connection is closed.
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			workerGroup.shutdownGracefully();
		}

	}

	public void setInterfaces(Set<Class<?>> interfaces) {
		this.interfaces = interfaces;
	}

	public String getUrl() {
		return this.host + ":" + this.port;
	}

}
