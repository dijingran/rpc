/**
 * ThreadPoolStatus.java
 * org.dxx.rpc.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import java.io.Serializable;
import java.util.Map;

import org.dxx.rpc.monitor.stat.StatTarget;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */

@SuppressWarnings("serial")
public class ServerStatus implements Serializable {
	private String app;

	private long startTime;

	private int clientCount;

	private int coreSize;

	private int maxSize;

	private int poolSize;// current size

	private int activeCount;

	private int largestPoolSize;

	private long taskCount;

	private long completedTaskCount;

	private int queueSize; // current size

	private Map<String, StatTarget> statMap;

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	public int getCoreSize() {
		return coreSize;
	}

	public void setCoreSize(int coreSize) {
		this.coreSize = coreSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public int getLargestPoolSize() {
		return largestPoolSize;
	}

	public void setLargestPoolSize(int largestPoolSize) {
		this.largestPoolSize = largestPoolSize;
	}

	public long getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(long taskCount) {
		this.taskCount = taskCount;
	}

	public long getCompletedTaskCount() {
		return completedTaskCount;
	}

	public void setCompletedTaskCount(long completedTaskCount) {
		this.completedTaskCount = completedTaskCount;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public Map<String, StatTarget> getStatMap() {
		return statMap;
	}

	public void setStatMap(Map<String, StatTarget> statMap) {
		this.statMap = statMap;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "ServerStatus [clientCount=" + clientCount + ", coreSize=" + coreSize + ", maxSize=" + maxSize
				+ ", poolSize=" + poolSize + ", activeCount=" + activeCount + ", largestPoolSize=" + largestPoolSize
				+ ", taskCount=" + taskCount + ", completedTaskCount=" + completedTaskCount + ", queueSize="
				+ queueSize + ", statMap=" + statMap + "]";
	}
}
