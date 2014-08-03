/**
 * Pojo1.java
 * org.dxx.rpc.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.serialization;

import java.io.Serializable;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */

@SuppressWarnings("serial")
public class Pojo1 implements Serializable {
	private String name = "pojo1";

	private Pojo2 pojo2;

	public Pojo2 getPojo2() {
		return pojo2;
	}

	public void setPojo2(Pojo2 pojo2) {
		this.pojo2 = pojo2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
