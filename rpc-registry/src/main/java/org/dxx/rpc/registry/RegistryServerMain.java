/**
 * Main.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import org.dxx.rpc.monitor.HttpUtils;
import org.dxx.rpc.registry.server.RegistryServerStartup;

/**
 * 用来启动注册中心
 * 
 * @author   dixingxing
 * @Date	 2014-6-19
 */

public class RegistryServerMain {
	public static void main(String[] args) throws Exception {
		HttpUtils.addMapping("", new RegistryController());
		HttpUtils.addMapping("/registry", new RegistryController());
		new RegistryServerStartup().run();
	}
}
