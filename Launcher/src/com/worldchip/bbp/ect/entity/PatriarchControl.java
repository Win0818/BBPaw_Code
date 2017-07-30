package com.worldchip.bbp.ect.entity;

/**
 * ¼Ò³¤¿ØÖÆ
 * @author WGQ
 * 2014-06-06
 */
public class PatriarchControl {
	
	private String name;
	
	private int drawableTopImg;
	
	private int index;
	
	public PatriarchControl(){};
	
	public PatriarchControl(String name, int drawableTopImg, int index) 
	{
		this.name = name;
		this.drawableTopImg = drawableTopImg;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDrawableTopImg() {
		return drawableTopImg;
	}

	public void setDrawableTopImg(int drawableTopImg) {
		this.drawableTopImg = drawableTopImg;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}