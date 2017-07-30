package com.worldchip.bbp.bbpawmanager.cn.utils;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class VaccineAlarmUtils {

	public static final String VACCINE_ALARM_SHOW_ACTION = "vaccine_alarm_show";
	public static final String VACCINE_ALARM_UPDATE_ACTION = "vaccine_alarm_update";
	
	
	public static void creatVaccineAlarm(Context context, long triggerAtMillis,int vaccineType) {
		Log.e("lee", "create alarm by == "+Common.timeStamp2Date(String.valueOf(triggerAtMillis)));
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(Utils.VACCINE_ALARM_ACTION);
		intent.putExtra(Vaccine.VACCINE_TYPE, vaccineType);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, vaccineType,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
	}
	
	public static void cancelVaccineAlarm(Context context,int vaccineType) {
		Log.e("lee", "cancel alarm type == "+vaccineType);
		Intent intent = new Intent(Utils.VACCINE_ALARM_ACTION);
		PendingIntent pi=PendingIntent.getBroadcast(context, vaccineType, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi); 
	}
	
	public static void cancelAllVaccineAlarm(Context context) {
		DBHelper helper = new DBHelper(context);
		Cursor cursor = null;
		try {
			cursor = helper.getCursor(DBHelper.VACCINE_TABLE, "");
			if (cursor == null) {
				return;
			}
			while (cursor.moveToNext()) {
				String vaccineType = cursor.getString(cursor.getColumnIndex(Vaccine.VACCINE_TYPE));
				cancelVaccineAlarm(context, Integer.parseInt(vaccineType));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
	}
}
