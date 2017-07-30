package com.worldchip.bbpawphonechat.db;

import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbpawphonechat.entity.HabitCategory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HabitDao {
	
	public static final String TABLE_NAME = "habit";
	public static final String CODE_ID = "codeid";
	public static final String IS_LOCK = "islock";
	
	private DbOpenHelper dbHelper;
	
	public HabitDao(Context  context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}
	
	public void initHabitData(List<HabitCategory>  mhaHabitCategories){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, null, null);
			for (HabitCategory habitCategory : mhaHabitCategories) {
				ContentValues values = new ContentValues();
				values.put(CODE_ID, habitCategory.getHuanxinId());
				values.put(IS_LOCK, habitCategory.isIsLock());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	
	//get habit data
	public   List<HabitCategory>  getHabitList(){
		SQLiteDatabase  db  =   dbHelper.getReadableDatabase() ;
		List<HabitCategory>  habitCategories = new ArrayList<HabitCategory>();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME ,null);
			while(cursor.moveToNext()){
				HabitCategory habitCategory = new HabitCategory();
			    int  code_id = cursor.getInt(cursor.getColumnIndex(CODE_ID));
			    int  is_lock = cursor.getInt(cursor.getColumnIndex(IS_LOCK));
			    habitCategory.setHuanxinId(code_id);
			    habitCategory.setIsLockInt(is_lock);
			    habitCategories.add(habitCategory);
			}
		}
        return  habitCategories;		
	}
	
	public void  updataHabitList(int codeid , int islock){
	   SQLiteDatabase  db  = dbHelper.getWritableDatabase();
	   if(db.isOpen()){
		    ContentValues values = new ContentValues();
			values.put(IS_LOCK, islock);
		    db.update(TABLE_NAME, values, CODE_ID+" = ?", new String[]{codeid+""});
	   }
	}

}
