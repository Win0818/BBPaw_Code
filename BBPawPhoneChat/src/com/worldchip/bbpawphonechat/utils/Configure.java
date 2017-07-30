package com.worldchip.bbpawphonechat.utils;

import android.content.Context;

public class Configure {

	
	/**
	 * 屏幕的宽,像素
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 屏幕的高,像素
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
}
