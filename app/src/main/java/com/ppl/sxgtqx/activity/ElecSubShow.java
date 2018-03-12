package com.ppl.sxgtqx.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.adpter.ShowImgAdapter;
import com.ppl.sxgtqx.application.LocationApplication;
import com.ppl.sxgtqx.com.loopj.android.image.SmartImageView;
import com.ppl.sxgtqx.utils.ConnType;
import com.ppl.sxgtqx.utils.FileUtils;
import com.ppl.sxgtqx.utils.MyPublicData;
import com.ppl.sxgtqx.view.MyToast;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;

public class ElecSubShow extends Activity implements OnClickListener{
	private static final String TAG = "ElecSubShow";
	protected static final int GET_DATA_SUCCESS = 3;
	protected static final int GET_DATA_FAUIL = 4;
	private static final int BAND_GRIDVIEW = 5;
	protected static final int SHOW_SUB_IMG = 6;
	int showSubId;
	ImageButton ibt_back;
	TextView tv_newBakTiltle,tv_show_conn,tv_show_title,tv_edit_sub;
	GridView gv_show;
	ShowImgAdapter adapter;
	ConnType info;
	Gson gson;
	List<String>ImgPath;
	List<String>imgPathLocal;
	ConnType showData;
	File imgThPath;			//将图片保存至本地
	String thImgDir="noUser";
	SmartImageView siv_show_big;
	boolean showBigSta = false;
	String localImgPaths = "noPic";
	//自定义Toast 
	MyToast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.elec_sub_show_aty);
		showSubId = getIntent().getIntExtra(MyPublicData.THIRD_ID, 0);
		info = MyPublicData.levelThird.get(showSubId);
		String selfId = info.getSelfId();
		//根据Id向服务器请求图片信息
		getSubImg(selfId);
		Log.e(TAG, "显示信息的ID： "+showSubId);
		if(info == null){
			Log.e(TAG, "未获取到要显示信息的ID！！！ ");
			finish();
		}else{
			Log.e(TAG, info.toString());
			initView();
			initData();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}
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
					showData = data;
					handler.sendEmptyMessage(GET_DATA_SUCCESS);
				}else{
					Log.e(TAG, "获取服务器数据失败");
					handler.sendEmptyMessage(GET_DATA_FAUIL);
				}
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		ibt_back = (ImageButton) findViewById(R.id.ibt_back);
		tv_newBakTiltle = (TextView) findViewById(R.id.tv_newBakTiltle);
		tv_show_conn = (TextView) findViewById(R.id.tv_show_conn);
		tv_show_title = (TextView) findViewById(R.id.tv_show_title);
		gv_show = (GridView) findViewById(R.id.gv_show);
		tv_edit_sub = (TextView) findViewById(R.id.tv_edit_sub);
		siv_show_big = (SmartImageView) findViewById(R.id.siv_show_big);
	}

	private void initData() {
		if(LocationApplication.adminLoginSta){
			tv_edit_sub.setVisibility(View.VISIBLE);
		}else{
			tv_edit_sub.setVisibility(View.GONE);
		}
		ibt_back.setOnClickListener(this);
		tv_edit_sub.setOnClickListener(this);
		tv_newBakTiltle.setText(info.getConn());
		tv_show_conn.setText(info.getInfo());
		tv_show_title.setText(info.getConn()+"简介");

		siv_show_big.setOnClickListener(this);

		myToast = new MyToast(ElecSubShow.this, 5000, Gravity.BOTTOM);

		File mTempDir = new File( Environment.getExternalStorageDirectory(),"sxgtqx");
		if(!mTempDir.exists()){
			mTempDir.mkdirs();
		}
		imgThPath = new File(mTempDir,thImgDir);
		if(!imgThPath.exists()){
			imgThPath.mkdirs();
		}
		Log.e("NewBak", "imgThPath.getPath(): "+imgThPath.getPath());
		imgPathLocal = new ArrayList<String>();//本地图片地址

		gv_show.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				siv_show_big.setImageUrl(ImgPath.get(arg2));
				siv_show_big.setVisibility(View.VISIBLE);
				showBigSta = true;
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ibt_back:
			finish();
			break;
		case R.id.tv_edit_sub:
			//编辑，修改内容
			goToEdit();
			break;
		case R.id.siv_show_big:
			siv_show_big.setVisibility(View.GONE);
			showBigSta = false;
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(showBigSta){
				siv_show_big.setVisibility(View.GONE);
				showBigSta = false;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void goToEdit() {
		goToEditSub();
	}

	private void goToEditSub() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(), NewSubLoc.class);
		intent.putExtra("NEW", false);
		intent.putExtra("SELFID", info.getSelfId());
		intent.putExtra("LOCALIMG", localImgPaths);
		Log.e(TAG, "localImgPaths:  "+localImgPaths);
		startActivity(intent);
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == GET_DATA_SUCCESS){
				Log.e(TAG, "获取服务器数据成功");
				//解析JSON
				gson = new Gson();
				Type type = new TypeToken<ArrayList<String>>() {}.getType();
				ImgPath = gson.fromJson(showData.getNetImgsPath(), type);

				if(ImgPath != null){
					Log.e(TAG, "服务器上图片有："+ImgPath.size()+"条");
					//GridView 显示
					handler.sendEmptyMessage(SHOW_SUB_IMG);
					//开一个新线程下载图片
					new Thread(new Runnable() {
						@Override
						public void run() {
							Log.e(TAG, "开始下载图片");
							downLoadImgs();
						}
					}).start();
				}
			}else if(msg.what == GET_DATA_FAUIL){
				toast("获取图片信息失败!");
			}else if(msg.what == BAND_GRIDVIEW){
				String s2 = gson.toJson(imgPathLocal); 
				info.setLocImgsPath(s2);
				localImgPaths = s2;
			}else if(msg.what == SHOW_SUB_IMG ){
				//显示图片
				adapter = new ShowImgAdapter(getApplicationContext(), ImgPath);
				gv_show.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		};
	};
	/**  
	 * fileName 文件名(必填)
	 *  group 组名（选填）
	 *  url  完整url地址（必填）
	 * 注：必须要有文件名和文件的完整url地址，group可为空
	 * BmobFile bmobfile =new BmobFile("xxx.png","","http://bmob-cdn-2.b0.upaiyun.com/2016/04/12/58eeed852a7542cb964600c6cc0cd2d6.png")；
	 */
	private void downLoadImgs() {
		Log.e(TAG, "进入图片下载函数");
		//允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
		for(int i=0;i<ImgPath.size();i++){
			Log.e(TAG, "开始第"+i+" zhang图片");
			int posLase = ImgPath.get(i).lastIndexOf("/");
			final String fileName = ImgPath.get(i).substring(posLase+1);
			Log.e(TAG, "fileName: "+fileName);
			int pointPos = fileName.indexOf(".");
			File checkExists = null;
			if(pointPos > 0){
				checkExists = new File(imgThPath, fileName.substring(0,pointPos ));
				Log.e(TAG, "服务器上图片地址: "+checkExists.getPath());
			}
			if(!checkExists.exists()){
				//检测本地是否以存在该文件
				final File outImg = new File(imgThPath, fileName);
				Log.e(TAG, "输出文件地址: "+outImg.getPath());
				//本地没有该文件下载
				BmobFile bmobfile =new BmobFile(outImg.getPath(),"",ImgPath.get(i));
				bmobfile.download(outImg, new DownloadFileListener() {

					@Override
					public void onProgress(Integer arg0, long arg1) {

					}
					@Override
					public void done(String savaPath, BmobException e) {
						if(e==null){
							Log.e(TAG, "下载图片网络地址："+fileName+",本地地址："+savaPath);
							outImg.renameTo(new File(FileUtils.getFileNameNoEx(outImg.getPath())));
							imgPathLocal.add(outImg.getPath());
							handler.sendEmptyMessage(BAND_GRIDVIEW);
							if(imgPathLocal.size() == ImgPath.size()){
								Log.e(TAG, "下载所有图片结束,更新GridView 显示");
							}
						}else{
							Log.e(TAG, "下载失败："+e.getMessage()+","+e.getErrorCode());
						}
					}
				});
			}
			else{
				Log.e(TAG, "本地已存在该图片");
				imgPathLocal.add(checkExists.getPath());
				handler.sendEmptyMessage(BAND_GRIDVIEW);
				//更新显示
				if(imgPathLocal.size() == ImgPath.size()){
					Log.e(TAG, "下载所有图片结束,更新GridView 显示");
				}
			}
		}
	}

	protected void toast(String string) {
		// TODO Auto-generated method stub
		myToast.show(string);
	}
}
