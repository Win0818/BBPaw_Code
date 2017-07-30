package com.mgle.member.util;

import java.text.DecimalFormat;

import android.content.Context;
import android.widget.Toast;

public class Common {
	
	/**
	 * ½ð¶î×ª»»
	 */
	public static String getDecimalFormat(long money)
	{
		DecimalFormat format = new DecimalFormat("###,###");
		return format.format(money);
	}
	
	public static void showMessage(Context mContext,String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
}
