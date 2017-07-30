package com.worldchip.bbp.ect.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.worldchip.bbp.ect.activity.ChargingActivity;
import com.worldchip.bbp.ect.common.TimeTontrol;
import com.worldchip.bbp.ect.db.DBGoldHelper;
import com.worldchip.bbp.ect.db.DataManager;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.installapk.InstallApkActivity;
import com.worldchip.bbp.ect.installapk.Utils;
import com.worldchip.bbp.ect.receiver.DetectionAlarmReceiver;
import com.worldchip.bbp.ect.util.CousorSqliteDataUtil;
import com.worldchip.bbp.ect.util.HttpUtils;

/**
 * ʱ����Ƽ��
 * 
 * @author Administrator
 * 
 */
public class TimeTontrolService extends Service {

	private static final String SET_TIME_COUNTDOWN_ALARM_ACTION = "set_time_countdown_alarm_action";
	private static final String SET_TIME_QUANTUM_ALARM_ACTION = "set_time_quantum_alarm_action";
	private static final int TIME_COUNTDOWN = 1000;
	private static final int TIME_QUANTUM = 5000;
	protected static final String PATH = "/mnt/extsd/BBPAW_APP";
	private AlarmManager mAlarm;
	private static final String TAG = "--TimeTontrolService--";
	private  boolean FLAG = false;
	private int currVolume;
	private AudioManager audioManager;
	private static final String CHARGING_PROTECT_PREFER_NAME = "charging_protect_config";
	public static final String CHARGING_PROTECT_SWITCH="chargingProtect_switch";
	@Override
	public void onCreate() {
		super.onCreate();
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		IntentFilter alarmIntent = new IntentFilter(
				SET_TIME_COUNTDOWN_ALARM_ACTION);
		registerReceiver(mAlarmChange, alarmIntent);
		IntentFilter alarmIntentQuantum = new IntentFilter(
				SET_TIME_QUANTUM_ALARM_ACTION);
		registerReceiver(mAlarmChange, alarmIntentQuantum);
		Log.d(TAG, "-------TimeTontrolService is running--->");
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mPowerConnectReceiver, intentFilter);
		startMediaUnMountedReceiver();
		registNetworkStateReceiver();
		registVolumeChangedReceiver();
		
	}
	//注册声音改变监听广播
	private void registVolumeChangedReceiver() {
		IntentFilter volumeChangedFilter = new IntentFilter();
		volumeChangedFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		//volumeChangedFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(mVolumeChangedReceiver, volumeChangedFilter);
	}
	//
	private void registNetworkStateReceiver() {
		//注册网络监听 
				IntentFilter isNetworkAvailableFilter = new IntentFilter(); 
				isNetworkAvailableFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
				registerReceiver(mNetworkStateReceiver, isNetworkAvailableFilter); 
	}
	
	private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "网络状态改变"); 
			boolean success = false; 
			//获得网络连接服务 
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); 
			// State state = connManager.getActiveNetworkInfo().getState(); 
			State state = connManager.getNetworkInfo( 
			ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态 
			if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络 
				Log.d("Wing", "网络状态改变    success");
				//context.sendBroadcast(new Intent(
						//com.worldchip.bbp.ect.util.Utils.LOGIN_OUT_ACTICON));
				success = true;
			} 
			if (!success) {
				Log.e(TAG, "网络状态改变    !success"); 
				LoginState loginState = LoginState.getInstance();
				loginState.loginOut();
				context.sendBroadcast(new Intent(
						com.worldchip.bbp.ect.util.Utils.LOGIN_OUT_ACTICON));
			}
		};
	};
	private void startMediaUnMountedReceiver() {
			
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
			intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
			intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
	        intentFilter.setPriority(2147483647);
	        intentFilter.addDataScheme("file");
	        registerReceiver(mMediaMountedReceiver, intentFilter);
		}
	
	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){  
            	final File file = new File(PATH);
            	String path = intent.getData().getPath();
            	Log.e(TAG, "Media mounted! path = "+path);
            	Log.e(TAG, "file.exist "+file.exists());
            	if(path.contains("extsd")){
            		if (file.exists()) {
            			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {     
                        	Intent i=new Intent(context, InstallApkActivity.class);
                        	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        	context.startActivity(i);    
                        }
            		}
				}
            }
            if (action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {       	
            	Intent i = new Intent();
                i.setAction(Utils.INSTALL_APK_ACTION);
    			context.sendBroadcast(i);  
            }
            
        }  
    };


	/**
	 * ����/ֹͣ���ӿ���
	 */
	public void onStartTime(boolean isCreate, Context context) {
		createAlarm(isCreate, context);
	}

	/**
	 * ����/ֹͣʱ��ο���
	 */
	public void onStartTimeQuantum(boolean isCreate, Context context) {
		createAlarmQuantum(isCreate, context);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (LoginState.getInstance().isLogin() && HttpUtils.isNetworkConnected(getApplicationContext())) {
					Log.d("TimeTontrolService", "------TimeTontrolService------>>" + "executed at" + new Date().toString());
					try {
						String databasePath = "sdcard/BBPaw/.Data/bbpaw_data.db";
						DBGoldHelper dbGoldHelper = new DBGoldHelper(TimeTontrolService.this);
						SQLiteDatabase  db  = CousorSqliteDataUtil.getDataBase(databasePath);
						String tableName = "game_record_table";
						String tableName2 = "game_action_table";
						String tableName3 = "gold_table";
						if(new File(databasePath).exists())
							CousorSqliteDataUtil.cursorData(databasePath, tableName, TimeTontrolService.this);
							dbGoldHelper.deleteTable(tableName);
							CousorSqliteDataUtil.cursorGoldData(databasePath, tableName3, TimeTontrolService.this);
							System.out.println("---------cursorGoldData----TimeTontrolService---->>>");
							dbGoldHelper.deleteTable(tableName3);
							CousorSqliteDataUtil.cursorDataGameAtion(databasePath, tableName2, TimeTontrolService.this);
							System.out.println("---------cursorDataGameAtion----TimeTontrolService---->>>");
							dbGoldHelper.deleteTable(tableName2);
							//TRUNCATE TABLE name
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		AlarmManager  manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int minuteTimer = 3 * 60 * 1000;//3 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + minuteTimer;
		Intent i = new Intent(this, DetectionAlarmReceiver.class);
		i.setAction("com.worldchip.bbp.ect.service.TimeTontrolService");
		PendingIntent pi =  PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mAlarmChange);
		if(mPowerConnectReceiver !=null){
			unregisterReceiver(mPowerConnectReceiver);
		}
		if(mMediaMountedReceiver !=null){
			unregisterReceiver(mMediaMountedReceiver);
		}
		if (mNetworkStateReceiver != null) {
			unregisterReceiver(mNetworkStateReceiver); 
		}
		
	}

	private BroadcastReceiver mAlarmChange = new BroadcastReceiver() {
		private SharedPreferences preferences;
		private Context context;
		@Override
		public void onReceive(Context context, Intent intent) {
			this.context = context;
			String action = intent.getAction();
			preferences = context.getSharedPreferences("time_info", 0);
			if (action.equals(SET_TIME_COUNTDOWN_ALARM_ACTION)) {
				if (preferences != null) {
					int time = preferences.getInt("countdown", 0);
					if (time <= 0) {
						/*
						 * Intent intentStart = new
						 * Intent(TimeTontrolService.this
						 * ,PassLockActivity.class);
						 * intentStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						 * startActivity(intentStart);
						 */
						// preferences.edit().putInt("countdownState",
						// 0).commit();
						preferences.edit()
								.putBoolean("isTimeCountdownOK", false)
								.commit();
						createAlarm(false, context);
					} else {
						time--;
						preferences.edit().putInt("countdown", time).commit();
						createAlarm(true, context);
					}
				}
			} else if (action.equals(SET_TIME_QUANTUM_ALARM_ACTION)) {
				if (preferences != null) {
					// int dateTimeState = preferences.getInt("dateTimeState",
					// 0);
					// int quantumState = preferences.getInt("quantumState", 0);
					boolean isTimeOK = preferences
							.getBoolean("isTimeOK", false);
					boolean isDateOK = preferences
							.getBoolean("isDateOK", false);
					String startTime = preferences.getString("startTime", "");
					String endTime = preferences.getString("endTime", "");
					int mon = preferences.getInt("mon", 0);
					int tues = preferences.getInt("tues", 0);
					int wed = preferences.getInt("wed", 0);
					int thur = preferences.getInt("thur", 0);
					int fri = preferences.getInt("fri", 0);
					int sta = preferences.getInt("sta", 0);
					int sun = preferences.getInt("sun", 0);
					Calendar c = Calendar.getInstance();
					int week = c.get(Calendar.DAY_OF_WEEK);
					if (isDateOK) // ��
					{
						if (isTimeOK) // �� + ʱ��λ��
						{
							if (mon != 0 || tues != 0 || wed != 0 || thur != 0
									|| fri != 0 || sta != 0 || sun != 0) {
								switch (week) {
								case 1:
									if (sun == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 2:
									if (mon == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 3:
									if (tues == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 4:
									if (wed == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 5:
									if (thur == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 6:
									if (fri == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								case 7:
									if (sta == 1) {
										if (TimeTontrol
												.startTimeTontrol(startTime)) {
											if (TimeTontrol
													.endTimeTontrol(endTime)) {
												createAlarmQuantum(true,
														context);
											} else {
												setTimeControlState();
											}
										} else {
											createAlarmQuantum(true, context);
										}
									} else {
										setTimeControlState();
									}
									break;
								default:
									break;
								}
							} else { // �����ܿ��� ��û��������һ�������� �Ͱ�ʱ��ο���
								if (TimeTontrol.startTimeTontrol(startTime)) {
									if (TimeTontrol.endTimeTontrol(endTime)) {
										createAlarmQuantum(true, context);
									} else {
										setTimeControlState();
									}
								} else {
									createAlarmQuantum(true, context);
								}
							}
						} else {
							switch (week) {
							case 1:
								if (sun == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							case 2:
								if (mon == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							case 3:
								if (tues == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							case 4:
								if (wed == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							case 5:
								if (thur == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							case 6:
								if (fri == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();

								}
								break;
							case 7:
								if (sta == 1) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
								break;
							default:
								break;
							}
						}
					} else {
						if (isTimeOK) {
							if (TimeTontrol.startTimeTontrol(startTime)) {
								if (TimeTontrol.endTimeTontrol(endTime)) {
									createAlarmQuantum(true, context);
								} else {
									setTimeControlState();
								}
							} else {
								createAlarmQuantum(true, context);
							}
						}
					}
				}
			}
		}

		private void setTimeControlState() {
			Log.e(TAG, "time is coming ");
			preferences.edit().putBoolean("isTimeOK", false).commit();
			preferences.edit().putBoolean("isDateOK", false).commit();
			preferences.edit().putBoolean("isTimeCountdownOK", false).commit();
			Intent intentStart = new Intent();
			intentStart.setComponent(new ComponentName("com.worldchip.bbp.ect",
					"com.worldchip.bbp.ect.activity.PassLockActivity"));
			intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intentStart);
			createAlarmQuantum(false, context);
		}
	};

	/**
	 * ����ʱ�����
	 * 
	 * @param isCreate
	 * @param context
	 */
	private void createAlarm(boolean isCreate, Context context) {
		mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int alarmType = AlarmManager.RTC_WAKEUP;

		Calendar calendar = Calendar.getInstance();
		Intent intent = new Intent(SET_TIME_COUNTDOWN_ALARM_ACTION);
		calendar.set(Calendar.MILLISECOND, TIME_COUNTDOWN); // һ�������ڵ�Ҫ���ú���
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		if (isCreate) {
			mAlarm.set(alarmType, calendar.getTimeInMillis(), pendingIntent);
		} else {
			mAlarm.cancel(pendingIntent);
		}
	}

	/**
	 * ʱ��ο���
	 * 
	 * @param isCreate
	 * @param context
	 */
	private void createAlarmQuantum(boolean isCreate, Context context) {
		mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int alarmType = AlarmManager.RTC_WAKEUP;

		Calendar calendar = Calendar.getInstance();
		Intent intent = new Intent(SET_TIME_QUANTUM_ALARM_ACTION);
		calendar.set(Calendar.MILLISECOND, TIME_QUANTUM); // һ�������ڵ�Ҫ���ú���
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		if (isCreate) {
			mAlarm.set(alarmType, calendar.getTimeInMillis(), pendingIntent);
		} else {
			mAlarm.cancel(pendingIntent);
		}
	}
	
	private BroadcastReceiver mPowerConnectReceiver = new BroadcastReceiver(){  
		//充电广播
		private Context context;
        @Override  
        public void onReceive(Context context, Intent intent) { 
        	this.context = context;
            String action = intent.getAction();
            SharedPreferences chargingProtectShare = getSharedPreferences(CHARGING_PROTECT_PREFER_NAME, 0);
            int messagePushSwitch = chargingProtectShare.getInt(CHARGING_PROTECT_SWITCH, 1);
            if(action.equals(Intent.ACTION_BATTERY_CHANGED) && (messagePushSwitch == 1)){
            	int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                    status == BatteryManager.BATTERY_STATUS_FULL;
                
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                
                int level = intent.getIntExtra("level", 0);  
                int scale = intent.getIntExtra("scale", 100);  
                  
                Log.e("Lee", "isCharging="+isCharging+"; usbCharge="+usbCharge+"; acCharge="+acCharge
                		+"; level="+level+"; scale="+scale);
                if (!FLAG) {
                	  if(isCharging && acCharge ){
                      	Intent i = new Intent();
                          i.setClass(context, ChargingActivity.class);
                          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          getApplicationContext().startActivity(i);
                          FLAG = true;
                          return;
                      }  /*else if (isCharging && usbCharge) {
                    	  Intent intentSelectAvtivity = new Intent(context, StoreOrChargeActivity.class);
                    	  intentSelectAvtivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    	  context.startActivity(intentSelectAvtivity);
                    	  FLAG = true;
                          return;
                      }*/
                }
                if(!isCharging) {
                	FLAG = false;
                }
                //notifyBattery(level,scale,status);  
            }
        }
	};
	
	private BroadcastReceiver mVolumeChangedReceiver  = new BroadcastReceiver() {
	//接受声音变化的广播，更改数据库中相应的值。
		@Override
		public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
					currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					Log.d("Wing", "TimeTontrolService ---mVolumeChangedReceiver---currVolume: " + currVolume);
					if (currVolume == 0) {
						DataManager.setMainIsPlayEffectEnable(context, false);
					} else {
						DataManager.setMainIsPlayEffectEnable(context, true);
					}
				}
		}
	};
	
}

