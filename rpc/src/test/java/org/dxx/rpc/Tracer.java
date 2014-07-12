/**
 * Tracer.java
 * org.dxx.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public abstract class Tracer {

	public static void doTrace(String name, int times, Tracer t) {
		long s = System.nanoTime();
		for (int i = 0; i < times; i++) {
			t.exec();
		}
		long cost = (System.nanoTime() - s);
		System.out.println(name + " : invoke " + times + " times cost : " + cost + " ms , average : " + cost * 1.0
				/ times + " ns.");
	}

	public abstract void exec();

}
