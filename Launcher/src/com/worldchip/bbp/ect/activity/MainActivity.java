package com.worldchip.bbp.ect.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.ImageAdapter;
import com.worldchip.bbp.ect.adapter.MyGallery;
import com.worldchip.bbp.ect.adapter.MyGallery.MYGalleryOnTounchListener;
import com.worldchip.bbp.ect.common.ChickenView;
import com.worldchip.bbp.ect.db.DataManager;
import com.worldchip.bbp.ect.entity.IconInfo;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.entity.UpdateBabyInfo;
import com.worldchip.bbp.ect.gwwheel.OnWheelScrollListener;
import com.worldchip.bbp.ect.gwwheel.WheelView;
import com.worldchip.bbp.ect.image.utils.ImageLoader;
import com.worldchip.bbp.ect.image.utils.ImageLoader.Type;
import com.worldchip.bbp.ect.service.ScanService;
import com.worldchip.bbp.ect.service.TimeTontrolService;
import com.worldchip.bbp.ect.util.AgeGroupBarAnimationUtils;
import com.worldchip.bbp.ect.util.BBPawAnimationUtils;
import com.worldchip.bbp.ect.util.BBPawAnimationUtils.MyAnimationListener;
import com.worldchip.bbp.ect.util.Configure;
import com.worldchip.bbp.ect.util.FishAnimationUtils;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.HttpResponseCallBack;
import com.worldchip.bbp.ect.util.HttpUtils;
import com.worldchip.bbp.ect.util.JsonUtils;
import com.worldchip.bbp.ect.util.LunarCalendar;
import com.worldchip.bbp.ect.util.MD5Util;
import com.worldchip.bbp.ect.util.PangxieAnimationUtils;
import com.worldchip.bbp.ect.util.PictureUploadUtils;
import com.worldchip.bbp.ect.util.SettingAnimationUtils;
import com.worldchip.bbp.ect.util.Utils;
import com.worldchip.bbp.ect.view.BearDrawableAnim;
import com.worldchip.bbp.ect.view.CircleImageView;
import com.worldchip.bbp.ect.view.GlobalProgressDialog;

//import com.worldchip.bbp.ect.util.MD5Check;

public class MainActivity extends Activity implements OnClickListener,
		OnTouchListener, MYGalleryOnTounchListener, HttpResponseCallBack {
	private static final int HTTP_UPDETE_CODE = 119;
	private static final int UPDATE_BBPAW_ALLPHOTO = 2;
	private static final int FIRST_SHOW_USER_EDIT = 7;
	private static final int EDIT_BABYINFORMATION = 9;
	private static final int UPDATE_BBPAW_AGEDUAN_THREE = 13;
	private static final int UPDATE_BBPAW_AGEDUAN_FIVE = 14;
	private static final int UPDATE_BBPAW_AGEDUAN_SEVEN = 15;
	private static final int REFRESH_USER_VIEW = 16;
	private static final int ANBLE_CLICK_NUMBER = 1010;
	// private static final int UPDATE_BABYINFO=16;
	/** 涓撻敓鏂ゆ嫹閿熸枻鎷疯閿熸枻鎷锋伅閿熸枻鎷烽敓鏂ゆ嫹 **/
	public static final String INFORMATION_MAIN_TYPE = "102";

	public static final String NOTIFY_MSG_MAIN_TYPE = "98";
	public static final String RECEIVE_PUSH_MESSAGE_TYPE_KEY = "messageType";
	public static final String RECEIVE_PUSH_MESSAGE_ACTION = "com.worldchip.bbpaw.client.manager.receivePushMessage";
	public static final int REFRESH_UNREAD_COUNT = 12;
	private boolean first ;
	private int startCount = 0;
	private Typeface mTypeface;
	private RelativeLayout mHomeMain;
	private MyGallery mGallery;
	private Animation mGalleryAnim;
	private ImageAdapter adapter;
	private AnimationDrawable mAnimationDrawableElephantsErduo;
	private ImageView mElephantErDuo, mPangXie;
	private ImageView mFinshView;
	private SoundPool soundPool = null;
	private HashMap<Integer, Integer> mSoundMap = null;
	private PopupWindow mEditInformation;
	private TextView mBaby_name, mBaby_sex, mBoy_tv, mGirl_tv, mBaby_year,
			mBaby_month, mBaby_ri, mBaby_fyear, mBaby_fmonth, mBaby_fri;
	private RelativeLayout mSecand_edit_relayout, mFirst_relayout,
			mPopuwidow_relayout, mPopuWindow_bg;
	private LinearLayout mBabyYearLayout, mBabyMonthLayout, mBabyRiLayout;
	private ImageView mBabyTete, mBaby_edit, mCancel_edit, mBabyMoren_edite;
	private TextView mBb_nicheng, mBb_shengri, mBb_nicheng_edit,
			mBb_shengri_edit, mBb_brithday_year_tv, mBb_brithday_month_tv,
			mBb_brithday_ri_tv;
	private RadioGroup mRaioGroup;
	private SharedPreferences mBabyinfo, mShared, mFestival, mAccount;
	private RadioButton mBoy_radiobtn, mGirl_radiobtn;
	private EditText mBaby_editname;
	private Button mSave, mYear, mMonth, mRi;
	private WheelView mYearWheelView;
	private WheelView mMonthWheelView;
	private WheelView mDayWheelView;

	private boolean mYearWheelViewShowing = false;
	private boolean mMonthWheelViewShowing = false;
	private boolean mDayWheelViewShowing = false;

	private ChickenView[] mChickenViews = null;
	private RelativeLayout mAgeGroupBar;
	private CircleImageView mPhotoView = null;
	private View mWindowRLView = null;
	private TextView mNameTV;
	private TextView mAgeTV;
	private TextView mAgeSwitchTV;
	// private boolean isTransitionDoorView = true;
	private boolean isDisplayAgeGroupBar = false;
	private TextView[] mAgeGroupBarView = null;
	private static final int AGE_GROUP_BAR_01 = 0;
	private static final int AGE_GROUP_BAR_02 = 1;
	private static final int AGE_GROUP_BAR_03 = 2;
	private static final int AGE_GROUP_BAR_COUNT = 3;
	private static final int[] AGR_GROUP_BAR_RES = {
			R.drawable.age_group_bg_01, R.drawable.age_group_bg_02,
			R.drawable.age_group_bg_03 };
	// private static final int DEFAULT_AGE_GROUP_SELECT = 1;
	private static final int DEFAULT_AGE_GROUP_SELECT = 2;
	private Resources mResources;
	private static ImageView mBearIV = null;
	private static ImageView mGrassIV = null;
	private static ImageView mWavesIV = null;
	private static final int START_ACTIVITY = 100;
	private boolean isErduoClickEnable = true;
	private boolean grassRunning = false;
	private BearDrawableAnim mBearDrawableAnim = null;

	private ImageView mImageViewWifi, mImageViewPower;
	private IntentFilter mWifiIntentFilter, mIntentFilter;

	private TextView mTvHourDecade, mTvHourUnits, mCountdownHourMaohao,
			mTvMinuteDecade, mTvMinuteUnits, mCountdownMaohao, mTvSecondDecade,
			mTvSecondUnits;
	private MinuteCountDown mc;
	private TimeQuanCountdownTimer tqct;
	private RelativeLayout mRedMessage;
	private TextView mMessageCount;
	private MainPushMessageReceiver mPushMessageReceiver;
	private LoginReceiver mLoginReceiver;
	private Context mContext = null;
	private static final int START_SETUPWIZARD = 101;
	private ImageView mCh_UK;
	private boolean isChange = true;
	private String mLanguage;
	private static final int CLICK_SOUND_KEY = 30;
	private boolean mUserPhotoChange = false;
	private static final int USER_EDIT_RESULT_NOTWORK = 0;
	@SuppressWarnings("unused")
	private static final int USER_EDIT_RESULT_SUCCESS = 1;
	private static final int USER_EDIT_RESULT_FAILURE = 2;
	private static final int START_PHOTO_UPLOAD = 1000;
	private static final int USER_EDIT_COMPLETE = 1001;
	private static final int PHOTO_UPLOAD_COMPLETE = 1002;
	protected static final int INIT_DATA = 1003;
	protected static final int RESUME_DATA = 1004;
	protected static final int FESTIVAL_BLESS_PUSH = 1005;
	public static boolean isIconClick = false;

	private GlobalProgressDialog mGlobalProgressDialog;
	private String mEditPhotoPath = null;
	
	private SharedPreferences mPref;
	private SharedPreferences.Editor editor;
	public static int MODE =  Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
	public static final String PREFER_NAME = "save_account";

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FESTIVAL_BLESS_PUSH:
				pushFestivalBless();
				break;
			case INIT_DATA:
				initData();
				LoginState loginState = LoginState.getInstance();
				boolean isLogin = loginState.isLogin();
				if (!isLogin) {
					loginAccount();
				}
				break;
			case RESUME_DATA:
				resumeData();
				break;
			case FIRST_SHOW_USER_EDIT:
				showUserInfo(true);
				break;
			case START_ACTIVITY:
				if (mAnimationDrawableElephantsErduo != null) {
					mAnimationDrawableElephantsErduo.stop();
				}
				isErduoClickEnable = true;
				Utils.startApp(MainActivity.this,
						"com.worldchip.bbp.bbpawmanager.cn",
						"com.worldchip.bbp.bbpawmanager.cn.activity.MainActivity");
				break;
			case EDIT_BABYINFORMATION:
				showUserInfo(false);
				break;
			case REFRESH_UNREAD_COUNT:
				if (DataManager.getMessageValue(MainActivity.this) > 0) {
					mRedMessage.setVisibility(View.VISIBLE);
					mMessageCount.setText(String.valueOf(DataManager
							.getMessageValue(MainActivity.this)));
				} else {
					mRedMessage.setVisibility(View.GONE);
				}
				break;
			case UPDATE_BBPAW_AGEDUAN_THREE:
				mAgeSwitchTV.setText(R.string.age_group_text01);
				break;
			case UPDATE_BBPAW_AGEDUAN_FIVE:
				mAgeSwitchTV.setText(R.string.age_group_text02);
				break;
			case UPDATE_BBPAW_AGEDUAN_SEVEN:
				mAgeSwitchTV.setText(R.string.age_group_text03);
				break;
			case START_SETUPWIZARD:
				Utils.startApplication(MyApplication.getAppContext(),
						"com.worldchip.bbpaw.bootsetting",
						"com.worldchip.bbpaw.bootsetting.activity.MainActivity");
				break;
			case REFRESH_USER_VIEW:
				refreshUserView();
				break;

			case START_PHOTO_UPLOAD:
				startProgressDialog();
				break;
			case PHOTO_UPLOAD_COMPLETE:
				String imageUrl = "";
				if (msg.obj != null) {
					imageUrl = (String) msg.obj;
					commitUserChange(imageUrl);
				} else {
					commitUserChange(LoginState.getInstance().getPhotoUrl());
				}
				break;

			case USER_EDIT_COMPLETE:
				int result = msg.arg1;
				stopProgressDialog();
				if (result == USER_EDIT_RESULT_NOTWORK) {
					Utils.showToastMessage(MyApplication.getAppContext(),
							getString(R.string.network_error_text));
				}
				break;
			case ANBLE_CLICK_NUMBER:
				mSave.setClickable(true);
				break;
			case HTTP_UPDETE_CODE:
				stopProgressDialog();
				Bundle data = msg.getData();
				String httpTag = null;
				if (data != null) {
					httpTag = data.getString("httpTag");
				}
				if (msg.arg1 == Configure.SUCCESS) {
					if (httpTag == null)
						return;
					String results = data.getString("result");
					String resultCode = JsonUtils.doParseValueForKey(results, "resultCode");
					if (resultCode == null || TextUtils.isEmpty(resultCode)) {
						return;
					}
					if (httpTag.equals(Configure.HTTP_TAG_UPDATE_BABYINFO)) {
						UpdateBabyInfo updateBabyInfo = null;
						if (Integer.parseInt(resultCode) == HttpUtils.HTTP_RESULT_CODE_SUCCESS) {
							updateBabyInfo = JsonUtils
									.updateBabyInfoJson2bean(results);
						}
						handResultMessge(Integer.parseInt(resultCode),
								Configure.HTTP_TAG_UPDATE_BABYINFO,	updateBabyInfo);
					} else if (httpTag.equals(Configure.HTTP_TAG_LOGIN) ){
						LoginState loginInfo = JsonUtils.doLoginJson2Bean(results);
						Log.d("Wing", "------++++----> " + loginInfo.toString());
						handResultMessge(loginInfo.getResultCode(), Configure.HTTP_TAG_LOGIN, loginInfo);
					}
				} else {
					if (httpTag.equals(Configure.HTTP_TAG_UPDATE_BABYINFO)) {
						Utils.showToastMessage(MyApplication.getAppContext(),
								getString(R.string.update_password_failure));
					} else if (httpTag.equals(Configure.HTTP_TAG_LOGIN)) {
						Utils.showToastMessage(MainActivity.this, getString(R.string.login_failure));
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		
		mPref = getSharedPreferences(PREFER_NAME, MODE);
		editor = mPref.edit();
		
		HttpCommon.hideSystemUI(MainActivity.this, true);
		initView();
		initHouseView();
		mResources = getResources();
		// startFinshAnim();//鱼的动画
		mTypeface = Typeface.createFromAsset(getAssets(), "Droidhuakangbaoti.TTF");

		adapter = new ImageAdapter(Utils.getGalleryImageList(), MainActivity.this);
		mGallery.setAdapter(adapter);
		mGallery.setSelection(1947);
		mGallery.setOnItemSelectedListener(mGallerySelectListener);
		mGallery.setOnItemClickListener(onItemClickListener);
		mShared = getSharedPreferences("config", Context.MODE_PRIVATE);
		startCount = mShared.getInt("startCount", 0);
		if (startCount == 0) {
			mShared.edit().putInt("startCount", 1).commit();
		}else if (startCount == 1) {
			mShared.edit().putInt("startCount", 2).commit();
		}else if (startCount == 2) {
			mShared.edit().putInt("startCount", 3).commit();
		}
		if (startCount < 3) {
			Utils.initBBpawDataBases(MyApplication.getAppContext());
			mHandler.sendEmptyMessageDelayed(START_SETUPWIZARD, 2000);
		}
		//注册接受登录广播
		registerLoginReceiver();
		
	}
	//开机登录
	private void loginAccount() {
		String account = mAccount.getString("account", "");
		String password = mAccount.getString("password", "");
		String cpuSerial = Utils.getCpuSerial(this);
		Log.d("Wing", "loginAccount: account=" + account + " password= " + password);
		if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
			String httpUrl = Configure.LOGIN_URL
					.replace("ACCOUNT", account)
					.replace("PASSWORD", MD5Util.string2MD5(password))
					.replace("DEVICE_ID", cpuSerial);
			Log.d("Wing", "httpUrl == " + httpUrl);
			HttpUtils.doPost(httpUrl, MainActivity.this,
					Configure.HTTP_TAG_LOGIN);
		} /*else {
			Utils.showToastMessage(MainActivity.this,
					getString(R.string.login_input_error));
		}*/
	}
	private void registerLoginReceiver() {
		mLoginReceiver = new LoginReceiver();
		IntentFilter loginReceiverFilter = new IntentFilter();
		loginReceiverFilter.addAction(Utils.LOGIN_SECCUSS_ACTION);
		loginReceiverFilter.addAction(Utils.LOGIN_OUT_ACTICON);
		registerReceiver(mLoginReceiver, loginReceiverFilter);
	}
	private void initData() {
		chickenMove();
		runBearAnim();
	}

	private void resumeData() {
		
		// WIFI
		mWifiIntentFilter = new IntentFilter();
		mWifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		
		Intent intent = new Intent(MainActivity.this, TimeTontrolService.class);
		startService(intent);
		
		Intent intentScanService = new Intent(MainActivity.this, ScanService.class);
		startService(intentScanService);

		/*mLoginReceiver = new LoginReceiver();
		IntentFilter loginReceiverFilter = new IntentFilter();
		loginReceiverFilter.addAction(Utils.LOGIN_SECCUSS_ACTION);
		loginReceiverFilter.addAction(Utils.LOGIN_OUT_ACTICON);
		registerReceiver(mLoginReceiver, loginReceiverFilter);
		*/
		
		mPushMessageReceiver = new MainPushMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(RECEIVE_PUSH_MESSAGE_ACTION);
		registerReceiver(mPushMessageReceiver, filter);

		mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
		// runBearAnim();
		if (wifiIntentReceiver != null) {
			registerReceiver(wifiIntentReceiver, mWifiIntentFilter);
		}
		if (mIntentReceiver != null) {
			registerReceiver(mIntentReceiver, mIntentFilter);
		}
		// mHandler.removeMessages(UPDATE_BBPAW_ALLPHOTO);
		// mHandler.sendEmptyMessage(UPDATE_BBPAW_ALLPHOTO);

		SharedPreferences preferences = getSharedPreferences("time_info", 0);
		TimeTontrolService service = new TimeTontrolService();
		if (preferences != null) {
			// int countdownState = preferences.getInt("countdownState", 0);
			// int quantumState = preferences.getInt("quantumState", 0);
			// int dateTimeState = preferences.getInt("dateTimeState", 0);

			boolean isTimeCountdownOK = preferences.getBoolean(
					"isTimeCountdownOK", false);
			boolean isTimeOK = preferences.getBoolean("isTimeOK", false);
			boolean isDateOK = preferences.getBoolean("isDateOK", false);
			if (isTimeCountdownOK) {
				if (mc != null) {
					mc.cancel();
				}
				service.onStartTime(false, MainActivity.this);
				service.onStartTime(true, MainActivity.this);
				int time = preferences.getInt("countdown", 0);
				// 閿熺瓔闀夸负120閿熸枻鎷烽敓鏂ゆ嫹
				mc = new MinuteCountDown(time * 1000, 1000);
				mc.start();
			}

			if (isTimeOK) {
				if (tqct != null) {
					tqct.cancel();
				}
				service.onStartTimeQuantum(false, MainActivity.this);
				service.onStartTimeQuantum(true, MainActivity.this);
				String timequantumstart = preferences
						.getString("startTime", "");
				String timequantumend = preferences.getString("endTime", "");
				int timequantumstarthour = Integer.parseInt(timequantumstart
						.substring(0, 2));
				int timequantumstartminit = Integer.parseInt(timequantumstart
						.substring(3, 5));
				int timequantumendhour = Integer.parseInt(timequantumend
						.substring(0, 2));
				int timequantumendminit = Integer.parseInt(timequantumend
						.substring(3, 5));
				int times;
				if (timequantumstarthour == timequantumendhour) {
					times = timequantumendminit - timequantumstartminit;
				} else {
					if (timequantumendminit >= timequantumstartminit) {
						times = (timequantumendhour - timequantumstarthour) * 60
								+ (timequantumendminit - timequantumstartminit);
					} else {
						times = ((timequantumendhour - timequantumstarthour) * 60 + timequantumendminit)
								- timequantumstartminit;
					}
				}
			}
			if (isDateOK) {
				service.onStartTimeQuantum(false, MainActivity.this);
				service.onStartTimeQuantum(true, MainActivity.this);
			}
		}
		startGrassAnim();
	}
	
	private void startGrassAnim() {
		if (mGrassIV != null) {
			SettingAnimationUtils.startAnimation(mGrassIV, 4000);

		}
	}

	private void startFinshAnim() {
		// ImageView finshView = (ImageView) findViewById(R.id.fish_iv);
		RelativeLayout waterRL = (RelativeLayout) findViewById(R.id.water_rl);
		if (mFinshView != null && waterRL != null) {
			mFinshView.clearAnimation();
			waterRL.clearAnimation();
			FishAnimationUtils.startAnimation(mFinshView, waterRL,
					FishAnimationUtils.START_NEXT_ANIMATION_OFFSET);

		}
	}

	// 小熊动画
	private void runBearAnim() {
		if (mBearIV != null) {
			if (mBearDrawableAnim != null) {
				mBearDrawableAnim.stopAnimation();
			}
			mBearDrawableAnim = BearDrawableAnim.getInstance(mBearIV);
			mBearDrawableAnim.setBearAnimGroup(Utils.BEAR_ANIM_DEFAULT_INDEX);
			mBearDrawableAnim.startAnimation(1, 1000);
		}

	}
	private void stopBearAnim() {
		if (mBearDrawableAnim != null) {
			mBearDrawableAnim.stopAnimation();
			mBearDrawableAnim = null;
		}
	}

	// 小屋
	private void initHouseView() {
		mAgeSwitchTV = (TextView) findViewById(R.id.age_switch_tv);
		mWindowRLView = findViewById(R.id.door_rl_view);
		mWindowRLView.setOnClickListener(this);
		mAgeSwitchTV.setOnClickListener(this);
		mAgeGroupBar = (RelativeLayout) findViewById(R.id.rl_age_group_bar);
		mPhotoView = (CircleImageView) findViewById(R.id.photo_iv);
		mNameTV = (TextView) mWindowRLView.findViewById(R.id.name_tv);
		mNameTV.setSelected(true);
		mAgeTV = (TextView) mWindowRLView.findViewById(R.id.age_tv);

		mAgeGroupBarView = new TextView[AGE_GROUP_BAR_COUNT];
		mAgeGroupBarView[AGE_GROUP_BAR_01] = (TextView) findViewById(R.id.ib_age_group_01);
		mAgeGroupBarView[AGE_GROUP_BAR_02] = (TextView) findViewById(R.id.ib_age_group_02);
		mAgeGroupBarView[AGE_GROUP_BAR_03] = (TextView) findViewById(R.id.ib_age_group_03);
		mAgeGroupBarView[AGE_GROUP_BAR_01].setOnClickListener(this);
		mAgeGroupBarView[AGE_GROUP_BAR_02].setOnClickListener(this);
		mAgeGroupBarView[AGE_GROUP_BAR_03].setOnClickListener(this);
		/*
		 * String[] ageGroups = getResources().getStringArray(
		 * R.array.age_group_arry);
		 * mAgeSwitchTV.setText(ageGroups[DEFAULT_AGE_GROUP_SELECT]);
		 * updateAgeGroupBar(DEFAULT_AGE_GROUP_SELECT);
		 */
		//refreshUserView();
	}

	private void initSoundPools() {
		if (mSoundMap != null && mSoundMap.size() > 0) {
			mSoundMap.clear();
		}
		if (soundPool == null) {
			soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		}
		if (mSoundMap == null) {
			mSoundMap = new HashMap<Integer, Integer>();
		}
		mSoundMap.put(CLICK_SOUND_KEY, soundPool.load(this, R.raw.click, 1));
		int[] appNameSounds = null;
		int languageIndex = Utils.getLanguageInfo(MyApplication.getAppContext());
		if (languageIndex == Utils.CH_LANGUAGE_INDEX) {
			appNameSounds = Utils.APP_NAME_SOUNDS_RES_MAP_CN;
		} else {
			appNameSounds = Utils.APP_NAME_SOUNDS_RES_MAP_ENG;
		}
		if (appNameSounds != null && appNameSounds.length > 0) {
			for (int i = 0; i < appNameSounds.length; i++) {
				mSoundMap.put(i + 1, soundPool.load(MainActivity.this, appNameSounds[i], 1));
			}
		}
	}

	private void initView() {
		mBabyinfo = getSharedPreferences("babyinfo", 0);
		mFestival = getSharedPreferences("mfestival", MODE_PRIVATE);
		mAccount = getSharedPreferences("save_account", MODE_PRIVATE);
		//初始化声音池
		initSoundPools();
		// 閿熸枻鎷�
		mBearIV = (ImageView) findViewById(R.id.ppaw_bear);
		mBearIV.setOnTouchListener(this);

		mGrassIV = (ImageView) findViewById(R.id.grass_iv);

		mGrassIV.setOnClickListener(this);
		mWavesIV = (ImageView) findViewById(R.id.waves_iv);
		mHomeMain = (RelativeLayout) findViewById(R.id.home_main);

		mGallery = (MyGallery) findViewById(R.id.gallery);
		mGallery.setOnTounchListener(this);
		mGalleryAnim = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		if (mGalleryAnim != null) {
			mGallery.startAnimation(mGalleryAnim);
		}
		// 鱼儿
		mFinshView = (ImageView) findViewById(R.id.fish_iv);
		mFinshView.setOnClickListener(this);
		// 大象耳朵
		mElephantErDuo = (ImageView) findViewById(R.id.elephants_erduo);
		mElephantErDuo.setImageResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "yuerbaodian_1", "drawable"));
		mElephantErDuo.setOnClickListener(this);
		// 螃蟹
		mPangXie = (ImageView) findViewById(R.id.pangxie);
		mPangXie.setOnClickListener(this);
		// wifi
		mImageViewWifi = (ImageView) findViewById(R.id.wifi_imageview);
		mImageViewPower = (ImageView) findViewById(R.id.dianchi_imageview);

		//
		mTvHourDecade = (TextView) findViewById(R.id.mTvHourDecade);
		// 鏃�(鍗佷綅閿熸枻鎷�
		mTvHourUnits = (TextView) findViewById(R.id.mTvHourUnits);
		// 鍐掗敓鏂ゆ嫹
		mCountdownHourMaohao = (TextView) findViewById(R.id.countdown_HourMaohao);
		// 閿熸枻鎷烽敓鎺ワ綇鎷峰崄浣嶉敓鏂ゆ嫹
		mTvMinuteDecade = (TextView) findViewById(R.id.mTvMinuteDecade);
		// 閿熸枻鎷烽敓鎺ワ綇鎷烽敓鏂ゆ嫹浣嶉敓鏂ゆ嫹
		mTvMinuteUnits = (TextView) findViewById(R.id.mTvMinuteUnits);
		// 鍐掗敓鏂ゆ嫹
		mCountdownMaohao = (TextView) findViewById(R.id.countdown_maohao);
		// 閿熻锛堝崄浣嶉敓鏂ゆ嫹
		mTvSecondDecade = (TextView) findViewById(R.id.mTvSecondDecade);
		// 閿熻锛堥敓鏂ゆ嫹浣嶉敓鏂ゆ嫹
		mTvSecondUnits = (TextView) findViewById(R.id.mTvSecondUnits);
		// 推送消息
		mRedMessage = (RelativeLayout) findViewById(R.id.redmessage_relayout);
		mMessageCount = (TextView) findViewById(R.id.message_tuisong);
		mRedMessage.setVisibility(View.GONE);
		// 中英文切换按钮
		mCh_UK = (ImageView) findViewById(R.id.china_english_qiechuan);
		mCh_UK.setOnClickListener(this);
		updateLanguageButton(Utils.getLanguageInfo(MyApplication.getAppContext()));
		//startGuid();
	}

	private void startGuid() {
		boolean bootSettingsValue = Utils.getSetupWizardPreference(MyApplication.getAppContext());
		SharedPreferences preferences = getSharedPreferences("user_info", 0);
		boolean ectShowSetup = preferences.getBoolean("isSetupWizard", true);

		boolean isSetupWizard = (bootSettingsValue && ectShowSetup ? true
				: false);
		Log.e("lee", "isSetupWizard == " + isSetupWizard
				+ " bootSettingsValue == " + bootSettingsValue
				+ " ectShowSetup == " + ectShowSetup);
		if (isSetupWizard) {
			preferences.edit().putBoolean("isSetupWizard", false).commit();
			mHandler.sendEmptyMessageDelayed(START_SETUPWIZARD, 2000);
		}
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷锋椂閿熸枻鎷烽敓鑺傝鎷烽敓鏂ゆ嫹 閿熺瓔闀夸负120閿熸枻鎷烽敓鏂ゆ嫹
	 */
	class MinuteCountDown extends CountDownTimer {
		public MinuteCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mTvHourDecade.setBackgroundResource(R.drawable.countdown_zero);
			mTvHourUnits.setBackgroundResource(R.drawable.countdown_zero);
			mTvMinuteDecade.setBackgroundResource(R.drawable.countdown_zero);
			mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_zero);
			mCountdownMaohao.setBackgroundResource(R.drawable.countdown_maohao);
			mTvSecondDecade.setBackgroundResource(R.drawable.countdown_zero);
			mTvSecondUnits.setBackgroundResource(R.drawable.countdown_zero);
			SharedPreferences preferences = getSharedPreferences("time_info", 0);
			preferences.edit().putBoolean("isTimeCountdownOK", false).commit();
			Intent intentStart = new Intent(MainActivity.this, PassLockActivity.class);

			startActivity(intentStart);
			// finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int sum_millisUntilFinished = (int) (millisUntilFinished - 1000);
			int hour = (int) millisUntilFinished / (60 * 60 * 1000);
			// int hour_decade = hour / 10;
			int hour_units = hour % 10;

			int minute = sum_millisUntilFinished / (60 * 1000);
			int minute_decade = minute % 60 / 10;
			int minute_units = minute % 10;

			int second = sum_millisUntilFinished / 1000;
			int second_decade = second % 60 / 10;
			int second_units = second % 10;

			mTvHourDecade.setBackgroundResource(R.drawable.countdown_zero);
			mCountdownHourMaohao
					.setBackgroundResource(R.drawable.countdown_maohao);
			mCountdownMaohao.setBackgroundResource(R.drawable.countdown_maohao);

			switch (hour_units) {
			case 1:
				mTvHourUnits.setBackgroundResource(R.drawable.countdown_one);
				break;
			case 0:
				mTvHourUnits.setBackgroundResource(R.drawable.countdown_zero);
				break;
			default:
				break;
			}

			switch (minute_decade) {
			case 6:
				mTvMinuteDecade.setBackgroundResource(R.drawable.countdown_six);
				break;
			case 5:
				mTvMinuteDecade
						.setBackgroundResource(R.drawable.countdown_five);
				break;
			case 4:
				mTvMinuteDecade
						.setBackgroundResource(R.drawable.countdown_four);
				break;
			case 3:
				mTvMinuteDecade
						.setBackgroundResource(R.drawable.countdown_three);
				break;
			case 2:
				mTvMinuteDecade.setBackgroundResource(R.drawable.countdown_two);
				break;
			case 1:
				mTvMinuteDecade.setBackgroundResource(R.drawable.countdown_one);
				break;
			case 0:
				mTvMinuteDecade
						.setBackgroundResource(R.drawable.countdown_zero);
				break;
			default:
				break;
			}

			switch (minute_units) {
			case 9:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_nine);
				break;
			case 8:
				mTvMinuteUnits
						.setBackgroundResource(R.drawable.countdown_eight);
				break;
			case 7:
				mTvMinuteUnits
						.setBackgroundResource(R.drawable.countdown_seven);
				break;
			case 6:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_six);
				break;
			case 5:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_five);
				break;
			case 4:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_four);
				break;
			case 3:
				mTvMinuteUnits
						.setBackgroundResource(R.drawable.countdown_three);
				break;
			case 2:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_two);
				break;
			case 1:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_one);
				break;
			case 0:
				mTvMinuteUnits.setBackgroundResource(R.drawable.countdown_zero);
				break;
			default:
				break;
			}

			switch (second_decade) {
			case 6:
				mTvSecondDecade.setBackgroundResource(R.drawable.countdown_six);
				break;
			case 5:
				mTvSecondDecade
						.setBackgroundResource(R.drawable.countdown_five);
				break;
			case 4:
				mTvSecondDecade
						.setBackgroundResource(R.drawable.countdown_four);
				break;
			case 3:
				mTvSecondDecade
						.setBackgroundResource(R.drawable.countdown_three);
				break;
			case 2:
				mTvSecondDecade.setBackgroundResource(R.drawable.countdown_two);
				break;
			case 1:
				mTvSecondDecade.setBackgroundResource(R.drawable.countdown_one);
				break;
			case 0:
				mTvSecondDecade
						.setBackgroundResource(R.drawable.countdown_zero);
				break;
			default:
				break;
			}

			switch (second_units) {
			case 9:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_nine);
				break;
			case 8:
				mTvSecondUnits
						.setBackgroundResource(R.drawable.countdown_eight);
				break;
			case 7:
				mTvSecondUnits
						.setBackgroundResource(R.drawable.countdown_seven);
				break;
			case 6:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_six);
				break;
			case 5:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_five);
				break;
			case 4:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_four);
				break;
			case 3:
				mTvSecondUnits
						.setBackgroundResource(R.drawable.countdown_three);
				break;
			case 2:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_two);
				break;
			case 1:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_one);
				break;
			case 0:
				mTvSecondUnits.setBackgroundResource(R.drawable.countdown_zero);
				break;
			default:
				break;
			}
		}
	}

	class TimeQuanCountdownTimer extends CountDownTimer {

		public TimeQuanCountdownTimer(long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {

		}

		@Override
		public void onTick(long millisUntilFinished) {
			int sum_millisUntilFinished = (int) (millisUntilFinished - 1000);

			int hour = (int) millisUntilFinished / (60 * 60 * 1000);
			// int hour_decade = hour / 10;
			int hour_units = hour % 10;

			int minute = sum_millisUntilFinished / (60 * 1000);
			int minute_decade = minute % 60 / 10;
			int minute_units = minute % 10;

			int second = sum_millisUntilFinished / 1000;
			int second_decade = second % 60 / 10;
			int second_units = second % 10;

		}

	}

	/**
	 * 鍥剧墖閿熸枻鎷烽敓鏂ゆ嫹褰曢敓锟�
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			playSimpleSound(CLICK_SOUND_KEY);
			IconInfo iconInfo = (IconInfo) parent.getAdapter().getItem(
					position % Utils.GALLERY_VIEWS_COUNT);
			isIconClick = true;
			int key = iconInfo.getIndex() + 1;
			String packageName = null;
			String activityName = null;
			switch (key) {
			case 1:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				//packageName = "com.worldchip.bbpaw.chinese.zhbx";
				//activityName = "org.cocos2dx.cpp.AppActivity";
				startActivity(new Intent(MainActivity.this, MyAppActivity.class));
				break;
			case 2:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				packageName = "com.worldchip.bbpaw.chinese.ctjy";
				activityName = "org.cocos2dx.cpp.AppActivity";
				break;
			case 3:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				packageName = "com.worldchip.bbpaw.chinese.yyqh";
				activityName = "org.cocos2dx.cpp.AppActivity";
				break;
			case 4:
				if (Utils.isFastDoubleClick()){
					return;
				}
				packageName = "com.worldchip.bbpaw.chinese.jyjy";
				activityName = "org.cocos2dx.cpp.AppActivity";
				break;
			case 5:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				packageName = "com.worldchip.bbpaw.chinese.whny";
				activityName = "org.cocos2dx.cpp.AppActivity";
				break;
			case 6:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				packageName = "com.worldchip.bbpaw.chinese.kxjy";
				activityName = "org.cocos2dx.cpp.AppActivity";
				break;
			}
			if (key >= 0 && mSoundMap != null && mSoundMap.size() > 0) {
				playSimpleSound(key);
			}

			if (packageName != null && activityName != null) {
				Utils.startApp(MainActivity.this, packageName, activityName);
			}
		}
	};

	private void chickenMove() {
		boolean hasChicken = false;
		for (int i = 0; i < mHomeMain.getChildCount(); i++) {
			if (mHomeMain.getChildAt(i) instanceof ChickenView) {
				hasChicken = true;
			}
		}

		if (!hasChicken) {
			addChicken();
		} else {
			if (mChickenViews != null) {
				for (int i = 0; i < mChickenViews.length; i++) {
					mChickenViews[i].start();
				}
			}
		}
	}

	private void stopChickenMove() {
		if (mChickenViews != null) {
			for (int i = 0; i < mChickenViews.length; i++) {
				mChickenViews[i].stop();
			}
		}
	}

	private OnItemSelectedListener mGallerySelectListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (!isIconClick) {
				playSimpleSound(CLICK_SOUND_KEY);
				Log.i("Lee", "-----------------playSimpleSound------+++++++++++>");
				isIconClick = false;
			}
			mGallery.setSelection(position);
			adapter.setSelectedPosition(position);
			adapter.notifyDataSetChanged();
			isIconClick = false;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	private String TAG;

	/**
	 * 
	 * 
	 * @param view
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.china_english_qiechuan:
			isIconClick = true;
			changeLanguage();
			// Intent intent=new Intent(MainActivity.this,
			// PatriarchControlActivity.class);
			// startActivity(intent);
			break;
		case R.id.fish_iv:
			playSimpleSound(CLICK_SOUND_KEY);
			startFinshAnim();
			break;
		case R.id.elephants_erduo:
			if ( Utils.isFastDoubleClick() ) {
					return;
			}
			playSimpleSound(7);
			if (isErduoClickEnable) {
				isErduoClickEnable = false;
				if (mAnimationDrawableElephantsErduo == null) {
					mElephantErDuo.setImageResource(Utils.getResourcesId(
							MyApplication.getAppContext(), "elephant_erduo",
							"anim"));
					mAnimationDrawableElephantsErduo = (AnimationDrawable) mElephantErDuo
							.getDrawable();
				}
				mAnimationDrawableElephantsErduo.start();
				int duration = 0;
				for (int i = 0; i < mAnimationDrawableElephantsErduo
						.getNumberOfFrames(); i++) {
					duration += mAnimationDrawableElephantsErduo.getDuration(i);
				}
				Log.e("lee", "duration == " + duration);
				mHandler.sendEmptyMessageDelayed(START_ACTIVITY, duration);
			}
			break;
		case R.id.pangxie:
			playSimpleSound(CLICK_SOUND_KEY);
			if (mPangXie != null && !PangxieAnimationUtils.isRunningAnimation()) {
				PangxieAnimationUtils.startInRotateAnimation(mPangXie, 0);
			}
			break;
		case R.id.door_rl_view:
			playSimpleSound(CLICK_SOUND_KEY);
			if (view.getId() == R.id.door_rl_view) {
				mHandler.sendEmptyMessage(EDIT_BABYINFORMATION);
			}
			break;
		case R.id.age_switch_tv:
			playSimpleSound(8);
			if (AgeGroupBarAnimationUtils.isRunningAnimation())
				return;
			if (isDisplayAgeGroupBar) {
				AgeGroupBarAnimationUtils.startOutRotateAnimation(mAgeGroupBar,0);
			} else {
				AgeGroupBarAnimationUtils.startInRotateAnimation(mAgeGroupBar,0);
			}
			isDisplayAgeGroupBar = !isDisplayAgeGroupBar;
			break;
		case R.id.ib_age_group_01:
			playSimpleSound(CLICK_SOUND_KEY);
			updateAgeGroupBar(AGE_GROUP_BAR_01);
			mHandler.sendEmptyMessage(UPDATE_BBPAW_AGEDUAN_THREE);
			Log.e(TAG, "group 01..before value="+ DataManager.getAgeValue(MainActivity.this));
			DataManager.setAgeValue(MainActivity.this, 0);
			Log.e(TAG, "group 01..after value=" + DataManager.getAgeValue(MainActivity.this));
			break;
		case R.id.ib_age_group_02:
			playSimpleSound(CLICK_SOUND_KEY);
			updateAgeGroupBar(AGE_GROUP_BAR_02);
			mHandler.sendEmptyMessage(UPDATE_BBPAW_AGEDUAN_FIVE);
			DataManager.setAgeValue(MainActivity.this, 1);
			break;
		case R.id.ib_age_group_03:
			playSimpleSound(CLICK_SOUND_KEY);
			updateAgeGroupBar(AGE_GROUP_BAR_03);
			mHandler.sendEmptyMessage(UPDATE_BBPAW_AGEDUAN_SEVEN);
			DataManager.setAgeValue(MainActivity.this, 2);
			break;
		case R.id.grass_iv:
			if ( Utils.isFastDoubleClick() ) {
				return;
			}
			playSimpleSound(CLICK_SOUND_KEY);
			// Utils.startApp(MainActivity.this,
			// "com.worldchip.bbp.bbpawmanager",
			// "com.worldchip.bbp.bbpawmanager.activity.MainActivity");
			/*if (!grassRunning) {
				grassRunning = true;
				runGrassAnim(view);
			}*/
			runGrassAnim(view);
			break;
		default:
			break;
		}
	}

	private void changeLanguage() {
		mLanguage = Locale.getDefault().getLanguage();
		try {
			Locale locale = null;
			int languageIndex = 0;
			if (!(mLanguage.equals("zh"))) {
				locale = Locale.CHINA;
				languageIndex = Utils.CH_LANGUAGE_INDEX;
			} else {
				locale = Locale.ENGLISH;
				languageIndex = Utils.ENG_LANGUAGE_INDEX;
			}
			Utils.updateLanguage(locale);
			updateLanguageButton(languageIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showUserInfo(boolean isEdit) {
		if (mEditInformation != null) {
			mEditInformation.dismiss();
			mEditInformation = null;
		}
		if (mPopuwidow_relayout == null) {
			mPopuwidow_relayout = (RelativeLayout) LayoutInflater.from(
					MainActivity.this).inflate(R.layout.bb_edit_box_layout, null);
			mPopuwidow_relayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					hideAllDateWheelView();
					return false;
				}
			});
		}
		mEditInformation = new PopupWindow(mContext);
		mEditInformation.setBackgroundDrawable(new ColorDrawable(0));
		mEditInformation.setWidth(LayoutParams.MATCH_PARENT);
		mEditInformation.setHeight(LayoutParams.MATCH_PARENT);
		mEditInformation.setOutsideTouchable(false);
		mEditInformation.setFocusable(true);
		mEditInformation.setContentView(mPopuwidow_relayout);
		mEditInformation.showAtLocation(findViewById(R.id.home_main),
				Gravity.CENTER, 0, -30);
		mPopuWindow_bg = (RelativeLayout) mPopuwidow_relayout
				.findViewById(R.id.bb_bg_relayout);
		// mPopuWindow_bg.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
		// "zz_bbinfo_bg", "drawable"));
		mCancel_edit = (ImageView) mPopuwidow_relayout
				.findViewById(R.id.bb_cancel_btn);
		mFirst_relayout = (RelativeLayout) mPopuwidow_relayout
				.findViewById(R.id.bb_secand_relayout);
		mSecand_edit_relayout = (RelativeLayout) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_secand_relayout);
		mSave = (Button) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_save_btn);
		mSave.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "save_selector", "drawable"));
		mBaby_edit = (ImageView) mPopuwidow_relayout
				.findViewById(R.id.bb_edit_btn);
		mBaby_edit.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "edit_selector", "drawable"));

		if (!isEdit) {
			mFirst_relayout.setVisibility(View.VISIBLE);
			mSecand_edit_relayout.setVisibility(View.GONE);
			mSave.setVisibility(View.GONE);
			mPopuWindow_bg.setBackgroundResource(Utils.getResourcesId(
					MyApplication.getAppContext(), "zz_bbinfo_bg", "drawable"));
			mBaby_edit.setVisibility(View.VISIBLE);
			mBb_nicheng = (TextView) mPopuwidow_relayout.findViewById(R.id.babymingzi);
			mBb_nicheng.setTypeface(mTypeface);
			mBb_shengri = (TextView) mPopuwidow_relayout.findViewById(R.id.babyshengri);
			mBb_shengri.setTypeface(mTypeface);
			mBabyTete = (ImageView) mPopuwidow_relayout.findViewById(R.id.bb_touxiang);
			mBaby_name = (TextView) mPopuwidow_relayout.findViewById(R.id.baby_name);
			mBaby_sex = (TextView) mPopuwidow_relayout.findViewById(R.id.baby_sex);
			mBaby_name.setTypeface(mTypeface);
			mBaby_sex.setTypeface(mTypeface);
			mBaby_fyear = (TextView) mPopuwidow_relayout.findViewById(R.id.baby_brithday_year);
			mBaby_fyear.setTypeface(mTypeface);
			if (LoginState.getInstance().isLogin()) {
				if (mBabyinfo != null) {
					ImageLoader imageLoader = ImageLoader.getInstance(1, Type.LIFO);
					String photoUrl = "";
					photoUrl = mBabyinfo.getString(Utils.USER_SHARD_PHOTO_KEY,"");
					if (photoUrl != null && !TextUtils.isEmpty(photoUrl) && mBabyTete != null) {
						imageLoader.loadImage(photoUrl, mBabyTete, true, false);
					}
					int sexIndex = mBabyinfo.getInt(Utils.USER_SHARD_SEX_KEY, -1);
					if (mBaby_sex != null) {
						if (sexIndex == 1) {
							mBaby_sex.setText(R.string.babyinfo_man);
						} else if (sexIndex == 0) {
							mBaby_sex.setText(R.string.babyinfo_girl);
						} else {
							mBaby_sex.setText("");
						}
					}
					if (mBaby_name != null) {
						mBaby_name.setText(mBabyinfo.getString(
								Utils.USER_SHARD_USERNAME_KEY, ""));
					}
					String birthdayTimestamp = mBabyinfo.getString(
							Utils.USER_SHARD_BIRTHDAY_KEY, "");
					if (birthdayTimestamp != null
							&& !TextUtils.isEmpty(birthdayTimestamp)) {
						String birthday = Utils.timeStamp2Date(
								birthdayTimestamp,
								Utils.USER_BIRTHDAY_TIME_FORMAT);
						if (mBaby_fyear != null) {
							mBaby_fyear.setText(birthday);
						}
					}
				}

			} else {
				mBabyTete.setImageResource(R.drawable.app_default_photo);
				if (mBaby_name != null) {
					mBaby_name.setText(getString(R.string.default_username));
				}
				if (mBaby_fyear != null) {
					mBaby_fyear.setText(getString(R.string.default_birthday));
				}
				int defaultSexIndex = (Integer
						.parseInt(getString(R.string.default_sex)));
				if (mBaby_sex != null) {
					if (defaultSexIndex == 1) {
						mBaby_sex.setText(R.string.babyinfo_man);
					} else if (defaultSexIndex == 0) {
						mBaby_sex.setText(R.string.babyinfo_girl);
					} else {
						mBaby_sex.setText("");
					}
				}
			}
		}
		mCancel_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSimpleSound(CLICK_SOUND_KEY);
				if (mEditInformation.isShowing()) {
					mEditInformation.dismiss();
					mEditInformation = null;
				}
				hideAllDateWheelView();
			}
		});
		mBaby_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				playSimpleSound(CLICK_SOUND_KEY);
				if (LoginState.getInstance().isLogin()) {
					if (mBabyinfo != null) {
						showEditbabyinfo();
					}
				} else {
					if (mEditInformation.isShowing()) {
						mEditInformation.dismiss();
						mEditInformation = null;
					}
					Utils.showToastMessage(MyApplication.getAppContext(),
							getString(R.string.please_login_first));
				}

			}
		});
	}

	private void showEditbabyinfo() {
		mFirst_relayout.setVisibility(View.GONE);
		mSecand_edit_relayout.setVisibility(View.VISIBLE);
		mSave.setVisibility(View.VISIBLE);
		mBaby_edit.setVisibility(View.GONE);
		mPopuWindow_bg.setBackgroundResource(Utils.getResourcesId(
						MyApplication.getAppContext(), "zz_bbinfo_edit_bg", "drawable"));
		mBb_nicheng_edit = (TextView) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_mingzi);
		//String niCheng = (String) mBb_nicheng_edit.getText();
		mBb_nicheng_edit.setTypeface(mTypeface);
		mBb_shengri_edit = (TextView) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_shengri);
		mBb_shengri_edit.setTypeface(mTypeface);
		mBoy_tv = (TextView) mPopuwidow_relayout.findViewById(R.id.baby_boy_tv);
		mBoy_tv.setTypeface(mTypeface);
		mGirl_tv = (TextView) mPopuwidow_relayout.findViewById(R.id.baby_girl_tv);
		mGirl_tv.setTypeface(mTypeface);

		mRaioGroup = (RadioGroup) mPopuwidow_relayout
				.findViewById(R.id.baby_selector_sex_radiogroup);

		mBoy_radiobtn = (RadioButton) mPopuwidow_relayout
				.findViewById(R.id.baby_boy_radiobtn);
		mGirl_radiobtn = (RadioButton) mPopuwidow_relayout
				.findViewById(R.id.baby_girl_radiobtn);
		if (mBabyinfo.getInt("user_sex", 0) == 1) {
			mBoy_radiobtn.setChecked(true);
		} else {
			mGirl_radiobtn.setChecked(true);
		}
		mBaby_year = (TextView) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_year_text);
		mBaby_year.setTypeface(mTypeface);
		// if (!mBabyinfo.getString("babyyear", "").isEmpty()) {
		// mBaby_year.setText(mBabyinfo.getString("babyyear", ""));
		// }
		mBaby_month = (TextView) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_month_text);
		mBaby_month.setTypeface(mTypeface);
		// if (!mBabyinfo.getString("babymonth", "").equals("")) {
		// mBaby_month.setText(mBabyinfo.getString("babymonth", ""));
		// }
		mBaby_ri = (TextView) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_ri_text);
		mBaby_ri.setTypeface(mTypeface);
		// if (!mBabyinfo.getString("babyri", "").equals("")) {
		// mBaby_ri.setText(mBabyinfo.getString("babyri", ""));
		// }
		String birthdayTimestamp = mBabyinfo.getString(
				Utils.USER_SHARD_BIRTHDAY_KEY, "");
		if (birthdayTimestamp != null && !TextUtils.isEmpty(birthdayTimestamp)) {
			String birthday = Utils.timeStamp2Date(birthdayTimestamp,
					Utils.USER_BIRTHDAY_TIME_FORMAT);
			if (mBaby_year != null && mBaby_month != null && mBaby_ri != null) {
				mBaby_year.setText(birthday.substring(0, 4));
				mBaby_month.setText(birthday.substring(5, 7));
				mBaby_ri.setText(birthday.substring(8, 10));
			}
		}
		mYear = (Button) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_year_button);
		mMonth = (Button) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_month_button);
		mRi = (Button) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_ri_button);
		mBabyYearLayout = (LinearLayout) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_year_linearlyout);
		mBabyMonthLayout = (LinearLayout) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_month_linearlyout);
		mBabyRiLayout = (LinearLayout) mPopuwidow_relayout
				.findViewById(R.id.baby_edit_brithday_ri_linearlyout);
		mBabyYearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				playSimpleSound(CLICK_SOUND_KEY);
				showYearWheelView();
			}
		});
		mBabyMonthLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				playSimpleSound(CLICK_SOUND_KEY);
				showMonthWheelView();
			}
		});
		mBabyRiLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				playSimpleSound(CLICK_SOUND_KEY);
				showDayWheelView();
			}
		});
		mBaby_editname = (EditText) mPopuwidow_relayout.findViewById(R.id.baby_edit_name);
		mBaby_editname.setTypeface(mTypeface);
		mBaby_editname.setText(mBabyinfo.getString("user_name", ""));
		mBaby_editname.setSelection(mBaby_editname.getText().length());
		mBabyMoren_edite = (ImageView) mPopuwidow_relayout
				.findViewById(R.id.bb_moren_touxiang);
		String photoUrl=mBabyinfo.getString("user_photo", "");
		ImageLoader imageLoader = ImageLoader.getInstance(1, Type.LIFO);
		if(LoginState.getInstance().isLogin()){
			if(mBabyinfo!=null){
			imageLoader.loadImage(photoUrl, mBabyMoren_edite, true,false);
			}
		}else{
			mBabyMoren_edite.setImageResource(R.drawable.app_default_photo);
		}
		mBabyMoren_edite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSimpleSound(CLICK_SOUND_KEY);
				startActivityForResult(new Intent(MainActivity.this,
						ImageNameActivity.class), 500);
			}
		});

		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playSimpleSound(CLICK_SOUND_KEY);
				mSave.setClickable(false);
				mHandler.sendEmptyMessageDelayed(ANBLE_CLICK_NUMBER, 4000);
				hideAllDateWheelView();
				mEditPhotoPath = HttpCommon.getPathImage(getPackageName())
						+ HttpCommon.ORIGINAL_PHOTO_TEMP_NAME;
				uploadUserPhoto();
			}
		});
		mRaioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup parent, int id) {
				switch (id) {
				case R.id.baby_boy_radiobtn:
					playSimpleSound(CLICK_SOUND_KEY);
					mBabyinfo.edit().putInt(Utils.USER_SHARD_SEX_KEY, 1).commit();
					break;
				case R.id.baby_girl_radiobtn:
					playSimpleSound(CLICK_SOUND_KEY);
					mBabyinfo.edit().putInt(Utils.USER_SHARD_SEX_KEY, 0).commit();
					break;
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("lee", "MainActivity onActivityResult resultCode == "
				+ resultCode + " data == " + data);
		// super.onActivityResult(requestCode, resultCode, data);
		try {
		switch (resultCode) {
		case 0:
			Bitmap bitmap = HttpCommon.getImageBitmap(getPackageName());
			Log.e("lee", "MainActivity onActivityResult bitmap == "+bitmap);
			if (bitmap != null) {
				if (mBabyMoren_edite != null) {
					mBabyMoren_edite.setImageBitmap(bitmap);
				}
			}
			break;
		case Utils.LOAD_SYSTEM_IMAGE_REQUEST_CODE:
			if (mBabyMoren_edite != null) {
				mBabyMoren_edite.setImageBitmap(HttpCommon
						.getImageBitmap(getPackageName()));
			}
			break;
		case Utils.LOAD_CUSTOM_IMAGE_REQUEST_CODE:
			if (mBabyMoren_edite != null) {
				mBabyMoren_edite.setImageBitmap(HttpCommon
						.getImageBitmap(getPackageName()));
			}
			break;
		/*default:
			if (requestCode == 100) {
				if (data == null)
					return;
				Uri uri = data.getData();
				Intent intent = new Intent();
				intent.setAction("com.android.camera.action.CROP");
				intent.setDataAndType(uri, "image/*");
				intent.putExtra("crop", "true");
				// aspectX aspectY 閿熻鍖℃嫹鍙╄皨閿熸枻鎷烽敓锟�
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				// outputX outputY 閿熻瑁佺》鎷峰浘鐗囬敓鏂ゆ嫹閿燂拷
				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("return-data", true);
				MainActivity.this.startActivityForResult(intent, 200);
			} else if (requestCode == 200) // 閿熸枻鎷烽敓鏂ゆ嫹鍥剧墖
			{
				if (data == null)
					return;
				// 閿熺煫纰夋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
				Bitmap bmap = data.getParcelableExtra("data");
				if (mBabyMoren_edite != null) {
					mBabyMoren_edite.setImageBitmap(bmap);
				}
				// mNameAndImageBtn.setImageBitmap(bmap);
				HttpCommon.SavaImage(bmap, getPackageName());
			} else if (requestCode == 500) // 閿熸枻鎷烽敓鏂ゆ嫹鍥剧墖
			{
				if (data == null)
					return;
				// 閿熺煫纰夋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
				Bitmap bmap = data.getParcelableExtra("data");
				if (bmap == null) {
					return;
				}
				if (mBabyMoren_edite != null) {
					mBabyMoren_edite.setImageBitmap(bmap);
				}
				// mNameAndImageBtn.setImageBitmap(bmap);
				HttpCommon.SavaImage(bmap, getPackageName());
			}
			break;*/
		}
			mUserPhotoChange = true;
		}catch (Exception e) {
			e.printStackTrace();
			Utils.showToastMessage(MyApplication.getAppContext(),
					getString(R.string.load_pic_error));
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		mHandler.removeMessages(INIT_DATA);
		mHandler.removeMessages(RESUME_DATA);
		mHandler.removeMessages(FESTIVAL_BLESS_PUSH);
		
		//加延迟解决卡的问题
		mHandler.sendEmptyMessageDelayed(FESTIVAL_BLESS_PUSH, 1 * 1000);
		mHandler.sendEmptyMessageDelayed(INIT_DATA, 1 * 1000);
		mHandler.sendEmptyMessageDelayed(RESUME_DATA, 5 * 1000);
		
		//打开ECT关闭Android界面中的声音
		Intent intent = new Intent("com.android.music.musicservicecommand");
		intent.putExtra("command", "stop");
		mContext.sendBroadcast(intent);
		
		//刷新小屋中用户信息
		refreshUserView();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopBearAnim();
		stopChickenMove();
		try {
			mHandler.removeMessages(INIT_DATA);
			mHandler.removeMessages(RESUME_DATA);

			if (mPushMessageReceiver != null) {
				unregisterReceiver(mPushMessageReceiver);
			}
			if (wifiIntentReceiver != null) {
				unregisterReceiver(wifiIntentReceiver);
			}
			if (mIntentReceiver != null) {
				unregisterReceiver(mIntentReceiver);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	protected void onStop() {
		/*if (mSoundMap != null) {
			mSoundMap.clear();
		}*/
		/*if (soundPool != null) {
			soundPool.release();
		}*/
		super.onStop();
		
	}

	private class YearAdapter extends com.worldchip.bbp.ect.gwwheel.AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		private YearAdapter(Context context) {
			super(context, R.layout.country_layout, NO_RESOURCE);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView tv = (TextView) view.findViewById(R.id.flag);
			tv.setText(Utils.WHEEL_YEAR_ARR[index]);
			if (mYearWheelView != null) {
				int position = mYearWheelView.getCurrentItem();
				if (index == position) {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_SELECTED);
				} else {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_NORMAL);
				}
			}
			return view;
		}

		@Override
		public int getItemsCount() {
			return Utils.WHEEL_YEAR_ARR.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			int postion = index;
			if (index < 0) {
				postion = Utils.WHEEL_YEAR_ARR.length + postion;
			}
			return Utils.WHEEL_YEAR_ARR[postion];
		}
	}

	private class MonthAdapter extends
			com.worldchip.bbp.ect.gwwheel.AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		private MonthAdapter(Context context) {
			super(context, R.layout.country_layout, NO_RESOURCE);

		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView tv = (TextView) view.findViewById(R.id.flag);
			tv.setText(Utils.WHEEL_MONTH_ARR[index]);
			if (mMonthWheelView != null) {
				int position = mMonthWheelView.getCurrentItem();
				if (index == position) {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_SELECTED);
				} else {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_NORMAL);
				}
			}
			return view;
		}

		@Override
		public int getItemsCount() {
			return Utils.WHEEL_MONTH_ARR.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			int postion = index;
			if (index < 0) {
				postion = Utils.WHEEL_MONTH_ARR.length + postion;
			}
			return Utils.WHEEL_MONTH_ARR[postion];
		}
	}

	private class RiAdapter extends
			com.worldchip.bbp.ect.gwwheel.AbstractWheelTextAdapter {

		/**
		 * Constructor
		 */
		private RiAdapter(Context context) {
			super(context, R.layout.country_layout, NO_RESOURCE);

		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView tv = (TextView) view.findViewById(R.id.flag);
			tv.setText(Utils.WHEEL_DAY_ARR[index]);
			if (mDayWheelView != null) {
				int position = mDayWheelView.getCurrentItem();
				if (index == position) {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_SELECTED);
				} else {
					tv.setTextColor(Utils.WHEELVIEW_TEXT_COLOR_NORMAL);
				}
			}
			return view;
		}

		@Override
		public int getItemsCount() {
			return Utils.WHEEL_DAY_ARR.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			int postion = index;
			if (index < 0) {
				postion = Utils.WHEEL_DAY_ARR.length + postion;
			}
			return Utils.WHEEL_DAY_ARR[postion];
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (soundPool != null) {
			soundPool.release();
		}
		if (mBearDrawableAnim != null) {
			mBearDrawableAnim.stopAnimation();
		}
		try {
			if (mLoginReceiver != null) {
				unregisterReceiver(mLoginReceiver);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// if (operatingAnim != null && mHomeSun != null
		// && operatingAnim.hasStarted()) {
		// mHomeSun.clearAnimation();
		// mHomeSun.startAnimation(operatingAnim);
		// }
	}

	/**
	 * 閿熸枻鎷烽敓鍙槄鎷烽敓锟�
	 */
	private void addChicken() {
		mChickenViews = new ChickenView[3];
		for (int i = 0; i < mChickenViews.length; i++) {
			final ChickenView chickenView = new ChickenView(MainActivity.this);
			if (i == 0) {
				Log.e(TAG, "width"+(int) mResources.getDimension(R.dimen.chicken_one_width));
				chickenView.setPosition((int) mResources.getDimension(R.dimen.chicken_one_width),
						(int) mResources.getDimension(R.dimen.chicken_one_height),
						(int) mResources.getDimension(R.dimen.chicken_one_speed), 
						(int) mResources.getDimension(R.dimen.chicken_one_fangxiang));
			} else if (i == 1) {
				chickenView.setPosition((int) mResources.getDimension(R.dimen.chicken_two_width),
						(int) mResources.getDimension(R.dimen.chicken_two_height),
						(int) mResources.getDimension(R.dimen.chicken_two_speed), 
						(int) mResources.getDimension(R.dimen.chicken_two_fangxiang));
			} else if (i == 2) {
				chickenView.setPosition((int) mResources.getDimension(R.dimen.chicken_three_width),
						(int) mResources.getDimension(R.dimen.chicken_three_height),
						(int) mResources.getDimension(R.dimen.chicken_three_speed), 
						(int) mResources.getDimension(R.dimen.chicken_three_fangxiang));
			}
			mChickenViews[i] = chickenView;
			chickenView.start();
			mHomeMain.addView(chickenView);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		HttpCommon.hideSystemUI(MainActivity.this, true);
	}

	private void updateAgeGroupBar(int index) {
		for (int i = 0; i < AGE_GROUP_BAR_COUNT; i++) {
			if (index == i) {
				mAgeGroupBarView[i].setBackgroundDrawable(null);
			} else {
				mAgeGroupBarView[i].setBackgroundResource(AGR_GROUP_BAR_RES[i]);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mBearDrawableAnim != null) {
				mBearDrawableAnim.onTouch(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mBearDrawableAnim != null) {
				mBearDrawableAnim.onTouch(false);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private void runGrassAnim(View view) {
		// TODO Auto-generated method stub

		BBPawAnimationUtils.runGrassAnimation(view, new MyAnimationListener() {
			@Override
			public void onAnimationEnd() {
				// TODO Auto-generated method stub
				Log.e("lee", "runGrassAnim onAnimationEnd -------");
				//grassRunning = false;
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
				overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			}
		});
		if (mWavesIV != null) {
			BBPawAnimationUtils.runWavesDrawableAnim(mWavesIV);
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * WIFI閿熸枻鎷锋伅閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
	 */
	private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = Math.abs(((WifiManager) getSystemService(WIFI_SERVICE))
					.getConnectionInfo().getRssi());
			mImageViewWifi.setImageResource(R.drawable.wifi_sel);
			mImageViewWifi.setImageLevel(level);
		}
	};

	// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷锋伅閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷� 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra("status", 0);
			int level = intent.getIntExtra("level", 0);

			switch (status) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				mImageViewPower.setImageResource(R.drawable.stat_sys_battery);
				mImageViewPower.getDrawable().setLevel(level);
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING: // 閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
				mImageViewPower.setImageResource(R.anim.stata_chongdian);
				AnimationDrawable animatinDrawbale = (AnimationDrawable) mImageViewPower.getDrawable();
				animatinDrawbale.start();
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING: // 閿熻剼纰夋嫹閿熸枻鎷�
				mImageViewPower.setImageResource(R.drawable.stat_sys_battery);
				mImageViewPower.getDrawable().setLevel(level);
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING: // 鏈敓鏂ゆ嫹閿燂拷
				mImageViewPower.setImageResource(R.drawable.stat_sys_battery);
				mImageViewPower.getDrawable().setLevel(level);
				break;
			case BatteryManager.BATTERY_STATUS_FULL: // 閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
				mImageViewPower.setImageResource(R.drawable.state_fulldianliang);
				mImageViewPower.getDrawable().setLevel(level);
				break;
			}
		}
	};

	@Override
	public void onGalleryTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mBearDrawableAnim != null) {
				mBearDrawableAnim.onTouch(true);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mBearDrawableAnim != null) {
				mBearDrawableAnim.onTouch(false);
			}
			break;
		}
	}

	private class MainPushMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(RECEIVE_PUSH_MESSAGE_ACTION)) {
				String messageType = intent.getStringExtra(RECEIVE_PUSH_MESSAGE_TYPE_KEY);
				Log.e(TAG, "runyigechinese");
				if (messageType.equals(INFORMATION_MAIN_TYPE)
						|| messageType.equals(NOTIFY_MSG_MAIN_TYPE)) {
					mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
				}
			}
		}
	}

	private class LoginReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Utils.LOGIN_SECCUSS_ACTION)
					|| action.equals(Utils.LOGIN_OUT_ACTICON)) {
				updateUserShard();
			}
		}
	}

	private void updateUserShard() {
		LoginState loginState = LoginState.getInstance();
		if (mBabyinfo != null) {
			mBabyinfo
					.edit()
					.putString(Utils.USER_SHARD_USERNAME_KEY,
							loginState.getUserName()).commit();
			mBabyinfo
					.edit()
					.putString(Utils.USER_SHARD_BIRTHDAY_KEY,
							loginState.getBirthday()).commit();
			mBabyinfo.edit()
					.putInt(Utils.USER_SHARD_SEX_KEY, loginState.getSex())
					.commit();
			mBabyinfo
					.edit()
					.putString(Utils.USER_SHARD_PHOTO_KEY,
							loginState.getPhotoUrl()).commit();
			mBabyinfo
					.edit()
					.putString(Utils.USER_SHARD_AGEINDEX_KEY,
							String.valueOf(loginState.getAgeIndex())).commit();
			Log.d("Wing", "updateUserShard: " + loginState.getUserName() + "++" + loginState.getPhotoUrl());
		}
		Log.d("Wing", "updateUserShard_mBabyinfo: ");
		mHandler.sendEmptyMessage(REFRESH_USER_VIEW);
	}

	private void refreshUserView() {
		if (mBabyinfo != null && LoginState.getInstance().isLogin()) {
			String photoUrl = mBabyinfo.getString(Utils.USER_SHARD_PHOTO_KEY, "");
			ImageLoader imageLoader = ImageLoader.getInstance(1, Type.LIFO);
			if (photoUrl != null && mPhotoView != null) {
				imageLoader.loadImage(photoUrl, mPhotoView, true, false);
			}
			if (mNameTV != null) {
				mNameTV.setText(mBabyinfo.getString(Utils.USER_SHARD_USERNAME_KEY, ""));
				editor.putString("nick_name", mBabyinfo.getString(Utils.USER_SHARD_USERNAME_KEY,
						getString(R.string.default_username))).commit();
			}
			String birthdayTimeStamp = mBabyinfo.getString(Utils.USER_SHARD_BIRTHDAY_KEY, "");
			if (!TextUtils.isEmpty(birthdayTimeStamp)) {
				int age = Utils.calculateAge(birthdayTimeStamp);
				String[] ageGroups = getResources().getStringArray(
						R.array.age_group_arry);
				if (age == 0) {
					age = 1;
				}
				if (age > 0) {
					mAgeTV.setText(getString(R.string.age_str_format, age));
				}
				// 判断age修改mAgeSwitchTV
				if (age <= 4) {
					mAgeSwitchTV.setText(ageGroups[0]);
					updateAgeGroupBar(AGE_GROUP_BAR_01);
					DataManager.setAgeValue(MainActivity.this, 0);
				}
				if (age > 4 && age <= 5) {
					mAgeSwitchTV.setText(ageGroups[1]);
					updateAgeGroupBar(AGE_GROUP_BAR_02);
					DataManager.setAgeValue(MainActivity.this, 1);
				}
				if (age > 5) {
					mAgeSwitchTV.setText(ageGroups[2]);
					updateAgeGroupBar(AGE_GROUP_BAR_03);
					DataManager.setAgeValue(MainActivity.this, 2);
				}
			}
		} else {
			mPhotoView.setImageResource(R.drawable.app_default_photo);
			if (mNameTV != null) {
				mNameTV.setText(getString(R.string.default_username));
			}
			if (mAgeTV != null) {
				mAgeTV.setText(getString(R.string.age_str_format, getString(R.string.default_age)));
			}
			String[] ageGroups = getResources().getStringArray(
					R.array.age_group_arry);
			mAgeSwitchTV.setText(ageGroups[DEFAULT_AGE_GROUP_SELECT]);
			updateAgeGroupBar(DEFAULT_AGE_GROUP_SELECT);
		}
	}

	private void updateLanguageButton(int index) {
		if (Utils.getLanguageInfo(mContext) == Utils.CH_LANGUAGE_INDEX) {
			mCh_UK.setBackgroundResource(R.drawable.china_selector);
		} else {
			mCh_UK.setBackgroundResource(R.drawable.english_selector);
		}
	}

	private void hideAllDateWheelView() {
		if (mYearWheelView != null) {
			mYearWheelView.setVisibility(View.GONE);
			mYearWheelViewShowing = false;
		}
		if (mMonthWheelView != null) {
			mMonthWheelView.setVisibility(View.GONE);
			mMonthWheelViewShowing = false;
		}
		if (mDayWheelView != null) {
			mDayWheelView.setVisibility(View.GONE);
			mDayWheelViewShowing = false;
		}
	}

	private void showYearWheelView() {
		if (mYearWheelView == null) {
			mYearWheelView = (WheelView) mPopuwidow_relayout.findViewById(R.id.wheelview_year);
			mYearWheelView.setVisibleItems(3);
			mYearWheelView.setViewAdapter(new YearAdapter(MainActivity.this));
			mYearWheelView.setCyclic(true);
			mYearWheelView.setCurrentItem(1);
			mYearWheelView.addScrollingListener(new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					if (wheel != null) {
						int index = wheel.getCurrentItem();
						mBaby_year.setText(Utils.WHEEL_YEAR_ARR[index]);
					}
				}
			});
		}
		if (!mYearWheelViewShowing) {
			mYearWheelView.setVisibility(View.VISIBLE);
		} else {
			mYearWheelView.setVisibility(View.GONE);
		}
		mYearWheelViewShowing = !mYearWheelViewShowing;
	}

	private void showMonthWheelView() {
		if (mMonthWheelView == null) {
			mMonthWheelView = (WheelView) mPopuwidow_relayout.findViewById(R.id.wheelview_month);
			mMonthWheelView.setVisibleItems(3);
			mMonthWheelView.setViewAdapter(new MonthAdapter(MainActivity.this));
			mMonthWheelView.setCyclic(true);
			mMonthWheelView.setCurrentItem(1);
			mMonthWheelView.addScrollingListener(new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					if (wheel != null) {
						int index = wheel.getCurrentItem();
						mBaby_month.setText(Utils.WHEEL_MONTH_ARR[index]);
					}
				}
			});
		}
		if (!mMonthWheelViewShowing) {
			mMonthWheelView.setVisibility(View.VISIBLE);
		} else {
			mMonthWheelView.setVisibility(View.GONE);
		}
		mMonthWheelViewShowing = !mMonthWheelViewShowing;
	}

	private void showDayWheelView() {
		if (mDayWheelView == null) {
			mDayWheelView = (WheelView) mPopuwidow_relayout.findViewById(R.id.wheelview_ri);
			mDayWheelView.setVisibleItems(3);
			mDayWheelView.setViewAdapter(new RiAdapter(MainActivity.this));
			mDayWheelView.setCyclic(true);
			mDayWheelView.setCurrentItem(1);
			mDayWheelView.addScrollingListener(new OnWheelScrollListener() {
				@Override
				public void onScrollingStarted(WheelView wheel) {
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					// TODO Auto-generated method stub
					if (wheel != null) {
						int index = wheel.getCurrentItem();
						mBaby_ri.setText(Utils.WHEEL_DAY_ARR[index]);
					}
				}
			});
		}

		if (!mDayWheelViewShowing) {
			mDayWheelView.setVisibility(View.VISIBLE);
		} else {
			mDayWheelView.setVisibility(View.GONE);
		}
		mDayWheelViewShowing = !mDayWheelViewShowing;
	}

	private void playSimpleSound(int key) {
		try {
			Integer soundIndex = mSoundMap.get(key);
			soundPool.play(soundIndex, 1, 1, 0, 0, 1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onStart(String httpTag) {

	}

	@Override
	public void onSuccess(String result, String httpTag) {
		Log.e("lee", "onSuccess response  == " + result.toString()
				+ " ----------->>>" + httpTag);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = HTTP_UPDETE_CODE;
		msg.arg1 = Configure.SUCCESS;
		bundle.putString("httpTag", httpTag);
		bundle.putString("result", result);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onFailure(Exception e, String httpTag) {
		Log.e("lee", "onFailure response  == " + e.toString()
				+ " ----------->>>" + httpTag);
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = HTTP_UPDETE_CODE;
		msg.arg1 = Configure.FAILURE;
		bundle.putString("httpTag", httpTag);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onFinish(int result, String httpTag) {
		stopProgressDialog();
	}

	private void handResultMessge(int resultCode, String httpTag, Object result) {
		String messageText = "";
		Time time = new Time();
		time.setToNow();
		Log.e("lee", "handResultMessge resultCode == " + resultCode);
		if (resultCode == HttpUtils.HTTP_RESULT_CODE_SUCCESS) {
			if (httpTag.equals(Configure.HTTP_TAG_UPDATE_BABYINFO)) {
				messageText = getString(R.string.update_password_suceecss);
				if (result != null) {
					UpdateBabyInfo info = (UpdateBabyInfo) result;
					if (info != null) {
						mFestival.edit().putInt("mbirthdayyear", time.year);
						Log.d("TAG_UPDATE_BABYINFO", "mbirthdayyear ---- " + time.year);
						LoginState loginState = LoginState.getInstance();
						loginState.setUserName(info.getUsername());
						loginState.setBirthday(info.getBrithday());
						loginState.setPhotoUrl(info.getImagePath());
						loginState.setSex(info.getSex());
						Log.e("lee", "username" + info.getUsername()
								+ "   brithday" + info.getBrithday() + " sex"
								+ info.getSex());
						mBabyinfo
								.edit()
								.putString(Utils.USER_SHARD_PHOTO_KEY,
										info.getImagePath()).commit();
						mBabyinfo
								.edit()
								.putInt(Utils.USER_SHARD_SEX_KEY, info.getSex())
								.commit();
						mBabyinfo
								.edit()
								.putString(Utils.USER_SHARD_USERNAME_KEY,
										info.getUsername()).commit();
						mBabyinfo
								.edit()
								.putString(Utils.USER_SHARD_BIRTHDAY_KEY,
										info.getBrithday()).commit();
						refreshUserView();
					}
				}
				if (mEditInformation.isShowing()) {
					mEditInformation.dismiss();
					mEditInformation = null;
				}
			}else if (httpTag.equals(Configure.HTTP_TAG_LOGIN)) {
				MainActivity.this.sendBroadcast(new Intent(Utils.LOGIN_SECCUSS_ACTION));
				if (result != null) {
					LoginState loginState = (LoginState) result;
					loginState.setLogin(true);
					loginState.setAccount(mAccount.getString("account", ""));
				}
			}
		} else {
			HttpUtils.handleRequestExcption(resultCode);
		}
		if (!TextUtils.isEmpty(messageText)) {
			Utils.showToastMessage(MyApplication.getAppContext(), messageText);
		}
	}

	private void uploadUserPhoto() {
		if (!mUserPhotoChange) {
			Message msg = new Message();
			msg.what = PHOTO_UPLOAD_COMPLETE;
			msg.obj = null;
			mHandler.sendMessage(msg);
			return;
		}
		mHandler.sendEmptyMessage(START_PHOTO_UPLOAD);
		mUserPhotoChange = false;
		if (!HttpUtils.isNetworkConnected(MyApplication.getAppContext())) {
			Message msg = new Message();
			msg.arg1 = USER_EDIT_RESULT_NOTWORK;
			mHandler.sendMessage(msg);
			return;
		}
		Log.e("lee", "upload photoPath == " + mEditPhotoPath);
		PictureUploadUtils.doPost(mEditPhotoPath, new HttpResponseCallBack() {
			@Override
			public void onSuccess(String result, String httpTag) {
				// TODO Auto-generated method stub
				Log.e("lee", "upload -- onSuccess == " + result);
				Message msg = new Message();
				msg.what = PHOTO_UPLOAD_COMPLETE;
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
				// TODO Auto-generated method stub
				if (e != null) {
					Log.e(TAG, "upload -- onFailure ==>> " + e.toString());
				}
				Message msg = new Message();
				msg.what = PHOTO_UPLOAD_COMPLETE;
				mHandler.sendMessage(msg);
			}
		}, "upload");
	}

	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			if (mGlobalProgressDialog.isShowing()) {
				mGlobalProgressDialog.dismiss();
			}
			mGlobalProgressDialog = null;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Log.e(TAG, "-------- onKeyBack------>> ");
			return true;
		}
			return super.onKeyDown(keyCode, event);
	}

	private void commitUserChange(String userPhotoUrl) {
		String cpuSerial = Utils.getCpuSerial(getApplicationContext());
		String account = LoginState.getInstance().getAccount();
		String name = mBaby_editname.getText().toString().trim();
		String brithday_year = mBaby_year.getText().toString();
		mBabyinfo.edit().putString("babyyear", brithday_year).commit();
		String brithday_month = mBaby_month.getText().toString();
		mBabyinfo.edit().putString("babymonth", brithday_month).commit();
		String brithday_ri = mBaby_ri.getText().toString();
		mBabyinfo.edit().putString("babyri", brithday_ri).commit();
		String brithday_date = brithday_year + "-" + brithday_month + "-" + brithday_ri;
		LoginState logins = LoginState.getInstance();
		String clientKey = logins.getClientKey();

		String httpurl = Configure.UPDATE_BABYINFO 
				.replace("ACCOUNT", account)
				.replace("ADMIN", name)
				.replace("BRITHDAY", brithday_date)
				.replace("IMAGEPATH", userPhotoUrl)
				.replace("SEX", String.valueOf(mBabyinfo.getInt(Utils.USER_SHARD_SEX_KEY, 0)))
				.replace("DEVICE_ID", cpuSerial)
				.replace("CLIENT_KEY", clientKey);
		Log.e("lee", "commitUserChange httpurl == " + httpurl);
		if (httpurl != null && !TextUtils.isEmpty(httpurl)) {
			HttpUtils.doPost(httpurl.trim(), MainActivity.this,
					Configure.HTTP_TAG_UPDATE_BABYINFO);
		}
	}
	
	private void pushFestivalBless() {
		int birthdayMonth = 0;
		int birthdayDay = 0;
		if (LoginState.getInstance().isLogin()) {
			if (mBabyinfo != null) {
				String birthdayTimestamp = mBabyinfo.getString(
						"user_birthday", "");
				if (birthdayTimestamp != null && !TextUtils.isEmpty(birthdayTimestamp)) {
					Date birthday = new Date(Long.valueOf(birthdayTimestamp));
					birthdayMonth = birthday.getMonth() + 1;
					birthdayDay = birthday.getDate();
				}
			}
		}
		LunarCalendar lunarCalendar = new LunarCalendar();
		int lunarYear = lunarCalendar.getYear();
		int lunarMonth = lunarCalendar.getMonth();
		int lunarDay = lunarCalendar.getDay();
		
		Time time = new Time();    
        time.setToNow();   
        int year = time.year;   
        int month = time.month + 1;   
        int day = time.monthDay;   
        int minute = time.minute;   
        int hour = time.hour;   
        int sec = time.second; 
        Intent mIntent = new Intent(MainActivity.this, FestivalActivity.class);
        first = mFestival.getBoolean("first", true);
        if (first) {
        	  	mFestival.edit().putInt("mbirthdayyear", year).commit();
		        mFestival.edit().putInt("myuandanyear", year).commit();
		        mFestival.edit().putInt("mchildrendayyear", year).commit();
		        mFestival.edit().putInt("mlunar_new_yearyear", lunarYear).commit();
		        mFestival.edit().putInt("mduanwuyear", lunarYear).commit();
		        mFestival.edit().putBoolean("first", false).commit();
        }
		if (month == birthdayMonth && day == birthdayDay) {
			int y = mFestival.getInt("mbirthdayyear", 0);
			if (y == year) {
				mFestival.edit().putInt("mbirthdayyear", year + 1).commit();
				mIntent.putExtra("flag", 0);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			}
		}
		if (month == 1 && day == 1 ) {
			int y = mFestival.getInt("myuandanyear", 0);
			if (y == year) {
				mFestival.edit().putInt("myuandanyear", year + 1).commit();
				mIntent.putExtra("flag", 1);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			}
				
		}
		if (month == 6 && day == 1 ){
			int y = mFestival.getInt("mchildrendayyear", 0);
			if (y == year) {
				mFestival.edit().putInt("mchildrendayyear", year + 1).commit();
				mIntent.putExtra("flag", 2);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			}
		}
		if (lunarMonth == 1 && lunarDay == 1 ) {
			int y = mFestival.getInt("mlunar_new_yearyear", 0);
			if (y == lunarYear) {
				mFestival.edit().putInt("mlunar_new_yearyear", lunarYear + 1).commit();
				mIntent.putExtra("flag", 3);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			}
		}
		if (lunarMonth == 5 && lunarDay == 5) {
			int y = mFestival.getInt("mduanwuyear", 0);
			if (y == lunarYear) {
				mFestival.edit().putInt("mduanwuyear", lunarYear + 1).commit();
				mIntent.putExtra("flag", 4);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mIntent);
			}
		}
	}	
}