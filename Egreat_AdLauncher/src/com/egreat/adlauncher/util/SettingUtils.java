package com.egreat.adlauncher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

public class SettingUtils {

	public static Boolean getApp01DefaultValue(Context mContext) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		return mSharedPreferences.getBoolean("default_add_1", true);
	}

	public static Boolean getApp02DefaultValue(Context mContext) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		return mSharedPreferences.getBoolean("default_add_2", true);
	}
	
	public static void setApp01DefaultValue(Context mContext, boolean defaultAdd) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putBoolean("default_add_1", defaultAdd).commit();
	}
	
	public static void setApp02DefaultValue(Context mContext, boolean defaultAdd) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putBoolean("default_add_2", defaultAdd).commit();
	}
	
	public static String getApp01PackageName(Context mContext) {
		String ret = "";
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		ret = mSharedPreferences.getString("app_01", "");
		return ret;
	}

	public static void setApp01PackageName(Context mContext, String name) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putString("app_01", name).commit();
	}

	public static String getApp02PackageName(Context mContext) {
		String ret = "";
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		ret = mSharedPreferences.getString("app_02", "");
		return ret;
	}

	public static void setApp02PackageName(Context mContext, String name) {
		SharedPreferences mSharedPreferences = mContext.getSharedPreferences("Config", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putString("app_02", name).commit();
	}

}
