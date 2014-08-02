package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.AbstractRequest;

/**
 * Server -> Registry
 * 
 * @author   dixingxing
 * @Date	 2014年7月11日
 */
@SuppressWarnings("serial")
public class RegisterRequest extends AbstractRequest {
	private String app;
	private List<Service> services = new ArrayList<Service>();
	private int port;

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegisterRequest [services=" + services + ", port=" + port + "]";
	}
}
