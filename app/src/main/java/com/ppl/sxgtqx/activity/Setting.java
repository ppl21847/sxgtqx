package com.ppl.sxgtqx.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.adpter.CityAdapter;
import com.ppl.sxgtqx.adpter.MainSprinAdapter;
import com.ppl.sxgtqx.adpter.SetAdpt;
import com.ppl.sxgtqx.application.LocationApplication;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.InitData;
import com.ppl.sxgtqx.utils.LevelRoot;
import com.ppl.sxgtqx.utils.LevelSecond;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.utils.MapCityInfo;
import com.ppl.sxgtqx.utils.SetData;
import com.ppl.sxgtqx.view.ListViewForScrollView;
import com.ppl.sxgtqx.view.MyToast;

public class Setting extends Activity implements OnClickListener,MKOfflineMapListener{
	private static final String TAG = "Setting";
	private static final int UPDATA_ADAPTER = 3;
	private static final int DELETE_FILUE = 4;
	protected static final int SHOW_TOAST = 5;

	private SharedPreferences sp;//	保存管理员账号密码

	ImageButton ib_set_back;
	ListViewForScrollView lv_set_conn;
	SetAdpt adapter;
	List<SetData>data;
	LinearLayout ll_set_conn;
	TextView tv_set_select_titel;

	//地址管理 
	LinearLayout ll_pos_manage;
	TextView tv_level_frist,tv_level_second;
	ListViewForScrollView lv_pos_manage;
	MainSprinAdapter adapterPos;
	List<ConnType>fristData,secondData;
	List<ConnType>posData;
	String fatherIdFirst,fatherIdSecond;

	//离线下载城市
	List<MapCityInfo>cityData;
	CityAdapter cityAdapter;
	ListViewForScrollView lv_map_contry;
	private MKOfflineMap mOffline = null;		//离线地图
	TextView tv_load;
	ImageView iv_sta;
	MyToast myToast;

	//三级地址显示状态，只有当当前值为1的时候后退才推出该页面
	int tmpPos = 1;

	//登录状态
	ImageView iv_user_nic;
	TextView tv_login_sta,tv_login_disc;
	Button bt_login;
	ScrollView sv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_setting_new);

		initView();
		initData();
		initLoginShow();
		initCity();
		initPos();
		initPosClick();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
	private void initView() {
		ib_set_back = (ImageButton) findViewById(R.id.ib_set_back);
		lv_set_conn = (ListViewForScrollView) findViewById(R.id.lv_set_conn);
		ll_set_conn = (LinearLayout) findViewById(R.id.ll_set_conn);
		tv_set_select_titel = (TextView) findViewById(R.id.tv_set_select_titel);
		lv_map_contry = (ListViewForScrollView) findViewById(R.id.lv_map_contry);
		ll_pos_manage = (LinearLayout) findViewById(R.id.ll_pos_manage);
		tv_level_frist = (TextView) findViewById(R.id.tv_level_frist);
		tv_level_second = (TextView) findViewById(R.id.tv_level_second);
		lv_pos_manage = (ListViewForScrollView) findViewById(R.id.lv_pos_manage);

		iv_user_nic = (ImageView) findViewById(R.id.iv_user_nic);
		tv_login_sta = (TextView) findViewById(R.id.tv_login_sta);
		tv_login_disc = (TextView) findViewById(R.id.tv_login_disc);
		bt_login = (Button) findViewById(R.id.bt_login);

		sv = (ScrollView) findViewById(R.id.sv);
	}

	/**
	 * 根据登录状态显示界面
	 * */
	private void initLoginShow() {
		if(LocationApplication.adminLoginSta){
			iv_user_nic.setImageResource(R.drawable.login);
			tv_login_sta.setText("管理员");
			tv_login_disc.setText("可以对地点进行管理");
			bt_login.setText("退出");
		}else{
			iv_user_nic.setImageResource(R.drawable.user_center_default_head);
			tv_login_sta.setText("未登录");
			tv_login_disc.setText("登录后才能对地点进行管理");
			bt_login.setText("登录");
		}

		bt_login.setOnClickListener(this);
	}

	private void initData() {
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		sv.smoothScrollTo(0, 0);

		myToast = new MyToast(Setting.this, 5000, Gravity.BOTTOM);
		tv_level_frist.setText("一级地址");
		tv_level_second.setText("二级地址");
		ib_set_back.setOnClickListener(this);
		tv_level_frist.setOnClickListener(this);
		tv_level_second.setOnClickListener(this);

		data = InitData.initSetData();
		adapter = new SetAdpt(data, getApplicationContext());
		lv_set_conn.setAdapter(adapter);

		lv_set_conn.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int arg2,
									long arg3) {
				for(int i=0;i<parent.getCount();i++){
					View v=parent.getChildAt(i);
					v.setBackgroundColor(Color.WHITE);
				}
				if(arg2 == 0){
					tmpPos = 1;
					ll_set_conn.setVisibility(View.VISIBLE);
					tv_set_select_titel.setText("城市列表");
					lv_map_contry.setVisibility(View.VISIBLE);
					ll_pos_manage.setVisibility(View.GONE);
				}else{
					ll_pos_manage.setVisibility(View.VISIBLE);
					ll_set_conn.setVisibility(View.GONE);
					lv_map_contry.setVisibility(View.GONE);
				}
			}
		});
	}
	private void initCity() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);

		cityData = new ArrayList<MapCityInfo>();
		System.out.println("cityData.size()================>>>>>>>>>>>>:"+cityData.size());
		// 获取所有支持离线地图的城市
		ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
		for (MKOLSearchRecord r : records2) {
			cityData.add(new MapCityInfo(r.cityName, false,r.cityID, this.formatDataSize(r.size)));
		}
		System.out.println("cityData.size()================>>>>>>>>>>>>:"+cityData.size());
		// 获取已下过的离线地图信息
		ArrayList<MKOLUpdateElement>localMapList = mOffline.getAllUpdateInfo();
		if(localMapList != null && cityData.size() != 0){
			for(int i=0;i<localMapList.size();i++){
				for(int j=0;j<cityData.size();j++){
					if(cityData.get(j).getCityId() == localMapList.get(i).cityID){
						cityData.get(j).setEixt(true);
						continue;
					}
				}
			}
		}
		System.out.println("cityData.size()================>>>>>>>>>>>>:"+cityData.size());
		cityAdapter = new CityAdapter(cityData, getApplicationContext(),cityListener);
		lv_map_contry.setAdapter(cityAdapter);
		lv_map_contry.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int arg2,
									long arg3) {
				tv_load = (TextView) view.findViewById(R.id.tv_downLoad);
				iv_sta = (ImageView) view.findViewById(R.id.iv_dwon_sta);
			}
		});
	}
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	/**
	 * 初始化位置
	 * */
	private void initPos() {
		List<LevelRoot>rootData = BNDemoMainActivity.dbHelper.getRootData();
		posData = new ArrayList<ConnType>();
		//从本地数据哭中获取一级根目录内容
		for(int i=0;i<rootData.size();i++){
			Log.e(TAG, rootData.get(i).toString());
			posData.add(new ConnType(1, 0,
					rootData.get(i).getName(),rootData.get(i).getID(),1));
		}
		posData.add(new ConnType(2, 0, "添加", "",1));

		fristData = null;
		fristData = new ArrayList<ConnType>();
		for(int i=0;i<posData.size();i++){
			fristData.add(posData.get(i));
		}

		adapterPos = new MainSprinAdapter(posData, Setting.this,1,mListener);
		lv_pos_manage.setAdapter(adapterPos);
	}
	private void initPosClick() {
		lv_pos_manage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int arg2,
									long arg3) {
				Log.e(TAG, "点击了第： "+arg2 +" 条数据");
				ConnType clickData = posData.get(arg2);

				if(arg2 == (posData.size()-1)){
					if(LocationApplication.adminLoginSta){
						addOrEditNew(clickData,arg2);
					}

					Log.e(TAG, "点击了 添加按钮");
					Log.e(TAG, "clickData :"+clickData.toString());
					//点击了 添加
					if(clickData.getLevel() != 2){
						editOrAdd(clickData,true,arg2);
					}else{
						Intent intent = new Intent(getApplicationContext(), NewSubLoc.class);
						intent.putExtra("NEW", true);
						Log.e(TAG, "父级ID: "+fatherIdSecond);
						intent.putExtra("fatherId", fatherIdSecond);
						startActivity(intent);
					}

				}
			}


		});
	}

	private void addOrEditNew(ConnType clickData, int arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * 实现类，城市下载响应按钮点击事件
	 */
	private CityAdapter.CityClickListener cityListener = new CityAdapter.CityClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			View parant = (View) v.getParent();
			tv_load = (TextView) parant.findViewById(R.id.tv_downLoad);
			iv_sta = (ImageView) parant.findViewById(R.id.iv_dwon_sta);
			if(v.getId() == R.id.iv_dwon_sta){
				if(cityData.get(position).isEixt()){
					//删除
					//已下载   点击删除
					mOffline.remove(cityData.get(position).getCityId());
					System.out.println("删除==========="+cityData.get(position).getName());
					Log.e(TAG, "删除==========="+cityData.get(position).getName());
					cityData.get(position).setEixt(false);
					cityAdapter.notifyDataSetChanged();
				}else{
					//下载
					//未下载  点击下载离线包
					tv_load.setVisibility(View.VISIBLE);
					mOffline.start(cityData.get(position).getCityId());
					System.out.println("下载----------------------"+cityData.get(position).getName());
					myToast.show("开始下载离线地图" + cityData.get(position).getName());
				}
			}
		}
	};

	/*
	 * 登录认证
	 * */
	private void LoginStaAuth(final int position, View v){

		managePos(position,v);
	}

	protected void managePos(int position, View v) {
		// TODO Auto-generated method stub
		Log.e(TAG, "点击了其他: "+posData.get(position).toString());
		if(posData.get(position).getLevel() == 0){
			fatherIdFirst = posData.get(position).getSelfId();
			Log.e(TAG,"点击的一级：Id: "+ fatherIdFirst+",内容： "+posData.get(position).getConn());
		}else if(posData.get(position).getLevel() == 1){
			fatherIdSecond = posData.get(position).getSelfId();
			Log.e(TAG,"点击的二级：Id: "+ fatherIdSecond+",内容： "+posData.get(position).getConn());
		}
		switch (v.getId()) {
			case R.id.ib_item_add:
				Log.e(TAG, "点击了删除按钮");
				//删除该条信息
				deleteItem(posData.get(position),position);
				break;
			case R.id.ib_next:
				Log.e(TAG, "点击了编辑按钮");


				editItem(posData.get(position),position);
				break;
			case R.id.tv_sub_selcet_conn:
				Log.e(TAG, "点击了文字查看下一条数据");
				if(posData.get(position).getLevel() == 0){
					//根级请求二级
					tv_level_frist.setText(posData.get(position).getConn());
					tv_level_second.setText("二级目录");
					tv_level_second.setVisibility(View.VISIBLE);
				}else{
					tv_level_second.setText("二级目录");
					tv_level_second.setVisibility(View.VISIBLE);
					tv_level_second.setText(posData.get(position).getConn());
				}
				selectNextData(posData.get(position),position);
				break;
			default:
				break;
		}
	}
	/**
	 * 实现类，地点管理响应按钮点击事件
	 */
	private MainSprinAdapter.MyClickListener mListener = new MainSprinAdapter.MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			if(LocationApplication.adminLoginSta){
				managePos(position,v);
			}else{
				LoginStaAuth(position,v);
			}
		}
	};


	/**
	 * 根据点击的id获取下级数据
	 * */
	private void selectNextData(ConnType clickData, int arg2) {
		Log.e(TAG, "------------>>>>>点击信息："+clickData.toString());
		if(clickData.getLevel() == 0){
			//根据根级目录Id 请求 二级目录
			Log.e(TAG, "根据根级目录Id "+clickData.getSelfId()+"请求 二级目录");
			getSecondData(clickData.getSelfId());
		}else if(clickData.getLevel() == 1){
			Log.e(TAG, "根据二级目录Id "+clickData.getSelfId()+"请求 二级目录");
			getThirdData(clickData.getSelfId());
		}else if(clickData.getLevel() == 2){
			Log.e(TAG, "编辑三级地址： "+clickData.getConn());
			goToThirdEdit(clickData.getSelfId());
		}
	}
	private void getThirdData(String selfId) {
		Log.e(TAG, "根据二级Id："+selfId+",请求三级数据");
		List<LevelThird>tmpData = BNDemoMainActivity.dbHelper.getThirdData(selfId);
		posData = null;
		posData = new ArrayList<ConnType>();
		if(tmpData.size() > 0){
			for(int i=0;i<tmpData.size();i++){
				Log.e(TAG, "根据二级目录Id: "+selfId+ " 获取到的三级目录信息的第： "+i+" 条信息是： "+tmpData.get(i).toString());
				posData.add(new ConnType(1, 2,
						tmpData.get(i).getName(), tmpData.get(i).getFatherId(),tmpData.get(i).getFatherReId(),
						tmpData.get(i).getID(), tmpData.get(i).getPosLat(),
						tmpData.get(i).getPosLong(), 1));
			}
		}else{
			Log.e(TAG, "根据二级Id："+selfId+",请求三级数据,-----------没有数据");
			Message msg = new Message();
			msg.what = SHOW_TOAST;
			msg.obj = "该目录下没有地点";
			handler.sendMessage(msg);
		}
		tmpPos = 3;
		posData.add(new ConnType(2, 2,  "添加", fatherIdSecond,"", "", 0, 0, 1));
		adapterPos = null;
		adapterPos = new MainSprinAdapter(posData, Setting.this,1,mListener);
		lv_pos_manage.setAdapter(adapterPos);
		handler.sendEmptyMessage(UPDATA_ADAPTER);
	}
	private void getSecondData(String selfId) {
		Log.e(TAG, "进入了请求二级目录。一级Id： "+selfId);
		List<LevelSecond>tmpData = BNDemoMainActivity.dbHelper.getSecondData(selfId);

		Log.e(TAG, "获得的二级目录大小是： "+tmpData.size());
		posData = null;
		posData = new ArrayList<ConnType>();
		if(tmpData.size() > 0){
			for(int i=0;i<tmpData.size();i++){
				Log.e(TAG, "根据根目录Id: "+selfId+ " 获取到的二级目录信息的第： "+i+" 条信息是： "+tmpData.get(i).toString());
				posData.add(new ConnType(1, 1,
						tmpData.get(i).getName(),tmpData.get(i).getFatherId(),
						tmpData.get(i).getID(), 1));
			}
		}else{
			Log.e(TAG, "该目录下没有地点");
			Message msg = new Message();
			msg.what = SHOW_TOAST;
			msg.obj = "该目录下没有地点";
			handler.sendMessage(msg);
		}
		posData.add(new ConnType(2, 1, "添加", fatherIdFirst, "", 1));
		tmpPos = 2;

		secondData = null;
		secondData = new ArrayList<ConnType>();
		for(int i=0;i<posData.size();i++){
			secondData.add(posData.get(i));
		}

		adapterPos = null;
		adapterPos = new MainSprinAdapter(posData, Setting.this,1,mListener);
		lv_pos_manage.setAdapter(adapterPos);
		handler.sendEmptyMessage(UPDATA_ADAPTER);
	}
	private void getRootData() {
		posData = null;
		posData = new ArrayList<ConnType>();
		List<LevelRoot>tmpData = BNDemoMainActivity.dbHelper.getRootData();
		if(tmpData.size() > 0){
			posData.clear();
			for(int i=0;i<tmpData.size();i++){
				Log.e(TAG, "获取到的根级目录信息的第： "+i+" 条信息是： "+tmpData.get(i).toString());
				posData.add(new ConnType(1, 0, tmpData.get(i).getName(), tmpData.get(i).getID(), 1));
			}
			posData.add(new ConnType(2, 0, "添加", "",1));
			adapterPos = null;
			adapterPos = new MainSprinAdapter(posData, Setting.this,1,mListener);
			lv_pos_manage.setAdapter(adapterPos);
			handler.sendEmptyMessage(UPDATA_ADAPTER);
		}else{
			Message msg = new Message();
			msg.what = SHOW_TOAST;
			msg.obj = "该目录下没有地点";
			handler.sendMessage(msg);
		}
	}

	private void editItem(ConnType clickData, int arg2) {
		if(clickData.getLevel() == 2){
			goToThirdEdit(clickData.getSelfId());
		}else{
			editOrAdd(clickData,false,arg2);
		}
	}
	private void goToThirdEdit(String selfId) {
		Intent intent = new Intent(getApplicationContext(), NewSubLoc.class);
		intent.putExtra("NEW", false);
		intent.putExtra("SELFID", selfId);
		intent.putExtra("LOCALIMG", "noPic");
		startActivity(intent);
	}

	private void editOrAdd(final ConnType clickData,final boolean addSta,final int arg2) {
		showAddOrEdit(clickData,addSta,arg2);
	}
	private void showAddOrEdit(final ConnType clickData, final boolean addSta, final int arg2) {
		String fatherConn = "";
		final int level = clickData.getLevel();
		if(level == 1){
			fatherConn = tv_level_frist.getText().toString().trim();
			System.out.println("level:"+level+",fatherConn:"+fatherConn);
			Log.e(TAG, "level:"+level+",fatherConn:"+fatherConn);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(Setting.this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_edit_add);

		TextView tv_title =  (TextView) window.findViewById(R.id.tv_dialog_title);
		final EditText ed_conn = (EditText) window.findViewById(R.id.et_level_conn);
		Button dialog_cancel = (Button) window.findViewById(R.id.dialog_cancel);
		Button dialog_confirm = (Button) window.findViewById(R.id.dialog_confirm);

		dialog_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取消
				alertDialog.dismiss();
			}
		});
		dialog_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//确定
				final String conn = ed_conn.getText().toString().trim();
				if(conn.length()>0){
					if(level == 0){
						Log.e(TAG, "根级目录: "+conn);
						addOrEditFirst(conn,clickData,addSta,arg2);
					}else{
						Log.e(TAG, "二级目录，操作: "+conn);
						addOrEditSecond(conn,clickData,addSta,arg2);
					}
					alertDialog.dismiss();
				}
				else{
					Toast.makeText(getApplicationContext(), "输入内容不能为空！", Toast.LENGTH_LONG).show();
				}
			}
		});


		if(addSta){
			Log.e(TAG, "添加");
			Drawable drawable= getResources().getDrawable(R.drawable.icon_bulue_bar_newshop);
			/// 这一步必须要做,否则不会显示.  
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_title.setCompoundDrawables(drawable,null,null,null);

			if(level == 0){
				Log.e(TAG, "一级");
				tv_title.setText("添加");//设置标题内容
			}else{
				Log.e(TAG, "二级，"+"在"+fatherConn+"下添加");
				tv_title.setText("在"+fatherConn+"下添加");//设置标题内容
			}
		}else{
			Log.e(TAG, "编辑： "+clickData.getConn()+",他呀");
			ed_conn.setText(clickData.getConn());

			Log.e(TAG, "编辑");
			Drawable drawable= getResources().getDrawable(R.drawable.extra_info_edit);
			/// 这一步必须要做,否则不会显示.  
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_title.setCompoundDrawables(drawable,null,null,null);

			if(level == 0){
				Log.e(TAG, "一级");
				tv_title.setText("编辑");//设置标题内容
			}else{
				Log.e(TAG, "二级，"+"在"+fatherConn+"下编辑");
				tv_title.setText("在"+fatherConn+"下编辑");//设置标题内容
			}
		}
	}

	private void addOrEditSecond(String conn, ConnType clickData,
								 boolean addSta, int arg2) {
		clickData.setConn(conn);
		LevelSecond tmp = new LevelSecond();
		tmp.setFatherId(clickData.getFatherId());
		tmp.setID(clickData.getSelfId());
		tmp.setName(conn);
		tmp.setLevel(1);
		tmp.setDelSta(1);
		if(addSta){
			Log.e(TAG, "添加二级目："+conn);
			saveNewSecond(tmp,conn);
		}else{
			Log.e(TAG, "编辑二级目："+conn);
			updataSecond(clickData,arg2);
		}
	}

	protected void addOrEditFirst(String conn, ConnType clickData,
								  boolean addSta,int arg2) {
		final LevelRoot tmp = new LevelRoot();
		tmp.setID(clickData.getSelfId());
		tmp.setName(conn);
		tmp.setLevel(0);
		tmp.setDelSta(1);
		if(addSta){
			Log.e(TAG, "添加一级目："+conn);
			saveNewFirst(tmp,conn);
		}else{
			Log.e(TAG, "编辑一级目："+conn);
			editFirst(tmp,arg2);
		}
	}

	private void updataSecond(final ConnType clickData, final int arg2) {
		Log.e(TAG, "更新二级目录信息："+clickData.toString());
		LevelSecond updata = new LevelSecond();
		updata.setValue("fatherId", clickData.getFatherId());
		updata.setValue("name", clickData.getConn());
		updata.setValue("delSta", 1);
		updata.setValue("level", 1);
		String objId = clickData.getSelfId();
		Log.e(TAG, "修改的Id： "+objId);

		updata.update(objId, new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e == null){
					posData.get(arg2).setConn(clickData.getConn());
					BNDemoMainActivity.dbHelper.updataSecondInfo(new LevelSecond(clickData.getFatherId(),
							clickData.getSelfId(), clickData.getConn(), 1, 1));
					handler.sendEmptyMessage(UPDATA_ADAPTER);
					msg.obj = "更新二级目录："+clickData.getConn()+" 成功";
					Log.e(TAG, "更新二级目录--------->>>>>>>>>>>>："+clickData.getConn()+" 成功");
				}else{
					Log.e(TAG, "更新二级目录：>>>>-------"+clickData.getConn()+" 失败!"+e);
					msg.obj = "更新二级目录："+clickData.getConn()+" 失败!";
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}
	/**
	 * 编辑更新一级目录
	 * */
	private void editFirst(final LevelRoot tmp,final int arg2) {
		Log.e(TAG, "更新一级目录："+tmp.toString());
		String upselfId = tmp.getID();
		Log.e(TAG, "更新Id: "+upselfId);
		LevelRoot upData = new LevelRoot();
		upData.setValue("name", tmp.getName());
		upData.setValue("delSta", 1);
		upData.setValue("level", 0);
		upData.update(upselfId, new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e == null){
					Log.e(TAG, "更新一级目录："+tmp.getName()+" 成功");
					posData.get(arg2).setConn(tmp.getName());
					BNDemoMainActivity.dbHelper.updataFirstInfo(tmp);
					handler.sendEmptyMessage(UPDATA_ADAPTER);
					msg.obj = "更新一级目录："+tmp.getName()+" 成功";
				}else{
					Log.e(TAG, "更新一级目录："+tmp.getName()+" 失败!");
					msg.obj = "更新一级目录："+tmp.getName()+" 失败!";
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}

	private void saveNewSecond(final LevelSecond tmp, String conn) {
		LevelSecond newSaveData = new LevelSecond();
		newSaveData.setFatherId(fatherIdFirst);
		newSaveData.setDelSta(1);
		newSaveData.setLevel(1);
		newSaveData.setName(tmp.getName());
		newSaveData.save(new SaveListener<String>() {
			@Override
			public void done(String arg0, BmobException e) {
				Message msg = new Message();
				if(e==null){
					tmp.setID(arg0);
					BNDemoMainActivity.dbHelper.addSecond(tmp);
					posData.add(0,new ConnType(1, 1, tmp.getName(), tmp.getFatherId(), arg0, 1));
					msg.obj = "添加二级目录: "+tmp.getName()+" 成功！";
					handler.sendEmptyMessage(UPDATA_ADAPTER);
					Log.e(TAG, "添加二级目录成功！："+tmp.getName());
				}else{
					msg.obj = "创建数据失败：" + e.getMessage();
					Log.e(TAG, "添加二级目录失败！："+"创建数据失败：" + e.getMessage());
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}
	private void saveNewFirst(final LevelRoot tmp,final String conn) {
		LevelRoot newSavaData = new LevelRoot();
		newSavaData.setLevel(0);
		newSavaData.setDelSta(1);
		newSavaData.setName(tmp.getName());
		tmp.setID("");
		tmp.setLevel(0);
		tmp.setDelSta(1);
		newSavaData.save(new SaveListener<String>() {
			@Override
			public void done(String objectId,BmobException e) {
				Message msg = new Message();
				if(e==null){
					tmp.setID(objectId);
					BNDemoMainActivity.dbHelper.addFirst(tmp);		//本地数据库添加
					posData.add(0, new ConnType(1, 0, tmp.getName(), objectId, 1));		//更改适配器显示
					msg.obj = "添加根级目录: "+conn+" 成功！";
					handler.sendEmptyMessage(UPDATA_ADAPTER);
					Log.e(TAG, "添加一级目录成功！："+conn);
				}else{
					System.out.println("创建数据失败：" + e.getMessage());
					msg.obj = "创建数据失败：" + e.getMessage();
					Log.e(TAG, "添加一级目录失败！："+"创建数据失败：" + e.getMessage());
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}

	/*
	 * 删除节点
	 */
	private void deleteItem(final ConnType clickData,final int position) {
		deleteAfterAuthItem(clickData,position);
	}

	private void deleteAfterAuthItem(final ConnType clickData,final int position) {
		// TODO Auto-generated method stub
		String msgShow;
		if(clickData.getLevel() == 0){
			//删除根级目录
			msgShow = clickData.getConn();
		}else if(clickData.getLevel() == 1){
			msgShow = tv_level_frist.getText().toString().trim()+"\n"+clickData.getConn();
		}else{
			msgShow = tv_level_frist.getText().toString().trim()+"\n"+tv_level_second.getText().toString().trim()+"\n"+clickData.getConn();
		}
		Dialog ad = new AlertDialog.Builder(this)
				.setTitle("确认删除该条信息？")
				.setIcon(R.drawable.webshell_notification_warning)
				.setMessage(msgShow)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						/**
						 * 删除根级目录信息
						 * */
						if(clickData.getLevel() == 0){
							deleteFirst(clickData,position);
						}else if(clickData.getLevel() == 1){
							deleteSecond(clickData,position);
						}else{
							deleteThird(clickData,position);
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create();
		ad.show();
	}

	/**
	 * 删除三级目录
	 * */
	protected void deleteThird(final ConnType clickData, final int position) {
		ConnType upThird = new ConnType();
		upThird.setValue("delSta", 0);
		upThird.update(clickData.getSelfId(), new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e == null){
					Log.e(TAG, "删除3333333333333三级目录: "+clickData.getConn()+" 成功");
					//删除成功
					BNDemoMainActivity.dbHelper.deleteThird(clickData.getSelfId())	;//更新数据库
					posData.remove(position);		//跟新本地显示
					msg.obj = "删除三级目录: "+clickData.getConn()+" 成功";
					handler.sendEmptyMessage(UPDATA_ADAPTER);
				}else{
					//删除失败
					Log.e(TAG, "删除333333333三级目录"+clickData.getConn()+"失败");
					msg.obj = "删除三级目录"+clickData.getConn()+"失败";
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}

	protected void deleteSecond(final ConnType clickData,final int position) {
		LevelSecond upInfSe = new LevelSecond();
		upInfSe.setValue("delSta", 0);
		upInfSe.update(clickData.getSelfId(), new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e == null){
					Log.e(TAG, "删除22222222222222222二级目录: "+clickData.getConn()+" 成功");
					//删除成功
					BNDemoMainActivity.dbHelper.deleteSecond(clickData.getSelfId())	;//更新数据库
					posData.remove(position);		//跟新本地显示
					msg.obj = "删除二级目录: "+clickData.getConn()+" 成功";
					handler.sendEmptyMessage(UPDATA_ADAPTER);
				}else{
					//删除失败
					Log.e(TAG, "删除2222222222二级目录"+clickData.getConn()+"失败");
					msg.obj = "删除二级目录"+clickData.getConn()+"失败";
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if(tmpPos == 3 || tmpPos == 2){
			//当前显示的是第三级地址
			if(tmpPos == 3){
				posData = secondData;
			}else{
				posData = fristData;
			}
			adapterPos = null;
			adapterPos = new MainSprinAdapter(posData, Setting.this,1,mListener);
			lv_pos_manage.setAdapter(adapterPos);
			handler.sendEmptyMessage(UPDATA_ADAPTER);
			tmpPos--;
		}else if(tmpPos == 1){
			//可直接推出
			finish();
			onDestroy();
		}
	}

	protected void deleteFirst(final ConnType clickData,final int position) {
		LevelRoot upInfo = new LevelRoot();
		upInfo.setValue("delSta", 0);
		upInfo.update(clickData.getSelfId(), new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e == null){
					Log.e(TAG, "删除------------根级目录: "+clickData.getConn()+" 成功");
					//删除成功
					BNDemoMainActivity.dbHelper.deleteRoot(clickData.getSelfId());	//更新数据库
					posData.remove(position);		//跟新本地显示
					msg.obj = "删除根级目录: "+clickData.getConn()+" 成功";
					handler.sendEmptyMessage(UPDATA_ADAPTER);
				}else{
					//删除失败
					Log.e(TAG, "删除-----------根级目录"+clickData.getConn()+"失败");
					msg.obj = "删除根级目录"+clickData.getConn()+"失败";
				}
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);
			}
		});
	}



	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.ib_set_back:
				finish();
				onDestroy();
				break;
			case R.id.tv_level_frist:
				tmpPos = 1;
				tv_level_frist.setText("一级目录");
				tv_level_second.setVisibility(View.GONE);
				Log.e(TAG, "请求根级目录");
				getRootData();
				break;
			case R.id.tv_level_second:
				tmpPos = 2;
				tv_level_second.setText("二级目录");
				Log.e(TAG, "请求二级目录，一级的 Id： "+fatherIdFirst);
				getSecondData(fatherIdFirst);
				break;
			case R.id.bt_login:
				userLogin();
				break;
			default:
				break;
		}
	}

	private void userLogin() {
		if(LocationApplication.adminLoginSta){
			//已登录，退出登录
			gotoQuit();
		}else{
			//未登录，去登陆
			goToLogin();
		}
	}

	private void gotoQuit() {
		final AlertDialog alertDialog = new AlertDialog.Builder(Setting.this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_quit_login);

		window.findViewById(R.id.bt_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp.edit().putLong(LocationApplication.NOW, 0L).commit();
				LocationApplication.adminLoginSta = false;
				tmpPos = 1;
				//显示地点管理 并刷新显示
				data.remove(data.size()-1);
				adapter.notifyDataSetChanged();
				initLoginShow();
				ll_pos_manage.setVisibility(View.GONE);
				alertDialog.dismiss();
			}
		});
	}

	private void goToLogin() {
		final AlertDialog alertDialog = new AlertDialog.Builder(Setting.this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_auth_info);

		final EditText et_userName = (EditText) window.findViewById(R.id.username);
		final EditText et_passWord = (EditText) window.findViewById(R.id.userpassword);

		window.findViewById(R.id.bt_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inName = et_userName.getText().toString().trim();
				String inPssw = et_passWord.getText().toString().trim();
				if(inName != null && inName.equals("admin") && inPssw != null && inPssw.equals("sxadmin")){
					Long curTime = System.currentTimeMillis();
					sp.edit().putLong(LocationApplication.NOW, curTime).commit();
					LocationApplication.adminLoginSta = true;

					//显示地点管理 并刷新显示
					SetData tmp1= new SetData();
					tmp1.setLogoId(R.drawable.icon_usercenter_address);
					tmp1.setTitle("管理地点");
					tmp1.setConn("添加或删除或修改地点信息");
					data.add(tmp1);
					adapter.notifyDataSetChanged();

					initLoginShow();

					alertDialog.dismiss();
				}else{
					Toast.makeText(getApplicationContext(), "账号或密码错误！", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPDATA_ADAPTER:
					adapterPos.notifyDataSetChanged();
					break;
				case DELETE_FILUE:
					toast("删除失败，请检查网络连接!");
					break;
				case SHOW_TOAST:
					toast((String)msg.obj);
					break;
				default:
					break;
			}
		}
	};
	private void toast(String string) {
		myToast.show(string);
	};
	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
			case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
				MKOLUpdateElement update = mOffline.getUpdateInfo(state);
				// 处理下载进度更新提示
				if (update != null) {
					tv_load.setText(String.format("%s: %d%%", "正在下载",
							update.ratio));
					if(update.ratio == 100){
						tv_load.setText("已下载");
						for(int i=0;i<cityData.size();i++){
							if(cityData.get(i).getCityId() == update.cityID){
								cityData.get(i).setEixt(true);
								break;
							}
						}
						cityAdapter.notifyDataSetChanged();
					}
				}
			}
			break;
			case MKOfflineMap.TYPE_NEW_OFFLINE:
				// 有新离线地图安装
				Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
				break;
			case MKOfflineMap.TYPE_VER_UPDATE:

				break;
			default:
				break;
		}
	}

}
