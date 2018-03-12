package com.ppl.sxgtqx.utils;

public class MapCityInfo {
	String name;
	boolean isEixt;
	int cityId;
	String size;
	
	
	public MapCityInfo(String name, boolean isEixt, int cityId, String size) {
		super();
		this.name = name;
		this.isEixt = isEixt;
		this.cityId = cityId;
		this.size = size;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEixt() {
		return isEixt;
	}
	public void setEixt(boolean isEixt) {
		this.isEixt = isEixt;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public MapCityInfo() {
		super();
	}
}
