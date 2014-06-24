package org.dxx.rpc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 启动服务。
 * <li>与spring集成时，在web.xml中请确保放在监听器{@link org.springframework.web.context.ContextLoaderListener} 的后面，
 * 以保证能够从spring context中获取正确的实例。
 * </li>
 * Put this after the other listeners.
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */
public class RpcListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebUtils.setSc(sce.getServletContext());
		RpcUtils.startup();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
