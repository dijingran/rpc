package org.dxx.rpc.registry;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.config.Registry;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryUtils {
	private static final Logger logger = LoggerFactory.getLogger(RegistryUtils.class);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private static AtomicLong sequence = new AtomicLong(0);
	private static Channel registyChannel;

	private static Map<Long, CountDownLatch> countDownLatches = new ConcurrentHashMap<Long, CountDownLatch>();

	private static Map<Long, GetServerLocationResponse> locationMap = new ConcurrentHashMap<Long, GetServerLocationResponse>();

	public static boolean isRegistryInitialized() {
		return registyChannel != null;
	}

	public static void receiveGetServerLocationResponse(GetServerLocationResponse response) {
		logger.debug("Receive GetServerLocationResponse : {}", response);
		CountDownLatch latch = countDownLatches.get(response.getId());
		if (latch != null) {
			locationMap.put(response.getId(), response);
			latch.countDown();
		}
	}

	public static void setRegistyChannel(Channel registyChannel) {
		RegistryUtils.registyChannel = registyChannel;
	}

	public static void removeRegistryChannel() {
		RegistryUtils.registyChannel = null;
	}

	/**
	 * 调用注册中心，并返回所指定接口的服务端地址
	 *
	 * @param interfaceClass
	 * @return
	 */
	public static GetServerLocationResponse getServerLocation(String interfaceClass, String deactiveUrl) {
		if (registyChannel == null) {
			throw new RegistryException("尚未连接到注册中心！");
		}
		GetServerLocationRequest request = new GetServerLocationRequest();
		request.setId(sequence.incrementAndGet());
		request.setInterfaceClass(interfaceClass);
		request.setDeactiveUrl(deactiveUrl);

		logger.debug("Locate urls for interface {} : {}", interfaceClass, request);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		countDownLatches.put(request.getId(), countDownLatch);

		try {
			registyChannel.writeAndFlush(request);
			countDownLatch.await(RpcConstants.LOCATE_TIME_OUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			logger.warn(e1.getMessage(), e1);
		} finally {
			countDownLatches.remove(request.getId());
		}

		GetServerLocationResponse response = locationMap.get(request.getId());
		if (response == null) {
			throw new RegistryException("Invoke Registry for server location timeout : " + RpcConstants.LOCATE_TIME_OUT);
		}
		if (!response.isSuccess()) {
			throw new RegistryException("Get server location error : " + response.getErrorMessage());
		}
		logger.debug("Get server location success : {}", response);
		return response;
	}

	public static Class<?> getInter(String interfaceClass) {
		try {
			return Class.forName(interfaceClass);
		} catch (ClassNotFoundException e) {
			throw new RpcException(e.getMessage(), e);
		}
	}

	public static void scheduleRegistry() {
		scheduler.schedule(new RegistryStartup(), RpcConstants.REGISTRY_RETRY_TIME, TimeUnit.MILLISECONDS);
	}

	public static void createRegistryChannel() {
		Registry registry = Loader.getRpcConfig().getRegistry();
		if (registry == null) {
			logger.debug("<registry../> is not configured !");
			return;
		}
		new RegistryStartup().startup();
	}

}
