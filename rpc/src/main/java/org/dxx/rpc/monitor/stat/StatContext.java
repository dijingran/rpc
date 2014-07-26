/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: guard 
 * Author: dixingxing
 * Createdate: 下午9:59:00
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.stat;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 统计各功能点执行的健康状况，包括调用次数，异常次数，最快执行时间， 最慢执行时间，平均执行时间以及执行时间的分布情况。参见方法 ：
 * {@link #trace(Throwable, String, String, long)}
 * </p>
 * <p>
 * 目前统计数据仅存放在内存中，应用重启即丢失。
 * </p>
 * 
 * <p>
 * <strong>使用方法 ：</strong>
 * <ol>
 * <li>
 * 增加依赖 :
 * 
 * <pre>
 * &lt;dependency&gt;
 * 	&lt;groupId&gt;mmt&lt;/groupId&gt;
 * 	&lt;artifactId&gt;guard&lt;/artifactId&gt;
 * 	&lt;version&gt;1.0.0-SNAPSHOT&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 * 
 * </li>
 * <li>
 * 配置servlet (请注意&lt;servlet-mapping&gt; 需要配置在所有&lt;servlet&gt;后面)：
 * 
 * <pre>
 * &lt;!-- 配置guard监控的servlet --&gt;
 * 	&lt;servlet&gt;
 * 		&lt;servlet-name&gt;guard&lt;/servlet-name&gt;
 * 		&lt;servlet-class&gt;com.hc360.guard.mvc.servlet.DispatcherServlet&lt;/servlet-class&gt;
 * 		&lt;!-- 配置此值可以记录应用的启动时间，数字值不和其他servlet冲突即可。 --&gt;
 * 		&lt;load-on-startup&gt;2&lt;/load-on-startup&gt;
 * 		
 * 		&lt;!-- 配置管理中心的系统名称，有两个作用： --&gt;
 * 		&lt;!-- 1：作为网页中的系统名称进行展示 --&gt;
 * 		&lt;!-- 2：从配置管理中心对应的系统取guard.xml配置文件，目前仅允许在guard.xml中指定的用户登录 --&gt;
 * 		&lt;init-param&gt;
 * 			&lt;param-name&gt;projectName&lt;/param-name&gt;
 * 			&lt;param-value&gt;tchecker-repairer&lt;/param-value&gt;
 * 		&lt;/init-param&gt;
 * 		
 * 		&lt;!-- 会自动扫描此包下面一些接口的实现类 ，多个包名用","分隔--&gt;
 * 		&lt;!-- （如：com.hc360.guard.domain.detect.Detector接口，com.hc360.guard.domain.monitor.Monitor接口） --&gt;
 * 		&lt;init-param&gt;
 * 			&lt;param-name&gt;scanPackages&lt;/param-name&gt;
 * 			&lt;param-value&gt;com.hc360.tchecker.repairer.domain&lt;/param-value&gt;
 * 		&lt;/init-param&gt;
 * 	&lt;/servlet&gt;
 * 
 * 
 * &lt;servlet-mapping&gt;
 * 		&lt;servlet-name&gt;guard&lt;/servlet-name&gt;
 * 		&lt;url-pattern&gt;*.do&lt;/url-pattern&gt;
 * 	&lt;/servlet-mapping&gt;
 * </pre>
 * 
 * </li>
 * 
 * <li>
 * 与spring集成（{@link StatAspect} ）：
 * 
 * <pre>
 * &lt;bean id="statAspectBean" class="com.hc360.guard.domain.stat.StatAspect"&gt;&lt;/bean&gt;
 * 
 * &lt;aop:config&gt;
 * 	&lt;aop:aspect id="statAspect" ref="statAspectBean"&gt;
 * 		&lt;!-- 配置想要统计的包名 --&gt;
 * 		&lt;aop:pointcut id="statPointcut"
 * 			expression="execution(* com.hc360.rsf.tchecker..*.*(..))" /&gt;
 * 		&lt;aop:around method="around" pointcut-ref="statPointcut" /&gt;
 * 	&lt;/aop:aspect&gt;
 * &lt;/aop:config&gt;
 * </pre>
 * 
 * </li>
 * 
 * <li>
 * 不使用spring aop方式，直接调用 {@link #trace(Throwable, String, String, long)}：
 * 
 * <pre>
 * long start = System.currentTimeMillis();// 注意必须取系统时间的毫秒数
 * try {
 *     ... some code
 *     StatContext.trace(null, SHORT_DESC, LONG_DESC, start);
 * } catch (Exception e) {
 *     StatContext.trace(e, SHORT_DESC, LONG_DESC, start);
 *     ... some code when exception occurred
 * }
 * </pre>
 * 
 * </li>
 * 
 * <li>
 * <p>
 * 访问地址 http://ip:port/发布的应用名/stat.do
 * ，如：http://192.168.10.10:9080/transaction/stat.do
 * </p>
 * 
 * </li>
 * 
 * <li>
 * 系统默认会读取配置管理中心guard系统下的guard.xml文件，仅此文件中指定的人才可以登录监控系统。<br>
 * 若想增加使用人员，可以自行编写guard.xml并上传至相应的业务系统(对应servlet中指定的projectName)如：
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
 * 	&lt;userList&gt;
 * 		&lt;user name="user1" password="123456" /&gt;
 * 		&lt;user name="user2" password="123456" /&gt;
 * 	&lt;/userList&gt;
 * &lt;/config&gt;
 * </pre>
 * 
 * </li>
 * 
 * 
 * </ol>
 * </p>
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-8-21 下午9:59:00
 */
public final class StatContext {
	public static final Map<String, StatTarget> map = new HashMap<String, StatTarget>();

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
