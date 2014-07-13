/**
 * Serializer.java
 * org.dxx.rpc.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.serialization;

/**
 * 
 * @author   dixingxing
 * @Date	 2014年7月13日
 */
public interface Serializer {

	byte[] serialize(Object object);

	Object deserialize(byte[] bytes);

}
