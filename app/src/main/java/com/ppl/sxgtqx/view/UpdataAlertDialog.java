package com.ppl.sxgtqx.view;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppl.sxgtqx.R;

public class UpdataAlertDialog extends Dialog {
    private static final String TAG = UpdataAlertDialog.class.getSimpleName();
    private TextView mTitle, mTips;
    private ImageView mIcon;
    private TextView mMessage;
    private TextView mPositiveBtn;
    private TextView mNegativeBtn;
    private TextView mNeutralBtn;
    private RelativeLayout mRootLayout;
    private View mBottomBar;
    private View mBottomBar1;
    private LinearLayout mLLProcess;
    private HorizontalProgressBarWithNumber mHpb;

    private Context mContext;
    public HandlerActionListener mListener;
    private static final int DURATION = 500;
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    public UpdataAlertDialog(Context context) {
        super(context, R.style.UpdateDialog);
        setContentView(R.layout.new_updata_dialog);
        mTitle = (TextView) findViewById(R.id.title);
        mIcon = (ImageView) findViewById(R.id.icon);
        mMessage = (TextView) findViewById(R.id.message);
        mPositiveBtn = (TextView) findViewById(R.id.positive);
        mNegativeBtn = (TextView) findViewById(R.id.tv_negative);
        mNeutralBtn = (TextView) findViewById(R.id.tv_neutral);
        mBottomBar =  findViewById(R.id.bottomBar);
        mBottomBar1 =  findViewById(R.id.bottomBar1);
        mRootLayout = (RelativeLayout) findViewById(R.id.updatadialog_parent_layout);
        mLLProcess = (LinearLayout)findViewById(R.id.ll_process);
        mHpb = (HorizontalProgressBarWithNumber) findViewById(R.id.hpb_down);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        mContext = context;
    }

    public void setTitle(int resId) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(resId);
    }

    public void setIcon(int resId) {
        mIcon.setVisibility(View.VISIBLE);
        mIcon.setImageResource(resId);
    }

    public void setMessage(int resId) {
        setMessage(getContext().getString(resId));
    }

    public void setMessage(String msg) {
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(msg);
    }
   

    public void setView(View view) {
        ViewGroup content = (ViewGroup) findViewById(R.id.content);
        content.removeAllViews();
        content.addView(view);
    }

    public void setProcess(int process){
        if(mHpb != null){
            mHpb.setProgress(process);
        }
    }

    public void setPositiveButton(String resId, View.OnClickListener listener) {
        Log.d(TAG,"非强制升级设置界面");
        mBottomBar.setVisibility(View.VISIBLE);
        mPositiveBtn.setVisibility(View.VISIBLE);
        mNegativeBtn.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(resId)) {
            mPositiveBtn.setText(resId);
        }

        if (listener != null) {
            mPositiveBtn.setOnClickListener(new OnClickListenerWrapper(listener));
        } else {
            mPositiveBtn.setOnClickListener(mCloseListener);
        }
    }

    public void setNegativeButton(int resId, View.OnClickListener listener) {
        mNegativeBtn.setVisibility(View.VISIBLE);
        mBottomBar.setVisibility(View.VISIBLE);

        if (resId > 0) {
            mNegativeBtn.setText(resId);
        }

        if (listener != null) {
            mNegativeBtn.setOnClickListener(new OnClickListenerWrapper(listener));
        } else {
            mNegativeBtn.setOnClickListener(mCloseListener);
        }
    }

    public void setNeutralButton(int resId, View.OnClickListener listener) {
        mBottomBar1.setVisibility(View.VISIBLE);
        mBottomBar.setVisibility(View.GONE);

        if (resId > 0) {
            mNeutralBtn.setText(resId);
        }

        if (listener != null) {
            mNeutralBtn.setOnClickListener(new OnClickListenerWrapper(listener));
        } else {
            mNeutralBtn.setOnClickListener(mCloseListener);
        }
    }

    private View.OnClickListener mCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	Log.d(TAG, "dialog dismiss");
            dismiss();
        }
    };

    private class OnClickListenerWrapper implements View.OnClickListener {
        private View.OnClickListener mListener;

        public OnClickListenerWrapper(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View v) {
            if(v != mNegativeBtn){
                if (mListener != null) {
                    mBottomBar.setVisibility(View.GONE);
                    mBottomBar1.setVisibility(View.GONE);
                    mLLProcess.setVisibility(View.VISIBLE);
                }
            }
            if(mListener != null){
                mListener.onClick(v);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mListener != null) {
            mListener.handleAction();
        }
    }

    public interface HandlerActionListener{
        void handleAction();
    }
}
