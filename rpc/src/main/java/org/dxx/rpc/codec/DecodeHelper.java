package org.dxx.rpc.codec;

/**
 * 因为同时支持2中文本协议：telnet和http，所以使用此类来标记http请求的开始和结束，避免把http请求的一部分解析成telnet请求（半包情况）
 * Created by dijingran on 15/3/7.
 */
public class DecodeHelper {
    static ThreadLocal<Boolean> HTTP_HOLDER = new ThreadLocal<Boolean>();

    public static boolean isHttp() {
        return HTTP_HOLDER.get() != null && HTTP_HOLDER.get();
    }

    public static void setHttp() {
        HTTP_HOLDER.set(true);
    }

    public static void reset() {
        HTTP_HOLDER.remove();
    }
}
