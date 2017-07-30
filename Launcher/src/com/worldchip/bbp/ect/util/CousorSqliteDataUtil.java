package com.worldchip.bbp.ect.util;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.worldchip.bbp.ect.entity.LoginState;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CousorSqliteDataUtil {
	
	
	private  static JSONArray array = null;
	private static HashMap<String, String>  mVideoWatchLogMap;
	public static final String COMMIT_DATA_URL = "http://push.bbpaw.com.cn/interface/heroesRank/heroesRank.php";
	
	private  static JSONArray arrayGameAction = null;
	private static HashMap<String, String>  mVideoWatchLogMapGameAction;
	
	private  static JSONArray arrayGold = null;
	private static HashMap<String, String>  mGoldHashMap;


	public static void  cursorData(String databasePath,String tableName,  final Context context){
		SQLiteDatabase  db  = getDataBase(databasePath);
		// mPref = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
		// String user_account = mPref.getString("account", "");
		String user_account = LoginState.getInstance().getAccount();
		if(db != null){
			String selectSQL = "select * from " + tableName;
			Cursor cursor = db.rawQuery(selectSQL, null);
			System.out.println("========cursor======"+cursor.getCount());
			//cursor.moveToFirst(); 
			array = new JSONArray();
			while (cursor.moveToNext()) {
				    System.out.println("cursor=="+cursor.getCount());
				    String  user_device_id = cursor.getString(1);
				    String  package_name = cursor.getString(2);
				    int  apk_type = cursor.getInt(3);
				    int  apk_type_2 = cursor.getInt(4);
				    String  game_score = cursor.getString(5);
				    long  game_time = cursor.getLong(6);
				    JSONObject json = new JSONObject();
				    try {
						json.put("user_device_id", user_device_id);
						json.put("package_name", package_name);
						json.put("apk_type", apk_type);
						json.put("apk_type_2", apk_type_2);
						json.put("game_score", game_score);
						json.put("game_time", game_time);
						json.put("game_email", user_account);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				    array.put(json);
			}
			 System.out.println("array=="+array.toString());
			 new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mVideoWatchLogMap = new HashMap<String, String>();
						mVideoWatchLogMap.put("param", "upload_data");
						mVideoWatchLogMap.put("input",  array.toString());
						String result  = HttpUtils.postRequest(COMMIT_DATA_URL, mVideoWatchLogMap, context);
					    System.out.println("--array commit---"+result);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public static void  cursorDataGameAtion(String databasePath,String tableName,  final Context context){
		 SQLiteDatabase  db  = getDataBase(databasePath);
		 String user_account = LoginState.getInstance().getAccount();
		if(db != null){
			String selectSQL = "select * from "+tableName;
			Cursor cursor = db.rawQuery(selectSQL, null);
			System.out.println("========cursorDataGameAtion======"+cursor.getCount());
			//cursor.moveToFirst(); 
			arrayGameAction = new JSONArray();
			while (cursor.moveToNext()) {
				    System.out.println("cursor=="+cursor.getCount());
				    String  user_device_id = cursor.getString(1);
				    String  package_name = cursor.getString(2);
				    long  day_time = cursor.getLong(3);
				    long   enter_time = cursor.getLong(4);
				    long  exit_time = cursor.getLong(5);
				    JSONObject json = new JSONObject();
				    try {
				    	json.put("user_device_id", user_device_id);
				    	json.put("package_name", package_name);
				    	json.put("day_time", day_time);
				    	json.put("enter_time", enter_time);
				    	json.put("exit_time", exit_time);
						json.put("game_email", user_account);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				    arrayGameAction.put(json);
			}
			 System.out.println("arrayGameAction=="+arrayGameAction.toString());
			 new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mVideoWatchLogMapGameAction = new HashMap<String, String>();
						mVideoWatchLogMapGameAction.put("param", "grow_record");
						mVideoWatchLogMapGameAction.put("input",  arrayGameAction.toString());
						String result  = HttpUtils.postRequest(COMMIT_DATA_URL, mVideoWatchLogMapGameAction, context);
					    System.out.println("--arrayGameAction commit---"+result);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public static void  cursorGoldData(String databasePath,String tableName,  final Context context){
		SQLiteDatabase  db  = getDataBase(databasePath);
		String user_account = LoginState.getInstance().getAccount();
		if(db != null){
			String selectSQL = "select * from "+tableName;
			Cursor cursor = db.rawQuery(selectSQL, null);
			System.out.println("========cursorGoldData======"+cursor.getCount());
			//cursor.moveToFirst(); 
			arrayGold = new JSONArray();
			while (cursor.moveToNext()) {
				    System.out.println("cursor=="+cursor.getCount());
				    String  apk_name = cursor.getString(1);
				   // int  old_gold  = cursor.getInt(2);
				    int  gold = cursor.getInt(3);
				    JSONObject json = new JSONObject();
				    try {
				    	json.put("apk_name", apk_name);
						//json.put("old_gold", old_gold);
						if (apk_name.contains("BBPAW_USER")) {
							json.put("gold", 0);
						} else {
							json.put("gold", gold);
						}
						json.put("game_email", user_account);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				    arrayGold.put(json);
			}
			 System.out.println("array=="+arrayGold.toString());
			 new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mGoldHashMap = new HashMap<String, String>();
						mGoldHashMap.put("param", "gold_system");
						mGoldHashMap.put("input",  arrayGold.toString());
						String result  = HttpUtils.postRequest(COMMIT_DATA_URL, mGoldHashMap, context);
					    System.out.println("--GoldData commit---"+result);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public static SQLiteDatabase getDataBase(String databasePath){
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
		if(database != null){
			return database;
		}
		return  null;
	}
	
	public static void deletDataBase(String tableName, String databasePath) {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
		database.delete(tableName, null, null);
		System.out.println("---------cursorDataGameAtion----deletDataBase---->>>");
		//database.deleteDatabase(file);
	};
	
}
