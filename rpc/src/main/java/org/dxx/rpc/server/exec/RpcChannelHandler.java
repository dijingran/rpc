package org.dxx.rpc.server.exec;

import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dxx.rpc.Request;

public class RpcChannelHandler {
	private static final ThreadFactory tf = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new NamedThreadFactory("rpc-exec-").newThread(r);
		}
	};

	private static final ExecutorService executorService = new ThreadPoolExecutor(0, 80, 60, TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>(), tf);

	public void handle(Channel channel, Request request) {
		executorService.execute(new RpcRunnable(channel, request));
	}

	public static ExecutorService getExecutorservice() {
		return executorService;
	}

}
