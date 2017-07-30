package com.worldchip.bbp.ect.installapk;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;


public class Utils {
	
	public static final String VIDEO_TO_PATH ="/mnt/sdcard/BBPaw/videos/";
	
	protected static final String TAG = "--Utils--";
	public static boolean isMainActivityStarted=false;
	public static int preInstallApksCount=0;
	public static int installedApksCount=0;

	public static final String VIDEO_COPY_ACTION ="com.worldchip.video.copy.finish";
	public static final String INSTALL_APK_ACTION ="com.worldchip.apkinstall.finish";
	
	public static List<CopyFile> mCopyFileList = new ArrayList<CopyFile>();
	
	public static String convertFileSize(long filesize)
    {
	    String strUnit="Bytes";
	    String strAfterComma="";
	    int intDivisor=1;
	    if(filesize>=1024*1024)
	    {
	    strUnit = "MB";
	    intDivisor=1024*1024;
	    }
	    else if(filesize>=1024)
	    {
	    strUnit = "KB";
	    intDivisor=1024;
	    }
	    if(intDivisor==1) return filesize + " " + strUnit;
	    strAfterComma = "" + 100 * (filesize % intDivisor) / intDivisor ;
	    if(strAfterComma=="") strAfterComma=".0";
	    return filesize / intDivisor + "." + strAfterComma + " " + strUnit;
    }
	
	//added by guofq
		public static void getCopyFileList(final List<CopyFile> copyFileList, File file) {
			if(file==null) return;
			
			file.listFiles(new FileFilter() {
				@SuppressLint("DefaultLocale")
				public boolean accept(File file){
					String name = file.getName();
					int i = name.lastIndexOf('.');
					if (i > -1) {
						CopyFile copyFile = new CopyFile();
						String fileName = name.substring(0, i);
						String extend = name.substring(i);
						if(extend==null) return false;
						if(getVideoExtens().contains(extend.toUpperCase())){
							copyFile.mFileName = name;
							copyFile.mFilePath = file.getAbsolutePath();
							Log.e(TAG, "copyFile.mFileName="+copyFile.mFileName);
							copyFileList.add(copyFile);
						}
						
					} 
					return false;
				}
			});
		}
		
		private static List<String> VideoExtens = null;

		public static List<String> getVideoExtens() 
		{
			if (VideoExtens == null) 
			{
				VideoExtens = new ArrayList<String>();
			}
			if (VideoExtens.size() < 1)
			{
				VideoExtens.add(".MP4");
				VideoExtens.add(".AVI");
				VideoExtens.add(".RMB");
				VideoExtens.add(".RMVB");
				VideoExtens.add(".FLV");
				VideoExtens.add(".MKV");
				VideoExtens.add(".MOV");
				VideoExtens.add(".TS");
				VideoExtens.add(".MPG");
				VideoExtens.add(".VOB");
				VideoExtens.add(".WMV");
				VideoExtens.add(".TP");
			}
			return VideoExtens;
		}
}
