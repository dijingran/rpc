/**
 * RpcConstants.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

/**
 * 常量，所有表示时间的均以毫秒为单位
 * 
 * @author   dixingxing
 * @Date	 2014-6-19
 */

public interface RpcConstants {
	/** 当注册中心不可用时，重连的时间间隔 */
	long REGISTRY_RETRY_TIME = 10000L;

	/** 从注册中心获取服务端URL的超时时间 */
	long LOCATE_TIME_OUT = 5000L;

	/** 调用方法默认的超时时间 */
	int DEFAULT_RESPONSE_TIMEOUT = 3000;
	/**默认的服务端端口（暴露服务的端口），使用自动端口时从此值开始，查找可用端口*/
	int DEFAULT_SERVER_PORT = 50050;
}
