package org.dxx.rpc.registry.cmd;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

import org.dxx.rpc.common.Awakeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start telnet service
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class TelnetServerStartup extends Awakeable {
	static final Logger logger = LoggerFactory.getLogger(TelnetServerStartup.class);
	private int port;

	public TelnetServerStartup(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
							ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
							ch.pipeline().addLast(new TelnetServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
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