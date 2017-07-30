package com.egreat.adlauncher.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class ScrollTextView extends TextView {
    public final static String TAG = "ScrollTextView";
   
    Context mContext=null;
    AttributeSet mAttributeSet=null;
    int mDefStyle =0;
    
    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容
    private float mSpeed=(float) 1;
   
    public ScrollTextView(Context context) {
        super(context,null);
        initView();
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        initView();
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mAttributeSet = attrs;
        mDefStyle =defStyle;
        initView();
    }

    private void initView()
    {
    }
    
    public void init(){
    	paint = getPaint();
        Log.d(TAG, " paint.getColor()=" + paint.getColor());
        paint.setColor(getCurrentTextColor());
        text = getText().toString();
        if (text == null)
        	text="";
        Log.d(TAG, " text=" + text);
        textLength = paint.measureText(text);
        step = textLength;        
        temp_view_plus_text_length =  textLength;
        Log.d(TAG, " getWidth=" + getWidth());
        Log.d(TAG, " temp_view_plus_text_length=" + temp_view_plus_text_length);
        temp_view_plus_two_text_length = textLength*2 ;
        y = getTextSize() + getPaddingTop();
        startScroll();
    }
    
    /**
     * 滚动速度，每次画的间隔距离
     * @param speed
     */
    public void setSpeed(float speed)
    {
    	mSpeed = speed;
    }
   
    public void startScroll()
    {
        isStarting = true;
        invalidate();
    }
   
   
    public void stopScroll()
    {
        isStarting = false;
        invalidate();
    }
   

    @Override
    public void onDraw(Canvas canvas) {
   // 	Log.d("ScrollTextView","debug here step="+step);
   //  Log.d(TAG, "onDraw getWidth=" + getWidth());
        if(!isStarting)
        {
        	canvas.drawText(text, temp_view_plus_text_length+getWidth()+2 - textLength, y, paint);
        	//Log.d("ScrollTextView","drawText x="+(temp_view_plus_text_length - textLength)+" y="+y);
        	step = textLength;
            return;
        }
        canvas.drawText(text, temp_view_plus_text_length+getWidth()+2 - step, y, paint);
       //Log.d("ScrollTextView","drawText x="+(temp_view_plus_text_length - step)+" y="+y);
        step += mSpeed;
        if(step > temp_view_plus_two_text_length+getWidth()+2)
            step = textLength;
        invalidate();
    }
    
    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
    }
    
    public final void setNewText(int text) {
    	super.setText(text);
    	init();
    }
    
    public final void setNewText(CharSequence text) {
    	super.setText(text);
    	init();
    }
}