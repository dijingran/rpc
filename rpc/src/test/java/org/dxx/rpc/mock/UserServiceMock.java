/**
 * HelloServiceMock.java
 * org.dxx.rpc.mock
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.mock;

import org.dxx.rpc.share.UserService;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-25
 */
public class UserServiceMock implements UserService {

	/**
	 * @see org.dxx.rpc.share.UserService#add(java.lang.String)
	 */
	@Override
	public int add(String name) {
		return -100;
	}

}
