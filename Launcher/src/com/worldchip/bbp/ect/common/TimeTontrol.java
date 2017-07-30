package com.worldchip.bbp.ect.common;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTontrol {
	
	/**
	 * 时间段控制----可以按照时间段来控制，比如，允许使用时间为10点15至15点40分可使用）
	 * 判断当前时间是否已经进入可用时间
	 * @param startTime  时间段开始时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean startTimeTontrol(String startTime)
	{
		try {
			SimpleDateFormat d = new SimpleDateFormat("HH:mm");// 格式化时间
			String nowTime = d.format(new Date());//
			long currentTime = d.parse(nowTime).getTime();
			long startTimes = d.parse(startTime).getTime();
			if(currentTime - startTimes > 0)
			{
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 时间段控制----可以按照时间段来控制，比如，允许使用时间为10点15至15点40分可使用）
	 * 判断该时间段内是否可用
	 * @param endTime  时间段结束时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean endTimeTontrol(String endTime)
	{
		try {
			SimpleDateFormat d = new SimpleDateFormat("HH:mm");// 格式化时间
			String nowTime = d.format(new Date());//
			long currentTime = d.parse(nowTime).getTime();
			long endTimes = d.parse(endTime).getTime();
			if(endTimes - currentTime > 0)
			{
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return false;
	}
}