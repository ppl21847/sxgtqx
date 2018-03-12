package com.ppl.sxgtqx.utils;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.application.LocationApplication;

import java.util.ArrayList;
import java.util.List;


public class InitData {
	public static List<ConnType>initSubData(int level){
		List<ConnType> data = new ArrayList<ConnType>();
		if(level == 0){
			ConnType selfInfo = new ConnType();
			selfInfo.setConn("我的位置");
			selfInfo.setLevel(0);
			selfInfo.setType(0);
			selfInfo.setSelectSta(true);
			data.add(selfInfo);
			
			ConnType taiyuanInfo = new ConnType();
			taiyuanInfo.setConn("太原供电段");
			taiyuanInfo.setLevel(0);
			taiyuanInfo.setType(1);
			taiyuanInfo.setSelfId("");
			taiyuanInfo.setSelectSta(false);
			data.add(taiyuanInfo);
			
			ConnType newInfo = new ConnType();
			newInfo.setConn("添加");
			newInfo.setLevel(0);
			newInfo.setType(2);
			newInfo.setSelectSta(false);
			data.add(newInfo);
		}else if(level == 1){
			ConnType jinzInfo = new ConnType();
			jinzInfo.setConn("晋中高铁供电车间");
			jinzInfo.setLevel(1);
			jinzInfo.setType(1);
			jinzInfo.setSelfId("");
			jinzInfo.setFatherId("");
			jinzInfo.setSelectSta(true);
			data.add(jinzInfo);
			
			ConnType yzxInfo = new ConnType();
			yzxInfo.setConn("忻州西高铁供电车间");
			yzxInfo.setLevel(1);
			yzxInfo.setType(1);
			yzxInfo.setSelfId("");
			jinzInfo.setFatherId("");
			yzxInfo.setSelectSta(true);
			data.add(yzxInfo);
			
			ConnType newInfo = new ConnType();
			newInfo.setConn("添加");
			newInfo.setLevel(1);
			newInfo.setType(2);
			newInfo.setSelectSta(false);
			data.add(newInfo);
		}else if(level == 2){
			ConnType jzInfo = new ConnType();
			jzInfo.setConn("晋中高铁供电车间");
			jzInfo.setLevel(2);
			jzInfo.setType(1);
			jzInfo.setSelfId("");
			jzInfo.setSelectSta(true);
			data.add(jzInfo);
			
			ConnType jzbdsInfo = new ConnType();
			jzbdsInfo.setConn("晋中变电所");
			jzbdsInfo.setLevel(2);
			jzbdsInfo.setType(1);
			jzbdsInfo.setSelfId("");
			jzbdsInfo.setSelectSta(false);
			data.add(jzbdsInfo);
			
			ConnType qxdInfo = new ConnType();
			qxdInfo.setConn("祁县东变电所");
			qxdInfo.setLevel(2);
			qxdInfo.setType(1);
			qxdInfo.setSelfId("");
			qxdInfo.setSelectSta(false);
			data.add(qxdInfo);
			
			ConnType newInfo = new ConnType();
			newInfo.setConn("添加");
			newInfo.setLevel(2);
			newInfo.setType(2);
			newInfo.setSelectSta(false);
			data.add(newInfo);
		}
		return data;
	}
	public static List<SetData>initSetData(){
		List<SetData> data = new ArrayList<SetData>();
		
		SetData tmp2= new SetData();
		tmp2.setLogoId(R.drawable.user_center_offline_maps);
		tmp2.setTitle("离线地图");
		tmp2.setConn("没网也能搜地点看地图");
		data.add(tmp2);
		
		if(LocationApplication.adminLoginSta){
			SetData tmp1= new SetData();
			tmp1.setLogoId(R.drawable.icon_usercenter_address);
			tmp1.setTitle("管理地点");
			tmp1.setConn("添加或删除或修改地点信息");
			data.add(tmp1);
		}
		
		
		return data;
	}

}
