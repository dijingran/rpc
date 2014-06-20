package org.dxx.rpc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

public class RpcListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebUtils.setSc(sce.getServletContext());

		RpcUtils.startupSync();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
