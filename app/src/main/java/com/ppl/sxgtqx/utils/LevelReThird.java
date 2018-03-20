package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;

public class LevelReThird extends BmobObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	int level;
	String ID;					//自身id
	String name;			//名称
	String info;		//描述
	int delSta;		//判断是否被删除  0 已删除   1-有用



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

	@Override
	public String toString() {
		return "LevelReThird{" +
				"level=" + level +
				", ID='" + ID + '\'' +
				", name='" + name + '\'' +
				", info='" + info + '\'' +
				", delSta=" + delSta +
				'}';
	}
}
