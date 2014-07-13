/**
 * MyEncoder.java
 * org.dxx.serialization.netty.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

import org.dxx.rpc.AbstractRequest;
import org.dxx.rpc.AbstractResponse;
import org.dxx.rpc.serialization.FstSerializer;
import org.dxx.rpc.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class DexnEncoder extends MessageToByteEncoder<Serializable> {

	static final Logger logger = LoggerFactory.getLogger(DexnEncoder.class);

	private Serializer serializer = new FstSerializer();

	private static final byte[] LENGTH_PLACEHOLDER = new byte[16];

	public static byte[] intToBytes(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
		if (msg == null) {
			logger.warn("Message is null, ctx : {}", ctx);
			return;
		}

		// Neither request nor response, deal as string 
		if (!AbstractRequest.class.isAssignableFrom(msg.getClass())
				&& !AbstractResponse.class.isAssignableFrom(msg.getClass())) {
			ByteBufOutputStream bout = new ByteBufOutputStream(out);
			bout.write(((String) msg).getBytes());
			bout.close();
			return;
		}

		int startIdx = out.writerIndex();
		LENGTH_PLACEHOLDER[0] = DexnDecoder.MAGIC_HIGH;
		LENGTH_PLACEHOLDER[1] = DexnDecoder.MAGIC_LOW;

		byte[] bytes = serializer.serialize(msg);

		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.write(LENGTH_PLACEHOLDER);
		bout.write(bytes);
		bout.close();

		out.setInt(startIdx + 2, out.writerIndex() - startIdx - 16); // body length after magic number
	}

}
