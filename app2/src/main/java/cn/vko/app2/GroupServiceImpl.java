/**
 * GroupServiceImpl.java
 * cn.vko.app2
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app2;

import java.util.ArrayList;
import java.util.List;

import org.dxx.rpc.config.annotation.RpcService;

import cn.vko.app.share.Group;
import cn.vko.app.share.GroupService;

/**
 * 
 * 
 * @author   dijingran
 * @Date	 2014年6月20日
 */
@RpcService
public class GroupServiceImpl implements GroupService {

	/**
	 * (non-Javadoc)
	 * @see cn.vko.app.share.GroupService#getGroups()
	 */
	@Override
	public List<Group> getGroups() {
		List<Group> groups = new ArrayList<Group>();
		Group g = new Group();
		g.setGroupName("群组1");
		groups.add(g);
		return groups;
	}

}
