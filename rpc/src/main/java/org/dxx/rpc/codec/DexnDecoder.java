/**
 * DexnObjectDecoder.java
 * org.dxx.serialization.netty.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.dxx.rpc.serialization.FstSerializer;
import org.dxx.rpc.serialization.Serializer;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-5
 */
public class DexnDecoder extends DexnTelnetDecoder {

	private Serializer serializer = new FstSerializer();

	private enum State {
		header, body, telnet;
	}

	private State state = State.header;

	private int bodyLength;

	/**
	 * @see org.dxx.serialization.netty.codec.DexnTelnetDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() <= 0) {
			return;
		}
		if (state == State.telnet) {
			super.decode(ctx, in, out);
		} else if (state == State.header) {
			byte magic = in.getByte(in.readerIndex());
			if (magic != DexnEncoder.MAGIC) {
				state = State.telnet;
				super.decode(ctx, in, out);
				return;
			}

			if (in.readableBytes() < DexnEncoder.HEADER_LENGTH) {
				return;
			}
			in.skipBytes(1); // skip magic
			bodyLength = in.readInt();
			in.skipBytes(11); // skip reserve bytes

			state = State.body;
		} else if (state == State.body) {
			if (in.readableBytes() < bodyLength) {
				return;
			}
			byte[] body = new byte[bodyLength];
			in.readBytes(body);
			out.add(serializer.deserialize(body));
			state = State.header;
		}
	}
}
