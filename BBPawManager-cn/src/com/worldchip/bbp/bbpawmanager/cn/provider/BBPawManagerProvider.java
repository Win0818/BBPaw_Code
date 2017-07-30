package com.worldchip.bbp.bbpawmanager.cn.provider;


import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.db.DBHelper;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager.InformMessage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class BBPawManagerProvider extends ContentProvider {

	public static final String AUTHORITY = "com.worldchip.bbpaw.manager.contentproviders";
	
    public static final int UNREAD = 1;  
    public static final UriMatcher URI_MATCHER;  
    static{  
    	URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);  
    	URI_MATCHER.addURI(AUTHORITY,"unread",UNREAD);  
    }  
    
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		Log.e("lee", " BBPawManagerProvider query uri == "+uri);
		int match = URI_MATCHER.match(uri);
		Log.e("lee", " BBPawManagerProvider query match == "+match);//match错误
		Cursor cursor = null;
		if (match == UNREAD) {
			DBHelper helper = null;
			try {
				helper = new DBHelper(MyApplication.getAppContext());
				String whereStr = " where " + InformMessage.ISREAD + " = " + "'0'";
				Log.e("lee", " BBPawManagerProvider query db -----");
				cursor = helper.getCursor(DBHelper.INFROM_MESSAGE_TABLE, whereStr);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception err) {
				err.printStackTrace();
			} 
		}
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
