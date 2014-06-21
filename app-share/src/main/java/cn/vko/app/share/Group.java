/**
 * Group.java
 * cn.vko.app.share
 * Copyright (c) 2014, 北京微课创景教育科技有限公司版权所有.
*/

package cn.vko.app.share;

import java.io.Serializable;

/**
 * 
 * 
 * @author   dixingxing
 * @Date	 2014-6-18
 */
@SuppressWarnings("serial")
public class Group implements Serializable {
	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Group [groupName=" + groupName + "]";
	}
}
