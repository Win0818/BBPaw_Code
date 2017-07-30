package com.egreat.adlauncher.util;

import java.io.File;

import android.os.Environment;

import com.mgle.launcher.R;


public class Utils {

	public static final String LAUNCHER_NAME = "com.mgle.launcher.update.apk";
	public static final String LAUNCHER_PACKAGE_NAME = "com.mgle.launcher";
	public static int SCREEN_WIDTH = 1024;
	public static int SCREEN_HEIGHT = 600;
	public static int IMAGE_MAX_HEIGHT_PX = 800;
	public static int IMAGE_MAX_WIDTH_PX = 400;
	
	public static final int RUN_CURRENT_SHORTCUT_APP = 100;
	public static final int HIDE_SHORTCUT_APP = 101;
	
	public static int[] GUIDE_DATA = new int[]{R.drawable.step1, R.drawable.step2,
    		R.drawable.step3,R.drawable.step4,R.drawable.step5,
    		R.drawable.step6};
	
	public static int[] SHORTCUT_DATA = new int[]{
		R.drawable.qicon_07, R.drawable.qicon_09,R.drawable.qicon_11,
		R.drawable.qicon_13,R.drawable.qicon_15,R.drawable.qicon_17};

	public static final String[] PACKAGE_NAME_ARRAY = { 
		"com.taixin.dtv.elec.doc", "com.shafa.market","com.mgle.allapps",
		"com.tv.clean","tv.tool.netspeedtest","com.ikan.nscreen.box.settings"
	};
	
	public static final String[] PACKAGE_LAUNCHER_ARRAY = { 
		"com.mgle.watchads", "com.taixin.dtvapp","com.starcor.mango",
		"com.mgle.shopping","com.example.estatesmanagerproject", "com.mgle.allapps"
	};
	public static final String SHAFA_MARKET = "com.shafa.market";
	public static final String SETTING_PACKAGENAME = "com.ikan.nscreen.box.settings";
	
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
