package com.worldchip.bbp.bbpawmanager.cn.view;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;
import com.worldchip.bbp.bbpawmanager.cn.service.VaccineAlarmSevice;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.NotifyPlayer;
import com.worldchip.bbp.bbpawmanager.cn.utils.VaccineAlarmUtils;

public class VaccineAlarmDialog extends Dialog implements android.view.View.OnClickListener{
	private Context mContext = null;
	private static VaccineAlarmDialog mVaccineAlarmDialog = null;
	private ImageView mLogoImageView = null;
	private ImageView mClockImageView = null;
	private TextView mDateTextView = null;
	private TextView mVaccineContentTextView = null;
	private static final int ALERT_WIDTH = 1024;
	private static final int ALERT_HEIGHT = 600;
	private static final int DISMISS_DIALOG = 100;
	private static final int ALARM_DIALOG_DISMISS_DELAY = 1000 * 60;//提醒显示时间
	private ClockView mClockView = null;
	private LinkedList<VaccineInfo> mVaccineList = new LinkedList<VaccineInfo>();
	public interface PasswordValidateListener {
		public void onValidateComplete(boolean success);
	}
	
	// 锁屏、唤醒相关
	private KeyguardManager km;
	private KeyguardLock kl;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case DISMISS_DIALOG:
				Log.e("lee", "handleMessage  DISMISS_DIALOG ------ >>>");
				dismissVaccineAlarmDialog(false);
				break;

			default:
				break;
			}
		}
		
	};
	
	
	public VaccineAlarmDialog(Context context){
		super(context);
		mContext = context;
	}
	
	public VaccineAlarmDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }
	
	public static VaccineAlarmDialog createDialog(Context context){
		mVaccineAlarmDialog = new VaccineAlarmDialog(context,R.style.Dialog_global);
		mVaccineAlarmDialog.setContentView(R.layout.vaccine_alarm_dialog);
		Window dialogWindow = mVaccineAlarmDialog.getWindow();
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  
		WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = ALERT_WIDTH;
        layoutParams.height = ALERT_HEIGHT;
		mVaccineAlarmDialog.setCancelable(false);
		
		return mVaccineAlarmDialog;
	}
 
	public void setVaccine(VaccineInfo vaccine) {
		mVaccineList.add(vaccine);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismissVaccineAlarmDialog(true);
            return true;
        }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		initView();
	}

	private void initView() {
		if (mVaccineAlarmDialog == null){
    		return;
    	}
		mLogoImageView = (ImageView)mVaccineAlarmDialog.findViewById(R.id.vaccine_logo_iv);
		mLogoImageView.setOnClickListener(this);
		mClockImageView = (ImageView)mVaccineAlarmDialog.findViewById(R.id.vaccine_clock_iv);
		mDateTextView = (TextView)mVaccineAlarmDialog.findViewById(R.id.vaccine_date_tv);
		mDateTextView.setText(getDate());
		mVaccineContentTextView = (TextView)mVaccineAlarmDialog.findViewById(R.id.vaccine_name_tv);
		mVaccineContentTextView.setSelected(true);
		if (mVaccineContentTextView != null && mVaccineList != null && mVaccineList.size()>0) {
			VaccineInfo vaccineInfo = mVaccineList.getFirst();
			if (vaccineInfo != null) {
				mVaccineContentTextView.setText(mContext.getResources().getString(R.string.vaccine_alarm_text_format, vaccineInfo.getVaccineTypeName()));
			}
		}
		mClockView = (ClockView)findViewById(R.id.clock_view);
		if (mLogoImageView != null) {
			AnimationDrawable logoAnimationDrawable = (AnimationDrawable)mLogoImageView.getBackground();
			if (logoAnimationDrawable != null) {
				logoAnimationDrawable.start();
			}
		}
		if (mClockImageView != null) {
			AnimationDrawable clockAnimationDrawable = (AnimationDrawable)mClockImageView.getBackground();
			if (clockAnimationDrawable != null) {
				clockAnimationDrawable.start();
			}
		}
		MyApplication.getAppContext().registerReceiver(mAlarmDialogUpdateReceiver, new IntentFilter(VaccineAlarmUtils.VACCINE_ALARM_UPDATE_ACTION));	
	}
    
	@SuppressLint("SimpleDateFormat")
	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(new Date());
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.vaccine_logo_iv:
				dismissVaccineAlarmDialog(true);
				break;
		}
	}
	
	public void showVaccineAlarmDialog() {
		if(mVaccineAlarmDialog != null) {
			wakeAndUnlock(true);
			mVaccineAlarmDialog.show();
			playDefaultAlarmRingtone(MyApplication.getAppContext());
			mHandler.sendEmptyMessageDelayed(DISMISS_DIALOG, ALARM_DIALOG_DISMISS_DELAY);
		}
	}
	
	public void dismissVaccineAlarmDialog(boolean dismiss) {
		mHandler.removeMessages(DISMISS_DIALOG);
		wakeAndUnlock(false);
		if(mVaccineAlarmDialog != null && mVaccineAlarmDialog.isShowing() && dismiss) {
			Log.e("lee", "dismiss ------ >>>");
			mVaccineAlarmDialog.dismiss();
			mVaccineAlarmDialog = null;
			if (mClockView != null) {
				mClockView.stopClock();
				mClockView = null;
			}
		}
		stopDefaultAlarmRingtone();
	}
	
	private void wakeAndUnlock(boolean enable) {
		Log.e("xiaolp", "enable == " + enable);
		if (enable) {
			pm = (PowerManager) MyApplication.getAppContext().getSystemService(
					Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
			wl.acquire();
			km = (KeyguardManager) MyApplication.getAppContext()
					.getSystemService(Context.KEYGUARD_SERVICE);
			kl = km.newKeyguardLock("unLock");
			kl.disableKeyguard();
		} else {
			try {
				if (kl != null)
					kl.reenableKeyguard();
				if (wl != null)
					wl.release();
			} catch (Exception e) {
				Log.e("wakeAndUnlock", " err = " + e.getStackTrace());
			}
		}
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		if (mAlarmDialogUpdateReceiver != null) {
			try{
				Log.e("lee", "dismiss  vaccine alarm Dialog ------ >>>");
				if (mVaccineList != null) {
					mVaccineList.clear();
				}
				VaccineAlarmSevice.stopVaccineAlarm(MyApplication.getAppContext());
				MyApplication.getAppContext().unregisterReceiver(mAlarmDialogUpdateReceiver);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void playDefaultAlarmRingtone(Context context) {
		 Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		 NotifyPlayer.getInstance().playByUri(context, defaultUri,true);
	}
	
	private void stopDefaultAlarmRingtone() {
		NotifyPlayer.getInstance().stopPlay();
	}
	
	private BroadcastReceiver mAlarmDialogUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			 int vaccineType = intent.getIntExtra(Vaccine.VACCINE_TYPE, -1);
			 Log.e("lee", "mAlarmDialogUpdateReceiver == "+vaccineType);
			 VaccineInfo vaccineInfo = Common.getVaccineAlarmByType(vaccineType);
			 mVaccineList.add(vaccineInfo);
			 if(isShowing()) {
				 if (mVaccineContentTextView != null) {
					 VaccineInfo currVaccineInfo = mVaccineList.removeFirst();
					 StringBuilder vaccineContentText = new StringBuilder(currVaccineInfo.getVaccineTypeName());
					 Iterator<VaccineInfo> iterator = mVaccineList.iterator();
					 while (iterator.hasNext()) {
					 	VaccineInfo info = iterator.next();
					 	if (info != null) {
					 		String vaccineTypeName = info.getVaccineTypeName();
					 		if (vaccineTypeName != null && !TextUtils.isEmpty(vaccineTypeName)) {
					 			vaccineContentText.append(",");
						 		vaccineContentText.append(vaccineTypeName);
					 		}
					 	}
					 }
					 mVaccineContentTextView.setText(mContext.getResources().getString(R.string.vaccine_alarm_text_format, vaccineContentText.toString()));
				 }
			 }
		}
	};
}