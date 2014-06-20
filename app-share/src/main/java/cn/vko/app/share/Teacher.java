/**
 * Teacher.java
 * cn.vko.app.share
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app.share;

import java.io.Serializable;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

@SuppressWarnings("serial")
public class Teacher implements Serializable {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
