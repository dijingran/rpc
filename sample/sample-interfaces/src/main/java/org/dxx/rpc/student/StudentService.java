/**
 * StudentService.java
 * org.dxx.rpc.student
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.student;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
public interface StudentService {

	/**
	 * 获取学生信息
	 */
	Student getStudent(long id);
}
