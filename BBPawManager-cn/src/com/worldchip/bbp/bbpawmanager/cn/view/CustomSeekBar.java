package com.worldchip.bbp.bbpawmanager.cn.view;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.OnCustomSeekBarChangeListener;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageTool;

public class CustomSeekBar extends View {

	private static final String TAG = "CustomSeekBar";
	
	
	private static final float DEFAULT_STEP = 0.069f;
	
	private List<OnCustomSeekBarChangeListener> listeners;
	
	private List<Thumb> thumbs;
	
	private float mThumbWidth;
	private float mThumbHeight;
	
	private float thumbHalf;
	private float pixelRangeMin =0;
	private float pixelRangeMax = 100;
	
	private float scaleRangeMin;
	private float scaleRangeMax;
	private float scaleStep;
	
	private Drawable track = null;
	private Drawable range = null;
	private Drawable thumb = null;
	private Drawable seekbarBg = null;
	
	//---------------------------------------------------------------------------------------------------------------
	private static final int DEFAULT_VALUE_TEXT_MARGIN_TOP = 0;
	private int mValueTextMarginTop = DEFAULT_VALUE_TEXT_MARGIN_TOP;
	private int mViewWidth;
	private int mViewHeight;
	
	private Paint mPaint;
	private int mDrawTextHeight = 0;
	
	private static final String TEST_TEXT = "seekbar";
	private int mSeekBarCenterY = 0;
	private static final int DEFAULT_THUMBS = 0;
	private int mThumbCount = DEFAULT_THUMBS;
	private int currentThumb = 0;
	private float lowLimit = pixelRangeMin;//左边最小值
    private float highLimit = pixelRangeMax;//右边最小值
	private float mRangeHeight;
	public static final int SECONDS_TIME_MODE = 0;
    public static final int MINUTE_TIME_MODE = 1;
    public static final int HOUR_TIME_MODE = 2;
    private int mDisplayTimeMode = HOUR_TIME_MODE;
    private String mMinuteStr = "";
    private String mSecondsStr = "";
    private int mMinTime = 0;
    private int mMaxTime = 0;
	public int mSeekBarHeight = 0;
    public static final int DEFAULT_PAINT_COLOR =Color.parseColor("#4085F4");
	private boolean isSliping = false;
    private boolean isFirstRun = true;
	private boolean isInitState = true;
    private int mDefaultStartTime = -1;
    private int mDefaultEndTime = -1;
    private float mTrackLength;
    
	public CustomSeekBar(Context context) {
		super(context);
		init(context);
		
	}

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

	private void init(Context context) {
		scaleRangeMin = 0;
		scaleRangeMax = 100;
		scaleStep = DEFAULT_STEP;
		mViewWidth = 0;
		mViewHeight = 0;
		isFirstRun = true;
		thumbs = new Vector<Thumb>();
		listeners = new Vector<OnCustomSeekBarChangeListener>();
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		initPaint();
		thumb = getResources().getDrawable(R.drawable.ic_custom_seekbar_thumb);
		range = getResources().getDrawable(R.drawable.ic_custom_seekbar_range);
		track = getResources().getDrawable(R.drawable.ic_custom_seekbar_track);
		seekbarBg = getResources().getDrawable(R.drawable.ic_custom_seekbar_bg);
		mSeekBarHeight = seekbarBg.getIntrinsicHeight();
		mThumbWidth = thumb.getIntrinsicWidth();
		mThumbHeight = thumb.getIntrinsicHeight();
		
		mRangeHeight = range.getIntrinsicHeight();
		for (int i = 0; i < mThumbCount; i++) {
			Thumb th = new Thumb();
			thumbs.add(th);
		}
		mMinuteStr = getResources().getString(R.string.minute_text);
		mSecondsStr = getResources().getString(R.string.seconds_text);
	}
	
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setColor(DEFAULT_PAINT_COLOR);
		mPaint.setAntiAlias(true);
		float paintTextSize = getResources().getDimension(R.dimen.eyecare_seekbar_paint_size);
		mPaint.setTextSize(paintTextSize);
		Rect rect = new Rect();
    	mPaint.getTextBounds(TEST_TEXT, 0, TEST_TEXT.length(), rect);
    	mDrawTextHeight = rect.height();
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	mViewWidth = measureWidth(widthMeasureSpec);
    	mViewHeight = measureHeight(heightMeasureSpec);
    	mValueTextMarginTop = getPaddingTop();
        setMeasuredDimension(mViewWidth, mViewHeight);
        thumbHalf =  mThumbWidth/2;
    	pixelRangeMin = 0 + thumbHalf;
    	pixelRangeMax =  mViewWidth - thumbHalf;
        //mTrackLength = (int) (getMeasuredWidth() - getPaddingLeft() - mThumbWidth);
        mTrackLength = getMeasuredWidth() - getPaddingRight() - getPaddingLeft()- mThumbWidth;
         
    	if(isFirstRun) {
            initThumbs();
            isFirstRun = false;
    	}
    	
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);	// 1. Make sure parent view get to draw it's components
        //mSeekBarCenterY = (mViewHeight - mDrawTextHeight - mSeekBarHeight)/2;
        mSeekBarCenterY = getMeasuredHeight() - getPaddingBottom() - mSeekBarHeight/2;
        drawSeekBarBg(canvas);
        drawGutter(canvas);	//绘制已滑过进度条	
        drawRange(canvas);//绘制未滑过进度条		
        drawThumbs(canvas);//绘制滑动按钮
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event) {
    	if(!thumbs.isEmpty()) {
    		
    		float coordinate = event.getX();
    		
	    	if(event.getAction() == MotionEvent.ACTION_DOWN) {
	    		currentThumb = getClosestThumb(coordinate);
	    		lowLimit = getLowerThumbRangeLimit(currentThumb);
	    		highLimit = getHigherThumbRangeLimit(currentThumb);
	    		if(listeners != null && listeners.size() > 0) {
	    			for(OnCustomSeekBarChangeListener listener : listeners) {
	    				listener.onStartTrackingTouch(this);
	    			}
	    		}
	    		isSliping = true;
	    		isInitState = false;
	    	}
	    	
    		/*if(coordinate < lowLimit) {//左边滑块
    			Log.d(TAG,"coordinate "+coordinate +" lowLimit == " + lowLimit);
    			if(lowLimit == highLimit && currentThumb >= thumbs.size()-1) {//处理当2个滑动条重叠时，无法滑动问题
    				currentThumb = getUnstuckFrom(currentThumb);
    				setThumbPos(currentThumb,coordinate);
    				lowLimit = getLowerThumbRangeLimit(currentThumb);
    	    		highLimit = getHigherThumbRangeLimit(currentThumb);
    			} else {
    				setThumbPos(currentThumb,lowLimit);
    			}
    		} else if(coordinate > highLimit) {//右边滑块
				setThumbPos(currentThumb,highLimit);
			} else {
				coordinate = asStep(coordinate);
				setThumbPos(currentThumb,coordinate);
			}*/
	    	moveTo(coordinate);
			if (event.getAction() == MotionEvent.ACTION_UP) {
				isSliping = false;
			}
	    	return true;
    	}
    	return false;
    }
    
    private void moveTo(float coordinate) {
    	if(coordinate < lowLimit) {//左边滑块
			if(lowLimit == highLimit && currentThumb >= thumbs.size()-1) {//处理当2个滑动条重叠时，无法滑动问题
				currentThumb = getUnstuckFrom(currentThumb);
				setThumbPos(currentThumb,coordinate);
				lowLimit = getLowerThumbRangeLimit(currentThumb);
	    		highLimit = getHigherThumbRangeLimit(currentThumb);
			} else {
				setThumbPos(currentThumb,lowLimit);
			}
		} else if(coordinate > highLimit) {//右边滑块
			setThumbPos(currentThumb,highLimit);
		} else {
			coordinate = asStep(coordinate);
			setThumbPos(currentThumb,coordinate);
		}
    }
    private int getUnstuckFrom(int index) {
    	int unstuck = 0;
    	float lastVal = thumbs.get(index).yPosition;
    	for(int i = index-1; i >= 0; i--) {
    		Thumb th = thumbs.get(i);
    		if(th.yPosition != lastVal)
    			return i+1;
    	}
    	return unstuck;
    }
    
    private float asStep(float pixelValue) {
    	return stepScaleToPixel(pixelToStep(pixelValue));
    }
    
    private float pixelToScale(float pixelValue) {
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float scaleRange = (scaleRangeMax - scaleRangeMin);
		float scaleValue = (((pixelValue - pixelRangeMin) * scaleRange) / pixelRange) + scaleRangeMin;
		return scaleValue;
    }
    
    /*private float scaleToPixel(float scaleValue) {
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float scaleRange = (scaleRangeMax - scaleRangeMin);
		float pixelValue = (((scaleValue - scaleRangeMin) * pixelRange) / scaleRange) + pixelRangeMin;
		return pixelValue;
    }*/
    
    private float pixelToStep(float pixelValue) {
    	float stepScaleMin = 0;
    	float stepScaleMax = (float) Math.floor((scaleRangeMax-scaleRangeMin)/scaleStep);
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float stepScaleRange = (stepScaleMax - stepScaleMin);
		float stepScaleValue = (((pixelValue - pixelRangeMin) * stepScaleRange) / pixelRange) + stepScaleMin;
		return Math.round(stepScaleValue);
    }
    
    private float stepScaleToPixel(float stepScaleValue) {
    	float stepScaleMin = 0;
    	float stepScaleMax = (float) Math.floor((scaleRangeMax-scaleRangeMin)/scaleStep);
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float stepScaleRange = (stepScaleMax - stepScaleMin);
		float pixelValue = (((stepScaleValue - stepScaleMin) * pixelRange) / stepScaleRange) + pixelRangeMin;
		return pixelValue;
    }
    
    private void calculateThumbValue(int index) {
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		th.yPosition = pixelToScale(th.xPosition);
    	}
    }
    
  /*  private void calculateThumbPos(int index) {
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		th.xPosition = scaleToPixel(th.yPosition);
    	}
    }*/

    private float getLowerThumbRangeLimit(int index) {
    	float limit = pixelRangeMin; 
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		for(int i = 0; i < thumbs.size(); i++) {
	    		if(i < index) {
	    			Thumb tht = thumbs.get(i);
	    			if(tht.xPosition <= th.xPosition && tht.xPosition > limit) {
	    				limit = tht.xPosition;
	    			}
				}
	    	}
    	}
    	return limit;
    }

    private float getHigherThumbRangeLimit(int index) {
    	float limit = pixelRangeMax; 
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		for(int i = 0; i < thumbs.size(); i++) {
	    		if(i > index){
	    			Thumb tht = thumbs.get(i);
	    			if(tht.xPosition >= th.xPosition && tht.xPosition < limit) {
	    				limit = tht.xPosition;
	    			}
				}
	    	}
    	}
    	return limit;
    }
    
    
    public void initThumbs() {
    	if(!thumbs.isEmpty()) {
    		for(int i = 0; i < thumbs.size(); i++) {
    			if (i==0) {//左边滑块
    				float calculateLowThumbPos = calculateDefaultThumbPos(0);
    				 if(calculateLowThumbPos > pixelRangeMax) {
    					setThumbPos(i, pixelRangeMax);
    				} else if (0 < calculateLowThumbPos) {
     					setThumbPos(i, calculateLowThumbPos);
     				}else {
    					setThumbPos(i, lowLimit +thumbHalf);
    				}
    			} else if (i==1) {//右边滑块
    				float calculateHighThumbPos = calculateDefaultThumbPos(1);
    				if (calculateHighThumbPos > 0) {
    					setThumbPos(i, calculateHighThumbPos);
    				} else {
    					setThumbPos(i, pixelRangeMax);
    				}
    			}
    		}
    	}
    }
    
  /*  public float getThumbValue(int index) {
    	return thumbs.get(index).yPosition;
    }
    
    public void setThumbValue(int index, float value) {
    	thumbs.get(index).yPosition = value;
    	calculateThumbPos(index);
		invalidate();
    }*/
    
    private void setThumbPos(int index, float pos) {
    	thumbs.get(index).xPosition = pos;
    	calculateThumbValue(index);
		invalidate();
    }

	private int getClosestThumb(float coordinate) {
		int closest = 0;
		if(!thumbs.isEmpty()) {
			float shortestDistance = pixelRangeMax+thumbHalf+((getPaddingLeft() + getPaddingRight()));
			for(int i = 0; i < thumbs.size(); i++) {
				float tcoordinate = thumbs.get(i).xPosition;
				float distance = Math.abs(coordinate-tcoordinate);
				if(distance <= shortestDistance) {
					shortestDistance = distance;
					closest = i;
				}
	    	}
		}
		return closest;
	}
	
	 
	 private void drawSeekBarBg(Canvas canvas) {
	    	if(seekbarBg != null) {
	    		Rect area1 = new Rect();
	            area1.left = getPaddingLeft();
	            area1.top =  mSeekBarCenterY - seekbarBg.getIntrinsicHeight()/2;
	            area1.right = getMeasuredWidth();
	            area1.bottom = mSeekBarCenterY + seekbarBg.getIntrinsicHeight()/2;
	            seekbarBg.setBounds(area1);
	            seekbarBg.draw(canvas);
	    	}
	    }
	 
    private void drawGutter(Canvas canvas) {
    	if(track != null) {
    		Rect area1 = new Rect();
            area1.left = getPaddingLeft();
            area1.top =  mSeekBarCenterY - track.getIntrinsicHeight()/2;
            area1.right = getMeasuredWidth();
            area1.bottom = mSeekBarCenterY + track.getIntrinsicHeight()/2;
    		track.setBounds(area1);
    		track.draw(canvas);
    	}
    }
    
    private void drawRange(Canvas canvas) {
    	if(!thumbs.isEmpty()){
	    	Thumb thLow = thumbs.get(getClosestThumb(0));//左边滑块
	    	Thumb thHigh = thumbs.get(getClosestThumb(pixelRangeMax));//右边滑块
	    	// If we only have 1 thumb - choose to draw from 0 in scale
	    	if(thumbs.size() == 1) {
	    		thLow = new Thumb();
	    	}
	    	if(range != null) {
	    		Rect rect = new Rect();
	    		rect.left = (int) thLow.xPosition;
	    		rect.top =  (int) (mSeekBarCenterY - mRangeHeight/2);
	    		rect.right = (int) thHigh.xPosition;
		        rect.bottom = (int) (mSeekBarCenterY + mRangeHeight/2);
	    		range.setBounds(rect);
	    		range.draw(canvas);
	    	}
    	}
    }
    
    private void drawThumbs(Canvas canvas) {
    	if(!thumbs.isEmpty()) {
    		for(Thumb th : thumbs) {
    			int left = (int) ((th.xPosition - thumbHalf) + getPaddingLeft());
    			String currTime = getCurrTime(left);
	    		Rect timeRect = new Rect();
		    	mPaint.getTextBounds(currTime, 0, currTime.length(), timeRect);
		    	mDrawTextHeight = timeRect.height();
		    	int timeTop = mValueTextMarginTop + mDrawTextHeight;
	    		canvas.drawText(currTime , left+5, timeTop, mPaint);
	    		th.time = currTime;
    			Rect area1 = new Rect();
	    		area1.left = left;
    	        area1.top =  mSeekBarCenterY - thumb.getIntrinsicHeight()/2;
    	        area1.right = (int) ((th.xPosition + thumbHalf) - getPaddingRight());
    	        area1.bottom = mSeekBarCenterY + thumb.getIntrinsicHeight()/2;
	    		thumb.setBounds(area1);
	    		thumb.draw(canvas);
	    		if (!isSliping && !isInitState && listeners != null && listeners.size() > 0) {
					for (OnCustomSeekBarChangeListener listener : listeners) {
						listener.onProgressChanged(this, currentThumb);
					}
				}
        	}
    	}
    }
    
    private String getCurrTime(int progress) {
    	String currTime = "";
    	if (mDisplayTimeMode == HOUR_TIME_MODE) {
    		currTime = getHourValue(progress);
    	} else if(mDisplayTimeMode == MINUTE_TIME_MODE) {
    		currTime = getMinuteValue(progress);
    	} else if (mDisplayTimeMode == SECONDS_TIME_MODE) {
    		currTime = getSecondsValue(progress);
    	}
		return currTime; 
    }

    
	private float calculateDefaultThumbPos(int thumbIndex) {
    	float progress = -1;
    	if (mDisplayTimeMode == HOUR_TIME_MODE) {
    		if (thumbIndex == 0) {
    			if (mDefaultStartTime != -1) {
    				progress = ((float)(mDefaultStartTime - mMinTime * 60) / ((mMaxTime - mMinTime) *60)) * mTrackLength + thumbHalf - getPaddingLeft();
    			}
    		} else if (thumbIndex == 1) {
    			if (mDefaultEndTime != -1) {
    				progress = ((float)(mDefaultEndTime - mMinTime * 60) / ((mMaxTime - mMinTime) *60)) * mTrackLength + thumbHalf - getPaddingLeft();
    			}
    		}
    	} else if(mDisplayTimeMode == MINUTE_TIME_MODE 
    			|| mDisplayTimeMode == SECONDS_TIME_MODE) {
    		if (thumbIndex == 0 && mDefaultStartTime != -1) {
    			progress = ((float)(mDefaultStartTime - mMinTime) / (mMaxTime - mMinTime)) * mTrackLength + thumbHalf - getPaddingLeft();
    		}
    	}
    	return progress;
    }
    
    private String getSecondsValue(int progress) {
    	int value = (int) (((float)progress/mTrackLength) * (mMaxTime - mMinTime) + mMinTime);
    	return value +mSecondsStr;
    }
    
    private String getMinuteValue(int progress) {
    	int value = (int) (((float)progress/mTrackLength) * (mMaxTime - mMinTime) + mMinTime);
    	return value +mMinuteStr;
    }
    
    private String getHourValue(int progress) {
    	int value = (int) (((float)progress/mTrackLength) * (mMaxTime - mMinTime) * 60 + mMinTime * 60);
		int hour  = value / 60;
		int minutes = value % 60;
		String hourStr;
		String minuteStr;
		if(hour < 10){
			hourStr = "0"+hour;
		}else{
			hourStr = hour+"";
		}
		if(minutes < 10){
			minuteStr = "0"+minutes;
		}else{
			minuteStr = minutes+"";
		}
		return hourStr+":"+minuteStr;
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
                int h = (int) (mThumbHeight+ getPaddingTop() + getPaddingBottom());
                result = Math.min(result, h);
            }
        }
        
        return result;
    }
    
    public class Thumb {
    	public float yPosition;
    	public float xPosition;
    	public String time;
    	public Thumb() {
    		yPosition = 0;
    		xPosition = 0;
    		time = "";
    	}
    }
    
    public void setOnCustomSeekBarChangeListener(OnCustomSeekBarChangeListener listener) {
		listeners.add(listener);
	}


	public void setTrack(Drawable track) {
		this.track = track;
	}

	public void setRange(Drawable range) {
		this.range = range;
	}


	public void setThumb(Drawable thumb) {
		this.thumb = thumb;
	}

	public void setValueTextMarginTop(int valueTextMarginTop) {
		this.mValueTextMarginTop = valueTextMarginTop;
	}

	public void setPaint(Paint paint) {
		this.mPaint = paint;
		Rect rect = new Rect();
    	mPaint.getTextBounds(TEST_TEXT, 0, TEST_TEXT.length(), rect);
    	mDrawTextHeight = rect.height();
	}

	public Paint getPaint() {
		return mPaint;
	}
	
	public void setThumbCount(int thumbCount) {
		this.mThumbCount = thumbCount;
		if (thumbs != null && !thumbs.isEmpty()) {
			thumbs.clear();
		}
		for (int i = 0; i < mThumbCount; i++) {
			Thumb th = new Thumb();
			thumbs.add(th);
		}
	}
	
	public void setProgressValues(int min, int max, int mode) {
		mMinTime = min;
		mMaxTime = max;
		mDisplayTimeMode =mode;
	}

	public int getDisplayTimeMode() {
		return mDisplayTimeMode;
	}

	public List<Thumb> getThumbs() {
		return thumbs;
	}

	public void setDefaultStartTime(int defaultStartTime) {
		if (defaultStartTime != mMinTime) {
			this.mDefaultStartTime = defaultStartTime +1;
		}
	}

	public void setDefaultEndTime(int defaultEndTime) {
		if (defaultEndTime != mMaxTime) {
			this.mDefaultEndTime = defaultEndTime +1;
		}
	}
	
	
}
