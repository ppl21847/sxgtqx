package com.ppl.sxgtqx.application;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.liulishuo.filedownloader.FileDownloader;
import com.ppl.sxgtqx.service.LocationService;
import com.ppl.sxgtqx.utils.LogcatHelper;

import cn.bmob.v3.Bmob;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 * 
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class LocationApplication extends Application {
	public LocationService locationService;
    public Vibrator mVibrator;
  //管理员登录状态
    public static boolean adminLoginSta = false;
    public static final String NOW = "tmpTime";
    
    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		initLoginInfo();

		SDKInitializer.initialize(getApplicationContext());

		LogcatHelper.getInstance(this).start();
		Log.e("LocationApplication", "Bmob.initialize : start");
		//第一：默认初始化  BMOB初始化
		Bmob.initialize(getApplicationContext(), "ba27ad9402b3fd4c9495c1b7d1379f48");
		Log.e("LocationApplication", "Bmob.initialize : end");

		FileDownloader.setupOnApplicationOnCreate(this);
    }
    
    /**
	 * 登录状态
	 * */
	private void initLoginInfo() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
				Log.e("PreTime", sp.getLong(NOW, 0L)+"");
				adminLoginSta = ((System.currentTimeMillis() - sp.getLong(NOW, 0L)) < 7*24*60*60*1000) ? true : false;
			}
		}).start();
	}
}
