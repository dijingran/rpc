/**
 * App1.java
 * cn.vko.app1
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app1;

import org.dxx.rpc.RpcUtils;

/**
 * 启动app1
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

public class App1Main {
	public static void main(String[] args) {
		RpcUtils.startupSync();

		//		while (true) {
		//			try {
		//				Thread.sleep(3000L);
		//				RpcUtils.get(GroupService.class).getGroups();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}

	}
}
