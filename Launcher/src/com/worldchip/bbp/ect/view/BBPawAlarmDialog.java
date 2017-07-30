package com.worldchip.bbp.ect.view;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.util.AlarmPlayer;
import com.worldchip.bbp.ect.util.Utils;

public class BBPawAlarmDialog extends Dialog implements android.view.View.OnClickListener {
	
	private Context mContext = null;
	private static BBPawAlarmDialog mAlarmDialog = null;
	private static final String TAG = "--PasswordInputDialog--";
	private static final int DISMISS_DIALOG = 100;
	private static final int ALARM_DIALOG_DISMISS_DELAY = 1000 * 60 * 10;
	private AlarmInfo mAlarmInfo = null;
	private ImageView mImgHour1, mImgHour2, mImgMinute1, mImgMinute2,mMaohao;
	private ImageButton mImgBtnCancel;
	AudioManager  mAudioManager ; 
	
	public interface PasswordValidateListener {
		public void onValidateComplete(boolean success);
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case DISMISS_DIALOG:
				Log.e("lee", "handleMessage  DISMISS_DIALOG ------ >>>");
				dismissAlarmDialog();
				break;

			default:
				break;
			}
		}
		
	};
	
	
	public BBPawAlarmDialog(Context context){
		super(context);
		mContext = context;
	}
	
	public BBPawAlarmDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        
    }
	
	public static BBPawAlarmDialog createDialog(Context context){
		mAlarmDialog = new BBPawAlarmDialog(context,R.style.Dialog_global);
		mAlarmDialog.setContentView(R.layout.alarm_come_activity);
		Window dialogWindow = mAlarmDialog.getWindow();
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = Utils.getScreenWidth(MyApplication.getAppContext());
        layoutParams.height = Utils.getScreenHeight(MyApplication.getAppContext());
        Log.e("lee", "width == "+Utils.getScreenWidth(MyApplication.getAppContext()) +" height == "+Utils.getScreenHeight(MyApplication.getAppContext()));
        mAlarmDialog.setCancelable(false);
		return mAlarmDialog;
	}
 

	public void setAlarmInfo(AlarmInfo alarmInfo) {
		mAlarmInfo = alarmInfo;
	}
	
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			 if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				 playAlarmRingtone(MyApplication.getAppContext());
				 Log.d("BBPawAlarmDialog", "-----------afChangeListener------------->");
			 }
		}
	};
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		initView();
	}

	private void initView() {
		if (mAlarmDialog == null){
    		return;
    	}
		mImgHour1 = (ImageView) mAlarmDialog.findViewById(R.id.iv_hour1);
		mImgHour2 = (ImageView) mAlarmDialog.findViewById(R.id.iv_hour2);
		mMaohao=(ImageView)mAlarmDialog.findViewById(R.id.clock_maohao_imageview);
		mImgMinute1 = (ImageView) mAlarmDialog.findViewById(R.id.iv_minute1);
		mImgMinute2 = (ImageView) mAlarmDialog.findViewById(R.id.iv_minute2);
		mImgBtnCancel = (ImageButton) mAlarmDialog.findViewById(R.id.imgbtn_cancel);
		mImgBtnCancel.setOnClickListener(this);
		intAlarmTime();
	}
    
	@SuppressLint("SimpleDateFormat")
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(new Date());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismissAlarmDialog();
            return true;
        }
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.imgbtn_cancel:
				Log.e("lee", "onClick  dismissDialog ------ >>>");
				dismissAlarmDialog();
				break;
		}
	}
	
	public void showAlarmDialog() {
		if(mAlarmDialog != null) {
			mAlarmDialog.show();
			//playAlarmRingtone(MyApplication.getAppContext());
			int result = mAudioManager.requestAudioFocus(afChangeListener,
				    // Use the music stream.
					  AudioManager.STREAM_MUSIC,
				      AudioManager.AUDIOFOCUS_GAIN);
		    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
		    	Log.d("BBPawAlarmDialog", "-----------playAlarmRingtone------------->");
		    	playAlarmRingtone(MyApplication.getAppContext());
		     // Start playback. // 开始播放音乐文件
		    }
			mHandler.sendEmptyMessageDelayed(DISMISS_DIALOG, ALARM_DIALOG_DISMISS_DELAY);
			//mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}
	}
	
	public void dismissAlarmDialog() {
		Log.e("lee", "dismiss  alarm Dialog ------ >>>");
		mHandler.removeMessages(DISMISS_DIALOG);
		if(mAlarmDialog != null && mAlarmDialog.isShowing()) {
			Log.e("lee", "dismiss ------ >>>");
			mAlarmDialog.dismiss();
			mAlarmDialog = null;
		}
		mAudioManager.abandonAudioFocus(afChangeListener);
		//mAudioManager.setStreamSolo(AudioManager.STREAM_ALARM, false);
		//mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		stopAlarmRingtone();
	}
	
	public void playAlarmRingtone(Context context) {
		 Log.e("lee", "playAlarmRingtone ------------------------>>>>>>>");
		 if (mAlarmInfo != null) {
			 String alarmRingtoneUri = mAlarmInfo.getAlert();
			 AlarmPlayer.getInstance().playByUri(context, Uri.parse(alarmRingtoneUri),true);
		 }
	}
	
	private void stopAlarmRingtone() {
		AlarmPlayer.getInstance().stopPlay();
	}
	
	private void intAlarmTime() {
		//Calendar c = Calendar.getInstance();
		//int minute = c.get(Calendar.MINUTE);
		//int hour = c.get(Calendar.HOUR_OF_DAY);
		if(mAlarmInfo!=null) {
			showAlarmTime(mAlarmInfo.getHours(), mAlarmInfo.getMusutes());
		}
	}

	private void showAlarmTime(int hour, int minute) {
		switch (hour / 10) {
		case 2:
			mImgHour1.setImageResource(R.drawable.clock_number_2);
			break;
		case 1:
			mImgHour1.setImageResource(R.drawable.clock_number_1);
			break;
		case 0:
			mImgHour1.setImageResource(R.drawable.clock_number_0);
			break;

		default:
			break;
		}
		switch (hour % 10) {
		case 0:
			mImgHour2.setImageResource(R.drawable.clock_number_0);
			break;
		case 1:
			mImgHour2.setImageResource(R.drawable.clock_number_1);
			break;
		case 2:
			mImgHour2.setImageResource(R.drawable.clock_number_2);
			break;
		case 3:
			mImgHour2.setImageResource(R.drawable.clock_number_3);
			break;
		case 4:
			mImgHour2.setImageResource(R.drawable.clock_number_4);
			break;
		case 5:
			mImgHour2.setImageResource(R.drawable.clock_number_5);
			break;
		case 6:
			mImgHour2.setImageResource(R.drawable.clock_number_6);
			break;
		case 7:
			mImgHour2.setImageResource(R.drawable.clock_number_7);
			break;
		case 8:
			mImgHour2.setImageResource(R.drawable.clock_number_8);
			break;
		case 9:
			mImgHour2.setImageResource(R.drawable.clock_number_9);
			break;

		default:
			break;
		}
		switch (minute / 10) {
		case 0:
			mImgMinute1.setImageResource(R.drawable.clock_number_0);
			break;
		case 1:
			mImgMinute1.setImageResource(R.drawable.clock_number_1);
			break;
		case 2:
			mImgMinute1.setImageResource(R.drawable.clock_number_2);
			break;
		case 3:
			mImgMinute1.setImageResource(R.drawable.clock_number_3);
			break;
		case 4:
			mImgMinute1.setImageResource(R.drawable.clock_number_4);
			break;
		case 5:
			mImgMinute1.setImageResource(R.drawable.clock_number_5);
			break;
		case 6:
			mImgMinute1.setImageResource(R.drawable.clock_number_6);
			break;

		default:
			break;
		}
		switch (minute % 10) {
		case 0:
			mImgMinute2.setImageResource(R.drawable.clock_number_0);
			break;
		case 1:
			mImgMinute2.setImageResource(R.drawable.clock_number_1);
			break;
		case 2:
			mImgMinute2.setImageResource(R.drawable.clock_number_2);
			break;
		case 3:
			mImgMinute2.setImageResource(R.drawable.clock_number_3);
			break;
		case 4:
			mImgMinute2.setImageResource(R.drawable.clock_number_4);
			break;
		case 5:
			mImgMinute2.setImageResource(R.drawable.clock_number_5);
			break;
		case 6:
			mImgMinute2.setImageResource(R.drawable.clock_number_6);
			break;
		case 7:
			mImgMinute2.setImageResource(R.drawable.clock_number_7);
			break;
		case 8:
			mImgMinute2.setImageResource(R.drawable.clock_number_8);
			break;
		case 9:
			mImgMinute2.setImageResource(R.drawable.clock_number_9);
			break;

		default:
			break;
		}
	}
	
}