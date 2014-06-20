/**
 * App1.java
 * cn.vko.app1
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app1;

import org.dxx.rpc.RpcUtils;

/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */

public class App1 {
	public static void main(String[] args) {
		RpcUtils.startup();

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
