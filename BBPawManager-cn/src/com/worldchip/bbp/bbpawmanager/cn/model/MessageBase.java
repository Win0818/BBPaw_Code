package com.worldchip.bbp.bbpawmanager.cn.model;

public class MessageBase {

	private int id;
	
	/**
	 * 消息id
	 */
	private int msgId;
	
	/***key:maintype
	 * 100: 首页公告
	 * 101:成长日志
	 * 98:通知消息
	 * 102:消息命令
	 */
	private int mainType;
	
	/**key:type
	 * 消息属于哪种类型 (育儿专家、优能推荐、活动、其他)
	 * 0:育儿专家
	 * 1:优能推荐
	 * 2:活动
	 * 3:其他
	 */
	private int type;
	
	/**key: order_type
	 * 消息类型(图文消息、活动、广告推送、系统升级、apk、其他资源)
	 * 1:图文信息
	 * 2:活动
	 * 3:广告推送
	 * 4:系统升级包
	 * 5:apk
	 * 6:其他资源
	 */
	private int msgType;
	private String icon;
	private String title;
	private String description;
	private String content;
	private String date;
	
	private boolean isRead;
	
	private boolean isFavorites;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public int getMainType() {
		return mainType;
	}

	public void setMainType(int mainType) {
		this.mainType = mainType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isFavorites() {
		return isFavorites;
	}

	public void setFavorites(boolean isFavorites) {
		this.isFavorites = isFavorites;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
