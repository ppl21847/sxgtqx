package com.ppl.sxgtqx.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.adpter.RouteAdapter;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoGuideActivity;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNEventHandler;
import com.ppl.sxgtqx.utils.BikingRouteOverlay;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.DrivingRouteOverlay;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.utils.MyPublicData;
import com.ppl.sxgtqx.utils.MyRouteLine;
import com.ppl.sxgtqx.utils.WalkingRouteOverlay;
import com.ppl.sxgtqx.view.MyToast;

@SuppressLint("ResourceAsColor") public class InputStartEnd extends Activity implements OnClickListener, OnGetRoutePlanResultListener{
	private String mSDCardPath = null;
	public static boolean hasInitSuccess = false;

	private static final String APP_FOLDER_NAME = "sxxlqx";
	private final static String authBaseArr[] =
		{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION };
	private final static String authComArr[] = { Manifest.permission.READ_PHONE_STATE };
	private final static int authBaseRequestCode = 1;
	private final static int authComRequestCode = 2;

	private boolean hasRequestComAuth = false;

	private static final String TAG = "InputStartEnd";
	public static List<Activity> activityList = new LinkedList<Activity>();
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	ImageButton ib_exchange,ib_back;
	TextView ib_search_neva,tv_bike_walk_info,tv_nevi_conn;
	Activity mActivity;
	boolean distSta = true;	//查找类型。TRUE 没有目的地,false 有目的地
	int sunId = -1;		//当存在目的地时，目的地的ID

	//路线规划
	MapView bdmp;
	GridView gv_rout_plan;
	ImageButton ib_neci_type;
	int routeType=1;		//0-骑行		1-驾车	2-步行

	LinearLayout search_result;
	BaiduMap mBaidumap = null;
	// 搜索相关
	RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
	int nowSearchType = -1 ; // 当前进行的检索，供判断浏览节点时结果使用。
	int nodeIndex = -1; // 节点索引,供浏览节点时使用
	DrivingRouteResult nowResultdrive  = null;
	BikingRouteResult bikeResultdrive = null;
	WalkingRouteResult walkResultdrive = null;
	Button mBtnPre,mBtnNext;
	boolean useDefaultIcon = false;
	List<MyRouteLine> routeData;
	RouteAdapter routrAdapte;
	private ProgressDialog progressDialog;    //点击搜索后，弹出等待提示框
	protected int ROUTPLANFUAIL = 7;
	private int ROUTE_END = 8;
	protected static final int DIMISS_DLG = 9;
	private static final int ROUTR_PLAN_FAILED = 11;
	protected static final int SELECT_START_POS = 12;
	protected static final int SELECT_DIST_POS = 13;
	protected static final String TAGMAIN = "InputSE";
	protected boolean hasRoutPlanSta = false;
	DrivingRouteOverlay overlay;
	BikingRouteOverlay bikingOverlay;
	WalkingRouteOverlay walkingOverlay;

	private RadioGroup pg_rout_type;  
	private RadioButton rb_bycle, rb_car, rb_walk;
	private View view_bycle,view_car,view_walk;
	private LinearLayout ll_start_nevi;
	private int showSubId = 1;
	LinearLayout ll_start,ll_end;
	TextView tv_show_end,tv_show_start;
	ConnType startPos,distPos;
	private MyToast myToast;
	private boolean selectPosSta = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//		activityList.add(this);
		BNOuterLogUtil.setLogSwitcher(true);
		mActivity = this;
		setContentView(R.layout.aty_in_start_end);

		distSta = getIntent().getBooleanExtra(BNDemoMainActivity.DEFAULTSTA, true);
		if(!distSta){
			startPos = new ConnType(0, 0, "我的位置", "", 1);
			sunId = getIntent().getIntExtra(BNDemoMainActivity.DISTID, 0);
			distPos = MyPublicData.levelThird.get(sunId);
		}
		initView();
		initData();
		if (initDirs()) {
			initNavi();
		}

		if(!distSta){
			serachRout();
		}
	}



	private void initData() {
		myToast = new MyToast(InputStartEnd.this, 5000, Gravity.BOTTOM);

		tv_show_start.setText("我的位置");
		startPos = new ConnType(0, 0, "我的位置", "", 1);

		if(!distSta){
			tv_show_end.setText(distPos.getConn());
		}
		// TODO Auto-generated method stub
		pg_rout_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {  
				case R.id.rb_bycle:
					ib_neci_type.setImageResource(R.drawable.icon_route_bike_blue);
					routeType = 0;
					view_bycle.setVisibility(View.VISIBLE);
					view_car.setVisibility(View.INVISIBLE);
					view_walk.setVisibility(View.INVISIBLE);
					break;  
				case R.id.rb_car:
					ib_neci_type.setImageResource(R.drawable.icon_route_car_blue);
					routeType = 1;
					view_bycle.setVisibility(View.INVISIBLE);
					view_car.setVisibility(View.VISIBLE);
					view_walk.setVisibility(View.INVISIBLE);
					break;  
				case R.id.rb_walk:
					ib_neci_type.setImageResource(R.drawable.icon_route_sectional_walk);
					routeType = 2;
					view_bycle.setVisibility(View.INVISIBLE);
					view_car.setVisibility(View.INVISIBLE);
					view_walk.setVisibility(View.VISIBLE);
					break;  
				}
				if(selectPosSta){
					serachRout();
				}
			}
		});
		ib_back.setOnClickListener(this);
		ib_search_neva.setOnClickListener(this);

		// 地图点击事件处理
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		overlay = new MyDrivingRouteOverlay(mBaidumap);
		bikingOverlay = new MyBikingRouteOverlay(mBaidumap);
		walkingOverlay = new MyWalkingRouteOverlay(mBaidumap);

		mBaidumap.setOnMarkerClickListener(overlay);
		mBaidumap.setOnMarkerClickListener(bikingOverlay);
		mBaidumap.setOnMarkerClickListener(walkingOverlay);

		routeData = new ArrayList<MyRouteLine>();

		ib_exchange.setOnClickListener(this);
		ll_start_nevi.setOnClickListener(this);
		ll_start.setOnClickListener(this);
		ll_end.setOnClickListener(this);
	}
	private void initView() {
		// TODO Auto-generated method stub
		ib_exchange = (ImageButton) findViewById(R.id.ib_exchange);
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_search_neva = (TextView) findViewById(R.id.ib_search_neva);
		tv_bike_walk_info = (TextView) findViewById(R.id.tv_bike_walk_info);
		tv_nevi_conn = (TextView) findViewById(R.id.tv_nevi_conn); 

		bdmp = (MapView)findViewById(R.id.bdmp);
		gv_rout_plan = (GridView) findViewById(R.id.gv_rout_plan);
		search_result = (LinearLayout)findViewById(R.id.search_result);
		mBaidumap = bdmp.getMap();

		pg_rout_type = (RadioGroup) findViewById(R.id.pg_rout_type);
		rb_bycle = (RadioButton) findViewById(R.id.rb_bycle);
		rb_car = (RadioButton) findViewById(R.id.rb_car);
		rb_walk = (RadioButton) findViewById(R.id.rb_walk);
		view_bycle = findViewById(R.id.view_bycle);
		view_car = findViewById(R.id.view_car);
		view_walk = findViewById(R.id.view_walk);
		ib_neci_type = (ImageButton) findViewById(R.id.ib_neci_type);
		ll_start_nevi = (LinearLayout) findViewById(R.id.ll_start_nevi);
		ll_start = (LinearLayout) findViewById(R.id.ll_start);
		ll_end = (LinearLayout) findViewById(R.id.ll_end);
		tv_show_end = (TextView) findViewById(R.id.tv_show_end);
		tv_show_start = (TextView) findViewById(R.id.tv_show_start);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ib_exchange:
			exchangeDist();
			break;
		case R.id.ll_start_nevi:
			startNevigate();
			break;
		case R.id.ib_back:
			finish();
			break;
		case R.id.ib_search_neva:
			serachRout();
			break;
		case R.id.ll_start:
			//选择开始地点
			selectDist(1);
			break;
		case R.id.ll_end:
			//选择目的地地点
			selectDist(2);
			break;
		default:
			break;
		}
	}

	/**
	 * 选择地点
	 * @param type 1=起始地点	2=目的地
	 * */
	private void selectDist(int type) {
		//
		System.out.println("选择地点类型:"+type);
		Intent intent = new Intent(getApplicationContext(), SelectPos.class);
		intent.putExtra("TYPE", type);
		startActivityForResult(intent, 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, requestCode, data);
		Log.e("InputStartEnd", "requestCode: "+requestCode+",requestCode: "+requestCode);
		if (requestCode == 1 && resultCode == 4) {  
			boolean selectStartSta = data.getBooleanExtra("START", true);
			String selectId = data.getStringExtra("SELECTID");
			Log.e("InputStartEnd", "selectId: "+selectId);
			ConnType selectInfo;
			if(selectId.equals("0")){
				selectInfo = new ConnType(0, 0, "我的位置", "", 1);
			}else{
				LevelThird tmpData = BNDemoMainActivity.dbHelper.getThird(selectId);
				selectInfo = new ConnType(1, 2, tmpData.getName(), 
						tmpData.getFatherId(), tmpData.getID(),
						tmpData.getPosLat(), tmpData.getPosLong(), 1);
			}
			Log.e("TAG", selectInfo.toString());
			if(selectStartSta){
				startPos = selectInfo;
				tv_show_start.setText(startPos.getConn());
			}else{
				distPos = selectInfo;
				tv_show_end.setText(distPos.getConn());
				selectPosSta = true;
				serachRout();
			}
		}
	}
	protected void toast(String msg) {
		// TODO Auto-generated method stub
		myToast.show(msg);
	}

	private void serachRout() {
		// TODO Auto-generated method stub
		//startPos,distPos
		if(startPos == null || distPos == null){
			toast("起点或终点不能为空");
			return;
		}
		if(startPos.getConn().equals(distPos.getConn())){
			toast("起点和终点不能是同一个地方。");
			bdmp.setVisibility(View.INVISIBLE);
			gv_rout_plan.setVisibility(View.INVISIBLE);
			search_result.setVisibility(View.INVISIBLE);
			return;
		}
		hasRoutPlanSta = false;
		progressDialog = ProgressDialog.show(InputStartEnd.this, "提示", "正在搜索路线请等待", true, true);  
		Thread thread = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(10000);
				} catch (InterruptedException e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				handle.sendEmptyMessage(ROUTPLANFUAIL );
			}

		};
		thread.start();
		// 设置起终点信息，对于tranist search 来说，城市名无意义
		LatLng staPosLng,distPosLng;
		if(startPos.getType() == 0){
			//我的位置
			staPosLng = new LatLng(MyPublicData.selfLatitude, MyPublicData.selfLongitude);
		}else{
			staPosLng = new LatLng(startPos.getPosLat(), startPos.getPosLong());
		}

		if(distPos.getType() == 0){
			distPosLng = new LatLng(MyPublicData.selfLatitude, MyPublicData.selfLongitude);
		}else{
			distPosLng = new LatLng(distPos.getPosLat(), distPos.getPosLong());
		}

		PlanNode stNode = PlanNode.withLocation(staPosLng);
		PlanNode enNode = PlanNode.withLocation(distPosLng);
		if(routeType == 0){	//骑行
			//骑行路线
			mSearch.bikingSearch(new BikingRoutePlanOption().from(stNode).to(enNode));
		}else if(routeType == 1){
			mSearch.drivingSearch((new DrivingRoutePlanOption())
					.from(stNode).to(enNode));
		}else if(routeType == 2){
			mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
		}
		nowSearchType = 1;
	}
	private void startNevigate() {
		// TODO Auto-generated method stub
		if (BaiduNaviManager.isNaviInited()) {
			routePlanToNavi(CoordinateType.BD09LL,startPos,distPos);
		}else{
			toast( "导航初始化失败，请重试。");
		}
	}

	private Handler handle = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == ROUTPLANFUAIL){
				if(progressDialog != null){
					if(!hasRoutPlanSta ){
						progressDialog.dismiss();
						toast("路线查询失败，请检查网络设置");
					}
				}
			}else if(ROUTE_END == msg.what){
				if(progressDialog != null){
					progressDialog.dismiss();
				}
			}else if(SELECT_START_POS == msg.what){
				tv_show_start.setText(startPos.getConn());
			}else if(msg.what == SELECT_DIST_POS){
				tv_show_end.setText(startPos.getConn());
			}else if(msg.what == DIMISS_DLG){
				progressDialog.dismiss();
			}else if(msg.what == ROUTR_PLAN_FAILED){
				progressDialog.dismiss();
				toast("路线规划失败，请检查网络设置");
			}
		};
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mSearch != null) {
			mSearch.destroy();
		}
		bdmp.onDestroy();
		handle.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bdmp.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bdmp.onPause();
	}

	private void exchangeDist() {
		// TODO Auto-generated method stub
		ConnType tmp;
		tmp = startPos;
		startPos = distPos;
		distPos = tmp;
		tv_show_start.setText(startPos.getConn());
		tv_show_end.setText(distPos.getConn());
	}

	private boolean hasCompletePhoneAuth() {
		// TODO Auto-generated method stub

		PackageManager pm = this.getPackageManager();
		for (String auth : authComArr) {
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		if (requestCode == authBaseRequestCode) {
			for (int ret : grantResults) {
				if (ret == 0) {
					continue;
				} else {
					Toast.makeText(InputStartEnd.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			initNavi();
		} else if (requestCode == authComRequestCode) {
			for (int ret : grantResults) {
				if (ret == 0) {
					continue;
				}
			}
			routePlanToNavi(CoordinateType.BD09LL,startPos,distPos);
		}
	}

	private boolean hasBasePhoneAuth() {
		// TODO Auto-generated method stub

		PackageManager pm = this.getPackageManager();
		for (String auth : authBaseArr) {
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/***
	 * 接收定位结果消息，并显示在地图上
	 * 用Handler来更新UI
	 */
	private Handler locHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == DIMISS_DLG){
				progressDialog.dismiss();
			}else if(msg.what == ROUTR_PLAN_FAILED){
				progressDialog.dismiss();
				Log.e(TAGMAIN,  "路线规划失败，请检查网络设置");
				showToastMsg("路线规划失败，请检查网络设置");
			}	
		}
	};

	public void showToastMsg(final String msg) {
		Log.e(TAGMAIN, "myToast show info------------>>>>>>>>>"+msg);
		myToast.show(msg);
	}

	private void initSetting() {
		// BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
		BNaviSettingManager
		.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		// BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
		BNaviSettingManager.setIsAutoQuitWhenArrived(true);
		Bundle bundle = new Bundle();
		// 必须设置APPID，否则会静音
		bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10899062");
		BNaviSettingManager.setNaviSdkParam(bundle);
	}

	@SuppressLint("NewApi") 
	private void initNavi() {
		// 申请权限
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			if (!hasBasePhoneAuth()) {
				this.requestPermissions(authBaseArr, authBaseRequestCode);
				return;
			}
		}

		Log.e(TAG, "mSDCardPath: "+mSDCardPath);
		Log.e(TAG, "APP_FOLDER_NAME: "+APP_FOLDER_NAME);
		BaiduNaviManager.getInstance().init(InputStartEnd.this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
			
			@Override
			public void onAuthResult(int status, String msg) {
				if (0 == status) {
					authinfo = "key校验成功!";
					Log.e(TAGMAIN, "key校验成功!");
				} else {
					authinfo = "key校验失败, " + msg;
				}
				Log.e(TAGMAIN, "authinfo:"+authinfo);
			}
			
			@Override
			public void initSuccess() {
				Log.e(TAGMAIN, "百度导航引擎初始化成功");
				hasInitSuccess = true;
				initSetting();
			}
			
			@Override
			public void initStart() {
				Log.e(TAGMAIN, "百度导航引擎初始化开始");
			}
			
			@Override
			public void initFailed() {
				Log.e(TAGMAIN, "百度导航引擎初始化失败");
			}
		}, null, ttsHandler, ttsPlayStateListener);
		
	}
	
	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };

	
    
	/**
	 * 内部TTS播报状态回调接口
	 */
	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

		@Override
		public void playEnd() {
			Log.e(TAG, "TTSPlayStateListener : TTS play end");
			
		}

		@Override
		public void playStart() {
			Log.e(TAG, "TTSPlayStateListener : TTS play start");
		}
	};
	/**
	 * 内部TTS播报状态回传handler
	 */
	private Handler ttsHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.e(TAG, "进入了： "+ttsHandler);
			int type = msg.what;
			switch (type) {
			case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
				Log.e(TAG, "TTSPlayStateListener : TTS play PLAY_START_MSG");
				break;
			}
			case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
				Log.e(TAG, "TTSPlayStateListener : TTS play PLAY_END_MSG");
				break;
			}
			default:
				break;
			}
		}
	};

	//导航部分
	@TargetApi(23) private void routePlanToNavi(CoordinateType coType, ConnType staPos, ConnType diPos) {
		if (!hasInitSuccess) {
			toast("还未初始化!");
			return;
		}
		//      // 权限申请
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			// 保证导航功能完备
			if (!hasCompletePhoneAuth()) {
				if (!hasRequestComAuth) {
					hasRequestComAuth = true;
//					this.requestPermissions(authComArr, authComRequestCode);
					return;
				} else {
					Toast.makeText(InputStartEnd.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
				}
			}

		}
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;

		LatLng startLng,distLng;
		if(staPos.getType() == 0){
			startLng = new LatLng(MyPublicData.selfLatitude, MyPublicData.selfLongitude);
		}else{
			startLng = new LatLng(staPos.getPosLat(), staPos.getPosLong());
		}

		if(diPos.getType() == 0){
			distLng =  new LatLng(MyPublicData.selfLatitude, MyPublicData.selfLongitude);
		}else{
			distLng = new LatLng(diPos.getPosLat(), diPos.getPosLong());
		}
		sNode = new BNRoutePlanNode(startLng.longitude, 
				startLng.latitude, staPos.getConn(), 
				null, coType);
		eNode = new BNRoutePlanNode(distLng.longitude, 
				distLng.latitude, diPos.getConn(), 
				null, coType);

		if (sNode != null && eNode != null) {
			hasRoutPlanSta = false;
			progressDialog = ProgressDialog.show(InputStartEnd.this, "提示", "正在规划路线请等待", true, true);  
			Thread thread = new Thread()
			{
				public void run()
				{
					try
					{
						sleep(10000);
					} catch (InterruptedException e)
					{
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					handle.sendEmptyMessage(ROUTPLANFUAIL);
				}
			};
			thread.start();
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			/*
			 * public boolean launchNavigator(Activity activity,
                      java.util.List<BNRoutePlanNode> nodes,
                      int preference,
                      boolean isGPSNav,
                      BaiduNaviManager.RoutePlanListener listener)
						发起算路操作并在算路成功后通过回调监听器进入导航过程.
						参数:
						activity - 建议是应用的主Activity
						nodes - 传入的算路节点，顺序是起点、途经点、终点，其中途经点最多三个，参考 BNRoutePlanNode
						preference - 算路偏好， 参考RoutePlanPreference定义
						//ROUTE_PLAN_MOD_AVOID_TAFFICJAM 	避免拥堵			16
						//ROUTE_PLAN_MOD_MIN_DIST			少走高速			4
						//ROUTE_PLAN_MOD_MIN_TIME			高速优先			2
						//ROUTE_PLAN_MOD_MIN_TOLL			少收费			8
						//ROUTE_PLAN_MOD_RECOMMEND			推荐				1
						isGPSNav - true表示真实GPS导航，false表示模拟导航
						listener - 开始导航回调监听器，在该监听器里一般是进入导航过程页面
						返回:
						是否执行成功
			 * */

			Log.e(TAG, "开始导航,计算");
			//			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
			// 开发者可以使用旧的算路接口，也可以使用新的算路接口,可以接收诱导信息等
			// BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode),
					eventListerner);
		}
	}

	BaiduNaviManager.NavEventListener eventListerner = new BaiduNaviManager.NavEventListener() {

		@Override
		public void onCommonEventCall(int what, int arg1, int arg2, Bundle bundle) {
			BNEventHandler.getInstance().handleNaviEvent(what, arg1, arg2, bundle);
		}
	};

	public class DemoRoutePlanListener implements RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public DemoRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */
			for (Activity ac : activityList) {

				if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

					return;
				}
			}
			handle.sendEmptyMessage(DIMISS_DLG);
			hasRoutPlanSta = true;
			
			Intent intent = new Intent(InputStartEnd.this, BNDemoGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);
		}

		@Override
		public void onRoutePlanFailed() {
			toast("算路失败");
			handle.sendEmptyMessage(ROUTR_PLAN_FAILED);
		}
	}
	//导航部分结束

	String authinfo = null;


	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}
	private class MyBikingRouteOverlay extends BikingRouteOverlay{

		public MyBikingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
			// TODO Auto-generated constructor stub
		}
		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}
		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}
	private class MyWalkingRouteOverlay extends WalkingRouteOverlay{

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
			// TODO Auto-generated constructor stub
		}
		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}
		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult result) {
		handle.sendEmptyMessage(ROUTE_END);
		hasRoutPlanSta = true;
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			toast("抱歉，未找到结果");
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR){
			routeData.clear();
			nodeIndex = -1;

			bdmp.setVisibility(View.VISIBLE);
			gv_rout_plan.setVisibility(View.VISIBLE);
			search_result.setVisibility(View.VISIBLE);

			if (result.getRouteLines().size() > 0 ) {
				bikeResultdrive = result;

				routrAdapte = null;
				mBaidumap.clear();
				gv_rout_plan.setVisibility(View.GONE);
				tv_bike_walk_info.setVisibility(View.VISIBLE);
				int time = result.getRouteLines().get(0).getDuration();//.getDistance()
				int destance = result.getRouteLines().get(0).getDistance();
				String conn = formotInfo(time,destance);
				tv_bike_walk_info.setText(conn);

				bikingOverlay.setData(bikeResultdrive.getRouteLines().get(0));
				bikingOverlay.addToMap();
				bikingOverlay.zoomToSpan();

			}
			else {
				toast("没有查到路线，请重试!");
				Log.d("route result", "结果数<0" );
				return;
			}
		}
	}

	private String formotInfo(int time, int destance) {
		String con = "";
		if ( time / 3600 == 0 ) {
			con += time / 60 + "分钟" ;
		} else {
			con += time / 3600 + "小时" + (time % 3600) / 60 + "分钟" ;
		}
		con += "    ";
		if(destance / 1000 == 0){
			con += destance + "m";
		}else{
			con += ((float)destance/1000) + "Km";
		}// TODO Auto-generated method stub
		return con;
	}



	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		// TODO Auto-generated method stub
		handle.sendEmptyMessage(ROUTE_END);
		hasRoutPlanSta = true;
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			toast("抱歉，未找到结果");
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			routeData.clear();
			nodeIndex = -1;

			bdmp.setVisibility(View.VISIBLE);
			gv_rout_plan.setVisibility(View.VISIBLE);
			search_result.setVisibility(View.VISIBLE);

			if (result.getRouteLines().size() > 0 ) {
				nowResultdrive = result;
				routrAdapte = null;
				mBaidumap.clear();

				for(int i=0;i<result.getRouteLines().size();i++){
					if(i==0){
						routeData.add(new MyRouteLine(result.getRouteLines().get(i), true));
					}else{
						routeData.add(new MyRouteLine(result.getRouteLines().get(i), false));
					}
				}
				gv_rout_plan.setVisibility(View.VISIBLE);
				tv_bike_walk_info.setVisibility(View.GONE);
				setRoutAdapter();

				overlay.setData(nowResultdrive.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();

			}
			else {
				toast("没有查到路线，请重试!");
				Log.d("route result", "结果数<0" );
				return;
			}
		}
	}


	private void setRoutAdapter() {
		routrAdapte = new RouteAdapter(getApplicationContext(), routeData);
		gv_rout_plan.setAdapter(routrAdapte);
		gv_rout_plan.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				for(int i=0;i<routeData.size();i++){
					if(i == arg2){
						routeData.get(i).setShowSta(true);
					}else {
						routeData.get(i).setShowSta(false);
					}
				}
				routrAdapte.notifyDataSetChanged();
				mBaidumap.clear();
				overlay.setData(routeData.get(arg2).getLine());
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		});
	}



	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		handle.sendEmptyMessage(ROUTE_END);
		hasRoutPlanSta = true;
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			toast( "抱歉，未找到结果");
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR){
			routeData.clear();
			nodeIndex = -1;

			bdmp.setVisibility(View.VISIBLE);
			gv_rout_plan.setVisibility(View.VISIBLE);
			search_result.setVisibility(View.VISIBLE);

			if (result.getRouteLines().size() > 0 ) {
				walkResultdrive = result;

				routrAdapte = null;
				mBaidumap.clear();

				gv_rout_plan.setVisibility(View.GONE);
				tv_bike_walk_info.setVisibility(View.VISIBLE);
				int time = result.getRouteLines().get(0).getDuration();//.getDistance()
				int destance = result.getRouteLines().get(0).getDistance();
				String conn = formotInfo(time,destance);
				tv_bike_walk_info.setText(conn);

				walkingOverlay.setData(walkResultdrive.getRouteLines().get(0));
				walkingOverlay.addToMap();
				walkingOverlay.zoomToSpan();

			}
			else {
				toast("没有查到路线，请重试!");
				Log.d("route result", "结果数<0" );
				return;
			}
		}
	}
}
