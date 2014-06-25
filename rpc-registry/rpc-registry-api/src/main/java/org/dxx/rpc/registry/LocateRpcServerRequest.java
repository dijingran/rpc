package org.dxx.rpc.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class LocateRpcServerRequest implements Serializable {
	private Long id;

	private List<String> interfaceClasses = new ArrayList<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
