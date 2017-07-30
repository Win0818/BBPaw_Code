package com.worldchip.bbp.ect.entity;

public class IconInfo {
	
	private int index;
	private int imageBackground;
	private int imageSrc;
	private String iconName;
	private int selectImage;
	//public static boolean isIconCLick = false;
	
	public IconInfo() {}
	
	public IconInfo(int index, int imageBackground, int imageSrc,
			String iconName) {
		super();
		this.index = index;
		this.imageBackground = imageBackground;
		this.imageSrc = imageSrc;
		this.iconName = iconName;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getImageBackground() {
		return imageBackground;
	}

	public void setImageBackground(int imageBackground) {
		this.imageBackground = imageBackground;
	}

	public int getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(int imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public int getSelectImage() {
		return selectImage;
	}

	public void setSelectImage(int selectImage) {
		this.selectImage = selectImage;
	}
	
	
}