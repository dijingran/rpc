package org.dxx.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dxx.rpc.client.ChannelContext;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.exception.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseFuture {
	static Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

	static final Map<Long, ResponseFuture> responses = new ConcurrentHashMap<Long, ResponseFuture>();

	private long begin;
	private Request request;
	private Response response;
	private int timeout = RpcConstants.DEFAULT_RESPONSE_TIMEOUT;
	private final Lock lock = new ReentrantLock();
	private final Condition done = lock.newCondition();

	public ResponseFuture(Request request) {
		super();
		begin = System.currentTimeMillis();
		this.request = request;
		if (request.getTimeout() >= 0) {
			this.timeout = request.getTimeout();
		}
		responses.put(request.getId(), this);
	}

	public static void receive(Response response) {
		ResponseFuture rf = responses.remove(response.getId());
		logger.trace("Receive response cost {} ms : {}", System.currentTimeMillis() - rf.begin, response);
		rf.doReceived(response);
	}

	private void doReceived(Response resp) {
		lock.lock();
		try {
			this.response = resp;
			done.signal();
		} finally {
			lock.unlock();
		}
	}

	private boolean isDone() {
		return response != null;
	}

	public Response get() {
		if (!isDone()) {
			lock.lock();
			try {
				if (!isDone()) {
					done.await(timeout, TimeUnit.MILLISECONDS);
				}
			} catch (InterruptedException e) {
				throw new RpcException(e);
			} finally {
				lock.unlock();
			}
			if (!isDone()) {
				throw timeoutException();
			}
		}
		return returnResponse();
	}

	private Response returnResponse() {
		if (response.getError() != null) {
			throw response.getError();
		}
		return response;
	}

	private TimeoutException timeoutException() {
		String s = ChannelContext.getOrCreateChannel(request.getInterfaceClass().getName()) + " : "
				+ request.getInterfaceClass().toString();
		return new TimeoutException(timeout, s);
	}

	public long getBegin() {
		return begin;
	}

	public Request getRequest() {
		return request;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * 清空此次请求，避免内存泄漏
	 * <p>
	*/
	public void release() {
		responses.remove(this.request.getId());

	}

}
