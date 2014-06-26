/**
 * StudentController.java
 * org.dxx.student.web
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.student.web;

import java.util.List;

import org.dxx.rpc.course.Course;
import org.dxx.rpc.course.CourseService;
import org.dxx.rpc.support.RpcBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
@Controller
public class StudentController {
	static final Logger logger = LoggerFactory.getLogger(StudentController.class);
	@RpcBean
	CourseService courseService;

	@RequestMapping("/")
	public String index(Model model) {
		List<Course> courses = courseService.getCoursesForStudent(1L);
		model.addAttribute("courses", courses);
		return "index";
	}
}
