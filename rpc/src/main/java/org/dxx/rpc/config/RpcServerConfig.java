package org.dxx.rpc.config;

import java.io.IOException;
import java.net.ServerSocket;

import javax.xml.bind.annotation.XmlAttribute;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerConfig {
	private static final Logger logger = LoggerFactory.getLogger(RpcServerConfig.class);
	private int port;
	private String packages;

	@XmlAttribute
	public int getPort() {
		if (port <= 0) {
			this.port = getAvailablePort(RpcConstants.DEFAULT_SERVER_PORT);
		}
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@XmlAttribute
	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	@Override
	public String toString() {
		return "RpcServerConfig [port=" + port + ", packages=" + packages + "]";
	}

	/**
	 * 
	 * 获得空闲端口
	 * <p>
	 *
	 * @param p
	 * @return 
	 */
	private int getAvailablePort(int p) {
		int i = p;
		for (; i < 65530; i++) {
			try {
				ServerSocket ss = new ServerSocket(i);
				ss.close();
				logger.debug("using free port : {}", i);
				return i;
			} catch (IOException ex) {
				continue; // try next port
			}
		}
		throw new RpcException("can't find a free port");
	}

}
