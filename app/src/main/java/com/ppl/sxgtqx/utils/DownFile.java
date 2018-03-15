package com.ppl.sxgtqx.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

/**
 * Created by liupaipai on 2018/3/15.
 */

public class DownFile {
    public static final int FILE_DOWN_PROGRESS = 33;
    public static final int FILE_DOWN_COMPLETED = 35;
    public static final int FILE_DOWN_ERROR = 34;

    private void downLoadFile(String fileUrl, final Handler mHandler){
        //得到当前外部存储设备的目录
        String SDCardRoot= Environment.getExternalStorageDirectory()+ File.separator;
        //File.separator为文件分隔符”/“,方便之后在目录下创建文件
        String fileDir = "sxgtxlt";
        File dirFile=new File(SDCardRoot+File.separator+fileDir);
        if(!dirFile.exists()){
            dirFile.mkdir();        //创建文件夹
        }

        final String updateFile = dirFile + File.separator + "new_sxgtqx.apk";
        final File newApk = new File(updateFile);
        if(newApk.exists()){
            newApk.delete();
        }

        FileDownloader.getImpl().create(fileUrl)
                .setPath(updateFile)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("downFile","pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d("downFile","connected");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                        final int progress = soFarBytes * 100 / totalBytes;
                        Message msg = new Message();
                        msg.what = FILE_DOWN_PROGRESS;
                        Log.d("downFile","progress: "+progress);
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.d("downFile","blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.d("downFile","retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //安装
                        Message msgDownCompletes = new Message();
                        msgDownCompletes.what = FILE_DOWN_COMPLETED;
                        Log.d("downFile","completed");
                        msgDownCompletes.obj = newApk.getAbsoluteFile();
                        mHandler.sendMessage(msgDownCompletes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("downFile","paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d("downFile","error");
                        mHandler.sendEmptyMessage(FILE_DOWN_ERROR);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d("downFile","error");
                    }
                }).start();
    }
}
