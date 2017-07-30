package com.worldchip.bbpaw.media.camera.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.worldchip.bbpaw.media.camera.R;
import com.worldchip.bbpaw.media.camera.activity.MyApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Utils {

	public static final long UNAVAILABLE = -1L;
    public static final long PREPARING = -2L;
    public static final long UNKNOWN_SIZE = -3L;
    public static final long LOW_STORAGE= 5000;
    private static long mLastDate;
    private static int mSameSecondCount;
    private static Object fileName = new Object();
    //public static final int FINAL_PICTURE_WIDTH = 800;
    //public static final int FINAL_PICTURE_HEIGHT = 600;
    
    
    /**
	 * 屏幕的宽,像素
	 */
	public static int SCREEN_WIDTH(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 屏幕的高,像素
	 */
	public static int SCREEN_HEIGHT(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	public static File createVideoOuputFile(Context context) {
		File videoFile;
		synchronized (fileName) {
        String result = getFileName(context.getString(R.string.video_file_name_format));
        String fileName = result + convertOutputFormatToFileExt(MediaRecorder.OutputFormat.MPEG_4);
        createRecordFile();
		File audioFile = new File(CameraConfig.VIDEOS_FILES_PATH);
		if (!audioFile.exists()) {
			audioFile.mkdirs();
		}
		videoFile = new File(audioFile.getPath(), fileName);
        return videoFile;
		}
    }
    
    
	public static File createPicOuputFile(Context context) {
		File picOutputFile;
		synchronized (fileName) {
			String result = getFileName(context.getString(R.string.pic_file_name_format));
			String fileName =  result + ".jpg";
			createRecordFile();
			File picFile = new File(CameraConfig.POTOS_FILES_PATH);
			if (!picFile.exists()) {
				picFile.mkdirs();
			}
			picOutputFile = new File(picFile.getPath(), fileName);
			return picOutputFile;
		}
	}
    
    private static String getFileName(String formatStr) {
    	long dateTaken = System.currentTimeMillis();
		Date date = new Date(dateTaken);
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
		String result = dateFormat.format(date);
		if (dateTaken / 1000 == mLastDate / 1000) {
			 mSameSecondCount++;
			 result += "_" + mSameSecondCount;
		} else {
			mLastDate = dateTaken;
			mSameSecondCount = 0;
		}
		return result;
    }
    
    public static File createRecordFile() {
    	File recordFile= new File(CameraConfig.SAMPLE_FILE_DIR);
        if(!recordFile.exists()){
        	recordFile.mkdirs();
		}
        return recordFile;
    }
    
	public static String convertOutputFormatToFileExt(int outputFileFormat) {
        if (outputFileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            return ".mp4";
        }
        return ".3gp";
    }
	
	public static long getAvailableSpace() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_CHECKING.equals(state)) {
            return PREPARING;
        }
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return UNAVAILABLE;
        }

        File path =Environment.getExternalStorageDirectory(); 

        try {
            StatFs stat = new StatFs(path.getPath());
            Log.e("lee", "AvailableSpace == "+stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return stat.getAvailableBlocks() * (long) stat.getBlockSize();
        } catch (Exception e) {
        }
        return UNKNOWN_SIZE;
    }

	public static boolean diskSpaceAvailable() {
		File SDCardDirectory = Environment.getExternalStorageDirectory();
        StatFs fs = new StatFs(SDCardDirectory.getAbsolutePath());
        // keep one free block
        return fs.getAvailableBlocks() > 1;
    }
	
	public static void delectTempVideo(String videoFilePath) {
		File currTempFile = new File(videoFilePath);
		if (currTempFile.exists()) {
			currTempFile.delete();
		}
	}
	
	public static void updateGallery(Context context, String filePath) {
		/*MediaScannerConnection.scanFile(context, new String[] { filePath },
				null, new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						 Log.e("ExternalStorage", "Scanned " + path + " ------->>>>>");
						// Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});*/
		ScannerClient.getInstance().scanPath(filePath);
	}
	
	
	public static float getCameraPreviewAspectRatio() {
		Resources resources = MyApplication.getAppContext().getResources();
		int[] intArray = resources.getIntArray(R.array.camera_preview_aspect_ratio);
		float w = intArray[0];
		float h = intArray[1];
		return w / h;
	}
}
