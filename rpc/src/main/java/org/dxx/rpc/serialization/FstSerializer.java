/**
 * FstSerializer.java
 * org.dxx.rpc.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.serialization;

import java.io.ByteArrayOutputStream;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectOutput;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年7月13日
 */
public class FstSerializer implements Serializer {
	static FSTConfiguration FST = FSTConfiguration.createDefaultConfiguration();

	@Override
	public byte[] serialize(Object object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FSTObjectOutput out = FST.getObjectOutput(baos);
		out.writeObject(object);
		out.flush();
		baos.close();
		return baos.toByteArray();
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		return FST.getObjectInput(bytes).readObject();
	}

}
