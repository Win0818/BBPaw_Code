package com.worldchip.bbp.ect.entity;

import java.io.Serializable;
import java.util.List;

/**
 * �?个目录的相册对象
 * @author WGQ
 */
@SuppressWarnings("serial")
public class ImageBucket implements Serializable{
	
	private int count = 0;
	private String bucketName;
	private List<ImageItem> imageList;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public List<ImageItem> getImageList() {
		return imageList;
	}
	public void setImageList(List<ImageItem> imageList) {
		this.imageList = imageList;
	}
}