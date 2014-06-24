/**
 * SpringUtils.java
 * org.dxx.rpc.support
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.support;

import java.util.Enumeration;

import org.dxx.rpc.WebUtils;
import org.dxx.rpc.exception.RpcException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class SpringUtils {
	private static ApplicationContext ctx;

	@SuppressWarnings("unchecked")
	public static ApplicationContext springContext() {
		if (ctx != null) {
			return ctx;
		}

		if (SpringContextHolder.getApplicationContext() != null) {
			ctx = SpringContextHolder.getApplicationContext();
			return ctx;
		}

		if (WebUtils.getSc() == null) {
			throw new RpcException("ServletContext has not been initialized!");
		}

		WebApplicationContext c = null;
		try {
			c = WebApplicationContextUtils.getRequiredWebApplicationContext(WebUtils.getSc());
		} catch (Exception e) {
		}
		if (c == null) {
			Enumeration<String> attrNames = WebUtils.getSc().getAttributeNames();
			for (; attrNames.hasMoreElements();) {
				String n = attrNames.nextElement();
				if (n.startsWith("org.springframework.web.servlet.FrameworkServlet.CONTEXT")) {
					c = (WebApplicationContext) WebUtils.getSc().getAttribute(n);
					break;
				}
			}
		}

		if (c == null) {
			throw new RpcException("Can not get Spring WebApplicationContext!");
		}
		SpringUtils.ctx = c;
		return c;

	}
}
