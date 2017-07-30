package com.worldchip.bbpawphonechat.entity;

public class LearnLog {
	
	
	private String id;
	
	private String learnDate;
	
	private String LearnTime;
	
	private String LearnContent;
	
	private String apkIcon;
	
    public LearnLog() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getApkIcon() {
		return apkIcon;
		
	}
	
	public void setApkIcon(String apkIcon) {
		this.apkIcon = apkIcon;
	}
	
	public String getLearnDate() {
		return learnDate;
	}

	public void setLearnDate(String learnDate) {
		this.learnDate = learnDate;
	}

	public String getLearnTime() {
		return LearnTime;
	}

	public void setLearnTime(String learnTime) {
		LearnTime = learnTime;
	}

	public String getLearnContent() {
		return LearnContent;
	}

	public void setLearnContent(String learnContent) {
		LearnContent = learnContent;
	}
    
}
