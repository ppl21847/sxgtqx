package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;

public class LevelReThird extends BmobObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String ID;					//自身id
	String name;			//名称
	String info;		//描述

	public LevelReThird(String fatherId, String iD, String name,
                        int level, String info, double posLat, double posLong,
                        String imgPath) {
		super();
		ID = iD;
		this.name = name;
		this.info = info;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "LevelReThird{" +
				", ID='" + ID + '\'' +
				", name='" + name + '\'' +
				", info='" + info + '\'' +
				'}';
	}
}
