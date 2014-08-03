/**
 * MonitorResponse.java
 * org.dxx.rpc.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import org.dxx.rpc.AbstractResponse;

/**
 * 
 * <li> 服务端运行状态
 * <ol>
 * <li>JVM</li>
 * <li>线程池</li>
 * <li>各服务运行状态（执行次数，异常次数，执行时间等）</li>
 * </ol>
 * </li>
 * 
 * <li> 客户端运行状态
 * <ol>
 * <li>JVM</li>
 * <li>调用状态（调用次数，超时次数，异常次数，相应时间等）</li>
 * </ol>
 * </li>
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
@SuppressWarnings("serial")
public class MonitorResponse extends AbstractResponse {
	private Object object;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
