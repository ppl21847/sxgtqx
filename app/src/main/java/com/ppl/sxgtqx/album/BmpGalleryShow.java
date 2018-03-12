package com.ppl.sxgtqx.album;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.activity.NewSubLoc;

public class BmpGalleryShow extends Activity implements OnClickListener {
	private ImageView IB_back,IB_delete;
	private TextView TV_pos;
	private ViewPager viewPager;
	//������ʾ����
	private List<View>content;
	private LayoutInflater inflater;
	private MyPagerAdapter adapter;
	private Bitmap bmp;
	private int tmpPos = -1;
	
	private ImageView IV_viewPager_isBig;
	private TextView TV_viewPager_isBig;
	
	private ImageButton IB_img_left,IB_img_right;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bmp_gallery_show);
		init();
	}
	void init(){
		IB_back = (ImageView) findViewById(R.id.ib_viewPics_back);
		IB_delete = (ImageView) findViewById(R.id.IB_showImgDelete);
		viewPager = (ViewPager) findViewById(R.id.VP_showPics);
		TV_pos = (TextView) findViewById(R.id.tv_showPics_descrip);
		
		IV_viewPager_isBig = (ImageView) findViewById(R.id.IV_viewPager_isBig);
		IV_viewPager_isBig.setOnClickListener(this);
		TV_viewPager_isBig = (TextView) findViewById(R.id.TV_viewPager_isBig);
		TV_viewPager_isBig.setOnClickListener(this);
		if(Bimp.isOrig_img_sta()){
			IV_viewPager_isBig.setImageResource(R.drawable.account_current);
			IV_viewPager_isBig.setBackgroundResource(R.drawable.ablum_select_photo_selected);
		}else{
			IV_viewPager_isBig.setImageResource(R.drawable.ablum_select_photo_normal);
			IV_viewPager_isBig.setBackgroundResource(R.drawable.ablum_select_photo_normal);
		}
		IB_img_left = (ImageButton) findViewById(R.id.IB_img_left_retato);
		IB_img_right = (ImageButton) findViewById(R.id.IB_img_right_retato);
		IB_img_left.setOnClickListener(this);
		IB_img_right.setOnClickListener(this);
		
		Intent intent= getIntent();
		int pos = intent.getIntExtra(PublicAlem.SHOW_PICS_POS, 0);
		TV_pos.setText( pos+1 + " / "+NewSubLoc.arrySelectImg.size());
		tmpPos = pos;
		inflater = LayoutInflater.from(this);
		
		content = new ArrayList<View>();
		for(int i = 0; i< NewSubLoc.arrySelectImg.size(); i++){
			View view = inflater.inflate(R.layout.item_pics_show, null);
			content.add(view);
		}
		adapter = new MyPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(pos);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		adapter.notifyDataSetChanged();
		
		IB_back.setOnClickListener(BmpGalleryShow.this);
		IB_delete.setOnClickListener(BmpGalleryShow.this);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ib_viewPics_back:
			NewSubLoc.picAdapter.notifyDataSetChanged();
			content = null;
			bmp.recycle();
			finish();
			System.gc();
			break;
		case R.id.IB_showImgDelete:
			if(tmpPos != -1){
				View view = content.get(tmpPos);
				ImageView iv = (ImageView) view.findViewById(R.id.IV_showPics);
				iv.setImageResource(R.drawable.empty_photo);
				NewSubLoc.arrySelectImg.remove(tmpPos);
				content.remove(tmpPos);
				String tmpConn = (String) TV_pos.getText();
				String a1 = tmpConn.substring(0, 1);
				String a2 = tmpConn.substring(tmpConn.length()-1, tmpConn.length());
				int b1 = Integer.parseInt(a1);
				int b2 = Integer.parseInt(a2);
				if(b2 == 1){
					content = null;
					bmp.recycle();
					finish();
					System.gc();
					return;
				}
				adapter.notifyDataSetChanged();
				
				
				if(b1 == b2){
					b1--;
				}
				TV_pos.setText(b1 +" / "+(b2-1));
			}
			break;
		case R.id.IV_viewPager_isBig:
		case R.id.TV_viewPager_isBig:
			Bimp.setOrig_img_sta(!Bimp.isOrig_img_sta());
			if(Bimp.isOrig_img_sta()){
				IV_viewPager_isBig.setImageResource(R.drawable.account_current);
				IV_viewPager_isBig.setBackgroundResource(R.drawable.ablum_select_photo_selected);
			}else{
				IV_viewPager_isBig.setImageResource(R.drawable.ablum_select_photo_normal);
				IV_viewPager_isBig.setBackgroundResource(R.drawable.ablum_select_photo_normal);
			}
			break;
		case R.id.IB_img_left_retato:
			break;
		case R.id.IB_img_right_retato:
			break;
		default:
			break;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		NewSubLoc.picAdapter.notifyDataSetChanged();
	}
	public class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}
		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			View view = content.get(position);
			ImageView iv = (ImageView) view.findViewById(R.id.IV_showPics);
			bmp = StringToBitmap.StringToBitmap(NewSubLoc.arrySelectImg.get(position).imagePath, getWindowManager(), 1);
			iv.setImageBitmap(bmp);
			((ViewPager)container).addView(view);
			return view;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return NewSubLoc.arrySelectImg.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return (arg0 == arg1);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
//			super.destroyItem(container, position, object);
			if(position < NewSubLoc.arrySelectImg.size()){
				((ViewPager)container).removeView(content.get(position));
			}
		}
	}
	//public class MyOnPageChangeListener implements OnPageChangeListener
	private class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		//ֻ��ҳ�滬��ʱ��ִ��
		@Override
		public void onPageSelected(int arg0) {
			tmpPos = arg0; 
			int tmpPosition = arg0+1;
			TV_pos.setText(tmpPosition + " / "+NewSubLoc.arrySelectImg.size());
		}
		
	}
}
