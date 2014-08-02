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
public class ViewService implements Comparable<ViewService> {
	private App app;

	private Server server;

	private String desc;

	private String interfaceName;

	private boolean paused;

	private String appName;// for sort

	public ViewService(String desc, String interfaceName, boolean paused) {
		super();
		this.desc = desc;
		this.interfaceName = interfaceName;
		this.paused = paused;
	}

	@Override
	public int compareTo(ViewService o) {
		return this.appName.compareTo(o.appName);
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result + ((interfaceName == null) ? 0 : interfaceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewService other = (ViewService) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (interfaceName == null) {
			if (other.interfaceName != null)
				return false;
		} else if (!interfaceName.equals(other.interfaceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ViewService [app=" + app + ", server=" + server + ", desc=" + desc + ", interfaceName=" + interfaceName
				+ "]";
	}

}
