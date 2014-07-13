/**
 * Balancer.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import java.util.List;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年7月13日
 */

public interface Balancer {

	/**
	 * 根据负载策略，从多个服务端url中选择一个
	 */
	String select(List<String> urls);

	/**
	 * 当服务端断开连接后，清除此服务端的负载数据
	 */
	void reset(String url);

}
