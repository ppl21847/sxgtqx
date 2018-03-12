package com.ppl.sxgtqx.com.baidu.navi.sdkdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.EditText;
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
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
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
import com.ppl.sxgtqx.utils.ElecSubInfo;
import com.ppl.sxgtqx.utils.LevelRoot;
import com.ppl.sxgtqx.utils.LevelSecond;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.utils.LocationEntity;
import com.ppl.sxgtqx.utils.LogcatHelper;
import com.ppl.sxgtqx.utils.MyPublicData;
import com.ppl.sxgtqx.utils.UpdateEntity;
import com.ppl.sxgtqx.view.CompletedView;
import com.ppl.sxgtqx.view.CustomDialog;
import com.ppl.sxgtqx.view.MyToast;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

@SuppressLint("NewApi") public class BNDemoMainActivity extends Activity implements OnClickListener{
	public static String TAGMAIN = "TagMainAty";
	public static List<Activity> activityList = new LinkedList<Activity>();

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";

	public static boolean hasInitSuccess = false;
	public static boolean hasRoutPlanSta = false;

	//基础地图 + 定位
	public static final String DEFAULTSTA = "defaltSta";
	public static final String DISTID = "distinationId";
	public static final String STARTID = "startId";

	protected static final int CHANGE_SELF_POS = 7;
	protected static final int DIMISS_DLG = 8;
	private static final int DIMISS_DLG_FUAIL = 9;
	protected static final int ROUTPLANFUAIL = 10;
	private static final int ROUTR_PLAN_FAILED = 11;
	private static final int SECOND_ADD_SUCCESS = 12;
	private static final int FRIST_ADD_SUCCESS = 13;
	private static final int ADD_FALUE = 14;
	protected static final int DATA_REPAIR_DONE = 16;
	protected static final int GET_ROOT_DATA = 15;
	protected static final int NOTIFY_SECOND_ADP = 17;
	private static final int NOTIFY_THRID_ADP = 18;
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
	ListView lv_level_first,lv_level_second,lv_level_third;
	int level1=0,level2=0,level3=0;
	String selectConn = "";
	ImageView iv_down;
	ImageButton ib_set;
	EditText et_level_conn;

	private LocationService locService;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果


	public static List<ElecSubInfo>elecSubAll = new ArrayList<ElecSubInfo>();
	MainSprinAdapter firstAdp,seceAdp,thirdAdp;
	TextView tv_distance,tv_father;
	private CompletedView completedView;
	private LinearLayout mLLProgress;

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
	private Context context;
	private CustomDialog.Builder builder;
	private CustomDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityList.add(this);

		checkPermissions(needPermissions);
		setContentView(R.layout.activity_main);

		context = this;
		initDataSQL();
		initView();
		initMap();
		initLocListView();
		checkUpdate();
		initSQL();//与服务器数据同步
	}

	/**
	 * 检查更新
	 * */
	private void checkUpdate() {
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
						Log.d("apkInfo","versionName: "+versionName+",versioncode: "+versioncode);

						if(versioncode >= 1){
							//校验是否更新
							if(object.getVersionCode() > versioncode){
								//弹出更新提示框
								mDialog = builder.setMessage(object.getUpdateDescroption())
										.setPositiveButton("升级", new OnClickListener() {
											@Override
											public void onClick(View v) {
												//开启现在进度显示
												mDialog.dismiss();
												mLLProgress.setVisibility(View.VISIBLE);

												downNewFile(object.getApkUrl());
											}
										})
										.createTwoButtonDialog();
								mDialog.show();
							}
						}
					} catch (PackageManager.NameNotFoundException e1) {
						e1.printStackTrace();
					}
				}else{
					Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
				}
			}

		});
	}


	/**
	 * 下载最新的安装包
	 * */
	private void downNewFile(String apkUrl) {
		//得到当前外部存储设备的目录
		String SDCardRoot= Environment.getExternalStorageDirectory()+ File.separator;
		//File.separator为文件分隔符”/“,方便之后在目录下创建文件
		String fileDir = "sxgtxlt";
		File dirFile=new File(SDCardRoot+File.separator+fileDir);
		if(!dirFile.exists()){
			dirFile.mkdir();        //创建文件夹
		}

		final String updateFile = dirFile + File.separator + "new_sxgtqx.apk";
		final File newApk = new File(updateFile);
		if(newApk.exists()){
			newApk.delete();
		}
		FileDownloader.getImpl().create(apkUrl)
				.setPath(updateFile)
				.setListener(new FileDownloadListener() {
					@Override
					protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
						Log.d("downFile","pending");
					}

					@Override
					protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
						Log.d("downFile","connected");
					}

					@Override
					protected void progress(BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
						final int progress = soFarBytes * 100 / totalBytes;
						Log.d("downFile","progress: "+progress);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								completedView.setProgress(progress);
							}
						});

					}

					@Override
					protected void blockComplete(BaseDownloadTask task) {
						Log.d("downFile","blockComplete");
					}

					@Override
					protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
						Log.d("downFile","retry");
					}

					@Override
					protected void completed(BaseDownloadTask task) {
						//安装
						Log.d("downFile","completed");

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
					}

					@Override
					protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
						Log.d("downFile","paused");
					}

					@Override
					protected void error(BaseDownloadTask task, Throwable e) {
						Log.d("downFile","error");
					}

					@Override
					protected void warn(BaseDownloadTask task) {
						Log.d("downFile","error");
					}
				}).start();
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
				Log.e(TAGMAIN, "--3333333333333333开始获取san级地址信息");
				syncThird();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(TAGMAIN, "--22222222开始获取二级地址信息");
				syncSecondSQL();
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(TAGMAIN, "---1111111111111开始获取一级地址信息");
				syncFirstSQL();
			}
		}).start();
	}
	protected void syncFirstSQL() {
		Log.e(TAGMAIN, "请求根木录信息-------------------------00000000000000000000000");
		BmobQuery<LevelRoot> query = new BmobQuery<LevelRoot>();
		query.addWhereEqualTo("delSta", 1);
		query.findObjects(new FindListener<LevelRoot>() {
			@Override
			public void done(List<LevelRoot> data, BmobException e) {
				if(e==null){
					Log.e(TAGMAIN, "get LevelRoot data.size():"+data.size());
					List<LevelRoot>tmpRoot = dbHelper.getRootData();
					if(tmpRoot == null){
						for(int i=0;i<data.size();i++){
							Log.e(TAGMAIN, "get LevelRoot data("+i+"):"+data.get(i).toString());
							Log.e(TAGMAIN, "添加至本地数据库: "+data.get(i).toString());
							LevelRoot tmpData = new LevelRoot(data.get(i).getObjectId(),
									data.get(i).getName(), 0, 1);
							//添加至本地数据库
							dbHelper.addFirst(tmpData);
						}
					}else{
						//更新数据库
						Log.e(TAGMAIN, "服务器端根木录有删除位置信息删除前数据： "+tmpRoot.size());
						for(int j=0;j<tmpRoot.size();){
							Log.e(TAGMAIN, j+"  : " +tmpRoot.get(j).toString());
							boolean findSta = true;
							for(int i=0;i<data.size();i++){
								Log.e(TAGMAIN, i+"  : " +data.get(i).toString());
								if(tmpRoot.get(j).getID().equals(
										data.get(i).getObjectId())){
									findSta = false;
									Log.e(TAGMAIN, "存在该条数据，更新");
									dbHelper.updataFirstInfo(new LevelRoot(
											data.get(i).getObjectId(),
											data.get(i).getName(),
											data.get(i).getLevel(),
											data.get(i).getDelSta()));
								}
							}
							if(findSta){
								//服务器已删除该条数据
								Log.e(TAGMAIN, "服务器已删除该条数据： "+tmpRoot.get(j).toString());
								dbHelper.deleteRoot(tmpRoot.get(j).getID());
								tmpRoot.remove(j);
							}else{
								j++;
							}
						}
						//保存至本地数据库
						if(data.size() > tmpRoot.size()){
							for(int i=0;i<data.size();i++){
								Log.e(TAGMAIN, "服务器一级地址，第 "+i+" 条，数据为： "+data.get(i).toString());
								Log.e(TAGMAIN, "get LevelRoot data("+i+"):"+data.get(i).toString());
								//有用位置 未被删除
								boolean isNewSta = false;
								for(int j=0;j<tmpRoot.size();j++){
									if(tmpRoot.get(j).getID().equals(
											data.get(i).getObjectId())){
										isNewSta = true;
										break;
									}
								}
								if(!isNewSta){
									Log.e(TAGMAIN, "添加至本地数据库: "+data.get(i).toString());
									LevelRoot tmpData = new LevelRoot(data.get(i).getObjectId(),
											data.get(i).getName(), 0, 1);
									//添加至本地数据库
									dbHelper.addFirst(tmpData);
								}
							}
						}
					}
				}else{
					Log.e(TAGMAIN, "网络连接失败，获取一级地址失败");
				}
			}
		});
	}
	protected void syncSecondSQL() {
		Log.e(TAGMAIN, "获取服务器二级地址信息----------22222222");
		BmobQuery<LevelSecond> query = new BmobQuery<LevelSecond>();
		//查询delSta叫“1”的数据
		int delSta = 1;
		query.addWhereEqualTo("delSta", delSta);
		//返回50条数据，如果不加上这条语句，默认返回10条数据
		//		query.setLimit(50);
		//执行查询方法
		query.findObjects(new FindListener<LevelSecond>() {
			@Override
			public void done(List<LevelSecond> data, BmobException e) {
				if(e==null){
					Log.e(TAGMAIN, "get LevelSecond data.size():"+data.size());
					//更新本地数据库		服务器端有数据删除，本地需要删除
					List<LevelSecond>tmpDataNet = dbHelper.getSecondData();

					if(tmpDataNet == null){
						Log.e(TAGMAIN, "本地数据库没有二级地址");
						//添加新数据
						for(int j=0;j<data.size();j++){
							Log.e(TAGMAIN, "本地不存在该条数据,需要添加至本地： "+data.get(j).toString());
							LevelSecond tmpData = new LevelSecond(data.get(j).getFatherId(),
									data.get(j).getObjectId(), data.get(j).getName(), 1, 1);
							Log.e(TAGMAIN, "像数据库添加的数据为  ： "+tmpData.toString());
							dbHelper.addSecond(tmpData);
						}
					}else{
						Log.e(TAGMAIN, "本地数据库二级地址有："+tmpDataNet.size()+" 条");
						for(int i=0;i<tmpDataNet.size();){
							boolean deleSta = false;
							for(int j=0;j<data.size();j++){
								if(tmpDataNet.get(i).getID().equals(data.get(j).getObjectId())){
									//服务器存在该条数据 不用删除 
									Log.e(TAGMAIN, "服务器同样存在该条数据： "+tmpDataNet.get(i).toString());
									Log.e(TAGMAIN, "更新该条数据");
									dbHelper.updataSecondInfo(new LevelSecond(
											data.get(j).getFatherId(),
											data.get(j).getObjectId(),
											data.get(j).getName(),
											data.get(j).getLevel(),
											data.get(j).getDelSta()));
									deleSta = true;
									break;
								}
							}
							if(!deleSta){
								//服务器已经删除该数据
								Log.e(TAGMAIN, "服务器已删除该条数据： "+tmpDataNet.get(i).toString());
								dbHelper.deleteSecond(tmpDataNet.get(i).getID());
								tmpDataNet.remove(i);
							}else{
								i++;
							}
						}
						//--------->>>>>>>>完成本地数据库数据删除
						if(data.size() > tmpDataNet.size()){
							//添加新数据
							for(int j=0;j<data.size();j++){
								Log.e(TAGMAIN, "服务器二级地址，第 "+j+" 条，数据为： "+data.get(j).toString());
								boolean addSta = false;
								for(int i=0;i<tmpDataNet.size();i++){
									if(tmpDataNet.get(i).getObjectId().equals(data.get(j).getID())){
										//本地存在 不用新添加
										Log.e(TAGMAIN, "本地存在该条数据： "+tmpDataNet.get(i).toString());
										addSta = true;
										break;
									}
								}
								if(!addSta){
									Log.e(TAGMAIN, "本地不存在该条数据,需要添加至本地： "+data.get(j).toString());
									LevelSecond tmpData = new LevelSecond(data.get(j).getFatherId(),
											data.get(j).getObjectId(), data.get(j).getName(), 1, 1);
									dbHelper.addSecond(tmpData);
								}
							}
						}
					}
				}else{
					Log.e(TAGMAIN, "2222222222222222222222连接服务器失败，获取二级地址失败!!!!!!!!" +e.getMessage()+","+e.getErrorCode());
				}
			}
		});
	}
	//同步三级地址信息
	private void syncThird() {
		Log.e(TAGMAIN, "请求服务器三级地址----------3333333333");
		BmobQuery<ConnType>query = new BmobQuery<ConnType>();
		query.addWhereEqualTo("delSta", 1);
		query.findObjects(new FindListener<ConnType>() {
			@Override
			public void done(List<ConnType> data, BmobException e) {
				if(e == null){
					Log.e(TAGMAIN, "从服务器获取三级目录信息数量："+data.size());
					//更新数据库没用信息    服务器已删除  本地还有
					List<LevelThird>tmpDataList = dbHelper.getThirdData();
					if(tmpDataList == null){
						Log.e(TAGMAIN, "数据库没有三级地址数据");
						Log.e(TAGMAIN, "向本地数据库添加"+data.size()+"条，三级数据");
						for(int i=0;i<data.size();i++){
							LevelThird tmpData = new LevelThird(data.get(i).getFatherId(),
									data.get(i).getObjectId(), data.get(i).getConn(), 2,
									data.get(i).getInfo(), data.get(i).getPosLat(),
									data.get(i).getPosLong(), data.get(i).getNetImgsPath());
							Log.e(TAGMAIN, "向数据库添加的新数据是： "+data.get(i).toString());
							dbHelper.addThird(tmpData);
						}
					}
					else{
						Log.e(TAGMAIN, "本地数据库获取三级目录信息数量："+tmpDataList.size());
						for(int i=0;i<tmpDataList.size();){
							Log.e(TAGMAIN, "本地数据库获取三级目录信息 ,第："+i+" 条信息时： "+tmpDataList.get(i).toString());
							boolean deleSta = false;
							for(int j=0;j<data.size();j++){
								if(data.get(j).getObjectId().equals(tmpDataList.get(i).getID())){
									//有用数据
									Log.e(TAGMAIN, "该条数据有用："+data.get(j).toString());
									deleSta = true;
									//更新
									Log.e(TAGMAIN, "更新数据");
									dbHelper.updataThirdInfo(new LevelThird(
											data.get(i).getFatherId(),
											data.get(i).getObjectId(),
											data.get(i).getConn(),
											data.get(i).getLevel(),
											data.get(i).getInfo(),
											data.get(i).getPosLat(),
											data.get(i).getPosLong(),
											data.get(i).getNetImgsPath()));
									break;
								}
							}if(!deleSta){
								Log.e(TAGMAIN, "服务器已删除该条数据: "+tmpDataList.get(i).toString());
								dbHelper.deleteThird(tmpDataList.get(i).getID());
								tmpDataList.remove(i);
							}else{
								i++;
							}
						}
						//更新本地数据库

						if(data.size() > tmpDataList.size()){
							//要更新本地数据库
							//添加新数据至服务器
							for(int i=0;i<data.size();i++){
								Log.e(TAGMAIN, "服务器三级地址，第 "+i+" 条，数据为： "+data.get(i).toString());
								boolean addSta = false;
								for(int j=0;j<tmpDataList.size();j++){
									if(tmpDataList.get(j).getID().equals(data.get(i).getObjectId())){
										//不是新数据
										addSta = true;
										Log.e(TAGMAIN, "不是新数据："+data.get(i).toString());
										break;
									}
								}
								if(!addSta){
									LevelThird tmpData = new LevelThird(data.get(i).getFatherId(),
											data.get(i).getObjectId(), data.get(i).getConn(), 2,
											data.get(i).getInfo(), data.get(i).getPosLat(),
											data.get(i).getPosLong(), data.get(i).getNetImgsPath());
									Log.e(TAGMAIN, "向数据库添加的新数据是： "+data.get(i).toString());
									dbHelper.addThird(tmpData);
								}
							}
						}
					}
				}else{
					Log.e(TAGMAIN, "2222222222222222222222222获取三级地址失败");
				}
			}
		});
	}
	private void initLocListView() {
		MyPublicData.levelFirst = new ArrayList<ConnType>();
		MyPublicData.levelSond = new ArrayList<ConnType>();
		MyPublicData.levelThird = new ArrayList<ConnType>();
		SharedPreferences sharedPreferences = this.getSharedPreferences("com.ppl.sxgtqx", MODE_PRIVATE);
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		Editor editor = sharedPreferences.edit();
		if (isFirstRun)
		{
			Log.d("debug", "第一次运行");
			editor.putBoolean("isFirstRun", false);
			editor.commit();
			dbHelper.addFirst(new LevelRoot("aaaa49a5f6", "太原供电段", 0,1));
			dbHelper.addSecond(new LevelSecond("aaaa49a5f6", "ZjJEGGGI", "晋中高铁供电车间", 1,1));
			dbHelper.addSecond(new LevelSecond("aaaa49a5f6", "46cce70cb1", "忻州西高铁供电车间", 1,1));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "7d8b932986", "晋中供电车间", 2, "晋中供电车间设有晋中网电工区、晋中变电工区2个班组，祁县东、太谷西站2个应急点，晋中、祁县东2座10KV无人值守配电室，晋中、祁县东2座牵引变电所，郝村、东观、新胜3座无人值守AT所，太原南、南郭2座无人值守分区所，和晋中、太谷西、祁县东3座给水所，主要担负着大西高铁太原南大西场（含）-平遥古城站(不含)间4个站场、4个区间，共计91.6正线公里的高铁牵引供电、电力、给水设备的运行、维护和应急处理任务，共管理接触网设备260条公里。"
					, 37.695969, 112.657084, ""));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "5aea39bafa", "祁县东变电所", 2, "车间设有晋中网电工区、晋中变电工区2个班组，祁县东、太谷西站。"
					, 37.319237, 112.354332, ""));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "xFup777Z", "南郭分区所", 2, ""
					, 37.4848, 112.558225, ""));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "7XVy666F", "郝村AT所", 2, ""
					, 37.591771, 112.593876, ""));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "key4222L", "东观AT所", 2, ""
					, 37.409576, 112.455842, ""));
			dbHelper.addThird(new LevelThird("ZjJEGGGI", "NbDZ333G", "新胜AT所", 2, ""
					, 27.173662, 118.390551, ""));
		} else{
			Log.d("debug", "不是第一次运行");
		}

		MyPublicData.rootData = dbHelper.getRootData();
		Log.e(TAGMAIN, "MyPublicData.rootData.size():"+MyPublicData.rootData.size());
		MyPublicData.levelFirst.add(new ConnType(0, 0, "我的位置", "0",1));
		//从本地数据哭中获取一级根目录内容
		for(int i=0;i<MyPublicData.rootData.size();i++){
			Log.e(TAGMAIN, MyPublicData.rootData.get(i).toString());
			MyPublicData.levelFirst.add(new ConnType(1, 0,
					MyPublicData.rootData.get(i).getName(), MyPublicData.rootData.get(i).getID(),1));
		}
		MyPublicData.levelFirst.add(new ConnType(2, 0, "添加", "",1));
		Log.e(TAGMAIN, "MyPublicData.rootData.size():"+MyPublicData.rootData.size());

		MyPublicData.secondData = new ArrayList<LevelSecond>();
		MyPublicData.thirdData = new ArrayList<LevelThird>();
		//		MyPublicData.levelSond.add(new ConnType(2, 1, "添加", "",1));
		Log.e(TAGMAIN, "MyPublicData.levelSond.size():"+MyPublicData.levelSond.size());
		MyPublicData.levelThird.add(new ConnType(2, 2, "新建", "",1));
		Log.e(TAGMAIN, "MyPublicData.levelThird.size():"+MyPublicData.levelThird.size());

		//初始化候选列表
		firstAdp = new MainSprinAdapter(MyPublicData.levelFirst, getApplicationContext());
		seceAdp = new MainSprinAdapter(MyPublicData.levelSond, getApplicationContext());
		thirdAdp = new MainSprinAdapter(MyPublicData.levelThird, getApplicationContext());

		lv_level_first.setAdapter(firstAdp);
		lv_level_second.setAdapter(seceAdp);
		lv_level_third.setAdapter(thirdAdp);

		lv_level_first.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				level1 = position;
				//				for(int i=0;i<parent.getCount();i++){
				//					View v=parent.getChildAt(i);
				//					v.setBackgroundColor(Color.WHITE);
				//				}
				System.out.println("=============>>>>>>>>>>>点击一级listView");
				if(position == 0){
					System.out.println("=============>>>>>>>>>>>点击我的位置");
					showSlefPos();
				}else{
					NoSearchSta = false;
					System.out.println("=============>>>>>>>>>>>点击"+position);
					//					View v=parent.getChildAt(position);
					//					v.setBackgroundColor(Color.GRAY);
					selectConn = MyPublicData.levelFirst.get(position).getConn();
					tv_selece_sub.setTextColor(Color.GREEN);
					tv_selece_sub.setText(selectConn);

					ll_selece_dis.setVisibility(View.VISIBLE);
					lv_level_third.setVisibility(View.GONE);
					lv_level_second.setVisibility(View.VISIBLE);

					//请求二级目录
					Log.e(TAGMAIN, "请求二级目录，一级id："+MyPublicData.levelFirst.get(level1).getSelfId());
					getSecondDataSQL(MyPublicData.levelFirst.get(level1).getSelfId());
				}
			}
		});
		lv_level_second.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				level2 = position;
				selectConn +="  "+ MyPublicData.levelSond.get(position).getConn();
				tv_selece_sub.setText(selectConn);
				tv_selece_sub.setTextColor(Color.GREEN);
				//					View v=parent.getChildAt(position);
				//					v.setBackgroundColor(Color.GRAY);
				lv_level_third.setVisibility(View.VISIBLE);
				lv_level_first.setVisibility(View.GONE);

				//请求二级目录
				Log.e(TAGMAIN, "请求三级目录，二级id："+MyPublicData.levelSond.get(level2).getSelfId());
				getThirdDate(MyPublicData.levelSond.get(level2).getSelfId());
				NoSearchSta = false;
			}
		});

		//点击三级地址显示相应信息
		lv_level_third.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				mBaiduMap.clear();
				lv_level_third.setVisibility(View.GONE);
				lv_level_second.setVisibility(View.GONE);
				ll_selece_dis.setVisibility(View.GONE);
				showSubSta = false;
				level3 = position;

				selectConn +="  "+ MyPublicData.levelThird.get(position).getConn();
				tv_selece_sub.setText(selectConn);
				tv_selece_sub.setTextColor(Color.GREEN);
				//定位到搜索位置
				//底层显示
				showSubInfo(level3);
			}
		});
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
	/**
	 * 从数据库获取三级目录数据
	 * */
	protected void getThirdDate(String selfId) {
		List<LevelThird>tmpData = dbHelper.getThirdData(selfId);
		Log.e(TAGMAIN, "获取三级目录内容数据大小:"+tmpData.size());
		MyPublicData.levelThird.clear();

		for(int i=0;i<tmpData.size();i++){
			Log.e(TAGMAIN, tmpData.get(i).toString());
			MyPublicData.levelThird.add(new ConnType(1, 2, 1,
					tmpData.get(i).getName(),
					tmpData.get(i).getFatherId(),
					tmpData.get(i).getID(),
					tmpData.get(i).getPosLat(),
					tmpData.get(i).getPosLong(),tmpData.get(i).getInfo()));
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

		completedView = findViewById(R.id.tasks_view);
		mLLProgress = findViewById(R.id.ll_down_progress);

		Log.e(TAGMAIN, "开始初始化MyToast");
		myToast = new MyToast(BNDemoMainActivity.this, 5000, Gravity.BOTTOM);
		Log.e(TAGMAIN, "初始化MyToast,结束!!!");

		builder = new CustomDialog.Builder(this);
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
				System.out.println("添加二级数据成功");
				seceAdp.notifyDataSetChanged();
			}else if(ADD_FALUE == msg.what){
				showToastMsg((String)msg.obj);
			}else if(FRIST_ADD_SUCCESS ==  msg.what){
				//添加一级内容成功
				showToastMsg("添加数据成功");
				System.out.println("添加一级数据成功");
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
					lv_level_third.setVisibility(View.GONE);
					lv_level_second.setVisibility(View.GONE);
				}else{
					lv_level_third.setVisibility(View.GONE);
					lv_level_second.setVisibility(View.GONE);
					ll_selece_dis.setVisibility(View.GONE);
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
}
