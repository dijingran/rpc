/**
 * MockUtils.java
 * org.dxx.rpc.client
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.client;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.dxx.rpc.config.RpcMockConfig;
import org.dxx.rpc.config.RpcMockConfigs;
import org.dxx.rpc.config.loader.Loader;
import org.dxx.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-25
 */
public class MockUtils {
	static final Logger logger = LoggerFactory.getLogger(MockUtils.class);

	/** interfaceName, mockObject */
	private static Map<String, Object> mocks = null;

	/**
	 * 
	 *
	 * @param interfaceClass
	 * @return
	 */
	static boolean isMock(Class<?> interfaceClass) {
		if (mocks == null) {
			mocks = new HashMap<String, Object>();
			RpcMockConfigs mockConfigs = Loader.getMockConfigs();
			for (RpcMockConfig c : mockConfigs.getMocks()) {
				Object target;
				try {
					target = Class.forName(c.getMockClass()).newInstance();
				} catch (Exception e) {
					throw new RpcException(e);
				}
				if (!interfaceClass.isAssignableFrom(target.getClass())) {
					throw new RpcException("mockClass " + c.getMockClass() + " not implement the interface : "
							+ c.getInterfaceClass());
				}
				mocks.put(c.getInterfaceClass(), target);
			}
		}
		return mocks.containsKey(interfaceClass.getName());
	}

	/**
	 *
	 * @param interfaceClass
	 * @param method
	 * @param args
	 * @return
	 */
	static Object invokeMockClass(Class<?> interfaceClass, Method method, Object[] args) {
		Object target = mocks.get(interfaceClass.getName());
		logger.warn("Invoking method [{}] with mock class : {}", method.getName(), target.getClass().getName());
		try {
			return method.invoke(target, args);
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
}
