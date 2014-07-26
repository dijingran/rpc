/**
 * HttpRequestUtilsTest.java
 * org.dxx.rpc.monitor.http
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.monitor;

import static org.junit.Assert.assertEquals;
import io.netty.handler.codec.http.QueryStringDecoder;

import org.dxx.rpc.monitor.HttpUtils;
import org.junit.Test;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class HttpRequestUtilsTest {

	@Test
	public void testPath() {
		assertEquals("/stat", HttpUtils.path(new QueryStringDecoder("/stat/?a=23&b=")));
		assertEquals("/stat", HttpUtils.path(new QueryStringDecoder("/stat?a=23&b=")));
		assertEquals("/stat", HttpUtils.path(new QueryStringDecoder("/stat/")));
		assertEquals("/stat", HttpUtils.path(new QueryStringDecoder("/stat")));
	}

}
