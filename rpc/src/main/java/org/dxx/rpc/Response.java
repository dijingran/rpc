package org.dxx.rpc;

import java.io.Serializable;

import org.dxx.rpc.exception.RpcException;

@SuppressWarnings("serial")
public class Response implements Serializable {
	private long id;

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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", obj=" + obj + "]";
	}

}
