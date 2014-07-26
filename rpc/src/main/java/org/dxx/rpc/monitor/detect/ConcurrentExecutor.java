/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: transaction 
 * Author: dixingxing
 * Createdate: 下午6:33:30
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.detect;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 为每个{@link Detector} 启动一个线程进行探测。
 * @project transaction
 * @author dixingxing
 * @version 1.0
 * @date 2013-4-5 下午6:33:30
 */
public class ConcurrentExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(ConcurrentExecutor.class);

	public static final int TIME_OUT_SECONDS = 15;

	ExecutorService exec = Executors.newCachedThreadPool();

	/**
	 * 为每个{@link Detector} 启动一个线程进行探测。
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-5 下午6:51:45
	 * @param detectors
	 */
	public void detectAll(List<Detector> detectors) {
		CompletionService<Detector> ecs = new ExecutorCompletionService<Detector>(exec);
		for (final Detector d : detectors) {
			ecs.submit(new java.util.concurrent.Callable<Detector>() {
				@Override
				public Detector call() throws Exception {
					d.detect();
					return d;
				}
			});
		}
		int n = detectors.size();

		try {
			for (int i = 0; i < n; ++i) {
				ecs.take().get(TIME_OUT_SECONDS, TimeUnit.SECONDS);
				if (i == n - 1) {
					LOG.debug("{}个探测器都已经执行完毕！", n);
					break;
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}
}
