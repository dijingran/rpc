package org.dxx.rpc;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Response implements Serializable {
	private long id;

	private Object obj;
	
	private Throwable error;
	
	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
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
