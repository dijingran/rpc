package org.dxx.rpc.exception;

@SuppressWarnings("serial")
public class RpcTimeoutException extends RpcException {

	public RpcTimeoutException(long timeout, String message) {
		super("Time out (" + timeout + ") ms -> " + message);
	}

	public RpcTimeoutException() {
		super();
	}

	public RpcTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcTimeoutException(String message) {
		super(message);
	}

	public RpcTimeoutException(Throwable cause) {
		super(cause);
	}

}
