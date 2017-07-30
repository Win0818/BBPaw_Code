package com.worldchip.bbp.ect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeTontrolReceiver extends BroadcastReceiver {

    private static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_CHANGED;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		String action = intent.getAction();
		if (ACTION_TIME_CHANGED.equals(action))
		{
			//Utils.showToastMessage(context, "我监听到时间变化了");
        }
	}
}
