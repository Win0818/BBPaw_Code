package com.worldchip.activity;

import android.app.Application;
import android.content.Context;


public class MyAplication extends Application{
	private static Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		/*MyAplication.context = getApplicationContext();
		  if(!EncryptData.encrypt(MyAplication.context)){
			  throw new java.lang.NullPointerException();
		  }*/
	}
	public static Context getAppContext() {
		return MyAplication.context;
	}
}
