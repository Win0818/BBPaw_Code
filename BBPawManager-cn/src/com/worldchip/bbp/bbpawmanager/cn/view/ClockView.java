package com.worldchip.bbp.bbpawmanager.cn.view;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


public class ClockView extends ImageView implements Runnable{
	
	private Calendar mCalendar = null;
	private float mHourRotateAngle = 0;
	private float mMinuteRotateAngle = 0;
	private float mSecondRotateAngle = 0;
	private Paint mClockPaint;
	private static final int HOUR_COLOR = Color.BLACK;
	private static final int MINUTE_COLOR = Color.BLACK;
	private static final int SECOND_COLOR = Color.RED;
	private static final float  HOUR_STROKE_WIDTH = 4.0f;
	private static final float  MINUTE_STROKE_WIDTH = 3.0f;
	private static final float  SECOND_STROKE_WIDTH = 2.0f;
	private static final float CENTER_CIRCLE_RADIUS = 5.0f;
	private boolean isRunning = true;
	
	public ClockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
		mCalendar = Calendar.getInstance();
		mClockPaint = new Paint();
		mClockPaint.setAntiAlias(true);
		mClockPaint.setStyle(Paint.Style.STROKE);
		new Thread(this).start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int centerX = getWidth()/2;
		int centerY = getHeight()/2;
		if (mClockPaint != null) {
			//画时针
			mClockPaint.setColor(HOUR_COLOR);
			mClockPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
			mClockPaint.setStyle(Paint.Style.STROKE);
			canvas.drawLine(centerX, centerY, (float) (centerX + (centerX * 0.6f) * Math.cos(Math.toRadians((mHourRotateAngle) - 90f))),
					(float) (centerY + (centerX * 0.5f) * Math.sin(Math.toRadians((mHourRotateAngle) - 90f))), mClockPaint);
			canvas.save();
			//画分针
			mClockPaint.setColor(MINUTE_COLOR);
			mClockPaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
			mClockPaint.setStyle(Paint.Style.STROKE);
			canvas.drawLine(centerX, centerY, (float) (centerX + (centerX * 0.9f) * Math.cos(Math.toRadians((mMinuteRotateAngle) - 90f))),
					(float) (centerY + (centerX * 0.6f) * Math.sin(Math.toRadians((mMinuteRotateAngle) - 90f))), mClockPaint);
			canvas.save();
			//画秒针
			mClockPaint.setColor(SECOND_COLOR);
			mClockPaint.setStrokeWidth(SECOND_STROKE_WIDTH);
			mClockPaint.setStyle(Paint.Style.STROKE);
			canvas.drawLine(centerX, centerY, (float) (centerX + (centerX * 0.65f) * Math.cos(Math.toRadians((mSecondRotateAngle) - 90f))),
					(float) (centerY + (centerX * 0.7f) * Math.sin(Math.toRadians((mSecondRotateAngle) - 90f))), mClockPaint);
			canvas.save();
			//画中心交叉点
			mClockPaint.setColor(HOUR_COLOR);
			mClockPaint.setStrokeWidth(2);
			mClockPaint.setStyle(Paint.Style.FILL);
	        canvas.drawCircle(centerX,centerX, CENTER_CIRCLE_RADIUS, mClockPaint);
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning && !Thread.currentThread().isInterrupted())
		{
			try
			{
				mCalendar.setTime(new Date());
				float hour = mCalendar.get(Calendar.HOUR_OF_DAY);
				float sec = mCalendar.get(Calendar.SECOND);
				float min = mCalendar.get(Calendar.MINUTE);
				mHourRotateAngle = (float)(hour / 12.0f * 360.0f);
				mMinuteRotateAngle = (float)(min / 60.0f * 360.0f);
				mSecondRotateAngle = (float)(sec / 60.0f * 360.0f);
			    Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			postInvalidate();
		}
	}

	public void stopClock() {
		isRunning = false;
	}
	
}
