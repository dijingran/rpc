package org.dxx.rpc.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 工具类, 获取Spring容器中的上下文信息
 * <p>
 * @author   hubin
 * @Date	 2014-5-22 	 
 */
public class SpringContextHolder implements ApplicationContextAware {
	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@SuppressWarnings("cast")
	public static <T> T getBean(Class<T> clazz) {
		checkApplicationContext();
		return (T) context.getBean(clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		context = ac;
	}

	private static void checkApplicationContext() {
		if (context == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在spring配置文件中定义SpringContextHolder");
		}
	}
}