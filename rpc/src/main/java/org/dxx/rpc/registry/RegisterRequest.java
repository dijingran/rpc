package org.dxx.rpc.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.AbstractRequest;

@SuppressWarnings("serial")
public class RegisterRequest extends AbstractRequest {
	private List<RegisterRequest.Service> services = new ArrayList<RegisterRequest.Service>();
	private int port;

	public static class Service implements Serializable {

		private String interfaceClass;

		private String desc;

		public String getInterfaceClass() {
			return interfaceClass;
		}

		public void setInterfaceClass(String interfaceClass) {
			this.interfaceClass = interfaceClass;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return "Service [interfaceClass=" + interfaceClass + ", desc=" + desc + "]";
		}

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<RegisterRequest.Service> getServices() {
		return services;
	}

	public void setServices(List<RegisterRequest.Service> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "RegisterRequest [services=" + services + ", port=" + port + "]";
	}
}
