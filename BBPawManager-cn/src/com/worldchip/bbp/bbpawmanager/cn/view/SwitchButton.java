package com.worldchip.bbp.bbpawmanager.cn.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;

import com.worldchip.bbp.bbpawmanager.cn.R;

public class SwitchButton extends View {
	
    private boolean isOpen = false;//记录当前按钮是否打开,true为打开,flase为关闭
    private Bitmap mOnBg = null;
    private Bitmap mOffBg = null;
    private Bitmap mSlipButton = null;
    private float mBeginTouchX = 0;  
    private float mCurrTouchX = 0;  
    private boolean mSliping = false;
    private int mTrackWidth = 0;
    private int mTrackHeight = 0;
    private float mCenterX = 0f;
    private float mCenterY = 0f;
    private int mSlipButtonWidth = 0;
    private int mSlipButtonHeight = 0;
    private OnSwitchChangeListener mListener = null;
    private boolean isFirstRun = true;
    
    public interface OnSwitchChangeListener {  
        public void onChanged(SwitchButton button, boolean state);  
    }
    
    public SwitchButton(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
        init();  
    }  
  
    public SwitchButton(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        init();  
    }  
  
    private void init(){  
    	mOnBg = BitmapFactory.decodeResource(getResources(), R.drawable.swicth_btn_on);  
    	mOffBg = BitmapFactory.decodeResource(getResources(), R.drawable.swicth_btn_off);
    	mSlipButton = BitmapFactory.decodeResource(getResources(), R.drawable.swicth_slip_btn);  
    	mSlipButtonWidth = mSlipButton.getWidth();
    	mSlipButtonHeight = mSlipButton.getHeight();
    }  
      
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	int measureWidth = measureWidth(widthMeasureSpec);
    	int measureHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
        mTrackWidth = measureWidth - getPaddingLeft() - getPaddingRight();
        mTrackHeight = measureHeight - getPaddingTop() - getPaddingBottom();
        mCenterX = (float)mTrackWidth/2;
        mCenterY = (float)mTrackHeight/2;
        if (isFirstRun) {
        	if (isOpen) {
    			mCurrTouchX = mTrackWidth - mSlipButtonWidth;
    		} else {
    			mCurrTouchX = 0;
    		}
        	isFirstRun = false;
        }
        
    }
    
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	result = specSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	result = specSize + getPaddingTop() + getPaddingBottom(); 
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
                int h = (int) (mOnBg.getHeight()+ getPaddingTop() + getPaddingBottom());
                result = Math.min(result, h);
            }
        }
        
        return result;
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {//绘图函数  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
        drawSwitchBg(canvas);
        drawSlipButton(canvas);
    }  
  
    private void drawSwitchBg(Canvas canvas) {
    	if(mCurrTouchX < (mTrackWidth/2)) {
            canvas.drawBitmap(mOffBg, mCenterX - (float)mOffBg.getWidth()/2, mCenterY - (float)mOffBg.getHeight()/2, null);
        } else {  
            canvas.drawBitmap(mOnBg, mCenterX - (float)mOnBg.getWidth()/2, mCenterY - (float)mOnBg.getHeight()/2, null);
        }
    }
  
	private void drawSlipButton(Canvas canvas) {
		float x;
		if (mSliping) {// 是否是在滑动状态,
			if (mCurrTouchX >= mTrackWidth) {
				x = mTrackWidth - mSlipButtonWidth / 2;
			} else {
				x = mCurrTouchX - mSlipButtonWidth / 2;
			}
		} else {// 非滑动状态
			if (isOpen) {
				x = mTrackWidth - mSlipButtonWidth / 2;
			} else {
				x = 0;
			}
		}
		if (x < 0) {
			x = 0;
		} else if (x > mTrackWidth - mSlipButtonWidth) {
			x = mTrackWidth - mSlipButtonWidth;
		}
		canvas.drawBitmap(mSlipButton, x, mCenterY - mSlipButtonHeight/2, null);
	}
    
	 @Override
	    public boolean onTouchEvent (MotionEvent event) {  
        // TODO Auto-generated method stub  
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX() > mTrackWidth
					|| event.getY() > mTrackHeight) {
				return false;
			}
			mSliping = true;
			mBeginTouchX = event.getX();
			mCurrTouchX = mBeginTouchX;
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrTouchX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			mSliping = false;
			if (event.getX() >= (mTrackWidth / 2)) {
				isOpen = true;
			} else {
				isOpen = false;
			}
			if (mListener != null) {
				mListener.onChanged(this, isOpen);
			}
			break;
		}
		invalidate();// 重画控件
		return true;
    }  
      
	 public void setOnSwitchChangeListener (OnSwitchChangeListener listener) {
		 mListener = listener;
	 }

	public void setOpened(boolean isOpen) {
		this.isOpen = isOpen;
	}
    
	 
}
