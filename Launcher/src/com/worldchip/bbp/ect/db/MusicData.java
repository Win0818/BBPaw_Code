package com.worldchip.bbp.ect.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.MusicInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class MusicData {
	
	private static List<MusicInfo> shareMusics = null;
	
	/**
	 * 清除
	 */
	public static void clearShareMusicList()
	{
		if (shareMusics != null)
		 {
			shareMusics.clear();
		 }
	}
	
	/**
	 * 添加音乐
	 * @param context
	 * @param values
	 */
	public static void addShareMusic(Context context, ContentValues values) 
	{
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_MUSIC_TABLE, values);
	}
	
	/**
	 * 根据路径获取获取所对应的视频
	 */
	public static MusicInfo getMusicInfo(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_MUSIC_TABLE, "where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			while(cursor.moveToFirst())
			{
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String title = cursor.getString(cursor.getColumnIndex("title"));
				String datas = cursor.getString(cursor.getColumnIndex("data"));
				String duration = cursor.getString(cursor.getColumnIndex("duration"));
				cursor.close();
				return new MusicInfo(id, title, datas, duration);
			}
		}
		return null;
	}
	
	/**
	 * 获取系统所有视频
	 * @param context
	 * @return
	 */
    public static List<MusicInfo> getLocalMusicDatas(Context context)
    {
		List<MusicInfo> items = new ArrayList<MusicInfo>();
		String[] columns = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION };
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if(cursor == null)
		{
			return items;
		}
		MusicInfo item=null;
		while (cursor.moveToNext()) 
		{
			item = new MusicInfo();
			//歌曲ID：MediaStore.Audio.Media._ID 
			//int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
			//歌曲的名称 ：MediaStore.Audio.Media.TITLE 
			item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));  
			//歌曲的专辑名：MediaStore.Audio.Media.ALBUM 
			//String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
			//歌曲的歌手名： MediaStore.Audio.Media.ARTIST 
			//String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
			//歌曲文件的路径 ：MediaStore.Audio.Media.DATA 
			item.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));  
			//歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION 
			item.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));  
			//歌曲文件的大小 ：MediaStore.Audio.Media.SIZE 
			//long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			item.isSelected = getShareMusicByData(context, cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			items.add(item);
		}
		return items;
	}
    
    /**
     * 扫描系统内部通知铃声
     */
    public static List<MusicInfo> getLocalRing(Context context)
    {
    	List<MusicInfo> items = new ArrayList<MusicInfo>();
		String[] columns = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION };
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, columns,"is_notification != ?",new String[] {"0"},"_id asc");
		if(cursor == null)
		{
			return items;
		}
		MusicInfo item=null;
		while (cursor.moveToNext()) 
		{
			item = new MusicInfo();
			//歌曲ID：MediaStore.Audio.Media._ID 
			//int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
			//歌曲的名称 ：MediaStore.Audio.Media.TITLE 
			item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));  
			//歌曲的专辑名：MediaStore.Audio.Media.ALBUM 
			//String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
			//歌曲的歌手名： MediaStore.Audio.Media.ARTIST 
			//String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
			//歌曲文件的路径 ：MediaStore.Audio.Media.DATA 
			String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			if(data == "" || data.equals(""))
			{
				continue;
			}
			item.setData(data);  
			//歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION 
			item.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));  
			//歌曲文件的大小 ：MediaStore.Audio.Media.SIZE 
			//long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			item.isSelected = getShareMusicByData(context, cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			items.add(item);
		}
		return items;
    }
    
    /**
     * 判断指定文件是否被分享
     */
    public static boolean getShareMusicByData(Context context,String data)
    {
    	DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursorBySql("select count(*) from '"+ DBSqlBuilder.BBP_MUSIC_TABLE +"' where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			if(count > 0)
			{
				cursor.close();
				return true;
			}
		}
		return false;
    }
    
    /**
     * 删除指定文件
     */
    public static boolean delShareMusicData(Context context,String data)
    {
    	DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_MUSIC_TABLE, "data='"+data+"'");
		Log.e("delShareMusicData", "falg："+falg);
		return falg;
    }
    
    /**
	 * 获取所有音乐
	 */
	public static List<MusicInfo> getLocalShareMusicDatas(Context context)
	{
		if(shareMusics != null && shareMusics.size() >0)
		{
			return shareMusics;
		}
		shareMusics = new ArrayList<MusicInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_MUSIC_TABLE, "order by _id desc");
		while(cursor.moveToNext())
		{
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String data = cursor.getString(cursor.getColumnIndex("data"));
			String duration = cursor.getString(cursor.getColumnIndex("duration"));
			File f = new File(data);
			if(f.exists())
            {
				MusicInfo videoInfo = new MusicInfo(id,title,data,duration);
				shareMusics.add(videoInfo); 
            }else{
            	delShareMusicId(context, id);
            }
		}
		cursor.close();
		return shareMusics;
	}

	/**
	 * 根据编号删除
	 * @param context
	 * @param id
	 */
	public static void delShareMusicId(Context context, int id) 
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_MUSIC_TABLE, id);
		Log.e("delShareMusicId", "falg："+falg);
	}
}