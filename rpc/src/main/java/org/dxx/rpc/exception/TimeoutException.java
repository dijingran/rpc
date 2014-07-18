package org.dxx.rpc.exception;

@SuppressWarnings("serial")
public class TimeoutException extends RpcException {

	public TimeoutException(long timeout, String message) {
		super("Time out (" + timeout + ") ms -> " + message);
	}

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}

}
