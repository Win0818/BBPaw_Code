package com.worldchip.bbp.ect.view;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.worldchip.bbp.ect.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DigitalClock extends LinearLayout {

	private TextView mHourLeft, mHourRight, mClockMaoHao, mMinuteLeft,
			mMinuteRight;
	private static final int REFRESH_DELAY = 500;
	private final Handler mHandler = new Handler();

	private final Runnable mTimeRefresher = new Runnable() {
		@Override
		public void run() {
			// TimeZone.getTimeZone("GMT+8")
			Calendar calendar = Calendar.getInstance();
			final Date d = new Date();
			calendar.setTime(d);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int hour_decade = hour / 10;
			int hour_units = hour % 10;

			switch (hour_decade) {
			case 0:
				mHourLeft.setBackgroundResource(R.drawable.clock_number_0);
				break;
			case 1:
				mHourLeft.setBackgroundResource(R.drawable.clock_number_1);
				break;
			case 2:
				mHourLeft.setBackgroundResource(R.drawable.clock_number_2);
				break;
			default:
				break;
			}

			switch (hour_units) {
			case 0:
				mHourRight.setBackgroundResource(R.drawable.clock_number_0);
				break;
			case 1:
				mHourRight.setBackgroundResource(R.drawable.clock_number_1);
				break;
			case 2:
				mHourRight.setBackgroundResource(R.drawable.clock_number_2);
				break;
			case 3:
				mHourRight.setBackgroundResource(R.drawable.clock_number_3);
				break;
			case 4:
				mHourRight.setBackgroundResource(R.drawable.clock_number_4);
				break;
			case 5:
				mHourRight.setBackgroundResource(R.drawable.clock_number_5);
				break;
			case 6:
				mHourRight.setBackgroundResource(R.drawable.clock_number_6);
				break;
			case 7:
				mHourRight.setBackgroundResource(R.drawable.clock_number_7);
				break;
			case 8:
				mHourRight.setBackgroundResource(R.drawable.clock_number_8);
				break;
			case 9:
				mHourRight.setBackgroundResource(R.drawable.clock_number_9);
				break;
			default:
				break;
			}

			int minute = calendar.get(Calendar.MINUTE);
			int minute_decade = minute / 10;
			int minute_units = minute % 10;

			switch (minute_decade) {
			case 0:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_0);
				break;
			case 1:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_1);
				break;
			case 2:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_2);
				break;
			case 3:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_3);
				break;
			case 4:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_4);
				break;
			case 5:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_5);
				break;
			case 6:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_6);
				break;
			case 7:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_7);
				break;
			case 8:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_8);
				break;
			case 9:
				mMinuteLeft.setBackgroundResource(R.drawable.clock_number_9);
				break;
			default:
				break;
			}

			switch (minute_units) {
			case 0:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_0);
				break;
			case 1:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_1);
				break;
			case 2:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_2);
				break;
			case 3:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_3);
				break;
			case 4:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_4);
				break;
			case 5:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_5);
				break;
			case 6:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_6);
				break;
			case 7:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_7);
				break;
			case 8:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_8);
				break;
			case 9:
				mMinuteRight.setBackgroundResource(R.drawable.clock_number_9);
				break;
			default:
				break;
			}
			mHandler.postDelayed(this, REFRESH_DELAY);
		}
	};

	@SuppressLint("NewApi")
	public DigitalClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DigitalClock(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.digital_clock_view, this);

		mHourLeft = (TextView) view.findViewById(R.id.hourleft);
		mHourRight = (TextView) view.findViewById(R.id.hourright);
		mClockMaoHao = (TextView) view.findViewById(R.id.clock_maohao);
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation1.setDuration(500);
		alphaAnimation1.setRepeatCount(Animation.INFINITE);
		alphaAnimation1.setRepeatMode(Animation.REVERSE);
		mClockMaoHao.setAnimation(alphaAnimation1);
		alphaAnimation1.start();
		mMinuteLeft = (TextView) view.findViewById(R.id.minuteleft);
		mMinuteRight = (TextView) view.findViewById(R.id.minuteright);
	}

	public void start() {
		mHandler.post(mTimeRefresher);
	}

	public void stop() {
		mHandler.removeCallbacks(mTimeRefresher);
	}
}