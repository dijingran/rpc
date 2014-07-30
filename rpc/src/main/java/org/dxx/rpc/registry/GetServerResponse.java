package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.AbstractResponse;

/**
 * 接口对应的一个服务端地址，以及此服务端提供的其它服务。
 * 
 * @author   dixingxing
 * @Date	 2014-7-12
 * @see GetServerRequest
 */
@SuppressWarnings("serial")
public class GetServerResponse extends AbstractResponse {

	private String errorMessage;

	private String host;

	private int port;

	private List<Service> services = new ArrayList<Service>();

	public GetServerResponse() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return this.errorMessage == null;
	}

	@Override
	public String toString() {
		return "GetServerLocationResponse [id=" + id + ",errorMessage=" + errorMessage + ", host=" + host + ", port="
				+ port + ", services=" + services + "]";
	}

}
