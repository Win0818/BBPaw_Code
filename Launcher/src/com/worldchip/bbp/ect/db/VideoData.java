package com.worldchip.bbp.ect.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.VideoInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.util.Log;

public class VideoData {

	private static List<VideoInfo> shareVideos = null;
	
	/**
	 * 清除
	 */
	public static void clearShareVideoList()
	{
		if (shareVideos != null)
		 {
			 shareVideos.clear();
		 }
	}
	
	/**
	 * 添加视频
	 * @param context
	 * @param values
	 */
	public static void addShareVideo(Context context, ContentValues values) 
	{
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_VIDEO_TABLE, values);
	}
	
	/**
	 * 根据路径获取获取所对应的视频
	 */
	public static VideoInfo getVideoInfo(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_VIDEO_TABLE, "where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			while(cursor.moveToFirst())
			{
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String title = cursor.getString(cursor.getColumnIndex("title"));
				String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
				String datas = cursor.getString(cursor.getColumnIndex("data"));
				String duration = cursor.getString(cursor.getColumnIndex("duration"));
				cursor.close();
				return new VideoInfo(id, title, displayName, datas, duration, getVideoCapture(context,datas));
			}
		}
		return null;
	}
	
	/**
	 * 获取所有视频
	 */
	public static List<VideoInfo> getLocalShareVideoDatas(Context context)
	{
		if(shareVideos != null && shareVideos.size() >0)
		{
			return shareVideos;
		}
		shareVideos = new ArrayList<VideoInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_VIDEO_TABLE, "order by _id desc");
		while(cursor.moveToNext())
		{
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
			String data = cursor.getString(cursor.getColumnIndex("data"));
			String duration = cursor.getString(cursor.getColumnIndex("duration"));
			File f = new File(data);
			if(f.exists())
            {
				VideoInfo videoInfo = new VideoInfo(id,title,displayName,data,duration,getVideoCapture(context, data));
				shareVideos.add(videoInfo); 
            }else{
            	delShareVideoId(context, id);
            }
		}
		cursor.close();
		return shareVideos;
	}
	
	/**
	 * 取消分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean delShareVideoId(Context context,long id)
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_VIDEO_TABLE, id);
		Log.e("delShareVideo", "falg："+falg);
		return falg;
	}
	
	/**
	 * 取消分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean delShareVideoData(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_VIDEO_TABLE, "data='"+data+"'");
		Log.e("delShareVideo", "falg："+falg);
		return falg;
	}
	
	/**
	 * 判断该视频是否已经分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean getShareVideoByData(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursorBySql("select count(*) from '"+ DBSqlBuilder.BBP_VIDEO_TABLE +"' where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			Log.e("Video", "count---"+count);
			if(count > 0)
			{
				cursor.close();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取系统所有视频
	 * @param context
	 * @return
	 */
    public static List<VideoInfo> getLocalVideoDatas(Context context)
    {
		List<VideoInfo> items = new ArrayList<VideoInfo>();
		String[] columns = { Media.DISPLAY_NAME, Media.TITLE, Media.DATA, Media.DURATION };
		Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if(cursor == null)
		{
			return items;
		}
		VideoInfo item = null;
		while (cursor.moveToNext()) 
		{
			item = new VideoInfo();
			item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE)));
			item.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)));
			item.setData(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)));
			item.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(Media.DURATION)));
			item.setIcon(getVideoCapture(context, cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))));
			item.isSelected = getShareVideoByData(context, cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)));
			items.add(item);
		}
		return items;
	}
	
	public static Bitmap getVideoCapture(Context context, String data)
	{
		Bitmap bitmap = null;
		if(data ==null || data.equals("")) 
		{
			return null;
		}
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Images.Thumbnails.MICRO_KIND);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 145, 122,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}