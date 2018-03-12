package com.ppl.sxgtqx.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.utils.MapCityInfo;

import java.util.List;


public class CityAdapter extends BaseAdapter {
	List<MapCityInfo>data;
	Context mContext;
	LayoutInflater layoutInflater;
	private CityClickListener mListener;	//注：所有listview的item都共用同一个listener对象！！！

	public CityAdapter(List<MapCityInfo> data, Context mContext,CityClickListener mListener) {
		super();
		this.data = data;
		this.mContext = mContext;
		this.mListener = mListener;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		viewHolder holder = null;
		if(convertView == null){
			holder = new viewHolder();
			convertView = layoutInflater.inflate(R.layout.item_lv_map_contry, null);
			holder.tv_city_name = (TextView) convertView.findViewById(R.id.tv_city_name);
			holder.tv_downLoad = (TextView) convertView.findViewById(R.id.tv_downLoad);
			holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.iv_dwon_sta = (ImageView) convertView.findViewById(R.id.iv_dwon_sta);
			convertView.setTag(holder);
		}else{
			holder = (viewHolder)convertView.getTag();
		}
		MapCityInfo tmpData = data.get(position);
		if(tmpData.isEixt()){
			holder.tv_downLoad.setText("(已下载)");
			holder.tv_downLoad.setVisibility(View.VISIBLE);
			holder.iv_dwon_sta.setImageResource(R.drawable.trip_content_icon_new_delete);
		}else{
			holder.tv_downLoad.setVisibility(View.INVISIBLE);
			holder.iv_dwon_sta.setImageResource(R.drawable.localmap_citylist_download_btn_enabled_new);
		}
		holder.iv_dwon_sta.setOnClickListener(mListener);
		holder.iv_dwon_sta.setTag(position);	//注：这样能使所有listview的item都共用同一个listener，而不用为每个item都设置各自的listener！！！
		holder.tv_city_name.setText(tmpData.getName());
		holder.tv_size.setText(tmpData.getSize());
		return convertView;
	}
	private class viewHolder{
		TextView tv_city_name;
		TextView tv_downLoad;
		TextView tv_size;
		ImageView iv_dwon_sta;
	}
	/**
	 * 用于回调的抽象类
	 * @author ppl
	 * 2017-2-23
	 */
	public static abstract class CityClickListener implements OnClickListener {
		/**
		 * 基类的onClick方法
		 */
		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}
		public abstract void myOnClick(int position, View v);
	}
}
