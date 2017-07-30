package com.worldchip.bbp.ect.entity;

public class AppInfo {
	
	private int id;
	
	private String packageName;
	
	private String icon;
	
	public boolean isSelected = false;

	public AppInfo() {}
	
	public AppInfo(int id, String packageName,String icon) 
	{
		super();
		this.id = id;
		this.packageName = packageName;
		this.icon = icon;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}