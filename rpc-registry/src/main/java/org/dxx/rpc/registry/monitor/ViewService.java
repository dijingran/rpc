/**
 * Service.java
 * org.dxx.rpc.registry.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class ViewService {
	private Server server;

	private String desc;

	private String interfaceName;

	private boolean paused;

	public ViewService(String desc, String interfaceName, boolean paused) {
		super();
		this.desc = desc;
		this.interfaceName = interfaceName;
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
