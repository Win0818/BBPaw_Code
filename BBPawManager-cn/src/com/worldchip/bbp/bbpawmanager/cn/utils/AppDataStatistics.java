package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class AppDataStatistics {
	
	public static PackageInfo getPackageInfo(Context context,String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for ( int i = 0; i < pinfo.size(); i++ ){
			Log.e("xiaolp","packageName = "+pinfo.get(i).packageName);
			if(pinfo.get(i).packageName.equalsIgnoreCase(packageName)){
				return pinfo.get(i);
			}
		}
		return null;
	}
	
	public static boolean isAppInstalled(Context context,String packageName) {
		if (packageName == null || TextUtils.isEmpty(packageName)) {
			return false;
		}
		PackageInfo packageInfo = getPackageInfo(context, packageName);
		if (packageInfo != null) {
			return true;
		}
		return false;
	}
}
