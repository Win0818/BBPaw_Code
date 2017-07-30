package com.egreat.adlauncher.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	public Context mContext = null;

	public static final String DATABASE_NAME = "Data.db";

	public static final String DELETE_ALL_USER_TABLE = "drop table if exists app_epg";
	public static final String CREATE_TABLE = "create table if not exists  app_epg"
			+ "(id INTEGER,id_text text, name text,packagename text,edition text,downloadlink text,apkconfig text,poster text)";

	/**
	 * 在SQLiteOpenHelper的子类当中，必须有该构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param factory
	 * @param version
	 *            当前数据库的版本，值必须是整数并且是递增的状态
	 */
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
		mContext = context;
	}

	public DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DBHelper(Context context) {
		this(context, DATABASE_NAME, VERSION);
	}

	// 该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("onCreate database");
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {

	}

}
