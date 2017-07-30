package com.worldchip.bbp.ect.view;

import java.lang.reflect.Field;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.R.id;
import com.worldchip.bbp.ect.R.layout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FloatWindowSmallView extends LinearLayout   {
	private static final String ACTION_BUTTON_BACK="cn.worldchip.www.BUTTON_BACK";
	public static int mviewWidth;

	public static int mviewHeight;

	private static int mstatusBarHeight;

	private WindowManager windowManager;

	private WindowManager.LayoutParams mParams;

	private float mxInScreen;

	private float myInScreen;

	private float mxDownInScreen;

	private float myDownInScreen;
	
	private float mxInView;

	private float myInView;
	
	ImageView mImageView;
    private boolean mIsTouchMoved;
    private Context mCtx;
    
	public FloatWindowSmallView(Context context) {
		super(context);
		mCtx = context;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
		View view = findViewById(R.id.small_window_layout);
		mviewWidth = view.getLayoutParams().width;
		mviewHeight = view.getLayoutParams().height;
		mImageView = (ImageView) findViewById(R.id.smallImageView);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			myInView = event.getY();
			mxDownInScreen = event.getRawX();
			myDownInScreen = event.getRawY() - getmstatusBarHeight();
			mxInScreen = event.getRawX();
			myInScreen = event.getRawY() - getmstatusBarHeight();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(event.getRawX()-mxDownInScreen) > 100 
			 ||Math.abs(event.getRawY()-myDownInScreen) > 100 ){
				mIsTouchMoved = true;
				mxInScreen = event.getRawX();
				myInScreen = event.getRawY() - getmstatusBarHeight();
			    updateViewPosition();
			}
			break;
		case MotionEvent.ACTION_UP:
          if(mIsTouchMoved){
        	    mIsTouchMoved = false;
        	    mxInScreen = event.getRawX();
				myInScreen = event.getRawY() - getmstatusBarHeight();
				if(mxInScreen>windowManager.getDefaultDisplay().getWidth()/2){
					mParams.x=windowManager.getDefaultDisplay().getWidth();
					mParams.y = (int) (myInScreen - myInView);
					windowManager.updateViewLayout(this, mParams);
					windowManager.updateViewLayout(this, mParams);
				}
				else{
					mParams.x=0;
					mParams.y=(int) (myInScreen - myInView);
					windowManager.updateViewLayout(this, mParams);
					windowManager.updateViewLayout(this, mParams);
				}
			}else{
              //Toast.makeText(mCtx, "click event...", Toast.LENGTH_LONG).show();
				mCtx.sendBroadcast(new Intent(ACTION_BUTTON_BACK));
			}
			
			break;
		default:
			break;
		}
		return true;
	}

	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	private void updateViewPosition() {
		mParams.x = (int) (mxInScreen - mxInView);
		mParams.y = (int) (myInScreen - myInView);
		windowManager.updateViewLayout(this, mParams);
	}

	

	private int getmstatusBarHeight() {
		if (mstatusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				mstatusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mstatusBarHeight;
	}
}
