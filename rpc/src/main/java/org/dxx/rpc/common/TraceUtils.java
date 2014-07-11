/**
 * TraceUtils.java
 * org.dxx.rpc.common
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.common;

import java.text.DecimalFormat;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-10
 */
public class TraceUtils {
	private static double MILLION = 1000000.0;
	private static DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * 计算起始时间（纳秒）和当前时间（纳秒）的差，并转成更易读的毫秒值
	 * @param start System.nanoTime() 
	 * @return
	 */
	public static String diff(long start) {
		return df.format((System.nanoTime() - start) / MILLION);
	}
}
