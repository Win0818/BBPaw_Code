package com.worldchip.bbpaw.bootsetting.util;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.util.Log;

import com.worldchip.bbpaw.bootsetting.R;


public class WifiUtils {

	public static final int SINGAL_NONE_INDEX = 0; 
	public static final int SINGAL_WEAK_INDEX = 1; 
	public static final int SINGAL_GENERAL_INDEX = 2; 
	public static final int SINGAL_GOOD_INDEX = 3;
	
	public static final int SECURITY_NONE = 0;  
	public static final int SECURITY_WEP = 1;  
	public static final int SECURITY_PSK = 2;  
	public static final int SECURITY_EAP = 3;  
    
	public static final int[] SINGAL_IMG_RES = {
		R.drawable.wifi_signal0,R.drawable.wifi_signal1,
		R.drawable.wifi_signal2,R.drawable.wifi_signal3};
	
	/** Anything worse than or equal to this will show 0 bars. */
    private static final int MIN_RSSI = -100;

    /** Anything better than or equal to this will show the max bars. */
    private static final int MAX_RSSI = -55;
	
	/**
	 * 获取信号强度图标
	 * @return
	 */
	public static int getWifiSingalImgRes(int singalNum) {
		int numLevels = SINGAL_IMG_RES.length;
		int level = 0;
		 if (singalNum <= MIN_RSSI) {
			 level = 0;
	        } else if (singalNum >= MAX_RSSI) {
	        	level = numLevels - 1;
	        } else {
	            int partitionSize = (MAX_RSSI - MIN_RSSI) / (numLevels - 1);
	            level = (singalNum - MIN_RSSI) / partitionSize;
	        }
		return SINGAL_IMG_RES[level];
	}
	
	/**
	 * 判断此wifi是否加密
	 * @return
	 */
	public static boolean isSecurity(ScanResult result) {
		if (result.capabilities.equals("NONE")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 按强度排序wifi列表
	 */
	public static void sortResultListForWifiSignal(List<ScanResult> scanResultList) {
		for (int i = 0; i < scanResultList.size() - 1; i++) {
			for (int j = i + 1; j < scanResultList.size(); j++) {
				if (scanResultList.get(i).level < scanResultList.get(j).level) {
					ScanResult temp = null;
					temp = scanResultList.get(i);
					scanResultList.set(i, scanResultList.get(j));
					scanResultList.set(j, temp);
				}
			}
		}
	}
	
  
	
   public static int getSecurity(WifiConfiguration config) {  
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {  
            return SECURITY_PSK;  
        }  
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {  
            return SECURITY_EAP;  
        }  
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;  
    } 
   
   public static int getSecurity(ScanResult result) {
       if (result.capabilities.contains("WEP")) {
           return SECURITY_WEP;
       } else if (result.capabilities.contains("PSK")) {
           return SECURITY_PSK;
       } else if (result.capabilities.contains("EAP")) {
           return SECURITY_EAP;
       }
       return SECURITY_NONE;
   }
   
}
