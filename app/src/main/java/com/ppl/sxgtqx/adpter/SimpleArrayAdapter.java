package com.ppl.sxgtqx.adpter;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
/**
 * 给下拉框的适配器
 * Created by IBM on 2016/10/25.
 */
public class SimpleArrayAdapter<T> extends ArrayAdapter {

	public SimpleArrayAdapter(Context context, int resource, List objects) {
		super(context, resource, objects);
	}
	
	//复写这个方法，使返回的数据没有最后一项
	@Override
	public int getCount() {
		// don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
	}

}
