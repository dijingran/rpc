package org.dxx.rpc.server;

import java.util.Random;

import org.dxx.rpc.config.annotation.RpcService;
import org.dxx.rpc.share.UserService;

@RpcService("用户服务类")
public class UserServiceImpl implements UserService{

	@Override
	public int add(String name) {
		try {
			Thread.sleep(300L);
		} catch (InterruptedException e) {
		}
		return new Random(1000).nextInt();
	}

}
