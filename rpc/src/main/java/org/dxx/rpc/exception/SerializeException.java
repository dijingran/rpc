/**
 * SerializeException.java
 * org.dxx.rpc.exception
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.exception;

/**
 * 
 * @author   dixingxing
 * @Date	 2014年7月13日
 */
@SuppressWarnings("serial")
public class SerializeException extends RuntimeException {

	public SerializeException() {
		super();
	}

	public SerializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializeException(String message) {
		super(message);
	}

	public SerializeException(Throwable cause) {
		super(cause);
	}

}
