package com.worldchip.bbp.ect.util;

import android.content.Context;

public class Configure {
	//锟角凤拷为锟斤拷锟斤拷模式
		public static final boolean DEBUG = true;
		
		public static final String ECT_REST_CODE = "LETC";
		public static final int STAR_MAX_NUM = 5;
		/**锟斤拷锟斤拷锟斤拷锟斤拷时时锟斤拷 **/
		public static final int HTTP_CONNECT_TIMEOUT = 20*1000;
		/**锟斤拷锟斤拷锟斤拷锟饺★拷锟捷筹拷时时锟斤拷 **/
		public static final int HTTP_READ_TIMEOUT = 20*1000;
		/**锟斤拷取锟斤拷莩晒锟�*/
		public static final int SUCCESS = 1;
		/**锟斤拷取锟斤拷锟绞э拷锟�*/
		public static final int FAILURE = 0;
		//public static final String SERVER = "http://120.24.152.7/";
		public static final String SERVER = "http://push.bbpaw.com.cn/";
		
		/** 用户上传头像url **/
		public static final String USER_PIC_UPLOAD_REQ_URL = SERVER +"/interface/upload_image.php";
		
		/**
		 * 锟斤拷录
		 */
		public static final String LOGIN_URL = SERVER +"interface/clientpassword.php?type=1&username=ACCOUNT&password=PASSWORD&deviceid=DEVICE_ID";

		public static final String HTTP_TAG_LOGIN = "login";
		/**
		 * 锟角筹拷
		 */
		public static final String LOGIN_OUT_URL = SERVER +"interface/clientpassword.php?type=2&username=ACCOUNT&deviceid=DEVICE_ID";

		public static final String HTTP_TAG_LOGIN_OUT = "login_out";
		/**
		 * 锟睫革拷锟斤拷锟斤拷
		 */
		public static final String UPDATE_PASSWORD = SERVER +"interface/clientpassword.php?type=3&username=ADMIN&new_password=NEWPASSWORD&old_password=OLDPASSWORD&deviceid=DEVICE_ID";
		public static final String HTTP_TAG_UPDATE_PASSWORD = "update_password";	
		/**
		 * 锟斤拷锟斤拷锟斤拷锟�
		 */
		public static final String FORGET_PASSWORD = SERVER +"interface/clientpassword.php?type=4&username=ADMIN&email=EMALIS&deviceid=DEVICE_ID";
		public static final String HTTP_TAG_FORGET_PASSWORD ="forget_password";
		/**
		 * 锟斤拷锟斤拷锟睫革拷锟矫伙拷锟斤拷息
		 */
		public static final String UPDATE_BABYINFO = SERVER +"interface/clientpassword.php?type=6&account=ACCOUNT&username=ADMIN&image=IMAGEPATH&deviceid=DEVICE_ID&gender=SEX&dob=BRITHDAY&clientKey=CLIENT_KEY";
		public static final String HTTP_TAG_UPDATE_BABYINFO ="update_babyinfo";
		
		/**
		 * 获取验证码
		 */
		public static final String GET_VERFICATION_CODE = SERVER +"interface/codeofc.php?method_type=1&language=LANGUAGE&input=ACCOUNT&deviceid=DEVICE_ID&code_type=1";
		public static final String HTTP_TAG_GET_VERFICATION_CODE ="get_verfication_code";
		
		/**
		 * 获取旧密码
		 */
		public static final String GET_OLD_PSW = SERVER +"interface/codeofc.php?method_type=2&input=ACCOUNT&deviceid=DEVICE_ID&code_type=1&code=CODE";
		public static final String HTTP_TAG_GET_OLD_PSW ="get_old_psw";
		
		/**
		 * 锟斤拷幕锟侥匡拷,锟斤拷锟斤拷
		 */
		public static int getScreenWidth(Context context) {
			return context.getResources().getDisplayMetrics().widthPixels;
		}

		/**
		 * 锟斤拷幕锟侥革拷,锟斤拷锟斤拷
		 */
		public static int getScreenHeight(Context context) {
			return context.getResources().getDisplayMetrics().heightPixels;
		}

		
}
