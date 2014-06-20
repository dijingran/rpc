package org.dxx.rpc.config;

import org.dxx.rpc.exception.RpcException;

public class ConfigUtils {
	
	public static String[] parseUrl(String url) {
		if(url == null || url.trim().length() == 0) {
			return null;
		}
		if(url.matches(".+[:]\\d+")) {
			return url.split(":");
		} else {
			throw new RpcException("url 格式不正确 : " + url);
		}
	}
}
