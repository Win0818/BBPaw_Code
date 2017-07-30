package com.worldchip.bbp.ect.activity;


import com.worldchip.bbp.ect.db.DBGoldHelper;
import com.worldchip.bbp.ect.util.EncryptData;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.app.Application;

public class MyApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
		/*if(!EncryptData.encrypt(MyApplication.context)){
	          	throw new java.lang.NullPointerException();
	          }*/
	}
	public static Context getAppContext() {
		return MyApplication.context;
	}
}