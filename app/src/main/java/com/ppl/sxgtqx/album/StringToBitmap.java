package com.ppl.sxgtqx.album;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;


public class StringToBitmap {

	public StringToBitmap() {
		// TODO Auto-generated constructor stub
	}
	public static String saveImgTopath(String inPath , Bitmap inBmp){
		String returnState = ImageUtils.FAILURE_STA;
		try {
			FileOutputStream fos = new FileOutputStream(inPath);
			inBmp.compress(CompressFormat.JPEG, 100, fos);
			try {
				fos.flush();
				fos.close();
				returnState = ImageUtils.SUCCESS_STA;
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
	/**
	 * ���ܣ�����ͼƬ��30-40k*/
	public static Bitmap ScalesBitmap(Bitmap inBmp){
		Bitmap outBmp = inBmp;

		int imgHeight = inBmp.getHeight();
		int imgWidth = inBmp.getWidth();

		int scale  = imgHeight * imgWidth * 4 / 3000000;
		if(scale <= 1)
			return outBmp;
		double scaleV = 1.0 / scale;
		Matrix matrix = new Matrix();
		matrix.postScale((float)scaleV,(float)scaleV);
		outBmp = Bitmap.createBitmap(inBmp, 0, 0, inBmp.getWidth(), inBmp.getHeight(), matrix, true);

		return outBmp;
	}
	/**
	 * ���ܣ���һ��ͼƬ�ľ���·��ת���ɳ���ָ����С��ͼƬ �ȱ� ����ͼƬ
	 * @param inBmpPath ͼƬ����·��
	 * @param width height ָ����С��ͼƬ
	 * */
	public static Bitmap StringToBitmap(Bitmap inBmpPath,int width,int height){

		Bitmap bmp;
		Matrix matrix = new Matrix();
		matrix.postScale(1, 1);
		//ͼƬ����ת��Bitmap��ʽ
		Bitmap bitmap = inBmpPath;//��ʾ ����Ļ�߶�/3
		if(width == 0 && height == 0)
			return bitmap;
		else if(width == 0){
			int dh = height;	//��ȡ��Ļ�߶�

			int hRatio = (int)Math.ceil(inBmpPath.getHeight() / (float)dh);
			if(hRatio > 1){
				matrix.postScale(hRatio, hRatio);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}else if(height == 0){
			int dw = width;
			int wRatio = (int)Math.ceil(inBmpPath.getWidth() / (float)dw);
			if(wRatio > 1){
				matrix.postScale(wRatio, wRatio);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}else{
			int dw = width;
			int dh = height;
			int wRatio = (int)Math.ceil(inBmpPath.getWidth() / (float)dw);
			int hRatio = (int)Math.ceil(inBmpPath.getHeight() / (float)dh);
			if(wRatio > 1 || hRatio>1){
				int scale= wRatio > hRatio ? wRatio:hRatio;		
				matrix.postScale(scale, scale);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		return bmp;
	}
	/**
	 * ��Bitmap������ļ�
	 * */
	public static File BitmapToFile(File dir,Bitmap bmp){
		File file = new File(dir,System.currentTimeMillis()+".jpg");
		if(file.exists()){
			file.delete();
		}
		FileOutputStream fops;
		try {
			fops = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fops);
			fops.flush();
			fops.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
	/**
	 * ��Bitmap������ļ�
	 * */
	public static File BitmapToFile(Bitmap bmp){
		File file = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis()+".jpg");
		if(file.exists()){
			file.delete();
		}
		FileOutputStream fops;
		try {
			fops = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fops);
			fops.flush();
			fops.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}
	/**
	 * ���ܣ���һ��ͼƬ�ľ���·��ת����ͼƬ
	 * @param 
	 * 
	 * */
	public static Bitmap StringToBitmap(String inBmpPath,WindowManager wm,int in_scale){

		//ͼƬ����ת��Bitmap��ʽ
		/*Ϊ��֤ͼƬ����̫�󳬹�"16M"����Ҫ��ͼƬ��������
			����:
			1.��ȡͼƬ��ͷ��Ϣ������������ȡͼƬ
			2.ѹ��ͼƬ����Ļ���
			3.�ٰ�ѹ�����ͼƬ�����ڴ�
		 */
		//����ֻ���Ļ���
		int heightWD = wm.getDefaultDisplay().getHeight() / in_scale;
		int widthWD = wm.getDefaultDisplay().getWidth() / in_scale;
		//1.��ȡͼƬ��ͷ��Ϣ������������ȡͼƬ
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(inBmpPath, opts);
		int imgHeight = opts.outHeight;
		int imgWidth = opts.outWidth;
		//2.����ѹ���� ѹ��ͼƬ����Ļ���
		double scaleX = (imgWidth*1.0)/widthWD;
		double scaleY = (imgHeight*1.0)/heightWD;
		double scale = 1.0;
		if(scaleX >= scaleY & scaleY >=1){
			scale = scaleX;
		}else if(scaleY > scaleX & scaleX >= 1){
			scale = scaleY;
		}
		//������ȡͼƬ
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = (int) Math.ceil(scale);//����ȡ��
		Bitmap bmpSmall = BitmapFactory.decodeFile(inBmpPath, opts);
		return bmpSmall;
	}
	/**
	 * @see ��content Uri ת��File Uri
	 * 
	 * */
	public static Uri convertUri(Context context,Uri uri)
	{
		InputStream is = null;
		try {
			is = context.getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return saveBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @see ��bmp����
	 * */
	public static Uri saveBitmap(Bitmap bm)
	{
		File tmpDir = new File(Environment.getExternalStorageDirectory() + "/camera");
		if(!tmpDir.exists())
		{
			tmpDir.mkdir();
		}
		String tmp = String.valueOf(System.currentTimeMillis());
		File img = new File(tmpDir.getAbsolutePath() + tmp+".png");
		try {
			FileOutputStream fos = new FileOutputStream(img);
			bm.compress(CompressFormat.PNG, 85, fos);
			fos.flush();
			fos.close();
			return Uri.fromFile(img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @param һ��ͼƬ��Uri
	 * @return  ���ļ�ϵͳ�е�·��
	 * @exception ����һ��ͼƬ��Uri ��content����ȡ�����ļ�ϵͳ�е�·��
	 * @author liupaipai
	 * */
	@SuppressLint("NewApi") public static String getRealFilePath(final Context context,final Uri uri){
		if(uri == null){
			return null;
		}
		final String scheme = uri.getScheme();
		String data = null;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, uri)){
			if(isExternalStorageDocument(uri)){
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				if(split.length > 0){
					String type = split[0];
					if("primary".equalsIgnoreCase(type)){
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}
				}
			}else if(isDownloadsDocument(uri)){
				String id = DocumentsContract.getDocumentId(uri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context,contentUri,null,null);
			}else if(isMediaDocument(uri)){
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				
				String type = split[0];
				Uri contentUri = null;
				if("image".equals(type)){
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				}else if("video".equals(type)){
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				}else if("audio".equals(type)){
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID+"=?";
				String[] selectionArgs = new String[]{split[1]};
				return getDataColumn(context,contentUri,selection,selectionArgs);
			}
		}//MediaStore
		else if("content".equalsIgnoreCase(uri.getScheme())){
			if(isGooglePhotoUri(uri)){
				return uri.getLastPathSegment();
			}
			return getDataColumn(context, uri, null, null);
		}
		//File
		else if("file".equalsIgnoreCase(uri.getScheme())){
			return uri.getPath();
		}
		return null;
	}
	/**
	 * @param uri ���uri
	 * @return uri �Ƿ������ExternalStorageDocument��
	 * */
	public static boolean isExternalStorageDocument(Uri uri) {
		// TODO Auto-generated method stub
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri ���uri
	 * @return uri �Ƿ�ΪdownloadsProvider
	 * */
	public static boolean isDownloadsDocument(Uri uri){
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}
	public static boolean isMediaDocument(Uri uri){
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	public static boolean isGooglePhotoUri(Uri uri){
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	public static String getDataColumn(Context context,Uri uri,String selection,String[] selectionArgs){
		String retrnData = null;
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if(cursor != null && cursor.moveToFirst()){
				int index = cursor.getColumnIndexOrThrow(column);
				retrnData = cursor.getString(index);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("StringToBitmap", "getDataColumn Exception");
		}
		
		if(cursor != null){
			cursor.close();
		}
		return retrnData;
	}

	public static String saveBitmap(Bitmap bmp,String path){
		String Picpath = "";
		File cache = new File(Environment.getExternalStorageDirectory(), "com.see");
		if(!cache.exists()){
			cache.mkdirs();
		}
		File file = new File(cache, path+".png");
		if(file.exists()){
			file.delete();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			Picpath = file.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Picpath;
	}
}
