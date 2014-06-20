package org.dxx.rpc.registry.server;

import org.dxx.rpc.registry.cmd.TelnetServerStartup;
import org.junit.Test;

public class RegistryServerStartupTest {

	@Test
	public void testStartup() throws Exception {
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
