/**
 * App.java
 * org.dxx.rpc.registry.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-8-2
 */
public class App {
	private String name;

	private Set<ViewService> services2 = new HashSet<ViewService>();

	private List<ViewService> services = new ArrayList<ViewService>();

	private Set<Server> servers = new HashSet<Server>();

	public App(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Server> getServers() {
		return servers;
	}

	public void setServers(Set<Server> servers) {
		this.servers = servers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		App other = (App) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Set<ViewService> getServices2() {
		return services2;
	}

	public void setServices2(Set<ViewService> services2) {
		this.services2 = services2;
	}

	public List<ViewService> getServices() {
		return services;
	}

	public void setServices(List<ViewService> services) {
		this.services = services;
	}

	public void addService(ViewService vs) {
		vs.setAppName(this.name);
		services.add(vs);
		if (services.size() == 1) {
			vs.setApp(this);
		}
	}

	public void addService2(ViewService vs) {
		vs.setAppName(this.name);
		services2.add(vs);
		if (services2.size() == 1) {
			vs.setApp(this);
		}
	}

	@Override
	public String toString() {
		return "App [name=" + name + "]";
	}
}
