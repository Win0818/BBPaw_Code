package com.worldchip.bbp.bbpawmanager.cn.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.OnCustomSeekBarChangeListener;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.view.CustomSeekBar.Thumb;
import com.worldchip.bbp.bbpawmanager.cn.view.SwitchButton.OnSwitchChangeListener;

@SuppressLint("NewApi")
public class ScreenTimePageFragment extends Fragment implements OnSwitchChangeListener{
	private static final String TAG = ScreenTimePageFragment.class
			.getSimpleName();
	
	private SwitchButton mSwicthButtom = null;
	private CustomSeekBar mUseTimeSeekbar = null;
	private CustomSeekBar mSleepTimeSeekbar = null;
	private CustomSeekBar mDayTimeSeekbar = null;
	private boolean mScreenTimeSwitch = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.screen_time_page_layout, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		
		mSwicthButtom = (SwitchButton)view.findViewById(R.id.screen_time_swicth_btn);
		mSwicthButtom.setOnSwitchChangeListener(this);
		
		mUseTimeSeekbar = (CustomSeekBar)view.findViewById(R.id.use_time_seekbar);
		mUseTimeSeekbar.setThumbCount(1);
		mUseTimeSeekbar.setProgressValues(5, 45,CustomSeekBar.MINUTE_TIME_MODE);
		mUseTimeSeekbar.setOnCustomSeekBarChangeListener(customSeekBarChangeListener);
		
		mSleepTimeSeekbar = (CustomSeekBar)view.findViewById(R.id.time_sleep_seekbar);
		mSleepTimeSeekbar.setThumbCount(1);
		mSleepTimeSeekbar.setProgressValues(5,45,CustomSeekBar.MINUTE_TIME_MODE);
		mSleepTimeSeekbar.setOnCustomSeekBarChangeListener(customSeekBarChangeListener);
		
		
		mDayTimeSeekbar = (CustomSeekBar)view.findViewById(R.id.time_day_seekbar);
		mDayTimeSeekbar.setThumbCount(2);
		mDayTimeSeekbar.setProgressValues(0,24,CustomSeekBar.HOUR_TIME_MODE);
		mDayTimeSeekbar.setOnCustomSeekBarChangeListener(customSeekBarChangeListener);
		initDatas();
	}

	private void initDatas() {
		String screenTimeSwitch = Common.getEyecareSettingsPreferences(Utils.EYECARE_SCREEN_TIME_PRE_KEY, "false");
		mScreenTimeSwitch = screenTimeSwitch.equals("false") ? false : true;
		mSwicthButtom.setOpened(mScreenTimeSwitch);
		
		String useTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_USE_TIME_PRE_KEY, "15");
		if (!TextUtils.isEmpty(useTime)) {
			mUseTimeSeekbar.setDefaultStartTime(Integer.parseInt(useTime));
		}
		
		String sleepTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_SLEEP_TIME_PRE_KEY, "15");
		if (!TextUtils.isEmpty(sleepTime)) {
			mSleepTimeSeekbar.setDefaultStartTime(Integer.parseInt(sleepTime));
		}
		
		
		String beginTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_BEGIN_PRE_KEY, "07:30");
		String endTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_END_PRE_KEY, "20:30");
		Log.e("lee", " beginTime == "+ beginTime +" endTime == "+endTime);
		if (!TextUtils.isEmpty(beginTime)) {
			mDayTimeSeekbar.setDefaultStartTime(formatDayTime(beginTime));
		}
		
		if (!TextUtils.isEmpty(endTime)) {
			mDayTimeSeekbar.setDefaultEndTime(formatDayTime(endTime));
		}
		
		
	}
	
	private int formatDayTime(String time) {
		if (!TextUtils.isEmpty(time)) {
			String[] timeSplit = time.split(":");
			return Integer.parseInt(timeSplit[0]) * 60 +Integer.parseInt(timeSplit[1]);
		}
		return -1;
	}
	
	@Override
	public void onChanged(SwitchButton button, boolean state) {
		// TODO Auto-generated method stub
		Log.e("lee", "SwitchButton onChanged state == "+state);
		if (mScreenTimeSwitch != state) {
			Common.saveEyecareSettingsPreferences(Utils.EYECARE_SCREEN_TIME_PRE_KEY, String.valueOf(state));
		}
	}

	
	OnCustomSeekBarChangeListener customSeekBarChangeListener = new OnCustomSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(CustomSeekBar seekbar) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onStartTrackingTouch(CustomSeekBar seekbar) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onProgressChanged(CustomSeekBar seekbar, int postion) {
			// TODO Auto-generated method stub
			if (seekbar == null)
				return;
			List<Thumb> thumbs = seekbar.getThumbs();
			switch (seekbar.getId()) {
			case R.id.use_time_seekbar:
				Thumb useTimeThumb = thumbs.get(postion);
				Log.e("lee", " use_time_seekbar time = "+useTimeThumb.time);
				String useTime = useTimeThumb.time;
				if (!TextUtils.isEmpty(useTime)) {
					String useTimeValue = useTime.trim().split(getResources().getString(R.string.minute_text))[0];
					Common.saveEyecareSettingsPreferences(Utils.EYECARE_USE_TIME_PRE_KEY, useTimeValue);
				}
				break;
			case R.id.time_sleep_seekbar:
				Thumb sleepTimeThumb = thumbs.get(postion);
				Log.e("lee", " time_sleep_seekbar time = " + sleepTimeThumb.time);
				String sleepTime = sleepTimeThumb.time;
				if (!TextUtils.isEmpty(sleepTime)) {
					String sleepTimeValue = sleepTime.trim().split(getResources().getString(R.string.minute_text))[0];
					Common.saveEyecareSettingsPreferences(Utils.EYECARE_SLEEP_TIME_PRE_KEY, sleepTimeValue);
				}
				
				break;
			case R.id.time_day_seekbar:
				Thumb startTimeThumb = thumbs.get(0);
				Thumb endTimeThumb = thumbs.get(thumbs.size()-1);
				Log.e("lee", " time_day_seekbar startTime = "+startTimeThumb.time +" end time = "+endTimeThumb.time);
				
				String startTime = startTimeThumb.time;
				if (!TextUtils.isEmpty(startTime)) {
					String startTimeValue = startTime.trim();
					Common.saveEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_BEGIN_PRE_KEY, startTimeValue);
				}
				
				String endTime = endTimeThumb.time;
				if (!TextUtils.isEmpty(endTime)) {
					String endTimeValue = endTime.trim();
					Common.saveEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_END_PRE_KEY, endTimeValue);
				}
				
				break;
			}
		}
	};


}
