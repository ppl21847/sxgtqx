package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;


public class LevelRoot extends BmobObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ID;
	String name;
	int delSta;		//判断是否被删除  0 已删除   1-有用
	int level;
	
	public LevelRoot(String iD, String name,  int level,int delSta) {
		super();
		ID = iD;
		this.name = name;
		this.level = level;
		this.delSta = delSta;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public int getDelSta() {
		return delSta;
	}
	public void setDelSta(int delSta) {
		this.delSta = delSta;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public LevelRoot() {
		super();
	}
	@Override
	public String toString() {
		return "LevelRoot [ID=" + ID + ", name=" + name + ", delSta=" + delSta
				+ ", level=" + level + "]";
	}
	
}
