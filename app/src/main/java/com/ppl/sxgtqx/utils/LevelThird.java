package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;

public class LevelThird extends BmobObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String fatherId;		//父级id
	String ID;					//自身id
	String name;			//名称
	int user;		//判断是否被删除  true 已删除
	int level;			//当前级别2-三级
	
	String info;		//描述
	double posLat;		//
	double posLong;
	String imgPath;		//三级地址下的图片地址  json 数组
	String imgLocal;	//三级地址下的图片地址  json 数组  本地
	int delSta;
	
	public LevelThird(String fatherId, String iD, String name, 
			int level, String info, double posLat, double posLong,
			String imgPath) {
		super();
		this.fatherId = fatherId;
		ID = iD;
		this.name = name;
		this.level = level;
		this.info = info;
		this.posLat = posLat;
		this.posLong = posLong;
		this.imgPath = imgPath;
	}
	
	
	public int getDelSta() {
		return delSta;
	}


	public void setDelSta(int delSta) {
		this.delSta = delSta;
	}


	public String getImgLocal() {
		return imgLocal;
	}

	public void setImgLocal(String imgLocal) {
		this.imgLocal = imgLocal;
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
	
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public double getPosLat() {
		return posLat;
	}
	public void setPosLat(double posLat) {
		this.posLat = posLat;
	}
	public double getPosLong() {
		return posLong;
	}
	public void setPosLong(double posLong) {
		this.posLong = posLong;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public LevelThird() {
		super();
	}
	@Override
	public String toString() {
		return "LevelThird [fatherId=" + fatherId + ", ID=" + ID + ", name="
				+ name + ", user=" + user + ", level=" + level + ", info="
				+ info + ", posLat=" + posLat + ", posLong=" + posLong
				+ ", imgPath=" + imgPath + "]";
	}
}
