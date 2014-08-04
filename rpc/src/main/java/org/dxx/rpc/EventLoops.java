/**
 * EventLoops.java
 * org.dxx.rpc
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014年8月4日
 */

public class EventLoops {
	public static EventLoopGroup workerGroup = new NioEventLoopGroup();
}
