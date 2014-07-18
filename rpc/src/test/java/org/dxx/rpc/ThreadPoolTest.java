/**
 * ThreadPoolTest.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年7月16日
 */
@Ignore
public class ThreadPoolTest {
	static Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

	BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(5);

	ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 10, 5, TimeUnit.SECONDS, workQueue);

	@Test
	public void test() throws InterruptedException {
		for (int i = 0; i < 9; i++) {
			final String name = "Job" + i;
			pool.submit(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
					}
					logger.info(name + " done! ");
				}
			});
		}

		for (int i = 0; i < 3; i++) {
			Thread.sleep(3000L);
			logger.info("pool size : {}", pool.getPoolSize());
		}

		System.out.println(" ======================== ");
		pool.setCorePoolSize(5);

		for (int i = 0; i < 9; i++) {
			final String name = "Job" + i;
			pool.submit(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
					}
					logger.info(name + " done! ");
				}
			});
		}

		for (int i = 0; i < 3; i++) {
			Thread.sleep(3000L);
			logger.info("pool size : {}", pool.getPoolSize());
		}

	}
}
