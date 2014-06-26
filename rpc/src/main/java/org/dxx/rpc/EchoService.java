/**
 * EchoService.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

/**
 * 回声，用于检测客户端与服务端的通信是否正常
 * @author   dixingxing
 * @Date	 2014-6-25
 */
public interface EchoService {
	String ECHO_METHOD_NAME = "echo$$$";

	/**
	 * 若得到返回值，且返回值与参数值相同，说明通信成功
	 * 
	 * @param src
	 * @return
	 */
	String echo$$$(String src);
}
