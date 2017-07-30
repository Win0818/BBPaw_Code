package com.worldchip.bbp.bbpawmanager.cn.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

public class SwitherImageView extends ImageView {
	
	private GestureDetector mGestureDetector = null;
	
	public SwitherImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}
	
	public SwitherImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}

	public SwitherImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}


	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int mFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 向左滑动
				//getNextMonth(mFlag);
				Log.e("lee", "scrool left --------------------->");
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				//getPrevMonth(mFlag);
				Log.e("lee", "scrool right --------------------->");
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return SwitherImageView.this.mGestureDetector.onTouchEvent(event);
	}
	
	
}
