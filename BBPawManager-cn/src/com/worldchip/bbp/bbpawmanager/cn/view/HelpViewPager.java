package com.worldchip.bbp.bbpawmanager.cn.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.worldchip.bbp.bbpawmanager.cn.view.HelpView.HelpViewListener;
public class HelpViewPager extends ViewPager {

	private HelpViewListener mListener;
	private float mBeginTouch = 0;
	
	public HelpViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HelpViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setListener(HelpViewListener listener) {
		mListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mBeginTouch = event.getX();
			if (mListener != null) {
				mListener.onBeginTouch();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mListener != null) {
				mListener.onEndTouch();
			}
			if (event.getX() == mBeginTouch) {
				if (mListener != null) {
					mListener.onClose();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
}
