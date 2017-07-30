package com.worldchip.bbp.bbpawmanager.cn.utils;

import com.worldchip.bbp.bbpawmanager.cn.R;

public class Utils {

	public static final String UPDATE_SYSTEMUI = "cn.worldchip.www.UPDATE_SYSTEMUI";

	//public static final String SERVER = "http://120.24.152.7";
	public static final String SERVER = "http://push.bbpaw.com.cn";
	
	public static final String NETERR = "neterr";
	
	public static final String SHARED_PREFERENCE_NAME = "bbpaw_manager_preference";
	
	public static final String SHARED_VACCINE_DEFAULT_KEY = "isInitVaccineDefault";
	public static final String SHARED_SENDDEVICEID_KEY = "send_deviceId";
	
	public static final String VACCINE_ALARM_DIALOG_SHOWING_KEY = "vaccine_alarm_dialog_showing";
	
	public static final String PUSH_MESSAGE_ACTION = "com.worldchip.bbpaw.client.manager.pushMessageService";
	public static final String RECEIVE_PUSH_MESSAGE_ACTION = "com.worldchip.bbpaw.client.manager.receivePushMessage";
	public static final String RECEIVE_PUSH_MESSAGE_TYPE_KEY = "messageType";
	
	public static final String VACCINE_ALARM_ACTION = "com.worldchip.bbpaw.vaccine.Action";
	
	public static final String REST_ALARM_ACTION = "com.worldchip.bbpaw.eyecare.rest.Action";
	public static final String EYECARE_ALARM_CHANGE_ACTION = "com.worldchip.bbpaw.eyecare.change.Action";
	
	public static final String MESSAGE_PUSH_SWITCH_ACTION = "com.worldchip.bbpaw.messageSwitch.Action";
	
	/**连接主机超时时间 **/
	public static final int HTTP_CONNECT_TIMEOUT = 20*1000;
	/**从主机读取数据超时时间 **/
	public static final int HTTP_READ_TIMEOUT = 20*1000;
	/**获取数据成功**/
	public static final int SUCCESS = 1;
	/**获取数据失败**/
	public static final int FAILURE = 0;
	
	public static final String HTTP_TAG_HOMEPAGE_LOAD = "homepage_load";
	public static final String HTTP_TAG_GROWTH_LOAD = "growth_load";
	public static final String HTTP_TAG_INFORMATION_LOAD = "information_load";
	public static final String HTTP_TAG_REPLY_STATE = "reply_state";
	public static final String HTTP_TAG_SEND_DEVICEID = "send_deviceId";
	
	/**
	 * 消息答复状态
	 */
	public static final int REPLY_STATE_RECEIVED = 1;
	public static final int REPLY_STATE_REDED = 2;
	public static final int REPLY_STATE_DOWNLOADED = 3;
	public static final int REPLY_STATE_INSTALLED = 4;
	
	/** 获取主界面数据URL**/	
	public static final String HTTP_HOME_PAGE_REQ_URL = SERVER +"/interface/mainpage.php?param=mainpage";
	
	/** 获取成长日志数据URL**/	
	//public static final String HTTP_GROWTH_REQ_URL = SERVER +"/interface/mainpage.php?param=growpage&deviceid=";
	public static final String HTTP_GROWTH_REQ_URL = SERVER +"/interface/mainpage.php?param=growpage&email=";
	
	/** 获取专家资讯全部数据URL**/
	public static final String HTTP_INFORM_ALL_REQ_URL = SERVER +"/interface/mainpage.php?param=expertinfo&deviceid=";
	
	/** 获取专家资讯某一类型数据URL**/
	public static final String HTTP_INFORM_TYPE_REQ_URL = SERVER +"/interface/mainpage.php?param=expertinfo&type=TYPE";
	
	public static final int[] HELP_PAGER_VIEW_RES= {R.drawable.help_pager_01,R.drawable.help_pager_02};
	
	public static final String HTTP_REPLY_STATE_REQ_URL = SERVER +"/interface/change_operation.php?maintype=MAINTYPE" +
			"&order_type=ORDER_TYPE&id=ID&operation=OPERATION";
	
	public static final String HTTP_SEND_DEIVCEID2SERVER_REQ_URL = SERVER +"/interface/update_deviceid.php?device_id=DEVICE_ID";
	
	
	public static final int CHECK_SERVICE_RUNNING_TIMEING = 20 * 1000;
	
	
	public static final String DEFAULT_VACCINE_TIME = "08:40:00";
	public static final String VACCINE_ALARM_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String EYECARE_SCREEN_TIME_PRE_KEY = "screen_time_switch_key";
	public static final String EYECARE_USE_TIME_PRE_KEY = "use_time_key";
	public static final String EYECARE_SLEEP_TIME_PRE_KEY = "sleep_time_key";
	public static final String EYECARE_DAY_TIME_BEGIN_PRE_KEY = "use_time_begin_key";
	public static final String EYECARE_DAY_TIME_END_PRE_KEY = "use_time_end_key";
	
	public static final String DISTANCE_SENSOR_PRE_KEY = "distance_sensor_key";
	public static final String REVERSE_SENSOR_PRE_KEY = "reverse_sensor_key";
	public static final String LIGHT_SENSOR_PRE_KEY = "light_sensor_key";
	
	
	
}
