package org.dxx.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.dxx.rpc.EchoService;
import org.dxx.rpc.Request;
import org.dxx.rpc.ResponseFuture;
import org.dxx.rpc.common.TraceUtils;
import org.dxx.rpc.config.RpcClientConfig;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFactory implements InvocationHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

	private static final AtomicLong sequence = new AtomicLong(0);

	private Class<?> inter;

	private int timeout;

	private ProxyFactory(Class<?> interfaceClass, int timeout) {
		super();
		this.inter = interfaceClass;
		this.timeout = timeout;
	}

	public static Object get(Class<?> interfaceClass, RpcClientConfig rpcClientConfig) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass,
				EchoService.class }, new ProxyFactory(interfaceClass, rpcClientConfig.getTimeout()));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		long start = System.nanoTime();
		if (ignoreMethod(method)) {
			if ("toString".equals(method.getName())) {
				return "RPC_" + proxy.getClass().toString();
			}
			throw new RpcException("Method not implement : " + method);
		}

		if (!EchoService.ECHO_METHOD_NAME.equals(method.getName())) {
			if (MockUtils.isMock(inter)) {
				return MockUtils.invokeMockClass(inter, args, method.getName(), method.getParameterTypes());
			}
		}

		Request r = new Request();
		r.setId(sequence.incrementAndGet());
		r.setInterfaceClass(inter);
		r.setTimeout(timeout);
		r.setMethodName(method.getName());
		r.setArgTypes(method.getParameterTypes());
		r.setArgs(args);

		ResponseFuture f = new ResponseFuture(r);
		try {
			ChannelContext.getChannel(inter).writeAndFlush(r);
		} catch (Throwable e) {
			f.release();
			throw e;
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Send request cost {} ms : {}", TraceUtils.diff(start), r);
		}
		return f.get().getObj();
	}

	private static final List<String> ignoreMethods = new ArrayList<String>();

	static {
		ignoreMethods.add("toString");
		ignoreMethods.add("hashCode");
		ignoreMethods.add("equals");
		ignoreMethods.add("wait");
		ignoreMethods.add("notify");
		ignoreMethods.add("notifyAll");
	}

	private static boolean ignoreMethod(Method m) {
		return ignoreMethods.contains(m.getName());
	}
}
