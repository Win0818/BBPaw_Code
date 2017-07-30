package com.worldchip.bbpawphonechat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.worldchip.bbpawphonechat.R;

public class MySlipButton extends View implements OnTouchListener{
	private Bitmap bg_on;
    private Bitmap bg_off;
    private Bitmap slip_btn_on;
    private Bitmap slip_btn_off;
    private boolean mIsOn;
    private OnChangedListener ChgLsn;
    private boolean isChgLsnOn = false;
    
	public MySlipButton(Context context) {
		super(context);
		initView();
	}

	public MySlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private void initView(){
		bg_on = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg_on);
        bg_off = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg);
        slip_btn_on = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_src);
        slip_btn_off = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_close_src);
        setOnTouchListener(this);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
	    Paint paint = new Paint();
	    if(mIsOn){
	    	canvas.drawBitmap(bg_on, matrix, paint);
	    	float x  = bg_on.getWidth()-slip_btn_on.getWidth();
	    	canvas.drawBitmap(slip_btn_on, x, 0, paint);
	    }else{
	    	canvas.drawBitmap(bg_off, matrix, paint);
	    	canvas.drawBitmap(slip_btn_off, 0, 0, paint);
	    }
	    System.out.println("---MySlipButton---onDraw---"+mIsOn);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(mIsOn){
				setmIsOn(false);
			}else{
				setmIsOn(true);
			}
			System.out.println("---event.getx--==--"+event.getX()+"---event.getY--==--"+event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
			
		default:
			break;
		}
		ChgLsn.OnChanged(mIsOn);
		invalidate();
		return false;
	}
	
	public boolean ismIsOn() {
		return mIsOn;
	}

	public void setmIsOn(boolean mIsOn) {
		this.mIsOn = mIsOn;
	}
   
	public void setOnChangedListener(OnChangedListener l) {
	        isChgLsnOn = true;
	        ChgLsn = l;
	   }

	public interface OnChangedListener {
	        abstract void OnChanged(boolean CheckState);
	    }

	   
    

}
