/**
 * TeacherServiceImpl.java
 * cn.vko.app1
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app1;

import org.dxx.rpc.config.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.vko.app.share.Teacher;
import cn.vko.app.share.TeacherService;

/**
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */
@RpcService("名师服务类")
public class TeacherServiceImpl implements TeacherService {
	private static final Logger logger = LoggerFactory.getLogger(TeacherServiceImpl.class);

	/**
	 * (non-Javadoc)
	 * @see cn.vko.app.share.TeacherService#save(cn.vko.app.share.Teacher)
	 */
	@Override
	public int save(Teacher t) {
		logger.debug("saveing : " + t.getName());
		return 0;
	}

}
