package org.dxx.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

import org.dxx.rpc.Request;
import org.dxx.rpc.ResponseFuture;
import org.dxx.rpc.config.RpcClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFactory implements InvocationHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProxyFactory.class);

	private static final AtomicLong idSeq = new AtomicLong(0);

	private Class<?> interfaceClass;

	private RpcClientConfig rpcClientConfig;

	private ProxyFactory(Class<?> interfaceClass, RpcClientConfig rpcClientConfig) {
		super();
		this.interfaceClass = interfaceClass;
		this.rpcClientConfig = rpcClientConfig;
	}

	public static Object get(Class<?> interfaceClass, RpcClientConfig rpcClientConfig) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new ProxyFactory(interfaceClass, rpcClientConfig));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Request r = new Request();
		r.setId(idSeq.incrementAndGet());
		r.setInterfaceClass(interfaceClass);
		r.setRpcClientConfig(rpcClientConfig);
		r.setMethodName(method.getName());
		r.setArgTypes(method.getParameterTypes());
		r.setArgs(args);
		r.setRpcClientConfig(rpcClientConfig);

		logger.debug("send : {}", r);

		ResponseFuture f = new ResponseFuture(r);
		try {
			ChannelContext.getChannel(interfaceClass).writeAndFlush(r);
		} catch (Throwable e) {
			f.release();
			throw e;
		}
		return f.get().getObj();
	}

}
