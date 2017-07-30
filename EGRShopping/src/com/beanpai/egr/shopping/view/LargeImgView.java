package com.beanpai.egr.shopping.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LargeImgView extends ImageView{
	 
    public static final String TAG = LargeImgView.class.getName();
     
    private BitmapRegionDecoder mDecoder;
    private final Rect mRect = new Rect();
    Bitmap[] bs ;
    int sw, sh;
    private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
     
    private float minScale; //默认是设备宽度/460
     
    Context cxt;
    int screenW = 1280;
    public LargeImgView(Context context) {
        this(context, null);
    }
 
    public LargeImgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public LargeImgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        cxt = context;
        setImageResource(R.color.white);
    }
 
    public void setMinScale1(float s){
        this.minScale = s;
    }
     
    /**
     * 每次绘制图片的长度高度
     * */
    public void setWH(int reqW,int reqH){
        this.sw = reqW;
        this.sh = reqH;
    }
     
    public void setImageBitmap1(final Bitmap bm) {
       // Toast.makeText(this.getContext(), "setImageBitmap1 bitmap"+bm.getHeight(), 0).show();
        startAnimation(AnimationUtils.loadAnimation(cxt, R.anim.fade_in));
        new Thread(new Runnable() {
            @SuppressLint("NewApi")
			@Override
            public void run() {
                try {
                    int w = bm.getWidth();
                    int h = bm.getHeight();
                    int c = h/sh;
                    int count = h%sh==0?c:c+1;
                    Log.e(TAG, "setImageBitmap1...sh=" + sh + ", w="+w+", h=" + h+",count="+count+",Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);
                    if(Build.VERSION.SDK_INT>1000){//因兼容低版本问题，暂不使用此切图方式
                        InputStream is = getIsFromBitmap(bm,100);
                        mDecoder = BitmapRegionDecoder.newInstance(is, true);
                        bs = new Bitmap[count];
                        for(int i=0;i<count;i++){
                            mRect.set(0, sh*i, w, (i+1)*sh);
                            bs[i] = mDecoder.decodeRegion(mRect, null);
                        }
                    }else{
                        bs = new Bitmap[count];
                        for(int i=0;i<count;i++){
                            int height = i==(count-1)? h-i*sh:sh;
                            bs[i] =Bitmap.createBitmap(bm, 0, sh*i, w, height);
                            if(bs[i] == null){
                                throw new IllegalArgumentException("bitmap is null,pos at " + i); 
                            }
                        }
                    }
                    if(bm!=null) bm.recycle();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(); 
    }
     
    public InputStream getIsFromBitmap(Bitmap bm, int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            int top = 0;
            for(int i=0;bs!=null && i<bs.length;i++){
            	Log.d(TAG,"onDraw...bs.length="+bs.length+",top="+top+",scale="+minScale);
                canvas.save();
                if(minScale != 0){
                    canvas.scale(minScale, minScale);
                }
                if(bs[i] == null){
                    return;
                }
                canvas.drawBitmap(bs[i], 0,top, mPaint);
                canvas.restore();
                top+=bs[i].getHeight();
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
     
    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(TAG,"get notify...");
            postInvalidate();
        }
    };
}
