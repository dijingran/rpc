/**
 * ServerController.java
 * org.dxx.rpc.registry.monitor
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dxx.rpc.monitor.HttpUtils;
import org.dxx.rpc.monitor.MonitorRequest.MonitorType;
import org.dxx.rpc.monitor.MonitorResponse;
import org.dxx.rpc.monitor.ServerStatus;
import org.dxx.rpc.monitor.stat.StatComparator;
import org.dxx.rpc.monitor.stat.StatTarget;
import org.dxx.rpc.registry.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 服务端监控页面
 * 
 * @author   dixingxing
 * @Date	 2014年8月3日
 */
public class ServerController {
	private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

	public String serverStatus(QueryStringDecoder qs, Map<String, Object> model) {
		String url = HttpUtils.getParam("server", qs);
		model.put("server", url);
		model.put("sortBy", HttpUtils.getParam("sortBy", qs) != null ? HttpUtils.getParam("sortBy", qs) : "invokeTimes");
		model.put("orderBy", HttpUtils.getParam("orderBy", qs) != null ? HttpUtils.getParam("orderBy", qs) : "des");
		Channel c = Channels.getByUrl(url);
		if (c != null) {
			Invoker i = new Invoker(c);
			MonitorResponse response = i.invoke(MonitorType.serverStatus);
			ServerStatus ss = (ServerStatus) response.getObject();
			model.put("startTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(ss.getStartTime())));
			model.put("ss", ss);
			model.put("app", ss.getApp());

			Map<String, StatTarget> statMap = ss.getStatMap();
			List<StatTarget> statTargets = new ArrayList<StatTarget>();
			statTargets.addAll(statMap.values());
			Collections.sort(statTargets, new StatComparator("des", "invokeTimes"));
			for (StatTarget st : statTargets) {
				st.chooseMajor();
			}
			model.put("list", statTargets);

		} else {
			logger.error("Can't find channel for url : {}", url);
		}
		return "vm/server.html";
	}
}
