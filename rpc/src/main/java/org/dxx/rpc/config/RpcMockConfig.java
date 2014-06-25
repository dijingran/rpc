package org.dxx.rpc.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class RpcMockConfig implements Serializable {
	Logger logger = LoggerFactory.getLogger(RpcMockConfig.class);

	private String interfaceClass;

	private String mockClass;

	@XmlAttribute
	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@XmlAttribute
	public String getMockClass() {
		return mockClass;
	}

	public void setMockClass(String mockClass) {
		this.mockClass = mockClass;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RpcMockConfig [interfaceClass=" + interfaceClass + ", mockClass=" + mockClass + "]";
	}
}
