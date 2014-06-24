/**
 * CourseAppMain.java
 * org.dxx.course.service
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.course;

import org.dxx.rpc.RpcUtils;
import org.dxx.rpc.student.StudentService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
public class CourseAppMain {
	public static void main(String[] args) throws InterruptedException {
		new ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml");

		RpcUtils.startup();

		while (true) {
			try {
				System.out.println(RpcUtils.get(StudentService.class).getStudent(1L));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(1000L);
		}
	}
}
