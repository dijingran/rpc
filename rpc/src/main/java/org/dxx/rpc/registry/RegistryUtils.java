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
import org.dxx.rpc.exception.RegistryException;
import org.dxx.rpc.exception.RpcException;
import org.dxx.rpc.server.Servers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryUtils {
	private static final Logger logger = LoggerFactory.getLogger(RegistryUtils.class);
	private static final ExecutorService registryExecutorService = Executors.newFixedThreadPool(1);
	private static final ScheduledExecutorService registrySchedule = Executors.newScheduledThreadPool(1);

	private static AtomicLong sequence = new AtomicLong(0);
	/** Registry channel */
	private static Channel channel;

	private static Map<Long, CountDownLatch> countDownLatches = new ConcurrentHashMap<Long, CountDownLatch>();

	private static Map<Long, GetServerResponse> locationMap = new ConcurrentHashMap<Long, GetServerResponse>();

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
				request.setApp(Loader.getRpcConfig().getRpcServerConfig().getApp());
				request.setPort(Loader.getRpcConfig().getRpcServerConfig().getPort());

				List<Service> services = new ArrayList<Service>();
				for (Class<?> interfaceClass : Servers.interAndImpl.keySet()) {
					Service s = new Service();
					s.setApp(request.getApp());
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
	public static GetServerResponse getServerLocation(String interfaceClass, String deactiveUrl) {
		if (channel == null) {
			throw new RegistryException("尚未连接到注册中心！");
		}

		if (!isActive(channel)) {
			removeChannel();
			logger.info("The registry channel is already deactive, close it and try create new channel.");
			createChannel();
		}

		if (channel == null) {
			throw new RegistryException("无法连接到注册中心！");
		}

		GetServerRequest request = new GetServerRequest();
		request.setId(sequence.incrementAndGet());
		request.setInterfaceClass(interfaceClass);
		request.setDeactiveUrl(deactiveUrl);

		logger.debug("Locate urls for interface {} : {}", interfaceClass, request);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		countDownLatches.put(request.getId(), countDownLatch);

		try {
			channel.writeAndFlush(request);
			countDownLatch.await(RpcConstants.LOCATE_TIME_OUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			logger.warn(e1.getMessage(), e1);
		} finally {
			countDownLatches.remove(request.getId());
		}

		GetServerResponse response = locationMap.get(request.getId());
		if (response == null) {
			throw new RegistryException("Invoke Registry for server location timeout : " + RpcConstants.LOCATE_TIME_OUT);
		}
		if (!response.isSuccess()) {
			logger.error("Get server location error : " + response.getErrorMessage());
		}
		logger.debug("Get server location success : {}", response);
		return response;
	}

	public static void receiveGetServerLocationResponse(GetServerResponse response) {
		logger.debug("Receive GetServerLocationResponse : {}", response);
		CountDownLatch latch = countDownLatches.get(response.getId());
		if (latch != null) {
			locationMap.put(response.getId(), response);
			latch.countDown();
		}
	}

	public static boolean isInitialized() {
		return channel != null;
	}

	public static boolean isChannelActive() {
		return channel != null && isActive(channel);
	}

	public static synchronized void setChannel(Channel registyChannel) {
		channel = registyChannel;
		updateAccessTime(registyChannel);
	}

	public static synchronized void removeChannel() {
		try {
			if (channel != null) {
				channel.close();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			channel = null;
		}
	}

	public static Class<?> getInter(String interfaceClass) {
		try {
			return Class.forName(interfaceClass);
		} catch (ClassNotFoundException e) {
			throw new RpcException(e.getMessage(), e);
		}
	}

	/**
	 * Synchronized avoid multi channels created !
	 */
	public static synchronized void createChannel() {
		Registry registry = Loader.getRpcConfig().getRegistry();
		if (registry == null) {
			logger.debug("<registry../> is not configured !");
			return;
		}
		new RegistryStartup().startup();
	}

	/**
	 * Create registry channel if channel not exists or channel is dead.
	 */
	public static void scheduleRegistry() {
		logger.debug("Schedule RegistryStartup instance.");
		registrySchedule.scheduleWithFixedDelay(new RegistryStartup(), 10, RpcConstants.REGISTRY_CHECK_TIME,
				TimeUnit.MILLISECONDS);
	}

	// =============== heartbeat S ===============

	static synchronized void updateAccessTime(Channel c) {
		c.attr(RpcConstants.ATTR_ACCESS_MILLS).set(System.currentTimeMillis());
	}

	/**
	 * Channel是否可用（长时间{@link RpcConstants#INVALID_THRESHOLD}没有接受到数据，视为不可用）
	 *
	 * @param c
	 * @return
	 */
	static boolean isActive(Channel c) {
		return (System.currentTimeMillis() - c.attr(RpcConstants.ATTR_ACCESS_MILLS).get()) <= RpcConstants.INVALID_THRESHOLD;
	}

	// =============== heartbeat E ===============

}
