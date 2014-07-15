/**
 * UpdateServerLocationRequest.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.AbstractRequest;

/**
 * 注册中心向客户端推送服务列表，供客户端缓存，当注册中心不可用时，可从缓存中读取服务端地址。
 * 
 * @author   dixingxing
 * @Date	 2014年7月15日
 */
@SuppressWarnings("serial")
public class UpdateServerLocationRequest extends AbstractRequest {

	/** interfaceClass, "host:port..." */
	private Map<String, List<String>> interAndUrl = new ConcurrentHashMap<String, List<String>>();

	public Map<String, List<String>> getInterAndUrl() {
		return interAndUrl;
	}

	public void setInterAndUrl(Map<String, List<String>> interAndUrl) {
		this.interAndUrl = interAndUrl;
	}

}
