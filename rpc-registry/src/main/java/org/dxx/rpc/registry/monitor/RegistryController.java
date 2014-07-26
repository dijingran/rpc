/**
 * RegistryController.java
 * org.dxx.rpc.registry
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.rpc.registry.monitor;

import io.netty.handler.codec.http.DefaultFullHttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dxx.rpc.monitor.Controller;
import org.dxx.rpc.registry.Service;
import org.dxx.rpc.registry.ServiceRepository;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-7-26
 */
public class RegistryController implements Controller {

	@Override
	public String exec(DefaultFullHttpRequest request, Map<String, Object> model) {
		model.put("layout", "vm/layout/registry-layout.html");

		//		Server s1 = new Server("192.168.1.78:50050");
		//		s1.addService(new ViewService("desc1", "xx.xx.X1Service"));
		//
		//		Server s2 = new Server("192.168.1.22:50053");
		//
		//		s2.addService(new ViewService("desc2", "xx.xx.X2Service"));
		//		s2.addService(new ViewService("desc3", "xx.xx.X3Service"));

		List<Server> servers = new ArrayList<Server>();
		Map<String, List<Service>> all = ServiceRepository.getInstance().getServices();

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
		return "vm/registry.html";
	}
}
