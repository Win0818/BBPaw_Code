package com.worldchip.bbp.ect.receiver;

import com.worldchip.bbp.ect.service.BootService;
import com.worldchip.bbp.ect.util.AlarmUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "-----BootReceiver------";

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{ 
			Log.i(TAG, "start BootReceiver from broadcast receiver");
			//Intent startService=new Intent(context, BootService.class); 
			//context.startService(startService);
			AlarmUtil.initAllAlarmByBootCompleted();
	    }
	}
}