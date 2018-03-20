package com.ppl.sxgtqx.dataSQL;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ppl.sxgtqx.utils.LevelReThird;
import com.ppl.sxgtqx.utils.LevelRoot;
import com.ppl.sxgtqx.utils.LevelSecond;
import com.ppl.sxgtqx.utils.LevelThird;

public class DbLocHelper extends SQLiteOpenHelper {

	public static final String DB_NAME="locinfo.db";
	public static final String LEVEL_FRIST_NAME="levelfirst";
	public static final String LEVEL_SECOND_NAME="levelsecond";
	public static final String LEVEL_THIRD_NAME="levelthird";
	public static final String LEVEL_RE_THIRD_NAME="levelrethird";
	public static final int version=1;
	private static final String TAG = "DbLocHelper";

	public DbLocHelper(Context context, String name, CursorFactory factory,
					   int version) {
		super(context, name, factory, version);
	}

	public void clearFirstData(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("delete from levelfirst");
	}

	public void clearSecondData(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("delete from levelsecond");
	}

	public void clearThirdData(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("delete from levelthird");
	}

	public void clearReThirdData(){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("delete from levelrethird");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("DB", "onCreat");
		String excelSQL="create table if not exists " +LEVEL_FRIST_NAME+
				"(dataId integer primary key autoincrement, " +
				"Id varchar(100)," +
				"Name varchar(100)," +
				"level integer)";
		Log.e("DB", excelSQL);
		db.execSQL(excelSQL);

		String secondSQL="create table if not exists " +LEVEL_SECOND_NAME+
				"(dataId integer primary key autoincrement," +
				"Id varchar(100)," +
				"Name varchar(60)," +
				"level integer not null," +
				"fatherId varchar(100))";
		Log.e("DB", secondSQL);
		db.execSQL(secondSQL);

		String thirdSQL="create table if not exists " +LEVEL_THIRD_NAME+
				"(dataId integer primary key autoincrement," +
				"Id varchar(100) not null," +
				"Name varchar(60) not null," +
				"level integer not null," +
				"info varchar(1000)," +
				"posLat double not null," +
				"posLong posLong not null," +
				"fatherReId varchar(100) not null,"+
				"imgPath varchar(1000)," +
				"imgLocPath varchar(1000),"+
				"fatherId varchar(100) not null)";

		Log.e("DB", thirdSQL);
		db.execSQL(thirdSQL);

		String reThirdSQL="create table if not exists " +LEVEL_RE_THIRD_NAME+
				"(dataId integer primary key autoincrement," +
				"Id varchar(100) not null," +
				"Name varchar(60) not null," +
				"info varchar(1000))";
		Log.e("DB", reThirdSQL);
		db.execSQL(reThirdSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,"oldVersion: "+oldVersion+" , newVersion："+newVersion);
	}

	public void addFirst(LevelRoot data){
		SQLiteDatabase db=this.getWritableDatabase();
		Log.e("SQLiteDatabase","SQLiteDatabase");
		ContentValues values =new ContentValues();
		values.put("Id", data.getID());
		values.put("Name", data.getName());
		values.put("level", data.getLevel());
		db.insert(LEVEL_FRIST_NAME, null, values);
		db.close();
	}
	public void addSecond(LevelSecond data){
		SQLiteDatabase db=this.getWritableDatabase();
		Log.e("SQLiteDatabase","SQLiteDatabase");
		ContentValues values =new ContentValues();
		values.put("Id", data.getID());
		values.put("Name", data.getName());
		values.put("level", data.getLevel());
		values.put("fatherId", data.getFatherId());
		db.insert(LEVEL_SECOND_NAME, null, values);
		db.close();
	}
	public void addThird(LevelThird data){
		SQLiteDatabase db=this.getWritableDatabase();
		Log.e("SQLiteDatabase","SQLiteDatabase");
		ContentValues values =new ContentValues();
		values.put("Id", data.getID());
		values.put("Name", data.getName());
		values.put("level", data.getLevel());
		values.put("fatherId", data.getFatherId());
		values.put("info", data.getInfo());
		values.put("posLat", data.getPosLat());
		values.put("posLong", data.getPosLong());
		values.put("imgPath", data.getImgPath());//imgLocPath
		values.put("imgLocPath", data.getImgLocal());
		values.put("fatherReId", data.getFatherReId());
		db.insert(LEVEL_THIRD_NAME, null, values);
		db.close();
	}

	public void addReThird(LevelReThird data){
		SQLiteDatabase db=this.getWritableDatabase();
		Log.e("SQLiteDatabase","SQLiteDatabase");
		ContentValues values =new ContentValues();
		values.put("Id", data.getID());
		values.put("Name", data.getName());
		values.put("info", data.getInfo());
		db.insert(LEVEL_RE_THIRD_NAME, null, values);
		db.close();
	}

	/**
	 * 获取根目录下所有数据
	 * */
	/*
	 * 获取所有备忘数据
	 * 根据user = 1;有用地点信息，0-已删除
	 * */
	public List<LevelRoot>getRootData(){
		List<LevelRoot>rootData = new ArrayList<LevelRoot>();
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = null;
		try {
			c=db.rawQuery("select * from "+LEVEL_FRIST_NAME,null);
			while (c.moveToNext()){
				rootData.add(new LevelRoot(
						c.getString(c.getColumnIndex("Id")),
						c.getString(c.getColumnIndex("Name")),
						c.getInt(c.getColumnIndex("level")),
						1));
			}
		} catch (Exception e) {
			// 处理能处理的异常，不能完整处理，重新封装异常抛给上层
			Log.e(TAG, "getRootData get data Exception");
		}

		if(c != null){
			c.close();
		}
		return rootData;
	}
	/**
	 * 获取二级所有数据
	 * */
	public List<LevelSecond>getSecondData(){
		List<LevelSecond>data = new ArrayList<LevelSecond>();
		SQLiteDatabase db=this.getReadableDatabase();
		//Cursor对象返回查询结果
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from "+LEVEL_SECOND_NAME,null);
			LevelSecond info=null;
			while (cursor.moveToNext()) {
				info= new LevelSecond(
						cursor.getString(cursor.getColumnIndex("fatherId")),
						cursor.getString(cursor.getColumnIndex("Id")),
						cursor.getString(cursor.getColumnIndex("Name")),
						cursor.getInt(cursor.getColumnIndex("level")),1);
				System.out.println("info="+info.toString());
				data.add(info);
			}
		} catch (Exception e) {
			Log.e(TAG, "getSecondData Exception");
		}

		if(cursor != null){
			cursor.close();
		}
		return data;
	}
	/**
	 * 根据输入的父级 ID 获取二级目录
	 * */
	public List<LevelSecond>getSecondData(String fatherId){
		List<LevelSecond>data = new ArrayList<LevelSecond>();
		SQLiteDatabase db = getWritableDatabase();
		try {
			Cursor c = db.query(LEVEL_SECOND_NAME,
					new String[]{"Id","Name","level","fatherId"},
					"fatherId=?",new String[]{fatherId+""}, null, null, null, null);
			while (c.moveToNext()){
				data.add(new LevelSecond(
						c.getString(c.getColumnIndex("fatherId")),
						c.getString(c.getColumnIndex("Id")),
						c.getString(c.getColumnIndex("Name")),
						c.getInt(c.getColumnIndex("level")),1));
			}
			if(c != null){
				c.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "getSecondData fatherId Exception");
		}


		return data;
	}
	/**
	 * 获取所有新的第三级数据
	 * */
	public List<LevelReThird>getReThirdData(){
		List<LevelReThird>data = new ArrayList<LevelReThird>();
		SQLiteDatabase db=this.getReadableDatabase();
		try {
			//Cursor对象返回查询结果
			Cursor cursor=db.rawQuery("select * from "+LEVEL_RE_THIRD_NAME,null);
			LevelReThird info=null;
			while (cursor.moveToNext()) {
				info= new LevelReThird();
				info.setName(cursor.getString(cursor.getColumnIndex("Name")));
				info.setID(cursor.getString(cursor.getColumnIndex("Id")));
				System.out.println("info="+info.toString());
				data.add(info);
			}
			if(cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getThirdData Exception");
		}
		return data;
	}
	/**
	 * 获取所有三级数据
	 * */
	public List<LevelThird>getThirdData(){
		List<LevelThird>data = new ArrayList<LevelThird>();
		SQLiteDatabase db=this.getReadableDatabase();
		try {
			//Cursor对象返回查询结果
			Cursor cursor=db.rawQuery("select * from "+LEVEL_THIRD_NAME,null);
			LevelThird info=null;
			while (cursor.moveToNext()) {
				info= new LevelThird(
						cursor.getString(cursor.getColumnIndex("fatherId")),
						cursor.getString(cursor.getColumnIndex("fatherReId")),
						cursor.getString(cursor.getColumnIndex("Id")),
						cursor.getString(cursor.getColumnIndex("Name")),
						cursor.getInt(cursor.getColumnIndex("level")),
						cursor.getString(cursor.getColumnIndex("info")),
						cursor.getDouble(cursor.getColumnIndex("posLat")),
						cursor.getDouble(cursor.getColumnIndex("posLong")),
						cursor.getString(cursor.getColumnIndex("imgPath")));
				System.out.println("info="+info.toString());
				data.add(info);
			}
			if(cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getThirdData Exception");
		}
		return data;
	}

	public boolean existReThirdData(){
		boolean isExistData = false;
		SQLiteDatabase db=this.getReadableDatabase();
		try {
			//Cursor对象返回查询结果
			Cursor cursor=db.rawQuery("select * from "+LEVEL_RE_THIRD_NAME,null);
			while (cursor.moveToNext()) {
				isExistData = true;
				break;
			}
			if(cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getThirdData Exception");
		}
		return isExistData;
	}
	/**
	 * 根据父级ID获取所有三级数据
	 * */
	public List<LevelThird>getThirdData(String fatherId,String reFatherId){
		List<LevelThird>data = new ArrayList<LevelThird>();
		SQLiteDatabase db=this.getReadableDatabase();
		try {
			String[] columns={"Id","Name","level","info","posLat","posLong","imgPath","fatherId","fatherReId"};//你要的数据
			String selections="fatherId=? and fatherReId=?";	//条件字段
			String[] selectionArgs={fatherId,reFatherId};//具体的条件,注意要对应条件字段
			//Cursor对象返回查询结果
			Cursor cursor = db.query(LEVEL_THIRD_NAME,
					columns,selections,selectionArgs, null, null, null, null);
			LevelThird info=null;
			while (cursor.moveToNext()) {
				info= new LevelThird(
						cursor.getString(cursor.getColumnIndex("fatherId")),
						cursor.getString(cursor.getColumnIndex("fatherReId")),
						cursor.getString(cursor.getColumnIndex("Id")),
						cursor.getString(cursor.getColumnIndex("Name")),
						cursor.getInt(cursor.getColumnIndex("level")),
						cursor.getString(cursor.getColumnIndex("info")),
						cursor.getDouble(cursor.getColumnIndex("posLat")),
						cursor.getDouble(cursor.getColumnIndex("posLong")),
						cursor.getString(cursor.getColumnIndex("imgPath")));
				System.out.println("info="+info.toString());
				data.add(info);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getThirdData fatherId: "+fatherId+" Exception");
		}

		return data;
	}
	/**
	 * 根据ID获取三级地址信息
	 * */
	public LevelThird getThird(String selfId){
		LevelThird info = null;
		SQLiteDatabase db=this.getReadableDatabase();

		try {
			//Cursor对象返回查询结果
			Cursor cursor = db.query(LEVEL_THIRD_NAME,
					new String[]{"Id","Name","level","info","posLat","posLong","imgPath","fatherId"},
					"Id=?",new String[]{selfId+""}, null, null, null, null);
			Log.e("DB", "查找结果有： "+cursor.getCount()+" 条");
			while (cursor.moveToNext()) {
				info= new LevelThird(
						cursor.getString(cursor.getColumnIndex("fatherId")),
						"",
						cursor.getString(cursor.getColumnIndex("Id")),
						cursor.getString(cursor.getColumnIndex("Name")),
						cursor.getInt(cursor.getColumnIndex("level")),
						cursor.getString(cursor.getColumnIndex("info")),
						cursor.getDouble(cursor.getColumnIndex("posLat")),
						cursor.getDouble(cursor.getColumnIndex("posLong")),
						cursor.getString(cursor.getColumnIndex("imgPath")));
				System.out.println("info="+info.toString());
			}
			cursor.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "getThird selfId: "+selfId+" Exception，"+e.getMessage());
		}

		return info;
	}

	/**
	 * 更新根目录
	 * */
	public int updataFirstInfo(LevelRoot rootInfo){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("Name",rootInfo.getName());
		return db.update(LEVEL_FRIST_NAME,values,"Id=?",new String[]{rootInfo.getID()});
	}
	/**
	 * 更新二级目录
	 * */
	public int updataSecondInfo(LevelSecond secondInfo){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("Name",secondInfo.getName());
		values.put("fatherId", secondInfo.getFatherId());
		return db.update(LEVEL_SECOND_NAME,values,"Id=?",new String[]{secondInfo.getID()});
	}
	/**
	 * 更新san级目录
	 * */
	public int updataThirdInfo(LevelThird rootInfo){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("Name",rootInfo.getName());
		values.put("fatherId", rootInfo.getFatherId());
		values.put("fatherReId", rootInfo.getFatherReId());
		values.put("level", rootInfo.getLevel());
		values.put("info", rootInfo.getInfo());
		values.put("posLat", rootInfo.getPosLat());
		values.put("posLong", rootInfo.getPosLong());
		values.put("imgPath", rootInfo.getImgPath());
		return db.update(LEVEL_THIRD_NAME,values,"Id = ?",new String[]{rootInfo.getID()});
		//db.update("Book", values, "name = ?", new String[] { "The DaVinci Code" });
	}

	/**
	 * 更新新的三级目录
	 * */
	public int updataReThirdInfo(LevelReThird reThirdInfo){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("Name",reThirdInfo.getName());
		values.put("info", reThirdInfo.getInfo());
		return db.update(LEVEL_RE_THIRD_NAME,values,"Id = ?",new String[]{reThirdInfo.getID()});
	}
	/**
	 * 删除根级目录
	 * */
	public void deleteRoot(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(LEVEL_FRIST_NAME, "Id=?", new String[]{id});
	}
	/**
	 * 删除二级目录
	 * */
	public void deleteSecond(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(LEVEL_SECOND_NAME, "Id=?", new String[]{id});
	}
	/**
	 * 删除三级目录
	 * */
	public void deleteThird(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(LEVEL_THIRD_NAME, "Id=?", new String[]{id});
	}
	/**
	 * 删除数据库中的表
	 * */
	public void dataInfo(){
		SQLiteDatabase db=this.getWritableDatabase();
		String sql ="DROP TABLE "+LEVEL_FRIST_NAME;
		String sql2 ="DROP TABLE "+LEVEL_SECOND_NAME;
		String sql3 ="DROP TABLE "+LEVEL_THIRD_NAME;
		db.execSQL(sql);
		db.execSQL(sql2);
		db.execSQL(sql3);
	}
	/**
	 * 清空表数据
	 * */
	public void clearSQL(String name){
		SQLiteDatabase db=this.getWritableDatabase();
		String sql = "DELETE FROM "+name;
		db.execSQL(sql);
	}
}
