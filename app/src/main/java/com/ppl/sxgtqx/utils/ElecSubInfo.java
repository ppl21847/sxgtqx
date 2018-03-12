package com.ppl.sxgtqx.utils;

import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class ElecSubInfo {
	int id;		//所有变电所有唯一的一个编号
	String name;
	LatLng pos;
	String describ;	//描述
	List<Integer>imgs;
	
	public ElecSubInfo() {
		super();
	}
	
	public ElecSubInfo(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public ElecSubInfo(int id, String name, LatLng pos) {
		super();
		this.id = id;
		this.name = name;
		this.pos = pos;
	}

	public ElecSubInfo(int id, String name, LatLng pos, String describ,
			List<Integer> imgs) {
		super();
		this.id = id;
		this.name = name;
		this.pos = pos;
		this.describ = describ;
		this.imgs = imgs;
	}

	public ElecSubInfo(String name, LatLng pos, String describ,
			List<Integer> imgs) {
		super();
		this.name = name;
		this.pos = pos;
		this.describ = describ;
		this.imgs = imgs;
	}
	public ElecSubInfo(String name, LatLng pos, String describ) {
		super();
		this.name = name;
		this.pos = pos;
		this.describ = describ;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescrib() {
		return describ;
	}
	public void setDescrib(String describ) {
		this.describ = describ;
	}
	public List<Integer> getImgs() {
		return imgs;
	}
	public void setImgs(List<Integer> imgs) {
		this.imgs = imgs;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public LatLng getPos() {
		return pos;
	}
	public void setPos(LatLng pos) {
		this.pos = pos;
	}
}
