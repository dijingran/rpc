/**
 * CourseService.java
 * org.dxx.rpc.course
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.course;

import java.util.List;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
public interface CourseService {
	/**
	 * 查询学生所修课程(示例中返回固定结果)
	 */
	List<Course> getCoursesForStudent(long studentId);
}
