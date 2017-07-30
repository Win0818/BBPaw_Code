package com.worldchip.bbpaw.bootsetting.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Locale;

import android.app.backup.BackupManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.worldchip.bbpaw.bootsetting.activity.MyApplication;

public class Common {
	private static final String TAG = Common.class.getSimpleName();
	
	
	public static String getDeviceId(Context context) {
		String deviceID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		Log.e(TAG, "getDeviceId=" + deviceID);
		return deviceID;
	}

	public static String getCpuSerial(Context context) {
		FileReader fr = null;
		BufferedReader br = null;
		String cpuSerial = null;
		try {
			fr = new FileReader("/proc/cpuinfo");
			br = new BufferedReader(fr);
			String temp = null;
			while ((temp = br.readLine()) != null) {
				cpuSerial = temp;
			}
			cpuSerial = cpuSerial.replaceAll("\\s*", "");
			String[] array = cpuSerial.split(":");
			for (int i = 0; i < array.length; i++) {
				cpuSerial = array[i];
			}
			Log.e(TAG, "getCpuSerial=" + cpuSerial);
			if (cpuSerial != null) {
				return cpuSerial;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	
    /** 
     * 判断网络是否连通 
     * @param context 
     * @return 
     */ 
    public static boolean isNetworkConnected(Context context)
    {  
        @SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = cm.getActiveNetworkInfo();  
        return info != null && info.isConnected();    
    }
    
	/**
	 * �ر������
	 */
	public static void CloseKeyBoard(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		System.out.println("isActive:" + imm.isActive());
		if (imm.isActive()) {
			// �����
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	public static void showKeyboard(Context context) {
		if (context == null) {
			return;
		}
		InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);
	}
	public static void hideKeyboard(Context context, View view) {
		if (context == null || view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static void updateLanguage(final Locale locale) {
		new Thread() {
			public void run() {
				LogUtil.e(TAG, "languageString =" + locale);
				try {
					Class amnClass = Class.forName("android.app.ActivityManagerNative");
					Object amn = null;
					Configuration config = null;

					// amn = ActivityManagerNative.getDefault();
					Method methodGetDefault = amnClass.getMethod("getDefault");
					methodGetDefault.setAccessible(true);
					amn = methodGetDefault.invoke(amnClass);

					// config = amn.getConfiguration();
					Method methodGetConfiguration = amnClass
							.getMethod("getConfiguration");
					methodGetConfiguration.setAccessible(true);
					config = (Configuration) methodGetConfiguration.invoke(amn);

					// config.setLocale(locale);
					Class configClass = config.getClass();
					
					Method methodSetLocale = configClass.getMethod("setLocale",Locale.class);
					methodSetLocale.invoke(config, locale);

					// amn.updateConfiguration(config);
					Method methodUpdateConfiguration = amnClass.getMethod(
							"updateConfiguration", Configuration.class);
					methodUpdateConfiguration.setAccessible(true);

					methodUpdateConfiguration.invoke(amn, config);
					BackupManager.dataChanged("com.android.providers.settings");
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	
	 public static void setPreferenceValue(String key, String value) {
	    	Shared.getInstance(MyApplication.getAppContext()).putString(Configure.SETTINGS_SHARD_NAME, Configure.SETTINGS_SHARD_MODE, key, value);
	 }

	 public static String getPreferenceValue(String key, String defValue) {
	    	return Shared.getInstance(MyApplication.getAppContext()).getString(Configure.SETTINGS_SHARD_NAME, Configure.SETTINGS_SHARD_MODE, key, defValue);
	 }
	
	 public static void restPreference() {
		 Shared.getInstance(MyApplication.getAppContext()).clearAllData(Configure.SETTINGS_SHARD_NAME, Configure.SETTINGS_SHARD_MODE);
	 }
	 
	public static String convertToFilePath(final Context context, final Uri imageUri) {
	    if (null == imageUri) return null;
	    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {  
	        if (Utils.isExternalStorageDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            if ("primary".equalsIgnoreCase(type)) {  
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	        } else if (Utils.isDownloadsDocument(imageUri)) {  
	            String id = DocumentsContract.getDocumentId(imageUri);  
	            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	            return getDataColumn(context, contentUri, null, null);  
	        } else if (Utils.isMediaDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	            String selection = MediaStore.Images.Media._ID + "=?";  
	            String[] selectionArgs = new String[] { split[1] };  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    }
	    else if ("content".equalsIgnoreCase(imageUri.getScheme())) {  
	        // Return the remote address  
	        if (Utils.isGooglePhotosUri(imageUri))  
	            return imageUri.getLastPathSegment();  
	        return getDataColumn(context, imageUri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(imageUri.getScheme())) {  
	        return imageUri.getPath();  
	    }  
	    return null;  
	}
	
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {  
	    Cursor cursor = null;  
	    String column = MediaStore.Images.Media.DATA;  
	    String[] projection = { column };  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
			Log.e(TAG, "getSDPath ...sdDir.toString=" + sdDir.toString());
			return sdDir.toString();
		}
		return null;
	}
}
