/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: transaction 
 * Author: dixingxing
 * Createdate: 下午5:06:07
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.detect;

/**
 * 
 * @project transaction
 * @author dixingxing
 * @version 1.0
 * @date 2013-4-3 下午5:06:07   
 */
public interface Detector {
	/**
	 * 检测的描述
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午5:31:06
	 * @return
	 */
	String name();

	/**
	 * 探测连接是否正常，若此方法不抛异常则视为正常。
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-21 下午4:23:28
	 * @throws Exception
	 */
	void detect() throws Exception;

	/**
	 * 返回抛出的异常。
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午5:18:49
	 * @return
	 */
	String getExStack();

	/**
	 * 是否检测失败
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午5:30:54
	 * @return
	 */
	boolean isFailed();

	/**
	 * 探测花费的时间
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-5 下午6:16:01
	 * @return 
	 */
	String getCost();
}
