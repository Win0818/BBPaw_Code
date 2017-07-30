package com.worldchip.bbpawphonechat.entity;

import android.graphics.Bitmap;

public class Contact {
	
	private Bitmap  headBitmap;
	
	private int  drawableId;
	
	private String  contactName;
	
	private int messageNumber;
	
	private boolean isBound;
	
	
	public Contact() {
		
	}

	public Contact(Bitmap headBitmap, int messageNumber, boolean isBound , String name) {
		super();
		this.headBitmap = headBitmap;
		this.messageNumber = messageNumber;
		this.isBound = isBound;
		this.contactName = name;
	}

	public Bitmap getHeadBitmap() {
		return headBitmap;
	}

	public void setHeadBitmap(Bitmap headBitmap) {
		this.headBitmap = headBitmap;
	}

	public int getMessageNumber() {
		return messageNumber;
	}

	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}


	public boolean isBound() {
		return isBound;
	}

	public void setBound(boolean isBound) {
		this.isBound = isBound;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}
	
	
	
	

}
