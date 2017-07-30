package com.worldchip.bbp.ect.entity;

import android.graphics.Bitmap;

public class VideoInfo {
	
	private int id;
	
	private String title;
	
	private String displayName;
	
	private String data;
	
	private String duration;
	
	private Bitmap icon;
	
	public boolean isSelected = false;
	
	public VideoInfo(){}

	public VideoInfo(int id, String title, String displayName, String data,String duration, Bitmap icon) 
	{
		super();
		this.id = id;
		this.title = title;
		this.displayName = displayName;
		this.data = data;
		this.duration = duration;
		this.icon = icon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
}