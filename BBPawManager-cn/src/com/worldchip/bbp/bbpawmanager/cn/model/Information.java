package com.worldchip.bbp.bbpawmanager.cn.model;

public class Information extends NotifyMessage {
	
	/**
	 * 
	 */
	public static final int ALL = -1;
	public static final int EXPERT= 0;
	public static final int RECOMMEND= 1;
	public static final int ACTIVITY = 2;
	public static final int OTHER = 3;
	
	public static final int MENU_ALL = 1;
	public static final int MENU_UNREAD = 2;
	public static final int MENU_READ = 3;
	public static final int MENU_FAVORITES = 4;

	
	/** 图文消息 **/
	public static final int MESSAGE_TYPE_NEW = 1;
	/** 活动 **/
	public static final int MESSAGE_TYPE_ACTIVITY = 2;
	/** 广告推送 **/
	public static final int MESSAGE_TYPE_AD_PUSH = 3;
	/** 系统升级 **/
	public static final int MESSAGE_TYPE_SYSUPDATE = 4;
	/** apk **/
	public static final int MESSAGE_TYPE_APK = 5;
	/** 其它资源 **/
	public static final int MESSAGE_TYPE_OTHER = 6;
	
	
	/** 消息详情的icon**/
	private String msgImage;
	
	private String activitiesUrl;
	
	private String downloadUrl;
	
	public String getMsgImage() {
		return msgImage;
	}
	public void setMsgImage(String msgImage) {
		this.msgImage = msgImage;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getActivitiesUrl() {
		return activitiesUrl;
	}
	public void setActivitiesUrl(String activitiesUrl) {
		this.activitiesUrl = activitiesUrl;
	}
	
}
