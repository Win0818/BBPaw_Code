/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldchip.bbpawphonechat.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.util.HanziToPinyin;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.entity.User;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_HEAD_IMAGE_URL = "headurl";
	public static final String COLUMN_SETTING_NAME  = "remark_name";
	
	public static final String PREF_TABLE_NAME = "pref";
	public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
	public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";
	private static final String TAG = "CHRIS";

	private DbOpenHelper dbHelper;

	public UserDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存好友list
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				if(user.getNick() != null)
					values.put(COLUMN_NAME_NICK, user.getNick());
				if(user.getAvatar() != null)
				    values.put(COLUMN_NAME_AVATAR, user.getAvatar());
				if(user.getHeadurl() != null)
					values.put(COLUMN_HEAD_IMAGE_URL,user.getHeadurl());
				if(user.getRemark_name() != null)
					values.put(COLUMN_SETTING_NAME, user.getRemark_name());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * @return
	 */
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME  /*+ " desc"*/ , null);
			Log.i(TAG, "------UserDao-------getContactList---rawQuery---");
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
				String headurl = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_IMAGE_URL));
				String remark_name = cursor.getString(cursor.getColumnIndex(COLUMN_SETTING_NAME));
				
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				user.setHeadurl(headurl);
				user.setRemark_name(remark_name);
				Log.i(TAG, headurl+"------UserDao-------getContactList------"+username);
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
	
	
	public  User  getOneContact(String username){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		User  user = null;
		if(db.isOpen()){
			//Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where  = "+ username, null);
			Cursor  cursor = db.query(TABLE_NAME, null, COLUMN_NAME_ID+" = ?", new String[]{username} , null, null, null);
			if (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				String headurl = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_IMAGE_URL));
				String remark_name = cursor.getString(cursor.getColumnIndex(COLUMN_SETTING_NAME));
				
				user = new User();
				user.setNick(nick);
				user.setUsername(name);
				user.setHeadurl(headurl);
				user.setRemark_name(remark_name);
				Log.e(TAG, name + "-----UserDao------getOneContact----"+nick);
			}
			return  user;
		}
		return  null;
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{username});
		}
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getUsername());
		if(user.getNick() != null)
			values.put(COLUMN_NAME_NICK, user.getNick());
		if(user.getAvatar() != null)
		    values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		if(user.getHeadurl() != null)
			values.put(COLUMN_HEAD_IMAGE_URL,user.getHeadurl());
		if(user.getRemark_name() != null)
			values.put(COLUMN_SETTING_NAME, user.getRemark_name());
		if(db.isOpen()){
			db.replace(TABLE_NAME, null, values);
		}
	}
	
	public void setDisabledGroups(List<String> groups){
        setList(COLUMN_NAME_DISABLED_GROUPS, groups);
    }
    
    public List<String>  getDisabledGroups(){       
        return getList(COLUMN_NAME_DISABLED_GROUPS);
    }
    
    public void setDisabledIds(List<String> ids){
        setList(COLUMN_NAME_DISABLED_IDS, ids);
    }
    
    public List<String> getDisabledIds(){
        return getList(COLUMN_NAME_DISABLED_IDS);
    }
    
    private void setList(String column, List<String> strList){
        StringBuilder strBuilder = new StringBuilder();
        
        for(String hxid:strList){
            strBuilder.append(hxid).append("$");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());
            db.update(PREF_TABLE_NAME, values, null,null);
        }
    }
    
    private List<String> getList(String column){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + PREF_TABLE_NAME,null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }
        cursor.close();
        String[] array = strVal.split("$");
        if(array != null && array.length > 0){
            List<String> list = new ArrayList<String>();
            for(String str:array){
                list.add(str);
            }
            return list;
        }
        return null;
    }
    
    
    public   synchronized void  updataNickName(User user, String nickname){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	if(db.isOpen()){
    		ContentValues  values  = new ContentValues();
    		values.put(COLUMN_NAME_NICK, nickname);
    		db.update(TABLE_NAME, values, COLUMN_NAME_ID +" = ?", new String[]{user.getUsername()+""});
    	}
    }
    
    //修改在本地数据库中的备注名
    public  void  updataRemarkName(User user, String remark_name){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	if(db.isOpen()){
    		ContentValues  values  = new ContentValues();
    		values.put(COLUMN_SETTING_NAME, remark_name);
    		db.update(TABLE_NAME, values, COLUMN_NAME_ID +" = ?", new String[]{user.getUsername()+""});
    	}
    }
    
    
    public synchronized void updataContactHeadUrl(User user ,String headurl){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	if(db.isOpen()){
    		ContentValues  values  = new ContentValues();
    		values.put(COLUMN_HEAD_IMAGE_URL, headurl);
    		db.update(TABLE_NAME, values, COLUMN_NAME_ID +" = ?", new String[]{user.getUsername()+""});
    	}
    }
    
    
}
