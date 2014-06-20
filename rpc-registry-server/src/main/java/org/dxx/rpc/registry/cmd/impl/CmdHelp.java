/**
 * CmdHelp.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd.impl;

import org.dxx.rpc.registry.cmd.AbstractCommand;

/**
 * 帮助
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public class CmdHelp extends AbstractCommand {
	public CmdHelp(String cmd) {
		super(cmd);
	}

	@Override
	public void exec() {
		StringBuilder sb = new StringBuilder();
		sb.append(">************************** HELP ********************\r\n");
		sb.append("1.list\r\n");
		sb.append("    Show all available services \r\n");
		sb.append("        filtrate by URL(host or port) ：list -u .78\r\n");
		sb.append("        filtrate by interface name ：list -n UserService\r\n");
		sb.append("        show paused services ：list -p\r\n");

		sb.append("2.pause\r\n");
		sb.append("    All services can be paused , support -u 、-n \r\n");

		sb.append("3.resume\r\n");
		sb.append("    All services can be resumed , support -u 、-n \r\n");

		sb.append("4.q\r\n");
		sb.append("    Quit\r\n");
		channel.writeAndFlush(sb.toString());
	}
}
