package org.dxx.rpc.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rpc")
public class RpcMockConfigs {

	private List<RpcMockConfig> mocks = new ArrayList<RpcMockConfig>();

	@XmlElement(name = "mock")
	public List<RpcMockConfig> getMocks() {
		return mocks;
	}

	public void setMocks(List<RpcMockConfig> mocks) {
		this.mocks = mocks;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RpcMockConfigs [mocks=" + mocks + "]";
	}
}
