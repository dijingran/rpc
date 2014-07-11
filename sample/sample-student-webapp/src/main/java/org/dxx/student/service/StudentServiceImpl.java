/**
 * StudentService.java
 * org.dxx.student.service
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.student.service;

import org.dxx.rpc.config.annotation.RpcSpringService;
import org.dxx.rpc.course.CourseService;
import org.dxx.rpc.student.Student;
import org.dxx.rpc.student.StudentService;
import org.dxx.rpc.support.RpcBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
@Service
@RpcSpringService
public class StudentServiceImpl implements StudentService {

	@RpcBean
	public CourseService courseService;

	/**
	 * @see org.dxx.rpc.student.StudentService#getStudent(long)
	 */
	@Override
	public Student getStudent(long id) {
		Student student = new Student();
		student.setId(id);
		student.setName("name" + id);
		return student;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext.xml");
		System.out.println(ctx.getBean(StudentServiceImpl.class).courseService);
	}

}
