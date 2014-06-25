/**
 * MockTest.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.mock;

import org.dxx.rpc.RpcUtils;
import org.dxx.rpc.share.UserService;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-25
 */
public class MockTest {

	@Test
	public void test() {
		RpcUtils.startup();
		Assert.assertEquals(-100, RpcUtils.get(UserService.class).add("dijingran"));
	}

}
