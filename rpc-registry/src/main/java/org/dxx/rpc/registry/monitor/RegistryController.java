/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dxx.rpc.monitor.Controller;
import org.dxx.rpc.monitor.HttpUtils;
import org.dxx.rpc.registry.Service;
import org.dxx.rpc.registry.ServiceRepository;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class RegistryController implements Controller {
	ServiceRepository repository = ServiceRepository.getInstance();

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		model.put("layout", "vm/layout/registry-layout.html");

		QueryStringDecoder qs = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = qs.parameters();
		if (params.containsKey("op")) {
			opState(request);
			return null;
		} else {
			list(model);
			return "vm/registry.html";
		}
	}

	/**
	 * op : pause, pauseAll, resume, resumeAll.
	 */
	private void opState(DefaultFullHttpRequest request) {
		QueryStringDecoder qs = new QueryStringDecoder(request.getUri());
		String op = HttpUtils.getParam("op", qs);
		String url = HttpUtils.getParam("url", qs);
		String name = HttpUtils.getParam("name", qs);

		// url , List<interfaceName>
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		List<String> inters = new ArrayList<String>();
		if (op.endsWith("All")) {
			List<Service> services = repository.getServices().get(url);
			for (Service s : services) {
				inters.add(s.getInterfaceClass());
			}
		} else {
			inters.add(name);
		}
		map.put(url, inters);

		if (op.startsWith("pause")) {
			repository.pause(map);
		} else {
			repository.resume(map);
		}

	}

	/**
	 * 显示服务列表
	 * @param model
	 */
	private void list(Map<String, Object> model) {
		List<Server> servers = new ArrayList<Server>();
		Map<String, List<Service>> all = repository.getServices();

		for (Entry<String, List<Service>> e : all.entrySet()) {
			String url = e.getKey();
			List<Service> list = e.getValue();

			Server server = new Server(url);
			int i = servers.indexOf(server);
			if (i >= 0) {
				server = servers.get(i);
			} else {
				servers.add(server);
			}

			for (Service s : list) {
				boolean pause = ServiceRepository.isPaused(url, s.getInterfaceClass());
				server.addService(new ViewService(s.getDesc(), s.getInterfaceClass(), pause));
			}
		}

		List<ViewService> services = new ArrayList<ViewService>();
		for (Server s : servers) {
			services.addAll(s.getServices());
		}

		model.put("services", services);

		model.put("projectName", "服务注册中心");
	}
}
