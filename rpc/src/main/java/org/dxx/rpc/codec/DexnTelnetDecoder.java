/**
 * DexnDecoder.java
 * org.dxx.serialization.netty.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-5
 */
public class DexnTelnetDecoder extends ByteToMessageDecoder {
	static final Logger logger = LoggerFactory.getLogger(DexnTelnetDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() == 0) {
			return;
		}

		byte b = in.getByte(in.readerIndex() + in.readableBytes() - 1); // last word
		if (firstLetter) {
			firstLetter = false;
			ctx.channel().writeAndFlush(new String(new byte[] { b }, getCharset()));
		}

		if (b < 0) { // ingore
			return;
		}

		byte[] bytes = new byte[in.readableBytes()];
		in.getBytes(in.readerIndex(), bytes);
		if (isExit(bytes)) {
			ctx.close();
			return;
		}

		String text = toString(bytes);
		if (text.indexOf("HTTP") > 5) {// is HTTP request, fire next decoder.
			ctx.fireChannelRead(in.readerIndex(0).retain());
			return;
		}

		if (b == 13 || b == 10) { // enter
			in.skipBytes(bytes.length);
			out.add(text);
		} else {
			if (b == 8) { // backspace
				boolean doublechar = bytes.length >= 3 && bytes[bytes.length - 3] < 0; // double byte char
				ctx.channel()
						.writeAndFlush(new String(doublechar ? new byte[] { 32, 32, 8, 8 } : new byte[] { 32, 8 }));
				return;
			}
		}

	}

	private static String toString(byte[] message) {
		byte[] copy = new byte[message.length];
		int index = 0;
		for (int i = 0; i < message.length; i++) {
			byte b = message[i];
			if (b == '\b') { // backspace
				if (index > 0) {
					index--;
				}
				if (i > 2 && message[i - 2] < 0) { // double byte char
					if (index > 0) {
						index--;
					}
				}
			} else if (b == 27) { // escape
				if (i < message.length - 4 && message[i + 4] == 126) {
					i = i + 4;
				} else if (i < message.length - 3 && message[i + 3] == 126) {
					i = i + 3;
				} else if (i < message.length - 2) {
					i = i + 2;
				}
			} else if (b == -1 && i < message.length - 2 && (message[i + 1] == -3 || message[i + 1] == -5)) { // handshake
				i = i + 2;
			} else {
				copy[index++] = message[i];
			}
		}
		if (index == 0) {
			return "";
		}
		return new String(copy, 0, index, getCharset()).trim();
	}

	private static boolean endsWith(byte[] message, byte[] command) {
		if (message.length < command.length) {
			return false;
		}
		int offset = message.length - command.length;
		for (int i = command.length - 1; i >= 0; i--) {
			if (message[offset + i] != command[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean isExit(byte[] message) {
		for (Object cmd : EXIT) {
			if (endsWith(message, (byte[]) cmd)) {
				return true;
			}
		}
		return false;
	}

	private static Charset getCharset() {
		try {
			return Charset.forName("GBK");
		} catch (Throwable t) {
			logger.warn(t.getMessage(), t);
		}
		return Charset.defaultCharset();
	}

	boolean firstLetter = true;

	private static final List<?> EXIT = Arrays
			.asList(new Object[] { new byte[] { 3 } /* Windows Ctrl+C */,
					new byte[] { -1, -12, -1, -3, 6 } /* Linux Ctrl+C */, new byte[] { -1, -19, -1, -3, 6 } /* Linux Pause */});

}
