package org.dxx.rpc.server;

import org.dxx.rpc.config.annotation.RpcService;
import org.dxx.rpc.share.HelloService;

@RpcService
public class HelloServiceImpl implements HelloService {
	@Override
	public String sayHello(String name) {
		return "Hello " + name + " !";
	}
}
