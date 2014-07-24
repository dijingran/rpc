/**
 * DefaultBalancerTest.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
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
 * @Date	 2014年7月13日
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
