/**
 * RegisryHandler.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.config.Registry;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.server.Servers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 向注册中心发布服务，注册中心断开后，尝试重连
 * @author   dixingxing
 * @Date	 2014-6-19
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(RegistryHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Registry registry = Loader.getRpcConfig().getRegistry();
		logger.debug("Registering rpc services to Registry : {}:{}", registry.getHost(), registry.getPort());

		RegisterRequest request = new RegisterRequest();
		request.setPort(Loader.getRpcConfig().getRpcServerConfig().getPort());

		List<Service> services = new ArrayList<Service>();
		for (Class<?> interfaceClass : Servers.interAndImpl.keySet()) {
			Service s = new Service();
			s.setInterfaceClass(interfaceClass.getName());
			s.setDesc(Servers.getDef(interfaceClass).getDescription());
			services.add(s);
		}
		request.setServices(services);
		ctx.writeAndFlush(request);
		logger.debug("RegisterRequest sent : {}", request);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof RegisterResponse) {
			RegisterResponse response = (RegisterResponse) msg;
			if (response.isSuccess()) {
				logger.debug("Register success : {}", response);
			} else {
				logger.error("Register failed : {}", response.getErrorMessage());
			}
		} else if (msg instanceof GetServerLocationResponse) {
			RegistryUtils.receiveLocateServerResponse((GetServerLocationResponse) msg);
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("Registry channel unregistered!");
		if (RegistryUtils.isRegistryInitialized()) {
			RegistryUtils.removeRegistryChannel();
			RegistryUtils.scheduleRegistry();
		}
	}
}
