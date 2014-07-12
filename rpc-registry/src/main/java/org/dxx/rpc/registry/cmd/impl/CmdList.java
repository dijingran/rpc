/**
 * CmdList.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dxx.rpc.registry.Service;
import org.dxx.rpc.registry.ServiceRepository;
import org.dxx.rpc.registry.cmd.AbstractConditionCommand;

/**
 * list命令(服务列表)
 * <ol>
 * <li><strong>list -u 192.168.1.78 </strong><br>
 * 		包含指定字符的 url 下的所有服务
 * 
 * <li><strong>list -n UserService </strong><br>
 * 		包含指定字符的服务
 * </ol>
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public class CmdList extends AbstractConditionCommand {

	protected Map<String, List<String>> filtratedResult = new HashMap<String, List<String>>();
	private boolean empty = true;

	public CmdList(String cmd) {
		super(cmd);
	}

	/**
	 * @see org.dxx.rpc.registry.cmd.AbstractCommand#exec()
	 */
	@Override
	public void exec() {
		channel.writeAndFlush(buildResponse().toString());
	}

	public boolean isEmpty() {
		return empty;
	}

	StringBuilder buildResponse() {
		Map<String, List<String>> map = filtrate();
		if (map.isEmpty()) {
			return new StringBuilder(">No services found!\r\n");
		}
		StringBuilder sb = conditionTip();
		for (String u : map.keySet()) {
			List<String> inters = map.get(u);
			if (inters != null && !inters.isEmpty()) {
				sb.append("[").append(u).append("]\r\n");
				for (String inter : inters) {
					sb.append("      ").append(inter).append("\r\n");
				}
			}
		}
		return sb;
	}

	private Map<String, List<String>> filtrate() {
		Map<String, List<Service>> services = ServiceRepository.getInstance().getServices();

		Map<String, List<String>> tmpMap = new HashMap<String, List<String>>();
		for (String u : services.keySet()) {
			if (this.url == null || u.toLowerCase().indexOf(this.url) >= 0) {
				List<Service> inters = services.get(u);
				List<String> list = new ArrayList<String>();
				for (Service inter : inters) {
					if (pause && !ServiceRepository.isPaused(u, inter.getInterfaceClass())) {
						continue;
					}
					if (!pause && ServiceRepository.isPaused(u, inter.getInterfaceClass())) {
						continue;
					}

					if (this.name == null || inter.getInterfaceClass().toLowerCase().indexOf(this.name) >= 0) {
						list.add(inter.getInterfaceClass());
						empty = false;
					}
				}
				tmpMap.put(u, list);
			}
		}

		this.filtratedResult = tmpMap;
		return tmpMap;
	}

	private StringBuilder conditionTip() {
		StringBuilder sb = new StringBuilder("> All ");
		sb.append("[");
		sb.append(pause ? "paused" : "available");
		sb.append("]  services ");
		if (url != null || name != null) {
			sb.append("which matched [");
			if (url != null) {
				sb.append("-u ").append(url);
			}
			if (name != null) {
				sb.append(" -n ").append(name);
			}
			sb.append("]");
		}

		sb.append("\r\n");
		return sb;
	}

}
