package com.worldchip.bbp.ect.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.AlphaInAnimationAdapter;
import com.worldchip.bbp.ect.adapter.BrowserListviewAdapter;
import com.worldchip.bbp.ect.adapter.GridAppAdapter;
import com.worldchip.bbp.ect.adapter.GridPictureAdapter;
import com.worldchip.bbp.ect.adapter.GridShareAppAdapter;
import com.worldchip.bbp.ect.adapter.GridSharePictureAdapter;
import com.worldchip.bbp.ect.adapter.GridShareVideoAdapter;
import com.worldchip.bbp.ect.adapter.GridVideoAdapter;
import com.worldchip.bbp.ect.adapter.MusicAdapter;
import com.worldchip.bbp.ect.adapter.MusicShareAdapter;
import com.worldchip.bbp.ect.db.AppData;
import com.worldchip.bbp.ect.db.BrowserData;
import com.worldchip.bbp.ect.db.MusicData;
import com.worldchip.bbp.ect.db.PictureData;
import com.worldchip.bbp.ect.db.VideoData;
import com.worldchip.bbp.ect.entity.AppInfo;
import com.worldchip.bbp.ect.entity.BrowserInfo;
import com.worldchip.bbp.ect.entity.MusicInfo;
import com.worldchip.bbp.ect.entity.PictureInfo;
import com.worldchip.bbp.ect.entity.VideoInfo;
import com.worldchip.bbp.ect.service.TimeTontrolService;
import com.worldchip.bbp.ect.util.HomeSettings;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.Utils;
import com.worldchip.bbp.ect.view.SlipButton;
import com.worldchip.bbp.ect.view.SlipButton.OnChangedListener;

public class PatriarchControlActivity extends Activity implements
		OnClickListener, OnTouchListener, OnCheckedChangeListener {

	private static final String TAG = "--PatriarchControlActivity--";
	public static final String PUSH_MESSAGE_SWITCH="pushMessage_switch";
	public static final String CHARGING_PROTECT_SWITCH = "chargingProtect_switch";
	private ViewGroup main = null;
	private int mSelection = 20;
	private Boolean isToMainActivity = true;
	private EditText mETHideSoftInput;

	private ImageView mParentalcontrolMainBack;
	private RadioGroup mParentalControl;
	private RadioButton mParentMode;
	private boolean isOnClickl = false;
	private LinearLayout mLayoutRight;

	private EditText mChangeManagerPassword;
	private EditText mUpdateManagerPassword;
	private TextView mManagerConfirm;

	private RelativeLayout mPopupWindow;
	private PopupWindow mParentModePopupWindow;
	private TextView mParentModelCancel, mParentModelConfirm;
	private SlipButton mToggleButton;
	private SlipButton mChargingToggleBtn;
	private TextView mPictureShare;
	private TextView mMusicShare;
	private TextView mVideoShare;
	private GridView mGridViewPictureLeft;
	private GridView mGridViewPictureRight;
	private TextView mSharePictureToRight;
	private TextView mSharePictureToLeft;
	private ImageView mSharePictureToBack;
	private GridPictureAdapter pictureAdapter;
	private AlphaInAnimationAdapter pictureAnimAdapter;
	private GridSharePictureAdapter sharePictureAdapter;
	private AlphaInAnimationAdapter sharePictureAnimAdapter;
	private List<PictureInfo> pictureInfos = null;
	private List<PictureInfo> sharePictureInfos = null;
	private ListView mListViewMusicLeft;
	private ListView mListViewMusicRight;
	private TextView mShareMusicToRight;
	private TextView mShareMusicToLeft;
	private ImageView mShareMusicToBack;
	private MusicAdapter musicAdapter;
	private MusicShareAdapter musicShareAdapter;
	private List<MusicInfo> musicInfos = null;
	private List<MusicInfo> shareMusicInfos = null;
	private GridView mGridViewVideoLeft, mGridViewVideoRight;
	private GridVideoAdapter videoAdapter;
	private AlphaInAnimationAdapter videoAnimAdapter;
	private GridShareVideoAdapter shareVideoAdapter;
	private AlphaInAnimationAdapter shareVideoAnimAdapter;
	private TextView mShareVideoToRight, mShareVideoToLeft;
	private ImageView mShareVideoToBack;
	private PopupWindow mSharePopupWindow;
	private List<VideoInfo> videoInfos = null;
	private List<VideoInfo> shareVideoInfos = null;

	private GridView mGridViewAllApp;
	private GridView mGridViewMyApp;
	private TextView mAllApp;
	private TextView mMyApp;
	private GridAppAdapter appAdapter;
	private GridShareAppAdapter shareAppAdapter;
	private UninstallReceiver mUninstallReceiver;
	private String uninstallPackage;
	private List<AppInfo> appInfos = null;
	private List<AppInfo> shareAppInfos = null;
	private EditText mEditTextName;
	private EditText mEditTextUrl;
	private Button mAddBtn;
	private ListView mListViewUrl;
	private BrowserListviewAdapter mBListAdapter;
	private Button mBtnBBPaw;
	private static final String CHARGING_PROTECT_PREFER_NAME = "charging_protect_config";

	// private Context mContext;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String videoData = videoAdapter.getVideoData();
				shareVideoAdapter.delItem(videoData);
				break;
			case 1:
				String musicData = musicAdapter.getMusicData();
				musicShareAdapter.delItem(musicData);
				break;
			case 2:
				String pictureData = pictureAdapter.getPictureData();
				sharePictureAdapter.delItem(pictureData);
				break;
			case 3:
				uninstallPackage = appAdapter.getPackageName();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getLayoutInflater();

		main = (ViewGroup) inflater.inflate(R.layout.patriarch_control_main,
				null);
		setContentView(main);

		HttpCommon.hideSystemUI(PatriarchControlActivity.this, true);
		mSelection = this.getIntent().getIntExtra("selection", 20);
		if (mSelection != 20) {
			isToMainActivity = false;
		}
		// 锟截硷拷锟斤拷始锟斤拷
		initView();

		// 锟斤拷锟斤拷卸锟截广播
		mUninstallReceiver = new UninstallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		this.registerReceiver(mUninstallReceiver, filter);
		Log.e("lee", "PatriarchControlActivity initBBpawDataBases---");
	}

	/**
	 * 锟截硷拷锟斤拷始锟斤拷
	 */
	private void initView() {
		// 锟揭憋拷
		mETHideSoftInput = (EditText) findViewById(R.id.et_hide_softinput);
		mLayoutRight = (LinearLayout) main
				.findViewById(R.id.parentalcontrol_main_right);
		LayoutInflater inflater = LayoutInflater
				.from(PatriarchControlActivity.this);
		View view = inflater.inflate(R.layout.patriarch_control_time, null);
		mLayoutRight.addView(view);
		// 锟斤拷锟斤拷
		mParentalcontrolMainBack = (ImageView) main
				.findViewById(R.id.parentalcontrol_main_back);
		mParentalcontrolMainBack.setOnTouchListener(this);
		// RadioGroup选锟斤拷
		mParentalControl = (RadioGroup) main.findViewById(R.id.parentalcontrol);
		mParentalControl.setOnCheckedChangeListener(this);
		mParentalControl.check(getRadioButtonID(mSelection));
		// 锟揭筹拷模式
		mParentMode = (RadioButton) main.findViewById(R.id.parent_mode);
		mParentMode.setOnClickListener(this);
	}

	private int getRadioButtonID(int selection) {
		int id = R.id.mediashare;
		switch (selection) {
		case 0:
			id = R.id.time_control;
			break;
		case 1:
			id = R.id.behavior_statistics;
			break;
		case 2:
			id = R.id.mediashare;
			break;
		case 3:
			id = R.id.app_management;
			break;
		case 4:
			id = R.id.browse_web_management;
			break;
		case 5:
			id = R.id.shop;
			break;
		case 6:
			id = R.id.charging_protect;
			break;
		case 7:
			id = R.id.push_message;
			break;
		case 8:
			id = R.id.parent_mode;
			break;
		case 9:
			id = R.id.password_manager;
			break;

		default:
			break;
		}
		return id;
	}

	/**
	 * 锟斤拷始锟斤拷锟斤拷锟斤拷锟斤拷锟�
	 */
	private void initViewPassword(View view) {
		mChangeManagerPassword = (EditText) view
				.findViewById(R.id.change_manager_password);
		mUpdateManagerPassword = (EditText) view
				.findViewById(R.id.update_manager_password);
		mManagerConfirm = (TextView) view.findViewById(R.id.manager_confirm);
		mManagerConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setPassword();
			}
		});

		mChangeManagerPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String mChangeManagerPasswordValue = mChangeManagerPassword
						.getText().toString().trim();
				if (mChangeManagerPasswordValue.length() > 6) {
					mChangeManagerPassword.setText(mChangeManagerPasswordValue
							.substring(0, 6));
					mChangeManagerPassword.setSelection(6);
				}
			}
		});

		mUpdateManagerPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String mUpdateManagerPasswordValue = mUpdateManagerPassword
						.getText().toString().trim();
				if (mUpdateManagerPasswordValue.length() > 6) {
					mUpdateManagerPassword.setText(mUpdateManagerPasswordValue
							.substring(0, 6));
					mUpdateManagerPassword.setSelection(6);
				}
			}
		});
	}

	private void setPassword() {
		String managerPassword = mChangeManagerPassword.getText().toString()
				.trim();
		String updataPassword = mUpdateManagerPassword.getText().toString()
				.trim();
		if (!managerPassword.equals("") && managerPassword != "") {

			if (managerPassword.length() == 6) {

				if (!updataPassword.equals("") && updataPassword != "") {
					if (updataPassword.equals(managerPassword)) {
						SharedPreferences password = getSharedPreferences(
								"password_info", 1);
						password.edit().putString("password", updataPassword)
								.commit();
						Utils.showToastMessage(this, getResources().getString(R.string.password_updata_success));
					} else {
						Utils.showToastMessage(this, getResources().getString(R.string.passwords_nots_match));
					}
				} else {
					Utils.showToastMessage(this,
							getResources().getString(R.string.default_password_empty));
				}
			} else {
				Utils.showToastMessage(this,
						getResources().getString(R.string.input_six_password));
			}
		} else {
			Utils.showToastMessage(this, getResources().getString(R.string.change_password_not_empty));
		}
	}

	/**
	 * 时锟斤拷锟斤拷锟�
	 */
	private SharedPreferences preferences;
	private TimeTontrolService service = new TimeTontrolService();
	private TextView mTimeCountdown, mTimeQuantum, mTimeDate;
	// new mode
	private RelativeLayout mRLCountdown1, mRLCountdown2, mRLTime1, mRLTime2,
			mRLDate1, mRLDate2;
	private RelativeLayout mRLTransparentBaffle;
	private ImageButton mImgBtnEdit;
	private boolean canEdit = false;
	// 锟斤拷锟斤拷时
	private TextView mTimeCountdownReduce, mTimeCountdownValue,
			mTimeCountdownIncrease;
	private int minute = 0;
	// 时锟斤拷慰锟斤拷锟�start
	private TextView mTimeStartHoursAdd, mTimeStartMinAdd,
			mTimeStartHoursSubtract, mTimeStartMinSubtract;
	private TextView mTimeStartHour, mTimeStartMin;
	private int startHour = 0, startMin = 0;
	// 时锟斤拷慰锟斤拷锟�end
	private TextView mTimeEndHoursAdd, mTimeEndMinAdd, mTimeEndHoursSubtract,
			mTimeEndMinSubtract;
	private TextView mTimeEndHour, mTimeEndMin;
	private int endHour = 0, endMin = 0;
	// 锟斤拷时锟斤拷锟斤拷锟�
	private TextView mTimeDateMon, mTimeDateTues, mTimeDateWed, mTimeDateThur,
			mTimeDateFri, mTimeDateSta, mTimeDateSun;

	private boolean isTimeCountdownOK = false;
	private boolean isTimeOK = false;
	private boolean isDateOK = false;

	/**
	 * 锟斤拷始锟斤拷时锟斤拷锟斤拷锟�
	 */
	private void initViewTime(View view) {
		minute = 20;
		startHour = 0;
		startMin = 0;
		endHour = 0;
		endMin = 0;
		preferences = getSharedPreferences("time_info", 0);
		isTimeCountdownOK = preferences.getBoolean("isTimeCountdownOK", false);
		isTimeOK = preferences.getBoolean("isTimeOK", false);
		isDateOK = preferences.getBoolean("isDateOK", false);
		mRLTransparentBaffle = (RelativeLayout) findViewById(R.id.rl_transparent_baffle);
		mImgBtnEdit = (ImageButton) findViewById(R.id.imgbtn_edit);
		final View imgBtnEditRL = findViewById(R.id.imgbtn_edit_rl);
		imgBtnEditRL.bringToFront();
		mImgBtnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!canEdit) {
					mRLTransparentBaffle.setVisibility(View.INVISIBLE);
					mImgBtnEdit
							.setBackgroundResource(R.drawable.parentalcontrol_timecontrol_canedit);
					canEdit = true;
				} else {
					mRLTransparentBaffle.setVisibility(View.VISIBLE);
					mImgBtnEdit
							.setBackgroundResource(R.drawable.parentalcontrol_timecontrol_edit);
					canEdit = false;
					saveSettingTime();
					imgBtnEditRL.bringToFront();
				}

			}
		});

		mRLCountdown1 = (RelativeLayout) findViewById(R.id.rl_countdown1);
		mRLCountdown2 = (RelativeLayout) findViewById(R.id.rl_countdown2);
		mRLTime1 = (RelativeLayout) findViewById(R.id.rl_time1);
		mRLTime2 = (RelativeLayout) findViewById(R.id.rl_time2);
		mRLDate1 = (RelativeLayout) findViewById(R.id.rl_date1);
		mRLDate2 = (RelativeLayout) findViewById(R.id.rl_date2);
		if (isTimeCountdownOK) {
			mRLCountdown1.setVisibility(View.GONE);
			mRLCountdown2.setVisibility(View.VISIBLE);

		}
		if (isTimeOK) {
			mRLTime1.setVisibility(View.GONE);
			mRLTime2.setVisibility(View.VISIBLE);
		}
		if (isDateOK) {
			mRLDate1.setVisibility(View.GONE);
			mRLDate2.setVisibility(View.VISIBLE);

		}

		mRLCountdown1.setOnClickListener(this);
		mRLCountdown2.setOnClickListener(this);
		mRLTime1.setOnClickListener(this);
		mRLTime2.setOnClickListener(this);
		mRLDate1.setOnClickListener(this);
		mRLDate2.setOnClickListener(this);

		mTimeCountdown = (TextView) view.findViewById(R.id.time_countdown);
		mTimeCountdown.setOnClickListener(this);
		mTimeCountdownReduce = (TextView) view
				.findViewById(R.id.time_countdown_reduce);
		mTimeCountdownValue = (TextView) view
				.findViewById(R.id.time_countdown_value);
		mTimeCountdownIncrease = (TextView) view
				.findViewById(R.id.time_countdown_increase);
		mTimeCountdownReduce.setOnClickListener(this);
		mTimeCountdownIncrease.setOnClickListener(this);
		if (isTimeCountdownOK) {
			// mTimeCountdown
			// .setBackgroundResource(R.drawable.parentalcontrol_time_button_click_bg);
			int time = preferences.getInt("old_time", minute);
			if (time > 20) {
				minute = time;
			}
		} else {
			minute = 20;
		}
		mTimeCountdownValue.setText("" + minute);

		// 时锟斤拷锟�
		mTimeQuantum = (TextView) view.findViewById(R.id.time_quantum);
		mTimeQuantum.setOnClickListener(this);

		// start
		mTimeStartHoursAdd = (TextView) view
				.findViewById(R.id.time_start_hours_add);
		mTimeStartMinAdd = (TextView) view
				.findViewById(R.id.time_start_min_add);
		mTimeStartHoursSubtract = (TextView) view
				.findViewById(R.id.time_start_hours_subtract);
		mTimeStartMinSubtract = (TextView) view
				.findViewById(R.id.time_start_min_subtract);
		mTimeStartHoursAdd.setOnClickListener(this);
		mTimeStartMinAdd.setOnClickListener(this);
		mTimeStartHoursSubtract.setOnClickListener(this);
		mTimeStartMinSubtract.setOnClickListener(this);
		mTimeStartHour = (TextView) view.findViewById(R.id.time_start_hour);
		mTimeStartMin = (TextView) view.findViewById(R.id.time_start_min);
		mTimeStartHour.setText("0" + startHour);
		mTimeStartMin.setText("0" + startMin);

		// end
		mTimeEndHoursAdd = (TextView) view
				.findViewById(R.id.time_end_hours_add);
		mTimeEndMinAdd = (TextView) view.findViewById(R.id.time_end_min_add);
		mTimeEndHoursSubtract = (TextView) view
				.findViewById(R.id.time_end_hours_subtract);
		mTimeEndMinSubtract = (TextView) view
				.findViewById(R.id.time_end_min_subtract);
		mTimeEndHoursAdd.setOnClickListener(this);
		mTimeEndMinAdd.setOnClickListener(this);
		mTimeEndHoursSubtract.setOnClickListener(this);
		mTimeEndMinSubtract.setOnClickListener(this);
		mTimeEndHour = (TextView) view.findViewById(R.id.time_end_hour);
		mTimeEndMin = (TextView) view.findViewById(R.id.time_end_min);
		mTimeEndHour.setText("0" + endHour);
		mTimeEndMin.setText("0" + endMin);

		if (isTimeOK) {
			// mTimeQuantum.setBackgroundResource(R.drawable.parentalcontrol_time_button_click_bg);
			String startTime = preferences.getString("startTime", "");
			if (startTime != "" && !startTime.equals("")) {
				String startHour = startTime.substring(0, 2);
				String startMin = startTime.substring(3, 5);
				mTimeStartHour.setText(startHour);
				mTimeStartMin.setText(startMin);
			}
			String endTime = preferences.getString("endTime", "");
			if (endTime != "" && !endTime.equals("")) {
				String endHour = endTime.substring(0, 2);
				String endMin = endTime.substring(3, 5);
				mTimeEndHour.setText(endHour);
				mTimeEndMin.setText(endMin);
			}
		} else {
			// mTimeQuantum
			// .setBackgroundResource(R.drawable.parentalcontrol_time_button_noclick_bg);
		}

		// 锟斤拷
		mTimeDate = (TextView) view.findViewById(R.id.time_date);
		mTimeDate.setOnClickListener(this);
		// 锟斤拷一锟斤拷锟斤拷锟斤拷
		mTimeDateMon = (TextView) view.findViewById(R.id.time_date_mon);
		mTimeDateTues = (TextView) view.findViewById(R.id.time_date_tues);
		mTimeDateWed = (TextView) view.findViewById(R.id.time_date_wed);
		mTimeDateThur = (TextView) view.findViewById(R.id.time_date_thur);
		mTimeDateFri = (TextView) view.findViewById(R.id.time_date_fri);
		mTimeDateSta = (TextView) view.findViewById(R.id.time_date_sta);
		mTimeDateSun = (TextView) view.findViewById(R.id.time_date_sun);
		mTimeDateMon.setOnClickListener(this);
		mTimeDateTues.setOnClickListener(this);
		mTimeDateWed.setOnClickListener(this);
		mTimeDateThur.setOnClickListener(this);
		mTimeDateFri.setOnClickListener(this);
		mTimeDateSta.setOnClickListener(this);
		mTimeDateSun.setOnClickListener(this);

		int mon = preferences.getInt("mon", 0);
		int tues = preferences.getInt("tues", 0);
		int wed = preferences.getInt("wed", 0);
		int thur = preferences.getInt("thur", 0);
		int fri = preferences.getInt("fri", 0);
		int sta = preferences.getInt("sta", 0);
		int sun = preferences.getInt("sun", 0);
		if (isDateOK) {
			if (mon == 1) {
				mTimeDateMon.setBackgroundResource(R.drawable.time_date_click);
			}
			if (tues == 1) {
				mTimeDateTues.setBackgroundResource(R.drawable.time_date_click);
			}
			if (wed == 1) {
				mTimeDateWed.setBackgroundResource(R.drawable.time_date_click);
			}
			if (thur == 1) {
				mTimeDateThur.setBackgroundResource(R.drawable.time_date_click);
			}
			if (fri == 1) {
				mTimeDateFri.setBackgroundResource(R.drawable.time_date_click);
			}
			if (sta == 1) {
				mTimeDateSta.setBackgroundResource(R.drawable.time_date_click);
			}
			if (sun == 1) {
				mTimeDateSun.setBackgroundResource(R.drawable.time_date_click);
			}
			// mTimeDate
			// .setBackgroundResource(R.drawable.parentalcontrol_time_button_click_bg);
		} else {
			// mTimeDate
			// .setBackgroundResource(R.drawable.parentalcontrol_time_button_noclick_bg);
		}
	}

	private void saveSettingTime() {

		if (isTimeCountdownOK) {
			setCountDownTime();
		}
		if (isTimeOK) {
			setQuantum();
		}
		if (isDateOK) {
			setDate();
		} else {
			resetWeekBG();
		}
		preferences.edit().putBoolean("isTimeCountdownOK", isTimeCountdownOK)
				.commit();
		preferences.edit().putBoolean("isTimeOK", isTimeOK).commit();
		preferences.edit().putBoolean("isDateOK", isDateOK).commit();
	}

	private void initViewWeb(View view) {
		mEditTextName = (EditText) view.findViewById(R.id.et_name);
		mEditTextUrl = (EditText) view.findViewById(R.id.et_url);
		mAddBtn = (Button) view.findViewById(R.id.btn_add);
		mBtnBBPaw = (Button) findViewById(R.id.btn_bbpaw);
		mListViewUrl = (ListView) view.findViewById(R.id.lv_url_list);
		List<BrowserInfo> browserList = BrowserData
				.getBrowserAddressList(PatriarchControlActivity.this);
		mBListAdapter = new BrowserListviewAdapter(browserList,
				PatriarchControlActivity.this);
		mBtnBBPaw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("address", mBtnBBPaw.getText().toString());
				ComponentName cn = new ComponentName("com.worldchip.bbp.ect",
						"com.worldchip.bbp.ect.activity.BrowserActivity");
				intent.setComponent(cn);
				PatriarchControlActivity.this.startActivity(intent);
			}
		});
		mAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mEditTextName.getText().toString() != null
						&& !mEditTextName.getText().toString().equals("")
						&& mEditTextUrl.getText().toString() != null
						&& !mEditTextUrl.getText().toString().equals("")) {

					BrowserInfo info = BrowserData.getBrowserInfo(
							PatriarchControlActivity.this, mEditTextName
									.getText().toString());
					if (info != null) {
						Toast.makeText(PatriarchControlActivity.this,
								getResources().getString(R.string.ip_name_exist), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					info = BrowserData.getBrowserInfoByUrl(
							PatriarchControlActivity.this, mEditTextUrl
									.getText().toString());
					if (info != null) {
						Toast.makeText(PatriarchControlActivity.this,
								getResources().getString(R.string.url_exist), Toast.LENGTH_SHORT)
								.show();
						return;
					}
					ContentValues values = new ContentValues();
					values.put("title", mEditTextName.getText().toString());
					values.put("url", mEditTextUrl.getText().toString());
					if (BrowserData.addBrowserAddress(
							PatriarchControlActivity.this, values)) {
						Toast.makeText(PatriarchControlActivity.this,
								getResources().getString(R.string.add_success), Toast.LENGTH_SHORT).show();
						mBListAdapter.setAdapter(BrowserData
								.getBrowserAddressList(PatriarchControlActivity.this));
						mBListAdapter.notifyDataSetChanged();
					} else {
						Toast.makeText(PatriarchControlActivity.this,
								getResources().getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
					}
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEditTextName.getWindowToken(),
							0);

				} else {
					Toast.makeText(PatriarchControlActivity.this,
							getResources().getString(R.string.title_not_empty),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mListViewUrl.setAdapter(mBListAdapter);
	}

	private RelativeLayout mRLBBPawShop;
    private RelativeLayout mRlGoogleShop;
	/**
	 * init Shop
	 */
	private void initShopView(View viewShop) {
		mRLBBPawShop = (RelativeLayout) viewShop
				.findViewById(R.id.rl_bbpaw_shop);
//		mRlGoogleShop=(RelativeLayout)viewShop.findViewById(R.id.rl_goole_shop);
//		mRlGoogleShop.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent();
//				intent.setClass(PatriarchControlActivity.this,
//						BrowserActivity.class);
//				intent.putExtra("address", "www.baidu.com");
//				startActivity(intent);
//				
//			}
//		});
		mRLBBPawShop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PatriarchControlActivity.this,
						BrowserActivity.class);
				if(Utils.getLanguageInfo(getApplicationContext())==1){
					intent.putExtra("address", "www.bbpaw.com.cn/");
				}else{
					intent.putExtra("address", "www.bbpaw.com/");
				}
				
				startActivity(intent);
			}
		});
	}

	/**
	 * 锟斤拷息锟斤拷锟窖匡拷锟斤拷
	 */
	private void initViewPushMessage(View view) {
		mToggleButton = (SlipButton) view.findViewById(R.id.toggleButton);
		final SharedPreferences configShard = getSharedPreferences("config", 0);
		int messagePushSwitch = configShard.getInt(PUSH_MESSAGE_SWITCH, 1);
        mToggleButton.setCheck(messagePushSwitch ==1 ? true : false);
		mToggleButton.setOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
                 if(CheckState){
                	 configShard.edit().putInt(PUSH_MESSAGE_SWITCH, 1).commit();
                 }else{
                	 configShard.edit().putInt(PUSH_MESSAGE_SWITCH, 0).commit();
                 }
                 sendMessageSwitchBroacast(CheckState);
			}
		});
	}
	/*
	 * 初始化充电界面控制按钮
	 * 
	 * */
	
	private void initViewChargingProtect(View view) {
		mChargingToggleBtn = (SlipButton) view.findViewById(R.id.charging_protect_togglebtn);
		final SharedPreferences configShard = getSharedPreferences(CHARGING_PROTECT_PREFER_NAME, 0);
		int messagePushSwitch = configShard.getInt(CHARGING_PROTECT_SWITCH, 1);
		mChargingToggleBtn.setCheck(messagePushSwitch ==1 ? true : false);
		mChargingToggleBtn.setOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
                 if(CheckState){
                	 configShard.edit().putInt(CHARGING_PROTECT_SWITCH, 1).commit();
                 }else{
                	 configShard.edit().putInt(CHARGING_PROTECT_SWITCH, 0).commit();
                 }
			}
		});
		
	}
	
	private void sendMessageSwitchBroacast(boolean isOpen) {
		// TODO Auto-generated method stub
		Intent nofityIntent = new Intent(Utils.MESSAGE_PUSH_SWITCH_ACTION);
        nofityIntent.putExtra("isOpen", isOpen);
        sendBroadcast(nofityIntent);
	}
	
	/**
	 * 锟斤拷媒锟藉共锟斤拷
	 */
	private void initViewShare(View view) {
		mPictureShare = (TextView) view.findViewById(R.id.picture_share);
		mMusicShare = (TextView) view.findViewById(R.id.music_share);
		mVideoShare = (TextView) view.findViewById(R.id.video_share);

		// 图片锟斤拷锟斤拷
		mPictureShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_picture = inflater.inflate(
						R.layout.patriarch_control_picture, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_picture);
				initViewPicture(view_picture);
			}
		});

		// 锟斤拷锟街凤拷锟斤拷
		mMusicShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_music = inflater.inflate(
						R.layout.patriarch_control_music, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_music);
				initViewMusic(view_music);
			}
		});

		// 锟斤拷频锟斤拷锟斤拷
		mVideoShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_video = inflater.inflate(
						R.layout.patriarch_control_video, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_video);
				initViewVideo(view_video);
			}
		});
	}

	/**
	 * 图片锟斤拷锟斤拷
	 */
	private void initViewPicture(View view) {
		// 锟斤拷锟�
		mGridViewPictureLeft = (GridView) view
				.findViewById(R.id.gridviewpictureleft);
		mGridViewPictureRight = (GridView) view
				.findViewById(R.id.gridviewpictureright);
		mSharePictureToRight = (TextView) view
				.findViewById(R.id.share_picture_toright);
		mSharePictureToLeft = (TextView) view
				.findViewById(R.id.share_picture_toleft);
		mSharePictureToBack = (ImageView) view
				.findViewById(R.id.share_picture_toback);
		mSharePictureToBack.setBackgroundResource(Utils.getResourcesId(MyApplication
				.getAppContext(), "share_transmission_blue_back_style", "drawable"));
		// 锟饺达拷锟斤拷菁锟斤拷锟�
		showLoadPopupWindow();

		// 锟斤拷取锟斤拷锟斤拷锟斤拷
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					pictureInfos = PictureData
							.getLocalPictureDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 锟斤拷取锟揭憋拷锟斤拷锟�
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PictureData.clearSharePictureList();
					sharePictureInfos = PictureData
							.getLocalSharePictureDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 锟斤拷锟�锟揭憋拷
		mSharePictureToRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<PictureInfo> sharePictures = pictureAdapter
						.getSharePictureData();
				for (PictureInfo sharePicture : sharePictures) {
					ContentValues values = new ContentValues();
					values.put("displayName", sharePicture.getDisplayName());
					values.put("data", sharePicture.getData());
					boolean flag = PictureData.getSharePictureByData(
							PatriarchControlActivity.this,
							sharePicture.getData());
					if (!flag) {
						PictureData.addSharePicture(
								PatriarchControlActivity.this, values);
						sharePictureAdapter.addItem(PictureData.getPictureInfo(
								PatriarchControlActivity.this,
								sharePicture.getData()));
					} else {
						//Utils.showToastMessage(PatriarchControlActivity.this,
						//		sharePicture.getDisplayName() + "锟窖凤拷锟斤拷");
					}
				}
				pictureAdapter.clearSharePictureList();
				sharePictureAdapter.notifyDataSetChanged();
			}
		});

		mSharePictureToLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<PictureInfo> sharePictures = sharePictureAdapter
						.getSharePictureData();
				for (PictureInfo sharePicture : sharePictures) {
					boolean falg = PictureData.delSharePictureData(
							PatriarchControlActivity.this,
							sharePicture.getData());
					if (falg) {
						sharePictureAdapter.delItem(sharePicture);
						pictureAdapter.selectItem(sharePicture.getData());
					} else {
						//Toast.makeText(PatriarchControlActivity.this, "取锟斤拷锟斤拷锟绞э拷锟�,
						//		Toast.LENGTH_LONG).show();
					}
				}
				sharePictureAdapter.clearSharePictureList();
				sharePictureAdapter.notifyDataSetChanged();
				sharePictureAnimAdapter.notifyDataSetChanged();
				pictureAdapter.notifyDataSetChanged();
				pictureAnimAdapter.notifyDataSetChanged();
			}
		});
		mSharePictureToBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_share = inflater.inflate(
						R.layout.patriarch_control_share, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_share);
				initViewShare(view_share);
			}
		});
		
	}

	/**
	 * 锟斤拷锟街凤拷锟斤拷
	 */
	private void initViewMusic(View view) {
		mListViewMusicLeft = (ListView) view
				.findViewById(R.id.listviewmusicleft);
		mListViewMusicRight = (ListView) view
				.findViewById(R.id.listviewmusicright);
		mShareMusicToRight = (TextView) view
				.findViewById(R.id.share_music_toright);
		mShareMusicToLeft = (TextView) view
				.findViewById(R.id.share_music_toleft);
		mShareMusicToBack = (ImageView) view
				.findViewById(R.id.share_music_toback);
		mShareMusicToBack.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
				"share_transmission_blue_back_style", "drawable"));
		// 锟饺达拷锟斤拷菁锟斤拷锟�
		showLoadPopupWindow();

		// 锟斤拷取锟斤拷锟斤拷锟斤拷
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					musicInfos = MusicData
							.getLocalMusicDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(2);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 锟斤拷取锟揭憋拷锟斤拷锟�
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MusicData.clearShareMusicList();
					shareMusicInfos = MusicData
							.getLocalShareMusicDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(3);
				} catch (Exception e) {
				//	Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 锟斤拷锟�锟揭憋拷
		mShareMusicToRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<MusicInfo> musicInfos = musicAdapter.getShareMusicData();
				for (MusicInfo music : musicInfos) {
					ContentValues values = new ContentValues();
					values.put("title", music.getTitle());
					values.put("data", music.getData());
					values.put("duration", music.getDuration());
					boolean flag = MusicData.getShareMusicByData(
							PatriarchControlActivity.this, music.getData());
					if (!flag) {
						MusicData.addShareMusic(PatriarchControlActivity.this,
								values);
						musicShareAdapter.addItem(MusicData.getMusicInfo(
								PatriarchControlActivity.this, music.getData()));
					} else {
						//Utils.showToastMessage(PatriarchControlActivity.this,
						//		music.getTitle() + "锟窖凤拷锟斤拷");
					}
				}
				musicAdapter.clearShareMusicList();
				musicShareAdapter.notifyDataSetChanged();
			}
		});

		// 取锟斤拷锟斤拷锟�
		mShareMusicToLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<MusicInfo> musicInfos = musicShareAdapter
						.getShareMusicData();
				for (MusicInfo music : musicInfos) {
					boolean falg = MusicData.delShareMusicData(
							PatriarchControlActivity.this, music.getData());
					if (falg) {
						musicShareAdapter.delItem(music);
						musicAdapter.selectItem(music.getData());
					} else {
						//Toast.makeText(PatriarchControlActivity.this, "取锟斤拷锟斤拷锟绞э拷锟�,
						//		Toast.LENGTH_LONG).show();
					}
				}
				musicShareAdapter.clearShareMusicList();
				musicShareAdapter.notifyDataSetChanged();
				musicAdapter.notifyDataSetChanged();
			}
		});
		mShareMusicToBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_share = inflater.inflate(
						R.layout.patriarch_control_share, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_share);
				initViewShare(view_share);
			}
		});
	}

	/**
	 * 视频共享
	 */
	private void initViewVideo(View view) {
		// 锟斤拷锟�
		mGridViewVideoLeft = (GridView) view
				.findViewById(R.id.gridviewvideoleft);
		mGridViewVideoRight = (GridView) view
				.findViewById(R.id.gridviewvideoright);
		mShareVideoToRight = (TextView) view
				.findViewById(R.id.share_video_toright);
		mShareVideoToLeft = (TextView) view
				.findViewById(R.id.share_video_toleft);
		mShareVideoToBack = (ImageView) view.findViewById(R.id.share_video_toback);
		mShareVideoToBack.setBackgroundResource(Utils.getResourcesId(MyApplication.getAppContext(),
				"share_transmission_blue_back_style", "drawable"));
		// 等待数据加载
		showLoadPopupWindow();

		// 获取左边数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					videoInfos = VideoData
							.getLocalVideoDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(4);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 获取右边数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					VideoData.clearShareVideoList();
					shareVideoInfos = VideoData
							.getLocalShareVideoDatas(PatriarchControlActivity.this);
					mShareHandler.sendEmptyMessage(5);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
						//	Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		// 锟斤拷锟�锟揭憋拷
		mShareVideoToRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<VideoInfo> shareVideos = videoAdapter.getShareVideoData();
				for (VideoInfo shareVideo : shareVideos) {
					ContentValues values = new ContentValues();
					values.put("title", shareVideo.getTitle());
					values.put("displayName", shareVideo.getDisplayName());
					values.put("data", shareVideo.getData());
					values.put("duration", shareVideo.getDuration());
					boolean flag = VideoData.getShareVideoByData(
							PatriarchControlActivity.this, shareVideo.getData());
					if (!flag) {
						VideoData.addShareVideo(PatriarchControlActivity.this,
								values);
						shareVideoAdapter.addItem(VideoData.getVideoInfo(
								PatriarchControlActivity.this,
								shareVideo.getData()));
					} else {
						//Utils.showToastMessage(PatriarchControlActivity.this,
							//	shareVideo.getTitle() + "锟窖凤拷锟斤拷");
					}
				}
				videoAdapter.clearShareVideoList();
				shareVideoAdapter.notifyDataSetChanged();
			}
		});

		// 取锟斤拷锟斤拷锟�
		mShareVideoToLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				List<VideoInfo> shareVideos = shareVideoAdapter
						.getShareVideoData();
				for (VideoInfo shareVideo : shareVideos) {
					boolean falg = VideoData.delShareVideoData(
							PatriarchControlActivity.this, shareVideo.getData());
					if (falg) {
						shareVideoAdapter.delItem(shareVideo);
						videoAdapter.selectItem(shareVideo.getData());
					} else {
						//Toast.makeText(PatriarchControlActivity.this, "取锟斤拷锟斤拷锟绞э拷锟�,
						//		Toast.LENGTH_LONG).show();
					}
				}
				shareVideoAdapter.clearShareVideoList();
				shareVideoAdapter.notifyDataSetChanged();
				shareVideoAnimAdapter.notifyDataSetChanged();
				videoAdapter.notifyDataSetChanged();
				videoAnimAdapter.notifyDataSetChanged();
			}
		});
		mShareVideoToBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater
						.from(PatriarchControlActivity.this);
				View view_share = inflater.inflate(
						R.layout.patriarch_control_share, null);
				mLayoutRight.removeAllViewsInLayout();
				mLayoutRight.addView(view_share);
				initViewShare(view_share);
			}
		});
	}

	/**
	 * 锟斤拷始锟斤拷APk锟斤拷锟斤拷
	 */
	private void initViewShareApp(View view) {
		mGridViewAllApp = (GridView) findViewById(R.id.gridViewAllApp);
		mGridViewMyApp = (GridView) findViewById(R.id.gridViewMyApp);
		mAllApp = (TextView) view.findViewById(R.id.allapp);
		mMyApp = (TextView) view.findViewById(R.id.myapp);

		// 锟饺达拷锟斤拷菁锟斤拷锟�
		if (mSelection == 20) {
			showLoadPopupWindow();
		} else {
			mSelection = 20;
		}
		// 锟斤拷取系统APP
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					appInfos = AppData
							.getLocalAppDatas(PatriarchControlActivity.this);
					Log.e("size", "size=" + appInfos.size());

					mShareHandler.sendEmptyMessage(6);
				} catch (Exception e) {
					//Toast.makeText(PatriarchControlActivity.this, "锟斤拷锟斤拷锟届常锟斤拷",
					//		Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();

		mAllApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mAllApp.setBackgroundResource(R.drawable.share_apk_title_right_bg);
				mMyApp.setBackgroundResource(R.drawable.share_apk_title_left_bg);
				mGridViewMyApp.setVisibility(View.GONE);
				mGridViewAllApp.setVisibility(View.VISIBLE);

				// 锟饺达拷锟斤拷菁锟斤拷锟�
				//showLoadPopupWindow();

				// 锟斤拷取系统APP
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							appInfos = AppData
									.getLocalAppDatas(PatriarchControlActivity.this);
							mShareHandler.sendEmptyMessage(6);
						} catch (Exception e) {
							//Toast.makeText(PatriarchControlActivity.this,
							//		"锟斤拷锟斤拷锟届常锟斤拷", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		// 锟揭碉拷应锟斤拷
		mMyApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mMyApp.setBackgroundResource(R.drawable.share_apk_title_right_bg);
				mAllApp.setBackgroundResource(R.drawable.share_apk_title_left_bg);
				mGridViewMyApp.setVisibility(View.VISIBLE);
				mGridViewAllApp.setVisibility(View.GONE);
				// 锟斤拷取锟斤拷锟斤拷锟紸PP
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							AppData.clearShareAppList();
							shareAppInfos = AppData
									.getLocalShareAppDatas(PatriarchControlActivity.this);
							mShareHandler.sendEmptyMessage(7);
						} catch (Exception e) {
							//Toast.makeText(PatriarchControlActivity.this,
							//		"锟斤拷锟斤拷锟届常锟斤拷", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	/**
	 * 锟斤拷为统锟斤拷
	 */
	public void initViewBehaviorStatistics(View view) {

	}

	public void onClick(View view) {
		if (canEdit) {
			switch (view.getId()) {

			case R.id.rl_countdown1:

				mRLCountdown1.setVisibility(View.GONE);
				mRLCountdown2.setVisibility(View.VISIBLE);
				isTimeCountdownOK = true;
				if (isTimeOK) {
					mRLTime2.setVisibility(View.GONE);
					mRLTime1.setVisibility(View.VISIBLE);
					isTimeOK = false;
				}
				if (isDateOK) {
					mRLDate2.setVisibility(View.GONE);
					mRLDate1.setVisibility(View.VISIBLE);
					isDateOK = false;
				}
				break;
			case R.id.rl_countdown2:
				mRLCountdown2.setVisibility(View.GONE);
				mRLCountdown1.setVisibility(View.VISIBLE);
				isTimeCountdownOK = false;
				break;
			case R.id.rl_time1:

				mRLTime1.setVisibility(View.GONE);
				mRLTime2.setVisibility(View.VISIBLE);
				isTimeOK = true;
				if (isTimeCountdownOK) {
					mRLCountdown2.setVisibility(View.GONE);
					mRLCountdown1.setVisibility(View.VISIBLE);
					isTimeCountdownOK = false;
				}

				break;
			case R.id.rl_time2:

				mRLTime2.setVisibility(View.GONE);
				mRLTime1.setVisibility(View.VISIBLE);
				isTimeOK = false;

				break;
			case R.id.rl_date1:

				mRLDate1.setVisibility(View.GONE);
				mRLDate2.setVisibility(View.VISIBLE);
				isDateOK = true;
				if (isTimeCountdownOK) {
					mRLCountdown2.setVisibility(View.GONE);
					mRLCountdown1.setVisibility(View.VISIBLE);
					isTimeCountdownOK = false;
				}
				break;
			case R.id.rl_date2:

				mRLDate2.setVisibility(View.GONE);
				mRLDate1.setVisibility(View.VISIBLE);
				isDateOK = false;

				break;
			case R.id.time_countdown_reduce:
				int reduceTimeValue = Integer.parseInt(mTimeCountdownValue
						.getText().toString().trim());
				if (reduceTimeValue >= 30) {
					minute = minute - 10;
				} else {
					minute = 20;
				}
				mTimeCountdownValue.setText("" + minute);
				break;
			case R.id.time_countdown_increase:
				int increaseTimeValue = Integer.parseInt(mTimeCountdownValue
						.getText().toString().trim());
				if (increaseTimeValue <= 110) {
					minute = minute + 10;
				} else {
					minute = 120;
				}
				mTimeCountdownValue.setText("" + minute);
				break;
			case R.id.time_start_hours_add: 
				int startAddHourValue = Integer.parseInt(mTimeStartHour
						.getText().toString().trim());
				int hourEnd = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				String startMinEnd = mTimeStartMin.getText().toString().trim();
				startHour = startAddHourValue;
				if (startAddHourValue != hourEnd) {
					if (startAddHourValue < 23) {
						startHour = startHour + 1;
						endHour = startHour;
						if (startAddHourValue <= 8) {
							mTimeStartHour.setText("0" + startHour);
							mTimeEndHour.setText("0" + startHour);
						} else {
							mTimeStartHour.setText("" + startHour);
							mTimeEndHour.setText("" + startHour);
						}
					} else {
						startHour = 0;
						endHour = startHour;
						mTimeStartHour.setText("0" + startHour);
						mTimeEndHour.setText("0" + startHour);
					}
				} else {
					if (startAddHourValue < 23) {
						startHour = startHour + 1;
						endHour = startHour;
						endMin = Integer.parseInt(startMinEnd);
						if (startAddHourValue <= 8) {
							mTimeStartHour.setText("0" + startHour);
							mTimeEndHour.setText("0" + startHour);
							mTimeEndMin.setText(startMinEnd);
						} else {
							mTimeStartHour.setText("" + startHour);
							mTimeEndHour.setText("" + startHour);
							mTimeEndMin.setText(startMinEnd);
						}
					} else {
						startHour = 0;
						endHour = startHour;
						mTimeStartHour.setText("0" + startHour);
						mTimeEndHour.setText("0" + startHour);
						mTimeEndMin.setText(startMinEnd);
					}
				}
				break;
			case R.id.time_start_min_add: // 锟斤拷始锟斤拷锟斤拷时锟戒（锟接ｏ拷
				int startAddMinValue = Integer.parseInt(mTimeStartMin.getText()
						.toString().trim());
				int hourValueStart = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				int hourValueEnd = Integer.parseInt(mTimeEndHour.getText()
						.toString().trim());
				startMin = startAddMinValue;
				if (hourValueEnd > hourValueStart) {
					if (startAddMinValue < 50) {
						startMin = startMin + 10;
						endMin = startMin;
						mTimeStartMin.setText("" + startMin);
					} else {
						startMin = 0;
						endMin = startMin;
						mTimeStartMin.setText("0" + startMin);
					}
				} else {
					if (startAddMinValue < 50) {
						startMin = startMin + 10;
						endMin = startMin;
						mTimeStartMin.setText("" + startMin);
						mTimeEndMin.setText("" + startMin);
					} else {
						startMin = 0;
						endMin = startMin;
						mTimeStartMin.setText("0" + startMin);
						mTimeEndMin.setText("0" + startMin);
					}
				}
				break;
			case R.id.time_start_hours_subtract: // 锟斤拷始时锟斤拷时锟戒（锟斤拷锟斤拷
				int startSubtractHourValue = Integer.parseInt(mTimeStartHour
						.getText().toString().trim());
				startHour = startSubtractHourValue;
				if (startSubtractHourValue >= 1) {
					startHour = startHour - 1;
					endHour = startHour;
					if (startSubtractHourValue < 11) {
						mTimeStartHour.setText("0" + startHour);
						mTimeEndHour.setText("0" + startHour);
					} else {
						mTimeStartHour.setText("" + startHour);
						mTimeEndHour.setText("" + startHour);
					}
				} else {
					startHour = 23;
					endHour = startHour;
					mTimeStartHour.setText("" + startHour);
					mTimeEndHour.setText("" + startHour);
				}
				break;
			case R.id.time_start_min_subtract:// 锟斤拷始锟斤拷锟斤拷时锟戒（锟斤拷锟斤拷
				int startSubtractMinValue = Integer.parseInt(mTimeStartMin
						.getText().toString().trim());
				int ValueStart = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				int ValueEnd = Integer.parseInt(mTimeEndHour.getText()
						.toString().trim());
				startMin = startSubtractMinValue;
				if (ValueEnd > ValueStart) {
					if (startSubtractMinValue >= 10) {
						startMin = startMin - 10;
						endMin = startMin;
						if (startMin == 0) {
							mTimeStartMin.setText("0" + startMin);
						} else {
							mTimeStartMin.setText("" + startMin);
						}
					} else if (startSubtractMinValue == 0) {
						startMin = 50;
						endMin = startMin;
						mTimeStartMin.setText("" + startMin);
					} else {
						startMin = 0;
						endMin = startMin;
						mTimeStartMin.setText("0" + startMin);
					}
				} else {
					if (startSubtractMinValue >= 10) {
						startMin = startMin - 10;
						endMin = startMin;
						if (startMin == 0) {
							mTimeStartMin.setText("0" + startMin);
							mTimeEndMin.setText("0" + startMin);
						} else {
							mTimeStartMin.setText("" + startMin);
							mTimeEndMin.setText("" + startMin);
						}
					} else if (startSubtractMinValue == 0) {
						startMin = 50;
						endMin = startMin;
						mTimeStartMin.setText("" + startMin);
						mTimeEndMin.setText("" + startMin);
					} else {
						startMin = 0;
						endMin = startMin;
						mTimeStartMin.setText("0" + startMin);
						mTimeEndMin.setText("0" + startMin);
					}
				}
				break;
			case R.id.time_end_hours_add:// 锟斤拷锟斤拷时锟斤拷时锟戒（锟接ｏ拷
				int startAddHour = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				int endAddHourValue = Integer.parseInt(mTimeEndHour.getText()
						.toString().trim());
				endHour = endAddHourValue;
				if (endAddHourValue >= startAddHour) {
					if (endAddHourValue < 23) {
						endHour = endHour + 1;
						if (endAddHourValue <= 8) {
							mTimeEndHour.setText("0" + endHour);
						} else {
							mTimeEndHour.setText("" + endHour);
						}
					} else {
						endHour = 23;
						mTimeEndHour.setText("" + endHour);
					}
				}
				break;
			case R.id.time_end_min_add:// 锟斤拷锟斤拷锟斤拷锟绞憋拷洌拷樱锟�
				int startAddMin = Integer.parseInt(mTimeStartMin.getText()
						.toString().trim());
				int endAddMinValue = Integer.parseInt(mTimeEndMin.getText()
						.toString().trim());
				int StartValue = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				int endValue = Integer.parseInt(mTimeEndHour.getText()
						.toString().trim());
				endMin = endAddMinValue;
				if (StartValue <= endValue) {
					if (StartValue == endValue) {
						if (endAddMinValue >= startAddMin) {
							if (endAddMinValue < 50) {
								endMin = endMin + 10;
								mTimeEndMin.setText("" + endMin);
							} else {
								endMin = 50;
								mTimeEndMin.setText("" + endMin);
							}
						}
					} else {
						if (endAddMinValue < 50) {
							endMin = endMin + 10;
							mTimeEndMin.setText("" + endMin);
						} else {
							endMin = 0;
							mTimeEndMin.setText("0" + endMin);
						}
					}
				}
				break;
			case R.id.time_end_hours_subtract: // 锟斤拷锟斤拷时锟斤拷时锟戒（锟斤拷锟斤拷
				int endSubtractHourValue = Integer.parseInt(mTimeEndHour
						.getText().toString().trim());
				int hourValue = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				endHour = endSubtractHourValue;
				if (endSubtractHourValue > hourValue) {
					int hours = endSubtractHourValue - hourValue;
					if (hours == 1) {
						int minStart = Integer.parseInt(mTimeStartMin.getText()
								.toString().trim());
						int minEnd = Integer.parseInt(mTimeEndMin.getText()
								.toString().trim());
						if (minEnd > minStart) {
							if (endSubtractHourValue >= 1) {
								endHour = endHour - 1;
								if (endSubtractHourValue < 11) {
									mTimeEndHour.setText("0" + endHour);
								} else {
									mTimeEndHour.setText("" + endHour);
								}
							} else {
								endHour = 23;
								mTimeEndHour.setText("" + endHour);
							}
						}
					} else {
						if (endSubtractHourValue >= 1) {
							endHour = endHour - 1;
							if (endSubtractHourValue < 11) {
								mTimeEndHour.setText("0" + endHour);
							} else {
								mTimeEndHour.setText("" + endHour);
							}
						} else {
							endHour = 23;
							mTimeEndHour.setText("" + endHour);
						}
					}
				}
				break;
			case R.id.time_end_min_subtract: // 锟斤拷锟斤拷锟斤拷锟绞憋拷洌拷锟斤拷锟�
				int startSubtractMin = Integer.parseInt(mTimeStartMin.getText()
						.toString().trim());
				int endSubtractMinValue = Integer.parseInt(mTimeEndMin
						.getText().toString().trim());
				int hourStartValue = Integer.parseInt(mTimeStartHour.getText()
						.toString().trim());
				int hourEndValue = Integer.parseInt(mTimeEndHour.getText()
						.toString().trim());
				endMin = endSubtractMinValue;
				if (hourEndValue >= hourStartValue) {
					if (hourEndValue == hourStartValue) {
						if (endSubtractMinValue > startSubtractMin
								& endSubtractMinValue > 10) {
							endMin = endMin - 10;
							mTimeEndMin.setText("" + endMin);
						} else {
							if (endSubtractMinValue == 0
									&& startSubtractMin == 0) {
								if (endSubtractMinValue >= 10) {
									endMin = endMin - 10;
									if (endMin == 0) {
										mTimeEndMin.setText("0" + endMin);
									} else {
										mTimeEndMin.setText("" + endMin);
									}
								} else if (endSubtractMinValue == 0) {
									endMin = 50;
									mTimeEndMin.setText("" + endMin);
								} else {
									endMin = 0;
									mTimeEndMin.setText("0" + endMin);
								}
							} else {
								if (endMin == 0) {
									mTimeEndMin.setText("0" + endMin);
								} else {
									mTimeEndMin.setText("" + endMin);
								}
							}
						}
					} else {
						if (endSubtractMinValue >= 10) {
							endMin = endMin - 10;
							if (endMin == 0) {
								mTimeEndMin.setText("0" + endMin);
							} else {
								mTimeEndMin.setText("" + endMin);
							}
						} else if (endSubtractMinValue == 0) {
							endMin = 50;
							mTimeEndMin.setText("" + endMin);
						} else {
							endMin = 0;
							mTimeEndMin.setText("0" + endMin);
						}
					}
				}
				break;
			case R.id.time_date_mon:
				int mon = preferences.getInt("mon", 0);
				if (mon == 0) {
					preferences.edit().putInt("mon", 1).commit();
					mTimeDateMon
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("mon", 0).commit();
					mTimeDateMon
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_tues:
				int tues = preferences.getInt("tues", 0);
				if (tues == 0) {
					preferences.edit().putInt("tues", 1).commit();
					mTimeDateTues
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("tues", 0).commit();
					mTimeDateTues
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_wed:
				int wed = preferences.getInt("wed", 0);
				if (wed == 0) {
					preferences.edit().putInt("wed", 1).commit();
					mTimeDateWed
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("wed", 0).commit();
					mTimeDateWed
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_thur:
				int thur = preferences.getInt("thur", 0);
				if (thur == 0) {
					preferences.edit().putInt("thur", 1).commit();
					mTimeDateThur
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("thur", 0).commit();
					mTimeDateThur
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_fri:
				int fri = preferences.getInt("fri", 0);
				if (fri == 0) {
					preferences.edit().putInt("fri", 1).commit();
					mTimeDateFri
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("fri", 0).commit();
					mTimeDateFri
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_sta:
				int sta = preferences.getInt("sta", 0);
				if (sta == 0) {
					preferences.edit().putInt("sta", 1).commit();
					mTimeDateSta
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("sta", 0).commit();
					mTimeDateSta
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.time_date_sun:
				int sun = preferences.getInt("sun", 0);
				if (sun == 0) {
					preferences.edit().putInt("sun", 1).commit();
					mTimeDateSun
							.setBackgroundResource(R.drawable.time_date_click);
				} else {
					preferences.edit().putInt("sun", 0).commit();
					mTimeDateSun
							.setBackgroundResource(R.drawable.time_date_noclick);
				}
				break;
			case R.id.manager_confirm:

				break;

			case R.id.parent_mode:// 锟揭筹拷模式
				if (isOnClickl) {
					//mLayoutRight.removeAllViewsInLayout();
					mLayoutRight.removeAllViews();
					showParentModePopupWindow();
				}
			default:
				break;

			}
		}
	}

	private void setCountDownTime() {
		int time = Integer.parseInt(mTimeCountdownValue.getText().toString()
				.trim()) * 60;
		preferences.edit().putInt("countdown", time).commit();
		preferences
				.edit()
				.putInt("time",
						Integer.parseInt(mTimeCountdownValue.getText()
								.toString().trim())).commit();
		preferences
				.edit()
				.putInt("old_time",
						Integer.parseInt(mTimeCountdownValue.getText()
								.toString().trim())).commit();
		// preferences.edit().putInt("countdownState", 1).commit();
		// preferences.edit().putInt("quantumState", 0).commit();
		// preferences.edit().putInt("dateTimeState", 0).commit();
		service.onStartTimeQuantum(false, PatriarchControlActivity.this);
		service.onStartTime(true, PatriarchControlActivity.this);
		Log.e(TAG, "Time is running");
	}

	// }

	private void setQuantum() {
		String startHourValue = mTimeStartHour.getText().toString().trim();
		String startMinValue = mTimeStartMin.getText().toString().trim();
		String endHourValue = mTimeEndHour.getText().toString().trim();
		String endMinValue = mTimeEndMin.getText().toString().trim();
		String startTime = startHourValue + ":" + startMinValue;
		String endTime = endHourValue + ":" + endMinValue;
		preferences.edit().putString("startTime", startTime).commit();
		preferences.edit().putString("endTime", endTime).commit();
		Log.e("zmh", preferences.getString("startTime","")+preferences.getString("endTime",""));
		// int quantumState = preferences.getInt("quantumState", 0);
		// if (quantumState == 1) {
		// preferences.edit().putInt("quantumState", 0).commit();
		// } else {
		// preferences.edit().putInt("quantumState", 1).commit();
		// preferences.edit().putInt("dateTimeState", 0).commit();
		// preferences.edit().putInt("countdownState", 0).commit();
		service.onStartTime(false, PatriarchControlActivity.this);
		service.onStartTimeQuantum(true, PatriarchControlActivity.this);
	}

	// }

	private void setDate() {
		// int dateTimeState = preferences.getInt("dateTimeState", 0);
		// if (dateTimeState == 1) {
		// preferences.edit().putInt("dateTimeState", 0).commit();
		// preferences.edit().putInt("mon", 0).commit();
		// preferences.edit().putInt("tues", 0).commit();
		// preferences.edit().putInt("wed", 0).commit();
		// preferences.edit().putInt("thur", 0).commit();
		// preferences.edit().putInt("fri", 0).commit();
		// preferences.edit().putInt("sta", 0).commit();
		// preferences.edit().putInt("sun", 0).commit();
		// } else {
		// preferences.edit().putInt("dateTimeState", 1).commit();
		// preferences.edit().putInt("countdown", 0).commit();
		service.onStartTime(false, PatriarchControlActivity.this);
		service.onStartTimeQuantum(true, PatriarchControlActivity.this);
	}

	// }
	private void resetWeekBG() {
		preferences.edit().putInt("mon", 0).commit();
		preferences.edit().putInt("tues", 0).commit();
		preferences.edit().putInt("wed", 0).commit();
		preferences.edit().putInt("thur", 0).commit();
		preferences.edit().putInt("fri", 0).commit();
		preferences.edit().putInt("sta", 0).commit();
		preferences.edit().putInt("sun", 0).commit();
	}

	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 锟斤拷锟斤拷
			mParentalcontrolMainBack
					.setImageResource(R.drawable.parentalcontrol_back);
			if (isToMainActivity) {
//				Intent intent = new Intent(PatriarchControlActivity.this,
//						MainActivity.class);
//				startActivity(intent);
				finish();
			} else {
				isToMainActivity = true;
				finish();
			}

			break;
		case MotionEvent.ACTION_UP:// 抬锟斤拷
			mParentalcontrolMainBack
					.setImageResource(R.drawable.parentalcontrol_back_on);
			break;
		default:
			break;
		}
		return true;
	}

	// 选锟斤拷选锟斤拷
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		LayoutInflater inflater = LayoutInflater
				.from(PatriarchControlActivity.this);
		Utils.CloseKeyBoard(PatriarchControlActivity.this, mETHideSoftInput);
		switch (checkedId) {
		case R.id.time_control: // 时锟斤拷锟斤拷锟�
			View view_time = inflater.inflate(R.layout.patriarch_control_time,
					null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_time);
			initViewTime(view_time);
			isOnClickl = false;
			break;
		case R.id.behavior_statistics: // 锟斤拷为统锟斤拷
			View view_behavior_statistics = inflater.inflate(
					R.layout.patriarch_control_behavior_statistics, null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_behavior_statistics);
			initViewBehaviorStatistics(view_behavior_statistics);
			isOnClickl = false;
			break;
		case R.id.mediashare: // 锟斤拷媒锟藉共锟斤拷
			View view_share = inflater.inflate(
					R.layout.patriarch_control_share, null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_share);
			initViewShare(view_share);
			isOnClickl = false;
			break;
		case R.id.app_management: // app锟斤拷锟斤拷
			View view_app = inflater.inflate(R.layout.patriarch_control_app,
					null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_app);
			initViewShareApp(view_app);
			isOnClickl = false;
			break;
		case R.id.browse_web_management: // 锟斤拷锟斤拷锟斤拷锟斤拷锟�
			View view_web = inflater.inflate(R.layout.patriarch_control_web,
					null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_web);
			initViewWeb(view_web);
			isOnClickl = false;
			break;
		case R.id.shop: // 锟教碉拷
			View view_shop = inflater.inflate(R.layout.patriarch_control_shop,
					null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_shop);
			initShopView(view_shop);
			isOnClickl = false;
			break;
		case R.id.charging_protect: // 锟斤拷息锟斤拷锟斤拷
			View view_charging_protect = inflater.inflate(
					R.layout.patriarch_control_charging_protect, null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_charging_protect);
			initViewChargingProtect(view_charging_protect);
			isOnClickl = false;
			break;
		case R.id.push_message: // 锟斤拷息锟斤拷锟斤拷
			View view_push_message = inflater.inflate(
					R.layout.patriarch_control_push_essage, null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_push_message);
			initViewPushMessage(view_push_message);
			isOnClickl = false;
			break;
		case R.id.parent_mode: // 锟揭筹拷模式
			//mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.removeAllViews();
			if (mSelection == 20) {
				showParentModePopupWindow();
			} else {
				mSelection = 20;
			}
			break;
		case R.id.password_manager: // 锟斤拷锟斤拷锟斤拷锟�
			View view_password = inflater.inflate(
					R.layout.patriarch_control_passwordmanager_popupwindow,
					null);
			mLayoutRight.removeAllViewsInLayout();
			mLayoutRight.addView(view_password);
			initViewPassword(view_password);
			isOnClickl = false;
			break;
		default:
			break;
		}
	}

	/**
	 * 锟揭筹拷模式
	 */
	private void showParentModePopupWindow() {
		mPopupWindow = (RelativeLayout) LayoutInflater.from(
				PatriarchControlActivity.this).inflate(
				R.layout.patriarch_control_parentmode, null);

		mParentModePopupWindow = new PopupWindow(PatriarchControlActivity.this);
		mParentModePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mParentModePopupWindow.setWidth(407);
		mParentModePopupWindow.setHeight(225);
		mParentModePopupWindow.setOutsideTouchable(false);
		mParentModePopupWindow.setFocusable(true);
		mParentModePopupWindow.setContentView(mPopupWindow);
		mParentModePopupWindow.showAtLocation(
				findViewById(R.id.parentalcontrol_main_right), Gravity.CENTER,
				140, 0);// 锟斤拷示锟斤拷位锟斤拷为:锟斤拷锟斤拷诟锟斤拷丶锟斤拷锟斤拷锟�

		mParentModelCancel = (TextView) mPopupWindow
				.findViewById(R.id.parent_model_cancel);
		mParentModelConfirm = (TextView) mPopupWindow
				.findViewById(R.id.parent_model_confirm);

		// 取锟斤拷
		mParentModelCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mParentModePopupWindow != null) {
					mParentModePopupWindow.dismiss();
					mParentModePopupWindow = null;
				}
				isOnClickl = true;
			}
		});

		// 确锟斤拷
		mParentModelConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				HttpCommon.hideSystemUI(PatriarchControlActivity.this, false);
				Intent intent = new Intent();
				intent.setAction("com.tipe.ui.exist");
				launchAndroid();
				startLaunch();
				sendBroadcast(intent);
				if (mParentModePopupWindow != null) {
					mParentModePopupWindow.dismiss();
					mParentModePopupWindow = null;
				}
				finish();
			}
		});

		// 锟截憋拷
		mParentModePopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mParentModePopupWindow != null) {
					mParentModePopupWindow.dismiss();
					mParentModePopupWindow = null;
				}
				isOnClickl = true;
			}
		});
	}

	private void launchAndroid() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ComponentName cn = new ComponentName("com.android.launcher",
				"com.android.launcher2.Launcher");
		intent.setComponent(cn);
		this.startActivity(intent);
	}

	private void startLaunch() {
		HomeSettings homeSetiings = new HomeSettings(this);
		homeSetiings.changeHomeToLauncher();
	}

	private View mShareLayout;

	/**
	 * 锟斤拷锟斤拷锟斤拷
	 */
	private void showLoadPopupWindow() {
		mHandler.postDelayed(mLoadPopupWindowRunnable, 1000);
	}
	
	private Runnable mLoadPopupWindowRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mShareLayout = (RelativeLayout) LayoutInflater.from(
					PatriarchControlActivity.this).inflate(R.layout.shara_loading,
					null);
			if (mSharePopupWindow != null) {
				mSharePopupWindow.dismiss();
				mSharePopupWindow = null;
			}
			mSharePopupWindow = new PopupWindow(PatriarchControlActivity.this);
			mSharePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
			mSharePopupWindow.setWidth(1024);
			mSharePopupWindow.setHeight(600);
			mSharePopupWindow.setContentView(mShareLayout);
			mSharePopupWindow.setOutsideTouchable(false);
			mSharePopupWindow.setFocusable(true);
			mSharePopupWindow.showAtLocation(
					findViewById(R.id.patriarch_popupWindow), Gravity.CENTER, 0, 0);//
			// 锟斤拷示锟斤拷位锟斤拷为:锟斤拷锟斤拷诟锟斤拷丶锟斤拷锟斤拷锟�
		}
	};
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(mUninstallReceiver);
		super.onDestroy();
	}

	// 锟斤拷锟斤拷APP锟角凤拷卸锟截成癸拷锟斤拷锟缴癸拷锟斤拷锟斤拷锟斤拷锟斤拷
	private class UninstallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (uninstallPackage != null
					&& ("package:" + uninstallPackage).equals(intent
							.getDataString())) {
				Log.i(TAG, "UninstallReceiver--------appAdapter----->>>>>>>>");
				appAdapter.delItem(uninstallPackage);
				appAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * MEDIA锟斤拷锟斤拷锟斤拷锟�
	 */
	@SuppressLint("HandlerLeak")
	private Handler mShareHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // 图片锟斤拷呒锟斤拷锟�
				mHandler.removeCallbacks(mLoadPopupWindowRunnable);
				pictureAdapter = new GridPictureAdapter(
						PatriarchControlActivity.this, pictureInfos, mHandler);
				pictureAnimAdapter = new AlphaInAnimationAdapter(pictureAdapter);
				pictureAnimAdapter.setAbsListView(mGridViewPictureLeft);
				mGridViewPictureLeft.setAdapter(pictureAnimAdapter);
				if (mSharePopupWindow != null) {
					mSharePopupWindow.dismiss();
					mSharePopupWindow = null;
				}
				break;
			case 1: // 图片锟揭边硷拷锟斤拷
				sharePictureAdapter = new GridSharePictureAdapter(
						PatriarchControlActivity.this, sharePictureInfos);
				sharePictureAnimAdapter = new AlphaInAnimationAdapter(
						sharePictureAdapter);
				sharePictureAnimAdapter.setAbsListView(mGridViewPictureRight);
				mGridViewPictureRight.setAdapter(sharePictureAnimAdapter);
				break;
			case 2:// 锟斤拷锟斤拷锟斤拷呒锟斤拷锟�
				mHandler.removeCallbacks(mLoadPopupWindowRunnable);
				musicAdapter = new MusicAdapter(PatriarchControlActivity.this,
						musicInfos, mHandler);
				mListViewMusicLeft.setAdapter(musicAdapter);
				if (mSharePopupWindow != null) {
					mSharePopupWindow.dismiss();
					mSharePopupWindow = null;
				}
				break;
			case 3:// 锟斤拷锟斤拷锟揭边硷拷锟斤拷
				musicShareAdapter = new MusicShareAdapter(
						PatriarchControlActivity.this, shareMusicInfos);
				mListViewMusicRight.setAdapter(musicShareAdapter);
				break;
			case 4: // 锟斤拷频锟斤拷呒锟斤拷锟�
				mHandler.removeCallbacks(mLoadPopupWindowRunnable);
				videoAdapter = new GridVideoAdapter(
						PatriarchControlActivity.this, videoInfos, mHandler);
				videoAnimAdapter = new AlphaInAnimationAdapter(videoAdapter);
				videoAnimAdapter.setAbsListView(mGridViewVideoLeft);
				mGridViewVideoLeft.setAdapter(videoAnimAdapter);
				if (mSharePopupWindow != null) {
					mSharePopupWindow.dismiss();
					mSharePopupWindow = null;
				}
				break;
			case 5: // 锟斤拷频锟揭边硷拷锟斤拷
				shareVideoAdapter = new GridShareVideoAdapter(
						PatriarchControlActivity.this, shareVideoInfos);
				shareVideoAnimAdapter = new AlphaInAnimationAdapter(
						shareVideoAdapter);
				shareVideoAnimAdapter.setAbsListView(mGridViewVideoRight);
				mGridViewVideoRight.setAdapter(shareVideoAnimAdapter);
				break;
			case 6:
				mHandler.removeCallbacks(mLoadPopupWindowRunnable);
				appAdapter = new GridAppAdapter(PatriarchControlActivity.this,
						appInfos, mHandler);
				mGridViewAllApp.setAdapter(appAdapter);
				if (mSharePopupWindow != null) {
					mSharePopupWindow.dismiss();
					mSharePopupWindow = null;
				}
				break;
			case 7:
				shareAppAdapter = new GridShareAppAdapter(
						PatriarchControlActivity.this, shareAppInfos);
				mGridViewMyApp.setAdapter(shareAppAdapter);
				appAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Monitor screen click event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// TODO Auto-generated method stub
		// if (mEditTextName != null) {
		// Utils.CloseKeyBoard(PatriarchControlActivity.this, mEditTextName);
		// }
		// if (mChangeManagerPassword != null) {
		// Utils.CloseKeyBoard(PatriarchControlActivity.this,
		// mChangeManagerPassword);
		// }
		Utils.CloseKeyBoard(PatriarchControlActivity.this, mETHideSoftInput);
		return super.onTouchEvent(event);
	}
	
}