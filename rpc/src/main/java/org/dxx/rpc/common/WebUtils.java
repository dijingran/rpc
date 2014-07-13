package org.dxx.rpc.common;

import javax.servlet.ServletContext;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */
public class WebUtils {

	private static WebUtils instance;

	private ServletContext sc;

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

	public static String getWebroot() {
		return getInstance().sc == null ? null : getInstance().sc.getRealPath("/");
	}
}
