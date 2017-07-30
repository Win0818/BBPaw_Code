package com.worldchip.bbp.bbpawmanager.cn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.utils.AlarmAlertWakeLock;
import com.worldchip.bbp.bbpawmanager.cn.utils.AsyncHandler;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.view.EyecareRestDialog;

public class EyecareAlarmReceiver extends BroadcastReceiver {
	
    private static final String TAG = "EyecareRestDialog";
    private EyecareRestDialog mEyecareRestDialog = null;
    
	@Override
    public void onReceive(final Context context, final Intent intent) {
		final PendingResult result = goAsync();
        final PowerManager.WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                handleIntent(context, intent);
                result.finish();
                wl.release();
            }
        });
    }
	
	private void handleIntent(Context context, Intent intent) {
		final String action = intent.getAction();
		if (Utils.REST_ALARM_ACTION.equals(action)) {
			Log.e("EyecareAlarmReceiver", " EyecareAlarmReceiver handleIntent REST_ALARM_ACTION -----");
			alertEyecareRestDialog(context,intent);
		} else if (Utils.EYECARE_ALARM_CHANGE_ACTION.equals(action)) {
			Log.e("EyecareAlarmReceiver", " EyecareAlarmReceiver handleIntent EYECARE_ALARM_CHANGE_ACTION -----");
			dissmissEyecareDialog();
		}
	}
	
	private void alertEyecareRestDialog(Context context, Intent intent) {
		dissmissEyecareDialog();
		String screenTimeSwitch = Common.getEyecareSettingsPreferences(Utils.EYECARE_SCREEN_TIME_PRE_KEY, "false");
		if (!Boolean.parseBoolean(screenTimeSwitch)) {
			return;
		}
		mEyecareRestDialog = EyecareRestDialog.createDialog(context);
		mEyecareRestDialog.setSensorDrawableAnim(Configure.EYECARE_REST_DRAWABLE_ANIM_RES);
		mEyecareRestDialog.setDismissDialogDelay(intent.getIntExtra("show_time", -1));
		mEyecareRestDialog.showEyecareDialog();
	}
	
	private void dissmissEyecareDialog() {
		if (mEyecareRestDialog != null) {
			mEyecareRestDialog.dismissEyecareDialog();
			mEyecareRestDialog = null;
		}
	}
}