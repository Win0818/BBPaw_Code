package com.worldchip.bbp.ect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class MD5Check {

	private static final String TAG = "---MD5Check---";
	private static final String LIBFiLE = "/system/xbin/sys.ect.so";
	private static final String JSFILE="/system/etc/system.js.lib";
	
	private static final boolean DEBUG = false;	
	
	public static boolean permission(){

		String usrLibKey = readUsrLibServiceKeyValue();
		if(usrLibKey==null || usrLibKey.equals("")) return false;
		
		if(checkMd5(usrLibKey, LIBFiLE)) return true;
		
		return false;
	}
	
    private static String createMd5(String f) {
        MessageDigest mMDigest;
        FileInputStream Input;
        byte buffer[] = new byte[1024];
        int len;
        
        File file = new File(f);
        if (!file.exists())
            return null;
        try {
            mMDigest = MessageDigest.getInstance("MD5");
            Input = new FileInputStream(file);
            while ((len = Input.read(buffer, 0, 1024)) != -1) {
                mMDigest.update(buffer, 0, len);
            }
            Input.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        BigInteger mBInteger = new BigInteger(1, mMDigest.digest());
        if(DEBUG)Log.v(TAG, "create_MD5=" + mBInteger.toString(16));
        return mBInteger.toString(16);

    }

    private static boolean checkMd5(String Md5, String file) {
    	if(Md5==null || Md5.equals("")) 
    		return false;
    	if(file == null) return false;
    	
        String str = createMd5(file);
        if(DEBUG)Log.d(TAG,"md5sum = " + str);
        if(Md5.compareTo(str) == 0) return true;
        else
         return false;
    }
    
    private static String readUsrLibServiceKeyValue() {
		String js ="";		
		File file = null;
		try{
			file = new File(JSFILE);
		}catch(IOError e){
			if(DEBUG)Log.i(TAG, "IOError read libservice..."+e.getMessage());
			return js;
		}
		if(!file.exists()){
			if(DEBUG)Log.i(TAG, "file not exits");
			return js;
		}
		
        BufferedReader reader = null;       
		try {
		    reader = new BufferedReader(new FileReader(file));
		    js = reader.readLine();
		    if(DEBUG)Log.i(TAG,"read value ="+js);		    
		    reader.close();
		} catch (Exception e) {
			if(DEBUG)Log.i(TAG,"read value had err"+e.getMessage());	
		    return js;
		} finally {
		    if (reader != null) {
		        try {
		            reader.close();
		        } catch (Exception err) {
		        	if(DEBUG)Log.i(TAG,"reader close had err"+err.getMessage());
		        	return js;
		        }
		    }
		}	
		return js;
	}
}