package org.dxx.rpc.monitor.stat;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 统计各功能点执行的健康状况，包括调用次数，异常次数，最快执行时间， 最慢执行时间，平均执行时间以及执行时间的分布情况。参见方法 ：
 * {@link #trace(Throwable, String, String, long)}
 * </p>
 * <p>
 * 目前统计数据仅存放在内存中，应用重启即丢失。
 * </p>
 */
public final class StatContext {
	public static final ConcurrentHashMap<String, StatTarget> map = new ConcurrentHashMap<String, StatTarget>();

	public static final int MAX_DESC_LENGTH = 70;

	private StatContext() {
	}

	/**
	 * 统计功能点的执行情况
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-21 下午9:54:28
	 * @param t
	 *            此值为空时，仅调用次数+1；此值不为空时，调用次数和异常次数都+1
	 * @param shortDesc
	 *            仅供展示
	 * @param longDesc
	 *            使用此值为key，将调用信息放入内存中 （请保证其唯一性，一般可以使用"类名.方法名" ）
	 * @param s
	 *            开始执行的时间(毫秒数, System.currentTimeMillis())
	 */
	public static void trace(Throwable t, String shortDesc, String longDesc, long s) {
		if (shortDesc == null || longDesc == null) {
			return;
		}

		String desc = (shortDesc.length() > MAX_DESC_LENGTH ? shortDesc.substring(shortDesc.length() - MAX_DESC_LENGTH)
				: shortDesc);
		StatTarget st = map.get(longDesc);
		if (st == null) {
			st = new StatTarget();
			st.setDesc(desc);
			st.setLongDesc(longDesc);
			map.put(longDesc, st);
		}
		st.addTotalCost(s, t != null);
	}

	/**
	 * 
	 * 统计功能点的执行情况
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-11-20 下午5:46:37
	 * @param t 此值为空时，仅调用次数+1；此值不为空时，调用次数和异常次数都+1
	 * @param longDesc 使用此值为key，将调用信息放入内存中 （请保证其唯一性，一般可以使用"类名.方法名" ）
	 * @param s 开始执行的时间(毫秒数, System.currentTimeMillis())
	 * 
	 * @see StatContext#trace(Throwable, String, String, long)
	 */
	public static void trace(Throwable t, String longDesc, long s) {
		trace(t, longDesc, longDesc, s);
	}

	public static void trace(Throwable t, Method m, long s) {
		if (m != null) {
			String shortDesc = m.getDeclaringClass().getSimpleName() + "." + m.getName()
					+ (m.getParameterTypes().length > 0 ? "(..)" : "()");
			trace(t, shortDesc, m.toString(), s);
		}
	}

	/**
	 * 获取所有统计数据
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-29 下午6:14:45
	 * @return
	 */
	public static Map<String, StatTarget> getMap() {
		return map;
	}

}
