/**
 * StudentController.java
 * org.dxx.student.web
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.student.web;

import java.util.List;

import org.dxx.rpc.RpcUtils;
import org.dxx.rpc.course.Course;
import org.dxx.rpc.course.CourseService;
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

	@RequestMapping("/")
	public String index(Model model) {
		List<Course> courses = RpcUtils.get(CourseService.class).getCoursesForStudent(1L);
		model.addAttribute("courses", courses);
		return "index";
	}
}
