package com.worldchip.bbp.bbpawmanager.cn.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkHelper {

	private static String LOG_TAG = NetWorkHelper.class.getSimpleName();
	
	private static boolean DEBUG = true;
	
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {
			if(DEBUG)Log.d(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable() && info[i].isConnected() ) {
						if(DEBUG)Log.d(LOG_TAG, "network is available");
						return true;
					}
				}
			}
		}
		if(DEBUG)Log.d(LOG_TAG, "network is not available");
		return false;
	}

	public static boolean isEthernetDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isEthernetDataEnable = false;

		isEthernetDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_ETHERNET).isConnectedOrConnecting();

		return isEthernetDataEnable;
	}

	public static boolean isWifiDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiDataEnable = false;
		isWifiDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		return isWifiDataEnable;
	}
}
