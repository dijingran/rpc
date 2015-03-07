package org.dxx.rpc.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Velocity 工具类
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-4-5 下午4:25:40
 */
public class VelocityUtils {

	private static boolean cacheView = false;

	/**
	 * 渲染内容.
	 * 
	 * @param templateName
	 *            模板名称.
	 * @param model
	 *            变量Map.
	 */
	public static String renderFile(String templateName, Map<String, ?> model) {
		try {
			StringWriter result = new StringWriter();
			Velocity.evaluate(new VelocityContext(model), result, "", getClasspathTemplate(templateName).toString());
			return result.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException("Parse template failed : " + templateName, e);
		}
	}

	static Pattern p = Pattern.compile("[#]parse\\(\"(.*?)\"\\)");

	/**
	 * 从classpath下读取模板文件
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-26 下午4:36:18
	 * @param templateName
	 * @return
	 * @throws IOException
	 */
	static StringBuffer getClasspathTemplate(String templateName) throws IOException {
		InputStream in = null;
		if (cacheView) {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateName);
		} else {
			URL res = Thread.currentThread().getContextClassLoader().getResource(templateName);
			if (res != null) {
				URLConnection resConn = res.openConnection();
				resConn.setUseCaches(false);
				in = resConn.getInputStream();
			}
		}

		if (in == null) {
			throw new IllegalArgumentException("Parse template failed , can not find file : " + templateName);
		}

		return evalParse(toStringBuffer(in));
	}

	/**
	 * 解析 parse标签
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-26 下午5:31:39
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	private static StringBuffer evalParse(StringBuffer buffer) throws IOException {
		StringBuffer sb = new StringBuffer();
		Matcher m = p.matcher(buffer);
		if (m.find()) {
			sb.append(buffer.subSequence(0, m.start()));
			sb.append(getClasspathTemplate(m.group(1)));
			sb.append(buffer.subSequence(m.end(), buffer.length()));
			if (m.find()) {
				sb = evalParse(sb);
			}
		} else {
			sb = buffer;
		}
		return sb;
	}

	/**
	 * {@link InputStream} -> {@link StringBuffer}
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-27 下午2:57:43
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static StringBuffer toStringBuffer(InputStream is) throws IOException {
		StringWriter output = new StringWriter();
		InputStreamReader input = new InputStreamReader(is);
		char buffer[] = new char[4096];
		for (int n; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
		}
		return output.getBuffer();
	}

}
