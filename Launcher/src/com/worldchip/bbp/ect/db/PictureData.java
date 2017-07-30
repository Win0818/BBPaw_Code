package com.worldchip.bbp.ect.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.PictureInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class PictureData {

	private static List<PictureInfo> sharePictures = null;
	
	/**
	 * 清除
	 */
	public static void clearSharePictureList()
	{
		if (sharePictures != null)
		 {
			sharePictures.clear();
		 }
	}
	
	/**
	 * 删除对应的图片
	 */
	public static void delSharePicture(PictureInfo pictureInfo)
	{
		if (sharePictures != null)
		{
			sharePictures.remove(pictureInfo);
		}
	}
	
	/**
	 * 添加图片
	 * @param context
	 * @param values
	 */
	public static void addSharePicture(Context context, ContentValues values) 
	{
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_PICTURE_TABLE, values);
	}
	
	/**
	 * 根据路径获取获取所对应的图片
	 */
	public static PictureInfo getPictureInfo(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_PICTURE_TABLE, "where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			while(cursor.moveToFirst())
			{
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
				String datas = cursor.getString(cursor.getColumnIndex("data"));
				cursor.close();
				return new PictureInfo(id, displayName, datas);
			}
		}
		return null;
	}
	
	/**
	 * 获取分享的所有图片
	 */
	public static List<PictureInfo> getLocalSharePictureDatas(Context context)
	{
		if(sharePictures != null && sharePictures.size() >0)
		{
			return sharePictures;
		}
		sharePictures = new ArrayList<PictureInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_PICTURE_TABLE, "order by _id desc");
		while(cursor.moveToNext())
		{
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
			String data = cursor.getString(cursor.getColumnIndex("data"));
			File f = new File(data);
			if(f.exists())
            {
				PictureInfo pictureInfo = new PictureInfo(id,displayName,data);
				sharePictures.add(pictureInfo); 
            }else{
            	delSharePictureId(context, id);
            }
		}
		cursor.close();
		return sharePictures;
	}
	
	/**
	 * 取消分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean delSharePictureId(Context context,long id)
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_PICTURE_TABLE, id);
		Log.e("delSharePictureId", "falg："+falg);
		return falg;
	}
	
	/**
	 * 取消分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean delSharePictureData(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_PICTURE_TABLE, "data='"+data+"'");
		Log.e("delSharePictureData", "falg："+falg);
		return falg;
	}
	
	/**
	 * 判断该图片是否已经分享
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean getSharePictureByData(Context context,String data)
	{
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursorBySql("select count(*) from '"+ DBSqlBuilder.BBP_PICTURE_TABLE +"' where data = '"+ data +"' order by _id desc");
		if(cursor != null)
		{
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			Log.e("Picture", "count---"+count);
			if(count > 0)
			{
				cursor.close();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取系统所有图片
	 * @param context
	 * @return
	 */
    public static List<PictureInfo> getLocalPictureDatas(Context context)
    {
		List<PictureInfo> items = new ArrayList<PictureInfo>();
		String[] columns = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		if(cursor == null)
		{
			return items;
		}
		PictureInfo item = null;
		while (cursor.moveToNext()) 
		{
			item = new PictureInfo();
			item.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
			item.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			item.isSelected = PictureData.getSharePictureByData(context,cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			items.add(item);
		}
		return items;
	}
}