package com.worldchip.bbp.bbpawmanager.cn.db;



import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.HomePageInfo;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.model.NotifyMessage;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.NotifyUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class DataManager {
	
	
	 public static class HomePage {
		 	public static final String MSG_ID = "msg_id";
	    	public static final String TITLE = "title";
	    	public static final String CONTENT = "content";
	    }
	 
	public static class GrowthLog {
		public static final String MSG_ID = "msg_id";
		public static final String ICON_URL = "icon";
		public static final String STUDY_TIME = "study_time";
		public static final String STUDY_CONTENT = "study_content";
		public static final String DATE = "date";
	}
	  
	public static class InformMessage {
		public static final String ID = "_id";
		public static final String MSG_ID = "msg_id";
		public static final String TYPE = "type";
		public static final String MAIN_TYPE = "mainType";
		public static final String MSG_TYPE = "msgType";
		public static final String ICON = "icon";
		public static final String MSG_IMAGE = "msgImage";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String CONTENT = "content";
		public static final String DOWNLOAD_URL = "download_url";
		public static final String ISREAD = "isRead";
		public static final String STATE = "state";
		public static final String ISFAVORITES = "isFavorites";
		public static final String ACTIVITIES_URL = "activities_url";
		public static final String DATE = "date";
	}
	public static class NotifyMsg {
		public static final String MAIN_TYPE = "mainType";
		public static final String TYPE = "type";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String CLICK_OPERATION = "click_operation";
		public static final String OPERATION_VALUE = "operation_value";
		public static final String NOTIFY_TYPE = "notify_type";
		public static final String MUSIC_URI = "music_uri";
		public static final String DATE = "date";
	}
	
	public static void updateNoticeBoardToDB(Context context,  HomePageInfo info){
		if(info == null) 
			return;
		if (info.getTitle() == null)
			return;
		DBHelper dbHelper = null;
		try{
			dbHelper = new DBHelper(context);
			dbHelper.deleteTable(DBHelper.HOME_PAGE_TABLE);
			ContentValues values = new ContentValues();
			values.put(HomePage.MSG_ID, info.getId());
			values.put(HomePage.TITLE, info.getTitle());
			values.put(HomePage.CONTENT, info.getContent());
			dbHelper.insertValues(DBHelper.HOME_PAGE_TABLE, values);
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
		}
	}
	
	
	
	public static HomePageInfo getNoticeBoardDatas(Context context) {
		HomePageInfo info = null;
		DBHelper dbHelper = null;
		Cursor cursor = null;
		try{
			String sqlStr = "select * from "+DBHelper.HOME_PAGE_TABLE;
			dbHelper = new DBHelper(context);
			cursor = dbHelper.getCursorBySql(sqlStr);
			if (cursor == null) {
				return null;
			}
			if (cursor.moveToFirst()) {
				info = new HomePageInfo();
				info.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(HomePage.MSG_ID))));
				info.setTitle(cursor.getString(cursor.getColumnIndex(HomePage.TITLE)));
				info.setContent(cursor.getString(cursor.getColumnIndex(HomePage.CONTENT)));
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception err) {
			err.printStackTrace();
			return null;
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return info;
	}
	
	
	public static void updateGrowthToDB(Context context,
			List<GrowthMessage> list) {
		if (list == null || list.isEmpty())
			return;
		Cursor cursor = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = new DBHelper(context);
			String whereStr = "where msg_id = ";
			for (int i = 0; i < list.size(); i++) {
				GrowthMessage info = list.get(i);
				if (info != null) {
					cursor = dbHelper.getCursor(DBHelper.GROWTH_TABLE, whereStr+info.getId());
					if (cursor != null) {
						cursor.moveToFirst();
					}
					if (cursor == null || cursor.getCount() == 0) {
						ContentValues values = new ContentValues();
						values.put(GrowthLog.MSG_ID,info.getId());
						values.put(GrowthLog.ICON_URL, info.getIconPath());
						values.put(GrowthLog.STUDY_TIME, info.getStudyTime());
						values.put(GrowthLog.STUDY_CONTENT, info.getStudyConetnt());
						values.put(GrowthLog.DATE, info.getDate());
						dbHelper.insertValues(DBHelper.GROWTH_TABLE, values);
					}
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public static List<GrowthMessage> getGrowthDatas(Context context) {
		List<GrowthMessage> list = new ArrayList<GrowthMessage>();
		String sqlStr = "select * from "+DBHelper.GROWTH_TABLE +" order by "+GrowthLog.DATE +" DESC";
		Cursor cursor = null;
		DBHelper dbHelper = null;
		try {
		dbHelper = new DBHelper(context);
		cursor = dbHelper.getCursorBySql(sqlStr);
		if (cursor == null) {
			return null;
		}
		while(cursor.moveToNext()){
			GrowthMessage info = new GrowthMessage();
			info.setIconPath(cursor.getString(cursor.getColumnIndex(GrowthLog.ICON_URL)));
			info.setId(cursor.getString(cursor.getColumnIndex(GrowthLog.MSG_ID)));
			info.setStudyConetnt(cursor.getString(cursor.getColumnIndex(GrowthLog.STUDY_CONTENT)));
			info.setStudyTime(cursor.getString(cursor.getColumnIndex(GrowthLog.STUDY_TIME)));
			info.setDate(cursor.getString(cursor.getColumnIndex(GrowthLog.DATE)));
			list.add(info);
		}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	
	public static boolean removeInformMessageToDB(Context context, Information info) {
		DBHelper dbHelper = null;
		try {
			dbHelper = new DBHelper(context);
			if (info != null) {
				ContentValues values = new ContentValues();
				values.put(InformMessage.MSG_ID,info.getMsgId());
				values.put(InformMessage.MSG_TYPE, info.getMsgType());
				dbHelper.insertValues(DBHelper.INFROM_DELETE_TABLE, values);
				Log.e("xiaolp","info.getId() = "+info.getId()+"    info.getMsgId() = "+info.getMsgId());
				dbHelper.deleteRow(DBHelper.INFROM_MESSAGE_TABLE, info.getId());
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
		}
	}
	
	public static void updateInformMessageToDB(Context context, List<Information> list) {
		if (list == null || list.isEmpty())
			return;
		Cursor cursor = null;
		Cursor deleteCursor = null;
		DBHelper dbHelper = null;
		try {
			dbHelper = new DBHelper(context);
			for (int i = 0; i < list.size(); i++) {
				Information info = list.get(i);
				String whereStr = "where msg_id = "+ info.getMsgId() + " and msgType = "+info.getMsgType();
				if (info != null) {
					cursor = dbHelper.getCursor(DBHelper.INFROM_MESSAGE_TABLE, whereStr);
					if (cursor != null) {
						cursor.moveToFirst();
					}
					deleteCursor = dbHelper.getCursor(DBHelper.INFROM_DELETE_TABLE, whereStr);
					if (deleteCursor != null) {
						deleteCursor.moveToFirst();
					}
					if (cursor == null || cursor.getCount() == 0 
							|| !cursor.getString(cursor.getColumnIndex(InformMessage.DATE)).equals(info.getDate())) {
						if (deleteCursor == null || deleteCursor.getCount() ==0
								|| !(Integer.parseInt(deleteCursor.getString(deleteCursor.getColumnIndex(InformMessage.MSG_ID)))==info.getMsgId())) {
							ContentValues values = new ContentValues();
							values.put(InformMessage.MSG_ID,info.getMsgId());
							values.put(InformMessage.TYPE, info.getType());
							values.put(InformMessage.MAIN_TYPE, info.getMainType());
							values.put(InformMessage.MSG_TYPE, info.getMsgType());
							values.put(InformMessage.ICON, info.getIcon());
							values.put(InformMessage.MSG_IMAGE, info.getMsgImage());
							values.put(InformMessage.TITLE, info.getTitle());
							values.put(InformMessage.DESCRIPTION, info.getDescription());
							values.put(InformMessage.CONTENT, info.getContent());
							values.put(InformMessage.DOWNLOAD_URL, info.getDownloadUrl());
							values.put(InformMessage.ACTIVITIES_URL, info.getActivitiesUrl());
							values.put(InformMessage.ISREAD, info.isRead());
							values.put(InformMessage.STATE, 0);
							values.put(InformMessage.ISFAVORITES, false);
							if (info.getContent().contains("file:///android_asset/")) {
								values.put(InformMessage.DATE, System.currentTimeMillis());
							}else{
								values.put(InformMessage.DATE, info.getDate());
							}
							dbHelper.insertValues(DBHelper.INFROM_MESSAGE_TABLE, values);
							Common.replyStateToServer(info,Utils.REPLY_STATE_RECEIVED);
						}
					}
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public static List<Information> getInformMessageDatas(Context context) {
		// TODO Auto-generated method stub
		List<Information> list = new ArrayList<Information>();
		String sqlStr = "select * from "+DBHelper.INFROM_MESSAGE_TABLE +" order by "+InformMessage.DATE +" DESC";
		Cursor cursor = null;
		DBHelper dbHelper = null;
		try {
		dbHelper = new DBHelper(context);
		cursor = dbHelper.getCursorBySql(sqlStr);
		if (cursor == null) {
			return null;
		}
		while(cursor.moveToNext()){
			Information info = new Information();
			info.setMainType(cursor.getInt(cursor.getColumnIndex(InformMessage.MAIN_TYPE)));
			info.setId(cursor.getInt(cursor.getColumnIndex(InformMessage.ID)));
			info.setMsgId(cursor.getInt(cursor.getColumnIndex(InformMessage.MSG_ID)));
			info.setType(cursor.getInt(cursor.getColumnIndex(InformMessage.TYPE)));
			info.setMsgType(cursor.getInt(cursor.getColumnIndex(InformMessage.MSG_TYPE)));
			info.setIcon(cursor.getString(cursor.getColumnIndex(InformMessage.ICON)));
			info.setMsgImage(cursor.getString(cursor.getColumnIndex(InformMessage.MSG_IMAGE)));
			info.setTitle(cursor.getString(cursor.getColumnIndex(InformMessage.TITLE)));
			info.setDescription(cursor.getString(cursor.getColumnIndex(InformMessage.DESCRIPTION)));
			info.setContent(cursor.getString(cursor.getColumnIndex(InformMessage.CONTENT)));
			info.setDownloadUrl(cursor.getString(cursor.getColumnIndex(InformMessage.DOWNLOAD_URL)));
			info.setActivitiesUrl(cursor.getString(cursor.getColumnIndex(InformMessage.ACTIVITIES_URL)));
			String clickOperation = cursor.getString(cursor.getColumnIndex(NotifyMsg.CLICK_OPERATION));
			if (!TextUtils.isEmpty(clickOperation)) {
				info.setClickOperation(Integer.valueOf(clickOperation));
			}
			info.setOperationValue(cursor.getString(cursor.getColumnIndex(NotifyMsg.OPERATION_VALUE)));
			info.setNotifyType(cursor.getString(cursor.getColumnIndex(NotifyMsg.NOTIFY_TYPE)));
			info.setMusicUri(cursor.getString(cursor.getColumnIndex(NotifyMsg.MUSIC_URI)));
			
			String read = cursor.getString(cursor.getColumnIndex(InformMessage.ISREAD));
			String favorites = cursor.getString(cursor.getColumnIndex(InformMessage.ISFAVORITES));
			boolean isRead = read.equals("1") ? true : false;
			info.setRead(isRead);
			boolean isFavorites= favorites.equals("1") ? true : false;
			info.setFavorites(isFavorites);
			info.setDate(cursor.getString(cursor.getColumnIndex(InformMessage.DATE)));
			list.add(info);
		}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	
	public static boolean updateInformation(Context context,String columeName, String value, String whereColume, String whereValue) {
		DBHelper helper = new DBHelper(context);
		boolean updateColumeValue = helper.updateColumeValue(DBHelper.INFROM_MESSAGE_TABLE, columeName,value, whereColume, whereValue);
		helper.close();
		return updateColumeValue;
	}
	
	public static int getInformUnReadCount(Context context) {
		DBHelper helper = null;
		Cursor cursor = null;
		try {
			helper = new DBHelper(context);
			String whereStr = " where " + InformMessage.ISREAD + " = " + "'0'";
			cursor = helper.getCursor(DBHelper.INFROM_MESSAGE_TABLE, whereStr);
			return cursor.getCount();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (helper != null) {
				helper.close();
				helper = null;
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return 0;
	}
	
	public static void updateNotifyMessageToDB(Context context, List<NotifyMessage> list) {
		if (list == null || list.isEmpty())
			return;
		DBHelper dbHelper = null;
		try {
			dbHelper = new DBHelper(context);
			for (int i = 0; i < list.size(); i++) {
				NotifyMessage info = list.get(i);
				if (info != null) {
						ContentValues values = new ContentValues();
						values.put(NotifyMsg.TYPE, info.getType());
						values.put(NotifyMsg.MAIN_TYPE, info.getMainType());
						values.put(NotifyMsg.TITLE, info.getTitle());
						values.put(NotifyMsg.DESCRIPTION, info.getDescription());
						values.put(NotifyMsg.CLICK_OPERATION, info.getClickOperation());
						values.put(NotifyMsg.OPERATION_VALUE, info.getOperationValue());
						values.put(NotifyMsg.NOTIFY_TYPE, info.getNotifyType());
						values.put(NotifyMsg.MUSIC_URI, info.getMusicUri());
						values.put(InformMessage.ISREAD, info.isRead());
						values.put(InformMessage.STATE, 0);
						values.put(InformMessage.ISFAVORITES, false);
						values.put(NotifyMsg.DATE, info.getDate());
						boolean flag = dbHelper.insertValues(DBHelper.INFROM_MESSAGE_TABLE, values);
						if (flag) {
							NotifyUtils.notify(context, info.getNotifyType(), info.getMusicUri());
						}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (dbHelper != null) {
				dbHelper.close();
				dbHelper = null;
			}
		}
	}
}