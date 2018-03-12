package com.ppl.sxgtqx.adpter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.com.loopj.android.image.SmartImageView;


public class ShowImgAdapter extends BaseAdapter {
	Context mContext;
	List<String>data;
	LayoutInflater layoutInflater;

	public ShowImgAdapter(Context mContext, List<String> data) {
		super();
		this.mContext = mContext;
		this.data = data;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if(data.size() == 0){
			size = 1;
		}else{
			size = data.size();
		}
		return size;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		viewHolder holder = null;
		if(convertView == null){
			holder = new viewHolder();
			convertView = layoutInflater.inflate(R.layout.item_show_img, null);
			holder.img = (SmartImageView) convertView.findViewById(R.id.iv_item_show_img);
			convertView.setTag(holder);
		}else{
			holder = (viewHolder)convertView.getTag();
		}
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int width2 = (width - 20 - 8 - 8) / 3;
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width2, width2);
		holder.img.setLayoutParams(param);
		
		if(data.size() == 0){
			holder.img.setImageResource(R.drawable.empty_photo);
		}else{
			holder.img.setImageUrl(data.get(arg0));
		}
		
		return convertView;
	}
	private class viewHolder{
		SmartImageView img;
	}

}
