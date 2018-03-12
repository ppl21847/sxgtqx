package com.ppl.sxgtqx.utils;

import com.baidu.location.BDLocation;

/**
 * 封装定位结果和时间的实体类
 * 
 * @author ppl
 *
 */
public class LocationEntity {
	BDLocation location;
	long time;
	public BDLocation getLocation() {
		return location;
	}
	public void setLocation(BDLocation location) {
		this.location = location;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public LocationEntity(BDLocation location, long time) {
		super();
		this.location = location;
		this.time = time;
	}
}
