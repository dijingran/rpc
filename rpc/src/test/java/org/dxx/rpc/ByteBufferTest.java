/**
 * ByteBufferTest.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.junit.Test;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author   dixingxing
 * @Date	 2014-6-19
 */

public class ByteBufferTest {

	@Test
	public void test() {
		ByteBuf bb = Unpooled.buffer(100);

		bb.writeInt(259);
		System.out.println(bb.readByte());
		System.out.println(bb.readByte());
		System.out.println(bb.readByte());
		System.out.println(bb.readByte());

	}
}
