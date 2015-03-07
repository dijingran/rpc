/**
 * HttpRequestHandler.java
 * org.dxx.rpc.registry.server
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
 */

package org.dxx.rpc.monitor;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dxx.rpc.codec.DecodeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle http request.
 *
 * @author dixingxing
 * @Date 2014-7-25
 */
public class HttpUtils {
    static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static Map<String, Controller> mappings = new ConcurrentHashMap<String, Controller>();

    public static void addMapping(String uri, Controller c) {
        mappings.put(uri, c);
    }

    public static void handleRequest(ChannelHandlerContext ctx, Object msg) {
        DecodeHelper.reset();
        DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
        logger.trace("{} : {}", request.getMethod().name(), request.getUri());
        logger.trace("Received : {}", request);
        QueryStringDecoder qsDecoder = new QueryStringDecoder(request.getUri());
        logger.trace("Query params: {}", qsDecoder.parameters());

        if (request.getMethod() == HttpMethod.POST) {
            HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
            logger.trace("Post params : {}", postDecoder.getBodyHttpDatas());
        }
        String responseBody = invokeController(request, qsDecoder);
        if (responseBody == null) {// 没有映射controller
            writeBadResponse(ctx);
            return;
        }
        writeResponse(ctx, responseBody);
    }

    public static String path(DefaultFullHttpRequest request) {
        String path = new QueryStringDecoder(request.getUri()).path();
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    public static String getParam(String key, QueryStringDecoder qs) {
        if (qs.parameters().containsKey(key)) {
            return qs.parameters().get(key).get(0);
        }
        return null;
    }

    static String path(QueryStringDecoder queryStringDecoder) {
        String path = queryStringDecoder.path();
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private static String invokeController(DefaultFullHttpRequest request, QueryStringDecoder qsDecoder) {
        Controller c = mappings.get(path(qsDecoder));
        if (c == null) {
            logger.debug("Can not find request mapping for uri : {}", path(qsDecoder));
            return null;
        }

        Map<String, Object> m = new HashMap<String, Object>();
        String result = c.exec(request, m);
        if (result == null) {
            return "";
        }

        m.put("screen_content", VelocityUtils.renderFile(result, m));

        String layout = m.get("layout") != null ? (String) m.get("layout") : "vm/layout/default.html";
        return VelocityUtils.renderFile(layout, m);
    }

    private static void writeResponse(ChannelHandlerContext ctx, String html) {
        byte[] bytes;
        try {
            bytes = html.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = "".getBytes();
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
        ctx.writeAndFlush(response);
        if (logger.isDebugEnabled()) {
            logger.debug("Write response body.");
        }
    }

    private static void writeBadResponse(ChannelHandlerContext ctx) {
        byte[] bytes = "".getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, Unpooled.wrappedBuffer(bytes));
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
        ctx.writeAndFlush(response);
        if (logger.isDebugEnabled()) {
            logger.debug("Write bad response");
        }
    }
}
