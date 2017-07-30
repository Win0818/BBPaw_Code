package com.worldchip.bbpawphonechat.entity;

public class HabitCategory {
	
	private String time;
	
	private boolean IsLock;
	
    private int drawableIndex;
    
    private int textIndex;
    
    private int huanxinId;
    
    private int isLockInt;
    
    public HabitCategory() {
    	
	}
    
    public HabitCategory(int huanxinId ,int isLockInt ){
    	this.huanxinId = huanxinId;
    	this.isLockInt = isLockInt;
   	}
    
    
    
	public HabitCategory(String time, int isLock, int drawableIndex,
			int textIndex,int id) {
		super();
		this.time = time;
		this.isLockInt = isLock;
		this.drawableIndex = drawableIndex;
		this.textIndex = textIndex;
		this.huanxinId  = id;
	}
	
	public int getIsLockInt() {
		return isLockInt;
	}

	public void setIsLockInt(int isLockInt) {
		this.isLockInt = isLockInt;
	}

	public int getHuanxinId() {
		return huanxinId;
	}

	public void setHuanxinId(int huanxinId) {
		this.huanxinId = huanxinId;
	}

	public String getTime() {
		
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isIsLock() {
		return IsLock;
	}

	public void setIsLock(boolean isLock) {
		IsLock = isLock;
	}

	public int getDrawableIndex() {
		return drawableIndex;
	}

	public void setDrawableIndex(int drawableIndex) {
		this.drawableIndex = drawableIndex;
	}

	public int getTextIndex() {
		return textIndex;
	}

	public void setTextIndex(int textIndex) {
		this.textIndex = textIndex;
	}
    
    
}
