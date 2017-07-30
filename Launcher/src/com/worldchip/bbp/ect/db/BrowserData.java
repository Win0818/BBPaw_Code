package com.worldchip.bbp.ect.db;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.BrowserInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
public class BrowserData {
	/**
	 * 添加APP
	 * @param context
	 * @param values
	 */
	public static boolean addBrowserAddress(Context context, ContentValues values) {
		DBHelper helper = new DBHelper(context);
		return helper.insertValues(DBSqlBuilder.BBP_BROWSER_TABLE, values);
	}

	/**
	 * 获取浏览器信息
	 */
	public static BrowserInfo getBrowserInfo(Context context, String title) {
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_BROWSER_TABLE,
				"where title = '" + title + "' order by _id desc");
		if (cursor != null) {
			while (cursor.moveToFirst()) {
				BrowserInfo info = new BrowserInfo();
				info.Id = cursor.getInt(cursor.getColumnIndex("_id"));
				info.title = cursor.getString(cursor.getColumnIndex("title"));
				info.url = cursor.getString(cursor.getColumnIndex("url"));
				cursor.close();
				return info;
			}
		}
		return null;
	}
	
	public static Boolean deleteBrowserInfo(Context context, String title) {
		DBHelper helper = new DBHelper(context);
		Log.e("--deleteBrowserInfo--","title is "+title);
		return helper.deleteRow(DBSqlBuilder.BBP_BROWSER_TABLE,
				"title = '" + title + "'");
	}
	
	public static BrowserInfo getBrowserInfoByUrl(Context context, String url) {
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_BROWSER_TABLE,
				"where url = '" + url + "' order by _id desc");
		if (cursor != null) {
			while (cursor.moveToFirst()) {
				BrowserInfo info = new BrowserInfo();
				info.Id = cursor.getInt(cursor.getColumnIndex("_id"));
				info.title = cursor.getString(cursor.getColumnIndex("title"));
				info.url = cursor.getString(cursor.getColumnIndex("url"));
				cursor.close();
				return info;
			}
		}
		return null;
	}

	/**
	 * 获取所有BrowserInfo
	 */
	public static List<BrowserInfo> getBrowserAddressList(Context context) {
		List<BrowserInfo> broserList = new ArrayList<BrowserInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_BROWSER_TABLE,
				"order by _id desc");
		while (cursor.moveToNext()) {
			BrowserInfo info = new BrowserInfo();
			info.Id = cursor.getInt(cursor.getColumnIndex("_id"));
			info.title = cursor.getString(cursor.getColumnIndex("title"));
			info.url = cursor.getString(cursor.getColumnIndex("url"));
			broserList.add(info);
		}
		cursor.close();
		return broserList;
	}

}