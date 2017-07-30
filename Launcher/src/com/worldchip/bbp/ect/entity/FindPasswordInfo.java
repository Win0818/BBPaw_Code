package com.worldchip.bbp.ect.entity;

public class FindPasswordInfo {

	private String originalPassword ="";
	private String account ="";
	private int resultCode = -1;
	
	public String getOriginalPassword() {
		return originalPassword;
	}
	public void setOriginalPassword(String originalPassword) {
		this.originalPassword = originalPassword;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	
}
