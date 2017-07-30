package com.worldchip.bbp.ect.adapter;

import java.util.Calendar;
import java.util.List;
import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.AlarmActivity;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.receiver.AlarmReceiver;
import com.worldchip.bbp.ect.util.HttpCommon;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ClockAlarmAdapter extends BaseAdapter implements OnTouchListener{

	static class Holder 
	{
		 private CheckBox mClcokAlarmUse;
		 private TextView mClockAlarmTime;
		 private ImageView mClockAlarmDel;
	}

	private static final String TAG = "--ClockAlarmAdapter--";

	private List<AlarmInfo> dataList;
	private Activity act;
	@SuppressWarnings("unused")
	private Handler mHandler;
	
	public ClockAlarmAdapter(Activity act, List<AlarmInfo> list, Handler mHandler) 
	{
		this.act = act;
		this.dataList = list;
		this.mHandler = mHandler;
	}
	
	@Override
	public int getCount() 
	{
		int count = 0;
		if (dataList != null) 
		{
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) 
	{
		if (dataList != null) 
		{
			return dataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.clock_alarm_item, null);
			holder.mClcokAlarmUse = (CheckBox) convertView.findViewById(R.id.clcok_alarm_use);
			holder.mClockAlarmTime = (TextView) convertView.findViewById(R.id.clock_alarm_time);
			holder.mClockAlarmDel = (ImageView) convertView.findViewById(R.id.clock_alarm_del);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.mClockAlarmTime.setOnTouchListener(this);
		final AlarmInfo alarm = dataList.get(position);
		if(alarm.getIsdefault() == 1)
		{
			if(alarm.getEnabled() == 1)
			{
				holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use_on);
				holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg_start);
			}else{
				holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use);
				holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg);
			}
			holder.mClockAlarmTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock_alarm_time, 0, 0, 0);  
			int hours = alarm.getHours();
			int musutes = alarm.getMusutes();
			String time = "";
			if(hours > 0 && hours < 24)
			{
				if(hours > 0 && hours < 10)
				{
					time = time + 0 + hours + " : ";
				}else{
					time = time + hours + " : ";
				}
			}else{
				time = time + "00" + " : ";
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
			
			holder.mClockAlarmTime.setText(time);
			holder.mClockAlarmTime.setOnLongClickListener(new OnLongClickListener() 
			{
				@Override
				public boolean onLongClick(View view) 
				{
					holder.mClockAlarmDel.setVisibility(View.VISIBLE);
					return false;
				}
			});
			
			//删除
			holder.mClockAlarmDel.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View view) 
				{
					Toast.makeText(act, "删除", Toast.LENGTH_LONG).show();
				}
			});
			//开启/关闭闹钟
			holder.mClcokAlarmUse.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean flag)
				{
					String weeks = alarm.getDaysofweek();
					if(!weeks.equals("-1") && weeks != "-1")
					{
						int week[] = HttpCommon.stringToInts(weeks);
						if(flag)
						{
							holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use_on);
							holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg_start);
							for (int i = 0; i < week.length; i++) 
							{
								setAlarm(alarm, true,week[i]);
							}
							ContentValues values = new ContentValues();
							values.put("isdefault", 1);
							ClockData.updataAlarmInfo(act,alarm.getId(),values);
						}else{
							holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use);
							holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg);
							for (int i = 0; i < week.length; i++) 
							{
								setAlarm(alarm, false,week[i]);
							}
							ContentValues values = new ContentValues();
							values.put("isdefault", 0);
							ClockData.updataAlarmInfo(act,alarm.getId(),values);
						}
					}else{
						if(flag)
						{
							holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use_on);
							holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg_start);
							setEveryDayAlarm(alarm, true);
							ContentValues values = new ContentValues();
							values.put("isdefault", 1);
							ClockData.updataAlarmInfo(act,alarm.getId(),values);
						}else{
							holder.mClcokAlarmUse.setBackgroundResource(R.drawable.clock_use);
							holder.mClockAlarmTime.setBackgroundResource(R.drawable.clock_item_bg);
							setEveryDayAlarm(alarm, false);
							ContentValues values = new ContentValues();
							values.put("isdefault", 0);
							ClockData.updataAlarmInfo(act,alarm.getId(),values);
						}
					}
				}
			});
		}
		
		//点击闹钟
		holder.mClockAlarmTime.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Intent intent = new Intent();
				intent.setClass(act,AlarmActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("_id", alarm.getId());
				intent.putExtras(bundle);
				act.startActivityForResult(intent, 500);
			}
		});
		return convertView;
	}

	private void setAlarm(AlarmInfo alarm, boolean enable,int today) 
	{
		ClockData.enableAlarm(this.act, alarm.getId(), enable);

		AlarmManager am = (AlarmManager) this.act.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this.act, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this.act, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, today);
		c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
		c.set(Calendar.MINUTE, alarm.getMusutes());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Log.i(TAG,"setAlarm...Your set getTimeInMillis()=" + c.getTimeInMillis() + "; System Time Millis: " + System.currentTimeMillis());

		if (enable) 
		{
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),10 * 60 * 1000, pi);
		} else {
			am.cancel(pi);
		}
	}
	
	private void setEveryDayAlarm(AlarmInfo alarm, boolean enable) 
	{
		ClockData.enableAlarm(this.act, alarm.getId(), enable);

		AlarmManager am = (AlarmManager) this.act.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this.act, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this.act, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, alarm.getHours());
		c.set(Calendar.MINUTE, alarm.getMusutes());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Log.i(TAG,"setAlarm...Your set getTimeInMillis()=" + c.getTimeInMillis() + "; System Time Millis: " + System.currentTimeMillis());

		if (enable) 
		{
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pi);
		} else {
			am.cancel(pi);
		}
	}
	
	
	@Override
	public boolean onTouch(View view, MotionEvent event) 
	{
		switch (event.getAction())
		{ 	  
	        case MotionEvent.ACTION_DOWN://按下
	        	view.setBackgroundResource(R.drawable.clock_item_bg_select);
				break;
	        case MotionEvent.ACTION_UP://抬起
	        	view.setBackgroundResource(R.drawable.clock_item_bg);
				break;
	        case MotionEvent.ACTION_CANCEL://表示手势被取消了
	        	view.setBackgroundResource(R.drawable.clock_item_bg);
				break;
	        default:
	            break;
		}
		return false;
	} 
}