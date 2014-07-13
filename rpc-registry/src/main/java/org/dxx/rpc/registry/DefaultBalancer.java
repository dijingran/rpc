/**
 * DefaultBalancer.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮循均衡 （Round Robin）
 * <p>
 * 不细化到具体服务，单纯根据url(host:port)做负载。最终效果是每个服务端（url）上的连接数趋近于相同。
 * </p>
 * 
 * @author   dixingxing
 * @Date	 2014年7月13日
 */
public class DefaultBalancer implements Balancer {

	private Map<String, AtomicInteger> couters = new ConcurrentHashMap<String, AtomicInteger>();

	@Override
	public String select(List<String> urls) {
		if (urls.isEmpty()) {
			return null;
		}
		int[] array = new int[urls.size()];

		for (int i = 0; i < urls.size(); i++) {
			AtomicInteger ai = couters.get(urls.get(i));
			if (ai == null) {
				ai = new AtomicInteger(0);
				couters.put(urls.get(i), ai);
			}
			array[i] = ai.get();
		}

		int minIndex = 0;
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
				minIndex = i;
			}
		}

		String finalUrl = urls.get(minIndex);
		couters.get(finalUrl).incrementAndGet();
		return finalUrl;
	}

	@Override
	public void reset(String url) {
		couters.remove(url);
	}

}
