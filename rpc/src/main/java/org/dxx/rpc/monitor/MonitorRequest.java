/**
 * MonitorRequest.java
 * org.dxx.rpc.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import org.dxx.rpc.AbstractRequest;

/**
 * 获取监控数据的request
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
@SuppressWarnings("serial")
public class MonitorRequest extends AbstractRequest {
	public static enum MonitorType {
		serverStatus, clientStatus
	}

	private MonitorType type;

	public MonitorRequest() {
	}

	public MonitorRequest(MonitorType type) {
		super();
		this.type = type;
	}

	public MonitorType getType() {
		return type;
	}

	public void setType(MonitorType type) {
		this.type = type;
	}

}
