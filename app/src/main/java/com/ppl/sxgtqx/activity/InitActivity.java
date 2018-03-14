package com.ppl.sxgtqx.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.liulishuo.filedownloader.FileDownloader;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.utils.LogcatHelper;

import cn.bmob.v3.Bmob;

/**
 * Created by liupaipai on 2018/3/14.
 */

public class InitActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);



        Intent intent = new Intent();
        intent.setClass(InitActivity.this, BNDemoMainActivity.class);
        startActivity(intent);
    }
}
