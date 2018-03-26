package com.ppl.sxgtqx.com.baidu.navi.sdkdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.ppl.sxgtqx.MainPermissionActivity;
import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.activity.ElecSubShow;
import com.ppl.sxgtqx.activity.InputStartEnd;
import com.ppl.sxgtqx.activity.Setting;
import com.ppl.sxgtqx.adpter.MainSprinAdapter;
import com.ppl.sxgtqx.application.LocationApplication;
import com.ppl.sxgtqx.dataSQL.DbLocHelper;
import com.ppl.sxgtqx.service.LocationService;
import com.ppl.sxgtqx.service.Utils;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.DownFile;
import com.ppl.sxgtqx.utils.ElecSubInfo;
import com.ppl.sxgtqx.utils.LevelReThird;
import com.ppl.sxgtqx.utils.LevelRoot;
import com.ppl.sxgtqx.utils.LevelSecond;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.utils.LocationEntity;
import com.ppl.sxgtqx.utils.LogcatHelper;
import com.ppl.sxgtqx.utils.MyPublicData;
import com.ppl.sxgtqx.utils.OrderThirdData;
import com.ppl.sxgtqx.utils.UpdateEntity;
import com.ppl.sxgtqx.view.MyToast;
import com.ppl.sxgtqx.view.UpdataAlertDialog;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

@SuppressLint("NewApi") public class BNDemoMainActivity extends Activity implements OnClickListener,OnItemClickListener{
	public static String TAGMAIN = "TagMainAty";
	public static List<Activity> activityList = new LinkedList<Activity>();

	public static final String ROUTE_PLAN_NODE = "routePlanNode";

	public static boolean hasRoutPlanSta = false;

	//基础地图 + 定位
	public static final String DEFAULTSTA = "defaltSta";
	public static final String DISTID = "distinationId";

	protected static final int CHANGE_SELF_POS = 7;
	protected static final int DIMISS_DLG = 8;
	private static final int DIMISS_DLG_FUAIL = 9;
	protected static final int ROUTPLANFUAIL = 10;
	private static final int ROUTR_PLAN_FAILED = 11;
	private static final int SECOND_ADD_SUCCESS = 12;
	private static final int FRIST_ADD_SUCCESS = 13;
	private static final int ADD_FALUE = 14;
	protected static final int GET_ROOT_DATA = 15;
	protected static final int NOTIFY_SECOND_ADP = 17;
	private static final int NOTIFY_THRID_ADP = 18;
	private static final int NOTIFY_RE_THRID_ADP = 19;
	private ImageButton mBtUpdate;
	MapView mMapView = null;
	private BaiduMap mBaiduMap;

	BitmapDescriptor BDStart ;
	ImageButton ib_naviga;//导航
	LinearLayout ll_subInfo;		//搜索结果显示结果
	RelativeLayout rl_searchInfo;
	TextView tv_searchConn;
	ImageButton ib_dist;		//根据查找结果导航
	int showSubId=0;
	LatLng selfPos;
	private ProgressDialog progressDialog;    //点击导航路线后，弹出等待提示框
	boolean showSelfPos =true;
	boolean NoSearchSta = true;				//未进行搜索，再次进入主页时 定位到自己的位置
	long preTime = 0;

	TextView tv_selece_sub;
	LinearLayout ll_serach;
	boolean showSubSta = false;			//是否显示可选地点
	LinearLayout ll_selece_dis;
	ListView lv_level_first,lv_level_second,lv_level_third,lv_level_third_re;
	int level1=0,level2=0,levelRe3 = 0,level3=0;
	String selectConn = "";
	String selectRoot = "";
	String selectSecond = "";
	String selectReThird = "";
	String selectThird = "";
	ImageView iv_down;
	ImageButton ib_set;

	private LocationService locService;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果


	public static List<ElecSubInfo>elecSubAll = new ArrayList<ElecSubInfo>();
	MainSprinAdapter firstAdp,seceAdp,thirdAdp,reThirdAdp;
	TextView tv_distance,tv_father;

	//数据库 保存获取数据
	public static DbLocHelper dbHelper;
	//自定义Toast 
	MyToast myToast;

	/**
	 * 需要进行检测的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE
	};

	private static final int PERMISSON_REQUESTCODE = 0;
	private UpdataAlertDialog mDialog;
	private String selectSecondId;		//点击的二级地址内容ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityList.add(this);

		checkPermissions(needPermissions);
		setContentView(R.layout.activity_main);

		initDataSQL();
		initView();
		initMap();
		initSQL();//与服务器数据同步
		MyPublicData.getLocData(dbHelper);
		initLocListView();
		checkUpdate();
	}

	/**
	 * 检查更新
	 * */
	private void checkUpdate() {
		Log.d("downFile","checkUpdate");
		BmobQuery<UpdateEntity> query = new BmobQuery<UpdateEntity>();
		query.getObject("8abce600e6", new QueryListener<UpdateEntity>() {

			@Override
			public void done(final UpdateEntity object, BmobException e) {
				if(e==null){
					// ---get the package info---
					PackageManager pm = getApplication().getPackageManager();
					PackageInfo pi = null;
					try {
						pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
						String versionName = pi.versionName;
						int versioncode = pi.versionCode;
						String desc = object.getUpdateDescroption();
						Log.d("downFile","versionName: "+versionName+",versioncode: "+versioncode);
						Log.d("downFile","msg: "+desc);
						Log.d("downFile","focsState: "+object.getFocsUpdate());

						if(versioncode >= 1){
							//校验是否更新
							if(object.getVersionCode() > versioncode){
								//弹出更新提示框
								//防止重复刷新弹出框
								if (mDialog != null && mDialog.isShowing()) {
									return;
								}

								mDialog = new UpdataAlertDialog(BNDemoMainActivity.this);

								mDialog.setTitle(R.string.update_log_title);
								Log.d("downFile","msg: "+desc);
								mDialog.setMessage("" + desc);
								mDialog.setCanceledOnTouchOutside(false);//点击空白处无响应
								mDialog.setCancelable(false);//用户按返回键也无法取消
								//强制更新没有取消按钮
								mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										if (Build.VERSION.SDK_INT >= 16)
											(BNDemoMainActivity.this).finishAffinity();
										else {
											System.exit(0);
										}
									}
								});


								if(!object.getFocsUpdate().equalsIgnoreCase("Y")){
									mDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
										@Override
										public void onClick(View v) {
											//非强制升级 点击了取消
											mDialog.dismiss();
										}
									});

									mDialog.setPositiveButton("升级", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Log.d("downFile","开始下载");
											downNewFile(object.getApkUrl());
										}
									});
								}else{
									//强制更新
									mDialog.setNeutralButton(R.string.immediately_update, new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Log.d("downFile","开始下载");
											downNewFile(object.getApkUrl());
										}
									});
								}


								mDialog.show();
							}
						}
					} catch (PackageManager.NameNotFoundException e1) {
						e1.printStackTrace();
					}
				}else{
					Log.i("downFile","失败："+e.getMessage()+","+e.getErrorCode());
				}
			}

		});
	}


	/**
	 * 下载最新的安装包
	 * */
	private void downNewFile(String apkUrl) {
		DownFile downFile = new DownFile();
		downFile.downLoadFile(apkUrl,locHander);
	}

	/**
	 *
	 * @since 2.5.0
	 * requestPermissions方法是请求某一权限，
	 */
	private void checkPermissions(String... permissions) {
		List<String> needRequestPermissonList = findDeniedPermissions(permissions);
		if (null != needRequestPermissonList
				&& needRequestPermissonList.size() > 0) {
			ActivityCompat.requestPermissions(this,
					needRequestPermissonList.toArray(
							new String[needRequestPermissonList.size()]),
					PERMISSON_REQUESTCODE);
		}
	}

	/**
	 * 获取权限集中需要申请权限的列表
	 *
	 * @param permissions
	 * @return
	 * @since 2.5.0
	 * checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
	 * shouldShowRequestPermissionRationale方法用来判断是否
	 * 显示申请权限对话框，如果同意了或者不在询问则返回false
	 */
	private List<String> findDeniedPermissions(String[] permissions) {
		List<String> needRequestPermissonList = new ArrayList<String>();
		for (String perm : permissions) {
			if (ContextCompat.checkSelfPermission(this,
					perm) != PackageManager.PERMISSION_GRANTED) {
				needRequestPermissonList.add(perm);
			} else {
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						this, perm)) {
					needRequestPermissonList.add(perm);
				}
			}
		}
		return needRequestPermissonList;
	}

	/**
	 * 检测是否所有的权限都已经授权
	 * @param grantResults
	 * @return
	 * @since 2.5.0
	 *
	 */
	private boolean verifyPermissions(int[] grantResults) {
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 申请权限结果的回调方法
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] paramArrayOfInt) {
		if (requestCode == PERMISSON_REQUESTCODE) {
			if (!verifyPermissions(paramArrayOfInt)) {
				showMissingPermissionDialog();
			}
		}
	}

	/**
	 * 显示提示信息
	 *
	 * @since 2.5.0
	 *
	 */
	private void showMissingPermissionDialog() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("提示");
		builder1.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

		// 拒绝, 退出应用
		builder1.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

		builder1.setPositiveButton("设置",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startAppSettings();
					}
				});

		builder1.setCancelable(false);

		builder1.show();
	}

	/**
	 *  启动应用的设置
	 *
	 * @since 2.5.0
	 *
	 */
	private void startAppSettings() {
		Intent intent = new Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

	private void initSQL() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//同步三级地址信息
				Log.e(TAGMAIN, "--44444444444444开始获取si级地址信息");
				MyPublicData.syncReThird(dbHelper);
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				//同步三级地址信息
				Log.e(TAGMAIN, "--3333333333333333开始获取san级地址信息");
				MyPublicData.syncThird(dbHelper);
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(TAGMAIN, "--22222222开始获取二级地址信息");
				MyPublicData.syncSecondSQL(dbHelper);
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(TAGMAIN, "---1111111111111开始获取一级地址信息");
				MyPublicData.syncFirstSQL(dbHelper);
			}
		}).start();
	}


	private void initLocListView() {
		//初始化候选列表
		firstAdp = new MainSprinAdapter(MyPublicData.levelFirst, getApplicationContext());
		seceAdp = new MainSprinAdapter(MyPublicData.levelSond, getApplicationContext());
		thirdAdp = new MainSprinAdapter(MyPublicData.levelThird, getApplicationContext());
		reThirdAdp = new MainSprinAdapter(MyPublicData.levelReThird, getApplicationContext());

		lv_level_first.setAdapter(firstAdp);
		lv_level_second.setAdapter(seceAdp);
		lv_level_third.setAdapter(thirdAdp);
		lv_level_third_re.setAdapter(reThirdAdp);

		lv_level_first.setOnItemClickListener(this);
		lv_level_second.setOnItemClickListener(this);

		//点击新第三级地址第四级列表内容
		lv_level_third_re.setOnItemClickListener(this);

		//点击三级地址显示相应信息
		lv_level_third.setOnItemClickListener(this);

	}

	/**
	 * 显示搜索信息
	 * */
	private void showSubInfo(int level3) {
		NoSearchSta = false;
		showSubId = level3;
		showSelfPos = false;
		LatLng cenpt = new LatLng(MyPublicData.levelThird.get(level3).getPosLat(), MyPublicData.levelThird.get(level3).getPosLong());
		String sunName=MyPublicData.levelThird.get(level3).getConn();
		LatLng selfPos = new LatLng(MyPublicData.selfLatitude, MyPublicData.selfLongitude);
		//计算p1、p2两点之间的直线距离，单位：米  
		double dist = DistanceUtil. getDistance(selfPos, cenpt);
		String distConn = "米";
		if(dist > 1000){
			dist = dist/1000;
			distConn = "千米";
		}
		BigDecimal b   =   new   BigDecimal(dist);
		double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
		tv_distance.setText(f1 + distConn);
		tv_searchConn.setText(sunName);
		tv_father.setText(MyPublicData.levelSond.get(level2).getConn());
		Log.e(TAGMAIN, "显示搜索内容："+MyPublicData.levelThird.get(level3).toString());

		rl_searchInfo.setVisibility(View.VISIBLE);
		ib_naviga.setVisibility(View.GONE);

		//定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder()
				.target(cenpt)
				.zoom(15f)
				.build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		BDStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green_paddle);
		MarkerOptions ooA = new MarkerOptions().position(cenpt).icon(BDStart)
				.zIndex(9).draggable(true);
		mBaiduMap.addOverlay(ooA);
	}

	/***
	 * 获取新的第三级数据显示
	 * */
	private void getReThirdDate() {
		//二级显示在左侧，需要显示返回功能
		if(MyPublicData.levelSond != null && !MyPublicData.levelSond.get(MyPublicData.levelSond.size()-1).getConn().equalsIgnoreCase("返回上一级")){
			MyPublicData.levelSond.add(new ConnType(1, 1,
					"返回上一级",
					"",
					"",1));
			locHander.sendEmptyMessage(NOTIFY_SECOND_ADP);
		}

		List<LevelReThird>tempData = dbHelper.getReThirdData();
		Log.e(TAGMAIN, "获取新的三级目录内容数据大小:"+tempData.size());
		MyPublicData.levelReThird.clear();

		for(int i=0;i<tempData.size();i++){
			Log.e(TAGMAIN, tempData.get(i).toString());
			//(int type, int level, String conn, String selfId,int delSta)
			MyPublicData.levelReThird.add(new ConnType(1, 3,
					tempData.get(i).getName(),
					tempData.get(i).getID(),0));
		}
		Log.e(TAGMAIN, "新的三级目录下个数:"+MyPublicData.levelReThird.size());
		locHander.sendEmptyMessage(NOTIFY_RE_THRID_ADP);
	}
	/**
	 * 从数据库获取三级目录数据
	 * */
	protected void getThirdDate(String fatherId, String reThird) {
		//新添加的第三级在左侧  需要返回按键
		if(MyPublicData.levelReThird != null && (!MyPublicData.levelReThird.get(MyPublicData.levelReThird.size()-1).getConn().equalsIgnoreCase("返回上一级"))){
			MyPublicData.levelReThird.add(new ConnType(1, 3,
					"返回上一级",
					"",
					"",1));
			locHander.sendEmptyMessage(NOTIFY_RE_THRID_ADP);
		}

		Log.d(TAGMAIN,"fatherId: "+fatherId+", reThird: "+reThird);
		List<LevelThird>tmpData = dbHelper.getThirdData(fatherId,reThird);
		Log.e(TAGMAIN, "获取三级目录内容数据大小:"+tmpData.size());

		MyPublicData.levelThird.clear();

		List<ConnType>temLevelThird = new ArrayList<>();

		for(int i=0;i<tmpData.size();i++){
			Log.e(TAGMAIN, tmpData.get(i).toString());
			temLevelThird.add(new ConnType(1, 2, 1,
					tmpData.get(i).getName(),
					tmpData.get(i).getFatherId(),
					tmpData.get(i).getID(),
					tmpData.get(i).getPosLat(),
					tmpData.get(i).getPosLong(),tmpData.get(i).getInfo()));
		}

		List<ConnType>softLevelThird = new ArrayList<>();
		softLevelThird = OrderThirdData.orderThirdData(temLevelThird);

		if(softLevelThird != null && softLevelThird.size() != 0){
			for(ConnType tmpThird : softLevelThird){
				MyPublicData.levelThird.add(tmpThird);
			}
		}

		Log.e(TAGMAIN, "新的三级目录下个数:"+MyPublicData.levelThird.size());
		locHander.sendEmptyMessage(NOTIFY_THRID_ADP);
	}
	protected void getSecondDataSQL(String selfId) {
		//获取服务器数据失败，直接从本地数据库获取
		List<LevelSecond> tmpData= dbHelper.getSecondData(selfId);
		Log.e(TAGMAIN, "获取二级目录内容数据大小:"+tmpData.size());
		MyPublicData.levelSond.clear();		//清空之前数据
		for(int i=0;i<tmpData.size();i++){
			Log.e(TAGMAIN, tmpData.get(i).toString());
			MyPublicData.levelSond.add(new ConnType(1, 1,
					tmpData.get(i).getName(),
					tmpData.get(i).getFatherId(),
					tmpData.get(i).getID(),1));
		}
		Log.e(TAGMAIN, "新的二级目录下个数:"+MyPublicData.levelSond.size());
		locHander.sendEmptyMessage(NOTIFY_SECOND_ADP);
	}
	/**
	 * 从本地数据库获取根级数据
	 * */
	private void getRootDataSQL() {
		MyPublicData.rootData = dbHelper.getRootData();
		if(MyPublicData.rootData == null){
			MyPublicData.rootData = new ArrayList<LevelRoot>();
		}
		Log.e(TAGMAIN, "MyPublicData.rootData.size():"+MyPublicData.rootData.size());
		MyPublicData.levelFirst.clear();
		MyPublicData.levelFirst.add(new ConnType(0, 0, "我的位置", "0",1));
		if(MyPublicData.rootData != null){
			//从本地数据哭中获取一级根目录内容
			for(int i=0;i<MyPublicData.rootData.size();i++){
				Log.e(TAGMAIN, MyPublicData.rootData.get(i).toString());
				MyPublicData.levelFirst.add(new ConnType(1, 0,
						MyPublicData.rootData.get(i).getName(), MyPublicData.rootData.get(i).getID(),1));
			}
		}
		//		MyPublicData.levelFirst.add(new ConnType(2, 0, "添加", "",1));
		Log.e(TAGMAIN, "MyPublicData.rootData.size():"+MyPublicData.rootData.size());
		locHander.sendEmptyMessage(GET_ROOT_DATA);
	}
	/**
	 * 初始化本地数据库
	 * */
	private void initDataSQL() {
		dbHelper = new DbLocHelper(getApplicationContext(),DbLocHelper.DB_NAME,null,DbLocHelper.version);
		dbHelper.getReadableDatabase();
	}
	private void initMap() {
		elecSubAll.add(new ElecSubInfo(0,"我的位置"));
		mBaiduMap = mMapView.getMap();
		mMapView.showZoomControls(false);//显示放大缩小按钮

		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent arg0) {
				// TODO Auto-generated method stub
				Log.e("BDMAP", "触摸了百度地图");
			}
		});

		locService = ((LocationApplication) getApplication()).locationService;
		LocationClientOption mOption = locService.getDefaultLocationClientOption();
		mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
		mOption.setCoorType("bd09ll");
		locService.setLocationOption(mOption);
		locService.registerListener(listener);
		locService.start();
	}

	private void initView() {
		BNOuterLogUtil.setLogSwitcher(true);

		//获取地图控件引用  
		mMapView = (MapView) findViewById(R.id.bmapView);
		ib_naviga = (ImageButton) findViewById(R.id.ib_naviga);
		mBtUpdate = (ImageButton) findViewById(R.id.ib_update);
		rl_searchInfo = (RelativeLayout) findViewById(R.id.rl_searchInfo);
		ll_subInfo = (LinearLayout) findViewById(R.id.ll_subInfo);
		tv_searchConn = (TextView) findViewById(R.id.tv_searchConn);
		ib_dist = (ImageButton) findViewById(R.id.ib_dist);

		ll_serach = (LinearLayout) findViewById(R.id.ll_serach);
		tv_selece_sub = (TextView) findViewById(R.id.tv_selece_sub);
		ll_selece_dis = (LinearLayout) findViewById(R.id.ll_selece_dis);
		lv_level_first = (ListView) findViewById(R.id.lv_level_first);
		lv_level_second = (ListView) findViewById(R.id.lv_level_second);
		lv_level_third_re = (ListView) findViewById(R.id.lv_level_third_re);
		lv_level_third = (ListView) findViewById(R.id.lv_level_third);
		iv_down = (ImageView) findViewById(R.id.iv_down);
		ib_set = (ImageButton) findViewById(R.id.ib_set);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		tv_father  = (TextView) findViewById(R.id.tv_father);

		ib_dist.setOnClickListener(this);
		ib_naviga.setOnClickListener(this);
		mBtUpdate.setOnClickListener(this);
		ll_subInfo.setOnClickListener(this);
		tv_selece_sub.setOnClickListener(this);
		iv_down.setOnClickListener(this);
		ib_set.setOnClickListener(this);

		tv_searchConn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		tv_searchConn.getPaint().setAntiAlias(true);//抗锯齿

		Log.e(TAGMAIN, "开始初始化MyToast");
		myToast = new MyToast(BNDemoMainActivity.this, 5000, Gravity.BOTTOM);
		Log.e(TAGMAIN, "初始化MyToast,结束!!!");
	}
	/*
	 * 显示自己当前位置
	 * */
	private void showSlefPos() {
		selectConn = "我的位置";
		tv_selece_sub.setText(selectConn);
		tv_selece_sub.setTextColor(Color.BLACK);
		lv_level_third.setVisibility(View.GONE);
		lv_level_second.setVisibility(View.GONE);
		ll_selece_dis.setVisibility(View.GONE);
		rl_searchInfo.setVisibility(View.GONE);
		showSubSta = false;
		showSelfPos = true;				//显示自己的位置
	}
	/***
	 * 定位结果回调，在此方法中处理定位结果
	 */
	BDLocationListener listener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub

			if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
				selfPos = null;
				selfPos = new LatLng(location.getLatitude(),location.getLongitude());


				Message locMsg = locHander.obtainMessage();
				Bundle locData;
				locData = Algorithm(location);
				LatLng tmpPos = new LatLng( locData.getDouble("Latitude"),locData.getDouble("Longitude"));
				elecSubAll.get(0).setPos(tmpPos);

				if(showSelfPos){
					if (locData != null) {
						locMsg.what = CHANGE_SELF_POS;
						locData.putParcelable("loc", location);
						locMsg.setData(locData);
						locHander.sendMessage(locMsg);
					}
				}

			}
		}
	};
	/***
	 * 接收定位结果消息，并显示在地图上
	 * 用Handler来更新UI
	 */
	@SuppressLint("HandlerLeak") private Handler locHander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == CHANGE_SELF_POS){
				try {
					BDLocation location = msg.getData().getParcelable("loc");
					if (location != null) {
						LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
						// 构建Marker图标
						BitmapDescriptor bitmap = null;
						bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 推算结果

						// 构建MarkerOption，用于在地图上添加Marker
						OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
						// 在地图上添加Marker，并显示
						mBaiduMap.clear();
						mBaiduMap.addOverlay(option);
						mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}else if(msg.what == DIMISS_DLG){
				progressDialog.dismiss();
			}else if(DIMISS_DLG_FUAIL == msg.what){
				progressDialog.dismiss();
				Log.e(TAGMAIN,  "路线规划失败，请检查网络设置");
				showToastMsg("路线规划失败，请检查网络设置");
			}else if(msg.what == ROUTPLANFUAIL){
				if(progressDialog != null){
					if(!hasRoutPlanSta){
						progressDialog.dismiss();
						Log.e(TAGMAIN,  "路线规划失败，请检查网络设置");
						showToastMsg("路线规划失败，请检查网络设置");
					}

				}
			}else if(msg.what == ROUTR_PLAN_FAILED){
				progressDialog.dismiss();
				Log.e(TAGMAIN,  "路线规划失败，请检查网络设置");
				showToastMsg("路线规划失败，请检查网络设置");
			}else if(SECOND_ADD_SUCCESS == msg.what){
				showToastMsg("添加数据成功");
				Log.d(TAGMAIN,"添加二级数据成功");
				seceAdp.notifyDataSetChanged();
			}else if(ADD_FALUE == msg.what){
				showToastMsg((String)msg.obj);
			}else if(FRIST_ADD_SUCCESS ==  msg.what){
				//添加一级内容成功
				showToastMsg("添加数据成功");
				Log.d(TAGMAIN,"添加一级数据成功");
				firstAdp.notifyDataSetChanged();
			}else if(GET_ROOT_DATA == msg.what){
				//一级目录有更新
				firstAdp.notifyDataSetChanged();
			}else if(NOTIFY_SECOND_ADP == msg.what){
				//更新耳机目录内容
				seceAdp.notifyDataSetChanged();
			}else if(NOTIFY_THRID_ADP == msg.what){
				//更新三级目录内容
				thirdAdp.notifyDataSetChanged();
			}else if(msg.what == DownFile.FILE_DOWN_PROGRESS){
				mDialog.setProcess((Integer) msg.obj);
			}else if(msg.what == DownFile.FILE_DOWN_COMPLETED){
				mDialog.dismiss();
				File newApk = new File((String) msg.obj);
				if(!newApk.exists()){
					myToast.show("下载安装包失败");
					return;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					Log.w("downFile", "版本大于 N ，开始使用 fileProvider 进行安装");
					intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					Uri contentUri = FileProvider.getUriForFile(
							getBaseContext()
							, "com.ppl.sxgtqx.fileprovider"
							, newApk);
					intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
				} else {
					Log.w("downFile", "正常进行安装");
					intent.setDataAndType(Uri.fromFile(newApk), "application/vnd.android.package-archive");
				}
				startActivity(intent);
			}else if(msg.what == DownFile.FILE_DOWN_ERROR){
				mDialog.dismiss();
				myToast.show("下载安装包失败");
			}else if(NOTIFY_RE_THRID_ADP == msg.what){
				reThirdAdp.notifyDataSetChanged();
			}
		}

	};

	/***
	 * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
	 * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
	 * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
	 *
	 * @return Bundle
	 */
	private Bundle Algorithm(BDLocation location) {
		Bundle locData = new Bundle();
		double curSpeed = 0;
		if (locationList.isEmpty() || locationList.size() < 2) {
			LocationEntity temp = new LocationEntity(location,System.currentTimeMillis());
			locData.putInt("iscalculate", 0);
			locationList.add(temp);
		} else {
			if (locationList.size() > 5)
				locationList.removeFirst();
			double score = 0;
			for (int i = 0; i < locationList.size(); ++i) {
				LatLng lastPoint = new LatLng(locationList.get(i).getLocation().getLatitude(),
						locationList.get(i).getLocation().getLongitude());
				LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
				double distance = DistanceUtil.getDistance(lastPoint, curPoint);
				curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).getTime()) / 1000;
				score += curSpeed * Utils.EARTH_WEIGHT[i];
			}
			if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
				location.setLongitude(
						(locationList.get(locationList.size() - 1).getLocation().getLongitude() + location.getLongitude())
								/ 2);
				location.setLatitude(
						(locationList.get(locationList.size() - 1).getLocation().getLatitude() + location.getLatitude())
								/ 2);
				locData.putInt("iscalculate", 1);
			} else {
				locData.putInt("iscalculate", 0);
			}
			MyPublicData.selfLongitude = location.getLongitude();
			MyPublicData.selfLatitude = location.getLatitude();
			locData.putDouble("Longitude", location.getLongitude());
			locData.putDouble("Latitude", location.getLatitude());
			LocationEntity newLocation = new LocationEntity(location,System.currentTimeMillis());
			locationList.add(newLocation);
		}
		return locData;
	}



	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
		mMapView.onResume();
		if(NoSearchSta){
			showSelfPos = true;
			//			mBaiduMap.clear();
			rl_searchInfo.setVisibility(View.GONE);
			ib_naviga.setVisibility(View.VISIBLE);
			showSlefPos();
		}
		initSQL();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
		mMapView.onPause();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
			if(!NoSearchSta){
				showSelfPos = true;
				mBaiduMap.clear();
				rl_searchInfo.setVisibility(View.GONE);
				ib_naviga.setVisibility(View.VISIBLE);
				NoSearchSta = true;
				showSlefPos();
				return false;
			}else{
				showQuitDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showQuitDialog() {
		// TODO Auto-generated method stub
		final AlertDialog alertDialog = new AlertDialog.Builder(BNDemoMainActivity.this).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_quit);


		window.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击取消
				alertDialog.dismiss();
			}
		});

		window.findViewById(R.id.dialog_confirm).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击确定
				locService.unregisterListener(listener);
				locService.stop();
				mMapView.onDestroy();
				LogcatHelper.getInstance(getApplicationContext()).stop();
				onDestroy();
				System.exit(0);
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
		super.onDestroy();
		locHander.removeCallbacksAndMessages(null);
	}
	@SuppressLint("ResourceAsColor") @Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.iv_down:
			case R.id.tv_selece_sub:
				showSubSta = !showSubSta;
				if(showSubSta){
					getRootDataSQL();
					ll_selece_dis.setVisibility(View.VISIBLE);
					lv_level_first.setVisibility(View.VISIBLE);
					lv_level_third_re.setVisibility(View.GONE);
					lv_level_third.setVisibility(View.GONE);
					lv_level_second.setVisibility(View.GONE);
				}else{
					lv_level_third.setVisibility(View.GONE);
					lv_level_second.setVisibility(View.GONE);
					ll_selece_dis.setVisibility(View.GONE);
					lv_level_third_re.setVisibility(View.GONE);
				}
				break;
			case R.id.ib_set:
				//判断三级候选地址是否显示，如果显示 隐藏
				lv_level_third.setVisibility(View.GONE);
				lv_level_second.setVisibility(View.GONE);
				ll_selece_dis.setVisibility(View.GONE);
				tv_selece_sub.setText("我的位置");
				tv_selece_sub.setTextColor(R.color.black);

				//停止监听位置
				showSelfPos = true;
				NoSearchSta = true;
				//跳转至设置中心
				goToSetting();
				break;
			case R.id.ib_naviga:
				//判断三级候选地址是否显示，如果显示 隐藏
				lv_level_third.setVisibility(View.GONE);
				lv_level_second.setVisibility(View.GONE);
				ll_selece_dis.setVisibility(View.GONE);
				tv_selece_sub.setText("我的位置");
				tv_selece_sub.setTextColor(R.color.black);

				//停止监听位置
				showSelfPos = true;
				NoSearchSta = true;

				Intent inte = new Intent(BNDemoMainActivity.this, InputStartEnd.class);
				inte.putExtra(DEFAULTSTA, true);
				startActivity(inte);
				break;
			case R.id.ib_update:
				Intent intentUpdate = new Intent();
				intentUpdate.setClass(getApplicationContext(), MainPermissionActivity.class);
				startActivity(intentUpdate);
				break;
			case R.id.ib_dist:
				Intent inteDis = new Intent(BNDemoMainActivity.this, InputStartEnd.class);
				inteDis.putExtra(DEFAULTSTA, false);
				inteDis.putExtra(DISTID, showSubId);
				startActivity(inteDis);
				break;
			case R.id.ll_subInfo:
				Intent intent = new Intent(BNDemoMainActivity.this, ElecSubShow.class);
				intent.putExtra(MyPublicData.THIRD_ID, level3);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	private void goToSetting() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BNDemoMainActivity.this, Setting.class);
		startActivity(intent);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showToastMsg(final String msg) {
		Log.e(TAGMAIN, "myToast show info------------>>>>>>>>>"+msg);
		myToast.show(msg);
	}

	/**
	 * item 点击
	 * */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()){
			case R.id.lv_level_first:
				Log.d(TAGMAIN,"点击了跟级数据");
				clickFirst(position);
				break;
			case R.id.lv_level_second:
				Log.d(TAGMAIN,"点击了2级数据");
				clickSecond(position);
				break;
			case R.id.lv_level_third_re:
				Log.d(TAGMAIN,"点击了3级数据");
				clickReThird(position);
				break;
			case R.id.lv_level_third:
				Log.d(TAGMAIN,"点击了4级数据");
				showSelectDis(position);
				break;
		}
	}

	private void showSelectDis(int position) {
		Log.d(TAGMAIN,"=============333333333333333>>>>>>>>>>>点击"+position);
		mBaiduMap.clear();
		lv_level_third.setVisibility(View.GONE);
		lv_level_second.setVisibility(View.GONE);
		ll_selece_dis.setVisibility(View.GONE);
		showSubSta = false;
		level3 = position;

		selectThird = MyPublicData.levelThird.get(position).getConn();
		selectConn =selectRoot +"  "+selectSecond+" "+ selectReThird + " "+selectThird;
		tv_selece_sub.setText(selectConn);
		tv_selece_sub.setTextColor(Color.GREEN);
		//定位到搜索位置
		//底层显示
		showSubInfo(level3);
		NoSearchSta = false;
	}

	private void clickReThird(int position) {
		Log.d(TAGMAIN,"=============r3r3r3r3r>>>>>>>>>>>点击"+position);
		levelRe3 = position;

		if("返回上一级".equalsIgnoreCase(MyPublicData.levelReThird.get(position).getConn())){
			lv_level_third_re.setVisibility(View.VISIBLE);
			lv_level_third.setVisibility(View.GONE);
			lv_level_first.setVisibility(View.GONE);
			lv_level_second.setVisibility(View.VISIBLE);
			MyPublicData.levelReThird.remove(MyPublicData.levelReThird.size()-1);
			locHander.sendEmptyMessage(NOTIFY_RE_THRID_ADP);
			selectConn =selectRoot + "  "+ selectSecond;
			tv_selece_sub.setText(selectConn);
		}else {
			tv_selece_sub.setTextColor(Color.GREEN);
			lv_level_third_re.setVisibility(View.VISIBLE);
			lv_level_third.setVisibility(View.VISIBLE);
			lv_level_first.setVisibility(View.GONE);
			lv_level_second.setVisibility(View.GONE);

			selectReThird = MyPublicData.levelReThird.get(position).getConn();
			selectConn = selectRoot + "  " + selectSecond + " " + selectReThird;
			tv_selece_sub.setText(selectConn);

			//请求二级目录
			Log.e(TAGMAIN, "请求三级目录，二级id：" + MyPublicData.levelSond.get(level2).getSelfId());

			getThirdDate(selectSecondId, MyPublicData.levelReThird.get(position).getSelfId());
		}
	}

	/**
	 * 点击的第二级item
	 * */
	private void clickSecond(int position) {
		Log.d(TAGMAIN,"=============2222222222222>>>>>>>>>>>点击"+position);


		if("返回上一级".equalsIgnoreCase(MyPublicData.levelSond.get(position).getConn())){
			lv_level_third_re.setVisibility(View.GONE);
			lv_level_third.setVisibility(View.GONE);
			lv_level_first.setVisibility(View.VISIBLE);
			MyPublicData.levelSond.remove(MyPublicData.levelSond.size()-1);
			locHander.sendEmptyMessage(NOTIFY_SECOND_ADP);
			selectConn =selectRoot;
			tv_selece_sub.setText(selectConn);
		}else{
			level2 = position;
			tv_selece_sub.setTextColor(Color.GREEN);
			lv_level_third_re.setVisibility(View.VISIBLE);
			lv_level_third.setVisibility(View.GONE);
			lv_level_first.setVisibility(View.GONE);

			//请求二级目录
			Log.e(TAGMAIN, "请求三级目录，二级id："+MyPublicData.levelSond.get(level2).getSelfId());

			selectSecond = MyPublicData.levelSond.get(level2).getConn();
			selectConn =selectRoot + "  "+ selectSecond;
			tv_selece_sub.setText(selectConn);

			selectSecondId = MyPublicData.levelSond.get(level2).getSelfId();

			getReThirdDate();
		}
	}

	/**
	 * 点击的是第一级item
	 *
	 * @param position
	 * */
	private void clickFirst(int position) {
		level1 = position;
		Log.d(TAGMAIN,"=============>>>>>>>>>>>点击一级listView");
		if(position == 0){
			Log.d(TAGMAIN,"=============>>>>>>>>>>>点击我的位置");
			showSlefPos();
		}else{
			NoSearchSta = false;
			Log.d(TAGMAIN,"=============1111111111111>>>>>>>>>>>点击"+position);
			//					View v=parent.getChildAt(position);
			//					v.setBackgroundColor(Color.GRAY);
			selectConn = MyPublicData.levelFirst.get(position).getConn();
			tv_selece_sub.setTextColor(Color.GREEN);
			tv_selece_sub.setText(selectConn);

			ll_selece_dis.setVisibility(View.VISIBLE);
			lv_level_third.setVisibility(View.GONE);
			lv_level_second.setVisibility(View.VISIBLE);

			selectRoot = MyPublicData.levelFirst.get(level1).getConn();
			selectConn = selectRoot;
			tv_selece_sub.setText(selectConn);

			//请求二级目录
			Log.e(TAGMAIN, "请求二级目录，一级id："+MyPublicData.levelFirst.get(level1).getSelfId());
			getSecondDataSQL(MyPublicData.levelFirst.get(level1).getSelfId());
		}
	}
}