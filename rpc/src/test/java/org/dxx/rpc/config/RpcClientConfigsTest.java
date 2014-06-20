package org.dxx.rpc.config;

import org.dxx.rpc.config.RpcClientConfigs;
import org.dxx.rpc.config.loader.JaxbMapper;
import org.junit.Assert;
import org.junit.Test;

public class RpcClientConfigsTest {

	@Test
	public void testGetClients() {
		RpcClientConfigs configs = JaxbMapper.fromClasspathXmlFile("RpcClient.xml", RpcClientConfigs.class);
		Assert.assertEquals("org.dxx.rpc.share.HelloService", configs.getClients().get(0).getInterfaceClass());
	}

}
