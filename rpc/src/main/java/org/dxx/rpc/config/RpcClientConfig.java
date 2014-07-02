package org.dxx.rpc.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.dxx.rpc.exception.RpcException;

@SuppressWarnings("serial")
public class RpcClientConfig implements Serializable {
	private String interfaceClass;

	private Class<?> inter;

	private String host;
	private int port;

	private String url;

	private int timeout;

	@XmlAttribute
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@XmlAttribute
	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
		try {
			this.inter = Class.forName(interfaceClass);
		} catch (ClassNotFoundException e) {
			throw new RpcException("接口名称不正确 : " + interfaceClass, e);
		}
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

	@XmlAttribute
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		String[] ss = ConfigUtils.parseUrl(url);
		if (ss != null) {
			this.host = ss[0];
			this.port = Integer.valueOf(ss[1]);
		}
	}

	public Class<?> getInter() {
		return inter;
	}

	public void setInter(Class<?> inter) {
		this.inter = inter;
	}

	@Override
	public String toString() {
		return "RpcClientConfig [interfaceClass=" + interfaceClass + ", inter=" + inter + ", host=" + host + ", port="
				+ port + ", timeout=" + timeout + "]";
	}

}
