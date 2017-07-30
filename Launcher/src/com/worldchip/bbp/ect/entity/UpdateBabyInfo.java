package com.worldchip.bbp.ect.entity;

public class UpdateBabyInfo {
	private String username;
	private int rusltCode;
	private String imagePath;
	private int sex;
	private String brithday;
	private static UpdateBabyInfo mInstance;
	public static UpdateBabyInfo getInstance() {
		if (mInstance == null) {
			mInstance = new UpdateBabyInfo();
		}
		return mInstance;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getRusltCode() {
		return rusltCode;
	}
	public void setRusltCode(int rusltCode) {
		this.rusltCode = rusltCode;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBrithday() {
		return brithday;
	}
	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}
	
}
