/**
 * Invoker.java
 * org.dxx.rpc.registry.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.dxx.rpc.exception.TimeoutException;
import org.dxx.rpc.monitor.MonitorRequest;
import org.dxx.rpc.monitor.MonitorRequest.MonitorType;
import org.dxx.rpc.monitor.MonitorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 与服务端（客户端）通信，得到其运行状态数据。
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
public class Invoker {
	private static final Logger logger = LoggerFactory.getLogger(Invoker.class);
	private static AtomicLong seq = new AtomicLong(0);
	private static ConcurrentHashMap<Long, Invoker> context = new ConcurrentHashMap<Long, Invoker>();

	private Channel channel;
	private CountDownLatch latch = new CountDownLatch(1);
	private MonitorResponse response;

	public Invoker(Channel channel) {
		super();
		this.channel = channel;
	}

	public static void receive(MonitorResponse response) {
		logger.debug("Receive : {}", response);
		Invoker i = context.get(response.getId());
		i.response = response;
		i.latch.countDown();
	}

	public MonitorResponse invoke(MonitorType type) {
		MonitorRequest req = new MonitorRequest(type);
		req.setId(seq.incrementAndGet());
		context.put(req.getId(), this);

		logger.debug("Send : {}", req);
		channel.writeAndFlush(req);
		try {
			latch.await(2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(), e);
		}
		if (latch.getCount() == 0) {
			return response;
		}
		throw new TimeoutException();
	}
}
