package com.worldchip.bbp.bbpawmanager.cn.db;

import java.io.File;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper
{
    private static final String DATABASE_NAME = "BBpawManager.db";
    
    private static final int DATABASE_VERSION = 1;
    public static final String VACCINE_TABLE = "vaccine";
    public static final String HOME_PAGE_TABLE = "homepage";
    public static final String GROWTH_TABLE = "growth";
    public static final String INFROM_MESSAGE_TABLE = "inform";
    public static final String INFROM_DELETE_TABLE = "deleteinform";
    
    public static final String VACCINE_CREATE_SQL = 
    		    "create table "+VACCINE_TABLE+" (_id integer primary key autoincrement, "
    		    + "age text, "
    		    + "ageTitle text,"
    		    + "vaccineTypeName text,"
	    	    + "vaccineType text,"
	    	    + "vaccineExplain text,"
	    	    + "date text);";
    
    public static final String HOME_PAGE_CREATE_SQL = 
		    "create table "+HOME_PAGE_TABLE+" (_id integer primary key autoincrement, "
		    + "msg_id text,"
		    + "title text, "
    	    + "content text);";
    
    public static final String GROWTH_CREATE_SQL = 
		    "create table "+GROWTH_TABLE+" (_id integer primary key autoincrement, "
		    + "msg_id text, "
		    + "icon text, "
		    + "study_time text,"
    	    + "study_content text,"
    	    + "date text);";
    
	
    public static final String INFORM_CREATE_SQL = 
		    "create table "+INFROM_MESSAGE_TABLE+" (_id integer primary key autoincrement, "
		    + "type integer, "
		    + "mainType integer,"
		    + "msgType integer,"
		    + "icon text,"
		    + "msgImage text,"
		    + "download_url text,"
    	    + "title text,"
    	    + "description text,"
    	    + "content text,"
    	    + "msg_id integer, "
    	    + "isRead text, "
    	    + "state text, "
    	    + "isFavorites text, "
    	    + "activities_url text, "
    	    + "click_operation text, "
    	    + "operation_value text, "
    	    + "notify_type text, "
    	    + "music_uri text, "
    	    + "date text);";
    
    public static final String INFORM_DELETE_SQL = 
		    "create table "+INFROM_DELETE_TABLE+" (_id integer primary key autoincrement, "
		    		+ "type integer, "
				    + "mainType integer,"
				    + "msgType integer,"
				    + "icon text,"
				    + "msgImage text,"
				    + "download_url text,"
		    	    + "title text,"
		    	    + "description text,"
		    	    + "content text,"
		    	    + "msg_id integer, "
		    	    + "isRead text, "
		    	    + "state text, "
		    	    + "isFavorites text, "
		    	    + "activities_url text, "
		    	    + "click_operation text, "
		    	    + "operation_value text, "
		    	    + "notify_type text, "
		    	    + "music_uri text, "
		    	    + "date text);";
    
    
    public static class Vaccine {
    	public static final String AGE = "age";
    	public static final String AGE_TITLE = "ageTitle";
    	public static final String VACCINE_TYPE_NAME = "vaccineTypeName";
    	public static final String VACCINE_TYPE = "vaccineType";
    	public static final String VACCINE_EXPLAIN = "vaccineExplain";
    	public static final String DATE = "date";
    }
    
    
    @SuppressWarnings("unused")
	private final Context mContext;
    private DatabaseHelper mDatabaseHelper;
    
    private SQLiteDatabase db;
    
    
    public DBHelper(Context context){
         this.mContext = context;
         mDatabaseHelper = new DatabaseHelper(context);
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper{
       private static final String TAG = "DatabaseHelper";

	   DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }
    
       @Override
       public void onCreate(SQLiteDatabase db){
    	   Log.e(TAG, "onCreate    db-----------");
    	   db.execSQL(VACCINE_CREATE_SQL);
    	   db.execSQL(HOME_PAGE_CREATE_SQL);
    	   db.execSQL(GROWTH_CREATE_SQL);
    	   db.execSQL(INFORM_CREATE_SQL);
    	   db.execSQL(INFORM_DELETE_SQL);
       } 
       
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
           Log.w(TAG, "Upgrading database from version " + oldVersion
           + " to "
           + newVersion + ", which will destroy all old data");
           db.execSQL("DROP TABLE IF EXISTS "+VACCINE_TABLE);     
           db.execSQL("DROP TABLE IF EXISTS "+HOME_PAGE_TABLE);     
           db.execSQL("DROP TABLE IF EXISTS "+GROWTH_TABLE);     
           db.execSQL("DROP TABLE IF EXISTS "+INFROM_MESSAGE_TABLE);   
           db.execSQL("DROP TABLE IF EXISTS "+INFROM_DELETE_TABLE);
		   onCreate(db);
       }
    }

    public void deleteTable(String tableName)
    {
    	open();
    	db.execSQL("DROP TABLE IF EXISTS "+tableName);
    	if(tableName.equals(VACCINE_TABLE)){
    	   db.execSQL(VACCINE_CREATE_SQL);
    	} else if (tableName.equals(HOME_PAGE_TABLE)) {
    		db.execSQL(HOME_PAGE_CREATE_SQL);
    	}else if (tableName.equals(GROWTH_TABLE)) {
    		db.execSQL(GROWTH_CREATE_SQL);
    	}else if (tableName.equals(INFORM_CREATE_SQL)) {
    		db.execSQL(INFORM_CREATE_SQL);
    	}else if (tableName.equals(INFORM_DELETE_SQL)) {
    		db.execSQL(INFORM_DELETE_SQL);
    	}
    }
    
    public DBHelper open() throws SQLException{
    	close();
        db = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
    	mDatabaseHelper.close();
    }

    public boolean deleteRow(String tableName, long rowId){
    	 open();
         return db.delete(tableName, "_id =" + rowId, null) > 0;
    }
    
    public boolean deleteRow(String tableName, String whereSql){
   	    open();
        return db.delete(tableName, whereSql, null) > 0;
   }

    public Cursor getCursor(String tableName, String whereStr){
    	open();
    	return db.rawQuery("select * from "+tableName+" "+ whereStr, null);
    	
    }

    public Cursor getCursorBySql(String sqlStr){
    	open();
    	return db.rawQuery(sqlStr, null);
    	
    }
    
    public Cursor getCursorGroupBy(String tableName, String columnName, String groupBy) throws SQLException{
    	open();
    	return db.rawQuery("select "+columnName+" from "+tableName+" " + groupBy, null);
    }  
    
    public Cursor getCursor(String tableName, String columeName, String value) throws SQLException{
    	open();
    	return db.rawQuery("select * from "+tableName+" where "+columeName+" ='"+value+"'", null);
    }     
      
    public Cursor getTopN(String tableName,String columeName, int topN) throws SQLException{
    	open();
    	return db.rawQuery("select * from "+tableName+" order by "+columeName+" desc limit "+ topN, null);
    }

    public boolean insertValues(String tableName, ContentValues values){
    	open();
        return db.insert(tableName, null, values) > 0;
    }
    
    public boolean updateValues(String tableName, ContentValues values, String whereStr){
    	open();
        return db.update(tableName, values, whereStr, null) >0;
    }
    
    public void insertValuesWithTrans(String tableName, ContentValues values){
    	open();
    	db.beginTransaction();
        db.insert(tableName, null, values);
        db.endTransaction();
    }
    
    public boolean updateColumeValue(String tableName, String columeName, String value, String whereColume, String whereValue){
    	ContentValues args = new ContentValues();
        args.put(columeName, value);
        open();
        return db.update(tableName, args,
        		whereColume + "='" + whereValue +"'", null) > 0;
    }
}