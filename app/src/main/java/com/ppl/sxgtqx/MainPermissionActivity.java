package com.ppl.sxgtqx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.utils.UpdateEntity;
import com.ppl.sxgtqx.view.MyToast;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainPermissionActivity extends Activity implements View.OnClickListener{
    private MyToast myToast;
    private Button mBtUpdateFile;
    private EditText mTvUrl;
    private EditText mEtVersionCode;
    private EditText mEtVersionDescription;
    private Button mBtUpdate;
    private File updateFile;
    private Boolean upFileSta = false;
    private String upFileUrl;
    private EditText mTvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_permission);
        myToast = new MyToast(MainPermissionActivity.this, 5000, Gravity.BOTTOM);
        //得到当前外部存储设备的目录
        String SDCardRoot= Environment.getExternalStorageDirectory()+ File.separator;
        //File.separator为文件分隔符”/“,方便之后在目录下创建文件
        String fileDir = "sxgtxlt";
        File dirFile=new File(SDCardRoot+File.separator+fileDir);
        if(!dirFile.exists()){
            dirFile.mkdir();        //创建文件夹
        }

        updateFile = new File(dirFile + File.separator + "sxgtqx-v4-20180322-2052.apk");

        initView();

        initData();
    }

    private void initData() {
        mBtUpdateFile.setOnClickListener(this);
        mBtUpdate.setOnClickListener(this);
    }

    private void initView() {
        mBtUpdateFile = findViewById(R.id.go_main);
        mTvUrl = findViewById(R.id.tv_apk_url);
        mEtVersionCode = findViewById(R.id.et_new_version);
        mEtVersionDescription = findViewById(R.id.et_new_version_des);
        mBtUpdate = findViewById(R.id.bt_update_info);
        mTvId = findViewById(R.id.tv_fileId);

        mTvUrl.setText(updateFile.getAbsolutePath());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_update_info:
                updateInfo();
                break;
            case R.id.go_main:
                upDataFile();
                break;
        }
    }

    /**
     * 更新新安装包信息
     * */
    private void updateInfo() {
        if(!upFileSta){
            myToast.show("请先上传文件");
            return;
        }
        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.setApkUrl(upFileUrl);
        updateEntity.setUpdateDescroption(mEtVersionDescription.getText().toString());
        updateEntity.setVersionCode(Integer.parseInt(mEtVersionCode.getText().toString()));

        updateEntity.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    myToast.show("创建数据成功：" + objectId);
                    mTvId.setText(objectId);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    myToast.show("失败："+e.getMessage()+","+e.getErrorCode());
                    mTvId.setText("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void upDataFile() {
        if(!updateFile.exists()){
            myToast.show("升级文件不存在");
        }else {
            final BmobFile bmobFile = new BmobFile(updateFile);
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        myToast.show("上传文件成功:" + bmobFile.getFileUrl());
                        mTvUrl.setText(""+bmobFile.getFileUrl());
                        upFileSta = true;
                        upFileUrl = bmobFile.getFileUrl();
                    }else{
                        myToast.show("上传文件失败：" + e.getMessage());
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    Log.d("progress","onProgress: "+value);
                    mTvUrl.setText("onProgress: "+value);
                }
            });
        }
    }
}
