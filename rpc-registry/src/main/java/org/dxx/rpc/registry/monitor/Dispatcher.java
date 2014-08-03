/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dxx.rpc.monitor.Controller;
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
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class Dispatcher implements Controller {
	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	ServiceController serviceController = new ServiceController();

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		model.put("layout", "vm/layout/registry-layout.html");

		QueryStringDecoder qs = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = qs.parameters();
		if (params.containsKey("op")) {
			return serviceController.opState(request);
		} else if (params.containsKey("server")) {
			return serverStatus(qs, model);
		} else {
			return serviceController.list(model, HttpUtils.getParam("v", qs));
		}
	}

	private String serverStatus(QueryStringDecoder qs, Map<String, Object> model) {
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
