package com.worldchip.bbp.ect.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

public class AlarmAlertWakeLock {
	private static final String TAG = "vaccineAlarm";
	 private static PowerManager.WakeLock sCpuWakeLock;

	    public static PowerManager.WakeLock createPartialWakeLock(Context context) {
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
	    }

	    public static void acquireCpuWakeLock(Context context) {
	        if (sCpuWakeLock != null) {
	            return;
	        }

	        sCpuWakeLock = createPartialWakeLock(context);
	        sCpuWakeLock.acquire();
	    }

	    @SuppressLint("Wakelock")
		public static void acquireScreenCpuWakeLock(Context context) {
	        if (sCpuWakeLock != null) {
	            return;
	        }
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        sCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
	                | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
	        sCpuWakeLock.acquire();
	    }

	    public static void releaseCpuLock() {
	        if (sCpuWakeLock != null) {
	            sCpuWakeLock.release();
	            sCpuWakeLock = null;
	        }
	    }
}
