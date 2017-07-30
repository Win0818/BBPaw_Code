package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.HttpResponseCallBack;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager.InformMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.model.NotifyMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;

public class Common {

	private static final String TAG = "--Common--";

	public static String getDeviceId(Context context) {
		String deviceID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
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

	@SuppressLint("NewApi")
	public static String getUserPassword(Context context) {
		String pwd = "";
		try {
			Context launcherContext = context.createPackageContext(
					"com.worldchip.bbp.ect", Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences pref = launcherContext.getSharedPreferences(
					"password_info", Context.MODE_MULTI_PROCESS);
			pwd = pref.getString("password", "");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pwd;
	}

	@SuppressLint("NewApi")
	public static boolean setUserPassword(Context context, String pwd) {
		try {
			Settings.Global.putString(context.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_RADIOS, pwd);
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
		return true;
	}

	public static void notifyMessageClick(Context context, Information info) {
		int operation = info.getClickOperation();
		String operationValue = info.getOperationValue();
		switch (operation) {
		case NotifyMessage.OPERATION_OPEN_APP:
		case NotifyMessage.OPERATION_OPEN_ACTIVITY:
			if (!TextUtils.isEmpty(operationValue)) {
				String packageName = "";
				String activityName = "";
				String[] nameArr = operationValue.trim().split(",");
				if (nameArr.length >= 2) {
					packageName = nameArr[0];
					activityName = nameArr[1];
				} else if (nameArr.length > 0) {
					packageName = nameArr[0];
				} else {
					Toast.makeText(context, R.string.start_app_error,
							Toast.LENGTH_SHORT).show();
				}
				startApp(context, packageName, activityName);
			} else {
				Toast.makeText(context, R.string.start_app_error,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case NotifyMessage.OPERATION_OPEN_URL:
			openBBPawBrowser(context, operationValue);
			break;
		case NotifyMessage.OPERATION_CUSTOM:

			break;
		}

	}

	public static void startApp(Context context, String packageName,
			String activityName) {

		if (TextUtils.isEmpty(packageName))
			return;

		try {
			Intent intent = null;
			if (TextUtils.isEmpty(activityName)) {
				intent = context.getPackageManager().getLaunchIntentForPackage(
						packageName);
			} else {
				intent = new Intent();
				intent.setComponent(new ComponentName(packageName, activityName));
			}
			if (intent == null) {
				intent = getLaunchIntentForNoCategory(context, packageName);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, R.string.start_app_error,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, R.string.start_app_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	public static Intent getLaunchIntentForNoCategory(Context context,
			String packageName) {
		Intent intent = null;

		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageinfo = null;
		try {
			packageinfo = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return null;
		}
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.setPackage(packageinfo.packageName);
		List<ResolveInfo> resolveinfoList = packageManager
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			String className = resolveinfo.activityInfo.name;
			intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
		}
		return intent;
	}

	public static void openBBPawBrowser(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.worldchip.bbp.ect",
					"com.worldchip.bbp.ect.activity.BrowserActivity"));
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 重新开一个栈
			intent.putExtra("address", url);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, R.string.start_bbpaw_browser_error,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(context, R.string.start_bbpaw_browser_error,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public static void systemUpgrade(Context context) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName comp = new ComponentName("com.softwinner.update",
					"com.softwinner.update.UpdateActivity");
			intent.setComponent(comp);
			intent.setAction("android.intent.action.VIEW");
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hideKeyboard(Context context, View view) {
		if (context == null || view == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean initVaccineDefaultDatas(Context context) {
		try {
			List<VaccineInfo> vaccineList = XmlReader
					.pullVeccineDefault(context);
			if (vaccineList == null || vaccineList.size() < 1) {
				Log.e(TAG, "initVaccineDefaultDatas is null !");
				return false;
			}

			DBHelper helper = new DBHelper(context);
			helper.deleteTable(DBHelper.VACCINE_TABLE);
			ContentValues values = null;
			for (VaccineInfo vaccineItem : vaccineList) {
				if (vaccineItem == null) {
					continue;
				}
				values = new ContentValues();
				values.put(Vaccine.AGE, vaccineItem.getAge());
				values.put(Vaccine.AGE_TITLE, vaccineItem.getAgeTitle());
				values.put(Vaccine.VACCINE_TYPE_NAME,
						vaccineItem.getVaccineTypeName());
				values.put(Vaccine.VACCINE_TYPE, vaccineItem.getVaccineType());
				values.put(Vaccine.VACCINE_EXPLAIN,
						vaccineItem.getVaccineExplain());
				values.put(Vaccine.DATE, vaccineItem.getDate());
				helper.insertValues(DBHelper.VACCINE_TABLE, values);
			}
			helper.close();
			return true;
		} catch (Exception err) {
			Log.e(TAG, "initSetting...err.getmessage=" + err.getMessage());
			err.printStackTrace();
		}
		return false;
	}

	public static List<VaccineInfo> getVaccineDatas(Context context) {
		List<VaccineInfo> datas = new ArrayList<VaccineInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = null;
		try {
			cursor = helper.getCursor(DBHelper.VACCINE_TABLE, "");
			if (cursor == null) {
				return datas;
			}
			VaccineInfo vaccine = null;
			while (cursor.moveToNext()) {
				vaccine = new VaccineInfo();
				vaccine.setAge(cursor.getString(cursor
						.getColumnIndex(Vaccine.AGE)));
				vaccine.setAgeTitle(cursor.getString(cursor
						.getColumnIndex(Vaccine.AGE_TITLE)));
				vaccine.setVaccineTypeName(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_TYPE_NAME)));
				vaccine.setVaccineType(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_TYPE)));
				vaccine.setVaccineExplain(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_EXPLAIN)));
				vaccine.setDate(cursor.getString(cursor
						.getColumnIndex(Vaccine.DATE)));
				datas.add(vaccine);
			}
			if (cursor != null) {
				cursor.close();
			}
			helper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			helper.close();
		}
		return datas;
	}

	public static void resetVaccineDates(List<VaccineInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			VaccineInfo info = list.get(i);
			if (info != null) {
				info.setDate("");
				info.setChecked(false);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	public static void calculateVaccineDates(List<VaccineInfo> list, String date) {
		if (list == null || list.isEmpty())
			return;
		int year = 0;
		int month = 0;
		int day = 0;
		try {
			String[] split = date.split("-");
			year = Integer.valueOf(split[0]);
			month = Integer.valueOf(split[1]);
			day = Integer.valueOf(split[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < list.size(); i++) {
			VaccineInfo info = list.get(i);
			if (info != null) {
				String ageStr = info.getAge();
				if (!TextUtils.isEmpty(ageStr)) {
					String[] ageSplit = ageStr.trim().split("-");
					StringBuffer dateSB = new StringBuffer();
					for (int j = 0; j < ageSplit.length; j++) {
						int age = Integer.valueOf(ageSplit[j]);
						String nextDate = getNextDate(year, month, age, day);
						if (j == 0) {
							dateSB.append(nextDate);
						} else {
							dateSB.append(" ~ ");
							dateSB.append(nextDate);
						}
					}
					info.setDate(dateSB.toString());
				}
			}
		}
	}

	public static String getNextDate(int year, int month, int jumpMonth, int day) {
		int stepYear = year;
		int stepMonth = month + jumpMonth;
		if (stepMonth > 0) {
			if (stepMonth % 12 == 0) {
				stepYear = year + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			stepYear = year - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
		}
		return String.valueOf(stepYear) + "-" + String.valueOf(stepMonth) + "-"
				+ day;
	}

	public static void replyStateToServer(Information info, int replyState) {
		if (info != null) {
			String whereStr = "where msg_id = ";
			Cursor cursor = null;
			DBHelper dbHelper = null;
			try {
				dbHelper = new DBHelper(MyApplication.getAppContext());
				cursor = dbHelper.getCursor(DBHelper.INFROM_MESSAGE_TABLE,
						whereStr + info.getMsgId());
				if (cursor != null && cursor.moveToNext()) {
					String state = cursor.getString(cursor
							.getColumnIndex(InformMessage.STATE));
					if (Integer.valueOf(state) < replyState) {
						DataManager.updateInformation(
								MyApplication.getAppContext(),
								InformMessage.STATE,
								String.valueOf(replyState),
								InformMessage.MSG_ID,
								String.valueOf(info.getMsgId()));

						String httpUrl = Utils.HTTP_REPLY_STATE_REQ_URL
								.replace("MAINTYPE",
										String.valueOf(info.getMainType()))
								.replace("ORDER_TYPE",
										String.valueOf(info.getMsgType()))
								.replace("ID", String.valueOf(info.getMsgId()))
								.replace("OPERATION",
										String.valueOf(replyState));
						Log.e("lee",
								"replyStateToServer ---------- httpUrl == "
										+ httpUrl);
						HttpUtils.doPost(httpUrl, null,
								Utils.HTTP_TAG_REPLY_STATE);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception err) {
				err.printStackTrace();
			} finally {
				if (dbHelper != null) {
					dbHelper.close();
					dbHelper = null;
				}
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
	}

	public static void startPushServiceAlarm(Context context) {
		Log.e(TAG, "sendPushServiceAlarm ...!");
		cancelPushServiceAlarm(context);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 10 * 1000;
		Intent intent = new Intent(Utils.PUSH_MESSAGE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		alarm.setRepeating(alarmType, firstTime,
				Utils.CHECK_SERVICE_RUNNING_TIMEING, pendingIntent);
	}

	public static void cancelPushServiceAlarm(Context context) {
		Intent intent = new Intent(Utils.PUSH_MESSAGE_ACTION);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi);
	}

	public static void sendReceivePushMessageBroadcast(Context context,
			String messageType) {
		Intent intent = new Intent(Utils.RECEIVE_PUSH_MESSAGE_ACTION);
		intent.putExtra(Utils.RECEIVE_PUSH_MESSAGE_TYPE_KEY, messageType);
		context.sendBroadcast(intent);
	}

	public static boolean vaccineSettingsAlreadyExists() {
		List<VaccineInfo> vaccineDatas = getVaccineDatas(MyApplication
				.getAppContext());
		for (int i = 0; i < vaccineDatas.size(); i++) {
			if (!TextUtils.isEmpty(vaccineDatas.get(i).getDate())) {
				return true;
			}
		}
		return false;
	}

	public static boolean resetVaccineSettings(List<VaccineInfo> datas) {
		VaccineAlarmUtils.cancelAllVaccineAlarm(MyApplication.getAppContext());
		clearAllVaccineSettings();
		return saveVaccineSettings(datas);
	}

	public static void clearAllVaccineSettings() {
		DBHelper helper = null;
		Cursor cursor = null;
		try {
			helper = new DBHelper(MyApplication.getAppContext());
			helper.deleteTable(DBHelper.VACCINE_TABLE);
			Common.initVaccineDefaultDatas(MyApplication.getAppContext());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (helper != null) {
				helper.close();
				helper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public static boolean saveVaccineSettings(List<VaccineInfo> datas) {
		if (datas == null || datas.isEmpty()) {
			return false;
		}
		DBHelper helper = null;
		try {
			helper = new DBHelper(MyApplication.getAppContext());
			ContentValues values = null;
			for (VaccineInfo vaccineItem : datas) {
				if (vaccineItem != null && vaccineItem.isChecked()) {
					String vaccineType = vaccineItem.getVaccineType();
					String date = vaccineItem.getDate();
					values = new ContentValues();

					if (!TextUtils.isEmpty(date)) {
						date = date.split("~")[0];
					}
					date = date + " " + Utils.DEFAULT_VACCINE_TIME;
					String date2TimeStamp = date2TimeStamp(date);

					// test code-----
					// long testDate2TimeStamp = System.currentTimeMillis() + 1
					// * 1000 * 60 * (Integer.valueOf(vaccineType)+1);
					// String timeStamp2Date =
					// timeStamp2Date(String.valueOf(testDate2TimeStamp));

					values.put(Vaccine.DATE, date2TimeStamp);
					boolean flag = helper.updateValues(DBHelper.VACCINE_TABLE,
							values, "vaccineType=" + vaccineType);
					if (flag) {
						if (!TextUtils.isEmpty(date2TimeStamp)
								&& compare2CurrDate(Common
										.timeStamp2Date(date2TimeStamp))) {
							VaccineAlarmUtils.creatVaccineAlarm(MyApplication
									.getAppContext(), Long
									.parseLong(date2TimeStamp), Integer
									.valueOf(vaccineType).intValue());
						}
						// test code
						// VaccineAlarmUtils.creatVaccineAlarm(MyApplication.getAppContext(),
						// testDate2TimeStamp,
						// Integer.valueOf(vaccineType).intValue());
					}
				}
			}
			helper.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (helper != null) {
				helper.close();
				helper = null;
			}
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public static String date2TimeStamp(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					Utils.VACCINE_ALARM_TIME_FORMAT);
			return String.valueOf(sdf.parse(date).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressLint("SimpleDateFormat")
	public static String timeStamp2Date(String timeStamp) {
		if (TextUtils.isEmpty(timeStamp)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(
				Utils.VACCINE_ALARM_TIME_FORMAT);
		return sdf.format(new Date(Long.valueOf(timeStamp)));
	}

	@SuppressLint("SimpleDateFormat")
	public static String timeStamp2Date(String timeStamp, String dateFormat) {
		if (TextUtils.isEmpty(timeStamp)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(new Date(Long.valueOf(timeStamp)));
	}

	@SuppressLint("SimpleDateFormat")
	public static boolean compare2CurrDate(String date) {
		DateFormat df = new SimpleDateFormat(Utils.VACCINE_ALARM_TIME_FORMAT);
		try {
			Date currDate = new Date();
			Date compareDate = df.parse(date);
			if (compareDate.getTime() >= currDate.getTime()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static VaccineInfo getVaccineAlarmByType(int vaccineType) {
		DBHelper helper = null;
		Cursor cursor = null;
		VaccineInfo info = null;
		try {
			helper = new DBHelper(MyApplication.getAppContext());
			String whereStr = " where vaccineType=" + "'" + vaccineType + "'";
			cursor = helper.getCursor(DBHelper.VACCINE_TABLE, whereStr);
			if (cursor != null && cursor.moveToFirst()) {
				info = new VaccineInfo();
				info.setAge(cursor.getString(cursor.getColumnIndex(Vaccine.AGE)));
				info.setAgeTitle(cursor.getString(cursor
						.getColumnIndex(Vaccine.AGE_TITLE)));
				info.setDate(cursor.getString(cursor
						.getColumnIndex(Vaccine.DATE)));
				info.setVaccineExplain(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_EXPLAIN)));
				info.setVaccineType(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_TYPE)));
				info.setVaccineTypeName(cursor.getString(cursor
						.getColumnIndex(Vaccine.VACCINE_TYPE_NAME)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (helper != null) {
				helper.close();
				helper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return info;
	}

	public static boolean removeVaccineAlarmByType(int vaccineType) {
		DBHelper helper = null;
		Cursor cursor = null;
		try {
			helper = new DBHelper(MyApplication.getAppContext());
			ContentValues values = new ContentValues();
			values.put(Vaccine.DATE, "");
			return helper.updateValues(DBHelper.VACCINE_TABLE, values,
					"vaccineType=" + vaccineType);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (helper != null) {
				helper.close();
				helper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return false;
	}

	public static void saveEyecareSettingsPreferences(String key, String value) {
		Shared.getInstance(MyApplication.getAppContext()).putString(
				Configure.EYECARE_SETTINGS_SHARED_NAME,
				Configure.EYECARE_SETTINGS_SHARED_MODE, key, value);
	}

	public static String getEyecareSettingsPreferences(String key,
			String defValue) {
		return Shared.getInstance(MyApplication.getAppContext()).getString(
				Configure.EYECARE_SETTINGS_SHARED_NAME,
				Configure.EYECARE_SETTINGS_SHARED_MODE, key, defValue);
	}

	public static boolean isPushServiceRunning() {
		boolean isServiceRunning = false;
		// 检查Service状态
		ActivityManager manager = (ActivityManager) MyApplication
				.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo service : runningServices) {
			if ("com.worldchip.bbp.bbpawmanager.cn.push.MessagePushService"
					.equals(service.service.getClassName())) {
				if (service.pid != 0) {// pid不为0时,即该服务进程正在运行
					isServiceRunning = true;
				}
			}
		}
		return isServiceRunning;
	}

	public static void handleCreatEyecareAlarm(Context context) {
		sendEyeChangeBroadcast(context);
		String screenTimeSwitch = Common.getEyecareSettingsPreferences(
				Utils.EYECARE_SCREEN_TIME_PRE_KEY, "false");
		if (Boolean.parseBoolean(screenTimeSwitch)) {

			String useBeginTime = Common.getEyecareSettingsPreferences(
					Utils.EYECARE_DAY_TIME_BEGIN_PRE_KEY,
					Configure.DEFAULT_USE_BEGIN_TIME);
			String useEndTime = Common.getEyecareSettingsPreferences(
					Utils.EYECARE_DAY_TIME_END_PRE_KEY,
					Configure.DEFAULT_USE_END_TIME);

			Date currentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentDate);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			boolean useBeginTimeCompare2CurrDate = compare2CurrDate(
					useBeginTime, hour, minute);
			boolean useEndTimeCompare2CurrDate = compare2CurrDate(useEndTime,
					hour, minute);

			String useTimeValue = Common.getEyecareSettingsPreferences(
					Utils.EYECARE_USE_TIME_PRE_KEY, "5");
			String sleepTimeValue = Common.getEyecareSettingsPreferences(
					Utils.EYECARE_SLEEP_TIME_PRE_KEY, "5");
			int useTime = Integer.parseInt(useTimeValue) * 60;
			long delayTime = 0;
			int showTime = Integer.parseInt(sleepTimeValue) * 60 * 1000;
			if (!useBeginTimeCompare2CurrDate && useEndTimeCompare2CurrDate) {
				long diffTime = getDiffTime(useEndTime, hour, minute);
				if (diffTime >= useTime) {
					delayTime = useTime;
				} else {
					delayTime = diffTime;
				}
			} else if (useBeginTimeCompare2CurrDate) {
				delayTime = 0;
				long diffTime = getDiffTime(useBeginTime, hour, minute);
				showTime = (int) diffTime;
			} else if (!useEndTimeCompare2CurrDate) {
				delayTime = 0;
				long diffEnd = Math
						.abs(getDiffTime(hour + ":" + minute, 24, 0));
				long diffBegin = Math.abs(getDiffTime(useBeginTime, 0, 0));
				showTime = (int) (diffBegin + diffEnd);
			}

			// long firstTime = SystemClock.elapsedRealtime();
			long firstTime = System.currentTimeMillis();
			firstTime += delayTime * 1000;
			// test time
			// firstTime += 20 * 1000;
			Common.creatEyecareUseTimeAlarm(context, firstTime, showTime);
		}
	}

	public static void sendEyeChangeBroadcast(Context context) {
		context.sendBroadcast(new Intent(Utils.EYECARE_ALARM_CHANGE_ACTION));
	}

	private static long getDiffTime(String dateStr, int hour, int minute) {
		if (dateStr == null && TextUtils.isEmpty(dateStr)) {
			return 0;
		}
		long date2Seconds = date2Seconds(dateStr);
		long currDateSeconds = (hour * 60 + minute) * 60;
		return date2Seconds - currDateSeconds;
	}

	/**
	 * 与某一时间比较
	 * 
	 * @param dateStr
	 *            比较时间 格式为HH:mm
	 * @param hour
	 *            被比较小时
	 * @param minute
	 *            被比较分钟
	 * @return true 大于被比较时间
	 */
	public static boolean compare2CurrDate(String dateStr, int hour, int minute) {
		if (dateStr == null && TextUtils.isEmpty(dateStr)) {
			return false;
		}
		String[] dateSplit = dateStr.split(":");
		int dateHour = Integer.parseInt(dateSplit[0]);
		int dateMinute = Integer.parseInt(dateSplit[1]);
		if (dateHour > hour) {
			return true;
		} else {
			if (dateHour == hour && dateMinute > minute) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 
	 * @param date
	 *            format HH:mm
	 * @return
	 */
	public static long date2Seconds(String date) {
		if (date != null && date != "") {
			String[] dateSplit = date.split(":");
			int hour = Integer.parseInt(dateSplit[0]);
			int minute = Integer.parseInt(dateSplit[1]);
			return (hour * 60 + minute) * 60;
		}
		return 0;
	}

	public static void creatEyecareUseTimeAlarm(Context context,
			long triggerAtMillis, int showTime) {
		// TODO Auto-generated method stub
		cancelEyecareUseTimeAlarm(context);
		Log.e("Common",
				" creatEyecareUseTimeAlarm at "
						+ Common.timeStamp2Date(String.valueOf(triggerAtMillis))
						+ " show_time == " + showTime);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(Utils.REST_ALARM_ACTION);
		intent.putExtra("show_time", showTime);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

	}

	public static void cancelEyecareUseTimeAlarm(Context context) {
		Intent intent = new Intent(Utils.REST_ALARM_ACTION);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pi);
	}

	public static boolean isSendDeviceId() {
		SharedPreferences preferences = MyApplication.getAppContext()
				.getSharedPreferences(Utils.SHARED_PREFERENCE_NAME, 0);
		return preferences.getBoolean(Utils.SHARED_SENDDEVICEID_KEY, true);
	}

	public static void sendDeviceId2Server() {
		String deviceId = getCpuSerial(MyApplication.getAppContext());
		String httpUrl = Utils.HTTP_SEND_DEIVCEID2SERVER_REQ_URL.replace(
				"DEVICE_ID", deviceId);
		String language = Locale.getDefault().getLanguage() + "_"
				+ Locale.getDefault().getCountry();
		httpUrl = httpUrl + "&language=" + language;
		// Log.e("xiaolp","httpUrl = "+httpUrl);
		HttpUtils.doPost(httpUrl, new HttpResponseCallBack() {
			@Override
			public void onSuccess(String result, String httpTag) {
				// TODO Auto-generated method stub
				Log.e("lee", "sendDeviceId2Server result == " + result);
				SharedPreferences preferences = MyApplication.getAppContext()
						.getSharedPreferences(Utils.SHARED_PREFERENCE_NAME, 0);
				preferences.edit()
						.putBoolean(Utils.SHARED_SENDDEVICEID_KEY, false)
						.commit();
			}

			@Override
			public void onStart(String httpTag) {
			}

			@Override
			public void onFinish(int result, String httpTag) {
			}

			@Override
			public void onFailure(Exception e, String httpTag) {
			}
		}, Utils.HTTP_TAG_SEND_DEVICEID);
	}

	/**
	 * 验证当前时间是否处于有效使用时间
	 * 
	 * @return true 处于有效使用时间
	 */
	public static boolean validationCurrTime2UseTime() {
		Date currentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String useBeginTime = Common.getEyecareSettingsPreferences(
				Utils.EYECARE_DAY_TIME_BEGIN_PRE_KEY,
				Configure.DEFAULT_USE_BEGIN_TIME);
		String useEndTime = Common.getEyecareSettingsPreferences(
				Utils.EYECARE_DAY_TIME_END_PRE_KEY,
				Configure.DEFAULT_USE_END_TIME);
		boolean useBeginTimeCompare2CurrDate = compare2CurrDate(useBeginTime,
				hour, minute);
		boolean useEndTimeCompare2CurrDate = compare2CurrDate(useEndTime, hour,
				minute);
		if (!useBeginTimeCompare2CurrDate && useEndTimeCompare2CurrDate) {
			return true;
		} else {
			return false;
		}
	}

	public static int getResourcesId(Context context, String resName,
			String resTpye) {
		String language = Locale.getDefault().getLanguage();
		String resString = resName + "_eng";
		if (language.contains("zh")) {
			resString = resName + "_cn";
		} else {
			resString = resName + "_eng";
		}
		return context.getResources().getIdentifier(resString, resTpye,
				context.getPackageName());
	}

}
