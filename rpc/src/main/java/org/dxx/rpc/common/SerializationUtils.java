/**
 * SerializeUtils.java
 * org.dxx.rpc.common
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dxx.rpc.exception.RpcException;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectOutput;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-5
 */
public class SerializationUtils {
	static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

	public static byte[] fstSerialize(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FSTObjectOutput out = fst.getObjectOutput(baos);
		try {
			out.writeObject(object);
			out.flush();
			baos.close();
		} catch (IOException e) {
			throw new RpcException(e);
		}
		return baos.toByteArray();
	}

	public static Object fstDeserialize(byte[] bytes) {
		try {
			return fst.getObjectInput(bytes).readObject();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
}
