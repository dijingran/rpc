package org.dxx.rpc.registry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.dxx.rpc.EventLoops;
import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.codec.DexnCodec;
import org.dxx.rpc.config.Registry;
import org.dxx.rpc.config.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于创建和注册中心的连接
 * 
 * @author   dixingxing
 * @Date	 2014-6-25
 */
public class RegistryStartup implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(RegistryStartup.class);

	private String host;
	private int port;

	public RegistryStartup() {
		super();
		Registry registry = Loader.getRpcConfig().getRegistry();
		if (registry == null) {
			logger.info("<registry../> is not configured !");
		} else {
			if (registry.getHost() == null || registry.getHost().isEmpty()) {
				logger.error("Registry host must not be empty.");
			} else {
				this.host = registry.getHost();
			}

			this.port = (registry.getPort() <= 0 ? RpcConstants.REGISTRY_DEFUALT_PORT : registry.getPort());
		}
	}

	@Override
	public void run() {
		startup();
	}

	public void startup() {
		long start = System.currentTimeMillis();
		if (this.host == null) {
			return;
		}

		if (RegistryUtils.isChannelActive()) {
			return;
		}

		if (RegistryUtils.isInitialized()) {
			RegistryUtils.removeChannel();
			logger.info("Registry channel is not avaliable, try reconnect.");
		}

		try {
			Bootstrap b = new Bootstrap();
			b.group(EventLoops.workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new DexnCodec(), new RegistryHandler());
				}
			});
			ChannelFuture f = b.connect(host, port).sync();
			RegistryUtils.setChannel(f.channel());
			logger.trace("Create registry channel cost {} ms.", (System.currentTimeMillis() - start));
		} catch (Throwable e) {
			logger.warn("Ex while create registy channel : " + e.getMessage(), e);
		}
	}

}
