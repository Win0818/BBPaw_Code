package com.worldchip.bbpaw.bootsetting.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.EncryptData;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

public class MyApplication extends Application {
	
	private static Context context; 
	public ScanResult mScanResult = null;
	public Locale languageLocale = null;
	private List<Activity> activityList = new LinkedList();
	private static MyApplication instance;
	public static boolean isRegister = false;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyApplication.context = getApplicationContext();
		if(!EncryptData.encrypt(MyApplication.context)){
        	throw new java.lang.NullPointerException();
        }
		/*String isFirst = Common.getPreferenceValue(Utils.IS_FIRST_START_KEY, "true");
		boolean first = isFirst.equals("true") ? true :false;
		LogUtil.e("MyApplication", "isFirst == "+isFirst);
		if (!first) {
			//MyApplication.getInstance().exit();
			//return;
		}*/
	}
	
	public static Context getAppContext() { 
        return MyApplication.context; 
    }
	
	// 单例模式中获取唯一的ExitApplication实例
		public static MyApplication getInstance() {
			if (null == instance) {
				instance = new MyApplication();
			}
			return instance;
		}

		// 添加Activity到容器中
		public void addActivity(Activity activity) {
			activityList.add(activity);
		}

		// 遍历所有Activity并finish
		public void exit() {
			for (Activity activity : activityList) {
				LogUtil.e("MyApplication", "exit activity == "+activity);
				activity.finish();
			}
			System.exit(0);
		}
}
