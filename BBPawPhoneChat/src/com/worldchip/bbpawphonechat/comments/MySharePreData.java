package com.worldchip.bbpawphonechat.comments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharePreData {
	
	 private static SharedPreferences sp; 
	 
	    @SuppressWarnings("static-access") 
	    public static void SetData(Context context, String filename, String key, 
	            String value) { 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        Editor editor = sp.edit(); 
	        editor.putString(key, value); 
	        editor.commit(); 
	    } 
	    
	    public static void SetIntData(Context context, String filename, String key, 
	            int value) { 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        Editor editor = sp.edit(); 
	        editor.putInt(key, value);
	        editor.commit(); 
	    } 
	    
	    public  static void  SetBooleanData(Context context, String filename, String key, 
	            boolean value){
	    	sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);
	    	Editor  editor  = sp.edit();
	    	editor.putBoolean(key, value);
	    	editor.commit();
	    }
	   
	    @SuppressWarnings("static-access") 
	    public static String GetData(Context context, String filename, String key) { 
	        String value = ""; 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        value = sp.getString(key, ""); 
	        return value; 
	    } 
	    
	    public static int GetIntData(Context context, String filename, String key) { 
	        int value = 0; 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        value = sp.getInt(key, 0); 
	        return value; 
	    } 
	    
	    public static boolean GetBooleanData(Context context, String filename, String key) { 
	        boolean value = false; 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        value = sp.getBoolean(key, false);
	        return value; 
	    } 
	    
	    
	    public static boolean GetBooleanTrueData(Context context, String filename, String key) { 
	        boolean value = true; 
	        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE); 
	        value = sp.getBoolean(key, true);
	        return value; 
	    } 
	    

}
