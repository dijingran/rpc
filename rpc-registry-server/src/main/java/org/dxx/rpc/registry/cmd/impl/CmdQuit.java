/**
 * CmdQuit.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd.impl;

import org.dxx.rpc.registry.cmd.AbstractCommand;


/**
 * 退出
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public class CmdQuit extends AbstractCommand {

	public CmdQuit(String cmd) {
		super(cmd);
	}

	/**
	 * (non-Javadoc)
	 * @see org.dxx.rpc.registry.cmd.AbstractCommand#exec()
	 */
	@Override
	public void exec() {
		channel.close();
	}

}
