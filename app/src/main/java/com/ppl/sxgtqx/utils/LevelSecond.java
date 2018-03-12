package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;


public class LevelSecond extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String fatherId;
	String ID;
	String name;
	int delSta;		///判断是否被删除  0 已删除   1-有用
	int level;
	
	
	public LevelSecond(String fatherId, String iD, String name, 
			int level,int delSta) {
		super();
		this.fatherId = fatherId;
		ID = iD;
		this.name = name;
		this.level = level;
		this.delSta = delSta;
	}
	public String getFatherId() {
		return fatherId;
	}
	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public LevelSecond() {
		super();
	}
	public int getDelSta() {
		return delSta;
	}
	public void setDelSta(int delSta) {
		this.delSta = delSta;
	}
	@Override
	public String toString() {
		return "LevelSecond [fatherId=" + fatherId + ", ID=" + ID + ", name="
				+ name + ", delSta=" + delSta + ", level=" + level + "]";
	}
}
