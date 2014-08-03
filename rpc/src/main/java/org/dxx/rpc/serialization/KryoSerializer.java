/**
 * KryoSerializer.java
 * org.dxx.rpc.serialization
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
public class KryoSerializer implements Serializer {
	Kryo kryo = new Kryo();

	@Override
	public byte[] serialize(Object object) throws Exception {
		Output out = new Output(512, -1);
		try {
			kryo.writeClassAndObject(out, object);
		} finally {
			out.flush();
		}
		return out.toBytes();
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		return kryo.readClassAndObject(new Input(bytes));
	}
}
