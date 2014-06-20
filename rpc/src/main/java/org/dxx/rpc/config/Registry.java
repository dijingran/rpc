package org.dxx.rpc.config;

import javax.xml.bind.annotation.XmlAttribute;


public class Registry {
	public static final int DEFAULT_PORT = 1;
	private String host;
	private int port;
	
	private String url;
	
	@XmlAttribute
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		String[] ss = ConfigUtils.parseUrl(url);
		if(ss != null) {
			this.host = ss[0];
			this.port = Integer.valueOf(ss[1]);
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

	@Override
	public String toString() {
		return "Registry [host=" + host + ", port=" + port + "]";
	}

}
