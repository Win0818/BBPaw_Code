package com.worldchip.bbp.ect.common;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTontrol {
	
	/**
	 * ʱ��ο���----���԰���ʱ��������ƣ����磬����ʹ��ʱ��Ϊ10��15��15��40�ֿ�ʹ�ã�
	 * �жϵ�ǰʱ���Ƿ��Ѿ��������ʱ��
	 * @param startTime  ʱ��ο�ʼʱ��
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean startTimeTontrol(String startTime)
	{
		try {
			SimpleDateFormat d = new SimpleDateFormat("HH:mm");// ��ʽ��ʱ��
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
	 * ʱ��ο���----���԰���ʱ��������ƣ����磬����ʹ��ʱ��Ϊ10��15��15��40�ֿ�ʹ�ã�
	 * �жϸ�ʱ������Ƿ����
	 * @param endTime  ʱ��ν���ʱ��
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean endTimeTontrol(String endTime)
	{
		try {
			SimpleDateFormat d = new SimpleDateFormat("HH:mm");// ��ʽ��ʱ��
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