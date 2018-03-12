package com.ppl.sxgtqx.album;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.activity.NewSubLoc;
import com.ppl.sxgtqx.com.loopj.android.image.SmartImageView;
import com.ppl.sxgtqx.view.MyGridView;


public class PicAdapter extends BaseAdapter {
	private int selectedPosition = -1;
	private boolean shape;
	private Activity activity ;
	private Context context;
	private MyGridView gv;
	private List<ImageItem> dataList;
	
	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}
	
	
	
	public PicAdapter(Activity activity, Context context,MyGridView gv,
			List<ImageItem> dataList) {
		super();
		this.activity = activity;
		this.context = context;
		this.gv = gv;
		this.dataList = dataList;
	}
	
	public void update() {
		loading();
	}
	
	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == dataList.size()) {
						Message message = new Message();
						message.what = PublicAlem.UPDATA_PICS_GRIDVIEW;
						NewSubLoc.handlerPics.sendMessage(message);
						break;
					} else {
						Bimp.max += 1;
						Message message = new Message();
						message.what = PublicAlem.UPDATA_PICS_GRIDVIEW;
						NewSubLoc.handlerPics.sendMessage(message);
					}
				}
			}
		}).start();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(NewSubLoc.arrySelectImg.size() == 9){
			return 9;
		}
		return (NewSubLoc.arrySelectImg.size() + 1);
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_published_grida, null);
			holder.iv_image =  (SmartImageView) convertView.findViewById(R.id.item_grida_image);
			holder.iv_delete_image = (ImageView) convertView.findViewById(R.id.iv_delete_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		int width = (gv.getWidth() - 20 
				- gv.getPaddingLeft() - gv.getPaddingRight()) / 3;
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
		holder.iv_image.setLayoutParams(params);
		if (position == dataList.size()) {
			holder.iv_image.setImageBitmap(BitmapFactory.decodeResource(
					activity.getResources(), R.drawable.icon_addpic_unfocused));
			holder.iv_delete_image.setVisibility(View.GONE);
			if (position == 9) {
				holder.iv_image.setVisibility(View.GONE);
			}
		} else {
			final int tmpPos = position;
			Bitmap bmpTmp;
			bmpTmp = StringToBitmap.StringToBitmap(NewSubLoc.arrySelectImg.get(position).imagePath, activity.getWindowManager(), 1);
			
			holder.iv_image.setImageBitmap(bmpTmp);
			holder.iv_delete_image.setVisibility(View.VISIBLE);
			holder.iv_delete_image.setOnClickListener(new OnClickListener() {
				//点击小叉 删除图片
				@Override
				public void onClick(View arg0) {
					NewSubLoc.arrySelectImg.remove(tmpPos);
					int pos = NewSubLoc.selectImgPos[tmpPos];
					if(pos >=0 && pos < AlbumDetail.dataList.size()){
						AlbumDetail.dataList.get(pos).setSelected(false);
						notifyDataSetChanged();
						AlbumDetail.adapter.notifyDataSetChanged();
					}
					
				}
			});
		}
		return convertView;
	}
	public static class ViewHolder {
		public SmartImageView iv_image;
		public ImageView iv_delete_image;
	}

}
