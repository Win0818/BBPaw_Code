package com.worldchip.bbp.ect.receiver;

import com.worldchip.bbp.ect.service.ScanService;
import com.worldchip.bbp.ect.service.TimeTontrolService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DetectionAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.d("Wing", "action:  " + action); 
		if (action != null) {
			if (action.equals("com.worldchip.bbp.ect.service.TimeTontrolService")) {
				Log.d("Wing", "com.worldchip.bbp.ect.service.TimeTontrolService");
				Intent i = new Intent(context, TimeTontrolService.class);
				context.startService(i);
			} else if(action.equals("com.worldchip.bbp.ect.service.ScanService")) {
				Log.d("Wing", "com.worldchip.bbp.ect.service.ScanService");
				Intent i = new Intent(context, ScanService.class);
				context.startService(i);
			} 
		}
				
	}
}
