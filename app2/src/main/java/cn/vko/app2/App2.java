/**
 * App2.java
 * cn.vko.app2
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app2;

import org.dxx.rpc.RpcUtils;
import org.dxx.rpc.client.Clients;
import org.dxx.rpc.exception.RpcTimeoutException;

import cn.vko.app.share.Teacher;
import cn.vko.app.share.TeacherService;
import cn.vko.fz.TeacherRpcService;

/**
 * 调用远程服务
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

public class App2 {
	public static void main(String[] args) throws InterruptedException {
		RpcUtils.startupSync();

		Teacher t = new Teacher();
		t.setName("袁腾飞");

		try {
			String name = RpcUtils.get(TeacherRpcService.class).getName(1L);
			System.out.println(name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TeacherService service = Clients.getRpcProxy(TeacherService.class);
		int i = 0;
		int timeoutTimes = 0;
		long s = System.currentTimeMillis();
		try {
			for (; i < 100; i++) {
				try {
					service.save(t);
				} catch (RpcTimeoutException e) {
					timeoutTimes++;
				}
			}
		} finally {
			long cost = System.currentTimeMillis() - s;
			if (i > 0) {
				System.out.println("--------- total times : " + i + ", timeout times : " + timeoutTimes
						+ "  total cost : " + cost + ", average cost : " + (cost / (i * 1.0)));
			}
		}

		while (true) {
			Thread.sleep(1000L);
			try {
				Clients.getRpcProxy(TeacherService.class).save(t);
			} catch (Exception e) {
				System.err.println("ERR : " + e.getMessage());
			}
		}
	}
}
