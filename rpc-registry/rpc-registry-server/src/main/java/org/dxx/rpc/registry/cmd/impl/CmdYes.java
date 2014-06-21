/**
 * CmdYes.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd.impl;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.registry.cmd.AbstractCommand;
import org.dxx.rpc.registry.cmd.ConfirmRequired;

/**
 * y
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public class CmdYes extends AbstractCommand {
	private static Map<Channel, ConfirmRequired> lastCmds = new ConcurrentHashMap<Channel, ConfirmRequired>();

	static void saveAsLast(ConfirmRequired cmd) {
		lastCmds.put(cmd.getChannel(), cmd);
	}

	static void removeLast(Channel c) {
		lastCmds.remove(c);
	}

	public CmdYes(String cmd) {
		super(cmd);
	}

	/**
	 * (non-Javadoc)
	 * @see org.dxx.rpc.registry.cmd.AbstractCommand#exec()
	 */
	@Override
	public void exec() {
		ConfirmRequired last = lastCmds.remove(channel);
		if (last != null) {
			last.afterConfirmed();
		} else {
			channel.writeAndFlush("Ignore...\r\n");
		}
	}
}
