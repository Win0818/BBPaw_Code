/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldchip.bbpawphonechat.comments;


public class MyComment {
	
	public static final String CHAT_SP_NAME = "phone_chat";
	public static final String MY_NICK_NAME = "nick_name";
	public static final String My_HEAD_URL = "my_head_url";
	
	public static final String SEND_CODE_URL = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=pushmes";
	public static final String  CHANGE_HUANXIN_PASSWORD = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=change_password";
	
	public static final String  CHANGE_MY_NICK = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php";
	public static final String  REGISTER_HUAN_XIN = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php";
	public static final String  FORGET_PSW_CHANGE = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php";
	public static final String  GET_VERIFY_CODE = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=random_code";
	public static final String  UPDATA_NEW_VERSION = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=getchatversion";
	public static final String  GET_CONTACT_NICK_NAME = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=read_nickname";	
	
	public static final String  GET_MY_HEAD_URL = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=readimage";
	public static final String  CHANGE_MY_HEAD_URL = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=changeimage";
	public static final String  GET_CONTROL_LEARN_DATA_URL = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=grow_record&device_id=";
	
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String CHAT_ROOM = "item_chatroom";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	public static final String CHAT_ROBOT = "item_robots";
	public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
	
	public static final String SEND_CONTACT_INFO_BROADCAST = "com.bbpaw.contact_info";
	public static final String SEND_CONTROL_CHANGE_BROADCAST = "com.bbpaw.contact_change";
	
	//setting
	public static final String IS_SHAKE= "is_shake";
	public static final String IS_VOICE = "is_voice";
	public static final String IS_NOTIFICATION = "is_notify";
	public static final String IS_WIFI_UPDATA= "is_wifi_updata";
	public static final String IS_EXIT_MESSAGE = "is_EXIT_MESSAGE";
	
	
	public static String    CONTROL_BABY_NAME = "";
	public static final int START_SCAN_ADD_FRIEND = 0x0010; 
	public static final int CHAT_FRAGMENT_ADD_FRIEND = 0x0011; 
	public static final int CHAT_FRAGMWENT_DELECT_FRIEND = 0x0012; 
	
	public static final int SEND_CODE_LOCK_BBPAW = 0x0013; 
	public static final int SEND_CODE_OPEN_BBPAW = 0x0014; 
	
	public static final int CLEAR_SOMEONE_ALL_MESSAGE= 0x0014; 
	public static final int CHANGE_NICK_NAME= 0x0015; 
	
	public static final int CHECK_NEW_VERSION_FAILED= 0x0016; 
	public static final int CHECK_NEW_VERSION_SUCCESS= 0x0017; 
	public static final int UPDATA_NEW_VERSION_MESSAGE= 0x0018; 
	public static final int UPDATA_PROGRESS_BAR= 0x0019; 
	public static final int UPDATA_HAVE_ERROR= 0x0020; 
	public static final int SELECTED_CONTROL_BABY= 0x0021; 
	
	public static final int CHANGE_MY_HEAD_SUCCESS = 0x0022;
	public static final int CHANGE_MY_HEAD_FIALED = 0x0023;
	public static final int UPDATA_HEAD_IMAGE = 0x0024;
	public static final int CHANGE_FRIENDS_REMARKNAME = 0x0025;
	
	public static  final  String USER_PIC_UPLOAD_REQ_URL = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=addquestion&typeId=1&content=testtesttesteteset.....&parent_unique=94hvds6wqec&children_unique=00fsad71nda3";// 涓婁紶鍦板潃

	public static final int HTTP_CONNECT_TIMEOUT = 30000;
	public static final int HTTP_READ_TIMEOUT = 30000;

	
}
