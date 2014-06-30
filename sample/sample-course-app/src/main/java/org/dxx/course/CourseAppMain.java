/**
 * CourseAppMain.java
 * org.dxx.course.service
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.course;

import org.dxx.course.service.CourseServiceImpl;
import org.dxx.rpc.RpcUtils;
import org.dxx.rpc.student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动服务
 * @author   dixingxing
 * @Date	 2014-6-23
 */
public class CourseAppMain {
	static final Logger logger = LoggerFactory.getLogger(CourseAppMain.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		// 1. 初始化spring 上下文（SpringContextHolder会持有spring的 ApplicationContext实例）
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext.xml");

		// 2. 初始化rpc服务，作为服务端：对外暴露CourseService服务（CourseServiceImpl标注了@RpcSpringService）。
		RpcUtils.startup();

		// 3. 作为客户端，调用其它应用提供的StudentService服务，见RpcClient.xml

		// 3.1 使用 @RpcBean 注入的结果
		StudentService studentService = ctx.getBean(CourseServiceImpl.class).studentService;
		logger.debug("使用RpcBean注解注入远程服务接口的代理类 ，返回的结果为 ： {}", studentService.getStudent(0L));

		// 3.2 持续调用
		long i = 1L;
		while (true) {
			try {
				logger.debug("student-webapp 返回的结果：{}", RpcUtils.get(StudentService.class).getStudent(i++));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			Thread.sleep(2000L);
		}
	}
}
