package org.dxx.rpc.server.exec;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {
	private AtomicLong num = new AtomicLong(1);
	private String name;

	public NamedThreadFactory(String name) {
		super();
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, name + num.getAndIncrement());
	}

}
