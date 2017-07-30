package com.mgle.member.util;

import java.io.File;

import android.os.Environment;

public class Utils {

	public static int IMG_RECT_WIDTH = 790;
	public static int UP_DOWN_STEP = 100;
	public static int SCREEN_WIDTH = 1024;
	public static int SCREEN_HEIGHT = 600;
	public static int IMAGE_MAX_HEIGHT_PX = 800;
	public static int IMAGE_MAX_WIDTH_PX = 400;
	
	public static final String WEB_SERVICE_URL = "http://101.200.173.180:8390/epgserver/minzhengapi/getMallInfo.action?";
	public static final String NAV_CATEGORY_URL = "name=亿格瑞商城";
	public static final String SEND_MSG_URL = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	public static final String SEND_MSG_ACCOUNT = "cf_ygr";
	public static final String SEND_MSG_PASSWORD = "Ha3gNy";
	
	public static final String LARGE_IMG_CACHE = "egrcache"+File.separator;
	
	public static String getCachePath(){
		File dir = new File(getRootPath()+LARGE_IMG_CACHE);
		if(!dir.exists()){
			dir.mkdirs();
		}
		return dir.getAbsolutePath();
	}
	
	public static String getRootPath(){
		return getSDPath()+File.separator;
	}
	
	public static String getSDPath() {  
        File sdDir = null;  
        boolean sdCardExist = Environment.getExternalStorageState().equals(  
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {  
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        }  
        return null;  
    } 
}
