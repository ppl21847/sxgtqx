package com.ppl.sxgtqx.album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.activity.NewSubLoc;


public class ImageGridAdapter extends BaseAdapter {

	final String TAG = getClass().getSimpleName();
	Activity act;
	public static List<ImageItem> selectDataList;
	Map<String, String> map = new HashMap<String, String>();
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

	public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
		this.act = act;
		selectDataList = list;
		cache = new BitmapCache();
	}
	@Override
	public int getCount() {
		int count = 0;
		if (selectDataList != null) {
			count = selectDataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return selectDataList.get(selectDataList.size()-1 - position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	class Holder {
		private ImageView iv;
		private ImageView selected;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_ablum_select, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.item_ablum_img);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.item_isSelected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = (ImageItem) getItem(position);

		boolean selectSta = false;
		for(int i = 0; i< NewSubLoc.arrySelectImg.size(); i++){
			if(item.getImagePath().equals(NewSubLoc.arrySelectImg.get(i).getImagePath())){
				selectSta = true;
				break;
			}
		}

		@SuppressWarnings("deprecation")
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				act.getWindowManager().getDefaultDisplay().getWidth()/3, 
				act.getWindowManager().getDefaultDisplay().getWidth()/3);
		holder.iv.setLayoutParams(params);

		holder.iv.setTag(item.imagePath);
		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
				callback);

		if(selectSta){
			holder.selected.setImageResource(R.drawable.account_current);
			holder.selected.setBackgroundResource(R.drawable.ablum_select_photo_selected);
		}else{
			holder.selected.setImageResource(R.drawable.ablum_select_photo_normal);
			holder.selected.setBackgroundResource(R.drawable.ablum_select_photo_normal);
		}
		return convertView;
	}

}
