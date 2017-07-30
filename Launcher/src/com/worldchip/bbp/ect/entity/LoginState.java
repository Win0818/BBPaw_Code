package com.worldchip.bbp.ect.entity;

public class LoginState {

	
	private String userName ="";
	private int ageIndex=-1;
	private int resultCode = -1;
	private String birthday="";
	private int sex = -1;
	private boolean isLogin = false;
	private String clientKey="";
	private String photoUrl="";
	private String account ="";
	private static LoginState mInstance;
	
	public static LoginState getInstance() {
		if (mInstance == null) {
			mInstance = new LoginState();
		}
		return mInstance;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getAgeIndex() {
		return ageIndex;
	}
	public void setAgeIndex(int ageIndex) {
		this.ageIndex = ageIndex;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void loginOut() {
		 userName ="";
		 ageIndex =-1;
		 resultCode = -1;
		 birthday ="";
		 sex=-1;
		 photoUrl="";
		 isLogin = false;
		 clientKey = "";
	}
	
}
