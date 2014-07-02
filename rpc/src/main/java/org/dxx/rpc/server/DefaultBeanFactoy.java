package org.dxx.rpc.server;

import org.dxx.rpc.support.SpringUtils;
import org.nutz.mvc.Mvcs;

public class DefaultBeanFactoy {

	public Object get(Class<?> clazz) throws Exception {
		return clazz.newInstance();
	}

	public Object getSpringBean(Class<?> clazz) throws Exception {
		return SpringUtils.getBean(clazz);
	}

	public Object getNutzBean(Class<?> implClass) {
		return Mvcs.ctx.getDefaultIoc().get(implClass);
	}
}
