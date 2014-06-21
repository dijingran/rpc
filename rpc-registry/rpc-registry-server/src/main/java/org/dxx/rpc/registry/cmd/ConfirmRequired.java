/**
 * ConfirmRequired.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd;

import io.netty.channel.Channel;

/**
 * 
 * 定义"y"确认后需要执行的后续操作
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public interface ConfirmRequired {

	/**
	 * "y"确认后需要执行的后续操作
	 * <p>
	 */
	void afterConfirmed();

	Channel getChannel();
}
