package com.ppl.sxgtqx.utils;

import cn.bmob.v3.BmobObject;


public class UpdateEntity extends BmobObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String apkUrl;
	private int versionCode;
	private String versionDecs;
	private String updateDescroption;

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionDecs() {
		return versionDecs;
	}

	public void setVersionDecs(String versionDecs) {
		this.versionDecs = versionDecs;
	}

	public String getUpdateDescroption() {
		return updateDescroption;
	}

	public void setUpdateDescroption(String updateDescroption) {
		this.updateDescroption = updateDescroption;
	}
}
