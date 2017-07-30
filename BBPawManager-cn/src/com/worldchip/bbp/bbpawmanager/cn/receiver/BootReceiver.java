package com.worldchip.bbp.bbpawmanager.cn.receiver;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver.PendingResult;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.activity.EyecareActivity;
import com.worldchip.bbp.bbpawmanager.cn.activity.MainActivity;
import com.worldchip.bbp.bbpawmanager.cn.push.MessagePushService;
import com.worldchip.bbp.bbpawmanager.cn.utils.AsyncHandler;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class BootReceiver extends BroadcastReceiver {
	
    private static final String TAG = "BootReceiver";
    
    
	@Override
    public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			Log.e(TAG, "REBOOT_SERVICE...will start push message  service!");
			Common.startPushServiceAlarm(MyApplication.getAppContext());
			String screenTimeSwitch = Common.getEyecareSettingsPreferences(Utils.EYECARE_SCREEN_TIME_PRE_KEY, "false");
			if (Boolean.parseBoolean(screenTimeSwitch)) {
				if (handleEyecareAlarm()) {
					Common.handleCreatEyecareAlarm(context);
				} else {
					 AsyncHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							startBBawEyecare(MyApplication.getAppContext());
						}
					}, 10 * 1000);
				}
			}
		}  
		
		if (!Common.isPushServiceRunning()) {
			Log.e(TAG, "restart services...!");
			startPushMessageService(context);
		}
    }
	
	
	private void startBBawEyecare(Context context) {
		// TODO Auto-generated method stub
		try{
			Log.e(TAG, "startBBawEyecare...!");
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	private boolean handleEyecareAlarm() {
		String useBeginTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_BEGIN_PRE_KEY, Configure.DEFAULT_USE_BEGIN_TIME);
		String useEndTime = Common.getEyecareSettingsPreferences(Utils.EYECARE_DAY_TIME_END_PRE_KEY, Configure.DEFAULT_USE_END_TIME);
		Date currentDate = new Date();
		Calendar  calendar= Calendar.getInstance();  
		calendar.setTime(currentDate);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		boolean useBeginTimeCompare2CurrDate = Common.compare2CurrDate(useBeginTime, hour, minute);
		boolean useEndTimeCompare2CurrDate = Common.compare2CurrDate(useEndTime, hour, minute);
		if (useBeginTimeCompare2CurrDate || !useEndTimeCompare2CurrDate) {
			return false;
		}
		return true;
	}
	
	private void startPushMessageService(Context context){
		try{
			MessagePushService.actionStart(context);
		}catch(Exception err){
			Log.e(TAG, "start push message Service...err:"+err.getMessage());
		}
	}
	
	
}