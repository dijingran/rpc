package org.dxx.rpc.registry;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RegisterResponse implements Serializable {
	private boolean success;

	private String errorMessage;

	public RegisterResponse() {
		this.success = true;
	}

	public RegisterResponse(String errorMessage) {
		super();
		this.success = false;
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return "RegisterResponse [success=" + success + ", errorMessage="
				+ errorMessage + "]";
	}

}
