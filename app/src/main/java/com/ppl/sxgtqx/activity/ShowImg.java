package com.ppl.sxgtqx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ppl.sxgtqx.R;

public class ShowImg extends Activity{
	private ViewPager viewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_show_img);
		viewPager = (ViewPager) findViewById(R.id.VP_showImg);
	}

}
