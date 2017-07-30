package com.worldchip.bbp.ect.entity;

public class UpdatePassword {
   String userName;
   int resultCode;
   
   private static UpdatePassword mInstance;
	public static UpdatePassword getInstance() {
		if (mInstance == null) {
			mInstance = new UpdatePassword();
		}
		return mInstance;
	}
	
	
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public int getResultCode() {
	return resultCode;
}
public void setResultCode(int resultCode) {
	this.resultCode = resultCode;
}
   
}
