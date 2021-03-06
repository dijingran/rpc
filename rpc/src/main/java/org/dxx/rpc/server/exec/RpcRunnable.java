package org.dxx.rpc.server.exec;

import io.netty.channel.Channel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.dxx.rpc.EchoService;
import org.dxx.rpc.Request;
import org.dxx.rpc.Response;
import org.dxx.rpc.common.TraceUtils;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.monitor.stat.StatContext;
import org.dxx.rpc.server.Servers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRunnable implements Runnable {
	Logger logger = LoggerFactory.getLogger(RpcRunnable.class);

	private Channel channel;

	private Request request;

	public RpcRunnable(Channel channel, Request request) {
		super();
		this.channel = channel;
		this.request = request;
	}

	@Override
	public void run() {
		logger.trace("Receive : {}", request);
		long s2 = System.currentTimeMillis();
		Response r = new Response();
		r.setId(request.getId());

		if (EchoService.ECHO_METHOD_NAME.equals(request.getMethodName())) {
			r.setObj(request.getArgs()[0]);
			channel.writeAndFlush(r);
			return;
		}

		Method m = null;
		try {
			Object service = Servers.getRpcService(request.getInterfaceClass());
			if (service == null) {
				r.setError(new RpcException("Service not found : " + request.getInterfaceClass() + " > "
						+ channel.toString()));
				logger.warn("Service not found : {}", request.getInterfaceClass());
			} else {
				m = service.getClass().getMethod(request.getMethodName(), request.getArgTypes());
				long start = System.nanoTime();
				r.setObj(m.invoke(service, request.getArgs()));

				if (logger.isTraceEnabled()) {
					logger.trace("Invoke method cost {} ms : {}", TraceUtils.diff(start), m);
				}
			}

		} catch (Throwable e) {
			logger.warn(e.getMessage(), e);
			r.setError(new RpcException("Remote Ex ->" + channel.toString() + " : " + getStackTrace(e)));
			StatContext.trace(e, m, s2);
		} finally {
			StatContext.trace(null, m, s2);
			channel.writeAndFlush(r);
			logger.trace("Wrote response.");
		}
	}

	private static String getStackTrace(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.getBuffer().toString();
	}
}
