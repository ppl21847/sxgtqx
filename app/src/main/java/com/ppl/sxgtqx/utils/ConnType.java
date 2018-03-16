package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;

import com.baidu.mapapi.model.LatLng;

public class ConnType extends BmobObject{
	int type;		//0-我的位置	1-有用信息		2-新建
	int Level;		//等级0-根目录	1-二级	2-三级
	int delSta;		//删除状态			0-已删除		1-未删除
	String conn;
	String fatherId;	//父级ID  属于谁
	String fatherReId;	//第三级属于谁 2018年3月16新添加需求
	String selfId;		//自身ID
	boolean selectSta;		//该item是否被选中
	LatLng pos;				//位置
	String locImgsPath;
	String netImgsPath;
	double posLat;
	double posLong;
	String info;
	
	
	public ConnType() {
		super();
	}
	/**
	 * 三级地址构造
	 * */
	public ConnType(int type, int level, String conn, String fatherId,String fatherReId,
			String selfId, double posLat, double posLong,int delSta) {
		super();
		this.type = type;
		Level = level;
		this.conn = conn;
		this.fatherId = fatherId;
		this.selfId = selfId;
		this.posLat = posLat;
		this.posLong = posLong;
		this.delSta = delSta;
		this.fatherReId = fatherReId;
	}
	
	public ConnType(int type, int level, int delSta, String conn,
			String fatherId, String selfId, double posLat, double posLong,
			String info) {
		super();
		this.type = type;
		Level = level;
		this.delSta = delSta;
		this.conn = conn;
		this.fatherId = fatherId;
		this.selfId = selfId;
		this.posLat = posLat;
		this.posLong = posLong;
		this.info = info;
	}
	/**
	 * 二级地址构造
	 * */
	public ConnType(int type, int level, String conn, String fatherId,String selfId,int delSta) {
		super();
		this.type = type;
		Level = level;
		this.conn = conn;
		this.fatherId = fatherId;
		this.selfId = selfId;
		this.delSta = delSta;
	}
	/**
	 * 一级地址构造
	 * */
	public ConnType(int type, int level, String conn, String selfId,int delSta) {
		super();
		this.type = type;
		Level = level;
		this.conn = conn;
		this.selfId = selfId;
		this.delSta = delSta;
	}
	
	public int getDelSta() {
		return delSta;
	}
	public void setDelSta(int delSta) {
		this.delSta = delSta;
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
	public String getLocImgsPath() {
		return locImgsPath;
	}
	public void setLocImgsPath(String locImgsPath) {
		this.locImgsPath = locImgsPath;
	}
	public String getNetImgsPath() {
		return netImgsPath;
	}
	public void setNetImgsPath(String netImgsPath) {
		this.netImgsPath = netImgsPath;
	}
	public LatLng getPos() {
		return pos;
	}
	public void setPos(LatLng pos) {
		this.pos = pos;
	}
	public boolean isSelectSta() {
		return selectSta;
	}
	public void setSelectSta(boolean selectSta) {
		this.selectSta = selectSta;
	}
	public String getFatherId() {
		return fatherId;
	}
	public void setFatherId(String fatherId) {
		this.fatherId = fatherId;
	}
	public String getSelfId() {
		return selfId;
	}
	public void setSelfId(String selfId) {
		this.selfId = selfId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLevel() {
		return Level;
	}
	public void setLevel(int level) {
		Level = level;
	}
	public String getConn() {
		return conn;
	}
	public void setConn(String conn) {
		this.conn = conn;
	}
	@Override
	public String toString() {
		return "ConnType [type=" + type + ", Level=" + Level + ", delSta="
				+ delSta + ", conn=" + conn + ", fatherId=" + fatherId
				+ ", selfId=" + selfId + ", selectSta=" + selectSta + ", pos="
				+ pos + ", locImgsPath=" + locImgsPath + ", netImgsPath="
				+ netImgsPath + ", posLat=" + posLat + ", posLong=" + posLong
				+ ", info=" + info + "]";
	}

	public String getFatherReId() {
		return fatherReId;
	}

	public void setFatherReId(String fatherReId) {
		this.fatherReId = fatherReId;
	}
}
