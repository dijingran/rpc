/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: guard 
 * Author: dixingxing
 * Createdate: 下午8:43:44
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.stat;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-8-21 下午8:43:44   
 */
public class Dimension {
	private long from;
	private long to;

	private AtomicLong times = new AtomicLong(0);

	public Dimension() {
		super();
	}

	public Dimension(long from, long to) {
		super();
		this.from = from;
		this.to = to;
	}

	/**
	 * 是否为主要的时间区间，每次页面展示时需要重新计算
	 * {@link StatTarget#chooseMajor()}
	 */
	private boolean major;

	public boolean matched(long cost) {
		if (cost >= from && cost < to) {
			times.incrementAndGet();
			return true;
		}

		return false;
	}

	public String getDesc() {
		if (to == Long.MAX_VALUE) {
			return from + "以上";
		}
		return from + "-" + to;
	}

	public boolean isMajor() {
		return major;
	}

	public void setMajor(boolean major) {
		this.major = major;
	}

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public AtomicLong getTimes() {
		return times;
	}

	public void setTimes(AtomicLong times) {
		this.times = times;
	}

}
