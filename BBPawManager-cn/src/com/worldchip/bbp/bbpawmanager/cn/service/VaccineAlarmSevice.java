package com.worldchip.bbp.bbpawmanager.cn.service;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.VaccineAlarmUtils;
import com.worldchip.bbp.bbpawmanager.cn.view.VaccineAlarmDialog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class VaccineAlarmSevice extends Service {

	private static final String	ACTION_START = "START";
	private static final String	ACTION_STOP = "STOP";
	private VaccineAlarmDialog mAlarmDialog;
	
	public static void startVaccineAlarm(Context context, int vaccineType) {
		Intent intent = new Intent(context, VaccineAlarmSevice.class);
		intent.putExtra(Vaccine.VACCINE_TYPE, vaccineType);
		intent.setAction(ACTION_START);
		context.startService(intent);
	}

	public static void stopVaccineAlarm(Context context) {
		Intent intent = new Intent(context, VaccineAlarmSevice.class);
		intent.setAction(ACTION_STOP);
		context.startService(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (ACTION_START.equals(intent.getAction())) {
			int vaccineType = intent.getIntExtra(Vaccine.VACCINE_TYPE, -1);
			if (vaccineType != -1) {
				showVaccineAlarmDialog(vaccineType);
			}
		} else if (ACTION_STOP.equals(intent.getAction())) {
			stopSelf();
		}

		return Service.START_NOT_STICKY;
	}

	
	private void showVaccineAlarmDialog(int vaccineType) {
		if (mAlarmDialog == null) {
			mAlarmDialog = VaccineAlarmDialog.createDialog(getApplicationContext());
		}
		if (mAlarmDialog != null && !mAlarmDialog.isShowing()) {
			mAlarmDialog.setVaccine(Common.getVaccineAlarmByType(vaccineType));
			mAlarmDialog.showVaccineAlarmDialog();
		} else {
			Intent intent = new Intent(VaccineAlarmUtils.VACCINE_ALARM_UPDATE_ACTION);
			intent.putExtra(Vaccine.VACCINE_TYPE, vaccineType);
			sendBroadcast(intent);
		}
	}
	
	private void dismissVaccineAlarmDialog() {
		if (mAlarmDialog != null && mAlarmDialog.isShowing()) {
			mAlarmDialog.dismiss();
		}
		mAlarmDialog = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		dismissVaccineAlarmDialog();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
