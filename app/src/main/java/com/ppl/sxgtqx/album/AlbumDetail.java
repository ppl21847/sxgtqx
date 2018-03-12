package com.ppl.sxgtqx.album;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppl.sxgtqx.R;
import com.ppl.sxgtqx.activity.NewSubLoc;


public class AlbumDetail extends Activity implements OnClickListener,OnItemClickListener{

	private static final int FINIFH_TEXT = 1;
	public static List<ImageItem> dataList;
	private AlbumHelper helper;
	private GridView myGrid;
	private Button cancel,ok_button;
	private ImageView IV_album_isBig;
	private TextView TV_album_isBig,myText;
	public static ImageGridAdapter adapter; //相册详情
	private List<String>tmpImgPath;
	BitmapCache cache;
	private int selectTotal = 0;
	@SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
		@SuppressLint("ShowToast") @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(AlbumDetail.this, "最多选择9张图片", Toast.LENGTH_LONG).show();
				break;
			case FINIFH_TEXT:
				ok_button.setText((String)msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());


		int tmpNum = getIntent().getIntExtra(Album.EXTRA_IMAGE_LIST, 0);
		dataList = Album.dataList.get(tmpNum).imageList;

		initView();
		initData();
	}

	private void initView(){
		tmpImgPath = new ArrayList<String>();
		cancel = (Button) findViewById(R.id.cancel);
		ok_button = (Button) findViewById(R.id.ok_button);
		IV_album_isBig = (ImageView) findViewById(R.id.IV_album_isBig);
		TV_album_isBig = (TextView) findViewById(R.id.TV_album_isBig);
		myText = (TextView) findViewById(R.id.myText);
		myGrid = (GridView) findViewById(R.id.myGrid);
	}
	private void initData(){
		cancel.setOnClickListener(AlbumDetail.this);
		ok_button.setOnClickListener(AlbumDetail.this);
		IV_album_isBig.setOnClickListener(AlbumDetail.this);
		TV_album_isBig.setOnClickListener(AlbumDetail.this);

		int selectNum = NewSubLoc.arrySelectImg.size();
		if(selectNum != 0){
			ok_button.setText("("+selectNum+"/"+9+")完成");
		}
		if(dataList.size() > 0){
			myText.setVisibility(View.INVISIBLE);
		}else{
			return;
		}
		OrigImgView();
		adapter = new ImageGridAdapter(AlbumDetail.this, dataList,
				mHandler);
		myGrid.setAdapter(adapter);
		myGrid.setOnItemClickListener(AlbumDetail.this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.cancel:
			//取消按钮
			break;
		case R.id.IV_album_isBig:
		case R.id.TV_album_isBig:
			//原图按钮
			Bimp.setOrig_img_sta(!Bimp.isOrig_img_sta());
			OrigImgView();
			break;
		case R.id.ok_button:
			//完成按钮
			okButton();
			break;
		default:
			break;
		}
	}
	private void okButton(){
		NewSubLoc.picAdapter.notifyDataSetChanged();
		this.finish();
		tmpImgPath.clear();
	}
	private void OrigImgView(){
		//原图与否
		if(Bimp.isOrig_img_sta()){
			IV_album_isBig.setImageResource(R.drawable.account_current);
			IV_album_isBig.setBackgroundResource(R.drawable.ablum_select_photo_selected);
		}else{
			IV_album_isBig.setImageResource(R.drawable.ablum_select_photo_normal);
			IV_album_isBig.setBackgroundResource(R.drawable.ablum_select_photo_normal);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = dataList.size() - 1 - arg2;
		boolean tmpSta = dataList.get(position).isSelected();
		ImageItem item = dataList.get(position);
		String path = dataList.get(position).imagePath;
		boolean selectSta = false;
		for(int j=0;j<NewSubLoc.arrySelectImg.size();j++){
			if(path.equals(NewSubLoc.arrySelectImg.get(j).getImagePath())){
				selectSta = true;
				break;
			}
		}

		if ((NewSubLoc.arrySelectImg.size() ) < 9) {
			if (selectSta) {
				selectTotal--;
				for(int j=0;j<NewSubLoc.arrySelectImg.size();j++){
					if(path.equals(NewSubLoc.arrySelectImg.get(j).getImagePath())){
						NewSubLoc.arrySelectImg.remove(j);
						break;
					}
				}
			} else if (!selectSta) {
				selectTotal++;
				ImageItem tmp = new ImageItem();
				tmp.setImagePath(path);
				tmp.setBmp(StringToBitmap.StringToBitmap(
						path, getWindowManager(),
						NewSubLoc.GRIDVIEW_SCALE));
				NewSubLoc.arrySelectImg.add(tmp);
				NewSubLoc.selectImgPos[NewSubLoc.arrySelectImg.size()-1] = position;
			}
		}else if ((NewSubLoc.arrySelectImg.size() ) >= 9) {
			if (selectSta) {
				selectTotal--;
				for(int j=0;j<NewSubLoc.arrySelectImg.size();j++){
					if(path.equals(NewSubLoc.arrySelectImg.get(j).getImagePath())){
						NewSubLoc.arrySelectImg.remove(j);
						break;
					}
				}
			} else {
				Message message = Message.obtain(mHandler, 0);
				message.sendToTarget();
				return;
			}
		}
		for(int j=0;j<NewSubLoc.arrySelectImg.size();j++){
			if(dataList.get(position).getImagePath().equals(NewSubLoc.arrySelectImg.get(j).getImagePath())){
				NewSubLoc.arrySelectImg.get(j).setSelected(!tmpSta);
				break;
			}
		}
		adapter.notifyDataSetChanged();
		Message msg = new Message();
		int selectNum = NewSubLoc.arrySelectImg.size();
		String finishText = ("("+selectNum+"/"+9+")"+"完成");
		msg.obj = finishText;
		msg.what = FINIFH_TEXT;
		mHandler.sendMessage(msg);
	}
}
