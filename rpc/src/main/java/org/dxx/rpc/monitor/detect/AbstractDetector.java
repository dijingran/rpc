/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: transaction 
 * Author: dixingxing
 * Createdate: 下午5:19:26
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.detect;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接探测器抽象类
 * 
 * @project transaction
 * @author dixingxing
 * @version 1.0
 * @date 2013-4-3 下午5:19:26   
 */
public abstract class AbstractDetector implements Detector {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDetector.class);
	public static final double MILLION = 1000000.0;
	protected Exception ex;

	protected String exStack;

	protected long cost;

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-21 下午4:24:32
	 * @see com.hc360.guard.domain.detect.Detector#detect()
	 */
	@Override
	public void detect() throws Exception {
		long s = System.nanoTime();
		try {
			LOG.debug("执行探测 : " + this);
			doDetect();
		} catch (Exception e) {
			ex = e;
			exStack = extractStack(e);
		} finally {
			cost = System.nanoTime() - s;
		}

	}

	/**
	 * 获取异常堆栈
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午7:30:21
	 * @param e
	 * @return
	 */
	private static String extractStack(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String s = sw.toString();

			s = s.replaceAll("\\n\\t", "<br/>");
			s = s.replaceAll("\\r\\n", "<br/>");
			return s;
		} catch (Exception e2) {
			LOG.error(e2.getMessage(), e2);
			return "";
		}
	}

	/**
	 * 子类实现此方法，如果此方法抛出异常则说明探测失败
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-21 下午4:40:32
	 * @throws Exception
	 */
	protected abstract void doDetect() throws Exception;

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午5:19:56
	 * @see com.hc360.transaction.common.detect.Detector#getExStack()
	 */
	@Override
	public String getExStack() {
		return exStack;
	}

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-3 下午5:31:28
	 * @see com.hc360.transaction.common.detect.Detector#isFailed()
	 */
	@Override
	public boolean isFailed() {
		return ex != null;
	}

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-5 下午6:19:05
	 * @see com.hc360.transaction.common.detect.Detector#getCost()
	 */
	@Override
	public String getCost() {
		return formatCost(cost);
	}

	/**
	 * 格式化为毫秒，保留四位小数
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-5 下午6:57:46
	 * @param cost 纳秒数
	 * @return
	 */
	public static String formatCost(double cost) {
		return String.format("%1$,.4f ms", cost / MILLION);
	}

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-7 下午2:21:23
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " : " + name();
	}

}
