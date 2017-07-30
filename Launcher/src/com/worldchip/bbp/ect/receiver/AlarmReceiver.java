package com.worldchip.bbp.ect.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.util.AlarmAlertWakeLock;
import com.worldchip.bbp.ect.util.AlarmUtil;
import com.worldchip.bbp.ect.util.AsyncHandler;
import com.worldchip.bbp.ect.view.BBPawAlarmDialog;
public class AlarmReceiver extends BroadcastReceiver {

	private static BBPawAlarmDialog mAlarmDialog;
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		
		final PendingResult result = goAsync();
        final PowerManager.WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();
        AsyncHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleIntent(context, intent);
                result.finish();
                wl.release();
            }
        },1000);
	}
	
	
	private void handleIntent(Context context, Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (AlarmUtil.BBPAW_ALARM_ACTION.equals(action)) {
				AlarmInfo alarmInfo = (AlarmInfo) intent.getSerializableExtra("alarmInfo");
				//String alarmTimeStamp = intent.getStringExtra("alarmTimeStamp");
				long alarmTimeStamp = intent.getLongExtra("alarmTimeStamp", 0);
				Log.e("lee", "alarmTimeStamp == "+alarmTimeStamp);
				if (alarmTimeStamp > 0) {
					if (AlarmUtil.isShowAlarmDialog(alarmTimeStamp)) {
						showAlarmDialog(context, alarmInfo);
					}
				}
				AlarmUtil.createNextAlarm(context, alarmInfo);
			}
		}
	}
	
	private void showAlarmDialog(final Context context, AlarmInfo alarmInfo) {
		Log.e("lee", "showVaccineAlarmDialog  context == "+context);
		if (mAlarmDialog != null) {
			Log.e("lee", "AlarmReceiver  mAlarmDialog != null");
			if (mAlarmDialog.isShowing()) {
				Log.e("lee", "AlarmReceiver  AlarmDialog isShowing!");
				return;
			}
		}
		mAlarmDialog = BBPawAlarmDialog.createDialog(context);
		mAlarmDialog.setAlarmInfo(alarmInfo);
		mAlarmDialog.showAlarmDialog();
	}	
	
}