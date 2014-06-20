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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.dxx.rpc.config.RpcClientConfig;
import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.registry.LocateRpcServerResponse;
import org.dxx.rpc.registry.LocateRpcServerResponse.Service;
import org.dxx.rpc.registry.RegistryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientStartup implements Runnable {
	static Logger logger = LoggerFactory.getLogger(ClientStartup.class);

	private String host;
	private int port;

	public ClientStartup(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	private static Map<String, Set<Class<?>>> urlAndInterfaces;

	private static AtomicInteger count;

	public static boolean isInitialized() {
		return count != null && count.get() == 0;
	}

	public static void startup() {
		//		if (urlAndInterfaces != null) {
		//			return;
		//		}
		urlAndInterfaces = new HashMap<String, Set<Class<?>>>();
		final List<ClientStartup> startups = new ArrayList<ClientStartup>();
		RpcClientConfigs configs = Loader.getRpcClientConfigs();

		List<String> intersWithoutUrl = new ArrayList<String>();
		// 指定了host 和 port，不用经过注册中心
		for (RpcClientConfig c : configs.getClients()) {
			if (ChannelContext.exists(c.getInterfaceClass())) {
				continue;
			}
			if (c.getUrl() == null || c.getUrl().isEmpty()) {
				intersWithoutUrl.add(c.getInterfaceClass());
				continue;
			}

			String url = c.getUrl();

			if (!urlAndInterfaces.containsKey(url)) {
				Set<Class<?>> interfaces = new HashSet<Class<?>>();
				interfaces.add(c.getInter());
				urlAndInterfaces.put(url, interfaces);
				startups.add(new ClientStartup(c.getHost(), c.getPort()));
			} else {
				urlAndInterfaces.get(url).add(c.getInter());
			}
		}

		if (intersWithoutUrl.size() > 0) {
			if (Loader.getRpcConfig().getRegistry() == null) {
				throw new RpcException("没有配置注册中心，并且以下接口没有指定服务提供者的URL ： " + intersWithoutUrl.toString());
			}
			// 调用注册中心，并返回所有服务端地址
			LocateRpcServerResponse response = RegistryUtils.locateServer(intersWithoutUrl);

			if (!response.isSuccess()) {
				throw new RpcException("注册中心返回失败:" + response.getErrorMessage());
			}

			for (Service s : response.getServices()) {
				String url = s.getHost() + ":" + s.getPort();
				if (!urlAndInterfaces.containsKey(url)) {
					Set<Class<?>> interfaces = new HashSet<Class<?>>();
					interfaces.add(RegistryUtils.getInter(s.getInterfaceClass()));
					urlAndInterfaces.put(url, interfaces);
					startups.add(new ClientStartup(s.getHost(), s.getPort()));
				} else {
					urlAndInterfaces.get(url).add(RegistryUtils.getInter(s.getInterfaceClass()));
				}
			}
		}
		count = new AtomicInteger(startups.size());
		for (ClientStartup s : startups) {
			new Thread(s).start();
		}
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

			// store the relation betweent interface class and channel
			Set<Class<?>> interfaces = urlAndInterfaces.get(host + ":" + port);
			for (Class<?> i : interfaces) {
				ChannelContext.add(i, c);
			}

			logger.debug("channel created : {}:{}", host, port);
			count.decrementAndGet();
			c.closeFuture().sync();
			// Wait until the connection is closed.
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		finally {
			workerGroup.shutdownGracefully();
		}

	}

}
