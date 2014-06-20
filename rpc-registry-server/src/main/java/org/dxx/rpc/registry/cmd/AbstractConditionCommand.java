/**
 * AbstractConditionCommand.java
 * org.dxx.rpc.registry.cmd
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-17
 */

public abstract class AbstractConditionCommand extends AbstractCommand {
	private static Pattern uPattern = Pattern.compile("\\s+[-]u\\s+(.+?)(?=\\s|$)");
	private static Pattern nPattern = Pattern.compile("\\s+[-]n\\s+(.+?)(?=\\s|$)");
	private static Pattern pPattern = Pattern.compile("\\s+[-]p(?=\\s|$)");

	protected String url;
	protected String name;
	protected boolean pause;// true：只查被暂停的， false：只查可用的

	public AbstractConditionCommand(String cmd) {
		super(cmd);
		Matcher m = uPattern.matcher(cmd);
		if (m.find()) {
			this.url = m.group(1).toLowerCase();
		}

		m = nPattern.matcher(cmd);
		if (m.find()) {
			this.name = m.group(1).toLowerCase();
		}

		m = pPattern.matcher(cmd);
		if (m.find()) {
			this.pause = true;
		}
	}
}
