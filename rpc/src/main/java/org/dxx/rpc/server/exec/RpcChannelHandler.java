package org.dxx.rpc.server.exec;

import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.dxx.rpc.Request;

public class RpcChannelHandler {
	NamedThreadFactory threadFactory = new NamedThreadFactory("rpc-exec-");

	ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return threadFactory.newThread(r);
		}
	});

	public void handle(Channel channel, Request request) {
		executorService.execute(new RpcRunnable(channel, request));
	}

}
