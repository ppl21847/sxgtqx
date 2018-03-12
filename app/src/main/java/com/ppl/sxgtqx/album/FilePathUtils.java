package com.ppl.sxgtqx.album;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class FilePathUtils {

	/**
	 * ����SDcard״̬
	 * @return boolean
	 * */
	public static boolean checkSDCard(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//�ж�SDCard�Ƿ���ڲ��ɶ�д
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * �����ļ���Ŀ¼
	 * @param context
	 * @return �ļ������Ŀ¼
	 * */
	public static String setMkdir(Context context){
		String filePath;
		if(checkSDCard()){
			filePath = Environment.getExternalStorageDirectory() + File.separator + "travelApp";
		}else{
			filePath = context.getCacheDir().getAbsolutePath() + File.separator + "travelApp";
		}
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}
		return filePath;
	}
}
