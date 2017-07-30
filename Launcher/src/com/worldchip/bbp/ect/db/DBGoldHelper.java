package com.worldchip.bbp.ect.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBGoldHelper {
	private static final String DATABASE_DIR = File.separator + ".Data";
	private static final String DATABASE_NAME = File.separator
			+ "bbpaw_data.db";

	private static final int DATABASE_VERSION = 1;

	public static final String DATA_TABLE = "data_table";
	public static final String GOLD_TABLE = "gold_table";
	public static final String EFFECT_TABLE = "effect_table";
	public static final String MyPet_TABLE = "mypet_table";
	public static final String RECORD_TABLE = "game_record_table";
	public static final String ACTION_TABLE = "game_action_table";

	public static final String BuilderDataTableSQL = "create table "
			+ DATA_TABLE + " (_id integer primary key autoincrement, "
			+ "apk_name text, " + "key text, " + "value text);";
	public static final String BuilderGoldSQL = "create table " + GOLD_TABLE
			+ " (_id integer primary key autoincrement, " + "apk_name text, "
			+ "old_gold integer, " + "gold integer);";
//+ "old_gold integer, " 
	public static final String BuilderEffectSQL = "create table "
			+ EFFECT_TABLE + " (_id integer primary key autoincrement, "
			+ "background_music integer, " + "effect_music integer, "  
			+ "is_effect_music integer);";

	public static final String BuilderMyPetSQL = "create table " + MyPet_TABLE
			+ "(_id integer primary key autoincrement, " + "blood integer ,"
			+ "exp integer , " + "level integer ," + "teeth integer ,"
			+ "isGameCount integer);";

	public static final String InsertMyPetSql = "insert into " + MyPet_TABLE
			+ " (blood ,exp, level ,teeth,isGameCount) values (100,0,1,5,0)";
	
	// game record
	public static final String BuilderRecordSQL = "create table "
			+ RECORD_TABLE + " (_id integer primary key autoincrement, "
			+ "user_device_id text ," + "apk_package text , "
			+ "apk_type integer ," + "apk_type_2 integer ,"
			+ "game_score text ," + "game_time integer);";
	// game action
	public static final String BuilderActionSQL = "create table "
				+ ACTION_TABLE + " (_id integer primary key autoincrement, "
				+ "user_device_id text ," + "apk_package text , "
				+ "day_time integer ," + "enter_time integer ,"
				+ "exit_time integer);";
		
	//
	//public static final String BuilderStatisticsSQL = "creat table " + Statistics_TABLE
			
	
	@SuppressWarnings("unused")
	private final Context mContext;
	private DatabaseHelper mDatabaseHelper;

	private SQLiteDatabase db;

	public DBGoldHelper(Context context) {
		this.mContext = context;
		mDatabaseHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String TAG = "DatabaseHelper";

		DatabaseHelper(Context context) {
			super(context, DataManager.getDataDir() + DATABASE_DIR
					+ DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.e(TAG, "onCreate    db-----------");
			db.execSQL(BuilderDataTableSQL);
			db.execSQL(BuilderGoldSQL);
			db.execSQL(BuilderEffectSQL);
			db.execSQL(BuilderRecordSQL);
			db.execSQL(BuilderActionSQL);
			db.execSQL(DBSqlBuilder.BuilderBbpUserTable());
			
			 //about pet
			 db.execSQL(BuilderMyPetSQL);
			 for(int i = 0;i < 3;i++)
			 {
				 db.execSQL(InsertMyPetSql);
			 }
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + GOLD_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + EFFECT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RECORD_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MyPet_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + ACTION_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "+DBSqlBuilder.BBP_USER_TABLE);
			onCreate(db);

		}
	}

	public DBGoldHelper open() throws SQLException {
		Log.e("*********", "open mDatabaseHelper=" + mDatabaseHelper);
		close();
		db = mDatabaseHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public boolean deleteRow(String tableName, long rowId) {
		open();
		return db.delete(tableName, "_id =" + rowId, null) > 0;
	}

	public boolean deleteRow(String tableName, String whereSql) {
		open();
		return db.delete(tableName, whereSql, null) > 0;
	}

	public void deleteTable(String tableName) {
		open();
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
		if (tableName.equals(DATA_TABLE)) {
			db.execSQL(BuilderDataTableSQL);
		} else if (tableName.equals(GOLD_TABLE)) {
			db.execSQL(BuilderGoldSQL);
		} else if (tableName.equals(EFFECT_TABLE)) {
			db.execSQL(BuilderEffectSQL);
		} else if (tableName.equals(MyPet_TABLE)) {
			db.execSQL(BuilderMyPetSQL);
		}else if (tableName.equals(RECORD_TABLE)) {
			db.execSQL(BuilderRecordSQL);
		}else if (tableName.equals(ACTION_TABLE)) {
				db.execSQL(BuilderActionSQL);
			}
	}

	public Cursor getCursor(String tableName, String whereStr) {
		open();
		return db.rawQuery("select * from " + tableName + " " + whereStr, null);
	}

	public Cursor getCursorBySql(String sqlStr) {
		open();
		return db.rawQuery(sqlStr, null);

	}

	public Cursor getCursorGroupBy(String tableName, String columnName,
			String groupBy) throws SQLException {
		open();
		return db.rawQuery("select " + columnName + " from " + tableName + " "
				+ groupBy, null);
	}

	public Cursor getCursor(String tableName, String columeName, String value)
			throws SQLException {
		open();
		return db.rawQuery("select * from " + tableName + " where "
				+ columeName + " ='" + value + "'", null);
	}

	public Cursor getTopN(String tableName, String columeName, int topN)
			throws SQLException {
		open();
		return db.rawQuery("select * from " + tableName + " order by "
				+ columeName + " desc limit " + topN, null);
	}

	public boolean insertValues(String tableName, ContentValues values) {
		open();
		return db.insert(tableName, null, values) > 0;
	}

	public boolean updateValues(String tableName, ContentValues values,
			String whereStr) {
		open();
		return db.update(tableName, values, whereStr, null) > 0;
	}

	public void InsertValuesWithTrans(String tableName, ContentValues values) {
		open();
		db.beginTransaction();
		db.insert(tableName, null, values);
		db.endTransaction();
	}

	public boolean updateColumeValue(String tableName, String columeName,
			String value, String whereColume, String whereValue) {
		ContentValues args = new ContentValues();
		args.put(columeName, value);
		open();
		return db.update(tableName, args,
				whereColume + "='" + whereValue + "'", null) > 0;
	}
}