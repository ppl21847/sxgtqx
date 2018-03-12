package com.ppl.sxgtqx.album;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageUtils {

	public static final int REQUEST_CODE_FROM_CAMERA = 5001;
	public static final int REQUEST_CODE_FROM_ALBUM = 5002;
	public static final int REQUEST_CODE_FROM_ALBUM_GALLERY = 5003;
	public static final int TAKE_PICTURE = 0x000001;
	public static String photoName = "";
	public static final String SUCCESS_STA = "success"; 
	public static final String FAILURE_STA = "failure"; 
	/**
	 * �������ͼƬ��uri��ַ
	 */
	public static Uri imageUriFromCamera;

	/**
	 * ��ʾ��ȡ��Ƭ��ͬ��ʽ�Ի���
	 */
	public static void showImagePickDialog(final Activity activity) {
		String title = "选择图片方式";
		String[] items = new String[] { "相机", "相册", "取消" };
		new AlertDialog.Builder(activity).setTitle(title)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							pickImageFromCamera(activity);
							break;
						case 1:
							album(activity);
							break;
						default:
							break;
						}
					}
				}).show();
	}

	/**
	 * ��������ջ�ȡͼƬ
	 */
	public static void pickImageFromCamera(final Activity activity) {
		String path = FilePathUtils.setMkdir(activity.getApplicationContext());
		File file = new File(path, System.currentTimeMillis() + ".jpg");
		imageUriFromCamera = Uri.fromFile(file);

		Intent intent2 = new Intent();
		intent2.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
		activity.startActivityForResult(intent2, REQUEST_CODE_FROM_CAMERA);
	}

	public static void photo(Activity activity) {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		activity.startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	/**
	 * ɾ��һ��ͼƬ
	 */
	public static void deleteImageUri(Context context, Uri uri) {
		context.getContentResolver().delete(imageUriFromCamera, null, null);
	}

	/**
	 * �򿪱������ѡȡͼƬ
	 */
	private static void album(Activity activity) {
		Intent openAbumlIntent = new Intent(activity, Album.class);
		activity.startActivity(openAbumlIntent);
	}
	/**
	 * ���ܣ�ʹ��camera���պ󣬵õ�����Ƭ�ᱻ�Զ���ת��ԭ����Ƭ�������Ǵ洢����ת��Ϣ
	 * �����������onActivityResult�����У���ȡ����Ƭ���ݺ󣬶�ȡ������ת��Ϣ
	 * */
	/**
	 * ��ȡͼƬ����ת�ĽǶ�
	 * @param path
	 * @return ͼƬ����ת�Ƕ�
	 * */
	private static int getBitmapDegree(String path){
		int degree = 0;
		//��ָ��·���¶�ȡͼƬ������ȡ��exif��Ϣ
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			//��ȡͼƬ����ת��Ϣ
			int torientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (torientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;			
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			default:
				degree = 0;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	/**
	 * ���ܣ���ͼƬ����ĳ���ǶȽ�����ת
	 * @param path ͼƬ·��
	 * @return ��ת�ɹ����  success�ɹ� failure ʧ��  
	 * */
	public static String rotateBitmapByDegree(String path,final Activity activity){
		String ratateSta = FAILURE_STA;
		Bitmap inImg = StringToBitmap.StringToBitmap(path, activity.getWindowManager(), 1);
		Matrix matrix = new Matrix();
		int degree = getBitmapDegree(path);
		matrix.postRotate(degree);
		Bitmap retatoImg = Bitmap.createBitmap(inImg, 0, 0, inImg.getWidth(), inImg.getHeight(), matrix, true);
		File newfile = new File(path);
		if(newfile.exists()){
			newfile.delete();
		}
		ratateSta = StringToBitmap.saveImgTopath(path, retatoImg);
		return ratateSta;
	}
	/**
	 * ��װ����Gallery �� intent
	 * */
	public static Intent getPhotoPickIntent(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outPutX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}
	/**
	 * ����Gallery ����
	 * */
	public static void doPickPhotoFromGallery(Activity activity){
//		Intent intent = getPhotoPickIntent();
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		activity.startActivityForResult(intent,REQUEST_CODE_FROM_ALBUM_GALLERY);
	}
	/**
	 * ���ܣ���һ��ͼƬ�ľ���·��ת���ɳ���ָ����С��ͼƬ �ȱ� ����ͼƬ
	 * 
	 * @param inBmpPath
	 *            ͼƬ����·��
	 * @param width
	 *            height ָ����С��ͼƬ
	 * */
	public static Bitmap StringToBitmap(Bitmap inBmpPath, int width, int height) {

		Bitmap bmp;
		Matrix matrix = new Matrix();
		matrix.postScale(1, 1);
		// ͼƬ����ת��Bitmap��ʽ
		Bitmap bitmap = inBmpPath;// ��ʾ ����Ļ�߶�/3
		if (width == 0 && height == 0)
			return bitmap;
		else if (width == 0) {
			// ��ָ�������ߵȱ�����
			int dh = height; // ��ȡ��Ļ�߶�
			// ��ͼƬ�ĸ߶Ƚ��ж�Ӧ�ֻ���Ļ

			int hRatio = (int) Math.ceil(inBmpPath.getHeight() / (float) dh);
			if (hRatio > 1) {
				matrix.postScale(hRatio, hRatio);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} else if (height == 0) {

			int dw = width;
			// ��ͼƬ�ĸ߶Ƚ��ж�Ӧ�ֻ���Ļ
			int wRatio = (int) Math.ceil(inBmpPath.getWidth() / (float) dw);
			if (wRatio > 1) {
				matrix.postScale(wRatio, wRatio);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} else {
			// �����ų߶ȴ�� �ȱ� ����
			int dw = width;
			int dh = height;

			// ��ͼƬ�ĸ߶Ƚ��ж�Ӧ�ֻ���Ļ
			int wRatio = (int) Math.ceil(inBmpPath.getWidth() / (float) dw);
			int hRatio = (int) Math.ceil(inBmpPath.getHeight() / (float) dh);
			if (wRatio > 1 || hRatio > 1) {
				int scale = wRatio > hRatio ? wRatio : hRatio;
				matrix.postScale(scale, scale);
			}
			bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}
		return bmp;
	}

	/**
	 * ���ܣ���һ��ͼƬ�ľ���·��ת����ͼƬ
	 * 
	 * @param
	 * 
	 * */
	public static Bitmap StringToBitmap(String inBmpPath, int heightWD,
			int widthWD) {

		// ͼƬ����ת��Bitmap��ʽ
		/*
		 * Ϊ��֤ͼƬ����̫�󳬹�"16M"����Ҫ��ͼƬ�������� ����: 1.��ȡͼƬ��ͷ��Ϣ������������ȡͼƬ 2.ѹ��ͼƬ����Ļ���
		 * 3.�ٰ�ѹ�����ͼƬ�����ڴ�
		 */
		// ����ֻ���Ļ���
		// 1.��ȡͼƬ��ͷ��Ϣ������������ȡͼƬ
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(inBmpPath, opts);
		int imgHeight = opts.outHeight;
		int imgWidth = opts.outWidth;
		// 2.����ѹ���� ѹ��ͼƬ����Ļ���
		double scaleX = (imgWidth * 1.0) / widthWD;
		double scaleY = (imgHeight * 1.0) / heightWD;
		double scale = 1.0;
		if (scaleX >= scaleY & scaleY >= 1) {
			scale = scaleX;
		} else if (scaleY > scaleX & scaleX >= 1) {
			scale = scaleY;
		}
		// ������ȡͼƬ
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = (int) Math.ceil(scale);// ����ȡ��
		Bitmap bmpSmall = BitmapFactory.decodeFile(inBmpPath, opts);
		return bmpSmall;
	}
}
