package com.worldchip.bbpaw.media.camera.db;



import java.io.File;
import android.util.Log;
import java.lang.Thread;
import android.os.Environment;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.SQLException;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class MainDataManager {

	public static void startASetOfActivity(Context context) {
		Log.e(TAG, "startASetOfActivity----");
		try {
			Intent intent = new Intent();

			intent.setComponent(new ComponentName("com.worldchip.bbp.ect",
					"com.worldchip.bbp.ect.activity.PassLockActivity"));
			Bundle bundle = new Bundle();
			bundle.putBoolean("setup", true);
			intent.putExtras(bundle);
			
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "startASetOfActivity....ActivityNotFoundException");
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "startASetOfActivity....Exception");
			e.printStackTrace();
		}
	}
	
	// work for goldGame
	public static void startGoldGameActivity(Context context) {
		Log.e(TAG, "startGoldGameActivity----");
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(
				"com.worldchip.bbpaw.gold.bbbuckstime",
				"org.cocos2dx.cpp.AppActivity"));
			context.startActivity(intent);

		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "startGoldGameActivity....ActivityNotFoundException");
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "startGoldGameActivity....Exception");
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------------------

	public static final String TAG = "--------DataManager---------";
	public static final String MUSIC_PATH_NAME = ".Music";
	public static final String SCREENSHOT_PATH_NAME = "ScreenShot";
	public static final String MAIN_PATH_NAME = File.separator + "BBPaw";

	public static String getData(Context context, final String apkName,
			final String key) {
		return getDataValue(context, apkName, key);
	}

	public static void setData(Context context, final String apkName,
			final String key, final String value) {
		Log.e(TAG, "setData key = " + key + "; value = " + value);
		if (!dirExist()) {
			return;
		}

		insertOrUpdateValues(context, apkName, key, value);
	}

	// ----------------------------------------------------------------------------------

	public static boolean updateGold(Context context, final String apkName,final int goldValue) {
		if (!dirExist()) {
			return false;
		}
		Log.e(TAG, "updateGold  apkName = " + apkName);
		return updateGoldValue(context, apkName, goldValue);
	}

	public static int getGold(Context context, final String apkName) {
		if (!dirExist()) {
			return -1;
		}
		Log.e(TAG, "getGold  apkName = " + apkName);
		return getDataValue(context, apkName);
	}

	public static int getSumGold(Context context) {
		if (!dirExist()) {
			return -1;
		}
		Log.e(TAG, "getSumGold--");
		return getDataValue(context);
	}

	public static boolean getMainIsPlayEffectEnable(Context context){
		if (!dirExist()) {
			return false;
		}
		return isPlayMusicEnable(context, "effect_music");
	}
	
    public static boolean getMainIsPlayBackgroundMusicEnable(Context context){
    	if (!dirExist()) {
			return false;
		}
		return isPlayMusicEnable(context, "background_music");
	}
	
	public static void setMainIsPlayEffectEnable(Context context, boolean isEnable){
		if (!dirExist()) {
			return;
		}
		updateMusicEffectValue(context, "effect_music", isEnable);
	}
	
    public static void setMainIsPlayBackgroundMusicEnable(Context context, boolean isEnable){
    	if (!dirExist()) {
			return;
		}
    	updateMusicEffectValue(context, "background_music", isEnable);
	}
 
	// ----------------------------------------------------------------------------------
   
	public static void createMusicDir(String apkName) {
		Log.e(TAG, "createDir apkName = " + apkName);
		while (!dirExist(MUSIC_PATH_NAME + File.separator + apkName)) {
			try {
				Thread.sleep(100);
			} catch (Exception err) {
				Log.e(TAG, "dir exist err");
				err.printStackTrace();
			}
		}
	}

	public static String getMusicPath(String apkName, String musicName) {
		return getDataDir() + File.separator + MUSIC_PATH_NAME + File.separator
				+ apkName + File.separator + musicName;
	}

	public static void openScreenShotDir(Context context, String apkName) {

		Log.e(TAG, "openScreenShotDir....apkName=" + apkName);
		try {

			String path = createScreenShotDir(apkName);
			if (path == null || path.equals("")) {
				Log.e(TAG, "createScreenShotDir fail!");
				return;
			}

			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			String filesPath = getDataDir() + File.separator
					+ SCREENSHOT_PATH_NAME + File.separator + apkName;
			Log.e(TAG, "openScreenShotDir----filesPath = " + filesPath);
			intent.putExtra("files_path", filesPath);
			intent.setComponent(new ComponentName("com.worldchip.bbpaw.album",
					"com.worldchip.bbpaw.album.activity.PhotosListActivity"));
			context.startActivity(intent);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(TAG, "openScreenShotDir....ArrayIndexOutOfBoundsException");
			e.printStackTrace();
			// mHandler.sendEmptyMessage(START_APP_ERROR);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "openScreenShotDir....ActivityNotFoundException");
			e.printStackTrace();
			// mHandler.sendEmptyMessage(START_APP_ERROR);
		} catch (Exception e) {
			Log.e(TAG, "openScreenShotDir....Exception");
			e.printStackTrace();
		}
	}
	
	// ----------------------------------------------------------------------------------

	// Screen shot path
	public static String createScreenShotDir(String apkName) {
		Log.e(TAG, "createScreenShotDir apkName = " + apkName);
		while (!dirExist(SCREENSHOT_PATH_NAME + File.separator + apkName)) {
			try {
				Thread.sleep(100);
			} catch (Exception err) {
				Log.e(TAG, "dir exist err");
				err.printStackTrace();
				return "";
			}
		}

		return getDataDir() + File.separator + SCREENSHOT_PATH_NAME
				+ File.separator + apkName + File.separator;
	}

	public static boolean isDataExist(Context context, String apkName,
			String key) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursor(MainDBHelper.DATA_TABLE,
				"where apk_name = '" + apkName + "' and key='" + key
						+ "' order by _id desc");
		if (cursor != null && cursor.moveToFirst()) {
			return true;
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			
		}
		return false;
	}

	public static boolean isMusicDataExist(Context context) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursorBySql("select * from "+ MainDBHelper.EFFECT_TABLE);
		if (cursor != null && cursor.moveToFirst()) {
			return true;
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			
		}
		return false;
	}
	
    private static int getExistGoldValue(Context context, String apkName) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursor(MainDBHelper.GOLD_TABLE,
				"where apk_name = '" + apkName + "'");
		if (cursor != null && cursor.moveToFirst()) {
			int value = cursor.getInt(cursor.getColumnIndex("gold"));
			Log.e(TAG, "getExistGoldValue...gold="+value);
			return value;
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return -1;
	}

    public static int getDataValue(Context context, String apkName) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursor(MainDBHelper.GOLD_TABLE,
				"where apk_name = '" + apkName + "'");
		if (cursor != null && cursor.moveToFirst()) {
			int value = cursor.getInt(cursor.getColumnIndex("gold"));
			Log.e(TAG, "getDataValue...gold="+value);
			return value;
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return 0;
	}

    public static int getDataValue(Context context) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursorBySql("select sum(gold) as gold_values from "+MainDBHelper.GOLD_TABLE);
		if (cursor != null && cursor.moveToFirst()) {
			int value = cursor.getInt(cursor.getColumnIndex("gold_values"));
			Log.e(TAG, "getDataValue...sum gold="+value);
			return value;
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return 0;
	}

	public static String getDataValue(Context context, String apkName,
			String key) {
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		 cursor = helper.getCursor(MainDBHelper.DATA_TABLE,
				"where apk_name = '" + apkName + "' and key='" + key
						+ "' order by _id desc");
		if (cursor != null && cursor.moveToFirst()) {
			String value = cursor.getString(cursor.getColumnIndex("value"));
			if (value != null && !value.equals("")) {
				return value.trim();
			} else {
				return "";
			}
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return "";
	}

	private static boolean isPlayMusicEnable(Context context, String musicType) {  //0:background; 1.effect
		MainDBHelper helper = new MainDBHelper(context);
		Cursor cursor = null;
		try {
		cursor = helper.getCursorBySql("select "+musicType +" from "+ MainDBHelper.EFFECT_TABLE);
		if (cursor != null && cursor.moveToFirst()) {
			int musicEffectValue = cursor.getInt(cursor.getColumnIndex(musicType));
			Log.e(TAG, "isPlayMusicEnable...musicType="+musicType
					+"; musicEffectValue="+musicEffectValue);
			if(musicEffectValue==1){
				return false;
			}
			return true; 
		}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			
		}
		return true;
	}
	
	public static void updateMusicEffectValue(Context context,  final String musicType, final boolean enabled){
		
		try{
			
			if (!isMusicDataExist(context)) {
				MainDBHelper helper = new MainDBHelper(context);
				ContentValues values = new ContentValues();
				values.put("background_music", 0);
				values.put("effect_music", 0);
				if (helper.insertValues(MainDBHelper.EFFECT_TABLE, values)) {
					Log.e(TAG, "updateMusicEffectValue insertValues success");
					
					MainDBHelper helper2 = new MainDBHelper(context);
					ContentValues values2 = new ContentValues();
					values2.put(musicType, enabled ? 0 : 1);
					if (helper2.updateValues(MainDBHelper.EFFECT_TABLE, values2, 
							musicType + " is null or "+musicType+" =0 or "+musicType+" =1")) {
						Log.e(TAG, "updateMusicEffectValue updateValues success");
					} else {
						Log.e(TAG, "updateMusicEffectValue updateValues failed");
					}
				} else {
					Log.e(TAG, "updateMusicEffectValue insertValues failed");
				}
			} else {
				MainDBHelper helper = new MainDBHelper(context);
			    ContentValues values = new ContentValues();
				values.put(musicType, enabled ? 0 : 1);
				if (helper.updateValues(MainDBHelper.EFFECT_TABLE, values, 
						musicType + " is null or "+musicType+" =0 or "+musicType+" =1")) {
					Log.e(TAG, "updateMusicEffectValue updateValues success");
				} else {
					Log.e(TAG, "updateMusicEffectValue updateValues failed");
				}
			}
		}catch (SQLException e) {
			Log.e(TAG, "updateMusicEffectValues...SQLException err");
			e.printStackTrace();
		} catch (Exception err) {
			Log.e(TAG, "updateMusicEffectValues...Exception err");
			err.printStackTrace();
		}
	}
	
	public static void insertOrUpdateValues(Context context,
			final String apkName, final String key, final String value) {
		try {
			if (isDataExist(context, apkName, key)) {
				MainDBHelper helper = new MainDBHelper(context);
				ContentValues values = new ContentValues();
				values.put("value", value);
				if (helper.updateValues(MainDBHelper.DATA_TABLE, values,
						"apk_name = '" + apkName + "' and key='" + key + "';")) {
					Log.e(TAG, "setData updateValues success");
				} else {
					Log.e(TAG, "setData updateValues failed");
				}
			} else {
				MainDBHelper helper = new MainDBHelper(context);
				ContentValues values = new ContentValues();
				values.put("apk_name", apkName);
				values.put("key", key);
				values.put("value", value);
				if (helper.insertValues(MainDBHelper.DATA_TABLE, values)) {
					Log.e(TAG, "setData insertValues success");
				} else {
					Log.e(TAG, "setData insertValues failed");
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, "setData...SQLException err");
			e.printStackTrace();
		} catch (Exception err) {
			Log.e(TAG, "setData...Exception err");
			err.printStackTrace();
		}
	}

    public static boolean updateGoldValue(Context context,
			final String apkName, final int value) {
		try {
			int existGoldValue = getExistGoldValue(context, apkName);
			if (existGoldValue > -1) {
				MainDBHelper helper = new MainDBHelper(context);
				ContentValues values = new ContentValues();
				values.put("gold", existGoldValue+value);
				if (helper.updateValues(MainDBHelper.GOLD_TABLE, values,
						"apk_name = '" + apkName + "'")) {
					Log.e(TAG, "updateGoldValue updateValues success...existGoldValue="+existGoldValue+"; gold="+value);
					return true;
				} else {
					Log.e(TAG, "updateGoldValue updateValues failed");
				}
				return false;
			} else {
				MainDBHelper helper = new MainDBHelper(context);
				ContentValues values = new ContentValues();
				values.put("apk_name", apkName);
				values.put("gold", value);
				if (helper.insertValues(MainDBHelper.GOLD_TABLE, values)) {
					Log.e(TAG, "setData insertValues success...existGoldValue="+existGoldValue+"; gold="+value);
				    return true;
				} else {
					Log.e(TAG, "setData insertValues failed");
				}
			}

			return false;
		} catch (SQLException e) {
			Log.e(TAG, "updateGoldValue...SQLException err");
			e.printStackTrace();
		} catch (Exception err) {
			Log.e(TAG, "updateGoldValue...Exception err");
			err.printStackTrace();
		}

		return false;
	}

	public static String getDataDir() {

		return getSDPath() + MAIN_PATH_NAME;
	}

	public static boolean dirExist(String path) {
		File file = null;
		if (path == null || path.equals("")) {
			file = new File(getDataDir());
		} else {
			file = new File(getDataDir() + File.separator + path);
		}
		if (!file.exists()) {
			Log.e(TAG, "dir not exist! will created!");
			try {
				return file.mkdirs();
			} catch (Exception err) {
				Log.e(TAG, "created getSDPath err!");
				err.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static boolean dirExist() {
		return dirExist(null);
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

	/*
	 * public static void saveBitmap(final String apkName, final byte[] bytes,
	 * final String bitmapName){ try{ Bitmap bitmap =
	 * BitmapUtils.bytesToBimap(bytes); Log.e(TAG, "saveBitmap bytes = " + bytes
	 * + "; bitmapName="+bitmapName+"; bitmap = " + bitmap); if(bitmap==null){
	 * Log.e(TAG, "saveBitmap byte to bitmap is null!"); return; }
	 * BitmapUtils.saveBitmap(apkName, bitmap, bitmapName); }catch(Exception
	 * err){ Log.e(TAG, "saveBitmap err Exception !"); err.printStackTrace(); }
	 * }
	 */
}