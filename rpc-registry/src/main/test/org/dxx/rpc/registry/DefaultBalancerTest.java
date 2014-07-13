/**
 * DefaultBalancerTest.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, ����΢�δ��������Ƽ����޹�˾��Ȩ����.
*/

package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014��7��13��
 */

public class DefaultBalancerTest {

	@Test
	public void test() {
		DefaultBalancer b = new DefaultBalancer();
		List<String> urls = new ArrayList<String>();
		urls.add("192.168.1.100:50020");
		urls.add("192.168.1.100:50030");
		urls.add("192.168.1.101:50040");
		urls.add("192.168.1.101:50050");

		Assert.assertEquals("192.168.1.100:50020", b.select(urls));
		Assert.assertEquals("192.168.1.100:50030", b.select(urls));
		Assert.assertEquals("192.168.1.101:50040", b.select(urls));
		Assert.assertEquals("192.168.1.101:50050", b.select(urls));

		Assert.assertEquals("192.168.1.100:50020", b.select(urls));

		b.reset("192.168.1.100:50020");
		Assert.assertEquals("192.168.1.100:50020", b.select(urls));
	}

}
