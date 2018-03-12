package com.ppl.sxgtqx.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.utils.SetData;

public class SetAdpt extends BaseAdapter {
	List<SetData>data;
	Context mContext;
	LayoutInflater layoutInflater;
	
	public SetAdpt(List<SetData> data, Context mContext) {
		super();
		this.data = data;
		this.mContext = mContext;
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
			convertView = layoutInflater.inflate(R.layout.item_lv_set, null);
			holder.iv_item_logo = (ImageView) convertView.findViewById(R.id.iv_item_logo);
			holder.tv_item_title = (TextView) convertView.findViewById(R.id.tv_item_title);
			holder.tv_item_conn = (TextView) convertView.findViewById(R.id.tv_item_conn);
			convertView.setTag(holder);
		}else{
			holder = (viewHolder)convertView.getTag();
		}
		SetData tmpData = data.get(position);
		holder.iv_item_logo.setImageResource(tmpData.getLogoId());
		holder.tv_item_title.setText(tmpData.getTitle());
		holder.tv_item_conn.setText(tmpData.getConn());
		return convertView;
	}
	private class viewHolder{
		ImageView iv_item_logo;
		TextView tv_item_title;
		TextView tv_item_conn;
	}
}
