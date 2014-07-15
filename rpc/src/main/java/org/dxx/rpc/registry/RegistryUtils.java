package org.dxx.rpc.registry;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.dxx.rpc.RpcConstants;
import org.dxx.rpc.config.Registry;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.server.Servers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryUtils {
	private static final Logger logger = LoggerFactory.getLogger(RegistryUtils.class);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final ExecutorService registryExecutorService = Executors.newFixedThreadPool(1);

	private static AtomicLong sequence = new AtomicLong(0);
	private static Channel registryChannel;

	private static Map<Long, CountDownLatch> countDownLatches = new ConcurrentHashMap<Long, CountDownLatch>();

	private static Map<Long, GetServerLocationResponse> locationMap = new ConcurrentHashMap<Long, GetServerLocationResponse>();

	/**
	 * 和注册中心的连接创建完毕后，向其注册自己所提供的服务。
	*/
	public static void register(final Channel channel) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				Registry registry = Loader.getRpcConfig().getRegistry();
				logger.debug("Registering rpc services to Registry : {}:{}", registry.getHost(), registry.getPort());

				RegisterRequest request = new RegisterRequest();
				request.setPort(Loader.getRpcConfig().getRpcServerConfig().getPort());

				List<Service> services = new ArrayList<Service>();
				for (Class<?> interfaceClass : Servers.interAndImpl.keySet()) {
					Service s = new Service();
					s.setInterfaceClass(interfaceClass.getName());
					s.setDesc(Servers.getDef(interfaceClass).getDescription());
					services.add(s);
				}
				request.setServices(services);
				channel.writeAndFlush(request);
				logger.debug("RegisterRequest sent : {}", request);

			}
		};
		registryExecutorService.execute(r);

	}

	/**
	 * 调用注册中心，并返回所指定接口的服务端地址
	 *
	 * @param interfaceClass
	 * @return
	 */
	public static GetServerLocationResponse getServerLocation(String interfaceClass, String deactiveUrl) {
		if (registryChannel == null) {
			throw new RegistryException("尚未连接到注册中心！");
		}

		if (!isActive(registryChannel)) {
			removeRegistryChannel(registryChannel);
			logger.info("The registry channel is already deactive, close it and try create new channel.");
			createRegistryChannel();
		}

		if (registryChannel == null) {
			throw new RegistryException("无法连接到注册中心！");
		}

		GetServerLocationRequest request = new GetServerLocationRequest();
		request.setId(sequence.incrementAndGet());
		request.setInterfaceClass(interfaceClass);
		request.setDeactiveUrl(deactiveUrl);

		logger.debug("Locate urls for interface {} : {}", interfaceClass, request);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		countDownLatches.put(request.getId(), countDownLatch);

		try {
			registryChannel.writeAndFlush(request);
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

	public static void receiveGetServerLocationResponse(GetServerLocationResponse response) {
		logger.debug("Receive GetServerLocationResponse : {}", response);
		CountDownLatch latch = countDownLatches.get(response.getId());
		if (latch != null) {
			locationMap.put(response.getId(), response);
			latch.countDown();
		}
	}

	public static boolean isRegistryInitialized() {
		return registryChannel != null;
	}

	public static synchronized void setRegistyChannel(Channel registyChannel) {
		RegistryUtils.registryChannel = registyChannel;
		updateAccessTime();
	}

	public static synchronized void removeRegistryChannel(Channel c) {
		c.close();
		RegistryUtils.registryChannel = null;
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
	 * synchronized , avoid multi channel created !
	 */
	public static synchronized void createRegistryChannel() {
		Registry registry = Loader.getRpcConfig().getRegistry();
		if (registry == null) {
			logger.debug("<registry../> is not configured !");
			return;
		}
		if (registryChannel != null) {
			logger.warn("Channel already exists!");
			return;
		}
		new RegistryStartup().startup();
	}

	// =============== heartbeat S ===============

	private static Long accessTime = 0L;

	static synchronized void updateAccessTime() {
		accessTime = System.currentTimeMillis();
	}

	/**
	 * Channel是否可用（长时间{@link RpcConstants#INVALID_THRESHOLD}没有接受到数据，视为不可用）
	 *
	 * @param c
	 * @return
	 */
	private static boolean isActive(Channel c) {
		return (System.currentTimeMillis() - accessTime) <= RpcConstants.INVALID_THRESHOLD;
	}

	// =============== heartbeat E ===============

}
