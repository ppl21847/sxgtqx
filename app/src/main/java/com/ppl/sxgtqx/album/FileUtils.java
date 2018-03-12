package com.ppl.sxgtqx.album;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.WindowManager;

public class FileUtils {
	public static String SDPATH = Environment.getExternalStorageDirectory() + "tmpFlold";
	public static File tmpfloder = new File(Environment.getExternalStorageDirectory(), "/"+"upLoadSkeletonizeFlie");;
	public static String saveBitmap(Bitmap bm){
		String returnState = "error";
		String fileName = ImageUtils.imageUriFromCamera.getPath();
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
				returnState = "success";
			} catch (IOException e) {
				e.printStackTrace();
				return returnState;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return returnState;
		}
		return returnState;
	}
	
	public static File skeletonize(String inFileName,WindowManager wm){
		File outFile = null;
		File file = new File(tmpfloder+"/");
		if(!file.exists()){
			file.mkdirs();
		}
		int start = inFileName.lastIndexOf("/");
		String fileName = inFileName.substring(start + 1);
		outFile = new File(file ,fileName);
		if(outFile.exists()){
			outFile.delete();
		}
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			Bitmap bmp = StringToBitmap.StringToBitmap(inFileName, wm, 1);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				return outFile;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return outFile;
		}
		return outFile;
	}
	/**
	 * �ݹ�ɾ���ļ����ļ���
	 * @param file ��ɾ���ĸ�Ŀ¼
	 * */
	public static void RecursionDeleteFile(File file){
		if(file.isFile()){
			file.delete();
			return;
		}
		if(file.isDirectory()){
			File[] childFile = file.listFiles();
			if(childFile == null || childFile.length == 0){
				file.delete();
				return;
			}
			for(File f : childFile){
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}
	public static void saveBitmap(Bitmap bm, String picName) {
		try {
			File f = new File(picName); 
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}
	public static boolean skeletonizeFile(File fromFile,File toFile,WindowManager wm){
		boolean writeSts = false;
		if (!fromFile.exists()) {
			return writeSts;
		}

		if (!fromFile.isFile()) {
			return writeSts;
		}

		if (!fromFile.canRead()) {
			return writeSts;
		}

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}

		if (toFile.exists()) {
			toFile.delete();
		}

//		//当文件不存时，canWrite一直返回的都是false
//		if (!toFile.canWrite()) {
//			//		 MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());
//			//		 Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
//			return writeSts;
//		}
		try {
			FileOutputStream fos = new FileOutputStream(toFile);
			Bitmap bmp = StringToBitmap.StringToBitmap(fromFile.getPath(), wm, 1);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
				writeSts = true;
			} catch (IOException e) {
				e.printStackTrace();
				writeSts = false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			writeSts = false;
		}
		return writeSts;
	}
	/**
	 * 去后缀
	 * */
	public static String getFileNameNoEx(String filename) {    
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length()))) {    
                return filename.substring(0, dot);    
            }    
        }    
        return filename;    
    }
}
