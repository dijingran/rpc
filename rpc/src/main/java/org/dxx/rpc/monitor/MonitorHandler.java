/**
 * MonitorUtils.java
 * org.dxx.rpc.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import java.util.concurrent.ThreadPoolExecutor;

import org.dxx.rpc.common.RpcUtils;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.monitor.MonitorRequest.MonitorType;
import org.dxx.rpc.monitor.stat.StatContext;
import org.dxx.rpc.server.ClientChannels;
import org.dxx.rpc.server.exec.RpcChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */

public class MonitorHandler {
	private static final Logger logger = LoggerFactory.getLogger(MonitorHandler.class);

	public MonitorResponse handle(MonitorRequest request) {
		logger.debug("Receive : {}", request);
		MonitorResponse response = new MonitorResponse();
		response.setId(request.getId());
		if (MonitorType.serverStatus == request.getType()) {
			ThreadPoolExecutor e = (ThreadPoolExecutor) RpcChannelHandler.getExecutorservice();

			ServerStatus ss = new ServerStatus();
			ss.setActiveCount(e.getActiveCount());
			ss.setCompletedTaskCount(e.getCompletedTaskCount());
			ss.setCoreSize(e.getCorePoolSize());
			ss.setLargestPoolSize(e.getLargestPoolSize());
			ss.setMaxSize(e.getMaximumPoolSize());
			ss.setPoolSize(e.getPoolSize());
			ss.setQueueSize(e.getQueue().size());
			ss.setTaskCount(e.getTaskCount());

			ss.setClientCount(ClientChannels.getCg().size());
			ss.setStatMap(StatContext.getMap());

			ss.setApp(Loader.getRpcConfig().getRpcServerConfig().getApp());
			ss.setStartTime(RpcUtils.getStartTime());

			response.setObject(ss);
		}
		return response;
	}
}
