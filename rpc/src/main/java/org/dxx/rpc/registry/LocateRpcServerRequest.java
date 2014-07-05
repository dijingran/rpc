package org.dxx.rpc.registry;

import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.AbstractRequest;

@SuppressWarnings("serial")
public class LocateRpcServerRequest extends AbstractRequest {

	private List<String> interfaceClasses = new ArrayList<String>();

	public List<String> getInterfaceClasses() {
		return interfaceClasses;
	}

	public void setInterfaceClasses(List<String> interfaceClasses) {
		this.interfaceClasses = interfaceClasses;
	}

	@Override
	public String toString() {
		return "LocateRpcServerRequest [interfaceClasses=" + interfaceClasses + "]";
	}

}
