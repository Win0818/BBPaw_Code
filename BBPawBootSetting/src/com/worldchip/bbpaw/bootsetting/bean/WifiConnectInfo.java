package com.worldchip.bbpaw.bootsetting.bean;

public class WifiConnectInfo {

	private String SSID;
	private String password;
	private int securityType;
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSecurityType() {
		return securityType;
	}
	public void setSecurityType(int securityType) {
		this.securityType = securityType;
	}
	
	
}
