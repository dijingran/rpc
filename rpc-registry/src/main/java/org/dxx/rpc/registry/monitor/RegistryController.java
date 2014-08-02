/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
			list(model, HttpUtils.getParam("v", qs));
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
	 * 测试数据
	 *
	 * @throws Exception
	 */
	void initTest() throws Exception {
		ConcurrentHashMap<String, List<Service>> all = new ConcurrentHashMap<String, List<Service>>();
		Field f = ServiceRepository.class.getDeclaredField("services");
		f.setAccessible(true);
		f.set(ServiceRepository.getInstance(), all);
		f.setAccessible(false);

		Service s1 = new Service();
		s1.setApp("app1");
		s1.setInterfaceClass("Service1");

		Service s2 = new Service();
		s2.setApp("app1");
		s2.setInterfaceClass("Service2");
		Service s3 = new Service();

		s3.setApp("app2");
		s3.setInterfaceClass("Service3");

		for (int i = 0; i < 3; i++) {
			all.put("192.168.1." + (i + 1), toList(s1, s2));
		}
		all.put("192.168.1.252", toList(s3));
	}

	private List<Service> toList(Service... services) {
		List<Service> list = new ArrayList<Service>();
		for (Service s : services) {
			list.add(s);
		}
		return list;
	}

	/**
	 * 显示服务列表
	 * @param model
	 * @param v 显示表格类型  2表示简洁视图
	 */
	void list(Map<String, Object> model, String v) {
		List<App> apps = new ArrayList<App>();
		List<Server> servers = new ArrayList<Server>();
		List<ViewService> services = new ArrayList<ViewService>();
		//		try {
		//			initTest();
		//		} catch (Exception e1) {
		//			e1.printStackTrace();
		//		}
		Map<String, List<Service>> all = repository.getServices();
		for (Entry<String, List<Service>> e : all.entrySet()) {
			String url = e.getKey();
			List<Service> list = e.getValue();
			if (list.isEmpty()) {
				continue;
			}

			Server server = new Server(url);
			int i = servers.indexOf(server);
			if (i >= 0) {
				server = servers.get(i);
			} else {
				servers.add(server);
			}

			App app = new App(list.get(0).getApp());
			int j = apps.indexOf(app);
			if (j >= 0) {
				app = apps.get(j);
			} else {
				apps.add(app);
			}
			app.getServers().add(server);

			for (Service s : list) {
				boolean pause = ServiceRepository.isPaused(url, s.getInterfaceClass());
				ViewService vs = new ViewService(s.getDesc(), s.getInterfaceClass(), pause);

				server.addService(vs);
				if ("2".equals(v)) {
					app.addService2(vs);
					if (!services.contains(vs)) {
						services.add(vs);
					}
				} else {
					app.addService(vs);
					services.add(vs);
				}
			}
		}

		Collections.sort(services);
		model.put("services", services);
		model.put("v", v);

		model.put("projectName", "服务注册中心");
	}
}
