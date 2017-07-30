package com.worldchip.bbpaw.media.camera.utils;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Configure {
	public static int screenHeight=600;
	public static int screenWidth=1024;
	public static float screenDensity=0;
	public static int IMAGE_MAX_HEIGHT_PX = 1024;
    public static int IMAGE_MAX_WIDTH_PX = 600;
    public static final int IMAGE_ROTATE_DEGREE = 90;
    
	public static void init(Activity context) {
		if(screenDensity==0||screenWidth==0||screenHeight==0){
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Configure.screenDensity = dm.density;
			Configure.screenHeight = dm.heightPixels;
			Configure.screenWidth = dm.widthPixels;
		}
	}
	
	
	/**
	 * 屏幕的宽,像素
	 */
	public static int SCREEN_WIDTH(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 屏幕的高,像素
	 */
	public static int SCREEN_HEIGHT(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
}
