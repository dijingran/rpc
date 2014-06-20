package org.dxx.rpc;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.dxx.rpc.exception.RpcException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */
public class WebUtils {

	private static WebUtils instance;

	private ServletContext sc;

	private ApplicationContext ctx;

	private WebUtils() {
	}

	private static synchronized WebUtils getInstance() {
		if (instance == null) {
			instance = new WebUtils();
		}
		return instance;
	}

	public static ServletContext getSc() {
		return getInstance().sc;
	}

	public static void setSc(ServletContext sc) {
		getInstance().sc = sc;
	}

	@SuppressWarnings("unchecked")
	public static ApplicationContext springContext() {
		if (getInstance().ctx != null) {
			return getInstance().ctx;
		}

		if (getInstance().sc == null) {
			throw new RpcException("ServletContext has not been initialized!");
		}

		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getInstance().sc);
		if (ctx == null) {
			Enumeration<String> attrNames = getInstance().sc.getAttributeNames();
			for (; attrNames.hasMoreElements();) {
				String n = attrNames.nextElement();
				if (n.startsWith("org.springframework.web.servlet.FrameworkServlet.CONTEXT")) {
					ctx = (WebApplicationContext) getInstance().sc.getAttribute(n);
					break;
				}
			}
		}

		if (ctx == null) {
			throw new RpcException("Can not get Spring WebApplicationContext!");
		}
		getInstance().ctx = ctx;
		return ctx;

	}
}
