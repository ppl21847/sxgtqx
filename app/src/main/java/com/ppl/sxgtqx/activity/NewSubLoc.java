package com.ppl.sxgtqx.activity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.album.Bimp;
import com.ppl.sxgtqx.album.BmpGalleryShow;
import com.ppl.sxgtqx.album.ImageItem;
import com.ppl.sxgtqx.album.ImageUtils;
import com.ppl.sxgtqx.album.PicAdapter;
import com.ppl.sxgtqx.album.PublicAlem;
import com.ppl.sxgtqx.application.LocationApplication;
import com.ppl.sxgtqx.com.baidu.navi.sdkdemo.BNDemoMainActivity;
import com.ppl.sxgtqx.service.LocationService;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.FileUtils;
import com.ppl.sxgtqx.utils.LevelThird;
import com.ppl.sxgtqx.utils.MyPublicData;
import com.ppl.sxgtqx.utils.ProgressDia;
import com.ppl.sxgtqx.view.AuthPosAlertDialog;
import com.ppl.sxgtqx.view.MyGridView;
import com.ppl.sxgtqx.view.MyToast;

public class NewSubLoc extends Activity implements OnClickListener{
	protected static final int CHANGE_SELF_POS = 7;
	protected static final int COPY_FILE_FAILE = 15;
	protected static final int CHINGE_TOAST = 11;
	protected static final int UPFILE_FAILUE = 19;
	protected static final int UPBAK_SUCCESS = 13;
	public static final int GRIDVIEW_SCALE = 3;
	protected static final int GET_DATA_FAUIL = 16;
	protected static final int NOTIFY_GRIDVIEW = 17;
	private static final int TOAST_NO_POS = 20;
	MyGridView picGridView;
	ImageButton ib_back;		//后退键
	boolean backSta = true;		//是否已修改信息
	Activity mActivity;

	public static PicAdapter picAdapter;
	public static List<ImageItem>arrySelectImg;	//从相册选择的图片

	private static final String TAG = "NewSubLoc";
	private static final int SET_POS = 33;
	public static int[] selectImgPos = new int[9];

	//定位
	LatLng selfPos;		//定位自己的位置
	EditText et_sub_name,et_sub_info;
	ImageButton ib_selp_pos;

	// 构建Marker图标
	BitmapDescriptor bitmap = null;
	long preTimeMi = 0;

	//百度地图
	private MapView bdmp_self_pos;
	private BaiduMap selfBDMP;
	double lat=0,longPos=0;
	double latEdit = 0.0,longEdit = 0.0;

	//保存
	TextView ib_save_sub_info;

	ConnType savaInfo;
	File imgThPath;			//将图片保存至本地
	String thImgDir="noUser";
	Gson gson;				//图片组 将返回的图片组 以JSON格式保存
	List<String>picListNet;		//服务器
	List<String>localImg;	//本地图片
	ProgressDialog upPD;
	String fatherId;
	String reFatherId;
	boolean newSub = true;			//true-新建一个变电所信息     false-编辑变电所信息
	String selfId;
	LevelThird editData;
	TextView tv_sub_title;

	MyToast myToast;
	boolean saveEnable = true;

	private boolean selectPosSta = false;		//点击地图或者点击定位 更新地图坐标
	private boolean clickSelfpos = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_sub);
		mActivity = this;
		savaInfo = new ConnType();
		newSub = getIntent().getBooleanExtra("NEW", true);
		gson = new Gson();
		localImg = new ArrayList<String>();
		arrySelectImg = new ArrayList<ImageItem>();
		boolean downImgSta = true;

		if(newSub){
			Log.e(TAG, "新建变电所信息");
			lat = getIntent().getDoubleExtra("Lat", 0);
			longPos = getIntent().getDoubleExtra("Long", 0);
			fatherId = getIntent().getStringExtra("fatherId");
			reFatherId = getIntent().getStringExtra("reFatherId");
			Log.e(TAG, "fatherId: "+fatherId+",reFatherId:  "+reFatherId);
			latEdit = 0.0;
			longEdit = 0.0;
		}else{
			Log.e(TAG, "编辑变电所信息");
			selfId = getIntent().getStringExtra("SELFID");
			editData = BNDemoMainActivity.dbHelper.getThird(selfId);
			savaInfo = new ConnType(1, 2, editData.getName(),
					editData.getFatherId(),reFatherId,
					selfId,
					editData.getPosLat(),
					editData.getPosLong(),
					1);
			Log.e(TAG, "要编辑的变电所信息: "+editData.toString());
			latEdit = editData.getPosLat();
			longEdit = editData.getPosLong();
			fatherId = editData.getFatherId();
			reFatherId = editData.getFatherReId();
		}

		initView();
		initData();
		initMap();
		if(!newSub){
			Log.e(TAG, "下载服务器上的图片");
			ProgressDia.showLoadingDialog(NewSubLoc.this, "正在从服务器请求数据", true);
			getSubImg(selfId);
		}
		setMapCenter();
	}

	private void initMap() {
		selfBDMP = bdmp_self_pos.getMap();
		selfBDMP.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		selfBDMP.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

		selfBDMP.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng point) {
				selectPosSta = true;
				lat=point.latitude;
				longPos=point.longitude;
				Log.e("location", "lat: "+lat+",longPos: "+longPos);
				setSlefPos(point.latitude, point.longitude);
			}
		});
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

				lat = location.getLatitude();
				longPos = location.getLongitude();
				Log.e("location", "lat: "+lat+",longPos: "+longPos);
				if(clickSelfpos){
					Log.e("location", "定位当前位置："+"lat: "+lat+",longPos: "+longPos);
					setMapCenter();
				}
			}
		}

	};
	/**
	 * 获取服务器上变电所图片
	 * */
	private void getSubImg(String selfId) {
		//查找Person表里面id为6b6c11c537的数据
		Log.e(TAG, "向服务器获取数据，三级数据ID： "+selfId);
		BmobQuery<ConnType> bmobQuery = new BmobQuery<ConnType>();
		bmobQuery.getObject(selfId, new QueryListener<ConnType>() {
			@Override
			public void done(ConnType data, BmobException e) {
				if(e == null){
					Log.e(TAG, "获取服务器数据成功"+data.toString());
					Log.e(TAG, "开始下载图片");
					downLoadImgs(data);
				}else{
					ProgressDia.closeLoadingDialog();
					Log.e(TAG, "获取服务器数据失败");
					handler.sendEmptyMessage(GET_DATA_FAUIL);
				}
			}
		});
	}

	protected void downLoadImgs(ConnType data) {
		Type type = new TypeToken<ArrayList<String>>() {}.getType();
		final List<String>ImgPath = gson.fromJson(data.getNetImgsPath(), type);
		//地图显示当前位置
		double getLat = data.getPosLat();
		double getLong = data.getPosLong();
		if(getLat != 0 && getLong != 0){
			latEdit = getLat;
			longEdit = getLong;
			handler.sendEmptyMessage(SET_POS);
		}
		if(ImgPath != null){
			Log.e(TAG, "服务器上图片有："+ImgPath.size()+"条");
			//开一个新线程下载图片
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.e(TAG, "开始下载图片");
					downImgs(ImgPath);
				}
			}).start();
		}else{
			ProgressDia.closeLoadingDialog();
			Log.e(TAG, "该地方没有上传过图片");
		}
	}

	/**
	 * 从服务器下载图片
	 * * @param fileName 文件名(必填)
	 * 注：必须要有文件名和文件的完整url地址，group可为空
	 * BmobFile bmobfile =new BmobFile("xxx.png","","http://bmob-cdn-2.b0.upaiyun.com/2016/04/12/58eeed852a7542cb964600c6cc0cd2d6.png")；
	 * */
	protected void downImgs(final List<String> imgPath) {
		Log.e(TAG, "进入图片下载函数");
		//允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
		for(int i=0;i<imgPath.size();i++){
			Log.e(TAG, "开始第"+i+" zhang图片");
			int posLase = imgPath.get(i).lastIndexOf("/");
			final String fileName = imgPath.get(i).substring(posLase+1);
			Log.e(TAG, "fileName: "+fileName);
			File checkExists = null;
			checkExists = new File(imgThPath, fileName);
			Log.e(TAG, "服务器上图片地址: "+checkExists.getPath());
			if(!checkExists.exists()){
				saveEnable = false;
				//检测本地是否以存在该文件
				final File outImg = new File(imgThPath, fileName);
				Log.e(TAG, "输出文件地址: "+outImg.getPath());
				//本地没有该文件下载
				BmobFile bmobfile =new BmobFile(outImg.getPath(),"",imgPath.get(i));
				bmobfile.download(outImg, new DownloadFileListener() {
					@Override
					public void onProgress(Integer arg0, long arg1) {

					}
					@Override
					public void done(String savaPath, BmobException e) {
						if(e==null){
							Log.e(TAG, "下载图片网络地址："+fileName+",本地地址："+savaPath);
							arrySelectImg.add(new ImageItem(savaPath,true));
							handler.sendEmptyMessage(NOTIFY_GRIDVIEW);
							if(arrySelectImg.size() == imgPath.size()){
								saveEnable = true;
								Log.e(TAG, "下载所有图片结束,更新GridView 显示");
							}
						}else{
							ProgressDia.closeLoadingDialog();
							Log.e(TAG, "下载失败："+e.getMessage()+","+e.getErrorCode());
						}
					}
				});
			}
			else{
				Log.e(TAG, "本地已存在该图片");
				ProgressDia.closeLoadingDialog();
				arrySelectImg.add(new ImageItem(checkExists.getPath(),true));
				handler.sendEmptyMessage(NOTIFY_GRIDVIEW);
				ProgressDia.closeLoadingDialog();
				if(arrySelectImg.size() == imgPath.size()){
					Log.e(TAG, "下载所有图片结束,更新GridView 显示");
				}
			}
		}
	}

	private void setMapCenter() {
		if(lat == 0 || longPos==0){
			return;
		}else{
			setSlefPos(lat, longPos);
		}
	}

	private void initData() {
		myToast = new MyToast(NewSubLoc.this, 5000, Gravity.BOTTOM);
		ib_back.setOnClickListener(this);
		ib_selp_pos.setOnClickListener(this);
		ib_save_sub_info.setOnClickListener(this);
		//		mBaiduMap = bdmp_self_pos.getMap();
		//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

		picGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		picAdapter = new PicAdapter(mActivity,NewSubLoc.this, picGridView, arrySelectImg);
		picGridView.setAdapter(picAdapter);
		picGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				if(arg2 == arrySelectImg.size()){
					ImageUtils.showImagePickDialog(mActivity);
				}
				else {
					//显示大图 待处理
					Intent i = new Intent();
					Integer tmpPos = arg2;
					i.putExtra(PublicAlem.SHOW_PICS_POS, tmpPos);
					i.setClass(NewSubLoc.this, BmpGalleryShow.class);
					startActivity(i);
				}
			}
		});

		File mTempDir = new File( Environment.getExternalStorageDirectory(),"sxgtqx");
		if(!mTempDir.exists()){
			mTempDir.mkdirs();
		}
		imgThPath = new File(mTempDir,thImgDir);
		if(!imgThPath.exists()){
			imgThPath.mkdirs();
		}
		Log.e(TAG, imgThPath.getPath());
		picListNet = new ArrayList<String>();
	}

	private void initView() {
		// TODO Auto-generated method stub
		picGridView = (MyGridView) findViewById(R.id.noScrollgridview);
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		bdmp_self_pos = (MapView) findViewById(R.id.bdmp_self_pos);

		et_sub_name = (EditText) findViewById(R.id.et_sub_name);
		et_sub_info = (EditText) findViewById(R.id.et_sub_info);
		ib_selp_pos = (ImageButton) findViewById(R.id.ib_selp_pos);
		ib_save_sub_info = (TextView) findViewById(R.id.ib_save_sub_info);
		tv_sub_title = (TextView) findViewById(R.id.tv_sub_title);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_sub_name.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(et_sub_info.getWindowToken(), 0);

		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);

		if(!newSub){
			et_sub_name.setText(editData.getName());
			et_sub_info.setText(editData.getInfo());
			tv_sub_title.setText("编辑 "+savaInfo.getConn());
		}

	}
	public static Handler handlerPics = new Handler(){
		public void handleMessage(android.os.Message msg){
			if(msg.what == PublicAlem.UPDATA_PICS_GRIDVIEW){
				if(picAdapter != null){
					picAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.ib_back:
				if(et_sub_info.length()!=0 || et_sub_name.length()!=0 || arrySelectImg.size()!=0){
					backSta = false;
				}
				if(backSta){
					Intent mIntent = new Intent();
					mIntent.putExtra("EDIT_STA", "0");
					// 设置结果，并进行传送
					this.setResult(Activity.RESULT_OK, mIntent);
					finish();
					onDestroy();
				}else{
					AlertWarnning();
				}
				break;
			case R.id.ib_selp_pos:
				clickSelfpos = true;
				selectPosSta = true;
				if(lat == 0.0 || longPos == 0.0){
					lat = MyPublicData.selfLatitude;
					longPos = MyPublicData.selfLongitude;
				}
				setSlefPos(lat,longPos);
				break;
			case R.id.ib_save_sub_info:
				if(saveEnable){
					saveNewLoc();
				}else{
					myToast.show("请稍后，先下载之前图片");
				}

				break;
			default:
				break;
		}
	}

	/**
	 * 保存新的位置
	 * */
	private void saveNewLoc() {
		final String newName = et_sub_name.getText().toString().trim();
		if(newName.length() == 0){
			//新建变电所名字为空
			//提示  新建名称不能为空
			alertWarnning();
			Log.e("save", "新建名称为空");
		}else{
			if(selectPosSta){
				latEdit = lat;
				longEdit = longPos;
			}

			if(latEdit == 0.0 || longEdit == 0.0){
				handler.sendEmptyMessage(TOAST_NO_POS);
				return;
			}

			setMapCenter();

			final AuthPosAlertDialog authPosAlertDialog = new AuthPosAlertDialog(NewSubLoc.this);
			authPosAlertDialog.setCancleButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					authPosAlertDialog.dismiss();
				}
			});
			authPosAlertDialog.setOkButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updataInfo(newName);
					authPosAlertDialog.dismiss();
				}
			});
			authPosAlertDialog.show();
		}

	}

	private void updataInfo(String newName) {
		Log.e("save", "正在上传至服务器。。。");
		ProgressDia.showLoadingDialog(NewSubLoc.this, "正在上传数据，请等待", false);

		savaInfo.setConn(newName);
		savaInfo.setDelSta(1);
		savaInfo.setLevel(2);
		if(arrySelectImg.size() == 0){
			//没有图片直接保存位置与信息
			new Thread(
					new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e("save", "图片为空，开始上传内容");
							saveNewSub();
						}
					}).start();
		}else{
			//先上传图片信息
			Log.e("save", "图片不为空，开始上传图片");
			saveImgs();
		}
	}

	/**
	 * 将新的变电所信息保存至服务器
	 * */
	private void saveNewSub() {
		savaInfo.setValue("posLat", latEdit);
		savaInfo.setValue("posLong", longEdit);

		savaInfo.setFatherId(fatherId);
		savaInfo.setFatherReId(reFatherId);
		savaInfo.setPosLat(lat);
		savaInfo.setPosLong(longPos);
		savaInfo.setInfo(et_sub_info.getText().toString().trim());
		savaInfo.setType(1);
		if(newSub){
			Log.e("save", "开始上传");
			Log.e(TAG, "新建！！！！！！！！！！");
			newSubInfo();
		}else{
			Log.e(TAG, "编辑旧的变电所信息:"+editData.toString());
			editSubSave();
		}
	}

	private void editSubSave() {
		Log.e(TAG, "进入编辑变电所信息");
		savaInfo.setValue("type", 1);
		savaInfo.setValue("Level", 2);
		savaInfo.setValue("delSta", 1);
		savaInfo.setValue("conn", savaInfo.getConn());
		savaInfo.setValue("fatherId", fatherId);
		savaInfo.setValue("fatherReId", reFatherId);
		savaInfo.setValue("netImgsPath", savaInfo.getNetImgsPath());

		savaInfo.setValue("posLat", latEdit);
		savaInfo.setValue("posLong", longEdit);

		savaInfo.setValue("info", et_sub_info.getText().toString().trim());
		Log.e("updata","更新数据是： "+savaInfo.toString());
		savaInfo.update(editData.getID(), new UpdateListener() {
			@Override
			public void done(BmobException e) {
				Message msg = new Message();
				if(e==null){
					Log.e("save", "编辑保存数据成功!!!!!!!");
					msg.what = UPBAK_SUCCESS;
					msg.obj = editData.getID();
					handler.sendMessage(msg);
					savaInfo.setSelfId(editData.getID());
					//保存至本地数据库
					LevelThird newData = new LevelThird();
					newData.setFatherId(fatherId);
					newData.setFatherReId(reFatherId);
					newData.setID(editData.getID());
					newData.setImgPath(savaInfo.getNetImgsPath());
					newData.setInfo(savaInfo.getInfo());
					newData.setLevel(2);
					newData.setName(savaInfo.getConn());
					newData.setPosLat(savaInfo.getPosLat());
					newData.setPosLong(savaInfo.getPosLong());
					BNDemoMainActivity.dbHelper.updataThirdInfo(newData);
				}else{
					msg.what = UPFILE_FAILUE;
					Log.e("save","编辑信息失败："+e.getMessage()+","+e.getErrorCode());
					msg.obj = "编辑信息失败："+e.getMessage()+","+e.getErrorCode();
					handler.sendMessage(msg);
				}
			}
		});
	}

	private void newSubInfo() {
		savaInfo.save(new SaveListener<String>() {
			@Override
			public void done(String objectId, BmobException e) {
				Message msg = new Message();
				if(e==null){
					Log.e("save", "创建数据成功");
					msg.what = UPBAK_SUCCESS;
					msg.obj = objectId;
					handler.sendMessage(msg);
					savaInfo.setSelfId(objectId);
					//保存至本地数据库
					LevelThird newData = new LevelThird();
					newData.setFatherId(fatherId);
					newData.setFatherReId(reFatherId);
					newData.setID(objectId);
					newData.setImgPath(savaInfo.getNetImgsPath());
					newData.setInfo(savaInfo.getInfo());
					newData.setLevel(2);
					newData.setName(savaInfo.getConn());
					newData.setPosLat(savaInfo.getPosLat());
					newData.setPosLong(savaInfo.getPosLong());
					BNDemoMainActivity.dbHelper.addThird(newData);
				}else{
					msg.what = UPFILE_FAILUE;
					Log.e("save","新建失败："+e.getMessage()+","+e.getErrorCode());
					msg.obj = "新建失败："+e.getMessage()+","+e.getErrorCode();
					handler.sendMessage(msg);
				}
			}
		});
	}

	private void alertWarnning() {
		Dialog ad = new AlertDialog.Builder(this)
				.setIcon(R.drawable.logo)
				.setTitle("提示")
				.setMessage("新建变电所名称不能空!")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).create();
		ad.show();
	}

	/**
	 * 将图片发送至服务器
	 * */
	private void saveImgs() {
		System.out.println("arrySelectImg.size(): "+arrySelectImg.size());
		Log.e("save", "图片bu为空，开始上传图片:"+arrySelectImg.size()+"涨");
		new Thread(new Runnable() {
			//将图片压缩成小图片 然后保存至本地
			@Override
			public void run() {
				List<String>picList = new ArrayList<String>();
				String[] filePaths;		//所有图片
				filePaths = new String[arrySelectImg.size()];
				for(int i=0;i<arrySelectImg.size();i++){
					filePaths[i] = arrySelectImg.get(i).getImagePath();
					picList.add(arrySelectImg.get(i).getImagePath());
				}
				Log.e("IMG", "压缩完成，待上传");
				String localImgList = gson.toJson(picList);
				savaInfo.setLocImgsPath(localImgList);
				Log.e("IMG", "开始上传图片们~~~");
				upLoadImgs(filePaths);
			}
		}).start();
	}

	private void upLoadImgs(final String[] filePaths) {
		picListNet= new ArrayList<String>();
		BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
			@Override
			public void onSuccess(List<BmobFile> files,List<String> urls) {
				//1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
				//2、urls-上传文件的完整url地址
				Message msg = new Message();
				msg.what = CHINGE_TOAST;
				String nickLocalPath;
				File cropFile;
				if(urls.size()==filePaths.length){//如果数量相等，则代表文件全部上传完成
					//do something
					String s2 = gson.toJson(urls);
					savaInfo.setNetImgsPath(s2);

					nickLocalPath = FileUtils.getFileNameNoEx(filePaths[urls.size()-1]);
					msg.obj = "上传所有图片成功~~~";
					Log.e("IMG", "上传所有图片成功~~~");
					saveNewSub();
					if(!Bimp.isOrig_img_sta()){
						//删除本缩略图地图片
						for(int i=0;i<filePaths.length;i++){
							File deleFile  = new File(filePaths[i]);
							deleFile.delete();
						}
					}
				}else{
					msg.obj = "上传第"+urls.size()+"张图片成功:"+urls.get(urls.size()-1);
					//上传图片结束后 需要将图片文件重命名  将后缀去掉
					nickLocalPath = FileUtils.getFileNameNoEx(filePaths[urls.size()-1]);
					Log.e("IMG", "上传第"+urls.size()+"张图片成功:"+urls.get(urls.size()-1));
				}


				handler.sendMessage(msg);
			}

			@Override
			public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
				//1、curIndex--表示当前第几个文件正在上传
				//2、curPercent--表示当前上传文件的进度值（百分比）
				//3、total--表示总的上传文件数
				//4、totalPercent--表示总的上传进度（百分比）
			}

			@Override
			public void onError(int statuscode, String errormsg) {
				Message msg = new Message();
				msg.what = UPFILE_FAILUE;
				msg.obj = "错误码"+statuscode +",错误描述："+errormsg;
				handler.sendMessage(msg);
				Log.e("IMG","错误码"+statuscode +",错误描述："+errormsg);
			}
		});
	}

	// 1.主线程创建消息处理器
	@SuppressLint("HandlerLeak") public Handler handler = new Handler() {
		@SuppressLint({ "ShowToast", "HandlerLeak" }) @Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CHINGE_TOAST) {
				String str = (String) msg.obj;
				myToast.show(str);
			}else if(UPFILE_FAILUE == msg.what){
				ProgressDia.closeLoadingDialog();
				String str = (String) msg.obj;
				toast("上传至服务器失败！"+str);
			}else if(msg.what == COPY_FILE_FAILE){
				toast("复制文件失败!");
			}else if(UPBAK_SUCCESS == msg.what){
				ProgressDia.closeLoadingDialog();
				toast("上传服务器成功!");
				//上传成功  显示详细信息
				Log.e(TAG, "创建数据成功，跳转至变电所详细页面显示");

				Intent mIntent = new Intent();
				mIntent.putExtra("EDIT_STA", "1");
				mIntent.putExtra("ID", (String) msg.obj);
				// 设置结果，并进行传送
				NewSubLoc.this.setResult(Activity.RESULT_OK, mIntent);
				finish();
				onDestroy();
			}else if(GET_DATA_FAUIL == msg.what){
				toast("连接服务器失败，获取数据失败!");
			}else if(NOTIFY_GRIDVIEW == msg.what){
				ProgressDia.closeLoadingDialog();
				picAdapter.notifyDataSetChanged();
			}else if(SET_POS == msg.what){
				setSlefPos(latEdit,longEdit);
			}else if(TOAST_NO_POS == msg.what){
				myToast.show("请先选择地图位置");
			}

		}
	};
	private void setSlefPos(double selfLatitude, double selfLongitude) {
		// TODO Auto-generated method stub
		LatLng point = new LatLng(selfLatitude, selfLongitude);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// 在地图上添加Marker，并显示
		selfBDMP.clear();
		selfBDMP.addOverlay(option);
		selfBDMP.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

	}

	private void AlertWarnning() {
		// TODO Auto-generated method stub
		long tmpTomeMill = System.currentTimeMillis();
		if(tmpTomeMill - preTimeMi < 4000){
			backSta = true;
			Intent mIntent = new Intent();
			mIntent.putExtra("EDIT_STA", "0");
			// 设置结果，并进行传送
			this.setResult(Activity.RESULT_OK, mIntent);
			finish();
			onDestroy();
		}else{
			preTimeMi = tmpTomeMill;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//		bdmp_self_pos.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//		bdmp_self_pos.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//		bdmp_self_pos.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(backSta){
				Intent mIntent = new Intent();
				mIntent.putExtra("EDIT_STA", "0");
				// 设置结果，并进行传送
				this.setResult(Activity.RESULT_OK, mIntent);
				finish();
				onDestroy();
			}else{
				AlertWarnning();
			}
		}
		return super.onKeyDown(keyCode, event);
	};
	private void toast(String string) {
		// TODO Auto-generated method stub
		myToast.show(string);
	};
}