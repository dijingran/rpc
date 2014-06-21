/**
 * AwakeableTest.java
 * org.dxx.rpc.common
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */

public class AwakeableTest {

	@Test
	public void test() {
		long s = System.currentTimeMillis();
		new Blocking().submitAndWait();
		long cost = System.currentTimeMillis() - s;
		Assert.assertTrue(cost > 1000L);
		Assert.assertTrue(cost < 1500L);
	}

	public class Blocking extends Awakeable {
		/**
		 * @see org.dxx.rpc.registry.cmd.Awakeable#run()
		 */
		@Override
		public void run() {
			try {
				Thread.sleep(1000L);
				awake();
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
