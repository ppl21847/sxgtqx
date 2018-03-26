package com.ppl.sxgtqx.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.ppl.sxgtqx.R;


public class CustomDialog extends ProgressDialog{
	private String msgShow;

	public CustomDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CustomDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}
	
	
	
	public CustomDialog(Context context, int theme, String msgShow) {
		super(context, theme);
		this.msgShow = msgShow;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		init(getContext());
	}

	private void init(Context context)
	{
		//设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
		setCancelable(false);
		setCanceledOnTouchOutside(false);

		setContentView(R.layout.load_dialog);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		
		TextView tv = (TextView) findViewById(R.id.tv_load_dialog);
		tv.setText(msgShow);
	}

	@Override
	public void show()
	{
		super.show();
	}



}
