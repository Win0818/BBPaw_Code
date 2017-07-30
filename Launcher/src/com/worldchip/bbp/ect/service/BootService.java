package com.worldchip.bbp.ect.service;

import java.util.Calendar;
import java.util.List;

import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.db.DBGoldHelper;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.receiver.AlarmReceiver;
import com.worldchip.bbp.ect.util.HttpCommon;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BootService extends Service {

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();

		List<AlarmInfo> alarms = ClockData.getAllEnableClockAlarm(this);
		if (alarms != null && alarms.size() > 0) 
		{
			for (AlarmInfo alarm : alarms)
			{
				int hours = alarm.getHours();
				int musutes = alarm.getMusutes();
				String time = "";
				if(hours > 0 && hours < 24)
				{
					if(hours > 0 && hours < 10)
					{
						time = time + 0 + hours + ":";
					}else{
						time = time + hours + ":";
					}
				}else{
					time = time + "00" + ":";
				}
				if(musutes > 0 && musutes < 60)
				{
					if(musutes > 0 && musutes < 10)
					{
						time = time + 0 + musutes;
					}else{
						time = time + musutes;
					}
				}else{
					time = time + "00";
				}
				String weeks = alarm.getDaysofweek();
				if(!weeks.equals("-1") && weeks != "-1")
				{
					int week[] = HttpCommon.stringToInts(weeks);
					for (int i = 0; i < week.length; i++) 
					{
						if(HttpCommon.isThanTime(time.trim()))
						{
							setAlarm(alarm,week[i]);
						}
					}
				}else{
					if(HttpCommon.isThanTime(time.trim()))
					{
						setEveryDayAlarm(alarm);
					}
				}
			}
		}
	}


	private void setAlarm(AlarmInfo alarm,int today)
	{
		AlarmManager am = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, today);
		c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
		c.set(Calendar.MINUTE, alarm.getMusutes());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 10000, pi);
	}
	

	private void setEveryDayAlarm(AlarmInfo alarm) 
	{
		AlarmManager am = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
		c.set(Calendar.MINUTE, alarm.getMusutes());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pi);
	}
	

}