package com.worldchip.bbp.bbpawmanager.cn.model;

public class NotifyMessage extends MessageBase{
	
	public static final int OPERATION_OPEN_APP = 1;
	public static final int OPERATION_OPEN_ACTIVITY = 2;
	public static final int OPERATION_OPEN_URL = 3;
	public static final int OPERATION_CUSTOM = 4;
	
	public static final int NOTIFY_TYPE_MUSIC = 0;
	public static final int NOTIFY_TYPE_VIBRA = 1;
	public static final int NOTIFY_TYPE_LED = 2;
	/** 点击通知操作 **/
	private int clickOperation;
	private String operationValue;
	/** 提醒方式 **/
	private String notifyType;
	private String musicUri;
	
	public int getClickOperation() {
		return clickOperation;
	}
	public void setClickOperation(int clickOperation) {
		this.clickOperation = clickOperation;
	}
	public String getOperationValue() {
		return operationValue;
	}
	public void setOperationValue(String operationValue) {
		this.operationValue = operationValue;
	}
	public String getNotifyType() {
		return notifyType;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	
	public String getMusicUri() {
		return musicUri;
	}
	public void setMusicUri(String musicUri) {
		this.musicUri = musicUri;
	}
}
