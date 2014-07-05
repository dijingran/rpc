package org.dxx.rpc;

import java.util.Arrays;

import org.dxx.rpc.config.RpcClientConfig;

@SuppressWarnings("serial")
public class Request extends AbstractRequest {

	private Class<?> interfaceClass;

	private String methodName;

	private Class<?>[] argTypes;

	private Object[] args;

	private RpcClientConfig rpcClientConfig;

	public RpcClientConfig getRpcClientConfig() {
		return rpcClientConfig;
	}

	public void setRpcClientConfig(RpcClientConfig rpcClientConfig) {
		this.rpcClientConfig = rpcClientConfig;
	}

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Class<?>[] argTypes) {
		this.argTypes = argTypes;
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", interfaceClass=" + interfaceClass + ", methodName=" + methodName
				+ ", argTypes=" + Arrays.toString(argTypes) + ", args=" + Arrays.toString(args) + "]";
	}

}
