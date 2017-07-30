package com.worldchip.bbpaw.bootsetting.receiver;

import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcaseReceiver extends BroadcastReceiver {
	private static final String TAG = BootBroadcaseReceiver.class.getSimpleName();
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.getAction().equals(ACTION)) {
			/*String isFirst = Common.getPreferenceValue(Utils.IS_FIRST_START_KEY, "true");
			boolean first = isFirst.equals("true") ? true :false;
			LogUtil.e(TAG, "isFirst == "+isFirst);
			if (!first) {
				return;
			}*/
			String startCount = Common.getPreferenceValue(Utils.IS_FIRST_START_KEY, "0");
			LogUtil.e(TAG, "startCount == "+startCount);
			if (startCount.equals("3")) {
				return;
			}
			Intent intent = new Intent();
			intent.setClassName("com.worldchip.bbpaw.bootsetting",
					"com.worldchip.bbpaw.bootsetting.activity.MainActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(intent);
		}
	}
}
