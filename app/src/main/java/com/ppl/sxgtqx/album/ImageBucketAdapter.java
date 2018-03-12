package com.ppl.sxgtqx.album;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppl.sxgtqx.R;

public class ImageBucketAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();

	Activity act;
	/**
	 * ͼƬ���б�
	 */
	List<ImageBucket> dataList;
	BitmapCache cache;
	
	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
//					Log.e(TAG, "callback, bmp not match");
				}
			} else {
//				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	public ImageBucketAdapter(Activity act, List<ImageBucket> list) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	class Holder {
		private ImageView iv;
		private TextView name;
	}
	
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder;
		if (arg1 == null) {
			holder = new Holder();
			arg1 = View.inflate(act, R.layout.item_ablum, null);
			holder.iv = (ImageView) arg1.findViewById(R.id.IV_ablum_Info);
			holder.name = (TextView) arg1.findViewById(R.id.TV_detail);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				act.getWindowManager().getDefaultDisplay().getWidth()/2, 
				act.getWindowManager().getDefaultDisplay().getWidth()/2);
		holder.iv.setLayoutParams(params);
		
		ImageBucket item = dataList.get(arg0);
		holder.name.setText(item.bucketName + "( "+item.count+" )");
		if (item.imageList != null && item.imageList.size() > 0) {
			String thumbPath = item.imageList.get(item.imageList.size()-1).thumbnailPath;
			String sourcePath = item.imageList.get(item.imageList.size()-1).imagePath;
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageBitmap(null);
		}
		return arg1;
	}

}
