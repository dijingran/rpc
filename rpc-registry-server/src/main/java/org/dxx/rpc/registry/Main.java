/**
 * Main.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import org.dxx.rpc.registry.cmd.TelnetServerStartup;
import org.dxx.rpc.registry.server.RegistryServerStartup;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author   dixingxing
 * @Date	 2014-6-19
 */

public class Main {
	public static void main(String[] args) throws Exception {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new TelnetServerStartup(8080).run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		RegistryServerStartup.startup();
	}
}
