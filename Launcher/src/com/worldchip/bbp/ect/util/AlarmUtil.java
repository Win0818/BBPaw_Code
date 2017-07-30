package com.worldchip.bbp.ect.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.receiver.AlarmReceiver;

public class AlarmUtil {
	private static final String TAG = "--AlarmUtil--";
	public static final String BBPAW_ALARM_ACTION = "com.worldchip.bbpaw.alarm.Action";
	
	public static void setAlarm(Context context, AlarmInfo alarm,
			boolean enable, int today) {
		Log.e(TAG, "alarmInfo=" + alarm.getMusutes());
		ClockData.enableAlarm(context, alarm.getId(), enable);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.putExtra("alarmInfo", alarm);
		intent.setClass(context, AlarmReceiver.class);

		PendingIntent pi = PendingIntent.getBroadcast(context,
				AlarmUtil.getRequestCode(alarm.getId(), today), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		if (enable) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_WEEK, today);
			c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
			c.set(Calendar.MINUTE, alarm.getMusutes());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);

			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

		} else {
			am.cancel(pi);
			intent = null;
			pi = null;
			am = null;
		}
	}

	public static void setAlarmAdayLater(Context context, AlarmInfo alarm,
			Boolean isCancel, int id, int days) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent();
		intent.putExtra("alarmInfo", alarm);

		intent.setClass(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context,
				getRequestCode(id, 24), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (isCancel) {
			am.cancel(pi);
		}
		Calendar c = Calendar.getInstance();
		// c.add(Calendar.DATE, days);
		c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
		c.set(Calendar.MINUTE, alarm.getMusutes());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

	}

	public static void setAlarm(Context context, AlarmInfo alarm, int today,
			Boolean isCancel, int id) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent();
		intent.putExtra("alarmInfo", alarm);

		intent.setClass(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context,
				getRequestCode(id, today), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (isCancel) {
			am.cancel(pi);
			intent = null;
			pi = null;
			am = null;
		} else {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_WEEK, today);
			c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
			c.set(Calendar.MINUTE, alarm.getMusutes());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		}

	}

	public static int getRequestCode(int id, int day) {

		String code = "" + id + day;
		int requestCode = Integer.valueOf(code);
		return requestCode;
	}
	
	public static void createAlarm(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		int[] weeks = HttpCommon.stringToInts(alarmInfo.getDaysofweek());
		if (weeks != null) {
			createAlarmSet(context, alarmInfo);
		} else {
			createOnceAlarm(context, alarmInfo);
		}
	}
	
	public static void cancleAlarm(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		int[] weeks = HttpCommon.stringToInts(alarmInfo.getDaysofweek());
		if (weeks != null) {
			cancleAlarmSet(context, alarmInfo);
		} else {
			int requestCode = getRequestCode(alarmInfo.getId(), 24);
			cancleAlarm(context, requestCode);
		}
	}
	
	private static void createAlarmSet(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		
		int[] weeks = HttpCommon.stringToInts(alarmInfo.getDaysofweek());
		Calendar currCalendar = Calendar.getInstance();
		int today = currCalendar.get(Calendar.DAY_OF_WEEK);
		int currWeekOfYear = currCalendar.get(Calendar.WEEK_OF_YEAR);
		Integer hours = alarmInfo.getHours();
		Integer musutes = alarmInfo.getMusutes();
		java.text.DecimalFormat df = new java.text.DecimalFormat("00");
		String time = df.format(hours) + ":" + df.format(musutes)+":00";
		boolean moreThanCurrTime = isThanTime(time.trim());
		
		if (weeks != null) {
			for(int i = 0; i < weeks.length; i++) {
				int requestCode = getRequestCode(alarmInfo.getId(), weeks[i]);
				cancleAlarm(context, requestCode);
				int week = weeks[i];
				int finalWeekOfYear = currWeekOfYear;
				if (today > week)  {
					finalWeekOfYear = currWeekOfYear + 1;
				} else if (today == week) {
					if (!moreThanCurrTime) {
						finalWeekOfYear = currWeekOfYear + 1;
					}
				}
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_WEEK, week);
				calendar.set(Calendar.WEEK_OF_YEAR, finalWeekOfYear);
				calendar.set(Calendar.HOUR_OF_DAY, hours);
				calendar.set(Calendar.MINUTE, musutes);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(BBPAW_ALARM_ACTION);
				intent.putExtra("alarmInfo", alarmInfo);
				long alarmTimeStamp = calendar.getTimeInMillis();
				intent.putExtra("alarmTimeStamp", alarmTimeStamp);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
						intent, PendingIntent.FLAG_UPDATE_CURRENT);
				Log.e("lee", "creatAlarmSet requestCode == "+requestCode);
				Log.e("lee", "create alarm by == "+timeStamp2Date(String.valueOf(alarmTimeStamp)));
				alarm.set(AlarmManager.RTC_WAKEUP, alarmTimeStamp, pendingIntent);
			}
		}
	}
	
	public static void cancleAlarmSet(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		int[] weeks = HttpCommon.stringToInts(alarmInfo.getDaysofweek());
		if (weeks != null) {
			for(int i = 0; i < weeks.length; i++) {
				int requestCode = getRequestCode(alarmInfo.getId(), weeks[i]);
				cancleAlarm(context, requestCode);
			}
		}
	}
	
	private static void createOnceAlarm(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		
		Calendar calendar = Calendar.getInstance();
		int today = calendar.get(Calendar.DAY_OF_WEEK);
		Integer hours = alarmInfo.getHours();
		Integer musutes = alarmInfo.getMusutes();
		java.text.DecimalFormat df = new java.text.DecimalFormat("00");
		String time = df.format(hours) + ":" + df.format(musutes)+":00";
		boolean moreThanCurrTime = isThanTime(time.trim());
		int week = today;
		int requestCode = getRequestCode(alarmInfo.getId(), 24);
		cancleAlarm(context, requestCode);
		if (!moreThanCurrTime) {
			week = today + 1;
		}
		calendar.set(Calendar.DAY_OF_WEEK, week);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, musutes);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(BBPAW_ALARM_ACTION);
		intent.putExtra("alarmInfo", alarmInfo);
		long alarmTimeStamp = calendar.getTimeInMillis();
		intent.putExtra("alarmTimeStamp", alarmTimeStamp);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.e("lee", "createOnceAlarm requestCode == "+requestCode);
		Log.e("lee", "create  OnceAlarm by == "+timeStamp2Date(String.valueOf(alarmTimeStamp)));
		alarm.set(AlarmManager.RTC_WAKEUP, alarmTimeStamp, pendingIntent);
	}
	
	public static void cancleAlarm(Context context,int requestCode) {
		Log.e("lee", "cancel alarm requestCode == "+requestCode);
		Intent intent = new Intent(BBPAW_ALARM_ACTION);
		PendingIntent pi=PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi); 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String timeStamp2Date(String timeStamp) {
		if (TextUtils.isEmpty(timeStamp)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(Long.valueOf(timeStamp)));
	}
	 
	public static void initAllAlarmByBootCompleted() {
		List<AlarmInfo> listAlarm = ClockData.getAllClockAlarm(MyApplication.getAppContext());
		if (listAlarm != null && listAlarm.size() >0) {
			for (int i =0; i<listAlarm.size();i++) {
				AlarmInfo alarmInfo = listAlarm.get(i);
				Integer enabled = alarmInfo.getEnabled();
				if (enabled == 1) {
					createAlarm(MyApplication.getAppContext(), alarmInfo);
				}
			}
		}
	}
	
	public static void createNextAlarm(Context context, AlarmInfo alarmInfo) {
		if (alarmInfo == null) {
			return;
		}
		String daysofweek = alarmInfo.getDaysofweek();
		if (daysofweek != null && !TextUtils.isEmpty(daysofweek)) {
			Calendar currCalendar = Calendar.getInstance();
			int currWeekOfYear = currCalendar.get(Calendar.WEEK_OF_YEAR);
			int today = currCalendar.get(Calendar.DAY_OF_WEEK);
			
			Integer hours = alarmInfo.getHours();
			Integer musutes = alarmInfo.getMusutes();
			int requestCode = getRequestCode(alarmInfo.getId(), today);
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_WEEK, today);
			calendar.set(Calendar.WEEK_OF_YEAR, currWeekOfYear + 1);
			calendar.set(Calendar.HOUR_OF_DAY, hours);
			calendar.set(Calendar.MINUTE, musutes);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(BBPAW_ALARM_ACTION);
			intent.putExtra("alarmInfo", alarmInfo);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			Log.e("lee", "createNextAlarm requestCode == "+requestCode);
			Log.e("lee", "create  createNextAlarm by == "+timeStamp2Date(String.valueOf(calendar.getTimeInMillis())));
			alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		} else {
			
			ClockData.enableAlarm(MyApplication.getAppContext(), alarmInfo.getId(), false);
		}
	}
	
	/**
	 * �ж��Ƿ���ڵ�ǰʱ��
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isThanTime(String time) {
		boolean flag = false;
		try {
			Date date = new Date();
			Log.e(TAG, date.toString());
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			String str = formatter.format(date);// ��ǰʱ��
			Date alarmDate = formatter.parse(time);
			Date currentDate = formatter.parse(str);
			long alarmTime = alarmDate.getTime();// ����ʱ��ĺ���
			long currentTime = currentDate.getTime();// ��ǰʱ��ĺ���
			Log.e(TAG, "alarmTime=" + alarmTime + " currentTime=" + currentTime);
			if (alarmTime >= currentTime) {
				flag = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return flag;
	}

	@SuppressLint("SimpleDateFormat")
	public static boolean isShowAlarmDialog(long timeStamp) {
		try {
			Date currDate = new Date();
			long currTimeStamp = currDate.getTime();
			Log.e("lee", " compare2CurrTimeStamp timeStamp == "+timeStamp +" currTimeStamp == "+currTimeStamp);
			if (timeStamp >= currTimeStamp - 5000) {//���յ��㲥����һ�����ӳ٣�������Ҫ��ȥһ����ֵ�ŶԱ�
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
