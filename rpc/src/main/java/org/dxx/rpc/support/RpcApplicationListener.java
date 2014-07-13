package org.dxx.rpc.support;

import java.lang.reflect.Field;

import org.dxx.rpc.client.Clients;
import org.dxx.rpc.common.RpcUtils;
import org.dxx.rpc.common.WebUtils;
import org.dxx.rpc.config.annotation.RpcSpringService;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * 在Spring web 项目中，可以在spring配置文件中配置：<p>
 * 
 * {@code <bean class="org.dxx.rpc.support.RpcApplicationListener"></bean>}
 *  </p>
 *  配置后可以:
 *  <li>使用{@link RpcSpringService}注解将spring的bean暴露为远程服务
 *  <li>使用 {@link RpcBean} 注解对远程服务的接口进行注入
 * @author   dixingxing
 * @Date	 2014-6-26
 */
public class RpcApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
	static final Logger logger = LoggerFactory.getLogger(RpcApplicationListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		long s = System.currentTimeMillis();
		ApplicationContext ctx = event.getApplicationContext();
		SpringUtils.setApplicationContext(ctx);

		try {
			if (ctx instanceof XmlWebApplicationContext) {
				WebUtils.setSc(((XmlWebApplicationContext) ctx).getServletContext());
			}
		} catch (Throwable e) {
			logger.debug("Case to XmlWebApplicationContext Ex : {}", e.getMessage());
		}

		Clients.init();

		DefaultListableBeanFactory bf = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
		for (String bdn : bf.getBeanDefinitionNames()) {
			wiring(bf.getBean(bdn));
		}

		if (ctx.getParent() != null) {
			bf = (DefaultListableBeanFactory) ctx.getParent().getAutowireCapableBeanFactory();
			for (String bdn : bf.getBeanDefinitionNames()) {
				wiring(bf.getBean(bdn));
			}
		}

		logger.debug("Wiring @RpcBean fileds cost : {} ms.", System.currentTimeMillis() - s);
		RpcUtils.startup();
	}

	public static void wiring(Object bean) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(RpcBean.class)) {
				try {
					boolean accessible = field.isAccessible();
					if (!accessible) {
						field.setAccessible(true);
					}
					Object v = RpcUtils.get(field.getType());
					field.set(bean, v);
					logger.debug("Wiring rpc bean : {}", field);
					if (!accessible) {
						field.setAccessible(false);
					}
				} catch (Exception e) {
					throw new RpcException("Wiring rpc bean error.", e);
				}
			}
		}

	}

}