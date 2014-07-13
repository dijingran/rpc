package org.dxx.rpc.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.dxx.rpc.support.RpcApplicationListener;

/**
 * 在web项目中启动rpc服务（请尽量配置在其他Listener后面）。
 * <li>与spring集成时，可在spring 配置文件中 配置 {@link RpcApplicationListener} 代替本监听器。
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
