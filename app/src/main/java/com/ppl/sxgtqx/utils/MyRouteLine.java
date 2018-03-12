package com.ppl.sxgtqx.utils;

import com.baidu.mapapi.search.route.DrivingRouteLine;

public class MyRouteLine {
	DrivingRouteLine line;
	boolean showSta;
	
	public MyRouteLine() {
		super();
	}
	public MyRouteLine(DrivingRouteLine line, boolean showSta) {
		super();
		this.line = line;
		this.showSta = showSta;
	}
	public DrivingRouteLine getLine() {
		return line;
	}
	public void setLine(DrivingRouteLine line) {
		this.line = line;
	}
	public boolean isShowSta() {
		return showSta;
	}
	public void setShowSta(boolean showSta) {
		this.showSta = showSta;
	}
}
