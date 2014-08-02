/**
 * HomeworkServiceImpl.java
 * org.dxx.student.service
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package org.dxx.student.service;

import org.dxx.rpc.config.annotation.RpcSpringService;
import org.dxx.rpc.student.HomeworkService;
import org.springframework.stereotype.Service;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-8-2
 */
@Service
@RpcSpringService("作业服务")
public class HomeworkServiceImpl implements HomeworkService {

	@Override
	public void save(String homework) {
	}

}
