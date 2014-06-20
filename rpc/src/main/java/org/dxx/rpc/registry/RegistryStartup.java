package org.dxx.rpc.registry;

import io.netty.bootstrap.Bootstrap;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dxx.rpc.config.Registry;
import org.dxx.rpc.config.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryStartup implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(RegistryStartup.class);
	private static ExecutorService es = Executors.newFixedThreadPool(1);
	private String host;

	private int port;

	public Lock lock = new ReentrantLock();
	public Condition done = lock.newCondition();

	public RegistryStartup(String host, int port) {
		super();
		if (host == null || host.isEmpty()) {
			logger.error("host must not be empty");
			throw new RegistryException("host must not be empty");
		}
		this.host = host;
		this.port = port;
	}

	private static RegistryStartup instance;

	public static synchronized RegistryStartup getInstance() {
		if (instance == null) {
			Registry registry = Loader.getRpcConfig().getRegistry();
			if (registry == null) {
				logger.info("<registry../> is not configured !");
				return null;
			}
			instance = new RegistryStartup(registry.getHost(), registry.getPort());
		}
		return instance;
	}

	public static void startupSync() throws Exception {
		RegistryStartup startup = getInstance();
		if (startup != null) {
			startup.lock.lock();
			try {
				es.submit(startup);
				startup.done.await(8, TimeUnit.SECONDS);
			} finally {
				startup.lock.unlock();
			}
		}
	}

	@Override
	public void run() {
		if (RegistryUtils.isRegistryInitialized()) {
			logger.debug("already inited ...");
			return;
		}
		Registry registry = Loader.getRpcConfig().getRegistry();
		logger.debug("Registering rpc services to Registry : {}:{}", registry.getHost(), registry.getPort());

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
					ch.pipeline().addLast(new ObjectEncoder(), decoder, new RegistryHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync();
			RegistryUtils.setRegistyChannel(f.channel());
			lock.lock();
			try {
				done.signal();
			} finally {
				lock.unlock();
			}
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.warn("连接注册中心异常 : " + e.getMessage(), e);
			RegistryUtils.scheduleRegistry();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

}
