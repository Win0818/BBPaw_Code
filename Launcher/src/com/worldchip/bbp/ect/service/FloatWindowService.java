package com.worldchip.bbp.ect.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.worldchip.bbp.ect.util.MyWindowManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class FloatWindowService extends Service{

	private static final String TAG = "----FloatWindowService----";

	private static final boolean DEBUG = false;

	private static final long INTERVAL = 500;
	private static final long BIG_WINDOW = 8000;
	private static final long TRANSLATE_WINDOW = 10000;
	private static final long TIMING = 1000;
	protected static final int CREATE_SMALL_WIEW = 0;
	protected static final int REMOVE_SMALL_WIEW = 1;
	protected static final int CREATE_BIG_WIEW = 2;
	protected static final int REMOVE_BIG_WIEW = 3;
	protected static final int TRANSLATE_IMAGE = 4;
	
    
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
				case CREATE_SMALL_WIEW:
					createSmallView();
					Log.i(TAG, "viewssss");
					break;
				case REMOVE_SMALL_WIEW:
					removeSmallView();
					break;
		    }
		}
	};
	
	private Timer timer;
	private Ticker ticker;
	private SmallIconTicker smallIconTicker;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		if(timer == null){
//			timer = new Timer();
//			timer.scheduleAtFixedRate(new RefreshTask(), 0, INTERVAL);
//	    }
		
			if(!MyWindowManager.isSmallWindowShowing()){
				Log.i(TAG, "jjjjkkkk");
			Message message = new Message();
			message.what = CREATE_SMALL_WIEW;
			handler.sendMessage(message);
		}	
//			if(MyWindowManager.isSmallWindowShowing()){
//				handler.post(new Runnable() {
//					public void run() {
//						if(smallIconTicker == null){
//							smallIconTicker = new SmallIconTicker(TRANSLATE_WINDOW, TIMING);
//							smallIconTicker.start();
//						}
//					}
//				});
//			
//		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		timer.cancel();
//		timer = null;
	}
	
	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			if(!MyWindowManager.isSmallWindowShowing()){
				Log.i(TAG, "jjjjkkkk");
			Message message = new Message();
			message.what = CREATE_SMALL_WIEW;
			handler.sendMessage(message);
		}	
			if(MyWindowManager.isSmallWindowShowing()){
				handler.post(new Runnable() {
					public void run() {
						if(smallIconTicker == null){
							smallIconTicker = new SmallIconTicker(TRANSLATE_WINDOW, TIMING);
							smallIconTicker.start();
						}
					}
				});
			}
		}
	}
	
	public class Ticker extends CountDownTimer{

		public Ticker(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			Log.i(TAG, "Ticker, onFinish....");
			Ticker.this.cancel();
			ticker = null;
//			Message message = new Message();
//			message.what = REMOVE_BIG_WIEW;
//			handler.sendMessage(message);
			
			Message msg = new Message();
			msg.what = CREATE_SMALL_WIEW;
			handler.sendMessage(msg);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Log.i(TAG, "Ticker, onTick....");
			Ticker.this.cancel();
			ticker = null;
			
		}
	}
	
	public class SmallIconTicker extends CountDownTimer{

		public SmallIconTicker(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			Log.i(TAG, "SmallIconTicker, onFinish....");
			SmallIconTicker.this.cancel();
			smallIconTicker = null;
			Message message = new Message();
			message.what = TRANSLATE_IMAGE;
			handler.sendMessage(message);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Log.i(TAG, "SmallIconTicker, onTick....");
		}
	}
	
	private boolean isHome() {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return getHomes().contains(rti.get(0).topActivity.getPackageName());
	}
	
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}
	
	private void createSmallView(){
		
		if(!MyWindowManager.isSmallWindowShowing()){
			MyWindowManager.createSmallWindow(getApplicationContext());
		}
	}
	private void removeSmallView(){
		if(MyWindowManager.isSmallWindowShowing()){
			MyWindowManager.removeSmallWindow(getApplicationContext());
		}
	}
	
}
