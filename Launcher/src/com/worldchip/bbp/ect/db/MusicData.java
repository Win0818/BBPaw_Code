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
	 * ���
	 */
	public static void clearShareMusicList()
	{
		if (shareMusics != null)
		 {
			shareMusics.clear();
		 }
	}
	
	/**
	 * �������
	 * @param context
	 * @param values
	 */
	public static void addShareMusic(Context context, ContentValues values) 
	{
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_MUSIC_TABLE, values);
	}
	
	/**
	 * ����·����ȡ��ȡ����Ӧ����Ƶ
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
	 * ��ȡϵͳ������Ƶ
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
			//����ID��MediaStore.Audio.Media._ID 
			//int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
			//���������� ��MediaStore.Audio.Media.TITLE 
			item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));  
			//������ר������MediaStore.Audio.Media.ALBUM 
			//String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
			//�����ĸ������� MediaStore.Audio.Media.ARTIST 
			//String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
			//�����ļ���·�� ��MediaStore.Audio.Media.DATA 
			item.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));  
			//�������ܲ���ʱ�� ��MediaStore.Audio.Media.DURATION 
			item.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));  
			//�����ļ��Ĵ�С ��MediaStore.Audio.Media.SIZE 
			//long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			item.isSelected = getShareMusicByData(context, cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			items.add(item);
		}
		return items;
	}
    
    /**
     * ɨ��ϵͳ�ڲ�֪ͨ����
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
			//����ID��MediaStore.Audio.Media._ID 
			//int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
			//���������� ��MediaStore.Audio.Media.TITLE 
			item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));  
			//������ר������MediaStore.Audio.Media.ALBUM 
			//String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
			//�����ĸ������� MediaStore.Audio.Media.ARTIST 
			//String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
			//�����ļ���·�� ��MediaStore.Audio.Media.DATA 
			String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			if(data == "" || data.equals(""))
			{
				continue;
			}
			item.setData(data);  
			//�������ܲ���ʱ�� ��MediaStore.Audio.Media.DURATION 
			item.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));  
			//�����ļ��Ĵ�С ��MediaStore.Audio.Media.SIZE 
			//long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			item.isSelected = getShareMusicByData(context, cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			items.add(item);
		}
		return items;
    }
    
    /**
     * �ж�ָ���ļ��Ƿ񱻷���
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
     * ɾ��ָ���ļ�
     */
    public static boolean delShareMusicData(Context context,String data)
    {
    	DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_MUSIC_TABLE, "data='"+data+"'");
		Log.e("delShareMusicData", "falg��"+falg);
		return falg;
    }
    
    /**
	 * ��ȡ��������
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
	 * ���ݱ��ɾ��
	 * @param context
	 * @param id
	 */
	public static void delShareMusicId(Context context, int id) 
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_MUSIC_TABLE, id);
		Log.e("delShareMusicId", "falg��"+falg);
	}
}