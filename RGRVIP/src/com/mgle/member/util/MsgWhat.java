package com.mgle.member.util;

public class MsgWhat {

	public static final int NO_NET = 0;
	public static final int GET_NAV_CATEGORY = NO_NET+1;
	public static final int GET_NAV_CATEGORY_RESULT = GET_NAV_CATEGORY+1;
	
	public static final int GET_COMMODITY_CONTENT = GET_NAV_CATEGORY_RESULT+1;
	public static final int GET_COMMODITY_CONTENT_RESULT = GET_COMMODITY_CONTENT+1;
	public static final int LOAD_IMAGE = GET_COMMODITY_CONTENT_RESULT+1;
	public static final int HIDE_TIP_PROGRESS = LOAD_IMAGE+1;
	public static final int DEVICE_LOGIN_OK = HIDE_TIP_PROGRESS+1;
	public static final int DEVICE_LOGIN_FAIL = DEVICE_LOGIN_OK+1;
	public static final int DEVICE_MEMBER_INFO = DEVICE_LOGIN_FAIL+1;
	public static final int DEVICE_GET_EPG_INFO = DEVICE_MEMBER_INFO+1;
	
	public static final int SEND_MSG_INFO_RESULT = DEVICE_GET_EPG_INFO+1;
}
