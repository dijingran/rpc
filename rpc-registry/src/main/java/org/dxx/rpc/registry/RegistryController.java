/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import io.netty.handler.codec.http.DefaultFullHttpRequest;

import java.util.Map;

import org.dxx.rpc.monitor.Controller;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class RegistryController implements Controller {

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		model.put("projectName", "服务注册中心");
		return "vm/registry.html";
	}

}
