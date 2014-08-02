/**
 * Service.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import java.io.Serializable;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-12
 */
@SuppressWarnings("serial")
public class Service implements Serializable {
	private String app;

	private String interfaceClass;

	private String desc;

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "Service [app=" + app + ", interfaceClass=" + interfaceClass + ", desc=" + desc + "]";
	}

}
