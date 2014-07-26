/**
 * ServerController.java
 * org.dxx.rpc.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import io.netty.handler.codec.http.DefaultFullHttpRequest;

import java.util.Map;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年7月26日
 */

public class ServerController implements Controller {

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		return "vm/server.html";
	}

}
