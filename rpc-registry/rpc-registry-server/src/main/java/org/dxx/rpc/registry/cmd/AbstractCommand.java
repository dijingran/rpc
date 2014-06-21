/**
 * AbstractCommand.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd;

import io.netty.channel.Channel;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public abstract class AbstractCommand {

	protected Channel channel;

	protected String cmd;

	public AbstractCommand(String cmd) {
		super();
		this.cmd = cmd;
	}

	/**
	 * 执行相应的行为，并返回
	 * <p>
	 */
	public abstract void exec();

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

}
