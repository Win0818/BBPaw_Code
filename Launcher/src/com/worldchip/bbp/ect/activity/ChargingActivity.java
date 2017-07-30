package com.worldchip.bbp.ect.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.util.Utils;

public class ChargingActivity extends Activity {

	protected static final String TAG = "--ChargingActivity--";
	private Context mCtx;
	private MediaPlayer mMediaPlayer;

	public static final int CHARGING = 0;
	public static final int CHARGING_ANIM = 1;

	private int mCurrentIndex;
	private int[] mIcons = new int[] { R.drawable.battery_2,
			R.drawable.battery_3, R.drawable.battery_4, R.drawable.battery_5 };

	protected int mStatus = BatteryManager.BATTERY_STATUS_FULL;
	private ImageView mImgView = null;
	private int keyBackClickCount = 0;
	//public static final int FLAG_HOMEKEY_DISPATCHED = 112; //129;//拦截 home按键 111
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHARGING_ANIM:
				startChargingAnim();				
				break;
			case CHARGING:
				// Log.e(TAG, "mHandler...mCurrentIndex="+mCurrentIndex);
				mCurrentIndex++;
				if (mCurrentIndex >= mIcons.length) {
					mCurrentIndex = 0;
				}
				startChargingAnim();
				mHandler.removeMessages(CHARGING);
				mHandler.sendEmptyMessageDelayed(CHARGING, 1000);
				break;
			}
		}
	};	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mCtx = ChargingActivity.this;
		// Common.hideSystemUI(mCtx, true);
		// Common.initViewFull(ChargingActivity.this);
		setContentView(R.layout.charge_layout);
		mImgView = (ImageView) findViewById(R.id.img_charge);
		//getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		registerMyReceiver();
		mCurrentIndex = 0;
		play();
		Log.e(TAG, "on create...");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.removeMessages(CHARGING_ANIM);
		mHandler.sendEmptyMessageDelayed(CHARGING_ANIM, 500);
		Log.e(TAG, "on onResume...");
	}

	private void startChargingAnim() {
		if (mStatus == BatteryManager.BATTERY_STATUS_FULL) {
			mImgView.setImageResource(R.drawable.battery_charge);
		} else {
			mImgView.setImageResource(mIcons[mCurrentIndex]);
		}
		mHandler.sendEmptyMessage(CHARGING);
	}

	private void play() {
		//mMediaPlayer = MediaPlayer.create(mCtx, R.raw.charging);
		if(Utils.getLanguageInfo(this) == Utils.CH_LANGUAGE_INDEX){
			mMediaPlayer = MediaPlayer.create(mCtx, R.raw.charging);
		} else {
			mMediaPlayer = MediaPlayer.create(mCtx, R.raw.gn_a_05_eng);
		}
		mMediaPlayer.start();
	}

	private void registerMyReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mPowerConnectReceiver, intentFilter);

		IntentFilter colseFilter = new IntentFilter();
		colseFilter.addAction(Utils.CLOSE_REST_WINDOW);
		registerReceiver(mCloseWindowReceiver, colseFilter);
	}

	private BroadcastReceiver mCloseWindowReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Utils.CLOSE_REST_WINDOW.equals(intent.getAction())) {
				Log.e(TAG, "mCloseWindowReceiver..close window");
				ChargingActivity.this.finish();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		case KeyEvent.KEYCODE_HOME:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDestroy() {
		super.onStop();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		} 

		if (mPowerConnectReceiver != null) {
			unregisterReceiver(mPowerConnectReceiver);
		}
		if (mCloseWindowReceiver != null) {
			unregisterReceiver(mCloseWindowReceiver);
		}
	}

	private BroadcastReceiver mPowerConnectReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

				mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				boolean isCharging = mStatus == BatteryManager.BATTERY_STATUS_CHARGING
						|| mStatus == BatteryManager.BATTERY_STATUS_FULL;

				Log.e(TAG, "mPowerConnectReceiver..isCharging=" + isCharging);
				int chargePlug = intent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, -1);
				boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
				boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

				Log.e(TAG, "isCharging=" + isCharging + "; usbCharge="
						+ usbCharge + "; acCharge=" + acCharge);
				if (!isCharging) {
					ChargingActivity.this.finish();
				}
			}
		}
	};
}
