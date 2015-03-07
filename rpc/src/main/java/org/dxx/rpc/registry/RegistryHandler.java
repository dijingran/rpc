/**
 * RegisryHandler.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.dxx.rpc.HeartbeatRequest;
import org.dxx.rpc.client.ServerCache;
import org.dxx.rpc.monitor.MonitorHandler;
import org.dxx.rpc.monitor.MonitorRequest;
import org.dxx.rpc.monitor.MonitorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 向注册中心发布服务，注册中心断开后，尝试重连
 * @author   dixingxing
 * @Date	 2014-6-19
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(RegistryHandler.class);
	private MonitorHandler monitorHandler = new MonitorHandler();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// this is important , just send message to registry, 
		// indicats this channle need heartbeat.
		RegistryUtils.register(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RegistryUtils.updateAccessTime(ctx.channel());
//		if (msg instanceof HeartbeatRequest) {
//			// do nothing
//		} else
        if (msg instanceof MonitorRequest) {
			MonitorResponse response = monitorHandler.handle((MonitorRequest) msg);
			ctx.channel().writeAndFlush(response);
		} else if (msg instanceof RegisterResponse) {
			RegisterResponse response = (RegisterResponse) msg;
			if (response.isSuccess()) {
				logger.info("Register success : {}", response);
			} else {
				logger.error("Register failed : {}", response.getErrorMessage());
			}
		} else if (msg instanceof GetServerResponse) {
			RegistryUtils.receiveGetServerLocationResponse((GetServerResponse) msg);
		} else if (msg instanceof UpdateServersRequest) {
			ServerCache.update((UpdateServersRequest) msg);
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.debug("Registry channel unregistered!");
		if (RegistryUtils.isInitialized()) {
			RegistryUtils.removeChannel();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		logger.error(cause.getMessage(), cause);
	}

}
