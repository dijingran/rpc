package org.dxx.rpc.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "rpc")
public class RpcConfig implements Serializable {
	private Registry registry;

	private RpcServerConfig rpcServerConfig;

	@XmlElement(name = "server")
	public RpcServerConfig getRpcServerConfig() {
		return rpcServerConfig;
	}

	public void setRpcServerConfig(RpcServerConfig rpcServerConfig) {
		this.rpcServerConfig = rpcServerConfig;
	}

	@XmlElement
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public String toString() {
		return "RpcConfig [registry=" + registry + ", rpcServerConfig="
				+ rpcServerConfig + "]";
	}

}
