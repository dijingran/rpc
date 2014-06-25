package org.dxx.rpc;

import java.util.concurrent.CyclicBarrier;

import org.dxx.rpc.exception.RpcTimeoutException;
import org.dxx.rpc.share.HelloService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcTest {
	static final Logger logger = LoggerFactory.getLogger(RpcTest.class);

	@Test
	public void test() throws Exception {
		RpcUtils.startup();

		final CyclicBarrier barrier = new CyclicBarrier(5);

		for (int i = 0; i < 5; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						String result = RpcUtils.get(HelloService.class).sayHello("dixingxing");
						Assert.assertEquals("Hello dixingxing !", result);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		int i = 0;
		int timeoutTimes = 0;
		long s = System.currentTimeMillis();
		try {
			for (; i < 1000; i++) {
				try {
					RpcUtils.get(HelloService.class).sayHello("" + i);
				} catch (RpcTimeoutException e) {
					timeoutTimes++;
					logger.error(e.getMessage(), e);
				}
			}
		} finally {
			long cost = System.currentTimeMillis() - s;
			if (i > 0) {
				System.out.println("--------- total times : " + i + ", timeout times : " + timeoutTimes
						+ "  total cost : " + cost + ", average cost : " + (cost / (i * 1.0)));
			}
		}
	}
}
