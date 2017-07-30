package com.worldchip.bbp.ect.db;

import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.AlarmInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ClockData {

	/**
	 * 添加闹钟
	 * @param context
	 * @param values
	 */
	public static void addClockAlarm(Context context, ContentValues values) 
	{
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_CLOCk_TABLE, values);
		Log.e("addClockAlarm", "success");
	}
	
	/**
	 * 获取所有的闹钟
	 */
	public static List<AlarmInfo> getAllClockAlarm(Context context)
	{
		return getAlarmInfo(context, null);
	}
	
	/**
	 * 获取所有打开的闹钟
	 */
	public static List<AlarmInfo> getAllEnableClockAlarm(Context context)
	{
		return getAlarmInfo(context, "where enabled = 1");
	}
	
	/**
	 * 根据编号获取一个闹钟
	 */
	public static AlarmInfo getByAlarmInfo(Context context,int _id)
	{
		List<AlarmInfo> alarms = getAlarmInfo(context, "where _id="+ _id);
		if(alarms !=null && alarms.size() > 0){
			return alarms.get(0);
		}
		return null;
	}
	
	/**
	 * 根据编号获取一个闹钟
	 */
	public static int getAlarmInfoSize(Context context)
	{
		int count = 0;
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursorBySql("select count(*) from '"+DBSqlBuilder.BBP_CLOCk_TABLE+"' order by _id desc");
		if(cursor != null)
		{
			cursor.moveToFirst();
			count = cursor.getInt(0);
		}
		return count;
	}
	
	public static List<AlarmInfo> getAlarmInfo(Context context, String whereStr)
	{
		List<AlarmInfo> alarmInfos = new ArrayList<AlarmInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = null;
		if(whereStr==null || whereStr.equals(""))
		{
			cursor = helper.getCursor(DBSqlBuilder.BBP_CLOCk_TABLE, "order by _id asc");
		}else{
			cursor =helper.getCursor(DBSqlBuilder.BBP_CLOCk_TABLE, whereStr);
		}
		
		while(cursor.moveToNext())
		{
			Integer id = cursor.getInt(cursor.getColumnIndex("_id"));
			Integer hours = cursor.getInt(cursor.getColumnIndex("hours"));
			Integer musutes = cursor.getInt(cursor.getColumnIndex("musutes"));
			String daysofweek = cursor.getString(cursor.getColumnIndex("daysofweek"));
			Integer alarmtime = cursor.getInt(cursor.getColumnIndex("alarmtime"));
			Integer enabled = cursor.getInt(cursor.getColumnIndex("enabled"));
			String alert = cursor.getString(cursor.getColumnIndex("alert"));
			Integer times = cursor.getInt(cursor.getColumnIndex("times"));
			Integer isdefault = cursor.getInt(cursor.getColumnIndex("isdefault"));
			Integer interval = cursor.getInt(cursor.getColumnIndex("interval"));
			AlarmInfo alarmInfo = new AlarmInfo(id, hours, musutes, daysofweek, alarmtime, enabled, alert, times,isdefault,interval);
			alarmInfos.add(alarmInfo); 
		}
		cursor.close();
		return alarmInfos;
	}
	/**
	 * 修改闹钟
	 */
	public static boolean updataAlarmInfo(Context context,int _id,ContentValues values)
	{
		DBHelper helper = new DBHelper(context);
		boolean flag = helper.updateValues(DBSqlBuilder.BBP_CLOCk_TABLE,values,"_id = "+ _id);
		return flag;
	}
	
	public static boolean enableAlarm(Context context, int _id, boolean enable)
	{
		ContentValues values = new ContentValues();
		values.put("enabled", enable ? 1 : 0);
		DBHelper helper = new DBHelper(context);
		boolean flag = helper.updateValues(DBSqlBuilder.BBP_CLOCk_TABLE,values, "_id = " + _id);
		return flag;
	}
	/**
	 * 删除某个闹钟
	 * @param context
	 * @param id
	 */
	public static boolean delClockById(Context context,long id)
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_CLOCk_TABLE, id);
		Log.e("delClockById", "falg："+falg);
		Log.e("delClockById", "id："+id);
		return falg;
	}
}