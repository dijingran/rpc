/**
 * Copyright(c) 2000-2013 HC360.COM, All Rights Reserved.
 * Project: guard 
 * Author: dixingxing
 * Createdate: 下午7:35:23
 * Version: 1.0
 *
 */
package org.dxx.rpc.monitor.stat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @project guard
 * @author dixingxing
 * @version 1.0
 * @date 2013-8-21 下午7:35:23
 */
public class StatTarget {

	public static final DecimalFormat DF = new DecimalFormat(",###");

	private String desc;

	private String longDesc;

	private long costMin = Long.MAX_VALUE;
	private long costMax = 0;

	private AtomicLong invokeTimes = new AtomicLong(0);
	private AtomicLong exTimes = new AtomicLong(0);

	private AtomicLong costTotal = new AtomicLong(0);

	private List<Dimension> dimensions;

	public StatTarget() {
		dimensions = new ArrayList<Dimension>();
		dimensions.add(new Dimension(0, 50));
		dimensions.add(new Dimension(50, 100));
		dimensions.add(new Dimension(100, 500));
		dimensions.add(new Dimension(500, 1000));
		dimensions.add(new Dimension(1000, 3000));
		dimensions.add(new Dimension(3000, 5000));
		dimensions.add(new Dimension(5000, 10000));
		dimensions.add(new Dimension(10000, 20000));
		dimensions.add(new Dimension(20000, 9223372036854775807L));
	}

	/**
	 * 计算出主要时间区间（出现次数最多的）
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-23 上午11:11:18
	 */
	public void chooseMajor() {
		Dimension max = null;
		for (Dimension d : dimensions) {
			d.setMajor(false);
			if (max == null) {
				max = d;
				continue;
			}
			if (d.getTimes().get() > max.getTimes().get()) {
				max = d;
			}
		}
		if (max != null) {
			max.setMajor(true);
		}
	}

	/**
	 * 增加调用次数及总共消耗时间。
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-23 上午11:12:48
	 * @param beginMillis
	 */
	public void addTotalCost(long beginMillis) {
		addTotalCost(beginMillis, false);
	}

	/**
	 * 增加调用次数及总共消耗时间。
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-4-18 下午4:38:14
	 * @param cost
	 */
	public void addTotalCost(long beginMillis, boolean hasException) {
		this.invokeTimes.incrementAndGet();
		if (hasException) {
			this.exTimes.incrementAndGet();
		}

		long cost = (System.currentTimeMillis() - beginMillis);

		this.costMin = (cost < costMin ? cost : costMin);
		this.costMax = (cost > costMax ? cost : costMax);

		for (Dimension d : dimensions) {
			if (d.matched(cost)) {
				break;
			}
		}
		this.costTotal.addAndGet(cost);
	}

	public long getAverage() {
		if (invokeTimes.get() == 0) {
			return 0;
		}
		return costTotal.get() / invokeTimes.get();
	}

	// ============== 格式化 S ====================
	public String getCostTotalStr() {
		return DF.format(costTotal.get());
	}

	/**
	 * 平均执行时间
	 * 
	 * @author dixingxing
	 * @version 1.0
	 * @date 2013-8-22 上午9:39:55
	 * @return
	 */
	public String getAverageStr() {
		if (invokeTimes.get() == 0) {
			return "0";
		}
		return DF.format(costTotal.get() / invokeTimes.get());
	}

	public String getCostMinStr() {
		return DF.format(costMin);
	}

	public String getCostMaxStr() {
		return DF.format(costMax);
	}

	// ============== 格式化 E ====================

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getCostMin() {
		return costMin;
	}

	public void setCostMin(long costMin) {
		this.costMin = costMin;
	}

	public long getCostMax() {
		return costMax;
	}

	public void setCostMax(long costMax) {
		this.costMax = costMax;
	}

	public AtomicLong getInvokeTimes() {
		return invokeTimes;
	}

	public void setInvokeTimes(AtomicLong invokeTimes) {
		this.invokeTimes = invokeTimes;
	}

	public AtomicLong getExTimes() {
		return exTimes;
	}

	public void setExTimes(AtomicLong exTimes) {
		this.exTimes = exTimes;
	}

	public List<Dimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}

	public AtomicLong getCostTotal() {
		return costTotal;
	}

	public void setCostTotal(AtomicLong costTotal) {
		this.costTotal = costTotal;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

}
