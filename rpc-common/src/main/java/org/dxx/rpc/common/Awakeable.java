/**
 * Awakeable.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhance the {@link Runnable} , support blocking current thread until {@link #awake()} is invoked.
 * <p>
 * {@link #submitAndWait()} will block for {@link Awakeable#TIME_OUT} mills.
 * </p>
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public abstract class Awakeable implements Runnable {
	private static ExecutorService es = Executors.newCachedThreadPool();
	private static final long TIME_OUT = 3000L;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private Lock lock = new ReentrantLock();
	private Condition done = lock.newCondition();

	/**
	 * Submit "this" to {@link ExecutorService} , blocking current thread until {@link #awake()} is invoked.
	 * <p>
	 * 
	 * </p>
	 */
	public void submitAndWait() {
		submitAndWait(TIME_OUT);
	}

	/**
	 * 
	 * @param timeout cause current thread wait such long time
	 */
	public void submitAndWait(long timeout) {
		lock.lock();
		try {
			es.submit(this);
			long start = System.currentTimeMillis();
			done.await(timeout, TimeUnit.MILLISECONDS);

			if (System.currentTimeMillis() - start > timeout) {
				logger.warn("Time out : {}", timeout);
			}
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Do the real thing, and call {@link #awake()} method to stop blocking current thread
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public abstract void run();

	/**
	 * Signal to awake the caller thread.
	 */
	protected void awake() {
		lock.lock();
		try {
		} finally {
			logger.debug("Signal to awake.");
			done.signal();
			lock.unlock();
		}
	}

}
