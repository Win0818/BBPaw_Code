package com.worldchip.bbp.ect.activity;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.util.AlarmUtil;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.view.DigitalClock;
import com.worldchip.bbp.ect.view.MultiDirectionSlidingDrawer;
import com.worldchip.bbp.ect.view.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.worldchip.bbp.ect.view.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.worldchip.bbp.ect.view.MyDialog;
import com.worldchip.bbp.ect.view.MyDialog.onCheckListener;

public class ClockActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnLongClickListener {

	private static final String TAG = "--ClockActivity--";
	private MultiDirectionSlidingDrawer mDirectionSlidingDrawer;
	private ImageView mHandle;
	private RelativeLayout mClockToolTip;
	private int cout = 0;

	private ImageView mClockTop, mDigitalClock, mSettingUp, mAlarmClock,
			mClockBack;
	private RelativeLayout mClockContent;

	private RelativeLayout mClockTopMain;
	private Handler mClockTopHandler;
	private boolean state = false;
	private boolean flagClockTop = false;
	private boolean flagDigitalClock = false;
	private MinuteCountDown mc = new MinuteCountDown(300 * 1000, 1000);
	private DigitalClock mClock;
	private RelativeLayout mDigitalClockMain;
	private Handler mDigitalDlockHandler;
	private WakeLock wakeLock;

	private CheckBox[] mClcokAlarmUse;
	private TextView[] mClockAlarmTime;
	private ImageView[] mClockAlarmDel;
	private List<AlarmInfo> listAlarm = null;
	private boolean isFirstShowAlarm = true;
	private static final int CLOCK_STATE = 0;
	private static final int CLOCK_CLOSE = 1;
    private HashMap<Integer, Integer> mSoundMap;
    private SoundPool mSoundpool;
    private int mCurrUIIndex = -1;
    private static final int CLOCK_INDEX = 0;
    private static final int ELE_CLOCK_INDEX = 1;
    private static final int ALARM_INDEX = 2;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOCK_STATE:
				mHandler.postDelayed(runnable, 1000);
				break;
			case CLOCK_CLOSE:
				mDirectionSlidingDrawer.close();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			cout++;
			if (cout <= 60) {
				mHandler.sendEmptyMessage(CLOCK_STATE);
			} else {
				mHandler.sendEmptyMessage(CLOCK_CLOSE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock_main);

		initView();
		mHandler.sendEmptyMessage(CLOCK_STATE);
	}

	private void initView() {
		mDirectionSlidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
		mHandle = (ImageView) findViewById(R.id.handle);
		mDirectionSlidingDrawer
				.setOnDrawerOpenListener(new OnDrawerOpenListener() {
					@Override
					public void onDrawerOpened() {
						mHandle.setImageResource(R.drawable.draw_left);
					}
				});
		mDirectionSlidingDrawer
				.setOnDrawerCloseListener(new OnDrawerCloseListener() {
					@Override
					public void onDrawerClosed() {
						mHandle.setImageResource(R.drawable.draw_right);
					}
				});

		mClockTop = (ImageView) findViewById(R.id.clocktop);
		mDigitalClock = (ImageView) findViewById(R.id.digitalclock);
		mSettingUp = (ImageView) findViewById(R.id.settingup); 
		mAlarmClock = (ImageView) findViewById(R.id.alarmclock);
		mClockBack = (ImageView) findViewById(R.id.clockback); 

		mClockToolTip = (RelativeLayout) findViewById(R.id.clock_tooltip);
		mClockToolTip.setVisibility(View.GONE);

		mClockTop.setOnClickListener(this);
		mDigitalClock.setOnClickListener(this);
		mSettingUp.setOnClickListener(this);
		mAlarmClock.setOnClickListener(this);
		mClockBack.setOnClickListener(this);
         
		mSoundpool=new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap=new HashMap<Integer, Integer>();
		mSoundMap.put(1, mSoundpool.load(ClockActivity.this, R.raw.click, 1));
		mClockContent = (RelativeLayout) findViewById(R.id.clockcontent);
		LayoutInflater inflater = LayoutInflater.from(ClockActivity.this);
		View view = inflater.inflate(R.layout.clock_top, null);
		mClockContent.addView(view);
		initViewClockTop(view);
	}

	private void initViewClockTop(View view) {
		cout = 0;
		flagClockTop = true;
		if (flagDigitalClock) {
			getApplicationContext().unregisterReceiver(mDigitalDlockReceiver);
		}
		flagDigitalClock = false;
		getApplicationContext().registerReceiver(mClockTopReceiver,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		state = true;
		mClockTopMain = (RelativeLayout) view.findViewById(R.id.clocktop_main);
		mClockTopHandler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (HttpCommon.isClockNight(ClockActivity.this)) {
					mClockTopMain
							.setBackgroundResource(R.drawable.clocktop_night);
				} else {
					mClockTopMain
							.setBackgroundResource(R.drawable.clocktop_daytime);
				}
				mClockTopHandler.postDelayed(this, 2000);
			}
		};
		mClockTopHandler.post(runnable);
	}

	private void initViewDigitalDlock(View view) {
		cout = 0;
		flagDigitalClock = true;
		if (flagClockTop) {
			getApplicationContext().unregisterReceiver(mClockTopReceiver);
		}
		flagClockTop = false;
		getApplicationContext().registerReceiver(mDigitalDlockReceiver,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		state = true;
		mDigitalClockMain = (RelativeLayout) view
				.findViewById(R.id.digitalclock_main);
		mClock = (DigitalClock) findViewById(R.id.customdigitalclock);
		mClock.start();
		mDigitalDlockHandler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (HttpCommon.isClockNight(ClockActivity.this)) {
					mDigitalClockMain
							.setBackgroundResource(R.drawable.digital_clock_night);
				} else {
					mDigitalClockMain
							.setBackgroundResource(R.drawable.digital_clock_daytime);
				}
				mDigitalDlockHandler.postDelayed(this, 2000);

			}
		};
		mDigitalDlockHandler.post(runnable);
	}

	private void initViewAlarm(View view) {
		mClcokAlarmUse = new CheckBox[5];
		mClockAlarmTime = new TextView[5];
		mClockAlarmDel = new ImageView[5];

		mClcokAlarmUse[0] = (CheckBox) view
				.findViewById(R.id.clcok_alarm_use_00);
		mClcokAlarmUse[1] = (CheckBox) view
				.findViewById(R.id.clcok_alarm_use_01);
		mClcokAlarmUse[2] = (CheckBox) view
				.findViewById(R.id.clcok_alarm_use_02);
		mClcokAlarmUse[3] = (CheckBox) view
				.findViewById(R.id.clcok_alarm_use_03);
		mClcokAlarmUse[4] = (CheckBox) view
				.findViewById(R.id.clcok_alarm_use_04);

		mClockAlarmTime[0] = (TextView) view
				.findViewById(R.id.clock_alarm_time_00);
		mClockAlarmTime[1] = (TextView) view
				.findViewById(R.id.clock_alarm_time_01);
		mClockAlarmTime[2] = (TextView) view
				.findViewById(R.id.clock_alarm_time_02);
		mClockAlarmTime[3] = (TextView) view
				.findViewById(R.id.clock_alarm_time_03);
		mClockAlarmTime[4] = (TextView) view
				.findViewById(R.id.clock_alarm_time_04);

		mClockAlarmDel[0] = (ImageView) view
				.findViewById(R.id.clock_alarm_del_00);
		mClockAlarmDel[1] = (ImageView) view
				.findViewById(R.id.clock_alarm_del_01);
		mClockAlarmDel[2] = (ImageView) view
				.findViewById(R.id.clock_alarm_del_02);
		mClockAlarmDel[3] = (ImageView) view
				.findViewById(R.id.clock_alarm_del_03);
		mClockAlarmDel[4] = (ImageView) view
				.findViewById(R.id.clock_alarm_del_04);

		initListener();
		setInitData();
	}

	private void initListener() {
		for (int i = 0; i < 5; i++) {
			//mClcokAlarmUse[i].setOnCheckedChangeListener(this);
			mClcokAlarmUse[i].setTag(i);
			mClcokAlarmUse[i].setOnClickListener(this);
			mClockAlarmTime[i].setOnClickListener(this);
			mClockAlarmTime[i].setOnLongClickListener(this);
			mClockAlarmDel[i].setOnClickListener(this);
		}
	}

	private void addClockAlarm(int index) {
		Intent intent = new Intent();
		intent.setClass(this, AlarmActivity.class);
		if (listAlarm != null) {
			int size = listAlarm.size();
			if (size > index) {
				AlarmInfo alarm = listAlarm.get(index);
				Bundle bundle = new Bundle();
				bundle.putInt("_id", alarm.getId());
				bundle.putInt("eable", alarm.getEnabled());
				intent.putExtras(bundle);
			}
		}
		startActivityForResult(intent, 500);
	}

	private void delClockAlarm(int id, int index) {
		MyDialog myDialog = new MyDialog(ClockActivity.this,
				new MyCheckListener(id, index));
		myDialog.show();
		myDialog.setCancelable(false);
	}

	private class MyCheckListener implements onCheckListener {
		private int id;
		private int index;

		public MyCheckListener(int id, int index) {
			this.id = id;
			this.index = index;
		}

		@Override
		public void onYes() {
			// TODO Auto-generated method stub
			stopClock(id);
			ClockData.delClockById(ClockActivity.this, id);
			Log.e(TAG, "onYes is running");
			mAlarmClock.performClick();
		}

		@Override
		public void onNo() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onNo is running");
			mClockAlarmDel[index].setVisibility(View.INVISIBLE);
		}

	}

	private void stopClock(int id) {
		AlarmInfo alarm = ClockData.getByAlarmInfo(ClockActivity.this, id);
		AlarmUtil.cancleAlarm(MyApplication.getAppContext(), alarm);
	}

	private void setInitData() {
		listAlarm = ClockData.getAllClockAlarm(this);
		int i = 0;
		for (AlarmInfo alarm : listAlarm) {
			if (alarm.getEnabled() == 1) {
				mClcokAlarmUse[i].setChecked(true);
				mClcokAlarmUse[i].setBackgroundResource(R.drawable.clock_use_on);
				mClockAlarmTime[i].setBackgroundResource(R.drawable.clock_item_bg_start);
			} else {
				mClcokAlarmUse[i].setBackgroundResource(R.drawable.clock_use);
				mClockAlarmTime[i].setBackgroundResource(R.drawable.clock_item_bg);
			}
			mClockAlarmTime[i].setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.clock_alarm_time, 0, 0, 0);
			int hours = alarm.getHours();
			int musutes = alarm.getMusutes();
			String time = "";
			if (hours > 0 && hours < 24) {
				if (hours > 0 && hours < 10) {
					time = time + 0 + hours + " : ";
				} else {
					time = time + hours + " : ";
				}
			} else {
				time = time + "00" + " : ";
			}
			if (musutes > 0 && musutes < 60) {
				if (musutes > 0 && musutes < 10) {
					time = time + 0 + musutes;
				} else {
					time = time + musutes;
				}
			} else {
				time = time + "00";
			}
			mClockAlarmTime[i].setText(time);
			i++;
		}
	}

	@Override
	public void onClick(View v) {
		LayoutInflater inflater = LayoutInflater.from(ClockActivity.this);
		View view = null;
		mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
		try {
			switch (v.getId()) {
			case R.id.clocktop: // ָ��ʱ��
				mCurrUIIndex = CLOCK_INDEX;
				if (mClock != null) {
					mClock.stop();
				}
				mClockTop.setBackgroundResource(R.drawable.clcok_select);
				mDigitalClock.setBackgroundColor(Color.parseColor("#00000000"));
				mSettingUp.setBackgroundColor(Color.parseColor("#00000000"));
				mAlarmClock
						.setBackgroundResource(Color.parseColor("#00000000"));
				mClockBack.setBackgroundResource(Color.parseColor("#00000000"));
				view = inflater.inflate(R.layout.clock_top, null);
				mClockContent.removeAllViews();
				mClockContent.addView(view);
				initViewClockTop(view);
				mc.cancel();
				break;
			case R.id.digitalclock: 
				mCurrUIIndex = ELE_CLOCK_INDEX;
				if (mClock != null) {
					mClock.stop();
				}
				mClockTop.setBackgroundColor(Color.parseColor("#00000000"));
				mDigitalClock.setBackgroundResource(R.drawable.clcok_select);
				mSettingUp.setBackgroundColor(Color.parseColor("#00000000"));
				mAlarmClock
						.setBackgroundResource(Color.parseColor("#00000000"));
				mClockBack.setBackgroundResource(Color.parseColor("#00000000"));
				view = inflater.inflate(R.layout.digital_clock, null);
				mClockContent.removeAllViews();
				mClockContent.addView(view);
				initViewDigitalDlock(view);
				mc.cancel();
				break;
			case R.id.alarmclock: // ����
				mCurrUIIndex = ALARM_INDEX;
				if (mClock != null) {
					mClock.stop();
				}
				mClockTop.setBackgroundColor(Color.parseColor("#00000000"));
				mDigitalClock.setBackgroundColor(Color.parseColor("#00000000"));
				mSettingUp.setBackgroundColor(Color.parseColor("#00000000"));
				mAlarmClock.setBackgroundResource(R.drawable.clcok_select);
				mClockBack.setBackgroundResource(Color.parseColor("#00000000"));
				view = inflater.inflate(R.layout.clock_list, null);
				mClockContent.removeAllViews();
				mClockContent.addView(view);
				initViewAlarm(view);
				mc.cancel();
				break;
			case R.id.clockback: 
				if (mClock != null) {
					mClock.stop();
				}
				mClockTop.setBackgroundColor(Color.parseColor("#00000000"));
				mDigitalClock.setBackgroundColor(Color.parseColor("#00000000"));
				mSettingUp.setBackgroundColor(Color.parseColor("#00000000"));
				mAlarmClock.setBackgroundColor(Color.parseColor("#00000000"));
				mClockBack.setBackgroundResource(R.drawable.clcok_select);
				mc.cancel();
				finish();
				break;
			case R.id.clock_alarm_time_00:
				addClockAlarm(0);
				break;
			case R.id.clock_alarm_time_01:
				addClockAlarm(1);
				break;
			case R.id.clock_alarm_time_02:
				addClockAlarm(2);
				break;
			case R.id.clock_alarm_time_03:
				addClockAlarm(3);
				break;
			case R.id.clock_alarm_time_04:
				addClockAlarm(4);
				break;
			case R.id.clock_alarm_del_00:
				delClockAlarm(listAlarm.get(0).getId(), 0);
				break;
			case R.id.clock_alarm_del_01:
				delClockAlarm(listAlarm.get(1).getId(), 1);
				break;
			case R.id.clock_alarm_del_02:
				delClockAlarm(listAlarm.get(2).getId(), 2);
				break;
			case R.id.clock_alarm_del_03:
				delClockAlarm(listAlarm.get(3).getId(), 3);
				break;
			case R.id.clock_alarm_del_04:
				delClockAlarm(listAlarm.get(4).getId(), 4);
				break;
			case R.id.clcok_alarm_use_00:
			case R.id.clcok_alarm_use_01:
			case R.id.clcok_alarm_use_02:
			case R.id.clcok_alarm_use_03:
			case R.id.clcok_alarm_use_04:
				CheckBox checkBox = (CheckBox)v;
				OpenAndCloseClockAlarm(checkBox.isChecked(), (Integer)v.getTag());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver mClockTopReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action))
				;
			{
				int status = intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);
				if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
					if (mClockTopMain != null && state
							|| mDigitalClockMain != null && state) {
						state = false;
						//showToolTip();
						mc.cancel();
						mc.start();
						if (wakeLock == null) {
							PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
							wakeLock = pm.newWakeLock(
									PowerManager.PARTIAL_WAKE_LOCK, this
											.getClass().getCanonicalName());
							wakeLock.acquire();
						}
					}
				}
			}
		}
	};

	private BroadcastReceiver mDigitalDlockReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action))
				;
			{
				int status = intent.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);
				if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
					if (mClockTopMain != null && state
							|| mDigitalClockMain != null && state) {
						state = false;
						//showToolTip();
						mc.cancel();
						mc.start();
						if (wakeLock == null) {
							PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
							wakeLock = pm.newWakeLock(
									PowerManager.PARTIAL_WAKE_LOCK, this
											.getClass().getCanonicalName());
							wakeLock.acquire();
						}
					}
				}
			}
		}
	};

	class MinuteCountDown extends CountDownTimer {
		public MinuteCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			state = false;
			if (mClockTopMain != null) {
				try {
					getApplicationContext().unregisterReceiver(
							mClockTopReceiver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mDigitalClockMain != null) {
				try {
					getApplicationContext().unregisterReceiver(
							mDigitalDlockReceiver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (wakeLock != null && wakeLock.isHeld()) {
				wakeLock.release();
				wakeLock = null;
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int sum_millisUntilFinished = (int) millisUntilFinished;
			int second = sum_millisUntilFinished / 1000;
			int result = second % 60;
			if (result == 0) {
				//showToolTip();
			}
		}
	}

	private void showToolTip() {
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation1.setDuration(500);
		alphaAnimation1.setRepeatCount(Animation.INFINITE);
		alphaAnimation1.setRepeatMode(Animation.REVERSE);
		mClockToolTip.setAnimation(alphaAnimation1);
		alphaAnimation1.start();
		mClockToolTip.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mClockToolTip.clearAnimation();
				mClockToolTip.setVisibility(View.GONE);
			}
		}, 3000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mClock != null) {
			mClock.start();
		}
		if (mCurrUIIndex == ALARM_INDEX) {
			setInitData();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mClock != null) {
			mClock.stop();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cout = 0;
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mHandler.sendEmptyMessage(CLOCK_STATE);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setInitData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onLongClick(View view) {
		int size = listAlarm.size();
		switch (view.getId()) {
		case R.id.clock_alarm_time_00:
			if (size > 0) {
				mClockAlarmDel[0].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.clock_alarm_time_01:
			if (size > 1) {
				mClockAlarmDel[1].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.clock_alarm_time_02:
			if (size > 2) {
				mClockAlarmDel[2].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.clock_alarm_time_03:
			if (size > 3) {
				mClockAlarmDel[3].setVisibility(View.VISIBLE);
			}
			break;
		case R.id.clock_alarm_time_04:
			if (size > 4) {
				mClockAlarmDel[4].setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		if (!isFirstShowAlarm) {
			switch (button.getId()) {
			case R.id.clcok_alarm_use_00:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				OpenAndCloseClockAlarm(isChecked, 0);
				break;
			case R.id.clcok_alarm_use_01:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				OpenAndCloseClockAlarm(isChecked, 1);
				break;
			case R.id.clcok_alarm_use_02:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				OpenAndCloseClockAlarm(isChecked, 2);
				break;
			case R.id.clcok_alarm_use_03:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				OpenAndCloseClockAlarm(isChecked, 3);
				break;
			case R.id.clcok_alarm_use_04:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				OpenAndCloseClockAlarm(isChecked, 4);
				break;
			default:
				break;
			}
		} else {
			isFirstShowAlarm = false;
		}
	}

	private void OpenAndCloseClockAlarm(boolean isChecked, int index) {
		Log.e(TAG, "onCheckedChanged  index == " + index + " isChecked = " + isChecked);
		if (listAlarm != null) {
			int size = listAlarm.size();
			if (size > index) {
				AlarmInfo alarm = listAlarm.get(index);
				ClockData.enableAlarm(ClockActivity.this, alarm.getId(), isChecked);
				if (isChecked) {
					mClcokAlarmUse[index]
							.setBackgroundResource(R.drawable.clock_use_on);
					mClockAlarmTime[index]
							.setBackgroundResource(R.drawable.clock_item_bg_start);
					AlarmUtil.createAlarm(MyApplication.getAppContext(), alarm);
				} else {
					mClcokAlarmUse[index]
							.setBackgroundResource(R.drawable.clock_use);
					mClockAlarmTime[index]
							.setBackgroundResource(R.drawable.clock_item_bg);
					AlarmUtil.cancleAlarm(MyApplication.getAppContext(), alarm);
				}
			}
		}
	}

}