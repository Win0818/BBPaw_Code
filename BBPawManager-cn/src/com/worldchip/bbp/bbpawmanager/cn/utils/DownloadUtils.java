package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.download.DownloadService;

public class DownloadUtils {

		public static final int ACTION_DOWNLOAD_PROCESS = 0;
		public static final int ACTION_DOWNLOAD_COMPLETE = 1;
		public static final int ACTION_DOWNLOAD_START = 2;
		public static final int ACTION_DOWNLOAD_PAUSE = 3;
		public static final int ACTION_DOWNLOAD_DELETE = 4;
		public static final int ACTION_DOWNLOAD_CONTINUE = 5;
		public static final int ACTION_DOWNLOAD_ADD = 6;
		public static final int ACTION_DOWNLOAD_STOP = 7; 
		public static final int ACTION_DOWNLOAD_ERROR = 8;
		
		public static final String TYPE = "type";
		public static final String PROCESS_PROGRESS = "process_progress";
		public static final String URL = "url";
		public static final String FILE_PATH = "file_path";
		public static final String ERROR_CODE = "error_code";
		public static final String IS_PAUSED = "is_paused";
		public static final String BBPAWMANAGE_DOWNLOAD_ACTION = "com.worldchip.bbp.bbpawmanager.Download";
		public static final String TEMP_SUFFIX = ".download";
		
		
	public static final String PREFERENCE_NAME = "download";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_WORLD_WRITEABLE);
	}

	public static String getString(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}

	public static void setString(Context context, String key, String value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public static final int URL_COUNT = 3;
	public static final String KEY_URL = "url";

	public static void storeURL(Context context, int index, String url) {
		setString(context, KEY_URL + index, url);
	}

	public static void clearURL(Context context, int index) {
		setString(context, KEY_URL + index, "");
	}

	public static String getURL(Context context, int index) {
		return getString(context, KEY_URL + index);
	}

	public static List<String> getURLArray(Context context) {
		List<String> urlList = new ArrayList<String>();
		for (int i = 0; i < URL_COUNT; i++) {
			if (!TextUtils.isEmpty(getURL(context, i))) {
				urlList.add(getString(context, KEY_URL + i));
			}
		}
		return urlList;
	}

	public static final String KEY_RX_WIFI = "rx_wifi";
	public static final String KEY_TX_WIFI = "tx_wifi";
	public static final String KEY_RX_MOBILE = "tx_mobile";
	public static final String KEY_TX_MOBILE = "tx_mobile";
	public static final String KEY_Network_Operator_Name = "operator_name";

	public static int getInt(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getInt(key, 0);
		else
			return 0;
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public static long getLong(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getLong(key, 0L);
		else
			return 0L;
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public static void addLong(Context context, String key, long value) {
		setLong(context, key, getLong(context, key) + value);
	}
	
	public static void downLoad(Context context,String filePath) {
		// TODO Auto-generated method stub
		Intent downloadIntent = new Intent(context, DownloadService.class);
        downloadIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_ADD);
        downloadIntent.putExtra(DownloadUtils.URL, filePath);
        context.startService(downloadIntent);
	}
	
	public static void pauseDownLoad(Context context,String filePath) {
		// TODO Auto-generated method stub
		Intent downloadIntent = new Intent(context, DownloadService.class);
		downloadIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_PAUSE);
		downloadIntent.putExtra(DownloadUtils.URL, filePath);
		context.startService(downloadIntent);
	}
	public static void continueDownLoad(Context context,String filePath) {
		// TODO Auto-generated method stub
		Intent downloadIntent = new Intent(context, DownloadService.class);
		downloadIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_CONTINUE);
		downloadIntent.putExtra(DownloadUtils.URL, filePath);
		context.startService(downloadIntent);
	}
	
	public static void cancelDownLoad(Context context,String filePath) {
		// TODO Auto-generated method stub
		Intent downloadIntent = new Intent(context, DownloadService.class);
		downloadIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_PAUSE);
		downloadIntent.putExtra(DownloadUtils.URL, filePath);
		context.startService(downloadIntent);
	}
	public static void deleteDownLoad(Context context,String filePath) {
		// TODO Auto-generated method stub
		Intent downloadIntent = new Intent(context, DownloadService.class);
		downloadIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_DELETE);
		downloadIntent.putExtra(DownloadUtils.URL, filePath);
		context.startService(downloadIntent);
	}
	
	public static boolean checkDownlodFileExists(String downloadUrl) {
		File file = getLocalFileFromUrl(downloadUrl);
		Log.e("lee", "checkDownlodFileExists ======= "+file.getAbsolutePath());
		return file.exists();
	}
	
	public static File getLocalFileFromUrl(String downloadUrl) {
		String fileName = NetworkUtils.getFileNameFromUrl(downloadUrl);
		return  new File(StorageUtils.FILE_ROOT, fileName);
	}
}
