package com.worldchip.bbp.ect.entity;

public class MusicInfo {
	
	public enum MusicState
    {
		STOP, PAUSE, PLAYING
    }
	
	private int id;
	
	private String title;
	
	private String data;
	
	private String duration;
	
	public boolean isSelected = false;
	
	private MusicState state = MusicState.STOP; 
	
	public MusicInfo(){}

	public MusicInfo(int id, String title, String data, String duration) 
	{
		super();
		this.id = id;
		this.title = title;
		this.data = data;
		this.duration = duration;
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

	public MusicState getState() {
		return state;
	}

	public void setState(MusicState state) {
		this.state = state;
	}
	
	
}