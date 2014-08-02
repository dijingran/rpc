/**
 * Server.java
 * org.dxx.rpc.registry.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class Server {
	private String url;

	private String app;

	private List<ViewService> services = new ArrayList<ViewService>();

	public Server(String url) {
		super();
		this.url = url;
	}

	public void addService(ViewService service) {
		services.add(service);
		if (services.size() == 1) {
			service.setServer(this);
		}
	}

	public boolean isAllPaused() {
		for (ViewService s : services) {
			if (!s.isPaused()) {
				return false;
			}
		}
		return true;
	}

	public boolean isNonePaused() {
		for (ViewService s : services) {
			if (s.isPaused()) {
				return false;
			}
		}
		return true;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ViewService> getServices() {
		return services;
	}

	public void setServices(List<ViewService> services) {
		this.services = services;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Server other = (Server) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
