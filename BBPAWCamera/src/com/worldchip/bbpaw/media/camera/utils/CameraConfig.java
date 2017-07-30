package com.worldchip.bbpaw.media.camera.utils;

import android.os.Environment;

public class CameraConfig {
	
    /**
     * Camera config
     */
	public static final boolean DEBUG = true;
	public static final String SAMPLE_FILE_DIR = Environment.getExternalStorageDirectory().getPath()+"/BBPAW/BBPawCamera";
    //public static final String VIDEOS_FILES_PATH = SAMPLE_FILE_DIR +"/videos/";
    // public static final String POTOS_FILES_PATH = SAMPLE_FILE_DIR +"/photos/";
	public static final String VIDEOS_FILES_PATH = SAMPLE_FILE_DIR;
	 public static final String POTOS_FILES_PATH = SAMPLE_FILE_DIR;
	/** 相片分辨率 宽**/
	public static final int CAMERA_PIC_SIZE_WIDTH = 1024;
	/** 相片分辨率 高**/
	public static final int CAMERA_PIC_SIZE_HEIGHT = 600;
	/** 后置摄像头**/
	public static final int CAMERA_FACING_BACK = 0;
	/** 前置摄像头**/
	public static final int CAMERA_FACING_FRONT = 1;
	public static final int DEFAULT_CAMERA_FACING = CAMERA_FACING_BACK;
	/** 相机模式**/
	public static final int CAMERA_PHOTO_MODE = 0;
	/** 录像模式**/
	public static final int CAMERA_VIDEO_MODE = 1;
	public static final int DEFAULT_CAMERA_MODE = CAMERA_PHOTO_MODE;
	
	public static final int VIDEO_SIZE_WIDTH = 1280;
	public static final int VIDEO_SIZE_HEIGHT = 720;
	
}
