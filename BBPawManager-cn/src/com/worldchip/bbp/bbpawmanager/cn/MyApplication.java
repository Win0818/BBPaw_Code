package com.worldchip.bbp.bbpawmanager.cn;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.worldchip.bbp.bbpawmanager.cn.push.MessagePushService;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.EncryptData;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class MyApplication extends Application {
	
	private static Context context; 
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(!EncryptData.encrypt(MyApplication.context)){
        	throw new java.lang.NullPointerException();
        }
		MyApplication.context = getApplicationContext(); 
		initVaccineData();
		saveDeviceId();
	}
	
	public static Context getAppContext() { 
        return MyApplication.context; 
    }
	
	private void saveDeviceId() {
		Editor editor = getSharedPreferences(MessagePushService.TAG, MODE_PRIVATE).edit(); 
    	editor.putString(MessagePushService.PREF_DEVICE_ID, Common.getCpuSerial(context));
    	editor.commit();
	}
	
	private void initVaccineData() {
		SharedPreferences preferences = this.getSharedPreferences(Utils.SHARED_PREFERENCE_NAME, 0);		
		boolean isInitVaccineDefault = preferences.getBoolean(Utils.SHARED_VACCINE_DEFAULT_KEY, true);
		if(isInitVaccineDefault){
		    if(Common.initVaccineDefaultDatas(this)){
		    	preferences.edit().putBoolean(Utils.SHARED_VACCINE_DEFAULT_KEY, false).commit();
		    }
		}
	}
	
	
}
