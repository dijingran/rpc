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

import org.dxx.rpc.codec.DexnDecoder;
import org.dxx.rpc.codec.DexnEncoder;
import org.dxx.rpc.config.RpcClientConfig;
import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.registry.GetServerLocationResponse;
import org.dxx.rpc.registry.RegistryUtils;
import org.dxx.rpc.registry.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 为单个接口定位服务端的ip及端口，并创建 channel。
 * <p>
 * 同时将此服务端提供的接口名记录下来，缓存到 {@link ChannelContext#channels} 中。
 * </p>
 * 
 * @author   dixingxing
 * @Date	 2014-7-12
 */
public class ClientStartup {
	static Logger logger = LoggerFactory.getLogger(ClientStartup.class);

	private String host;
	private int port;

	private String interfaceClass;

	// TODO use list
	private String deactiveUrl;

	public ClientStartup(String interfaceClass, String deactiveUrl) {
		super();
		this.interfaceClass = interfaceClass;
		this.deactiveUrl = deactiveUrl;
	}

	public void startup() {
		RpcClientConfigs configs = Loader.getRpcClientConfigs();
		for (RpcClientConfig c : configs.getClients()) {
			if (interfaceClass.equals(c.getInterfaceClass())) {
				if (c.getUrl() != null && c.getUrl().length() > 0) {
					this.port = c.getPort();
					this.host = c.getHost();
				}
			}
		}

		GetServerLocationResponse serverLocation = null;
		// no url
		if (this.host == null) {
			try {
				serverLocation = RegistryUtils.getServerLocation(interfaceClass, deactiveUrl);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return;
			}
			this.host = serverLocation.getHost();
			this.port = serverLocation.getPort();
		}

		logger.debug("Try create channel : {}:{}, for : {}", new Object[] { host, port, interfaceClass });
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new DexnEncoder(), new DexnDecoder(), new ObjectClientHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync();
			Channel c = f.channel();

			// store the relation between interface class and channel
			if (serverLocation != null) {
				for (Service s : serverLocation.getServices()) {
					ChannelContext.add(s.getInterfaceClass(), c);
				}
			} else {
				ChannelContext.add(interfaceClass, c);
			}
			logger.debug("Channel created : {}", c);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
