/**
 * KryoSerializerTest.java
 * org.dxx.rpc.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.serialization;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */

public class KryoSerializerTest {
	KryoSerializer serializer = new KryoSerializer();

	@Test
	public void test() throws Exception {
		Pojo1 obj1 = new Pojo1();
		obj1.setPojo2(new Pojo2());
		byte[] bytes = serializer.serialize(obj1);

		Pojo1 obj2 = (Pojo1) serializer.deserialize(bytes);
		Assert.assertEquals("pojo1", obj2.getName());
	}

}
