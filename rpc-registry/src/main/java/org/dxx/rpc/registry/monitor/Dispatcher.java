/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

import org.dxx.rpc.monitor.Controller;
import org.dxx.rpc.monitor.HttpUtils;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class Dispatcher implements Controller {

	ServiceController serviceController = new ServiceController();
	ServerController serverController = new ServerController();

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		model.put("layout", "vm/layout/registry-layout.html");

		QueryStringDecoder qs = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = qs.parameters();
		if (params.containsKey("op")) {
			return serviceController.opState(request);
		} else if (params.containsKey("server")) {
			return serverController.serverStatus(qs, model);
		} else {
			return serviceController.list(model, HttpUtils.getParam("v", qs));
		}
	}

}
