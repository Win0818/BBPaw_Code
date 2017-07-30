package com.worldchip.bbp.bbpawmanager.cn.model;

public class GrowthMessage {

	/*public static final int ITEM = 0;
	public static final int SECTION = 1;

	public final int type;
	public final String text;

	public int sectionPosition;
	public int listPosition;*/
	public String id;
	public String date;
	public String studyTime;
	public String studyConetnt;
	public String iconPath;

	public GrowthMessage(){}
	
	public GrowthMessage(String date, String studyTime, String studyConetnt) {
	    this.date = date;
	    this.studyTime = studyTime;
	    this.studyConetnt = studyConetnt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStudyTime() {
		return studyTime;
	}

	public void setStudyTime(String studyTime) {
		this.studyTime = studyTime;
	}

	public String getStudyConetnt() {
		return studyConetnt;
	}

	public void setStudyConetnt(String studyConetnt) {
		this.studyConetnt = studyConetnt;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	
}
