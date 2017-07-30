package com.worldchip.bbp.bbpawmanager.cn.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class AsyncHandler {
	private static final HandlerThread sHandlerThread = new HandlerThread(
			"AsyncHandler");
	private static final Handler sHandler;

	static {
		sHandlerThread.start();
		sHandler = new Handler(sHandlerThread.getLooper());
	}

	public static void post(Runnable r) {
		sHandler.post(r);
	}

	public static void postDelayed(Runnable r, long delayMillis) {
		sHandler.postDelayed(r, delayMillis);
	}
	
	private AsyncHandler() {
	}
}
