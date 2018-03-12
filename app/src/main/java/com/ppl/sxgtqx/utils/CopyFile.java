package com.ppl.sxgtqx.utils;

import java.io.File;
import java.io.FileOutputStream;
import android.util.Log;

public class CopyFile {
	public static boolean copyfile(File fromFile, File toFile,Boolean rewrite )
	{
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

		if (toFile.exists() && rewrite) {
			toFile.delete();
		}

		//当文件不存时，canWrite一直返回的都是false
		if (!toFile.canWrite()) {
			//		 MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());
			//		 Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
			return writeSts;
		}

		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); //将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
			writeSts = true;
		} catch (Exception ex) {
			Log.e("readfile", ex.getMessage());
			writeSts = false;
		}
		return writeSts;
	}
}
