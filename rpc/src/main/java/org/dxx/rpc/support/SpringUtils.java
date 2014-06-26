/**
 * SpringUtils.java
 * org.dxx.rpc.support
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.support;

import java.util.Enumeration;

import org.dxx.rpc.WebUtils;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class SpringUtils {
	static final Logger logger = LoggerFactory.getLogger(SpringUtils.class);
	private static ApplicationContext ctx;

	public static void setApplicationContext(ApplicationContext context) {
		ctx = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		T bean = springContext().getBean(clazz);
		if (bean != null) {
			return bean;
		}
		if (springContext().getParent() != null) {
			return springContext().getParent().getBean(clazz);
		}
		logger.warn("Could not resolve spring bean of type : {}", clazz);
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ApplicationContext springContext() {
		if (ctx != null) {
			return ctx;
		}

		if (WebUtils.getSc() != null) {
			if (ctx == null) {
				Enumeration<String> attrNames = WebUtils.getSc().getAttributeNames();
				for (; attrNames.hasMoreElements();) {
					String n = attrNames.nextElement();
					if (n.startsWith("org.springframework.web.servlet.FrameworkServlet.CONTEXT")) {
						ctx = (WebApplicationContext) WebUtils.getSc().getAttribute(n);
						break;
					}
				}
			}

			if (ctx == null) {
				try {
					ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(WebUtils.getSc());
				} catch (Exception e) {
				}
			}

		}

		if (ctx == null) {
			throw new RpcException("Can not get Spring ApplicationContext!");
		}
		return ctx;

	}

}
