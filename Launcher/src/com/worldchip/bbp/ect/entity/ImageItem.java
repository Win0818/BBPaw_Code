package com.worldchip.bbp.ect.entity;

import java.io.Serializable;

/**
 * 一个图片对象
 * @author WGQ
 */
@SuppressWarnings("serial")
public class ImageItem implements Serializable {
	
	private String imageId;
	private String thumbnailPath;
	private String imagePath;
	private boolean isSelected;
	
	public ImageItem() {}
	
	public ImageItem(String imageId, String thumbnailPath, String imagePath,boolean isSelected) 
	{
		this.imageId = imageId;
		this.thumbnailPath = thumbnailPath;
		this.imagePath = imagePath;
		this.isSelected = isSelected;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}