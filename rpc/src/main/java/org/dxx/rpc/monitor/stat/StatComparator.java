/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: guard 
 * Author: dixingxing
 * Createdate: 下午1:54:56
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.stat;

import java.util.Comparator;

/**
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-8-25 下午1:54:56   
 */
public class StatComparator implements Comparator<StatTarget> {
	private String sortBy;

	private String orderBy;

	private boolean asc;

	/**
	* @param sortBy
	* @param orderBy
	*/
	public StatComparator(String sortBy, String orderBy) {
		super();
		assert sortBy != null;
		this.sortBy = sortBy;
		this.orderBy = orderBy;
		this.asc = !("des".equals(orderBy));
	}

	/**
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-25 下午1:55:26
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(StatTarget o1, StatTarget o2) {
		if (o1 == null || o2 == null) {
			return 0;
		}
		if ("desc".equals(sortBy)) {
			if (asc) {
				return o1.getDesc().compareTo(o2.getDesc());
			} else {
				return o2.getDesc().compareTo(o1.getDesc());
			}
		} else if ("invokeTimes".equals(sortBy)) {
			Long n1 = Long.valueOf(o1.getInvokeTimes().get());
			Long n2 = Long.valueOf(o2.getInvokeTimes().get());
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		} else if ("exTimes".equals(sortBy)) {
			Long n1 = Long.valueOf(o1.getExTimes().get());
			Long n2 = Long.valueOf(o2.getExTimes().get());
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		} else if ("costTotal".equals(sortBy)) {
			Long n1 = Long.valueOf(o1.getCostTotal().get());
			Long n2 = Long.valueOf(o2.getCostTotal().get());
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		} else if ("average".equals(sortBy)) {
			Long n1 = o1.getAverage();
			Long n2 = o2.getAverage();
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		} else if ("costMin".equals(sortBy)) {
			Long n1 = o1.getCostMin();
			Long n2 = o2.getCostMin();
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		} else if ("costMax".equals(sortBy)) {
			Long n1 = o1.getCostMax();
			Long n2 = o2.getCostMax();
			if (asc) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		}

		return 0;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
