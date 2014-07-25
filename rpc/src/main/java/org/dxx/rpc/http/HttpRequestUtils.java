/**
 * HttpRequestHandler.java
 * org.dxx.rpc.registry.server
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Handle http request.
 * 
 * @author   dixingxing
 * @Date	 2014-7-25
 */
public class HttpRequestUtils {
	static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

	public static void handle(ChannelHandlerContext ctx, Object msg) {
		DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
		if (request.getUri().endsWith("favicon.ico")) {
			return;
		}

		logger.debug("{} : {}", request.getMethod().name(), request.getUri());
		logger.trace("Received : {}", request);

		QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
		logger.trace("Query params: {}", queryDecoder.parameters());

		if (request.getMethod() == HttpMethod.POST) {
			HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
			logger.trace("Post params : {}", postDecoder.getBodyHttpDatas());
		}

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("Comming soon!"
				.getBytes()));
		response.headers().set(CONTENT_TYPE, "text/plain");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(CONNECTION, Values.KEEP_ALIVE);
		ctx.write(response);
		ctx.flush();
	}
}
