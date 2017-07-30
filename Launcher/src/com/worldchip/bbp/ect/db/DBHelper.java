package com.worldchip.bbp.ect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper
{
    private static final String DATABASE_NAME = "ShareDB.db";
    private static final int DATABASE_VERSION = 1;
    
    @SuppressWarnings("unused")
	private final Context mContext;
    private DatabaseHelper mDatabaseHelper;
    
    private SQLiteDatabase db;
    
    public DBHelper(Context context)
    {
         this.mContext = context;
         mDatabaseHelper = new DatabaseHelper(context);
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
	   DatabaseHelper(Context context)
	   {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }
    
       @Override
       public void onCreate(SQLiteDatabase db)
       {
    	   db.execSQL(DBSqlBuilder.BuilderBbpPictureTable());
    	   db.execSQL(DBSqlBuilder.BuilderBbpBrowserTable());
    	   db.execSQL(DBSqlBuilder.BuilderBbpMusicTable());
           db.execSQL(DBSqlBuilder.BuilderBbpVideoTable());
           db.execSQL(DBSqlBuilder.BuilderBbpAppTable());
           db.execSQL(DBSqlBuilder.BuilderBbpClockTable());
           
           /*db.beginTransaction(); 
    	   
    	   //初始化默认数据
           initClockAlramData(db);
    	   
           db.setTransactionSuccessful();  
      	   db.endTransaction();*/
       }
       
       /**
        * 初始化闹钟默认数据
        * @param db
        */
		/*private void initClockAlramData(SQLiteDatabase db) 
		{
			ContentValues values_0 = new ContentValues();
			values_0.put("hours", 0);
			values_0.put("musutes", 0);
			values_0.put("daysofweek", 0);
			values_0.put("alarmtime", 0);
			values_0.put("enabled", 0);
			values_0.put("alert", "");
			values_0.put("times", 10);
			values_0.put("isdefault", 0);
			values_0.put("interval", 0);
			db.insert(DBSqlBuilder.BBP_CLOCk_TABLE, null, values_0);

			ContentValues values_1 = new ContentValues();
			values_1.put("hours", 0);
			values_1.put("musutes", 0);
			values_1.put("daysofweek", 0);
			values_1.put("alarmtime", 0);
			values_1.put("enabled", 0);
			values_1.put("alert", "");
			values_1.put("times", 10);
			values_1.put("isdefault", 0);
			values_1.put("interval", 0);
			db.insert(DBSqlBuilder.BBP_CLOCk_TABLE, null, values_1);
			
			ContentValues values_2 = new ContentValues();
			values_2.put("hours", 0);
			values_2.put("musutes", 0);
			values_2.put("daysofweek", 0);
			values_2.put("alarmtime", 0);
			values_2.put("enabled", 0);
			values_2.put("alert", "");
			values_2.put("times", 10);
			values_2.put("isdefault", 0);
			values_2.put("interval", 0);
			db.insert(DBSqlBuilder.BBP_CLOCk_TABLE, null, values_2);
			
			ContentValues values_3 = new ContentValues();
			values_3.put("hours", 0);
			values_3.put("musutes", 0);
			values_3.put("daysofweek", 0);
			values_3.put("alarmtime", 0);
			values_3.put("enabled", 0);
			values_3.put("alert", "");
			values_3.put("times", 10);
			values_3.put("isdefault", 0);
			values_3.put("interval", 0);
			db.insert(DBSqlBuilder.BBP_CLOCk_TABLE, null, values_3);
			
			ContentValues values_4 = new ContentValues();
			values_4.put("hours", 0);
			values_4.put("musutes", 0);
			values_4.put("daysofweek", 0);
			values_4.put("alarmtime", 0);
			values_4.put("enabled", 0);
			values_4.put("alert", "");
			values_4.put("times", 10);
			values_4.put("isdefault", 0);
			values_4.put("interval", 0);
			db.insert(DBSqlBuilder.BBP_CLOCk_TABLE, null, values_4);
		}*/
       
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
       {
    	   db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_PICTURE_TABLE);
    	   db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_MUSIC_TABLE);
           db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_VIDEO_TABLE);
           db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_APP_TABLE);
           db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_CLOCk_TABLE);
           
           onCreate(db);
       }
    }

    public DBHelper open() throws SQLException
    {
    	close();
        db = mDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
    	mDatabaseHelper.close();
    }

    public boolean deleteRow(String tableName, long rowId)
    {
    	 open();
         return db.delete(tableName, "_id =" + rowId, null) > 0;
    }
    
    public boolean deleteRow(String tableName, String whereSql)
    {
   	    open();
        return db.delete(tableName, whereSql, null) > 0;
   }
    
    public void deleteTable(String tableName)
    {
    	open();
    	db.execSQL("DROP TABLE IF EXISTS "+tableName);
    	if(tableName.equals(DBSqlBuilder.BBP_PICTURE_TABLE))
    	{
    	   db.execSQL(DBSqlBuilder.BuilderBbpPictureTable());
    	}else if(tableName.equals(DBSqlBuilder.BBP_MUSIC_TABLE))
    	{
     	   db.execSQL(DBSqlBuilder.BuilderBbpMusicTable());
     	}else if(tableName.equals(DBSqlBuilder.BBP_BROWSER_TABLE))
    	{
      	   db.execSQL(DBSqlBuilder.BuilderBbpBrowserTable());
      	}else if(tableName.equals(DBSqlBuilder.BBP_VIDEO_TABLE))
    	{
     	   db.execSQL(DBSqlBuilder.BuilderBbpVideoTable());
     	}else if(tableName.equals(DBSqlBuilder.BBP_APP_TABLE))
    	{
      	   db.execSQL(DBSqlBuilder.BuilderBbpAppTable());
      	}else if(tableName.equals(DBSqlBuilder.BBP_CLOCk_TABLE))
    	{
       	   db.execSQL(DBSqlBuilder.BuilderBbpClockTable());
       	}
    }

    public Cursor getCursor(String tableName, String whereStr)
    {
    	open();
    	return db.rawQuery("select * from "+tableName+" "+ whereStr, null);
    	
    }

    public Cursor getCursorBySql(String sqlStr)
    {
    	open();
    	return db.rawQuery(sqlStr, null);
    	
    }
    
    public Cursor getCursorGroupBy(String tableName, String columnName, String groupBy) throws SQLException
    {
    	open();
    	return db.rawQuery("select "+columnName+" from "+tableName+" " + groupBy, null);
    }  
    
    public Cursor getCursor(String tableName, String columeName, String value) throws SQLException
    {
    	open();
    	return db.rawQuery("select * from "+tableName+" where "+columeName+" ='"+value+"'", null);
    }     
      
    public Cursor getTopN(String tableName,String columeName, int topN) throws SQLException
    {
    	open();
    	return db.rawQuery("select * from "+tableName+" order by "+columeName+" desc limit "+ topN, null);
    }

    public boolean insertValues(String tableName, ContentValues values)
    {
    	open();
        return db.insert(tableName, null, values) > 0;
    }
    
    public boolean updateValues(String tableName, ContentValues values, String whereStr)
    {
    	open();
        return db.update(tableName, values, whereStr, null) >0;
    }
    
    public void InsertValuesWithTrans(String tableName, ContentValues values)
    {
    	open();
    	db.beginTransaction();
        db.insert(tableName, null, values);
        db.endTransaction();
    }
    
    public boolean updateColumeValue(String tableName, String columeName, String value, String whereColume, String whereValue)
    {
    	ContentValues args = new ContentValues();
        args.put(columeName, value);
        open();
        return db.update(tableName, args,whereColume + "='" + whereValue +"'", null) > 0;
    }
    
}