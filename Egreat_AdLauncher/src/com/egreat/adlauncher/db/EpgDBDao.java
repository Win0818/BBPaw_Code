package com.egreat.adlauncher.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EpgDBDao {
	private static String LOGTAG = "EpgDBDao";

	private Context mContext;
	private DBHelper dbHelper;

	public EpgDBDao(Context context) {
		this.mContext = context;
		dbHelper = new DBHelper(context);
	}

	public void addApp(ApkInfo app) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("id", app.getId());
			values.put("id_text", app.getId_text());
			values.put("name", app.getName());
			values.put("packagename", app.getPackagename());
			values.put("edition", app.getEdition());
			values.put("downloadlink", app.getDownloadlink());
			values.put("apkconfig", app.getApkconfig());
			values.put("poster", app.getPoster());
			db.insert("app_epg", null, values);
			db.close();
		}
	}

	public List<ApkInfo> getAppList() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		List<ApkInfo> list = new ArrayList<ApkInfo>();
		if (db.isOpen()) {
			Cursor cur = db.rawQuery("select id,id_text,name,packagename,edition,downloadlink,apkconfig,poster from app_epg", null);
			while (cur.moveToNext()) {
				ApkInfo tmp = new ApkInfo();
				tmp.setId(cur.getInt(cur.getColumnIndex("id")));
				tmp.setId_text(cur.getString(cur.getColumnIndex("id_text")));
				tmp.setName(cur.getString(cur.getColumnIndex("name")));
				tmp.setPackagename(cur.getString(cur.getColumnIndex("packagename")));
				tmp.setEdition(cur.getString(cur.getColumnIndex("edition")));
				tmp.setDownloadlink(cur.getString(cur.getColumnIndex("downloadlink")));
				tmp.setApkconfig(cur.getString(cur.getColumnIndex("apkconfig")));
				tmp.setPoster(cur.getString(cur.getColumnIndex("poster")));
				list.add(tmp);
			}
			cur.close();
			db.close();
		}
		return list;
	}

	public void deleteApp(String packagename) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("packagename", packagename);
			db.delete("app_epg", "packagename=?", new String[] { packagename });
			db.close();
		}
	}

	public void deleteApp(int id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete("app_epg", "id=?", new String[] { String.valueOf(id) });
			db.close();
		}
	}

	public void updateApp(int fromid, ApkInfo to) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("id", to.getId());
			values.put("id_text", to.getId_text());
			values.put("name", to.getName());
			values.put("packagename", to.getPackagename());
			values.put("edition", to.getEdition());
			values.put("downloadlink", to.getDownloadlink());
			values.put("apkconfig", to.getApkconfig());
			values.put("poster", to.getPoster());
			db.update("app_epg", values, "id=?", new String[] { String.valueOf(fromid) });
			Log.d(LOGTAG,"update database! id="+to.getId());
			Log.d(LOGTAG,"update database! edition="+to.getEdition());
			db.close();
		}
	}

	public int getAppCount() {
		int count = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cur = db.rawQuery("Select count(*) from app_epg", null);
			if (cur.moveToFirst() == false) {
				return 0;
			}
			count = cur.getInt(0);
			cur.close();
			db.close();
		}
		return count;
	}

}
