package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.io.File;

import com.worldchip.bbp.bbpawmanager.cn.R;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;


/**
 * 
 * @ClassName Config
 * @Description 客户端所有的配置项
 */
public class Configure {
	
	private static Context mContext;
	
    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }
	//是否为调试模式
	public static final boolean DEBUG = true;
	
	public static int SCREEN_WIDTH = 1024;
	public static int SCREEN_HEIGHT = 600;
	
	public static int IMAGE_ICON_WIDTH_PX = 120;
    public static int IMAGE_MAX_HEIGHT_PX = 800;
    public static int IMAGE_MAX_WIDTH_PX = 400;
    public static boolean isStopLoadImage = false; // 是否禁止下载图片，内存过低时使用
    /** 推送服务器地址**/
    //public  static final String PUSH_MESSAGE_MQTT_HOST = "120.24.152.7";
    public  static final String PUSH_MESSAGE_MQTT_HOST = "push.bbpaw.com.cn";
    /** 推送broker 端口**/
    public static final int PUSH_MESSAGE_MQTT_BROKER_PORT_NUM = 1883;
    
    private static String _BASE_FOLDER_SDCARD = "";
    
    public static final String EYECARE_SETTINGS_SHARED_NAME = "eyecare_settings";
    public static final int EYECARE_SETTINGS_SHARED_MODE = Context.MODE_WORLD_READABLE;
    
    public static final int EYECARE_DRAWABLE_ANIM_DURATION = 100;
	
    public static final String DEFAULT_USE_BEGIN_TIME = "00:00";
    public static final String DEFAULT_USE_END_TIME = "23:59";
    
    public static final String DEFAULT_NOTICE_ENG = "file:///android_asset/default_notice_eng.htm";
	public static final String DEFAULT_NOTICE_CN = "file:///android_asset/default_notice_cn.htm";
    
    public static final int[] EYECARE_REST_DRAWABLE_ANIM_RES = {
    	R.drawable.eyecare_rest_anim01,R.drawable.eyecare_rest_anim02,R.drawable.eyecare_rest_anim03,R.drawable.eyecare_rest_anim04,
    	R.drawable.eyecare_rest_anim05,R.drawable.eyecare_rest_anim06,R.drawable.eyecare_rest_anim07,R.drawable.eyecare_rest_anim08,
    	R.drawable.eyecare_rest_anim09,R.drawable.eyecare_rest_anim10,R.drawable.eyecare_rest_anim11,R.drawable.eyecare_rest_anim12,
    	R.drawable.eyecare_rest_anim13,R.drawable.eyecare_rest_anim14,R.drawable.eyecare_rest_anim15,R.drawable.eyecare_rest_anim16,
    	R.drawable.eyecare_rest_anim17,R.drawable.eyecare_rest_anim18,R.drawable.eyecare_rest_anim19,R.drawable.eyecare_rest_anim20,
    	R.drawable.eyecare_rest_anim21,R.drawable.eyecare_rest_anim22,R.drawable.eyecare_rest_anim23,R.drawable.eyecare_rest_anim24,
    	R.drawable.eyecare_rest_anim25,R.drawable.eyecare_rest_anim26,R.drawable.eyecare_rest_anim27,R.drawable.eyecare_rest_anim28,
    	R.drawable.eyecare_rest_anim29,R.drawable.eyecare_rest_anim30,R.drawable.eyecare_rest_anim31,R.drawable.eyecare_rest_anim32,
    	R.drawable.eyecare_rest_anim33,R.drawable.eyecare_rest_anim34,R.drawable.eyecare_rest_anim35,R.drawable.eyecare_rest_anim36,
    	R.drawable.eyecare_rest_anim37,R.drawable.eyecare_rest_anim38
    	};
    
    
    
    /**
	 * 获得APP在SDCARD上开辟的目录<br>
	 * 形如 /mnt/sdcard/wanfujiazheng/
	 */
	public static String BASE_FOLDER_SDCARD() {
		if (TextUtils.isEmpty(_BASE_FOLDER_SDCARD)) {
			_BASE_FOLDER_SDCARD = Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState()) ? Environment
					.getExternalStorageDirectory().toString()
					+ File.separator
					+ "wanfujiazheng" + File.separator : mContext.getFilesDir()
					.toString();
		}
		return _BASE_FOLDER_SDCARD;
	};
	
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
