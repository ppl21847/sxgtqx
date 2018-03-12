package com.ppl.sxgtqx.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.utils.MyRouteLine;

import java.util.List;


public class RouteAdapter extends BaseAdapter {
	Context mContext;
	List<MyRouteLine>data;
	LayoutInflater layoutInflater;
	
	public RouteAdapter(Context mContext, List<MyRouteLine> data) {
		super();
		this.mContext = mContext;
		this.data = data;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size() > 3?3:data.size();
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		viewHolder holder = null;
		if(convertView == null){
			holder = new viewHolder();
			convertView = layoutInflater.inflate(R.layout.item_route_line, null);
			holder.tv_num = (TextView) convertView.findViewById(R.id.tv_item_line_num);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_item_line_time);
			holder.tv_dist = (TextView) convertView.findViewById(R.id.tv_item_line_dist);
			holder.ll_item_line = (LinearLayout) convertView.findViewById(R.id.ll_item_line);
			convertView.setTag(holder);
		}else{
			holder = (viewHolder)convertView.getTag();
		}
		MyRouteLine tmpData = data.get(position);
		if(tmpData.isShowSta()){
			holder.tv_num.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.tv_dist.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.ll_item_line.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.line_select));
		}else{
			holder.tv_num.setTextColor(mContext.getResources().getColor(R.color.gray_light2));
			holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.black));
			holder.tv_dist.setTextColor(mContext.getResources().getColor(R.color.gray_light2));
			holder.ll_item_line.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.line_dis_select));
		}
		if(position == 0){
			holder.tv_num.setText("推荐");
		}else{
			holder.tv_num.setText("方案"+position);
		}
		int time = tmpData.getLine().getDuration();
        if ( time / 3600 == 0 ) {
            holder.tv_time.setText( time / 60 + "分钟" );
        } else {
            holder.tv_time.setText( time / 3600 + "小时" + (time % 3600) / 60 + "分钟" );
        }
        int dist = tmpData.getLine().getDistance();
        if(dist / 1000 == 0){
        	holder.tv_dist.setText(dist + "m");
        }else{
        	holder.tv_dist.setText((dist/1000) + "Km");
        }
        
		return convertView;
	}
	private class viewHolder{
		LinearLayout ll_item_line;
		TextView tv_num;
		TextView tv_time;
		TextView tv_dist;
	}

}
