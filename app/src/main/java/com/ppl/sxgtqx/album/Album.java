package com.ppl.sxgtqx.album;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ppl.sxgtqx.R;


public class Album extends Activity {
	private GridView gv;
	public static List<ImageBucket> dataList;
	ImageBucketAdapter adapter;// �Զ����������
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		init();
		initView();
	}
	/**
	 * ��ʼ������
	 */
	void init(){
		dataList = helper.getImagesBucketList(false);
	}
	
	/**
	 * ��ʼ��view��ͼ
	 */
	private void initView() {
		gv = (GridView) findViewById(R.id.gv);
		adapter = new ImageBucketAdapter(Album.this, dataList);
		gv.setAdapter(adapter);

		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * ����position���������Ի�ø�GridView����View��󶨵�ʵ���࣬Ȼ���������isSelected״̬��
				 * ���ж��Ƿ���ʾѡ��Ч���� ����ѡ��Ч���Ĺ��������������Ĵ����л���˵��
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * ֪ͨ���������󶨵����ݷ����˸ı䣬Ӧ��ˢ����ͼ
				 */
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AlbumDetail.class);
				intent.putExtra(Album.EXTRA_IMAGE_LIST, position);
				startActivity(intent);
				finish();
			}

		});
	}
}
