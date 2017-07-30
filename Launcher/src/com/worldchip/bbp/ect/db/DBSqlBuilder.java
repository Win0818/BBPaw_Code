package com.worldchip.bbp.ect.db;

public class DBSqlBuilder {

	public static final String BBP_PICTURE_TABLE = "bbp_picture_table";
	public static final String BBP_MUSIC_TABLE = "bbp_music_table";
	public static final String BBP_VIDEO_TABLE = "bbp_video_table";
	public static final String BBP_APP_TABLE = "bbp_app_table";
	public static final String BBP_CLOCk_TABLE = "bbp_clock_table";
	public static final String BBP_BROWSER_TABLE = "bbp_browser_table";
	public static final String BBP_USER_TABLE = "user_table";
	
	public static final String BuilderBbpUserTable(){
		return  "create table "
			+ BBP_USER_TABLE + " (_id integer primary key autoincrement, "
			+ "user_key text, " + "user_value integer);";
	}
	
	/**
	 * 创建图片表
	 * 
	 * @return
	 */
	public static String BuilderBbpPictureTable() {
		return "create table " + BBP_PICTURE_TABLE
				+ " (_id integer primary key autoincrement, "
				+ "displayName text," + "data text);";
	}

	/**
	 * 创建浏览器表
	 * 
	 * @return
	 */
	public static String BuilderBbpBrowserTable() {
		return "create table " + BBP_BROWSER_TABLE
				+ " (_id integer primary key autoincrement, " + "title text,"
				+ "url text);";
	}

	/**
	 * 创建音乐表
	 * 
	 * @return
	 */
	public static String BuilderBbpMusicTable() {
		return "create table " + BBP_MUSIC_TABLE
				+ " (_id integer primary key autoincrement, " + "title text,"
				+ "data text, " + "duration text);";
	}

	/**
	 * 创建视频表
	 * 
	 * @return
	 */
	public static String BuilderBbpVideoTable() {
		return "create table " + BBP_VIDEO_TABLE
				+ " (_id integer primary key autoincrement, " + "title text, "
				+ "displayName text, " + "data text, " + "duration text);";
	}

	/**
	 * 创建分享的APP表
	 */
	public static String BuilderBbpAppTable() {
		return "create table " + BBP_APP_TABLE
				+ " (_id integer primary key autoincrement, "
				+ "packageName text, " + "icon text);";
	}

	/**
	 * 创建闹钟表
	 */
	public static String BuilderBbpClockTable() {
		return "create table " + BBP_CLOCk_TABLE
				+ " (_id integer primary key autoincrement , "
				+ "hours integer, " + "musutes integer, " + "daysofweek text, "
				+ "alarmtime integer, " + "enabled integer, " + "alert text, "
				+ "times integer, " + "isdefault integer, "
				+ "interval integer);";
	}
}