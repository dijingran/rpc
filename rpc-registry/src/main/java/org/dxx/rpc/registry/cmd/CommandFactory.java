/**
 * CommandFactory.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd;

import org.dxx.rpc.registry.cmd.impl.CmdHelp;
import org.dxx.rpc.registry.cmd.impl.CmdList;
import org.dxx.rpc.registry.cmd.impl.CmdPause;
import org.dxx.rpc.registry.cmd.impl.CmdQuit;
import org.dxx.rpc.registry.cmd.impl.CmdResume;
import org.dxx.rpc.registry.cmd.impl.CmdUnknown;
import org.dxx.rpc.registry.cmd.impl.CmdYes;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public class CommandFactory {

	public AbstractCommand get(Object msg) {
		String cmd = msg.toString().replaceAll("\\r\\n", "");
		if (cmd == null || cmd.isEmpty()) {
			return null;
		}
		if (cmd.equalsIgnoreCase("help")) {
			return new CmdHelp(cmd);
		} else if (cmd.matches("list(\\s.*)?")) {
			return new CmdList(cmd);
		} else if (cmd.matches("pause(\\s.*)?")) {
			return new CmdPause(cmd);
		} else if (cmd.matches("resume(\\s.*)?")) {
			return new CmdResume(cmd);
		} else if (cmd.equalsIgnoreCase("y")) {
			return new CmdYes(cmd);
		} else if (cmd.equalsIgnoreCase("q")) {
			return new CmdQuit(cmd);
		}
		return new CmdUnknown(cmd);
	}
}
