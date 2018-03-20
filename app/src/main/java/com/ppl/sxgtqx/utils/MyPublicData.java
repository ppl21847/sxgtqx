package com.ppl.sxgtqx.utils;

import android.util.Log;

import com.ppl.sxgtqx.dataSQL.DbLocHelper;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyPublicData {
	private static String TAG = MyPublicData.class.getSimpleName();
	public static double selfLongitude = 0;
	public static  double selfLatitude = 0;
	public static List<ConnType>levelFirst = new ArrayList<ConnType>();
	public static List<ConnType>levelSond = new ArrayList<ConnType>();
	public static List<ConnType>levelThird = new ArrayList<ConnType>();
	public static List<ConnType>levelReThird = new ArrayList<ConnType>();
	public static List<LevelRoot>rootData;
	public static List<LevelSecond>secondData;
	public static List<LevelThird>thirdData;
	public static List<LevelThird>reThirdData;
	public static final String THIRD_ID = "selfId";
	public static final String SELF_ID = "showSelfId";

	public static void getLocData(DbLocHelper dbHelper){
		MyPublicData.rootData = dbHelper.getRootData();
		Log.e(TAG, "MyPublicData.rootData.size():"+(MyPublicData.rootData == null ? 0 : MyPublicData.rootData.size()));
		MyPublicData.levelFirst.add(new ConnType(0, 0, "我的位置", "0",1));
		//从本地数据哭中获取一级根目录内容
		for(int i=0;i<MyPublicData.rootData.size();i++){
			Log.e(TAG, MyPublicData.rootData.get(i).toString());
			MyPublicData.levelFirst.add(new ConnType(1, 0,
					MyPublicData.rootData.get(i).getName(), MyPublicData.rootData.get(i).getID(),1));
		}
		MyPublicData.levelFirst.add(new ConnType(2, 0, "添加", "",1));
		Log.e(TAG, "MyPublicData.rootData.size():"+MyPublicData.rootData.size());

		MyPublicData.secondData = new ArrayList<LevelSecond>();
		MyPublicData.thirdData = new ArrayList<LevelThird>();
		//		MyPublicData.levelSond.add(new ConnType(2, 1, "添加", "",1));
		Log.e(TAG, "MyPublicData.levelSond.size():"+MyPublicData.levelSond.size());
		MyPublicData.levelThird.add(new ConnType(2, 2, "新建", "",1));
		Log.e(TAG, "MyPublicData.levelThird.size():"+MyPublicData.levelThird.size());
	}

	/**
	 *  第一级数据同步
	 * 本地数据与服务器数据同步
	 * 每次获取数据从数据库获取 保证数据比较快的获取到
	 * 所以需要同步数据
	 * */
	public static void syncFirstSQL(final DbLocHelper dbHelper) {
		Log.e(TAG, "请求根木录信息-------------------------00000000000000000000000");
		BmobQuery<LevelRoot> query = new BmobQuery<LevelRoot>();
		query.addWhereEqualTo("delSta", 1);
		query.findObjects(new FindListener<LevelRoot>() {
			@Override
			public void done(List<LevelRoot> data, BmobException e) {
				if(e==null){
					Log.e(TAG, "get LevelRoot data.size():"+data.size());
					List<LevelRoot>tmpRoot = dbHelper.getRootData();
					if(tmpRoot != null){
						dbHelper.clearFirstData();
					}
					for(int i=0;i<data.size();i++){
						Log.e(TAG, "get LevelRoot data("+i+"):"+data.get(i).toString());
						Log.e(TAG, "添加至本地数据库: "+data.get(i).toString());
						LevelRoot tmpData = new LevelRoot(data.get(i).getObjectId(),
								data.get(i).getName(), 0, 1);
						//添加至本地数据库
						dbHelper.addFirst(tmpData);
					}
				}else{
					Log.e(TAG, "网络连接失败，获取一级地址失败");
				}
			}
		});
	}

	public static void syncSecondSQL(final DbLocHelper dbHelper) {
		Log.e(TAG, "获取服务器二级地址信息----------22222222");
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
					Log.e(TAG, "get LevelSecond data.size():"+data.size());
					//更新本地数据库		服务器端有数据删除，本地需要删除
					List<LevelSecond>tmpDataNet = dbHelper.getSecondData();

					if(tmpDataNet != null){
						dbHelper.clearSecondData();
					}
					Log.e(TAG, "本地数据库没有二级地址");
					//添加新数据
					for(int j=0;j<data.size();j++){
						Log.e(TAG, "本地不存在该条数据,需要添加至本地： "+data.get(j).toString());
						LevelSecond tmpData = new LevelSecond(data.get(j).getFatherId(),
								data.get(j).getObjectId(), data.get(j).getName(), 1, 1);
						Log.e(TAG, "像数据库添加的数据为  ： "+tmpData.toString());
						dbHelper.addSecond(tmpData);
					}
				}else{
					Log.e(TAG, "2222222222222222222222连接服务器失败，获取二级地址失败!!!!!!!!" +e.getMessage()+","+e.getErrorCode());
				}
			}
		});
	}

	//同步三级地址信息
	public static void syncThird(final DbLocHelper dbHelper) {
		Log.e(TAG, "请求服务器三级地址----------3333333333");
		BmobQuery<ConnType>query = new BmobQuery<ConnType>();
		query.addWhereEqualTo("delSta", 1);
		query.findObjects(new FindListener<ConnType>() {
			@Override
			public void done(List<ConnType> data, BmobException e) {
				if(e == null){
					Log.e(TAG, "从服务器获取三级目录信息数量："+data.size());
					//更新数据库没用信息    服务器已删除  本地还有
					List<LevelThird>tmpDataList = dbHelper.getThirdData();
					if(tmpDataList != null){
						dbHelper.clearThirdData();
					}
					Log.e(TAG, "数据库没有三级地址数据");
					Log.e(TAG, "向本地数据库添加"+data.size()+"条，三级数据");
					for(int i=0;i<data.size();i++){
						LevelThird tmpData = new LevelThird(data.get(i).getFatherId(),data.get(i).getFatherReId(),
								data.get(i).getObjectId(), data.get(i).getConn(), 2,
								data.get(i).getInfo(), data.get(i).getPosLat(),
								data.get(i).getPosLong(), data.get(i).getNetImgsPath());
						Log.e(TAG, "向数据库添加的新数据是： "+data.get(i).toString());
						dbHelper.addThird(tmpData);
					}
				}else{
					Log.e(TAG, "2222222222222222222222222获取三级地址失败");
				}
			}
		});
	}

	/**
	 * 同步新添加的第三级地址
	 * */
    public static void syncReThird(final DbLocHelper dbHelper) {
		Log.e(TAG, "请求服务器xinde三级地址----------44444444444444");
		BmobQuery<LevelReThird>query = new BmobQuery<LevelReThird>();
		query.addWhereEqualTo("delSta", 1);
		query.findObjects(new FindListener<LevelReThird>() {
			@Override
			public void done(List<LevelReThird> data, BmobException e) {
				if(e == null){
					Log.e(TAG, "从服务器获取xinde三级目录信息数量："+data.size());
					//更新数据库没用信息    服务器已删除  本地还有
					if( dbHelper.existReThirdData()){
						dbHelper.clearReThirdData();
					}

					Log.e(TAG, "数据库没有xinde三级地址数据");
					Log.e(TAG, "向本地数据库添加"+data.size()+"条，xinde三级数据");
					for(int i=0;i<data.size();i++){
						LevelReThird tmpData = new LevelReThird();
						tmpData.setLevel(3);
						tmpData.setID(data.get(i).getObjectId());
						tmpData.setName(data.get(i).getName());
						Log.e(TAG, "向数据库添加的新数据是： "+data.get(i).toString());
						dbHelper.addReThird(tmpData);
					}
				}else{
					Log.e(TAG, "2222222222222222222222222获取三级地址失败");
				}
			}
		});
    }
}
