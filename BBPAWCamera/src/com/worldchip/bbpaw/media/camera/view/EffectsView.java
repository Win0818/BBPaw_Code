package com.worldchip.bbpaw.media.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.worldchip.bbpaw.media.camera.activity.CameraActivity.SpecicalEfficacy;
import com.worldchip.bbpaw.media.camera.utils.CameraConfig;
import com.worldchip.bbpaw.media.camera.utils.Configure;
import com.worldchip.bbpaw.media.camera.utils.ImageTool;

public class EffectsView extends View {

	private Bitmap mEffectBitmap = null;
	private Bitmap mPicBitmap = null;
	private Bitmap mAccessoriesBitmap = null;
	private int mWidth;
	private int mHeight;
	private Context mContext;
	public SpecicalEfficacy mEffectMode = SpecicalEfficacy.GENERAL;
	private Paint mPaint;
	private Canvas mCanvas;
	private Bitmap mDrawBitmap;
	private int mCurPaintSize = 10;
	private boolean mCanDraw = false;
	
		
	public EffectsView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EffectsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mWidth = CameraConfig.CAMERA_PIC_SIZE_WIDTH;
		mHeight = CameraConfig.CAMERA_PIC_SIZE_HEIGHT;
		//intPaint();
	}

	public EffectsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构建涂鸦画笔 画布
	 */
	public void creatDrawCanvas() {
		if (mCanvas == null || mDrawBitmap == null) {
			mDrawBitmap = Bitmap.createBitmap(mWidth, mHeight , Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mDrawBitmap);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(mCurPaintSize);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		
		if (mPicBitmap != null) {//居中绘制
			//canvas.drawBitmap(mPicBitmap, (mWidth - mPicBitmap.getWidth())/2, mHeight - (mHeight - mPicBitmap.getHeight())/2, null);
			canvas.drawBitmap(mPicBitmap, 0, 0, null);
		} 
		
		if (mEffectBitmap != null) {
			canvas.drawBitmap(mEffectBitmap, 0, 0, null);
		}
		if (mDrawBitmap != null) {
			canvas.drawBitmap(mDrawBitmap, 0, 0, null);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		Configure.screenWidth = w;
		Configure.screenHeight = h;
		mWidth = w;
		mHeight = h;
	}

	/**
	 * 清除涂鸦效果
	 */
	public void clearDraw() {
		mDrawBitmap = null;
		invalidate();
	}
	
	/**
	 * 清除个性效果
	 */
	
	public void clearEffectDraw() {
		mEffectBitmap = null;
		invalidate();
	}
	
	public void clearAll() {
		mPicBitmap = null;
		mEffectBitmap = null;
		mDrawBitmap = null;
		mAccessoriesBitmap = null;
		invalidate();
	}
	
	public void clearAllEffectDraw() {
		mEffectBitmap = null;
		mDrawBitmap = null;
		mAccessoriesBitmap = null;
		invalidate();
		
	}
	
	public boolean hasEffect() {
		if (mEffectBitmap != null || mAccessoriesBitmap != null || mDrawBitmap != null) {
			return true;
		}
		return false;
	}
	
	private float mX, mY;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		if (mCanDraw && mEffectMode == SpecicalEfficacy.GRAFFITI) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {// 如果拖动
				mCanvas.drawLine(mX, mY, event.getX(), event.getY(), mPaint);// 画线
				invalidate();
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {// 如果点击
				mX = (int) event.getX();
				mY = (int) event.getY();
				mCanvas.drawPoint(mX, mY, mPaint);// 画点
				invalidate();
			}
			mX = (int) event.getX();
			mY = (int) event.getY();
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if (mEffectMode == SpecicalEfficacy.ACCESSORIES) {
				drawProps(x, y);
			}
		}
		return true;
	}

	
	/**
	 * 添加饰品及人物
	 * @param x
	 * @param y
	 * @param resid
	 */
	private void drawProps(float x, float y) {
		if (mAccessoriesBitmap != null) {
			mCanvas.drawBitmap(mAccessoriesBitmap, x - mAccessoriesBitmap.getWidth() / 2, y - mAccessoriesBitmap.getHeight() / 2, null);
			invalidate();
		}
	}


	public void onDrawEffectImage(int res) {
		mEffectBitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
		invalidate();
	}

	public void setPicBitmap(Bitmap picBitmap) {
		mPicBitmap = picBitmap;
		invalidate();
	}
	
	//---------------------------------------------------------
	/**
	 * 绘制哈哈镜效果
	 * @param picBitmap
	 */
	public void drawPicEffectBitmap(Bitmap picBitmap) {
		setPicBitmap(picBitmap);
		invalidate();
	}
	
	public void setAccessoriesBitmap(int res) {
		mAccessoriesBitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
	}
	

	public void savePic() {

		if (mPicBitmap == null || mPicBitmap.isRecycled()) {
			return;
		} 
		
		int width = mPicBitmap.getWidth();
		int height = mPicBitmap.getHeight();
		Log.e("lee", "savePic mPicBitmap Width = "+width +" height == "+height);
		//Bitmap picBitmap = ImageTool.creatCustomBitmap(mPicBitmap, CameraConfig.CAMERA_PIC_SIZE_WIDTH, CameraConfig.CAMERA_PIC_SIZE_HEIGHT);
		Bitmap tempBmp = Bitmap.createBitmap(width, height,
								Bitmap.Config.ARGB_8888);
		Canvas tempCanvas = new Canvas(tempBmp);

		if (mPicBitmap != null) {
			tempCanvas.drawBitmap(mPicBitmap, 0, 0, null);
		} 
		if (mEffectBitmap != null) {
			tempCanvas.drawBitmap(mEffectBitmap, 0, 0, null);
		}
		if (mDrawBitmap != null) {
			tempCanvas.drawBitmap(mDrawBitmap, 0, 0, null);
		}
		ImageTool.savePic(mContext, tempBmp);
	}

	public void setCurrEffectMode(SpecicalEfficacy effect) {
		mEffectMode = effect;
	}
    
    public void setGraffitiEnabled(boolean enabled) {
    	if (enabled) {
    		if (mDrawBitmap == null) {
    			creatDrawCanvas();
    		}
    	} 
    	mCanDraw = enabled;
    }
    
    public void release() {
    	if (mDrawBitmap!=null&& !mDrawBitmap.isRecycled()) {
    		mDrawBitmap.recycle();
    		mDrawBitmap = null;
    	}
    	if (mPicBitmap!=null && !mPicBitmap.isRecycled()) {
    		mPicBitmap.recycle();
    		mPicBitmap = null;
    	}
    	invalidate();
    }
   
}
