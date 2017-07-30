package com.worldchip.bbp.ect.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.ect.db.DBGoldHelper;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.receiver.DetectionAlarmReceiver;
import com.worldchip.bbp.ect.util.CousorSqliteDataUtil;
import com.worldchip.bbp.ect.util.HttpResponseCallBack;
import com.worldchip.bbp.ect.util.HttpUtils;
import com.worldchip.bbp.ect.util.JsonUtils;
import com.worldchip.bbp.ect.util.ZipExtractorTask;

public class ScanService extends Service {

	private static final String URL = "http://bbpaw.com.cn/weixin/Data/bbpaw/BBPaw.zip";
	private static final String MENU_APK_URL = "http://bbpaw.com.cn/weixin/Data/bbpaw/bbpaw.json";
	private List<Integer> menuDataList;
	/*private int mTotalGameCount = 100, mArts_Music = 19, mArts_Painting = 24, mEliteEducation = 66,
			mInteractiveGames = 11, mLearningZone_Chinese = 31, mLearningZone_English = 22,
			mLearningZone_GuoXue = 38, mLearningZone_LifeSkills = 14,
			mLearningZone_Mathematics = 23, mLearningZone_Stories = 15, mScience = 34,
			mToolBox  = 12;*/
	private int mTotalGameCount , mArts_Music , mArts_Painting , mEliteEducation ,
			mInteractiveGames , mLearningZone_Chinese , mLearningZone_English,
			mLearningZone_GuoXue, mLearningZone_LifeSkills,
			mLearningZone_Mathematics, mLearningZone_Stories, mScience,
			mToolBox;
	private static final String MENU_RESOURCE_DATA = "/mnt/sdcard/BBPaw/";
	
	public static final int SUCCESS = 1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == SUCCESS) {
				String result = (String) msg.obj;
				if (result != null) {
					menuDataList = JsonUtils.parseApkMenuData(result);
					mTotalGameCount = menuDataList.get(0);
					mArts_Music = menuDataList.get(1);
					mArts_Painting = menuDataList.get(2);
					mEliteEducation = menuDataList.get(3);
					mInteractiveGames = menuDataList.get(4);
					mLearningZone_Chinese = menuDataList.get(5);
					mLearningZone_English = menuDataList.get(6);
					mLearningZone_GuoXue = menuDataList.get(7);
					mLearningZone_LifeSkills = menuDataList.get(8);
					mLearningZone_Mathematics = menuDataList.get(9);
					mLearningZone_Stories = menuDataList.get(10);
					mScience = menuDataList.get(11);
					mToolBox = menuDataList.get(12);

					Log.d("Wing", "menuDataList: " + menuDataList
							+ "------" + mTotalGameCount + "------mArts_Music" + mArts_Music);
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							boolean is_equal_Arts_Music = compareToFileNumber(mArts_Music,
									MENU_RESOURCE_DATA + "BBPaw/_icon/Arts_Music");
							boolean is_equal_Arts_Painting = compareToFileNumber(
									mArts_Painting, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/Arts_Painting");
							boolean is_equal_EliteEducation = compareToFileNumber(
									mEliteEducation, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/EliteEducation");
							boolean is_equal_InteractiveGames = compareToFileNumber(
									mInteractiveGames, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/InteractiveGames");
							boolean is_equal_LearningZone_Chinese = compareToFileNumber(
									mLearningZone_Chinese, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_Chinese");
							boolean is_equal_LearningZone_English = compareToFileNumber(
									mLearningZone_English, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_English");
							boolean is_equal_LearningZone_GuoXue = compareToFileNumber(
									mLearningZone_GuoXue, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_GuoXue");
							boolean is_equal_LearningZone_LifeSkills = compareToFileNumber(
									mLearningZone_LifeSkills, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_LifeSkills");
							boolean is_equal_LearningZone_Mathematics = compareToFileNumber(
									mLearningZone_Mathematics, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_Mathematics");
							boolean is_equal_LearningZone_Stories = compareToFileNumber(
									mLearningZone_Stories, MENU_RESOURCE_DATA
											+ "BBPaw/_icon/LearningZone_Stories");
							boolean is_equal_Science = compareToFileNumber(mScience,
									MENU_RESOURCE_DATA + "BBPaw/_icon/Science");
							boolean is_equal_ToolBox = compareToFileNumber(mToolBox,
									MENU_RESOURCE_DATA + "BBPaw/_icon/ToolBox");
							Log.d("Wing", mArts_Music + "---"+ mArts_Painting + "---"+ mEliteEducation + "---"+ mInteractiveGames + 
									"---"+ mLearningZone_Chinese + "---"+ mLearningZone_English + "---"+ mLearningZone_GuoXue + 
									"---"+ mLearningZone_LifeSkills+ "---"+ mLearningZone_Mathematics + 
									"---"+ mLearningZone_Stories + "---"+ mScience + "---" + mToolBox);
							
							Log.d("Wing", is_equal_Arts_Music + "---"+ is_equal_Arts_Painting + "---"+ is_equal_EliteEducation + "---"+ is_equal_InteractiveGames + 
									"---"+ is_equal_LearningZone_Chinese + "---"+ is_equal_LearningZone_English + "---"+ is_equal_LearningZone_GuoXue + 
									"---"+ is_equal_LearningZone_LifeSkills+ "---"+ is_equal_LearningZone_Mathematics + 
									"---"+ is_equal_LearningZone_Stories + "---"+ is_equal_Science + "---" + is_equal_ToolBox);
							if (!(is_equal_Arts_Music && is_equal_Arts_Painting
									&& is_equal_EliteEducation && is_equal_InteractiveGames
									&& is_equal_LearningZone_Chinese
									&& is_equal_LearningZone_English
									&& is_equal_LearningZone_GuoXue
									&& is_equal_LearningZone_LifeSkills
									&& is_equal_LearningZone_Mathematics
									&& is_equal_LearningZone_Stories && is_equal_Science && is_equal_ToolBox)) {
								
								download(URL);

							}
						}
					}).start();
				} 
				
			}
			
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		HttpUtils.doPost(MENU_APK_URL, new HttpResponseCallBack() {
			@Override
			public void onSuccess(String result, String httpTag) {
				Log.d("Wing", "result: " + result);
				Message msg = new Message();
				msg.what = SUCCESS;
				msg.obj = result;
				mHandler.sendMessage(msg);			

			}

			@Override
			public void onStart(String httpTag) {

			}

			@Override
			public void onFinish(int result, String httpTag) {

			}

			@Override
			public void onFailure(Exception e, String httpTag) {

			}

		}, null);
		
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int minuteTimer = 2 * 60 * 60 * 1000; // 两小时检测一次 2 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + minuteTimer;
		Intent i = new Intent(this, DetectionAlarmReceiver.class);
		i.setAction("com.worldchip.bbp.ect.service.ScanService");
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return START_STICKY;
	}

	private void download(final String path) {
		new Thread() {
			@Override
			public void run() {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					URL url;
					try {
						url = new URL(path);
						Log.d("Wing", "=------downLoadApk---URL---" + path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setConnectTimeout(5000);
						// 获取到文件的大小
						InputStream is = conn.getInputStream();

						File updatefile = new File(
								Environment.getExternalStorageDirectory()
										+ "/BBPaw" + "/BBPaw.zip");
						if (updatefile.exists()) {											
							updatefile.delete();
							updatefile.createNewFile();
						} else {
							updatefile.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(updatefile);
						BufferedInputStream bis = new BufferedInputStream(is);
						byte[] buffer = new byte[1024];
						int len;
						while ((len = bis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						fos.close();
						bis.close();
						is.close();
						doZipExtractorWork();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void doZipExtractorWork() {
		Log.d("Wing", "");
		File file = new File("/mnt/sdcard/BBPaw/BBPaw");
		if (file.exists()) {
			deleteFolderFile("/mnt/sdcard/BBPaw/BBPaw", true);
		}
		ZipExtractorTask task = new ZipExtractorTask(
				"/mnt/sdcard/BBPaw/BBPaw.zip", "/mnt/sdcard/BBPaw/", this, true);
		task.execute();
	}

	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean compareToFileNumber(int apkData, String filePath) {

		try {
			int fileNumber = getFiles(filePath);
			Log.d("Wing", "fileNumber:  " + fileNumber);
			if (apkData == fileNumber)
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private int getFiles(String string) {
		// TODO Auto-generated method stub
		int i = 0;
		File file;
		try {
			file = new File(string);
			File[] files = file.listFiles();
			for (int j = 0; j < files.length; j++) {
				String name = files[j].getName();
				if (files[j].isDirectory()) {
					String dirPath = files[j].toString().toLowerCase();
					System.out.println(dirPath);
					getFiles(dirPath + "/");
				} else if (files[j].isFile() &  name.endsWith(".png")) {
					//System.out.println("FileName===" + files[j].getName());
					i++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		Log.d("Wing", "i =   " + i);
		return i;
	}

}
