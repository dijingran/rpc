/**
 * DexnObjectDecoder.java
 * org.dxx.serialization.netty.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.dxx.rpc.common.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-5
 */
public class DexnDecoder extends DexnTelnetDecoder {
	static final Logger logger = LoggerFactory.getLogger(DexnDecoder.class);
	// header length.
	protected static final int HEADER_LENGTH = 16;

	// magic header.
	protected static final short MAGIC = (short) 0xdabb;

	protected static final byte MAGIC_HIGH = shortToByte(MAGIC)[0];

	protected static final byte MAGIC_LOW = shortToByte(MAGIC)[1];

	private static byte[] shortToByte(short s) {
		int t = s;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(t & 0xff).byteValue();
			t = t >> 8;
		}
		return b;
	}

	public static int byteToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * @see org.dxx.serialization.netty.codec.DexnTelnetDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEADER_LENGTH) {
			super.decode(ctx, in, out);
			return;
		}
		int oldIndex = in.readerIndex();
		byte[] header = new byte[HEADER_LENGTH];
		in.readBytes(header);
		if (header[0] != MAGIC_HIGH || header[1] != MAGIC_LOW) {
			in.setIndex(oldIndex, in.writerIndex());
			super.decode(ctx, in, out);
			return;
		}

		// with magic num
		int bodyLength = byteToInt(header, 2);
		byte[] body = new byte[bodyLength];
		in.readBytes(body);

		out.add(SerializationUtils.fstDeserialize(body));
	}
}
