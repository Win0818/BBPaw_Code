package com.worldchip.bbp.ect.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {
	
	private MYGalleryOnTounchListener mListener = null;
	
	public interface MYGalleryOnTounchListener {
		public void onGalleryTouchEvent(MotionEvent event);
	}
	
	
	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGallery(Context context) 
	{
		super(context);
	}
	
	public boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) 
	{ 
	    return e2.getX() > e1.getX(); 
	} 
	
	public void setOnTounchListener(MYGalleryOnTounchListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (mListener != null) {
			mListener.onGalleryTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

	/**
	* �ٵ���ȥ���¼�
	* �ڵ�ǰ���¼�
	* �ۻ�����x�����ٶ�
	* ��y������ٶ�
	*/
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) 
	{
		/*System.out.println(e1.getX()+"-------"+e2.getX() + "++++"+ (e1.getX() - e2.getX()));
		System.out.println(e1.getDownTime()+"-------"+e2.getDownTime());
		System.out.println(e1.getEventTime()+"-------"+e2.getEventTime());*/
		System.out.println("velocityX-----"+velocityX / 5);
		return super.onFling(e1, e2, velocityX / 2, velocityY);
		/*velocityX = velocityX > 0 ? 3200 : -3200;
		return super.onFling(e1, e2, velocityX, velocityY);*/
	   /* if (e2.getX() > e1.getX()) 
	    {
	        // ����߻���
	        super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
	    } else {
	        // ���ұ߻���
	        super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
	    }*/
	   /* int keyCode; 
        if (isScrollingLeft(e1, e2))
        {       
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT; 
        } else { 
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT; 
        } 
        onKeyDown(keyCode, null); */
        //return false; 
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		/*float x = 0f;
		if (e2.getX() < e1.getX()) 
		{
			x = 15f;
		} else {
			x = -15f;
		}
		boolean result = false;
		if (Math.abs(distanceX) < 10) 
		{
			result = super.onScroll(e1, e2, x, distanceY);
		} else {
			result = super.onScroll(e1, e2, (-1) * distanceX, distanceY);
		}
        return result;*/
		return super.onScroll(e1, e2, distanceX * 1.5f, distanceY);
	}
}