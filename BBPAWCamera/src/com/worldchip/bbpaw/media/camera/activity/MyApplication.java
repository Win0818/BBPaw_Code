package com.worldchip.bbpaw.media.camera.activity;


import android.app.Application;
import android.content.Context;

import com.worldchip.bbpaw.media.camera.utils.EncryptData;
import com.worldchip.bbpaw.media.camera.utils.SimpleAudioPlayer;

public class MyApplication extends Application {
	
	private static Context context; 
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		/*if(!EncryptData.encrypt(this)){
        	throw new java.lang.NullPointerException();
        }*/
		MyApplication.context = getApplicationContext(); 
		SimpleAudioPlayer.initialize(this);
	}
	
	public static Context getAppContext() { 
        return MyApplication.context; 
    }
	
	
}
