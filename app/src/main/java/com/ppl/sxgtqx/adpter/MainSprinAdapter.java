package com.ppl.sxgtqx.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.utils.ConnType;

import java.util.List;


public class MainSprinAdapter extends BaseAdapter {
	public static final int TYPE_ADD = 0;
    public static final int TYPE_CONN = 1;
	List<ConnType>data;
	Context mContext;
	LayoutInflater layoutInflater;
	int manage = 0;
	private MyClickListener mListener;	//注：所有listview的item都共用同一个listener对象！！！

	public MainSprinAdapter(List<ConnType> data, Context mContext) {
		super();
		this.data = data;
		this.mContext = mContext;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	public MainSprinAdapter(List<ConnType> data, Context mContext, int manage,
			MyClickListener mListener) {
		super();
		this.data = data;
		this.mContext = mContext;
		this.manage = manage;
		this.mListener = mListener;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(data.get(position).getType() == 2){
			return TYPE_ADD;
		}
		return TYPE_CONN;
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		switch (getItemViewType(position)) {
		case TYPE_ADD:
			viewAddHolder addHolder;
			if(convertView == null){
				addHolder = new viewAddHolder();
				convertView = layoutInflater.inflate(R.layout.item_add, null);
				addHolder.iv_add_icon = (ImageView) convertView.findViewById(R.id.iv_add_icon);
				addHolder.tv_add = (TextView) convertView.findViewById(R.id.tv_add);
				convertView.setTag(addHolder);
			}else{
				addHolder = (viewAddHolder) convertView.getTag();
			}
			addHolder.tv_add.setText("添加");    
			break;
		case TYPE_CONN:
			viewHolder holder = null;
			if(convertView == null){
				holder = new viewHolder();
				convertView = layoutInflater.inflate(R.layout.item_spinner, null);
				holder.ib_item_add = (ImageButton) convertView.findViewById(R.id.ib_item_add);
				holder.tv_sub_selcet_conn = (TextView) convertView.findViewById(R.id.tv_sub_selcet_conn);
				holder.ib_next = (ImageButton) convertView.findViewById(R.id.ib_next);
				convertView.setTag(holder);
			}else{
				holder = (viewHolder)convertView.getTag();
			}
			ConnType tmpData = data.get(position);
			if(tmpData.getType() == 2){
				holder.ib_item_add.setVisibility(View.VISIBLE);
				holder.ib_item_add.setImageResource(R.drawable.poi_result_add_new);
			}else{
				if(manage == 0){
					holder.ib_item_add.setVisibility(View.INVISIBLE);
				}else if(manage == 1){
					holder.ib_item_add.setVisibility(View.VISIBLE);
					holder.ib_item_add.setImageResource(R.drawable.delete_poi);
					holder.ib_item_add.setOnClickListener(mListener);
					holder.ib_item_add.setTag(position);	//注：这样能使所有listview的item都共用同一个listener，而不用为每个item都设置各自的listener！！！
				}
			}

			if((tmpData.getLevel()==2 && manage==0) || tmpData.getType() == 2 || tmpData.getType() == 0){
				holder.ib_next.setVisibility(View.INVISIBLE);
			}else{
				if(manage == 0){
					holder.ib_next.setVisibility(View.VISIBLE);
				}else if(manage == 1){
					holder.ib_next.setVisibility(View.VISIBLE);
					holder.ib_next.setImageResource(R.drawable.extra_info_edit);
					
					holder.ib_next.setOnClickListener(mListener);
					holder.ib_next.setTag(position);	//注：这样能使所有listview的item都共用同一个listener，而不用为每个item都设置各自的listener！！！
				}
			}

			holder.tv_sub_selcet_conn.setText(tmpData.getConn());
			if(manage == 1 && tmpData.getType() != 2){
				holder.tv_sub_selcet_conn.setOnClickListener(mListener);
				holder.tv_sub_selcet_conn.setTag(position);	//注：这样能使所有listview的item都共用同一个listener，而不用为每个item都设置各自的listener！！！
			}
			break;
		default:
			break;
		}
		return convertView;
	}
	private class viewHolder{
		ImageButton ib_item_add;
		TextView tv_sub_selcet_conn;
		ImageButton ib_next;
	}
	
	private class viewAddHolder{
		ImageView iv_add_icon;
		TextView tv_add;
	}
	/**
	 * 用于回调的抽象类
	 * @author ppl
	 * 2017-2-23
	 */
	public static abstract class MyClickListener implements OnClickListener {
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
