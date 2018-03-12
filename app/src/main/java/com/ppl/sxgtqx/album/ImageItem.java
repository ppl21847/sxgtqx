package com.ppl.sxgtqx.album;

import android.graphics.Bitmap;

/**
 * һ��ͼƬ����
 * 
 * @author Administrator
 * 
 */
public class ImageItem {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public Bitmap bmp;
	public boolean isSelected = false;
	
	public ImageItem() {
		super();
	}
	public ImageItem(String imagePath) {
		super();
		this.imagePath = imagePath;
	}
	public Bitmap getBmp() {
		return bmp;
	}
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
