package com.worldchip.bbpaw.bootsetting.util;

import android.net.Uri;

public class Utils {
	
	/** 用户注册本地shard保存key **/
	public static final String IS_FIRST_START_KEY = "first_start";
	public static final String PHOTO_SHARD_KEY = "user_photo";
	public static final String USERNAME_SHARD_KEY = "user_name";
	public static final String GENDER_SHARD_KEY = "user_gender";
	public static final String DOB_SHARD_KEY = "user_dob";
	public static final String EMAIL_SHARD_KEY = "user_email";
	public static final String PASSWORD_SHARD_KEY = "user_password";
	public static final String DEVICEID_SHARD_KEY = "user_device_id";
	public static final String TERMS_ENABLE_SHARD_KEY  = "terms_enable";
	public static final String LANGUAGE_SHARD_KEY  = "language";
	public static final String UPDATE_SYSTEMUI = "cn.worldchip.www.UPDATE_SYSTEMUI";
	
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	}
	
}
