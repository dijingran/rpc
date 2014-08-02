package org.dxx.rpc.config;

import static org.junit.Assert.assertEquals;

import org.dxx.rpc.config.loader.JaxbMapper;
import org.junit.Test;

public class RpcConfigTest {

	@Test
	public void test() {
		RpcConfig conf = JaxbMapper.fromClasspathXmlFile("RpcConfig.xml", RpcConfig.class);
		//		assertEquals("127.0.0.1", conf.getRegistry().getHost());
		assertEquals(50011, conf.getRpcServerConfig().getPort());
		assertEquals("app1", conf.getRpcServerConfig().getApp());
	}

}
