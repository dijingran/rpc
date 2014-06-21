package org.dxx.rpc.registry;

@SuppressWarnings("serial")
public class RegistryException extends RuntimeException {

	public RegistryException() {
		super();
	}

	public RegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegistryException(String message) {
		super(message);
	}

	public RegistryException(Throwable cause) {
		super(cause);
	}

}
