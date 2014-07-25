/**
 * DexnCodec.java
 * org.dxx.rpc.codec
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

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
