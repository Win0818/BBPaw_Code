package com.worldchip.bbp.bbpawmanager.cn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.SeekBar;

public class InformVerticalSeekBar extends SeekBar {

	private ListView mScrollListView = null;
	public InformVerticalSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InformVerticalSeekBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public InformVerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		// 将SeekBar转转90度
		canvas.rotate(90);
		// 将旋转后的视图移动回来
		canvas.translate(0, -getWidth());
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			int i = 0;
			i = getMax() - (int) (getMax() * event.getY() / getHeight());
			setProgress(100 - i);
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			scroll(event.getY());
			break;

		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return true;
	}

	public void setScrollListView(ListView listView) {
		mScrollListView = listView;
	}
	public void onScrollBarSizeChang() {
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}
	
	private void scroll(float height) {
		if (mScrollListView != null) {
			int itemCount = mScrollListView.getAdapter().getCount();
	        int position = (int) ((height / getHeight()) * itemCount);
	        if (position < 0) {
	        	position = 0;
	        } else if (position >= itemCount){
	        	position = itemCount - 1;
	        }
	        mScrollListView.setSelection(position);
		}
        
    }
}
