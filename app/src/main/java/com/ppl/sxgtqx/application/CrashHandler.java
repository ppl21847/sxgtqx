package com.ppl.sxgtqx.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

public class CrashHandler implements UncaughtExceptionHandler{
	private static final String TAG = "CrashHandler";  
    private static final boolean DEBUG = true;  
  
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/ryg_test/log/";  
    private static final String FILE_NAME = "sxgtqxxlt_crash";
    
  //log文件的后缀名  
    private static final String FILE_NAME_SUFFIX = ".trace";  
    private static CrashHandler sInstance = new CrashHandler(); 
  //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）  
    private UncaughtExceptionHandler mDefaultCrashHandler;  
    private Context mContext;
    
    
    //构造方法私有，防止外部构造多个实例，即采用单例模式  
	public CrashHandler() {
	}
	
	public static CrashHandler getInstance(){
		return sInstance;
	}

    //这里主要完成初始化工作
	public void init(Context context){
		//获取系统默认的异常处理器
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		//将当前实例设为系统默认的异常处理器 
		Thread.setDefaultUncaughtExceptionHandler(this);
		//获取Context，方便内部使用  
		mContext = context.getApplicationContext();
	}
	/** 
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法 
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。 
     */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try {
			//导出异常信息到SD卡中
			String fileName = dumpExceptionToSDCard(ex);
            //这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
			if(!TextUtils.isEmpty(fileName)){
				uploadExceptionToServer(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();  
		}
		//打印出当前调用栈信息  
        ex.printStackTrace();  
  
        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己  
        if (mDefaultCrashHandler != null) {  
            mDefaultCrashHandler.uncaughtException(thread, ex);  
        } else { 
        	//退出程序  
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
	}
	private void uploadExceptionToServer(String fileName) {
		// TODO Auto-generated method stub
		String crashFile = fileName;
		BmobFile bmobFile = new BmobFile(new File(crashFile));
		bmobFile.uploadblock(new UploadFileListener() {

			@Override
			public void done(BmobException e) {
			}

			@Override
			public void onProgress(Integer value) {
				// 返回的上传进度（百分比）
			}
		});
	}

	private String dumpExceptionToSDCard(Throwable ex)throws IOException{
		//如果SD卡不存在或无法使用，则无法把异常信息写入SD卡  
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return null;
//			if(DEBUG){
//				Log.w(TAG, "sdcard unmounted,skip dump exception");
//
//			}
		}
		File dir = new File(PATH);
		if(!dir.exists()){
			dir.mkdirs();
		}
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
		//以当前时间创建log文件
		String fileName = PATH + FILE_NAME + time + FILE_NAME_SUFFIX;
        File file = new File(fileName);
        
        try {
        	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        	
        	//导出发生异常的时间  
        	pw.println(time);
        	//导出手机信息  
            dumpPhoneInfo(pw);
            pw.println();
          //导出异常的调用栈信息  
            ex.printStackTrace(pw);
            pw.close();
            return fileName;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG,  "dump crash info failed");
		}
        return null;
	}

	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
		// 应用的版本名称和版本号 
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES); 
		pw.print("App Version: ");  
        pw.print(pi.versionName);  
        pw.print('_');  
        pw.println(pi.versionCode);  
  
        //android版本号  
        pw.print("OS Version: ");  
        pw.print(Build.VERSION.RELEASE);  
        pw.print("_");  
        pw.println(Build.VERSION.SDK_INT);  
  
        //手机制造商  
        pw.print("Vendor: ");  
        pw.println(Build.MANUFACTURER);  
  
        //手机型号  
        pw.print("Model: ");  
        pw.println(Build.MODEL);  
  
        //cpu架构  
        pw.print("CPU ABI: ");  
        pw.println(Build.CPU_ABI);  
	}

}
