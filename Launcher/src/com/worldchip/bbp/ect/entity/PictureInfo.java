package com.worldchip.bbp.ect.entity;

public class PictureInfo {
	
	private int id;
	
	private String displayName;
	
	private String data;
	
	public boolean isSelected = false;
	
	public PictureInfo(){}
	
	public PictureInfo(int id, String displayName, String data)
	{
		super();
		this.id = id;
		this.displayName = displayName;
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}