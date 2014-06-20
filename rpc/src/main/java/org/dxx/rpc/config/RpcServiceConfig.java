/**
 * RpcServiceConfig.java
 * org.dxx.rpc.config
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.config;

import java.io.Serializable;

/**
 * 服务类的定义
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

@SuppressWarnings("serial")
public class RpcServiceConfig implements Serializable {
	private String description;

	public RpcServiceConfig() {
	}

	public RpcServiceConfig(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
