/**
 * DefaultBalancer.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry;

import java.util.List;
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

	/** url, counter */
	private ConcurrentHashMap<String, AtomicInteger> couters = new ConcurrentHashMap<String, AtomicInteger>();

	@Override
	public String select(List<String> urls) {
		if (urls.isEmpty()) {
			return null;
		}
		int[] countArray = new int[urls.size()];

		for (int i = 0; i < urls.size(); i++) {
			AtomicInteger ai = couters.putIfAbsent(urls.get(i), new AtomicInteger(0));
			countArray[i] = ai != null ? ai.get() : 0;
		}

		int minIndex = 0;
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < countArray.length; i++) {
			if (countArray[i] < min) {
				min = countArray[i];
				minIndex = i;
			}
		}

		String url = urls.get(minIndex);
		couters.get(url).incrementAndGet();
		return url;
	}

	@Override
	public void reset(String url) {
		couters.remove(url);
	}

}
