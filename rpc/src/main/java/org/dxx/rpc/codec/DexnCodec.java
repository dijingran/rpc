/**
 * DexnCodec.java
 * org.dxx.rpc.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;
import java.util.List;

import org.dxx.rpc.AbstractRequest;
import org.dxx.rpc.AbstractResponse;
import org.dxx.rpc.serialization.FstSerializer;
import org.dxx.rpc.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-25
 */
public class DexnCodec extends CombinedChannelDuplexHandler<DexnDecoder, DexnEncoder> {

	public DexnCodec() {
		super(new DexnDecoder(), new DexnEncoder());
	}
}

class DexnDecoder extends DexnTelnetDecoder {

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

class DexnEncoder extends MessageToByteEncoder<Serializable> {

	static final Logger logger = LoggerFactory.getLogger(DexnEncoder.class);

	// TODO extend point
	private Serializer serializer = new FstSerializer();

	// header length. 
	static final int HEADER_LENGTH = 16;
	static final byte MAGIC = (byte) 0x80;
	static final byte[] RESERVE_PLACEHOLDER = new byte[11];

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

		byte[] bytes = serializer.serialize(msg);

		ByteBufOutputStream bout = new ByteBufOutputStream(out);

		bout.write(MAGIC);
		bout.writeInt(bytes.length);
		bout.write(RESERVE_PLACEHOLDER);
		bout.write(bytes);
		bout.close();
	}

}
