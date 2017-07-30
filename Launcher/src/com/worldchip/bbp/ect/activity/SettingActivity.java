package com.worldchip.bbp.ect.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.DataManager;
import com.worldchip.bbp.ect.entity.FindPasswordInfo;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.gwwheel.WheelView;
import com.worldchip.bbp.ect.image.utils.ImageLoader;
import com.worldchip.bbp.ect.image.utils.ImageLoader.Type;
import com.worldchip.bbp.ect.util.Configure;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.HttpResponseCallBack;
import com.worldchip.bbp.ect.util.HttpUtils;
import com.worldchip.bbp.ect.util.JsonUtils;
import com.worldchip.bbp.ect.util.MD5Util;
import com.worldchip.bbp.ect.util.ResourceManager;
import com.worldchip.bbp.ect.util.Utils;
import com.worldchip.bbp.ect.view.GlobalProgressDialog;
import com.worldchip.bbp.ect.view.RoundImageView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 锟斤拷锟斤拷
 * 
 * @author WGQ 2014-06-11
 */
public class SettingActivity extends Activity implements OnClickListener,
		OnGestureListener, HttpResponseCallBack {

	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;

	// 图片锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	private RelativeLayout mImageNameSetting_popupWindow;
	private PopupWindow mImageNamePopupWindow;
	private RoundImageView mNameAndImageBtn;
	private ImageView mImageNameClose, mImageNameBig, mIamgeNameSelect;
	private EditText mNameValue, mMaleValue, mAgeValue, mBirthValue;
	private ImageButton mSettingNameImageSave;

	private PopupWindow mEditInformation;
	private LinearLayout mBabyYearLayout, mBabyMonthLayout, mBabyRiLayout;
	private TextView mBb_nicheng, mBb_shengri, mBb_nicheng_edit,
			mBb_shengri_edit, mBb_brithday_year_tv, mBb_brithday_month_tv,
			mBb_brithday_ri_tv;
	private TextView mBaby_name, mBaby_sex, mBoy_tv, mGirl_tv, mBaby_year,
			mBaby_month, mBaby_ri, mBaby_fyear, mBaby_fmonth, mBaby_fri;
	private RelativeLayout mSecand_edit_relayout, mFirst_relayout,
			mPopuwidow_relayout, mPopuWindow_bg;
	private ImageView mBabyTete, mBabyTete_edit, mBaby_edit, mCancel_edit,
			mBabyMoren_edite;
	private RadioGroup mRaioGroup;

	private RadioButton mBoy_radiobtn, mGirl_radiobtn;
	private EditText mBaby_editname;
	private Button mSave, mYear, mMonth, mRi;
	WheelView country;
	private SharedPreferences mBabyinfo;
	private HashMap<String, String> mHashMap;
	private HashMap<Integer, Integer> mSoundMap = null;
	// 锟斤拷锟绞憋拷锟斤拷锟斤拷锟�
	private ImageView mTimeLayout;
	private RelativeLayout mTimeSetting_popupWindow;
	private PopupWindow mTimePopupWindow;
	private ImageView mSettinggStandbytimeClose, mSettingStandbytimeReduce,
			mSettingStandbytimencrease;
	private SeekBar mTimeSeekBar;
	private TextView mTimeValue;
	private float result;

	// 系统锟斤拷锟斤拷锟斤拷锟斤拷
	private ImageView mSoundLayout;
	private RelativeLayout mSoundSetting_popupWindow;
	private PopupWindow mSoundPopupWindow;
	private ImageView mSoundSoundColse, mSettingSoundReduce,
			mImageSettingSound, mSettingSoundIncrease;
	private SeekBar mSoundSeekBar;
	private AudioManager audioManager;

	// 锟斤拷幕锟斤拷锟饺碉拷锟斤拷
	private ImageView mBrightnessLayout;
	private RelativeLayout mBrightnessSetting_popupWindow;
	private PopupWindow mBrightnessPopupWindow;
	private ImageView mSettingLightClose, mSettingBrightnessReduce,
			mSettingBrightnessIncrease;
	private SeekBar mBrightnessSeekBar;
	private int mBrightnessVolume;
	// 注锟斤拷锟铰�
	private PopupWindow mLoginPopuWindow;
	private RelativeLayout mRegisterRelativelayout;
	private TextView mRegister, mForgetPassWord, mZhangHao_text, mMiMa_text;
	private ImageView mLogin, mCanle;
	private EditText mZhangHao, mMiMa;

	private PopupWindow mExitLoginPopuWindow;
	private RelativeLayout mExitLoginRelativelayout;
	private TextView mUserName;
	private ImageView mExitLogin, mExitCancle, mChangePSWBtn;
	// 锟睫革拷锟斤拷锟斤拷
	private PopupWindow mXiuGaiPassWordPopuwindow;
	private RelativeLayout mXiuGaiPasswordRelativelayout;
	private TextView mOldPassword, mNewPassword, mConfrimPassword,
			mXiuGaiPassword;
	private EditText mOldPassword_edittext, mNewPassword_edittext,
			mConfrimPassword_edittext;
	private ImageView mXiuGaiPasswordCanle, mXiuGaiPasswordConfrim;
	// 锟斤拷锟斤拷锟斤拷锟�
	private PopupWindow mForgetPasswordPopuwindow;
	private RelativeLayout mForgetPasswordRelativelayout;
	private TextView mFindAccountTV, mOr, mPhoneMessageFind, mInputValidationCode;
	private EditText mValidateCodeET, mFindAccountET;
	private Button mValidateCodeBtn;
	private ImageView mForfetPasswordCancle, mForgetPasswordConfrim;
	// WIFI
	private ImageView mWifi_image;

	// 锟斤拷锟斤拷
	//private LinearLayout mBluetoothLayout;
	//private ImageView mBluetooth_imgage;
	// background sound
	private ImageView mImageBGSound;
	private boolean isOperBGSound = true;
	// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	private LinearLayout mSensorLayout;
	private ImageView mSensorImage;
	private Typeface mTypeface;
	// 锟斤拷锟斤拷
	private ImageView mSettingBack;
	private SoundPool mSoundpool;
	private static final int HTTP_REQ_CODE = 100;
	private static final int MSG_GET_VERIFICATION_CODE = 101;
	private LinearLayout mAccountLL;
	private TextView mAccountTV;
    private VerificationRefreshTask mRefreshTask;
    private int INTERVAL = 60;
    private Timer mTimer;
    private GlobalProgressDialog mGlobalProgressDialog;
	private static final int START_PROGRESS_DIALOG = 1000;
	private static final int STOP_PROGRESS_DIALOG = 1001;
	private SharedPreferences mPref;
	private SharedPreferences.Editor editor;
	public static int MODE =  Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
	public static final String PREFER_NAME = "save_account";
	private IntentFilter mWifiIntentFilter;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_PROGRESS_DIALOG:
				startProgressDialog();
				break;
			case STOP_PROGRESS_DIALOG:
				stopProgressDialog();
				break;
			case MSG_GET_VERIFICATION_CODE:
                if (INTERVAL > 0) {
                    INTERVAL--;
                    mValidateCodeBtn.setBackgroundResource(R.drawable.validate_count_down_text);
                    if (Utils.getLanguageInfo(getApplicationContext()) == Utils.CH_LANGUAGE_INDEX) {
                    	mValidateCodeBtn.setText(INTERVAL+"秒");
            		} else {
            			mValidateCodeBtn.setText(INTERVAL+"S");
            		}
                    if (INTERVAL == 60) {
                    	mValidateCodeBtn.setClickable(false);
                    }
                } else {
                    INTERVAL = 60;
                    mValidateCodeBtn.setBackgroundResource(Utils.getResourcesId(
            				MyApplication.getAppContext(), "validatecode_selector", "drawable"));
                    mValidateCodeBtn.setText("");
                    mValidateCodeBtn.setClickable(true);
                    stopVerficationRefreshTimer();
                }
                break;
                
			case HTTP_REQ_CODE:
				if (msg.arg1 == Configure.SUCCESS) {
					Bundle data = msg.getData();
					if (data != null) {
						String httpTag = data.getString("httpTag");
						if (httpTag == null)
							return;
						String results = data.getString("result");
						String resultCode = JsonUtils.doParseValueForKey(results, "resultCode");
						if (resultCode == null || TextUtils.isEmpty(resultCode)) {
							return;
						}
						if (httpTag.equals(Configure.HTTP_TAG_LOGIN)) {
							LoginState loginInfo = JsonUtils.doLoginJson2Bean(results);
							Log.d("Wing", "------++++----> " + loginInfo.toString());
							handResultMessge(loginInfo.getResultCode(), Configure.HTTP_TAG_LOGIN, loginInfo);
							
						} else if (httpTag.equals(Configure.HTTP_TAG_UPDATE_PASSWORD)) {
							handResultMessge(Integer.parseInt(resultCode), Configure.HTTP_TAG_UPDATE_PASSWORD, null);
						} else if (httpTag.equals(Configure.HTTP_TAG_GET_VERFICATION_CODE)) {
							handResultMessge(Integer.parseInt(resultCode), Configure.HTTP_TAG_GET_VERFICATION_CODE, null);
						}else if (httpTag.equals(Configure.HTTP_TAG_GET_OLD_PSW)) {
							FindPasswordInfo passwordInfo = JsonUtils.doOriginalPSWJson2Bean(results);
							handResultMessge(Integer.parseInt(resultCode), Configure.HTTP_TAG_GET_OLD_PSW, passwordInfo);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting_main);
		
		HttpCommon.hideSystemUI(SettingActivity.this, true);
		mBabyinfo = getSharedPreferences("babyinfo", 0);
		mSoundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap = new HashMap<Integer, Integer>();
		mSoundMap.put(30, mSoundpool.load(this, R.raw.click, 1));
		initView();
		mTypeface = Typeface.createFromAsset(getAssets(),
				"Droidhuakangbaoti.TTF");
		mNameAndImageBtn.setOnClickListener(this);
		mTimeLayout.setOnClickListener(this);
		mBrightnessLayout.setOnClickListener(this);
		mSoundLayout.setOnClickListener(this);
		mSettingBack.setOnClickListener(this);
		mWifi_image.setOnClickListener(this);
		//mBluetooth_imgage.setOnClickListener(this);
		mSensorImage.setOnClickListener(this);
		mImageBGSound.setOnClickListener(this);
		mPref = getSharedPreferences(PREFER_NAME, MODE);
		editor = mPref.edit();
	}

	/**
	 * 锟截硷拷锟斤拷始锟斤拷
	 */
	private void initView() {
		// 图片锟斤拷锟斤拷锟街帮拷钮
		mNameAndImageBtn = (RoundImageView) findViewById(R.id.name_and_image_btn);
		if (LoginState.getInstance().isLogin()){
			String photoUrl = mBabyinfo.getString(Utils.USER_SHARD_PHOTO_KEY, "");
			ImageLoader imageLoader = ImageLoader.getInstance(1, Type.LIFO);
			if (photoUrl != null && mNameAndImageBtn != null) {
				imageLoader.loadImage(photoUrl, mNameAndImageBtn, true,false);
			}
		}
		mAccountLL = (LinearLayout) findViewById(R.id.account_ll);
		mAccountTV = (TextView) findViewById(R.id.account_tv);
		findViewById(R.id.account_manager_TV).setSelected(true);

		mTimeLayout = (ImageView) findViewById(R.id.stand_by_time_ll);
		mImageBGSound = (ImageView) findViewById(R.id.img_bg_sound);
		// 锟斤拷锟斤拷锟斤拷锟节帮拷钮
		mSoundLayout = (ImageView) findViewById(R.id.sound_ll);
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 锟斤拷取锟斤拷前锟斤拷锟斤拷锟斤拷锟斤拷
		int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		if (volume == 0) {
			mSoundLayout.setImageResource(R.drawable.mute);
			if (DataManager.getMainIsPlayEffectEnable(MyApplication.getAppContext())) {
						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), false);
			}
		} else {
			mSoundLayout.setImageResource(R.drawable.sound_on);
			if (!(DataManager.getMainIsPlayEffectEnable(MyApplication.getAppContext()))) {
						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), true);
			}
		}

		// 锟斤拷锟饺碉拷锟节帮拷钮
		mBrightnessLayout = (ImageView) findViewById(R.id.brightness_ll);

		// WIFI
		mWifi_image = (ImageView) findViewById(R.id.wifi_image);
		// 锟斤拷始锟斤拷WIFI
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			mWifi_image.setImageResource(R.drawable.wifi_on);
		} else {
			mWifi_image.setImageResource(R.drawable.wifi_off);
		}
		// 锟斤拷锟斤拷
		//mBluetoothLayout = (LinearLayout) findViewById(R.id.bluetooth_layout);
		//mBluetooth_imgage = (ImageView) findViewById(R.id.bluetooth_imgage);
	
		isOperBGSound = DataManager
				.getMainIsPlayBackgroundMusicEnable(MyApplication
						.getAppContext());
		if (DataManager
				.getMainIsPlayEffectEnable(MyApplication.getAppContext())) {
			if (isOperBGSound) {
				mImageBGSound.setImageResource(R.drawable.btn_music_on_state);
			} else {
				mImageBGSound.setImageResource(R.drawable.btn_music_off_state);
			}
		} else {
			mImageBGSound.setImageResource(R.drawable.btn_music_off_state);
		}
		// 锟斤拷始锟斤拷锟斤拷锟斤拷
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		/*if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isEnabled()) {
				mBluetooth_imgage.setImageResource(R.drawable.bluetooth_on);
			} else {
				mBluetooth_imgage.setImageResource(R.drawable.bluetooth_off);
			}
		} else {
			mBluetoothLayout.setVisibility(View.INVISIBLE);
		} */
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟矫帮拷钮
		mSensorLayout = (LinearLayout) findViewById(R.id.sensor_layout);
		mSensorImage = (ImageView) findViewById(R.id.sensor_image);
		// 锟斤拷始锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		// 锟斤拷锟斤拷系统Sensor锟斤拷锟斤拷锟斤拷
		//SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//Sensor sensor_accelerometer = sensorManager
			//	.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		SharedPreferences configShard = getSharedPreferences("config", 0);
		boolean sensorSwitch = configShard.getBoolean("sensor_switch", false);
		if (sensorSwitch) {
			mSensorImage.setImageResource(R.drawable.accelerometer_off);
		} else {
			mSensorImage.setImageResource(R.drawable.accelerometer_on);
		}

		// 锟斤拷锟斤拷
		mSettingBack = (ImageView) findViewById(R.id.setting_back);
		//updateAccountView();
	}

	private void updateAccountView() {
		LoginState loginState = LoginState.getInstance();
		if (mAccountLL != null && !loginState.isLogin()) {
			mAccountLL.setVisibility(View.GONE);
		} else {
			if (mAccountTV != null) {
				mAccountTV.setText(loginState.getAccount());
				mAccountLL.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 锟斤拷示锟斤拷锟绞憋拷锟斤拷锟斤拷么锟斤拷锟�
	 */
	private void showTimePopupWindow() {
		if (mTimePopupWindow != null) {
			mTimePopupWindow.dismiss();
			mTimePopupWindow = null;
		}
		mTimeSetting_popupWindow = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(
				R.layout.activity_setting_time_item, null);
		// 锟斤拷锟矫斤拷锟斤拷示锟侥达拷锟节的匡拷锟街碉拷锟轿拷锟侥伙拷锟斤拷值锟斤拷50%
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = 615;// 锟斤拷锟�
		int height = 528;// 锟竭讹拷

		mTimePopupWindow = new PopupWindow(SettingActivity.this);
		mTimePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mTimePopupWindow.setWidth(width);
		mTimePopupWindow.setHeight(height);
		mTimePopupWindow.setOutsideTouchable(false);
		mTimePopupWindow.setFocusable(true);
		mTimePopupWindow.setContentView(mTimeSetting_popupWindow);
		mTimePopupWindow.showAtLocation(findViewById(R.id.setting_main),
				Gravity.CENTER, 0, 0);// 锟斤拷示锟斤拷位锟斤拷为:锟斤拷锟斤拷诟锟斤拷丶锟斤拷锟斤拷锟�

		mSettinggStandbytimeClose = (ImageView) mTimeSetting_popupWindow
				.findViewById(R.id.settingg_standbytime_close);
		// 锟斤拷锟斤拷
		mSettingStandbytimeReduce = (ImageView) mTimeSetting_popupWindow
				.findViewById(R.id.setting_standbytime_reduce);
		// 锟接猴拷
		mSettingStandbytimencrease = (ImageView) mTimeSetting_popupWindow
				.findViewById(R.id.setting_standbytime_increase);
		mTimeSeekBar = (SeekBar) mTimeSetting_popupWindow
				.findViewById(R.id.timeSeekBar);
		mTimeValue = (TextView) mTimeSetting_popupWindow
				.findViewById(R.id.timeValue);
		
        //mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),"setting_standbytime_selection_box", "drawable"));
		// 锟斤拷取锟斤拷前系统锟斤拷锟绞憋拷锟�
		try {
			result = Settings.System.getInt(getContentResolver(),
					Settings.System.SCREEN_OFF_TIMEOUT);
			mTimeSeekBar.setMax(7);
			if (result == 2147483000) {
				mTimeSeekBar.setProgress(7);
				mTimeValue.setText("");
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"standbytime_selection_box_never", "drawable"));
			} else if (result == 1800000.0) {
				mTimeSeekBar.setProgress(6);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("30");
			} else if (result == 600000.0) {
				mTimeSeekBar.setProgress(5);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("10");
			} else if (result == 300000.0) {
				mTimeSeekBar.setProgress(4);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("5");
			} else if (result == 120000.0) {
				mTimeSeekBar.setProgress(3);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("2");
			} else if (result == 60000.0) {
				mTimeSeekBar.setProgress(2);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("1");
			} else if (result == 30000.0) {
				mTimeSeekBar.setProgress(1);
				mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
						"setting_standbytime_selection_box", "drawable"));
				mTimeValue.setText("0.5");
			}
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		// 锟斤拷锟斤拷
		mSettingStandbytimeReduce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				int Progress = 0;
				// 锟斤拷取锟斤拷前系统锟斤拷锟绞憋拷锟�
				try {
					float result = Settings.System.getInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT);
					mTimeSeekBar.setMax(7);
					if (result == 2147483000) {
						Progress = 7;
					} else if (result == 1800000.0) {
						Progress = 6;
					} else if (result == 600000.0) {
						Progress = 5;
					} else if (result == 300000.0) {
						Progress = 4;
					} else if (result == 120000.0) {
						Progress = 3;
					} else if (result == 60000.0) {
						Progress = 2;
					} else if (result == 30000.0) {
						Progress = 1;
					}
				} catch (SettingNotFoundException e) {
					e.printStackTrace();
				}
				if (Progress > 0) {
					Progress = Progress - 1;
				}
				mTimeSeekBar.setProgress(Progress); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟斤拷锟斤拷锟�
			}
		});
		// 锟接猴拷
		mSettingStandbytimencrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				int Progress = 0;
				// 锟斤拷取锟斤拷前系统锟斤拷锟绞憋拷锟�
				try {
					float result = Settings.System.getInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT);
					mTimeSeekBar.setMax(7);
					if (result == 2147483000) {
						Progress = 7;
					} else if (result == 1800000.0) {
						Progress = 6;
					} else if (result == 600000.0) {
						Progress = 5;
					} else if (result == 300000.0) {
						Progress = 4;
					} else if (result == 120000.0) {
						Progress = 3;
					} else if (result == 60000.0) {
						Progress = 2;
					} else if (result == 30000.0) {
						Progress = 1;
					}
				} catch (SettingNotFoundException e) {
					e.printStackTrace();
				}
				if (Progress <= 6) {
					Progress = Progress + 1;
				}
				mTimeSeekBar.setProgress(Progress); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟斤拷锟斤拷锟�
			}
		});

		// 锟斤拷锟节达拷锟绞憋拷锟�
		mTimeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean arg2) {
				switch (progress) {
				case 1:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("0.5");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 30000);
					break;
				case 2:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("1");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 60000);
					break;
				case 3:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("2");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 120000);
					break;
				case 4:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("5");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 300000);
					break;
				case 5:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("10");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 600000);
					break;
				case 6:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"setting_standbytime_selection_box", "drawable"));
					mTimeValue.setText("30");
					Settings.System.putInt(getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 1800000);
					break;
				case 7:
					mTimeSeekBar.setProgress(progress);
					mTimeValue.setText("");
					mTimeValue.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
							"standbytime_selection_box_never", "drawable"));
					Settings.System.putInt(getContentResolver(), 
							Settings.System.SCREEN_OFF_TIMEOUT, 2147483000);
					break;
				default:
					mTimeSeekBar.setProgress(1);
					mTimeValue.setText("0.5");
					break;
				}
			}
		});

		// 锟截憋拷
		mTimePopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mTimePopupWindow != null) {
					mTimePopupWindow.dismiss();
					mTimePopupWindow = null;
				}
			}
		});

		// 锟斤拷钮锟截憋拷
		mSettinggStandbytimeClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mTimePopupWindow != null) {
					mTimePopupWindow.dismiss();
					mTimePopupWindow = null;
				}
			}
		});
	}

	class MyPostVideoWatchLog extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	/**
	 * 锟斤拷示锟斤拷锟斤拷锟斤拷锟矫达拷锟斤拷
	 */
	private void showSoundPopupWindow() {
		if (mSoundPopupWindow != null) {
			mSoundPopupWindow.dismiss();
			mSoundPopupWindow = null;
		}
		mSoundSetting_popupWindow = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(
				R.layout.activity_setting_sound_item, null);
		// 锟斤拷锟矫斤拷锟斤拷示锟侥达拷锟节的匡拷锟街碉拷锟轿拷锟侥伙拷锟斤拷值锟斤拷50%
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = 615;// 锟斤拷锟�
		int height = 528;// 锟竭讹拷

		mSoundPopupWindow = new PopupWindow(SettingActivity.this);
		mSoundPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mSoundPopupWindow.setWidth(width);
		mSoundPopupWindow.setHeight(height);
		mSoundPopupWindow.setOutsideTouchable(false);
		mSoundPopupWindow.setFocusable(true);
		mSoundPopupWindow.setContentView(mSoundSetting_popupWindow);
		mSoundPopupWindow.showAtLocation(findViewById(R.id.setting_main),
				Gravity.CENTER, 0, 0);// 锟斤拷示锟斤拷位锟斤拷为:锟斤拷锟斤拷诟锟斤拷丶锟斤拷锟斤拷锟�

		// 锟截憋拷
		mSoundSoundColse = (ImageView) mSoundSetting_popupWindow
				.findViewById(R.id.settingg_sound_close);
		// 锟斤拷锟斤拷锟斤拷锟斤拷
		mSettingSoundReduce = (ImageView) mSoundSetting_popupWindow
				.findViewById(R.id.setting_sound_reduce);
		// 锟斤拷锟斤拷锟接猴拷
		mSettingSoundIncrease = (ImageView) mSoundSetting_popupWindow
				.findViewById(R.id.setting_sound_increase);
		mImageSettingSound = (ImageView) mSoundSetting_popupWindow
				.findViewById(R.id.img_setting_sound);
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟�
		mSoundSeekBar = (SeekBar) mSoundSetting_popupWindow
				.findViewById(R.id.soundSeekBar);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 锟斤拷取锟斤拷前锟斤拷锟斤拷锟斤拷锟斤拷
		int mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (mVolume == 0) {
			mImageSettingSound.setImageResource(R.drawable.sound_mute);
		} else {
			mImageSettingSound.setImageResource(R.drawable.setting_up_sound);
		}
		mSoundSeekBar.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // SEEKBAR锟斤拷锟斤拷为锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
		mSoundSeekBar.setProgress(mVolume); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟斤拷锟斤拷锟�
		registerVolumeChangeBroadcast();
		// 锟斤拷锟斤拷锟斤拷锟斤拷
		mSettingSoundReduce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				int mVolume = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume > 0) {
					mVolume = mVolume - 1;
				}
				mSoundSeekBar.setProgress(mVolume); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟斤拷锟斤拷锟�
			}
		});
		// 锟斤拷锟斤拷锟接猴拷
		mSettingSoundIncrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				int mVolume = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume <= 14) {
					mVolume = mVolume + 1;
				}
				mSoundSeekBar.setProgress(mVolume); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟斤拷锟斤拷锟�
			}
		});
		// 锟斤拷锟斤拷锟斤拷锟斤拷锟�
		mSoundSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean arg2) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0); // 锟较讹拷
				Log.e("img_bg_sound", "arg1=" + progress);
				if (progress == 0) {
					mSoundLayout.setImageResource(R.drawable.mute);
					mImageSettingSound.setImageResource(R.drawable.sound_mute);
					mImageBGSound
							.setImageResource(R.drawable.btn_music_off_state);
					
					if (DataManager.getMainIsPlayEffectEnable(MyApplication.getAppContext())) {
						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), false);
						Log.e("--SettingActivity--",
								DataManager.getMainIsPlayEffectEnable(MyApplication
										.getAppContext()) + "");
					}
				} else {
					mSoundLayout.setImageResource(R.drawable.sound_on);
					mImageSettingSound
							.setImageResource(R.drawable.setting_up_sound);
					if (isOperBGSound) {
						mImageBGSound
								.setImageResource(R.drawable.btn_music_on_state);
					}
					if (DataManager.getMainIsPlayEffectEnable(MyApplication
							.getAppContext())) {
						return;
					} else {

						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), true);
					}
				}

			}
		});

		// 锟截憋拷
		mSoundPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				unRegisterVolumeChangeBroadcast();
				if (mSoundPopupWindow != null) {
					mSoundPopupWindow.dismiss();
					mSoundPopupWindow = null;
				}
			}
		});

		// 锟斤拷钮锟截憋拷
		mSoundSoundColse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mSoundPopupWindow != null) {
					mSoundPopupWindow.dismiss();
					mSoundPopupWindow = null;
				}
			}
		});
	}

	/**
	 * 锟斤拷示锟斤拷锟斤拷锟斤拷锟矫达拷锟斤拷
	 */
	private void showBrightnessPopupWindow() {
		if (mBrightnessPopupWindow != null) {
			mBrightnessPopupWindow.dismiss();
			mBrightnessPopupWindow = null;
		}
		mBrightnessSetting_popupWindow = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(
				R.layout.activity_setting_brightness_item, null);
		// 锟斤拷锟矫斤拷锟斤拷示锟侥达拷锟节的匡拷锟街碉拷锟轿拷锟侥伙拷锟斤拷值锟斤拷50%
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = 615;// 锟斤拷锟�
		int height = 528;// 锟竭讹拷

		mBrightnessPopupWindow = new PopupWindow(SettingActivity.this);
		mBrightnessPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mBrightnessPopupWindow.setWidth(width);
		mBrightnessPopupWindow.setHeight(height);
		mBrightnessPopupWindow.setOutsideTouchable(false);
		mBrightnessPopupWindow.setFocusable(true);
		mBrightnessPopupWindow.setContentView(mBrightnessSetting_popupWindow);
		mBrightnessPopupWindow.showAtLocation(findViewById(R.id.setting_main),
				Gravity.CENTER, 0, 0);// 锟斤拷示锟斤拷位锟斤拷为:锟斤拷锟斤拷诟锟斤拷丶锟斤拷锟斤拷锟�

		// 锟截憋拷
		mSettingLightClose = (ImageView) mBrightnessSetting_popupWindow
				.findViewById(R.id.settingg_light_close);
		// 锟斤拷锟饺硷拷锟斤拷
		mSettingBrightnessReduce = (ImageView) mBrightnessSetting_popupWindow
				.findViewById(R.id.setting_brightness_reduce);
		// 锟斤拷锟饺加猴拷
		mSettingBrightnessIncrease = (ImageView) mBrightnessSetting_popupWindow
				.findViewById(R.id.setting_brightness_increase);
		// 锟斤拷锟饺斤拷锟斤拷锟�
		mBrightnessSeekBar = (SeekBar) mBrightnessSetting_popupWindow
				.findViewById(R.id.brightnessSeekBar);

		// 锟斤拷取系统锟斤拷锟斤拷
		mBrightnessVolume = Settings.System.getInt(
				SettingActivity.this.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, -1);
		mBrightnessSeekBar.setMax(255);
		mBrightnessSeekBar.setProgress(mBrightnessVolume); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟饺斤拷锟�

		// 锟斤拷锟饺硷拷锟斤拷
		mSettingBrightnessReduce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mBrightnessVolume > 5) {
					mBrightnessVolume = mBrightnessVolume - 5;
				}
				if (mBrightnessVolume < 5) {
					mBrightnessVolume = 5;
				}
				mBrightnessSeekBar.setProgress(mBrightnessVolume); // 锟斤拷锟斤拷SEEKBAR为锟斤拷前锟斤拷锟饺斤拷锟�
				// 注锟斤拷锟斤拷锟斤拷锟斤拷锟街�~100锟斤拷锟斤拷锟芥到系统锟斤拷时要转锟斤拷为0~255
				Uri uri = android.provider.Settings.System
						.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
				ContentResolver resolver = SettingActivity.this
						.getContentResolver();
				android.provider.Settings.System.putInt(resolver,
						Settings.System.SCREEN_BRIGHTNESS, mBrightnessVolume);
				resolver.notifyChange(uri, null);
			}
		});

		// 锟斤拷锟饺加猴拷
		mSettingBrightnessIncrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mBrightnessVolume <= 250) {
					mBrightnessVolume = mBrightnessVolume + 5;
				} else {
					mBrightnessVolume = 255;
				}
				mBrightnessSeekBar.setProgress(mBrightnessVolume); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟饺斤拷锟�
				// 注锟斤拷锟斤拷锟斤拷锟斤拷锟街�~100锟斤拷锟斤拷锟芥到系统锟斤拷时要转锟斤拷为0~255
				Uri uri = android.provider.Settings.System
						.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
				ContentResolver resolver = SettingActivity.this
						.getContentResolver();
				android.provider.Settings.System.putInt(resolver,
						Settings.System.SCREEN_BRIGHTNESS, mBrightnessVolume);
				resolver.notifyChange(uri, null);
			}
		});

		// 锟斤拷锟饺斤拷锟斤拷锟�
		mBrightnessSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						Uri uri = android.provider.Settings.System
								.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
						ContentResolver resolver = SettingActivity.this
								.getContentResolver();
						android.provider.Settings.System.putInt(resolver,
								Settings.System.SCREEN_BRIGHTNESS,
								seekBar.getProgress());
						resolver.notifyChange(uri, null);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (progress < 5) {
							progress = 5;
						}
						mBrightnessVolume = progress;
						mBrightnessSeekBar.setProgress(progress); // 锟斤拷锟斤拷seekbar为锟斤拷前锟斤拷锟饺斤拷锟�
						// 注锟斤拷锟斤拷锟斤拷锟斤拷锟街�~100锟斤拷锟斤拷锟芥到系统锟斤拷时要转锟斤拷为0~255
						Uri uri = android.provider.Settings.System
								.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
						ContentResolver resolver = SettingActivity.this
								.getContentResolver();
						android.provider.Settings.System.putInt(resolver,
								Settings.System.SCREEN_BRIGHTNESS, progress);
						resolver.notifyChange(uri, null);
					}
				});

		// 锟截憋拷
		mBrightnessPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mBrightnessPopupWindow != null) {
					mBrightnessPopupWindow.dismiss();
					mBrightnessPopupWindow = null;
				}
			}
		});

		// 锟截闭帮拷钮
		mSettingLightClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mBrightnessPopupWindow != null) {
					mBrightnessPopupWindow.dismiss();
					mBrightnessPopupWindow = null;
				}
			}
		});
	}

	private void showExitLogin() {
		if (mExitLoginPopuWindow != null) {
			mExitLoginPopuWindow.dismiss();
			mExitLoginPopuWindow = null;
		}
		if (mExitLoginRelativelayout == null) {
			mExitLoginRelativelayout = (RelativeLayout) LayoutInflater.from(
					SettingActivity.this).inflate(R.layout.exit_login_dailog,
					null);
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = (int) (dm.widthPixels);
		int height = (int) (dm.heightPixels);

		mExitLoginPopuWindow = new PopupWindow(SettingActivity.this);
		mExitLoginPopuWindow.setBackgroundDrawable(new ColorDrawable(0));
		mExitLoginPopuWindow.setWidth(width);
		mExitLoginPopuWindow.setHeight(height);
		mExitLoginPopuWindow.setOutsideTouchable(false);
		mExitLoginPopuWindow.setFocusable(true);
		mExitLoginPopuWindow.setContentView(mExitLoginRelativelayout);
		mExitLoginPopuWindow.showAtLocation(findViewById(R.id.setting_main),
				Gravity.CENTER, 0, 0);

		TextView loginTitle = (TextView) mExitLoginRelativelayout
				.findViewById(R.id.login_title_tv);
		loginTitle.setTypeface(mTypeface);
		LoginState loginState = LoginState.getInstance();

		mUserName = (TextView) mExitLoginRelativelayout
				.findViewById(R.id.login_account_tv);
		mUserName.setTypeface(mTypeface);
		mUserName.setText(loginState.getAccount());
		mExitCancle = (ImageView) mExitLoginRelativelayout
				.findViewById(R.id.bb_cancels_extit_btn);
		mExitLogin = (ImageView) mExitLoginRelativelayout
				.findViewById(R.id.login_out_btn);
		mChangePSWBtn = (ImageView) mExitLoginRelativelayout
				.findViewById(R.id.change_psw_btn);
		mExitLogin.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(), "exit_selector", "drawable"));
		mChangePSWBtn.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(), "update_password_selector", "drawable"));
		mExitCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mExitLoginPopuWindow != null) {
					mExitLoginPopuWindow.dismiss();
					mExitLoginPopuWindow = null;
				}
			}
		});
		mExitLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				LoginState loginState = LoginState.getInstance();
				String httpUrl = Configure.LOGIN_OUT_URL.replace("ACCOUNT",
						loginState.getUserName()).replace("DEVICE_ID",
						Utils.getCpuSerial(getApplicationContext()));
				HttpUtils.doPost(httpUrl, SettingActivity.this,
						Configure.HTTP_TAG_LOGIN_OUT);
				editor.clear();
				editor.commit();
				loginState.loginOut();
				SettingActivity.this.sendBroadcast(new Intent(
						Utils.LOGIN_OUT_ACTICON));
				if (mExitLoginPopuWindow != null) {
					mExitLoginPopuWindow.dismiss();
					mExitLoginPopuWindow = null;
				}
				updateAccountView();
				showLogin();
			}
		});

		mChangePSWBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mExitLoginPopuWindow != null) {
					mExitLoginPopuWindow.dismiss();
					mExitLoginPopuWindow = null;
				}
				showXiuGaiPassword(null);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 //刷新用户信息
		updateAccountView();
		
		//注册WIFI状态广播
		mWifiIntentFilter = new IntentFilter();
		mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		if (mWifiIntentReceiver != null) {
			registerReceiver(mWifiIntentReceiver,mWifiIntentFilter);
		}
	}
	/*
	*WIFI网络断开时广播，账户注销。
	*/
	private BroadcastReceiver mWifiIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
				LoginState loginState = LoginState.getInstance();
				/*String httpUrl = Configure.LOGIN_OUT_URL.replace("ACCOUNT",
						loginState.getUserName()).replace("DEVICE_ID",
						Utils.getCpuSerial(getApplicationContext()));
				HttpUtils.doPost(httpUrl, SettingActivity.this,
						Configure.HTTP_TAG_LOGIN_OUT);*/
				editor.clear();
				loginState.loginOut();
				//SettingActivity.this.sendBroadcast(new Intent(
						//Utils.LOGIN_OUT_ACTICON));
				updateAccountView();
			}
		}
	};

	private void showLogin() {
		if (mLoginPopuWindow != null) {
			mLoginPopuWindow.dismiss();
			mLoginPopuWindow = null;
		}
		mRegisterRelativelayout = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(R.layout.login_dailog, null);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = (int) (dm.widthPixels);
		int height = (int) (dm.heightPixels);

		mLoginPopuWindow = new PopupWindow(SettingActivity.this);
		mLoginPopuWindow.setBackgroundDrawable(new ColorDrawable(0));
		mLoginPopuWindow.setWidth(width);
		mLoginPopuWindow.setHeight(height);
		mLoginPopuWindow.setOutsideTouchable(false);
		mLoginPopuWindow.setFocusable(true);
		mLoginPopuWindow.setContentView(mRegisterRelativelayout);
		mLoginPopuWindow.showAtLocation(findViewById(R.id.setting_main),
				Gravity.CENTER, 0, 0);
		mZhangHao_text = (TextView) mRegisterRelativelayout
				.findViewById(R.id.accountnumber_textview);
		mZhangHao_text.setTypeface(mTypeface);
		// mUpdatePassword=(TextView)mRegisterRelativelayout.findViewById(R.id.xiugai_password_textview);
		// mUpdatePassword.setTypeface(mTypeface);
		mMiMa_text = (TextView) mRegisterRelativelayout
				.findViewById(R.id.password_textview);
		mMiMa_text.setTypeface(mTypeface);
		mZhangHao = (EditText) mRegisterRelativelayout
				.findViewById(R.id.accountnumber_edittext);
		mZhangHao.setTypeface(mTypeface);
		mZhangHao.setOnFocusChangeListener(mInputFocusChangeListener);
		mMiMa = (EditText) mRegisterRelativelayout
				.findViewById(R.id.password_edittext);
		mMiMa.setTypeface(mTypeface);
		mLogin = (ImageView) mRegisterRelativelayout
				.findViewById(R.id.bb_denglu_btn);
		mLogin.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "login_selector", "drawable"));
		mCanle = (ImageView) mRegisterRelativelayout
				.findViewById(R.id.bb_cancels_btn);
		mRegister = (TextView) mRegisterRelativelayout
				.findViewById(R.id.register_textview);
		mRegister.setTypeface(mTypeface);
		mForgetPassWord = (TextView) mRegisterRelativelayout
				.findViewById(R.id.forget_password_textview);
		mForgetPassWord.setTypeface(mTypeface);
		mCanle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mLoginPopuWindow != null) {
					mLoginPopuWindow.dismiss();
					mLoginPopuWindow = null;
				}
			}
		});
		
		mLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				String zhanghao = mZhangHao.getText().toString().trim();
				String mima = mMiMa.getText().toString().trim();
				String cpuSerial = Utils.getCpuSerial(getApplicationContext());
				if (zhanghao.length() != 0 && mima.length() != 0) {
					editor.putString("account", zhanghao);//账号保存到本地。 
					editor.putString("password", mima); //密码保存
					editor.commit();
					String httpUrl = Configure.LOGIN_URL
							.replace("ACCOUNT", zhanghao)
							.replace("PASSWORD", MD5Util.string2MD5(mima))
							.replace("DEVICE_ID", cpuSerial);
					Log.e("lee", "httpUrl == " + httpUrl);
					HttpUtils.doPost(httpUrl, SettingActivity.this,
							Configure.HTTP_TAG_LOGIN);
				} else {
					Utils.showToastMessage(SettingActivity.this,
							getString(R.string.login_input_error));
				}
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				try {
					Intent intent = new Intent();
					intent.putExtra("isRegister", true);
					ComponentName comp = new ComponentName(
							"com.worldchip.bbpaw.bootsetting",
							"com.worldchip.bbpaw.bootsetting.activity.WifiListActivity");
					intent.setComponent(comp);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		mForgetPassWord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mLoginPopuWindow != null) {
					mLoginPopuWindow.dismiss();
					mLoginPopuWindow = null;
				}
				showForgetPassword();
			}

		});
	}

	private void showForgetPassword() {
		if (mForgetPasswordPopuwindow != null) {
			mForgetPasswordPopuwindow.dismiss();
			mForgetPasswordPopuwindow = null;
		}
		mForgetPasswordRelativelayout = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(R.layout.forget_password_dailog,
				null);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = (int) (dm.widthPixels);
		int height = (int) (dm.heightPixels);
		mForgetPasswordPopuwindow = new PopupWindow(SettingActivity.this);
		mForgetPasswordPopuwindow.setBackgroundDrawable(new ColorDrawable(0));
		mForgetPasswordPopuwindow.setWidth(width);
		mForgetPasswordPopuwindow.setHeight(height);
		mForgetPasswordPopuwindow.setOutsideTouchable(false);
		mForgetPasswordPopuwindow.setFocusable(true);
		mForgetPasswordPopuwindow.setContentView(mForgetPasswordRelativelayout);
		mForgetPasswordPopuwindow.showAtLocation(
				findViewById(R.id.setting_main), Gravity.CENTER, 0, 0);
		mForgetPasswordPopuwindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				stopVerficationRefreshTimer();
			}
		});
//		mFindAccountTV = (TextView) mForgetPasswordRelativelayout
//				.findViewById(R.id.account_find_title);
//		mFindAccountTV.setTypeface(mTypeface);
		mValidateCodeET = (EditText) mForgetPasswordRelativelayout
				.findViewById(R.id.validate_code_et);
		mValidateCodeET.setTypeface(mTypeface);
		mFindAccountET = (EditText) mForgetPasswordRelativelayout
				.findViewById(R.id.find_psw_account_et);
		mFindAccountET.setTypeface(mTypeface);
		mValidateCodeET.setOnFocusChangeListener(mInputFocusChangeListener);
		mFindAccountET.setOnFocusChangeListener(mInputFocusChangeListener);
		mValidateCodeBtn = (Button) mForgetPasswordRelativelayout
					.findViewById(R.id.validate_code_btn);
		mValidateCodeBtn.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "validatecode_selector", "drawable"));
	    mValidateCodeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					getVerficationCode();
				}
			});
		mForfetPasswordCancle = (ImageView) mForgetPasswordRelativelayout
				.findViewById(R.id.forget_canle_button);
		mForgetPasswordConfrim = (ImageView) mForgetPasswordRelativelayout
				.findViewById(R.id.forget_comfrim_button);
		mForfetPasswordCancle.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "canlce_selector", "drawable"));
		mForgetPasswordConfrim.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "confrim_button_selector", "drawable"));
		mForfetPasswordCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mForgetPasswordPopuwindow != null) {
					mForgetPasswordPopuwindow.dismiss();
					mForgetPasswordPopuwindow = null;
				}
			}
		});
		mForgetPasswordConfrim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				commitFindPassword();
			}
		});
	}

	private void showXiuGaiPassword(final FindPasswordInfo pswInfo) {
		if (mXiuGaiPassWordPopuwindow != null) {
			mXiuGaiPassWordPopuwindow.dismiss();
			mXiuGaiPassWordPopuwindow = null;
		}
		mXiuGaiPasswordRelativelayout = (RelativeLayout) LayoutInflater.from(
				SettingActivity.this).inflate(R.layout.xiugai_password_dailog,
				null);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = (int) (dm.widthPixels);
		int height = (int) (dm.heightPixels);

		mXiuGaiPassWordPopuwindow = new PopupWindow(SettingActivity.this);
		mXiuGaiPassWordPopuwindow.setBackgroundDrawable(new ColorDrawable(0));
		mXiuGaiPassWordPopuwindow.setWidth(width);
		mXiuGaiPassWordPopuwindow.setHeight(height);
		mXiuGaiPassWordPopuwindow.setOutsideTouchable(false);
		mXiuGaiPassWordPopuwindow.setFocusable(true);
		mXiuGaiPassWordPopuwindow.setContentView(mXiuGaiPasswordRelativelayout);
		mXiuGaiPassWordPopuwindow.showAtLocation(
				findViewById(R.id.setting_main), Gravity.CENTER, 0, 0);
		mXiuGaiPassword = (TextView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.change_psw_title);
		mXiuGaiPassword.setTypeface(mTypeface);
		mOldPassword = (TextView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.jiumima_textview);
		mOldPassword.setTypeface(mTypeface);
		View oldPasswordView = mXiuGaiPasswordRelativelayout.findViewById(R.id.old_password_ll);
		if (pswInfo != null) {
			oldPasswordView.setVisibility(View.GONE);
		} else {
			oldPasswordView.setVisibility(View.VISIBLE);
		}
		
		mNewPassword = (TextView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.xinmima_textview);
		mNewPassword.setTypeface(mTypeface);
		mConfrimPassword = (TextView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.comfrin_password_textview);
		mConfrimPassword.setTypeface(mTypeface);
		mOldPassword_edittext = (EditText) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.jiumima_edittext);
		mOldPassword_edittext.setTypeface(mTypeface);
		mNewPassword_edittext = (EditText) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.xinmima_edittext);
		mNewPassword_edittext.setTypeface(mTypeface);
		mConfrimPassword_edittext = (EditText) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.comfrin_password_edittext);
		mConfrimPassword_edittext.setTypeface(mTypeface);
		mXiuGaiPasswordCanle = (ImageView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.forgetpassword_canle_button);
		mXiuGaiPasswordConfrim = (ImageView) mXiuGaiPasswordRelativelayout
				.findViewById(R.id.forgetpassword_comfrim_button);
		mXiuGaiPasswordConfrim.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "confrim_button_selector",
				"drawable"));
		mXiuGaiPasswordCanle.setBackgroundResource(Utils.getResourcesId(
				MyApplication.getAppContext(), "canlce_selector", "drawable"));
		mXiuGaiPasswordCanle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				if (mXiuGaiPassWordPopuwindow != null) {
					mXiuGaiPassWordPopuwindow.dismiss();
					mXiuGaiPassWordPopuwindow = null;
				}
			}
		});
		
		mXiuGaiPasswordConfrim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
				String oldpassword = (pswInfo != null ? pswInfo.getOriginalPassword() : mOldPassword_edittext.getText().toString().trim());
				Log.e("lee", "oldpassword == "+oldpassword);
				String newpassword = mNewPassword_edittext.getText().toString().trim();
				String confrimpassword = mConfrimPassword_edittext.getText().toString().trim();
				String CpuSerial = Utils.getCpuSerial(getApplicationContext());
				if (oldpassword.length() != 0 && newpassword.length() != 0
						&& confrimpassword.length() != 0) {
					if (newpassword.matches("^[a-z0-9A-Z]{6,16}$")) {
						if (newpassword.equals(confrimpassword)) {
							if (pswInfo != null  || !oldpassword.equals(newpassword)) {
								String httpUrl = Configure.UPDATE_PASSWORD
										.replace("ADMIN", pswInfo == null ? LoginState.getInstance().getAccount() : pswInfo.getAccount())
										.replace("NEWPASSWORD", MD5Util.string2MD5(newpassword))
										.replace("OLDPASSWORD", pswInfo != null ? oldpassword : MD5Util.string2MD5(oldpassword))
										.replace("DEVICE_ID", CpuSerial);
								Log.e("lee", "httpUrl == " + httpUrl);
								HttpUtils.doPost(httpUrl, SettingActivity.this, Configure.HTTP_TAG_UPDATE_PASSWORD);
							} else {
								Utils.showToastMessage(SettingActivity.this, getString(R.string.original_eq_newpsw_error_text));
							}
						} else {
							Utils.showToastMessage(SettingActivity.this, getString(R.string.code_input_agen));
						}
				} else {
					Utils.showToastMessage(SettingActivity.this, getString(R.string.validate_psw_empty_error));
				}
			} else {
				Utils.showToastMessage(SettingActivity.this, getString(R.string.code_not_empty));
			}
			}
		});
	}

	public void onClick(View view) {
		mSoundpool.play(mSoundMap.get(30), 1, 1, 0, 0, 1);
		switch (view.getId()) {
		case R.id.name_and_image_btn: 
			if (!LoginState.getInstance().isLogin()) {
				showLogin();
			} else {
				showExitLogin();
			}
			break;
		case R.id.stand_by_time_ll:
			showTimePopupWindow();
			break;
		case R.id.sound_ll:
			showSoundPopupWindow();
			break;
		case R.id.brightness_ll:
			showBrightnessPopupWindow();
			break;
		case R.id.wifi_image:
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				Toast.makeText(SettingActivity.this, R.string.wifi_off,
						Toast.LENGTH_SHORT).show();
				wifiManager.setWifiEnabled(false);
				mWifi_image.setImageResource(R.drawable.wifi_off);
			} else {
				Toast.makeText(SettingActivity.this, R.string.wifi_on,
						Toast.LENGTH_SHORT).show();
				wifiManager.setWifiEnabled(true);
				mWifi_image.setImageResource(R.drawable.wifi_on);
			}
			break;
		/*case R.id.bluetooth_imgage:
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (mBluetoothAdapter != null) {
				if (mBluetoothAdapter.isEnabled()) {
					// Utils.showToastMessage(SettingActivity.this,
					mBluetoothAdapter.disable();
					mBluetooth_imgage
							.setImageResource(R.drawable.bluetooth_off);
				} else {
					// Utils.showToastMessage(SettingActivity.this,
					mBluetoothAdapter.enable();
					mBluetooth_imgage.setImageResource(R.drawable.bluetooth_on);
				}
			} else {
				// Utils.showToastMessage(SettingActivity.this,
			}
			break;*/
		case R.id.sensor_image:
			//SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			//Sensor sensor_accelerometer = sensorManager
			//		.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			SharedPreferences configShard = getSharedPreferences("config", 0);
			boolean sensorSwitch = configShard.getBoolean("sensor_switch", true);
			if (sensorSwitch) {
				Toast.makeText(SettingActivity.this,
						R.string.accelerometer_on, Toast.LENGTH_SHORT).show();
				mSensorImage.setImageResource(R.drawable.accelerometer_on);
				
			} else {
				//sensorManager.registerListener(null, sensor_accelerometer,
				//		SensorManager.SENSOR_DELAY_UI);
				Toast.makeText(SettingActivity.this, R.string.accelerometer_off,
						Toast.LENGTH_SHORT).show();
				mSensorImage.setImageResource(R.drawable.accelerometer_off);
			}
			configShard.edit().putBoolean("sensor_switch", !sensorSwitch).commit();
			break;
		case R.id.img_bg_sound:
			if (DataManager.getMainIsPlayEffectEnable(MyApplication
					.getAppContext())) {
				DataManager.setMainIsPlayBackgroundMusicEnable(
						MyApplication.getAppContext(), !isOperBGSound);
				isOperBGSound = !isOperBGSound;
				if (isOperBGSound) {
					mImageBGSound
							.setImageResource(R.drawable.btn_music_on_state);
				} else {
					mImageBGSound
							.setImageResource(R.drawable.btn_music_off_state);
				}
			}
			break;
		case R.id.setting_back:
			SettingActivity.this.finish();
			break;
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("onActivityResult", requestCode + "-------------" + resultCode);
		switch (resultCode) {
		case 0:
			break;
		case 1:

			break;
		case 800:
			mBabyMoren_edite.setImageBitmap(HttpCommon
					.getImageBitmap(getPackageName()));
			break;
		default: 
			if (requestCode == 100) {
				if (data == null)
					return;
				Uri uri = data.getData();
				Intent intent = new Intent();
				intent.setAction("com.android.camera.action.CROP");
				intent.setDataAndType(uri, "image/*");
				intent.putExtra("crop", "true");
				// aspectX aspectY 锟角匡拷叩谋锟斤拷锟�
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				// outputX outputY 锟角裁硷拷图片锟斤拷锟�
				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("return-data", true);
				SettingActivity.this.startActivityForResult(intent, 200);
			} else if (requestCode == 200) // 锟斤拷锟斤拷图片
			{
				if (data == null)
					return;
				Bitmap bmap = data.getParcelableExtra("data");
				mBabyMoren_edite.setImageBitmap(bmap);
			} else if (requestCode == 500)
			{
				if (data == null)
					return;
				Bitmap bmap = data.getParcelableExtra("data");
				if (bmap == null) {
					return;
				}
				mBabyMoren_edite.setImageBitmap(bmap);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		SharedPreferences image_name = getSharedPreferences("image_name", 0);
		int index = image_name.getInt("index", 0);
		Resources res = getResources();
		Bitmap bmp = null;
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			switch (index) {
			case 0:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[2]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[2]);
				image_name.edit().putInt("index", 2).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[2]);
				break;
			case 1:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[0]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[0]);
				image_name.edit().putInt("index", 0).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[0]);
				break;
			case 2:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[1]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[1]);
				image_name.edit().putInt("index", 1).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[1]);
				break;
			default:
				break;
			}
			HttpCommon.SavaImage(bmp, getPackageName());
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			switch (index) {
			case 0:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[1]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[1]);
				image_name.edit().putInt("index", 1).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[1]);
				break;
			case 1:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[2]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[2]);
				image_name.edit().putInt("index", 2).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[2]);
				break;
			case 2:
				mImageNameBig.setImageResource(ResourceManager.SYSTME_IMAGE[0]);
				// mNameAndImageBtn.setImageResource(HttpCommon.res[0]);
				image_name.edit().putInt("index", 0).commit();
				bmp = BitmapFactory.decodeResource(res,
						ResourceManager.SYSTME_IMAGE[0]);
				break;
			default:
				break;
			}
			HttpCommon.SavaImage(bmp, getPackageName());
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	private MyVolumeReceiver mVolumeReceiver;

	/**
	 * Register when the volume changes received broadcast
	 */
	private void registerVolumeChangeBroadcast() {
		mVolumeReceiver = new MyVolumeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(mVolumeReceiver, filter);
	}

	private void unRegisterVolumeChangeBroadcast() {
		try {
			if (mVolumeReceiver != null) {
				unregisterReceiver(mVolumeReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * When handling the volume change of interface display
	 * 
	 * @author long
	 */
	private class MyVolumeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟戒化锟斤拷锟斤拷seekbar锟斤拷位锟斤拷
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int currVolume = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);// 锟斤拷前锟斤拷媒锟斤拷锟斤拷锟斤拷
				if (mSoundSeekBar != null) {
					mSoundSeekBar.setProgress(currVolume);
				}
			}
		}
	}

	@Override
	public void onStart(String httpTag) {
		mHandler.sendEmptyMessage(START_PROGRESS_DIALOG);
	}
    

	@Override
	public void onSuccess(String result, String httpTag) {
		if (Configure.DEBUG) {
			Log.e("lee", "onSuccess response  == " + result.toString()
					+ " ----------->>>" + httpTag);
			Log.e("Wing", result.toString());
		}
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = HTTP_REQ_CODE;
		msg.arg1 = Configure.SUCCESS;
		bundle.putString("httpTag", httpTag);
		bundle.putString("result", result);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onFailure(Exception e, String httpTag) {
		if (httpTag.equals(Configure.HTTP_TAG_LOGIN)) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Utils.showToastMessage(MyApplication.getAppContext(),
							getString(R.string.login_failure));
				}
			});
		}
		if (httpTag.equals(Configure.HTTP_TAG_UPDATE_PASSWORD)) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Utils.showToastMessage(MyApplication.getAppContext(),
							getString(R.string.update_password_failure));
				}
			});
		}
	}

	@Override
	public void onFinish(int result, String httpTag) {
		if (result == HttpUtils.HTTP_RESULT_NETWORK_ERROR) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpUtils.handleRequestExcption(HttpUtils.HTTP_RESULT_NETWORK_ERROR);
				}
			});
		}
		mHandler.sendEmptyMessage(STOP_PROGRESS_DIALOG);
	}

	private void handResultMessge(int resultCode, String httpTag,Object result) {
		String messageText = "";
		Log.e("lee", "handResultMessge resultCode == "+resultCode + "-------" + result);
		if (resultCode == HttpUtils.HTTP_RESULT_CODE_SUCCESS) {
			if (httpTag.equals(Configure.HTTP_TAG_LOGIN)) {
				messageText = getString(R.string.login_seccuss);
				SettingActivity.this.sendBroadcast(new Intent(
						Utils.LOGIN_SECCUSS_ACTION));
				if (mLoginPopuWindow != null) {
					mLoginPopuWindow.dismiss();
					mLoginPopuWindow = null;
				}
				if (result != null) {
					LoginState loaLoginState = (LoginState)result;
					loaLoginState.setLogin(true);
					loaLoginState.setAccount(mZhangHao.getText().toString().trim());
					showExitLogin();
				}
				updateAccountView();
			} else if (httpTag.equals(Configure.HTTP_TAG_UPDATE_PASSWORD)) {
				messageText = getString(R.string.update_password_suceecss);
				if (mXiuGaiPassWordPopuwindow != null) {
					mXiuGaiPassWordPopuwindow.dismiss();
					mXiuGaiPassWordPopuwindow = null;
				}
				//showExitLogin();
				showLogin();
			} else if (httpTag.equals(Configure.HTTP_TAG_GET_VERFICATION_CODE)) {
				messageText = getString(R.string.validate_code_sendent);
			} else if (httpTag.equals(Configure.HTTP_TAG_GET_OLD_PSW)) {
				if (mForgetPasswordPopuwindow != null) {
					mForgetPasswordPopuwindow.dismiss();
					mForgetPasswordPopuwindow = null;
				}
				if (result != null) {
					showXiuGaiPassword((FindPasswordInfo)result);
				}
			}
		} else {
			HttpUtils.handleRequestExcption(resultCode);
		}
		if (!TextUtils.isEmpty(messageText)) {
			Utils.showToastMessage(MyApplication.getAppContext(), messageText);
		}
	}
	
	private OnFocusChangeListener mInputFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			// TODO Auto-generated method stub
			EditText accountEdit = (EditText)view;
            String hint;
            if (hasFocus) {
                hint = accountEdit.getHint().toString();
                accountEdit.setTag(hint);
                accountEdit.setHint("");
            } else {
                hint = accountEdit.getTag().toString();
                accountEdit.setHint(hint);
            }
		}
	};
	
	
	 class VerificationRefreshTask extends TimerTask {
	        @Override
	        public void run() {
	            mHandler.sendMessage(mHandler
	                    .obtainMessage(MSG_GET_VERIFICATION_CODE));
	        }
	    }
	private void startVerficationRefreshTimer() {
		if (mValidateCodeBtn != null) {
			mValidateCodeBtn.setEnabled(false);
		}
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();

        if (mRefreshTask != null) {
            mRefreshTask.cancel();
        }
        mRefreshTask = new VerificationRefreshTask();

        if (mTimer != null && mRefreshTask != null) {
            mTimer.schedule(mRefreshTask, 0, 1000);
        }
    }
    private void stopVerficationRefreshTimer() {
    	if (mValidateCodeBtn != null) {
			mValidateCodeBtn.setEnabled(true);
			INTERVAL = 60;
		}
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mRefreshTask != null) {
            mRefreshTask.cancel();
            mRefreshTask = null;
        }
    }
    
    private void getVerficationCode() {
    	startVerficationRefreshTimer();
		if (mFindAccountET != null) {
			String account = mFindAccountET.getText().toString();
			String language = null;
			if(Utils.getLanguageInfo(this) == Utils.ENG_LANGUAGE_INDEX){
				language = "ENGLISH";
			} else {
				language = "CHINESE";
			}
			if (!TextUtils.isEmpty(account)) {
				String httpUrl = Configure.GET_VERFICATION_CODE.replace("LANGUAGE", language).replace("ACCOUNT", account)
				.replace("DEVICE_ID", Utils.getCpuSerial(MyApplication.getAppContext()));
				Log.e("lee", " httpUrl == "+httpUrl);
				HttpUtils.doPost(httpUrl, SettingActivity.this, Configure.HTTP_TAG_GET_VERFICATION_CODE);
			} else {
				Utils.showToastMessage(MyApplication.getAppContext(), getString(R.string.find_account_empty_error));
			}
		}
    }
    
    private void commitFindPassword() {
    	if (mFindAccountET != null) {
    		String account = mFindAccountET.getText().toString();
			if (!TextUtils.isEmpty(account)) {
				String code = mValidateCodeET.getText().toString();
				if (!TextUtils.isEmpty(code)) {
					String httpUrl = Configure.GET_OLD_PSW.replace("ACCOUNT", account)
							.replace("DEVICE_ID", Utils.getCpuSerial(MyApplication.getAppContext()))
							.replaceFirst("CODE", code);
					HttpUtils.doPost(httpUrl, SettingActivity.this, Configure.HTTP_TAG_GET_OLD_PSW);
				} else {
					Utils.showToastMessage(MyApplication.getAppContext(), getString(R.string.verfication_code_empty_error));
				}
			}else {
				Utils.showToastMessage(MyApplication.getAppContext(), getString(R.string.find_account_empty_error));
			}
    	}
    }
    
	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			mGlobalProgressDialog.dismiss();
			mGlobalProgressDialog = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWifiIntentReceiver != null) {
			unregisterReceiver(mWifiIntentReceiver);
		}
	}
}