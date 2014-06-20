package org.dxx.rpc.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rpc")
public class RpcClientConfigs {

	private List<RpcClientConfig> clients = new ArrayList<RpcClientConfig>();

	@XmlElement(name = "client")
	public List<RpcClientConfig> getClients() {
		return clients;
	}

	public void setClients(List<RpcClientConfig> clients) {
		this.clients = clients;
	}

	@Override
	public String toString() {
		return "RpcClientConfigs [clients=" + clients + "]";
	}

}
