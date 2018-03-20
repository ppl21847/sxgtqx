package com.ppl.sxgtqx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.adpter.MainSprinAdapter;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.LevelReThird;
import com.ppl.sxgtqx.utils.LevelRoot;
import com.ppl.sxgtqx.utils.LevelSecond;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.view.MyToast;

import java.util.ArrayList;
import java.util.List;

public class SelectPos extends Activity implements OnClickListener{
	private static final String TAG = "SelectPos";
	protected static final int NOTIFY_THIRD = 3;
	protected static final int NOTIFY_SECOND = 4;
	private static final int NOTIFY_RE_THIRD = 5;
	ListView lv_dlg_first,lv_dlg_second,lv_dlg_third,lv_dlg_reThird;
	List<ConnType> firstPos,secondPos,thirdPos,reThirdPos;
	MainSprinAdapter firstAdp,seceAdp,thirdAdp,reThirdAdp;
	TextView tv_select_self;
	ImageView iv_dlg_icon;
	TextView tv_dlg_title;
	LinearLayout ll_dlg_cancel;
	int type=1;			//请求类型  1-出发地   2-目的地
	MyToast myToast;
	private String secondClickId;		//点击的二级地址的ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pos_select_dlg);

		type = getIntent().getIntExtra("TYPE", 1);

		initView();
		initData();
		initListView();
		listViewItemClick();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handle.removeCallbacksAndMessages(null);
	}
	private void initView() {
		tv_select_self = (TextView) findViewById(R.id.tv_select_self);
		lv_dlg_first = (ListView) findViewById(R.id.lv_dlg_first);
		lv_dlg_second = (ListView) findViewById(R.id.lv_dlg_second);
		lv_dlg_reThird = findViewById(R.id.lv_dlg_re_third);
		lv_dlg_third  = (ListView) findViewById(R.id.lv_dlg_third);
		iv_dlg_icon = (ImageView) findViewById(R.id.iv_dlg_icon);
		tv_dlg_title = (TextView) findViewById(R.id.tv_dlg_title);
		ll_dlg_cancel =  (LinearLayout) findViewById(R.id.ll_dlg_cancel);
	}
	private void initData() {
		myToast = new MyToast(SelectPos.this, 5000, Gravity.BOTTOM);

		if(type == 1){
			iv_dlg_icon.setImageResource(R.drawable.icon_st);
			tv_dlg_title.setText("选择出发地");
		}else{
			iv_dlg_icon.setImageResource(R.drawable.icon_en);
			tv_dlg_title.setText("选择目的地");
		}
		ll_dlg_cancel.setOnClickListener(this);

		List<LevelRoot> tmpData= BNDemoMainActivity.dbHelper.getRootData();
		Log.e(TAG, "获取根目录内容数据大小:"+tmpData.size());
		firstPos = new ArrayList<ConnType>();
		for(int i=0;i<tmpData.size();i++){
			Log.e(TAG, tmpData.get(i).toString());
			firstPos.add(new ConnType(1, 0,
					tmpData.get(i).getName(),
					tmpData.get(i).getID(),1));
		}
	}
	private void initListView() {
		firstAdp = new MainSprinAdapter(firstPos, getApplicationContext());

		lv_dlg_first.setAdapter(firstAdp);
		lv_dlg_first.setVisibility(View.VISIBLE);
		lv_dlg_second.setVisibility(View.GONE);
		lv_dlg_third.setVisibility(View.GONE);
	}

	private void listViewItemClick() {
		lv_dlg_first.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				//根据当前selfId 获取下级信息
				String selfId = firstPos.get(arg2).getSelfId();
				Log.e(TAG, "点击的一级目录内容是："+firstPos.get(arg2).getConn()+",ID为： "+selfId);
				getSecondData(selfId);
				if(secondPos.size() == 0){
					Log.e(TAG, "该地址下没有候选地址");
					toast("该地址下没有候选地址");
					lv_dlg_second.setVisibility(View.GONE);
				}else{
					seceAdp = null;
					seceAdp = new MainSprinAdapter(secondPos, getApplicationContext());
					lv_dlg_second.setAdapter(seceAdp);

					Log.e(TAG, "该地址下you:"+secondPos.size()+" ge候选地址");
					lv_dlg_second.setVisibility(View.VISIBLE);
					handle.sendEmptyMessage(NOTIFY_SECOND);
				}
			}
		});
		lv_dlg_second.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				secondClickId = secondPos.get(arg2).getSelfId();
				getReThirdData();
				if(reThirdPos == null || reThirdPos.size()==0){
					Log.e(TAG, "该地址下没有候选地址");
					toast("该地址下没有候选地址");
					lv_dlg_first.setVisibility(View.VISIBLE);
					lv_dlg_reThird.setVisibility(View.GONE);
				}else{
					reThirdAdp = null;
					reThirdAdp = new MainSprinAdapter(reThirdPos,getApplicationContext());
					lv_dlg_reThird.setAdapter(reThirdAdp);
					lv_dlg_first.setVisibility(View.GONE);
					lv_dlg_reThird.setVisibility(View.VISIBLE);
					lv_dlg_third.setVisibility(View.GONE);
					handle.sendEmptyMessage(NOTIFY_RE_THIRD);
				}
			}
		});

		lv_dlg_reThird.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String selfId = secondClickId;
				Log.e(TAG, "点击的二级目录内容,ID为： "+selfId);
				getThirdData(selfId,reThirdPos.get(position));
				if(thirdPos.size() == 0){
					Log.e(TAG, "该地址下没有候选地址");
					toast("该地址下没有候选地址");
					lv_dlg_second.setVisibility(View.VISIBLE);
					lv_dlg_third.setVisibility(View.GONE);
				}else{
					thirdAdp = null;
					thirdAdp  = new MainSprinAdapter(thirdPos, getApplicationContext());
					lv_dlg_third.setAdapter(thirdAdp);
					Log.e(TAG, "该地址下you:"+thirdPos.size()+" ge候选地址");
					lv_dlg_second.setVisibility(View.GONE);
					lv_dlg_third.setVisibility(View.VISIBLE);
					handle.sendEmptyMessage(NOTIFY_THIRD);
				}
			}
		});
		lv_dlg_third.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				String selectId;
				selectId = thirdPos.get(arg2).getSelfId();
				setSelectRuslt(selectId);
			}
		});
		tv_select_self.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setSelectRuslt("0");
			}
		});
	}

	/**
	 * 获取新三级内容
	 * */
	private void getReThirdData() {
		reThirdPos = null;
		reThirdPos = new ArrayList<ConnType>();
		List<LevelReThird>sqlData = BNDemoMainActivity.dbHelper.getReThirdData();
		if(sqlData.size() == 0){
			Log.e(TAG,  "根据新三级获取到数据个数：0");
		}else{
			Log.e(TAG, "根据新三级,获取到数据个数："+sqlData.size());
			for(int i=0;i<sqlData.size();i++){
				reThirdPos.add(new ConnType(1, 3,
						sqlData.get(i).getName(),
						sqlData.get(i).getID(), 1));
				Log.e(TAG, "新添加的三级信息是： "+sqlData.get(i).toString());
			}
		}
	}

	protected void setSelectRuslt(String selectId2) {
		Intent intent =new Intent();
		if(type == 1){
			intent.putExtra("START", true);
		}else{
			intent.putExtra("START", false);
		}
		intent.putExtra("SELECTID", selectId2);
		Log.e("SelectPos", "选择"+(type == 1 ? "起点为":"终点为")+selectId2);
		this.setResult(4,intent);
		finish();
	}
	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case NOTIFY_THIRD:
					thirdAdp.notifyDataSetChanged();
					break;
				case NOTIFY_SECOND:
					seceAdp.notifyDataSetChanged();
					break;
				case NOTIFY_RE_THIRD:
					reThirdAdp.notifyDataSetChanged();
					break;
				default:
					break;
			}
		};
	};
	protected void toast(String string) {
		// TODO Auto-generated method stub
		myToast.show(string);
	}
	protected void getSecondData(String selfId) {
		secondPos = null;
		secondPos = new ArrayList<ConnType>();
		List<LevelSecond>sqlData = BNDemoMainActivity.dbHelper.getSecondData(selfId);
		if(sqlData.size() == 0){
			Log.e(TAG, "根据一级id："+selfId+",获取到数据个数：0");
		}else{
			Log.e(TAG, "根据一级id："+selfId+",获取到数据个数："+sqlData.size());
			for(int i=0;i<sqlData.size();i++){
				secondPos.add(new ConnType(1, 1, sqlData.get(i).getName(),
						sqlData.get(i).getFatherId(), sqlData.get(i).getID(), 1));
				Log.e(TAG, "新添加的二级信息是： "+sqlData.get(i).toString());
			}
		}
	}
	protected void getThirdData(String selfId, ConnType connType) {
		thirdPos = null;
		thirdPos = new ArrayList<ConnType>();
		List<LevelThird>sqlData = BNDemoMainActivity.dbHelper.getThirdData(selfId,connType.getSelfId());
		if(sqlData.size() == 0){
			Log.e(TAG,  "根据二级id："+selfId+",获取到数据个数：0");
		}else{
			Log.e(TAG, "根据二级id："+selfId+",获取到数据个数："+sqlData.size());
			for(int i=0;i<sqlData.size();i++){
				thirdPos.add(new ConnType(1, 2,
						sqlData.get(i).getName(),
						sqlData.get(i).getFatherId(),
						sqlData.get(i).getFatherReId(),
						sqlData.get(i).getID(),
						sqlData.get(i).getPosLat(),
						sqlData.get(i).getPosLong(), 1));
				Log.e(TAG, "新添加的三级信息是： "+sqlData.get(i).toString());
			}
		}
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_dlg_cancel:
				finish();
				break;

			default:
				break;
		}
	}
}
