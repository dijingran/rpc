/**
 * StopWatch.java
 * org.dxx.rpc.common
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.common;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-10
 */
public class StopWatch {
	private static int MILLION = 1000000;
	private long s;

	public StopWatch() {
		s = System.nanoTime();
	}

	/**
	 * 当前时间 - 创建此实例的时间。 单位为毫秒。
	 * @return
	 */
	public double stop() {
		return (System.nanoTime() - s) / MILLION;
	}
}
