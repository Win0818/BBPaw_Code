package com.worldchip.activity;

import java.io.File;
import android.util.Log;
import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class EncryptData {
	
	public static final String DEFALUT_VALUE = "677163752521";
	public static final String TAG = "EncryptData";
	public static final boolean DEBUG = false;
	
	public static boolean encrypt(Context context) {
		if(DEBUG)Log.e(TAG, "---encrypt start----");
		try {
			String encrypt = null;
			Process process;
			try {
				process = Runtime.getRuntime().exec("getprop sys.rw.mark");
				InputStreamReader ir = new InputStreamReader(process.getInputStream());
		        BufferedReader input = new BufferedReader(ir);
		        encrypt = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			if(DEBUG)Log.e(TAG, "---encrypt value="+encrypt);
			if(encrypt==null || encrypt.equals("")){
				return false;
			}
			if(encrypt.equals(DEFALUT_VALUE)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}