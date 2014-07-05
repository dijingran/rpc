/**
 * AbstractResponse.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import java.io.Serializable;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-5
 */
@SuppressWarnings("serial")
public abstract class AbstractResponse implements Serializable {
	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
