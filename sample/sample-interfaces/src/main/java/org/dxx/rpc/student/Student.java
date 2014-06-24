/**
 * Student.java
 * org.dxx.rpc.student
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.student;

import java.io.Serializable;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-23
 */
@SuppressWarnings("serial")
public class Student implements Serializable {
	private long id;

	private String name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + "]";
	}
}
