package org.dxx.rpc;

import org.dxx.rpc.exception.RpcException;

@SuppressWarnings("serial")
public class Response extends AbstractResponse {

	private Object obj;

	private RpcException error;

	public RpcException getError() {
		return error;
	}

	public void setError(RpcException error) {
		this.error = error;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", obj=" + obj + "]";
	}

}
