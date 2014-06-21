package org.dxx.rpc.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LocateRpcServerResponse implements Serializable {
	private String errorMessage;
	
	private List<Service> services = new ArrayList<LocateRpcServerResponse.Service>();
	
	public LocateRpcServerResponse() {}
	
	public LocateRpcServerResponse(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}



	public static class Service implements Serializable{
		private String interfaceClass;
		private String host;
		private int port;
		
		
		public Service() {
			super();
		}
		
		public Service(String interfaceClass, String host, int port) {
			super();
			this.interfaceClass = interfaceClass;
			this.host = host;
			this.port = port;
		}


		public String getInterfaceClass() {
			return interfaceClass;
		}
		public void setInterfaceClass(String interfaceClass) {
			this.interfaceClass = interfaceClass;
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

		@Override
		public String toString() {
			return "Service [interfaceClass=" + interfaceClass + ", host="
					+ host + ", port=" + port + "]";
		}
		
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
		return "LocateRpcServerResponse [errorMessage=" + errorMessage
				+ ", services=" + services + "]";
	}
}
