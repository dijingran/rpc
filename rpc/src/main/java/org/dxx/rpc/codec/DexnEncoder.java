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
import org.dxx.rpc.common.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-21
 */
public class DexnEncoder extends MessageToByteEncoder<Serializable> {

	static final Logger logger = LoggerFactory.getLogger(DexnEncoder.class);

	private static final byte[] LENGTH_PLACEHOLDER = new byte[16];

	public static byte[] intToBytes(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		//必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
		//，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。虽//然 result[0] = (byte)(i  & 0xFF000000); 这样不行，但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);
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
			return;
		}

		int startIdx = out.writerIndex();
		LENGTH_PLACEHOLDER[0] = DexnDecoder.MAGIC_HIGH;
		LENGTH_PLACEHOLDER[1] = DexnDecoder.MAGIC_LOW;

		byte[] bytes = SerializationUtils.fstSerialize(msg);
		System.arraycopy(intToBytes(bytes.length), 0, LENGTH_PLACEHOLDER, 2, 4);

		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.write(LENGTH_PLACEHOLDER);

		bout.write(bytes);
		out.setInt(startIdx + 2, out.writerIndex() - startIdx - 16); // body length after magic number
	}

}
