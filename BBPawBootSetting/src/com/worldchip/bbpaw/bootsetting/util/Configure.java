package com.worldchip.bbpaw.bootsetting.util;

import android.content.Context;


/**
 * 
 * @ClassName Config
 * @Description 客户端所有的配置项
 */
public class Configure {
	
	//是否为调试模式
	public static final boolean DEBUG = true;
	/**连接主机超时时间 **/
	public static final int HTTP_CONNECT_TIMEOUT = 20*1000;
	/**从主机读取数据超时时间 **/
	public static final int HTTP_READ_TIMEOUT = 20*1000;
	/**获取数据成功**/
	public static final int SUCCESS = 1;
	/**获取数据失败**/
	public static final int FAILURE = 0;
	//public static final String SERVER = "http://120.24.152.7";
	public static final String SERVER = "http://push.bbpaw.com.cn";
	/** 用户上传头像url **/
	public static final String USER_PIC_UPLOAD_REQ_URL = SERVER +"/interface/upload_image.php";
	
	public static final String USER_TEMP_PIC_NAME = "user_photo.jpg";
	
	public static final String SETTINGS_SHARD_NAME = "user";
	
	 public static final int SETTINGS_SHARD_MODE = Context.MODE_WORLD_READABLE;
	 
	/** 用户注册提交参数**/
	public static final String USER_REGISTER_PARAMETER =  
			"username=USERNAME" +
			"&gender=GENDER" +
			"&dob=DOB" +
			"&email=EMAIL" +
			"&password=PASSWORD" +
			"&device_id=DEVICE_ID"+
			"&image=PHOTO"+
			"&language=LANGUAGE"+
			"&coc=TERMS_ENABLE"; 
	
	/** 用户注册信息提交**/
	public static final String USER_REGISTER_REQ_URL = SERVER +"/interface/register.php?";//+USER_REGISTER_PARAMETER;
	
	public static final String USER_TERMS_HTML_PATH_ENG = "file:///android_asset/bbpaw_software_terms_eng.htm";
	public static final String USER_TERMS_HTML_PATH_CN = "file:///android_asset/bbpaw_software_terms_cn.htm";
	
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
