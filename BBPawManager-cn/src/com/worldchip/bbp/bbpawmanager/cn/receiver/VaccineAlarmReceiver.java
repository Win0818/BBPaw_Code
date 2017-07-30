package com.worldchip.bbp.bbpawmanager.cn.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper.Vaccine;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;
import com.worldchip.bbp.bbpawmanager.cn.service.VaccineAlarmSevice;
import com.worldchip.bbp.bbpawmanager.cn.utils.AlarmAlertWakeLock;
import com.worldchip.bbp.bbpawmanager.cn.utils.AsyncHandler;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.utils.VaccineAlarmUtils;
import com.worldchip.bbp.bbpawmanager.cn.view.VaccineAlarmDialog;

public class VaccineAlarmReceiver extends BroadcastReceiver {
	
    private static final String TAG = "VaccineAlarmReceiver";
    //private VaccineAlarmDialog mVaccineAlarmDialog = null;
    
	@Override
    public void onReceive(final Context context, final Intent intent) {
		final PendingResult result = goAsync();
        final PowerManager.WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                handleIntent(context, intent);
                result.finish();
                wl.release();
            }
        });
    }
	
	private void handleIntent(Context context, Intent intent) {
		final String action = intent.getAction();
		if (Utils.VACCINE_ALARM_ACTION.equals(action)) {
			int vaccineType = intent.getIntExtra(Vaccine.VACCINE_TYPE, -1);
			alertNotification(context,vaccineType);
		} else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			Log.e("lee", " VaccineAlarmReceiver ACTION_BOOT_COMPLETED -----------------");
			creatAllVaccineAlarmByBoot();
		} 
	}
	
	private void creatAllVaccineAlarmByBoot() {
		List<VaccineInfo> vaccineDatas = Common.getVaccineDatas(MyApplication.getAppContext());
		if (vaccineDatas != null) {
			for (int i = 0; i<vaccineDatas.size(); i++) {
				VaccineInfo vaccineInfo = vaccineDatas.get(i);
				if (vaccineInfo != null) {
					String date = vaccineInfo.getDate();
					if (!TextUtils.isEmpty(vaccineInfo.getDate()) && 
							Common.compare2CurrDate(Common.timeStamp2Date(vaccineInfo.getDate()))) {
						VaccineAlarmUtils.creatVaccineAlarm(MyApplication.getAppContext(), Long.parseLong(date), Integer.valueOf(vaccineInfo.getVaccineType()).intValue());
					}
				}
			}
		}
	}
	
	private void alertNotification(Context context, int vaccineType) {
		Log.e("lee", "alertNotification -- vaccineType == "+vaccineType);
		//1.弹出提醒界面
		showVaccineAlarmDialog(context,vaccineType);
		//2.移除相应的疫苗数据库数据
		Common.removeVaccineAlarmByType(vaccineType);
	}
	
	private void showVaccineAlarmDialog(final Context context, int vaccineType) {
		VaccineAlarmSevice.startVaccineAlarm(context, vaccineType);
	}	
	
}