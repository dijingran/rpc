package org.dxx.rpc.server;

import org.dxx.rpc.support.SpringUtils;

public class DefaultBeanFactoy {

	public Object get(Class<?> clazz) throws Exception {
		return clazz.newInstance();
	}

	public Object getSpringBean(Class<?> clazz) throws Exception {
		return SpringUtils.springContext().getBean(clazz);
	}
}
