package org.dxx.rpc.registry;

import io.netty.channel.Channel;

import java.util.List;
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

	private static LocateRpcServerResponse serverLocations;

	private static CountDownLatch countDownLatch;

	public static boolean isRegistryInitialized() {
		return registyChannel != null;
	}

	public static void receiveLocateServerResponse(LocateRpcServerResponse response) {
		serverLocations = response;
		countDownLatch.countDown();

		if (response.getId() == 0L) {
			logger.debug("Update server locations : {}", response);
		} else {
			if (response.isSuccess()) {
				logger.debug("Get server locations success : {}", response);
			} else {
				logger.error("Get server locations failed : {}", response.getErrorMessage());
			}
		}
	}

	public static void setRegistyChannel(Channel registyChannel) {
		RegistryUtils.registyChannel = registyChannel;
	}

	public static void removeRegistryChannel() {
		RegistryUtils.registyChannel = null;
	}

	/**
	 * 调用注册中心，并返回所有服务端地址
	 *
	 * @param intersWithoutUrl
	 * @return
	 */
	public static LocateRpcServerResponse locateServer(List<String> intersWithoutUrl) {
		if (serverLocations == null) {
			serverLocations = locateServerFromRegistry(intersWithoutUrl);
		}
		return serverLocations;
	}

	private synchronized static LocateRpcServerResponse locateServerFromRegistry(List<String> intersWithoutUrl) {
		if (registyChannel == null) {
			return null; // TODO 重连或者抛异常?
		}
		logger.debug("Locate urls for interfaces : {}", intersWithoutUrl);
		LocateRpcServerRequest request = new LocateRpcServerRequest();
		request.setId(sequence.incrementAndGet());
		request.setInterfaceClasses(intersWithoutUrl);
		registyChannel.writeAndFlush(request);
		countDownLatch = new CountDownLatch(1);
		try {
			countDownLatch.await(RpcConstants.LOCATE_TIME_OUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			logger.warn(e1.getMessage(), e1);
		}

		if (serverLocations == null) {
			logger.error("调用注册中心获取server地址超时 ： " + RpcConstants.LOCATE_TIME_OUT);
		}
		return serverLocations;
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

	/**
	 * 创建注册中心的channel
	 */
	public static void createRegistryChannelSync() {
		Registry registry = Loader.getRpcConfig().getRegistry();
		if (registry == null) {
			logger.debug("<registry../> is not configured !");
			return;
		}
		new RegistryStartup().startup();
	}

}
