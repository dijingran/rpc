/**
 * CmdPause.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd.impl;

import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.ConfirmRequired;

/**
 * 恢复
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */
public class CmdResume extends CmdList implements ConfirmRequired {

	public CmdResume(String cmd) {
		super(cmd);
		this.pause = true; // 仅查询尚未暂停的服务
	}

	/**
	 * (non-Javadoc)
	 * @see org.dxx.rpc.registry.cmd.AbstractCommand#exec()
	 */
	@Override
	public void exec() {
		StringBuilder sb = buildResponse();
		if (super.isEmpty()) {
			channel.writeAndFlush("> No services can be resumed!\r\n");
			return;
		}
		sb.append("-------You are resuming the services, [Confirm] \"y\" / [Cancel] other keys:");

		channel.writeAndFlush(sb.toString());
		CmdYes.saveAsLast(this);
	}

	/**
	 * @see org.dxx.rpc.registry.cmd.ConfirmRequired#afterConfirmed()
	 */
	@Override
	public void afterConfirmed() {
		channel.writeAndFlush("Resuming...\r\n");
		ServiceRepository.getInstance().resume(filtratedResult);
		getChannel().writeAndFlush("Resumed!\r\n");
	}

}
