package com.egreat.adlauncher.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.egreat.adlauncher.activity.PeopleLiveActivity.GetPeopleLiveThread;
import com.egreat.adlauncher.adapter.MarqueeAdapter;
import com.egreat.adlauncher.adapter.ShortcutAppAdapter;
import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.db.EpgDBDao;
import com.egreat.adlauncher.effect.ABaseTransformer;
import com.egreat.adlauncher.effect.AccordionTransformer;
import com.egreat.adlauncher.effect.BackgroundToForegroundTransformer;
import com.egreat.adlauncher.effect.CubeInTransformer;
import com.egreat.adlauncher.effect.CubeOutTransformer;
import com.egreat.adlauncher.effect.DefaultTransformer;
import com.egreat.adlauncher.effect.DepthPageTransformer;
import com.egreat.adlauncher.effect.FlipHorizontalTransformer;
import com.egreat.adlauncher.effect.FlipVerticalTransformer;
import com.egreat.adlauncher.effect.ForegroundToBackgroundTransformer;
import com.egreat.adlauncher.effect.RotateDownTransformer;
import com.egreat.adlauncher.effect.RotateUpTransformer;
import com.egreat.adlauncher.effect.StackTransformer;
import com.egreat.adlauncher.effect.TabletTransformer;
import com.egreat.adlauncher.effect.ViewPagerScroller;
import com.egreat.adlauncher.effect.ZoomInTransformer;
import com.egreat.adlauncher.effect.ZoomOutSlideTransformer;
import com.egreat.adlauncher.effect.ZoomOutTranformer;
import com.egreat.adlauncher.entity.HomePageCategory;
import com.egreat.adlauncher.entity.MemoryInfo;
import com.egreat.adlauncher.util.AppTool;
import com.egreat.adlauncher.util.ScrollTextView;
import com.egreat.adlauncher.util.SettingUtils;
import com.egreat.adlauncher.util.Utils;
import com.egreat.adlauncher.util.WeatherUtil;
import com.egreat.adlauncher.view.MenuPopupWindow;
import com.egreat.devicemanger.DeviceManager;
import com.egreat.devicemanger.LoginStatus;
import com.egreat.devicemanger.UpdateInfo;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.mgle.launcher.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener,
		OnFocusChangeListener, OnKeyListener {

	public static final String LOGTAG = "MainActivity";

	public Context mContext = null;
	DeviceManager mDeviceManager = null;
	UpdateInfo mUpdateInfo = null;
    
	private ShortcutAppAdapter mShortcutAdapter;
	private ApkInfo mCurrentShortcutApkInfo;
	
	private RelativeLayout mMainLayout;
	private ViewPager mViewPager;
	private ABaseTransformer mTransformer = null;
	private ViewPagerScroller mScroller = null;
	
	private int mCurrentIndex = 5;
	public FrameLayout mBtnApp_1 = null;
	public RelativeLayout mBtnApp_2 = null;
	public RelativeLayout mBtnApp_3 = null;
	public RelativeLayout mBtnApp_4 = null;
	public RelativeLayout mBtnApp_5 = null;
	public RelativeLayout mBtnApp_6 = null;
	public RelativeLayout mBtnApp_7 = null;
	public RelativeLayout mBtnApp_8 = null;
	public RelativeLayout mBtnApp_9 = null;

	public ImageView mLogo =null;
	
	public ImageView mImageDown = null;
	public ImageView mImage_1 = null;
	public ImageView mImage_2 = null;
	public ImageView mImage_3 = null;
	public ImageView mImage_4 = null;
	public ImageView mImage_5 = null;
	public RelativeLayout mImage_6 = null;
	public RelativeLayout mImage_7 = null;
	public ImageView mImage_8 = null;
	public ImageView mImage_9 = null;

	public ImageView mIcon_6 = null;
	public ImageView mIcon_7 = null;

	public ImageView mIconUsb = null;
	public ImageView mIconWeather = null;
	public ImageView mIvWifi = null;
	public ImageView mIvEthernet = null;

	public TextView mTitle_6 = null;
	public TextView mTitle_7 = null;

	public TextView mTime = null;

	ScrollTextView mRollingText = null;

	GetMemberThread mGetMemberThread;
	
	GetDownLoadThread mDownloadThread;
	
	private boolean mDownloadIsError =false;
	
	LoginThread mLoginThread = null;

	GetEPGThread mGetEPGThread = null;

	GetUpdateThread mGetUpdateThread = null;

	EpgDBDao mEpgDBDao = null;

	int apk_sum = 0;

	List<ApkInfo> mApkList = null;
	List<String> mPosterPathList = null;
	List<ApkInfo> mShortcutApkList = null;
	ProgressDialog progressDialog;

	String mAppStr_6 = null;
	String mAppStr_7 = null;

	PackageManager mPackageManager = null;

	WeatherUtil mWeatherUtil = null;
	boolean isWeatherOK = false;

	boolean isAddUser1 = false;
	boolean isAddUser2 = false;
	
	private SharedPreferences mPrefs;
	private SharedPreferences mUpdatePrefs;
	private SharedPreferences mTempUpdatePrefs;
	
	private int mCurrentFocusId;

	protected int mIndex = 0;
	private DisplayMetrics mDm;

	private TextView mMoneyTotal;
	private boolean mUpdateing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this;
		mCurrentFocusId =-1;
		mDm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDm);
        
		mEpgDBDao = new EpgDBDao(mContext);

		apk_sum = mEpgDBDao.getAppCount();
		Log.d(LOGTAG, "apk_sum=" + apk_sum);
		mApkList = mEpgDBDao.getAppList();

		File file = getFilesDir();
		String path = file.getAbsolutePath();
		mPosterPathList = new ArrayList<String>();
		for (int i = 0; i < 9; i++) {
			mPosterPathList.add(path + "/" + (i + 1) + ".png");
		}

		mPackageManager = getPackageManager();

		mDeviceManager = DeviceManager.getInstance(mContext);

		mRollingText = (ScrollTextView) findViewById(R.id.rolling_text);
		setRollingText("");
 
		mMainLayout = (RelativeLayout) findViewById(R.id.main);
		
		mMoneyTotal = (TextView)findViewById(R.id.txt_money_total);
		
		mMainHandler.removeMessages(DEVICE_GET_EPG_INFO);
		mMainHandler.sendEmptyMessage(DEVICE_GET_EPG_INFO);
		mMainHandler.removeMessages(DEVICE_MEMBER_INFO);
		mMainHandler.sendEmptyMessageDelayed(DEVICE_MEMBER_INFO, 1 * 1000);
		
		mBtnApp_1 = (FrameLayout) findViewById(R.id.button_1);
		mBtnApp_2 = (RelativeLayout) findViewById(R.id.button_2);
		mBtnApp_3 = (RelativeLayout) findViewById(R.id.button_3);
		mBtnApp_4 = (RelativeLayout) findViewById(R.id.button_4);
		mBtnApp_5 = (RelativeLayout) findViewById(R.id.button_5);
		mBtnApp_6 = (RelativeLayout) findViewById(R.id.button_6);
		mBtnApp_7 = (RelativeLayout) findViewById(R.id.button_7);
		mBtnApp_8 = (RelativeLayout) findViewById(R.id.button_8);
		mBtnApp_9 = (RelativeLayout) findViewById(R.id.button_9);

		mImageDown = (ImageView) findViewById(R.id.image_btn_down);
		
		//add t
		mLogo = (ImageView) findViewById(R.id.logo);
		
		mImage_1 = (ImageView) findViewById(R.id.image_1);
		mImage_2 = (ImageView) findViewById(R.id.image_2);
		mImage_3 = (ImageView) findViewById(R.id.image_3);
		mImage_4 = (ImageView) findViewById(R.id.image_4);
		mImage_5 = (ImageView) findViewById(R.id.image_5);
		mImage_6 = (RelativeLayout) findViewById(R.id.image_6);
		mImage_7 = (RelativeLayout) findViewById(R.id.image_7);
		mImage_8 = (ImageView) findViewById(R.id.image_8);
		mImage_9 = (ImageView) findViewById(R.id.image_9);

		mIcon_6 = (ImageView) findViewById(R.id.icon_6);
		mIcon_7 = (ImageView) findViewById(R.id.icon_7);

		mIvWifi = (ImageView) findViewById(R.id.icon_wifi);
		mIvEthernet = (ImageView) findViewById(R.id.icon_ethernet);
		mIconWeather = (ImageView) findViewById(R.id.icon_weather);
		mIconUsb = (ImageView) findViewById(R.id.icon_usb);

		mTitle_6 = (TextView) findViewById(R.id.title_6);
		mTitle_7 = (TextView) findViewById(R.id.title_7);

		mTime = (TextView) findViewById(R.id.tv_time);

		mBtnApp_1.setOnClickListener(this);
		mBtnApp_2.setOnClickListener(this);
		mBtnApp_3.setOnClickListener(this);
		mBtnApp_4.setOnClickListener(this);
		mBtnApp_5.setOnClickListener(this);
		mBtnApp_6.setOnClickListener(this);
		mBtnApp_7.setOnClickListener(this);
		mBtnApp_8.setOnClickListener(this);
		mBtnApp_9.setOnClickListener(this);

		mBtnApp_1.setOnFocusChangeListener(this);
		mBtnApp_2.setOnFocusChangeListener(this);
		mBtnApp_3.setOnFocusChangeListener(this);
		mBtnApp_4.setOnFocusChangeListener(this);
		mBtnApp_5.setOnFocusChangeListener(this);
		mBtnApp_6.setOnFocusChangeListener(this);
		mBtnApp_7.setOnFocusChangeListener(this);
		mBtnApp_8.setOnFocusChangeListener(this);
		mBtnApp_9.setOnFocusChangeListener(this);
		mBtnApp_1.requestFocus();

		updateView();
		registerBroadcast();
		
		mPrefs = getSharedPreferences("egreat_setup", 0);
		mUpdatePrefs = getSharedPreferences("update_config", 0);
		mTempUpdatePrefs =getSharedPreferences("update_temp_config", 0);
		
		boolean showGuide = mPrefs.getBoolean("show_guide", true);
		if(showGuide){
			mMainHandler.removeMessages(SHOW_GUIDE_WINDOW);
			mMainHandler.sendEmptyMessageDelayed(SHOW_GUIDE_WINDOW, 500);
		} 
		
		boolean isFirst =mPrefs.getBoolean("is_first", true);
		if(isFirst){
			SettingUtils.setApp01PackageName(mContext,  Utils.SHAFA_MARKET);
			SettingUtils.setApp02PackageName(mContext,  Utils.SHAFA_MARKET);
			mPrefs.edit().putBoolean("is_first", false).commit();
		}
	}

	private void initGuidView() {

		ImageView imgView = null;
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		for (int i = 0; i < Utils.GUIDE_DATA.length; i++) {
			imgView = new ImageView(mContext);
			imgView.setScaleType(ScaleType.FIT_XY);
			imgView.setBackgroundResource(Utils.GUIDE_DATA[i]);
			views.add(imgView);
		}

		MarqueeAdapter marquee = new MarqueeAdapter();
		marquee.setData(views);
		mScroller.initViewPagerScroll(mViewPager);
		mScroller.setScrollDuration(1500);
		mViewPager.setAdapter(marquee);
		//mViewPager.setCurrentItem(mIndex);
	}
	
	public void setScrollerAnimation() {

		int index = 3;
		// 随机0~13
		index = -1; //(int) Math.round(Math.random() * 15);
		Log.e(LOGTAG, "index=" + index);
		switch (index) {
		case 0:
			mTransformer = new AccordionTransformer();
			break;
		case 1:
			mTransformer = new BackgroundToForegroundTransformer();
			break;
		case 2:
			mTransformer = new CubeInTransformer();
			break;
		case 3:
			mTransformer = new CubeOutTransformer();
			break;
		case 4:
			mTransformer = new DepthPageTransformer();
			break;
		case 5:
			mTransformer = new FlipHorizontalTransformer();
			break;
		case 6:
			mTransformer = new FlipVerticalTransformer();
			break;
		case 7:
			mTransformer = new ForegroundToBackgroundTransformer();
			break;
		case 8:
			mTransformer = new RotateDownTransformer();
			break;
		case 9:
			mTransformer = new RotateUpTransformer();
			break;
		case 10:
			mTransformer = new StackTransformer();
			break;
		case 11:
			mTransformer = new TabletTransformer();
			break;
		case 12:
			mTransformer = new ZoomInTransformer();
			break;
		case 13:
			mTransformer = new ZoomOutSlideTransformer();
			break;
		case 14:
			mTransformer = new ZoomOutTranformer();
			break;
		default:
			mTransformer = new DefaultTransformer();
			break;
		}

	}
	
	public void updateView() {
		for (int i = 0; i < 9; i++) {
			updateView(i);
		}
		updateUserAPKView();
	}

	@Override
	protected void onResume() {
		updateUserAPKView();
		mMainHandler.removeMessages(UPDATE_TIME);
		mMainHandler.sendEmptyMessageDelayed(UPDATE_TIME, 10000);
		isAddUser1 = false;
		isAddUser2 = false;

		mMainHandler.removeMessages(DEVICE_GET_EPG_INFO);
		mMainHandler.sendEmptyMessage(DEVICE_GET_EPG_INFO);
		mMainHandler.removeMessages(DEVICE_MEMBER_INFO);
		mMainHandler.sendEmptyMessageDelayed(DEVICE_MEMBER_INFO, 1 * 1000);
		
		super.onResume();

	}

	private void initGuidePopWindow() {
		onFocusChange(mBtnApp_1, false);
		View popView = LayoutInflater.from(mContext).inflate(
				R.layout.popup_guide_layout, null);
		final PopupWindow popupWindow = new PopupWindow(popView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewPager = (ViewPager) popView.findViewById(R.id.view_pager);
		mScroller = new ViewPagerScroller(mContext);
		setScrollerAnimation();
		
		initGuidView();
		
        final TextView start = (TextView)popView.findViewById(R.id.txt_start);
        final TextView startNoGuide = (TextView)popView.findViewById(R.id.txt_start_no_guide);
        //final FrameLayout guidelMain = (FrameLayout)popView.findViewById(R.id.guide_main);
        
        start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 onFocusChange(mBtnApp_1, true);
				 popupWindow.dismiss();
			}
		});
        
        start.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent key) {
				if (key.getRepeatCount() == 0
						&& key.getAction() == KeyEvent.ACTION_UP) {
					if (key.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT){
						start.setVisibility(View.GONE);
						startNoGuide.setVisibility(View.GONE);
						
						mViewPager.setFocusable(true);
						mIndex--;
						//guidelMain.setBackgroundResource(Utils.GUIDE_DATA[mIndex]);
						mViewPager.setCurrentItem(mIndex);
						mViewPager.setPageTransformer(true, mTransformer);
					}
				}
				return false;
			}
		});
        
        startNoGuide.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent key) {
				if (key.getRepeatCount() == 0
						&& key.getAction() == KeyEvent.ACTION_UP) {
					if (key.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT){
						start.setVisibility(View.GONE);
						startNoGuide.setVisibility(View.GONE);
						
						mViewPager.setFocusable(true);
						mIndex--;
						//guidelMain.setBackgroundResource(Utils.GUIDE_DATA[mIndex]);
						mViewPager.setCurrentItem(mIndex);
						mViewPager.setPageTransformer(true, mTransformer);
					}
				}
				return false;
			}
		});
        
        startNoGuide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mPrefs.edit().putBoolean("show_guide", false).commit();
				onFocusChange(mBtnApp_1, true);
				popupWindow.dismiss();
			}
		});
        
		popupWindow.setFocusable(true);
		// popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
		//guidelMain.setBackgroundResource(Utils.GUIDE_DATA[0]);
		mViewPager.setFocusable(true);
		mViewPager.setFocusableInTouchMode(true);
		mViewPager.setOnKeyListener(new OnKeyListener() {
			

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent key) {
                Log.e(LOGTAG, "onKey...key.getKeyCode()="+key.getKeyCode()+"; mIndex="+mIndex );
				if (key.getRepeatCount() == 0
						&& key.getAction() == KeyEvent.ACTION_UP) {
					if (key.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
						start.setVisibility(View.GONE);
						startNoGuide.setVisibility(View.GONE);
						
						mViewPager.setFocusable(true);
						
						mIndex--;
						if (mIndex < 0) {
							mIndex = 0;
							return true;
						}
					} else if (key.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
						mIndex++;
						if(mIndex == Utils.GUIDE_DATA.length-1){
							mViewPager.setFocusable(false);
						   
							start.setVisibility(View.VISIBLE);
							startNoGuide.setVisibility(View.VISIBLE);
						}
					}
					

					if (mIndex <0 || mIndex >= Utils.GUIDE_DATA.length) {
						return true;
					}
					//guidelMain.setBackgroundResource(Utils.GUIDE_DATA[mIndex]);
					mViewPager.setCurrentItem(mIndex);
					mViewPager.setPageTransformer(true, mTransformer);
				}
				return false;
			}
		});
		popupWindow.showAtLocation(mMainLayout, Gravity.FILL, 0, 0);
	}
	
	public void updateUserAPKView() {
		Log.d(LOGTAG, "updateUserAPKView");
		Boolean defaultAdd1 = SettingUtils.getApp01DefaultValue(mContext);
	    Boolean defaultAdd2 = SettingUtils.getApp02DefaultValue(mContext);
		mAppStr_6 = SettingUtils.getApp01PackageName(mContext);
		Log.d(LOGTAG, "defaultAdd1="+defaultAdd1+"; mAppStr_6=" + SettingUtils.getApp01PackageName(mContext));
		mAppStr_7 = SettingUtils.getApp02PackageName(mContext);
		Log.d(LOGTAG, "defaultAdd2="+defaultAdd2+"; mAppStr_7=" + SettingUtils.getApp02PackageName(mContext));

		
		ApplicationInfo appInfo = null;
		if (AppTool.isEmpty(mAppStr_6) != true) {
			if(defaultAdd1 && mAppStr_6.equals(Utils.SHAFA_MARKET)){
				mTitle_6.setText(getString(R.string.add));
				mIcon_6.setImageDrawable(getResources().getDrawable(
						R.drawable.add_icon));
			}else{
				try {
					appInfo = mPackageManager.getApplicationInfo(mAppStr_6, 0);
					if (appInfo != null) {
						mTitle_6.setText(appInfo.loadLabel(mPackageManager));
						mIcon_6.setImageDrawable(appInfo.loadIcon(mPackageManager));
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					SettingUtils.setApp01PackageName(mContext, "");
					mTitle_6.setText(getString(R.string.add));
					mIcon_6.setImageDrawable(getResources().getDrawable(
							R.drawable.add_icon));
				}
			}
		}else{
				mTitle_6.setText(getString(R.string.add));
				mIcon_6.setImageDrawable(getResources().getDrawable(
						R.drawable.add_icon));
		} 
		
		if (AppTool.isEmpty(mAppStr_7) != true) {
			if(defaultAdd2 && mAppStr_7.equals(Utils.SHAFA_MARKET)){
				mTitle_7.setText(getString(R.string.add));
				mIcon_7.setImageDrawable(getResources().getDrawable(
						R.drawable.add_icon));
			}else{
				try {
					appInfo = mPackageManager.getApplicationInfo(mAppStr_7, 0);
					if (appInfo != null) {
						mTitle_7.setText(appInfo.loadLabel(mPackageManager));
						mIcon_7.setImageDrawable(appInfo.loadIcon(mPackageManager));
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					SettingUtils.setApp02PackageName(mContext, "");
					mTitle_7.setText(getString(R.string.add));
					mIcon_7.setImageDrawable(getResources().getDrawable(
							R.drawable.add_icon));
				}
			}
		} else {
			mTitle_7.setText(getString(R.string.add));
			mIcon_7.setImageDrawable(getResources().getDrawable(
					R.drawable.add_icon));
		}

	}

	public void updateView(int id) {
		switch (id) {
		case 0:
			updateViewBg(mImage_1, mPosterPathList.get(id), id);
			break;
		case 1:
			updateViewBg(mImage_2, mPosterPathList.get(id), id);
			break;
		case 2:
			updateViewBg(mImage_3, mPosterPathList.get(id), id);
			break;
		case 3:
			updateViewBg(mImage_4, mPosterPathList.get(id), id);
			break;
		case 4:
			updateViewBg(mImage_5, mPosterPathList.get(id), id);
			break;
		case 5:
			if(SettingUtils.getApp01PackageName(mContext) !=null
			&& !SettingUtils.getApp01PackageName(mContext).equals("")
			&& !SettingUtils.getApp01PackageName(mContext).equals(Utils.SHAFA_MARKET)){
			    updateViewBg(mImage_6, mPosterPathList.get(id), id);
			}else{
				mImage_6.setBackgroundResource(R.drawable.add);
			}
			break;
		case 6:
			if(SettingUtils.getApp02PackageName(mContext) !=null
			&& !SettingUtils.getApp02PackageName(mContext).equals("")
			&& !SettingUtils.getApp02PackageName(mContext).equals(Utils.SHAFA_MARKET)){
			    updateViewBg(mImage_7, mPosterPathList.get(id), id);
			}else{
				mImage_7.setBackgroundResource(R.drawable.add);
			}
			break;
		case 7:
			updateViewBg(mImage_8, mPosterPathList.get(id), id);
			break;
		case 8:
			updateViewBg(mImage_9, mPosterPathList.get(id), id);
			break;
		default:
			break;
		}
	}

	
	public void updateViewBg(ImageView imgView, String filePath, int id) {
		if (AppTool.isEmpty(filePath)) {
			return;
		}
		Log.d(LOGTAG, "filePath=" + filePath);
		File iconFile = new File(filePath);
		if (iconFile.exists() != true) {
			if (mApkList.size() > id) {
				ApkInfo a = mApkList.get(id);
				//a.setEdition("-1");
				mEpgDBDao.updateApp(id + 1, a);
			}
			return;
		}
		FileInputStream fis;
		Bitmap bitmap;
		try {
			fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			imgView.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void updateViewBg(RelativeLayout imgView, String filePath, int id) {
		if (AppTool.isEmpty(filePath)) {
			return;
		}
		Log.d(LOGTAG, "filePath=" + filePath);
		File iconFile = new File(filePath);
		if (iconFile.exists() != true) {
			if (mApkList.size() > id) {
				ApkInfo a = mApkList.get(id);
				//a.setEdition("-1");
				mEpgDBDao.updateApp(id + 1, a);
			}
			return;
		}
		FileInputStream fis;
		Bitmap bitmap;
		try {
			fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			imgView.setBackground(new BitmapDrawable(bitmap));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setRollingText(String text) {
		mRollingText.stopScroll();
		mRollingText.setText(text);
		mRollingText.init();
		mRollingText.setSpeed((float)0.5);
		mRollingText.startScroll();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(LOGTAG, "onkey=="+keyCode);
		return false;
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				return true;
		    case 8: //1
			    startMainApp(R.id.button_1);
			    break;
		    case 9: //2
			    startMainApp(R.id.button_2);
			    break;
		    case 12: //3
			    startMainApp(R.id.button_3);
			    break;
		    case 10: //4
			    startMainApp(R.id.button_4);
			    break;
		    case 11: //5
			    startMainApp(R.id.button_5);
			    break;
		    case 13: //8
			    startMainApp(R.id.button_8);
			    break;
		    case 7: //9
			    startMainApp(R.id.button_9);
			    break;
			
			case KeyEvent.KEYCODE_MENU: {
				Log.d(LOGTAG, "press menu key mCurrentSetApp=" + mCurrentSetApp);
				if (mCurrentSetApp != -1) {
					showReplaceDialog();
					return true;
				}
				break;
			}
			
			case KeyEvent.KEYCODE_DPAD_DOWN:{
	            Log.e(LOGTAG, "mCurrentFocusId="+mCurrentFocusId);
	            boolean isShow =false;
				switch (mCurrentFocusId) {
					case R.id.button_5://807
						mCurrentIndex = 5;
						isShow = true;
						break;
					case R.id.button_6://803
						mCurrentIndex = 6;
						isShow = true;
						break;
					case R.id.button_7://812
						mCurrentIndex = 7;
						isShow = true;
						break;
					case R.id.button_8://816
						mCurrentIndex = 8;
						isShow = true;
						break;
					case R.id.button_9: //818
						mCurrentIndex = 9;
						isShow = true;
						break;
				}
				if(isShow){
				    showShotcutPopup();
				}
				break;
			}
			
			default:
				break;
			}
		return super.onKeyDown(keyCode, event);
	}

	private void showShotcutPopup() {
		mBtnApp_1.clearFocus();
		mBtnApp_2.clearFocus();
		mBtnApp_3.clearFocus();
		mBtnApp_4.clearFocus();
		mBtnApp_5.clearFocus();
		mBtnApp_6.clearFocus();
		mBtnApp_7.clearFocus();
		mBtnApp_8.clearFocus();
		mBtnApp_9.clearFocus();
		mImageDown.requestFocus();
		mImageDown.setScaleX(0.00f);
		mImageDown.setScaleY(0.00f);
		//mImageDown.setVisibility(View.INVISIBLE);
		mRollingText.setVisibility(View.INVISIBLE);
		if(!isOnline()
		|| mShortcutApkList==null || mShortcutAdapter==null 
		|| mShortcutApkList.size()<1 || mShortcutAdapter.getCount() < 1){
			//Toast.makeText(mContext, "no shortcut application info!", Toast.LENGTH_LONG).show();
			mShortcutApkList = AppTool.getShortcutApp(mContext);
			mShortcutAdapter = new ShortcutAppAdapter(mContext, mShortcutApkList);
			
		}
		
		MenuPopupWindow menuPopupWindow = new MenuPopupWindow(this, mMainLayout, mMainHandler,
				mShortcutApkList, mShortcutAdapter,  mDm);
		menuPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				mMainHandler.sendEmptyMessage(Utils.HIDE_SHORTCUT_APP);
			}
		});
		menuPopupWindow.show();
	}

	
	private boolean isOnline() {
		boolean isOnline =  com.egreat.adlauncher.util.HttpUtils.isNetworkConnected(mContext);
		Log.e(LOGTAG, "isOnline="+isOnline);
		return isOnline;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		mCurrentSetApp = -1;
		if (hasFocus) {
			mCurrentFocusId = v.getId();
			switch (v.getId()) {
			case R.id.button_1:
			case R.id.button_2:
			case R.id.button_3:
			case R.id.button_4:
			case R.id.button_5:
			case R.id.button_6:
			case R.id.button_7:
			case R.id.button_8:
			case R.id.button_9:
				if (v.getId() == R.id.button_6)
					mCurrentSetApp = CURRENT_SET_APP01;
				else if (v.getId() == R.id.button_7)
					mCurrentSetApp = CURRENT_SET_APP02;
				v.setScaleX(1.1f);
				v.setScaleY(1.1f);
				v.bringToFront();
				mMainHandler.sendEmptyMessage(UPDATE_TIME);
				Log.d(LOGTAG, "bringToFront!!!!!!!!!!!!!");
				break;

			}
		} else {
			mCurrentFocusId = -1;
			switch (v.getId()) {
			case R.id.button_1:
			case R.id.button_2:
			case R.id.button_3:
			case R.id.button_4:
			case R.id.button_5:
			case R.id.button_6:
			case R.id.button_7:
			case R.id.button_8:
			case R.id.button_9:
				v.setScaleX(1.0f);
				v.setScaleY(1.0f);
				v.invalidate();
				break;

			}
		}

	}

   private boolean startActivityByPackageName(String packageName) {
		Log.e(LOGTAG, "startActivityByPackageName...packagename = "+packageName);
		PackageManager pm = this.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packageName);
		try {
			this.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//Toast.makeText(MainActivity.this, "未安装此应用。", Toast.LENGTH_LONG).show();
		} 
		return false;
   }
   
   private boolean startActivityByComponent(String packageName, String className) {
		Log.e(LOGTAG, "startActivityByComponent...packagename = "+packageName+"; className="+className);
		
		Intent intent = new Intent();
		try {
			ComponentName name = new ComponentName(packageName, className);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(name);
			this.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//Toast.makeText(MainActivity.this, "未安装此应用。", Toast.LENGTH_LONG).show();
		} 
		return false;
	}

	private boolean RunApp(String packageName) {
		if(packageName.equals(Utils.SETTING_PACKAGENAME)){
			return startActivityByComponent(Utils.SETTING_PACKAGENAME, "com.ikan.nscreen.box.settings.SystemSettings");
		}else{
		    return startActivityByPackageName(packageName);
		}
		/*
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pManager = getPackageManager();
			List<ResolveInfo> apps = pManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				// intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				startActivity(intent);
				return true;
			} else
				return false;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch(Exception err){
			err.printStackTrace();
			return false;
		}
		*/
	}

	@Override
	public void onClick(View v) {
		apk_sum = mEpgDBDao.getAppCount();
		
		
		startMainApp(v.getId());
	}

	private void startMainApp(int id) {
		String packageName = null;
		String getEdiation = null;
		String localEdition = null;
		
		switch (id) {
		case R.id.button_1:
			if(mApkList.size() > 0 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(0).getPackagename();
				getEdiation = mApkList.get(0).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				Log.e(LOGTAG, "onClick..getEdiation="+getEdiation
						+"; localEdition="+localEdition
						+"; packageName="+packageName);
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				
				if(canUpdate){
					//Log.e(LOGTAG, "start update one...");
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(0).getDownloadlink(), mApkList
							.get(0).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(0).getPackagename()) != true) {
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(0).getPackagename());
						downloadApk(mApkList.get(0).getDownloadlink(), mApkList
								.get(0).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[0]);
			}
			break;
		case R.id.button_2:
			if(mApkList.size() > 1 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(1).getPackagename();
				getEdiation = mApkList.get(1).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				
				if(canUpdate){
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(1).getDownloadlink(), mApkList
							.get(1).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(1).getPackagename()) != true) {
						mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(1).getPackagename());
						downloadApk(mApkList.get(1).getDownloadlink(), mApkList
								.get(1).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[1]);
			}
			
			break;
		case R.id.button_3:
			if(mApkList.size() > 2 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(2).getPackagename();
				getEdiation = mApkList.get(2).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				if(canUpdate){
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(2).getDownloadlink(), mApkList
							.get(2).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(2).getPackagename()) != true) {
						mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(2).getPackagename());
						downloadApk(mApkList.get(2).getDownloadlink(), mApkList
								.get(2).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[2]);
			}
			break;
		case R.id.button_4:
			//dating
			//Log.e(LOGTAG, "button.4..mApkList.get(3).getPackagename()="+mApkList.get(3).getPackagename());
			if(mApkList.size() > 3 && mApkList.get(3).getPackagename() !=null){
				if(mApkList.get(3).getPackagename().equals("com.mgle.launcher.app")){
					Intent intent = new Intent(MainActivity.this, AppActivity.class);
					startActivity(intent);
				}else{
					if (RunApp(mApkList.get(3).getPackagename()) != true) {
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(3).getPackagename());
						downloadApk(mApkList.get(3).getDownloadlink(), mApkList
								.get(3).getPackagename() + ".apk");
					}
				}
			}else{
				Intent intent = new Intent(MainActivity.this, AppActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.button_5:
			if(mApkList.size() > 4 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(4).getPackagename();
				getEdiation = mApkList.get(4).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				
				if(canUpdate){
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(4).getDownloadlink(), mApkList
							.get(4).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(4).getPackagename()) != true) {
						mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(4).getPackagename());
						downloadApk(mApkList.get(4).getDownloadlink(), mApkList
								.get(4).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[3]);
			}
			break;
		case R.id.button_6:
			isAddUser1 =false;
			Log.e(LOGTAG,"receiver..button_6..packagename="+SettingUtils.getApp01PackageName(mContext)+";isAddUser1="+isAddUser1);
			if(AppTool.isEmpty(SettingUtils.getApp01PackageName(mContext))){
				if(!RunApp(Utils.SHAFA_MARKET)){
					if(mApkList.size() > 5)
				       downloadApk(mApkList.get(5).getDownloadlink(), mApkList.get(5).getPackagename() + ".apk");
				}else{
					isAddUser1 = true;
				}
			}else{
				if(SettingUtils.getApp01PackageName(mContext).equals(Utils.SHAFA_MARKET)){
					isAddUser1 = true;
				}
				
				if(!RunApp(SettingUtils.getApp01PackageName(mContext))){
					if(SettingUtils.getApp01PackageName(mContext) !=Utils.SHAFA_MARKET 
							&& !RunApp(Utils.SHAFA_MARKET)){
					    downloadApk(mApkList.get(5).getDownloadlink(), mApkList.get(5).getPackagename() + ".apk");
					}
				}
			}
			
			break;
		case R.id.button_7:
			isAddUser2 =false;
			Log.e(LOGTAG,"receiver..button_7..packagename="+SettingUtils.getApp02PackageName(mContext)+";isAddUser2="+isAddUser2);
			if(AppTool.isEmpty(SettingUtils.getApp02PackageName(mContext))){
				if(!RunApp(Utils.SHAFA_MARKET)){
					if(mApkList.size() > 6)
				       downloadApk(mApkList.get(6).getDownloadlink(), mApkList.get(6).getPackagename() + ".apk");
				}else{
					isAddUser2 = true;
				}
			}else{
				if(SettingUtils.getApp02PackageName(mContext).equals(Utils.SHAFA_MARKET)){
					isAddUser2 = true;
				}
				
				if(!RunApp(SettingUtils.getApp02PackageName(mContext))){
					if(SettingUtils.getApp02PackageName(mContext) !=Utils.SHAFA_MARKET 
							&& !RunApp(Utils.SHAFA_MARKET)){
					    downloadApk(mApkList.get(6).getDownloadlink(), mApkList.get(6).getPackagename() + ".apk");
					}
				}
			}
			break;
		case R.id.button_8:
			if(mApkList.size() > 7 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(7).getPackagename();
				getEdiation = mApkList.get(7).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				
				if(canUpdate){
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(7).getDownloadlink(), mApkList
							.get(7).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(7).getPackagename()) != true) {
						mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(7).getPackagename());
						downloadApk(mApkList.get(7).getDownloadlink(), mApkList
								.get(7).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[4]);
			}
			break;
		case R.id.button_9:
			if(mApkList.size() > 8 && AppTool.isNetworkAvailable(mContext)){
				packageName = mApkList.get(8).getPackagename();
				getEdiation = mApkList.get(8).getEdition();
				localEdition = mUpdatePrefs.getString(packageName, "");
				
				boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
				if(localEdition==null || localEdition.equals("")){
				    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
				}
				
				if(canUpdate){
					mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
					downloadApk(mApkList.get(8).getDownloadlink(), mApkList
							.get(8).getPackagename() + ".apk");
				}else{
					if (RunApp(mApkList.get(8).getPackagename()) != true) {
						mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
						Log.d(LOGTAG, "need to dowload apk "
								+ mApkList.get(8).getPackagename());
						downloadApk(mApkList.get(8).getDownloadlink(), mApkList
								.get(8).getPackagename() + ".apk");
					}
				}
			}else{
				RunApp(Utils.PACKAGE_LAUNCHER_ARRAY[5]);
			}
			break;
		default:
			break;
		}
	}

	public static int mCurrentSetApp = -1;
	public static final int CURRENT_SET_APP01 = 1;
	public static final int CURRENT_SET_APP02 = 2;

	public void showReplaceDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_replace_app, null);
		Button mReplaceBtn = (Button) view.findViewById(R.id.btn_replace);
		Button mRemoveBtn = (Button) view.findViewById(R.id.btn_remove);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog mAlertDialog = builder.create();
		mReplaceBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentSetApp != -1) {
					Intent intent = new Intent();
					intent.setClass(mContext, AddAppActivity.class);
					startActivity(intent);
					mAlertDialog.dismiss();
					updateView(5);
					updateView(6);
					return;
				}
				mAlertDialog.dismiss();
			}
		});

		mRemoveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentSetApp >= CURRENT_SET_APP01
						&& mCurrentSetApp <= CURRENT_SET_APP02) {
					switch (mCurrentSetApp) {
					case CURRENT_SET_APP01:
						SettingUtils.setApp01PackageName(mContext, "");
						break;
					case CURRENT_SET_APP02:
						SettingUtils.setApp02PackageName(mContext, "");
						break;
					}
					updateView(5);
					updateView(6);
					updateUserAPKView();
				}
				mAlertDialog.dismiss();
			}
		});

		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}

	public void downloadAppBackground(){
		String currentVersion = AppTool.getVersion(mContext);
		String targetVersion = mUpdateInfo.verName;
		Log.e(LOGTAG, "downloadAppBackground...currentVersion="+currentVersion+"; targetVersion="+targetVersion);
		mPrefs.edit().putString("new_version", targetVersion).commit();
		if(AppTool.canUpdate(currentVersion, targetVersion)){
		     downloadApkBackground(mUpdateInfo.apkurl, Utils.LAUNCHER_NAME);
		}
	}

	private void downloadApkBackground(String apkurl, String apkname) {
		Log.e(LOGTAG, "downloadApkBackground...mUpdateing="+mUpdateing
				+"; apkurl="+apkurl+"; apkname="+apkname+"; version="+mUpdateInfo.verName);
		if(mUpdateing ){
			return;
		}
		
		try{
			String filePath = Utils.getRootPath()+apkname;
	        File file = new File(filePath);  
	        Log.e(LOGTAG, "downloadApkBackground..filePath="+filePath+"; file.exist="+file.exists());
	        if(file.exists()){
	        	file.delete();
	        	//return;
	        }
	        
	        mDownloadThread = new GetDownLoadThread();
	        mDownloadThread.apkName = filePath;
	        mDownloadThread.apkUrl = apkurl;
			mUpdateing = true;
			mDownloadIsError = false;
			mDownloadThread.start();
		}catch(Exception er){
			er.printStackTrace();
		}
	}

	private class GetDownLoadThread extends Thread{			
		public static final int SUSPEND_TIME_MILLISECONDS = 50;
		public String apkUrl;
		public String apkName;
		@Override
		public void run() {
			try {
				while(true){
					synchronized (this) {
						Message msg = new Message();
				        msg.what = downloadFile(apkUrl, apkName);
				        mMainHandler.sendMessage(msg);
						if(mDownloadIsError || mDownloadThread.isInterrupted()){
							break;
						}
					}
					if(mDownloadIsError || mDownloadThread.isInterrupted()){
						break;
					}else{
						Thread.sleep(SUSPEND_TIME_MILLISECONDS);
					}
				}
			}catch(InterruptedException err){
				err.printStackTrace();
			}
		}	
    }
	
	private int downloadFile(final String apkurl, final String apkname) {		
		Log.e(LOGTAG, "downloadApkBackground..apkurl="+apkurl+"; filePath="+apkname);
        File file = new File(apkname);  
        FileOutputStream out = null;
		InputStream is = null;
		int fileLength = 0;
		long count = 0;
        try{
        	URL url = new URL(apkurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6*1000);  
            conn.setRequestMethod("GET"); 
            conn.setDoOutput(true); 
            if(file.exists()){
                count = file.length();
            }
            
            conn.setRequestProperty("User-Agent", "NetFox");
            conn.setRequestProperty("RANGE", "bytes=" + count + "-");
            conn .setRequestProperty("Accept-Encoding", "identity"); 
            out = new FileOutputStream(file, true); 
            is = conn.getInputStream();
            fileLength = conn.getContentLength();  
            Log.e(LOGTAG, "downloadApkBackground...count="+count+"; fileLength="+fileLength);
            
			byte buf[] = new byte[4 * 1024];
			int size = 0;
			
			while ((size = is.read(buf)) != -1) {  //down and cached
				try {
					out.write(buf, 0, size);
					count += size;
					if(count >= fileLength){
						return UPDATE_FAIL_WITH_SUCCESS;
					}
					
					//Log.e(LOGTAG, "downloadApkBackground..count="+count);
					//publishProgress(count, fileLength);   
					out.flush();
				} catch (Exception e) {		
					e.printStackTrace();
					mDownloadIsError = true;
					return UPDATE_FAIL_WITH_FAIL_UNKONWN;
				}
			}
			return UPDATE_FAIL_WITH_SUCCESS;
        }catch (MalformedURLException e) {
        	mDownloadIsError = true;
			e.printStackTrace();
		}catch (IOException e) {
			mDownloadIsError = true;
			e.printStackTrace();
		}finally{  
            try{  
            	out.close();  
                is.close();  
            }  
            catch(Exception e){  
            	mDownloadIsError = true;
                e.printStackTrace();  
            }  
        }
        
        return UPDATE_FAIL_WITH_FAIL_UNKONWN;
	}
	
	public void showUpdateDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_update_app, null);
		Button mUpdateBtn = (Button) view.findViewById(R.id.btn_update);
		Button mCancelBtn = (Button) view.findViewById(R.id.btn_cancel);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog mAlertDialog = builder.create();
		mUpdateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mUpdateInfo != null) {
					downloadApk(mUpdateInfo.apkurl, mUpdateInfo.apkname);
					return;
				}
				mAlertDialog.dismiss();
			}
		});

		mCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
			}
		});

		mAlertDialog.show();
		mAlertDialog.getWindow().setContentView(view);
	}
	
	@Override
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}
	
	public void initMemberInfo(MemoryInfo memberInfo){
		Log.e(LOGTAG, "initMemberInfo...memberInfo.integral="+memberInfo.integral);
		//memberInfo.integral = 247983217;
		DecimalFormat format = new DecimalFormat("###,###");
		mMoneyTotal.setText(format.format(memberInfo.integral)+"");
	}

	public void paserShortcutInfo(){
		if(DeviceManager.getShortcutJsonData()==null 
	    || DeviceManager.getShortcutJsonData().equals("")) return;
		
		Log.d(LOGTAG, "shortcut json=" + DeviceManager.getShortcutJsonData());
		mShortcutApkList = new ArrayList<ApkInfo>();
		try {
			JSONTokener jsonParser = new JSONTokener(
					DeviceManager.getShortcutJsonData());
			if(jsonParser==null) return;
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("application");
			int sum = application.length();
			Log.d(LOGTAG, "application sum=" + sum);
			for (int i = 0; i < sum; i++) {
				JSONObject subObject = application.getJSONObject(i);
				ApkInfo apk = new ApkInfo();
				apk.setId(i + 1);
				apk.setId_text(subObject.getInt("id") + "");
				apk.setName(subObject.getString("name"));
				apk.setPackagename(subObject.getString("packagename"));
				apk.setEdition(subObject.getString("edition"));
				apk.setDownloadlink(subObject.getString("downloadlink"));
				apk.setApkconfig(subObject.getString("apkconfig"));
				apk.setPoster(subObject.getString("poster"));
				mShortcutApkList.add(apk);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception err){
			err.printStackTrace();
		}
		
		Message msg = new Message();
		msg.what = INIT_SHORTCUT_APP;
		mMainHandler.sendMessage(msg);
	}
	
	private void parsePosterLogoParams() {
		String jsonIndex = DeviceManager.getSubCategoryjsonIndex();
        Log.e(LOGTAG, "parsePosterLogoParams..jsonIndex="+jsonIndex);
        if(jsonIndex==null || jsonIndex.equals("")) return;
        
        try {
			JSONTokener jsonParser = new JSONTokener(jsonIndex);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("category");
			int sum = application.length();
			for (int i = 0; i < sum; i++) {
				JSONObject subObject = application.getJSONObject(i);
				HomePageCategory category = new HomePageCategory();
				category.id = subObject.getInt("id");
				category.name = subObject.getString("name");
				category.poster = subObject.getString("poster");
				category.logo = subObject.getString("logo");
				
				Log.e(LOGTAG, "parsePosterLogoParams..name="+category.name+"; poster="+category.poster);
				if(category.poster == null || category.poster.equals("")) continue;
				if(category.name.equals("便民服务")){
				   downloadPic(category.poster, Utils.getRootPath()+"app_background.jpg", UPDATE_APP_BACKGROUND);
				}else if(category.name.equals("芒果乐商标logo")){
				   downloadPic(category.poster, Utils.getRootPath()+"main_logo.png", UPDATE_MAIN_LOGO);
				}else if(category.name.equals("会员背景图")){
				   downloadPic(category.poster, Utils.getRootPath()+"member_background.jpg", -1);
			    }else if(category.name.equals("商城背景图")){
				   downloadPic(category.poster, Utils.getRootPath()+"shopping_background.jpg", -1);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	
	
	public void paserMemoryInfo(String jsonData){
		Log.d(LOGTAG, "initMemoryInfo...paserMemoryInfo=" + jsonData);
		MemoryInfo memberInfo = new MemoryInfo();
		try {
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			
			memberInfo.customername = epgObj.getString("customername");
			memberInfo.level = epgObj.getInt("level");
			memberInfo.mobile = epgObj.getString("mobile");
			memberInfo.onlineDuration = epgObj.getLong("onlineDuration");
			memberInfo.integral =  epgObj.getInt("integral");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		
		/*Message msg = new Message();
		msg.what = INIT_MEMBER_INFO; 
		msg.obj = memberInfo;
		mMainHandler.sendMessage(msg);*/
		initMemberInfo(memberInfo);
	}

	public void paserEPGInfo() {
		Log.d(LOGTAG, "indexJsonData=" + DeviceManager.getIndexJsonData());
		//List<ApkInfo> tmpApkList = new ArrayList<ApkInfo>();
		
		mApkList = new ArrayList<ApkInfo>();
		try {
			JSONTokener jsonParser = new JSONTokener(
					DeviceManager.getIndexJsonData());
			if(jsonParser ==null) return;
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("application");
			int sum = application.length();
			Log.d(LOGTAG, "application sum=" + sum);
			for (int i = 0; i < sum; i++) {
				JSONObject subObject = application.getJSONObject(i);
				ApkInfo apk = new ApkInfo();
				apk.setId(i + 1);
				apk.setId_text(subObject.getInt("id") + "");
				apk.setName(subObject.getString("name"));
				apk.setPackagename(subObject.getString("packagename"));
				apk.setEdition(subObject.getString("edition"));
				Log.e(LOGTAG, "apk.edition="+apk.getEdition());
				apk.setDownloadlink(subObject.getString("downloadlink"));
				apk.setApkconfig(subObject.getString("apkconfig"));
				apk.setPoster(subObject.getString("poster"));
				mApkList.add(apk);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		
		for (int i = 0; i < mApkList.size(); i++) {
			Message msg = new Message();
			msg.what = DEVICE_UPDATE_APK_POSTER;
			msg.arg1 = i;
			Log.d(LOGTAG, "Apk info " + i + " update");
			mMainHandler.sendMessage(msg);
		}
		
		/* added by guofq
		try{
			Log.d(LOGTAG, "tmpApkList.size=" + tmpApkList.size());
			apk_sum = mEpgDBDao.getAppCount();
			if (apk_sum == 0) {
				Log.d(LOGTAG, "Need to add apk info to database!");
				for (int i = 0; i < tmpApkList.size(); i++) {
					mEpgDBDao.addApp(tmpApkList.get(i));
				}
				mApkList = mEpgDBDao.getAppList();
				for (int i = 0; i < tmpApkList.size(); i++) {
					Message msg = new Message();
					msg.what = DEVICE_UPDATE_APK_POSTER;
					msg.arg1 = i;
					Log.d(LOGTAG, "Apk info " + i + " update");
					mMainHandler.sendMessage(msg);
				}
			} else {
				Log.d(LOGTAG, "Check apk info for update");
				mApkList = mEpgDBDao.getAppList();
				for (int i = 0; i < mApkList.size(); i++) {
					if(i>=tmpApkList.size()) continue;
					Log.d(LOGTAG, "mApkList.get(i).getEdition()="
							+ mApkList.get(i).getEdition());
					Log.d(LOGTAG, "tmpApkList.get(i).getEdition()="
							+ tmpApkList.get(i).getEdition());
					if (mApkList.get(i).getEdition()
							.equals(tmpApkList.get(i).getEdition()) != true) {
						Message msg = new Message();
						msg.what = DEVICE_UPDATE_APK_POSTER;
						msg.arg1 = i;
						Log.d(LOGTAG, "Apk info " + i + " update");
						mEpgDBDao.updateApp(i + 1, tmpApkList.get(i));
						mApkList = mEpgDBDao.getAppList();
						mMainHandler.sendMessage(msg);
					}
	
				}
			}
		}catch(Exception err){
			err.printStackTrace();
		}
		*/
		updateUserAPKView();
	}

	public void installApk(String filePath) {
		Log.i(LOGTAG, "file path =" + filePath);
		File file = new File(filePath);
		if (file.exists() != true) {
			Toast.makeText(mContext, "apk not exist!", Toast.LENGTH_LONG).show();
			return;
		}
		PackageManager pm = mContext.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(filePath,
				PackageManager.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			startActivity(intent);
		} else {
			Toast.makeText(mContext, "start app error! application is null!", Toast.LENGTH_LONG).show();
			if (file.exists())
				file.delete();
		}
	}

	public static final int DEVICE_LOGIN_OK = 0;
	public static final int DEVICE_LOGIN_FAIL = 1;
	public static final int DEVICE_APK_UPDATE = 2;
	public static final int DEVICE_SHOW_ROLLTEXT = 3;
	public static final int DEVICE_PASER_EPG_INFO = 4;
	public static final int DEVICE_UPDATE_APK_POSTER = 5;
	public static final int DEVICE_APK_POSTER_DOWNLOADED = 6;
	public static final int DEVICE_APK_DOWNLOAD_OK = 7;
	public static final int DEVICE_APK_DOWNLOAD_FAIL = 8;
	public static final int UPDATE_TIME = 9;
	public static final int UPDATE_WIFI_RSSI = 10;
	public static final int UPDATE_WEATHER = 11;
	public static final int DEVICE_GET_EPG_INFO = 12;
	public static final int DEVICE_GET_UPDATE_INFO = 13;
	public static final int SHOW_GUIDE_WINDOW = 14;
	public static final int INIT_SHORTCUT_APP = 15;
	public static final int UPDATE_LAUNCHER_APK = 16;
	public static final int GET_MEMORY_JSONDATA = 17;
	protected static final int DEVICE_MEMBER_INFO =18;
	protected static final  int UPDATE_FAIL_WITH_SUCCESS = 19;
	protected static final  int UPDATE_FAIL_WITH_FILEEXIST =20;
	protected static final  int UPDATE_FAIL_WITH_FAIL_UNKONWN = 21;
	protected static final int INIT_MEMBER_INFO = 22;
	protected static final int UPDATE_MAIN_BACKGROUND = 23;
	protected static final int UPDATE_MAIN_LOGO = 24;
	protected static final int UPDATE_APP_BACKGROUND = 26;
	
	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_MEMBER_INFO:
				Log.e(LOGTAG, "initMemberInfo...msg.obj="+msg.obj);
				if(msg.obj!=null){
					MemoryInfo memberInfo = (MemoryInfo)msg.obj;
					initMemberInfo(memberInfo);
				}
				break;
			case UPDATE_FAIL_WITH_SUCCESS:
				Log.e(LOGTAG, "downloadApkBackground success!");
				try{
					if(mDownloadThread!=null && mDownloadThread.isAlive() && !mDownloadThread.isInterrupted()){
						mDownloadThread.interrupt();
					}
				}catch(Exception err){
					err.printStackTrace();
				}
				break;
			case UPDATE_FAIL_WITH_FILEEXIST:
			case UPDATE_FAIL_WITH_FAIL_UNKONWN:
				Log.e(LOGTAG, "downloadApkBackground faild..what="+msg.what);
				try{
					if(mDownloadThread!=null && mDownloadThread.isAlive() && !mDownloadThread.isInterrupted()){
						mDownloadThread.interrupt();
					}
				}catch(Exception err){
					err.printStackTrace();
				}
				break;
			case GET_MEMORY_JSONDATA:
				if(msg.obj!=null){
					String jsonData = (String)msg.obj;
					paserMemoryInfo(jsonData);
				}
				break;
			case UPDATE_LAUNCHER_APK:
				//showUpdateDialog();
				if(mUpdateInfo!=null){
					downloadAppBackground();
				}
				
				break;
			case UPDATE_MAIN_BACKGROUND:
				try{
					if(msg.obj!=null){
						Log.e(LOGTAG, "main_background="+msg.obj);
						mMainLayout.setBackground(Drawable.createFromPath(msg.obj.toString()));
					}
				}catch(Exception err){
					err.printStackTrace();
				}
				break;
			case UPDATE_MAIN_LOGO:
				try{
					if(msg.obj!=null){
						Log.e(LOGTAG, "UPDATE_MAIN_LOGO="+msg.obj);
						mLogo.setImageBitmap(BitmapFactory.decodeFile(msg.obj.toString()));
					}
				}catch(Exception err){
					err.printStackTrace();
				}
				break;
			case UPDATE_APP_BACKGROUND:
				try{
					//nothing
				}catch(Exception err){
					err.printStackTrace();
				}
				break;
			case Utils.HIDE_SHORTCUT_APP:
				//mBtnApp_1.requestFocus();
				mImageDown.setScaleX(1.00f);
				mImageDown.setScaleY(1.00f);
				mRollingText.setVisibility(View.VISIBLE);
				switch(mCurrentIndex){
					case 5:
						mBtnApp_5.requestFocus();
						break;
					case 6:
						mBtnApp_6.requestFocus();
						break;
					case 7:
						mBtnApp_7.requestFocus();
						break;
					case 8:
						mBtnApp_8.requestFocus();
						break;
					case 9:
						mBtnApp_9.requestFocus();
						break;
				}
				break;
			case Utils.RUN_CURRENT_SHORTCUT_APP:
				mCurrentShortcutApkInfo = (msg.obj==null ? null : (ApkInfo)msg.obj);
				if(mCurrentShortcutApkInfo!=null){
					runShortcutApp();
				}else{
					Toast.makeText(mContext, "未安装此APP!", Toast.LENGTH_LONG).show();
				}
				break;
			case INIT_SHORTCUT_APP:
				Log.e(LOGTAG, "INIT_SHORTCUT_APP..hotApps.size="+(mShortcutApkList==null ? "0" : mShortcutApkList.size()));
				mShortcutAdapter = new ShortcutAppAdapter(mContext, mShortcutApkList);
				break;
			case SHOW_GUIDE_WINDOW:
				initGuidePopWindow();
				break;
			case DEVICE_LOGIN_OK:
				Toast.makeText(mContext, "用户登录成功!", Toast.LENGTH_LONG)
						.show();
				mMainHandler.sendEmptyMessage(DEVICE_GET_EPG_INFO);
				mMainHandler.sendEmptyMessageDelayed(DEVICE_GET_UPDATE_INFO, 1*60*1000);
				
				mMainHandler.removeMessages(DEVICE_MEMBER_INFO);
				mMainHandler.sendEmptyMessageDelayed(DEVICE_MEMBER_INFO,
						 1 * 1000);
				break;
			case DEVICE_MEMBER_INFO:
				paserMemoryInfo(DeviceManager.getMemoryJsonData());
				mMainHandler.removeMessages(DEVICE_MEMBER_INFO);
				mMainHandler.sendEmptyMessageDelayed(DEVICE_MEMBER_INFO,
						30* 60 * 1000);
				break;
			case DEVICE_GET_EPG_INFO:
				mGetEPGThread = new GetEPGThread();
				mGetEPGThread.start();
				mMainHandler.removeMessages(DEVICE_GET_EPG_INFO);
				mMainHandler.sendEmptyMessageDelayed(DEVICE_GET_EPG_INFO,
						30 * 60 * 1000);
				break;
			case DEVICE_GET_UPDATE_INFO:
				mGetUpdateThread = new GetUpdateThread();
				mGetUpdateThread.start();
				mMainHandler.removeMessages(DEVICE_GET_UPDATE_INFO);
				mMainHandler.sendEmptyMessageDelayed(DEVICE_GET_UPDATE_INFO,
						30 * 60 * 1000);
				break;
			case DEVICE_LOGIN_FAIL:
				Toast.makeText(mContext, "用户登录失败!",
						Toast.LENGTH_LONG).show();
				break;
			case DEVICE_APK_UPDATE:

				break;
			case DEVICE_SHOW_ROLLTEXT:
				setRollingText(DeviceManager.getRollText());
				break;
			case DEVICE_PASER_EPG_INFO:
				paserEPGInfo();
				paserShortcutInfo();
				break;
			case DEVICE_UPDATE_APK_POSTER:
				Log.d(LOGTAG, "Apk poster " + msg.arg1 + " need to be update");
				downloadPic(msg.arg1);
				break;
			case DEVICE_APK_POSTER_DOWNLOADED:
				Log.d(LOGTAG, "Apk poster " + msg.arg1 + " download completed!");
				updateView(msg.arg1);
				break;
			case DEVICE_APK_DOWNLOAD_OK:
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				Bundle date = msg.getData();
				String filePath = date.getString("file_path");
				Log.d(LOGTAG, "need to install " + filePath);
				if (AppTool.isEmpty(filePath) != true) {
					installApk(filePath);
				}
				break;
			case DEVICE_APK_DOWNLOAD_FAIL:
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				break;
			case UPDATE_TIME:
				Log.d(LOGTAG, "UPDATE_TIME----------------------");
				SimpleDateFormat mSDFtime;
				mSDFtime = new SimpleDateFormat("HH:mm");
				mTime.setText(mSDFtime.format(new Date(System
						.currentTimeMillis())));
				mMainHandler.removeMessages(UPDATE_TIME);
				mMainHandler.sendEmptyMessageDelayed(UPDATE_TIME, 10 * 1000);
				break;
			case UPDATE_WIFI_RSSI:
				if (AppTool.isWifiConnected(mContext)) {
					WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

					WifiInfo info = wifiManager.getConnectionInfo();
					if (info.getBSSID() != null) {
						int strength = WifiManager.calculateSignalLevel(
								info.getRssi(), 5);
						switch (strength) {
						case 0:
							mIvWifi.setImageDrawable(mContext.getResources()
									.getDrawable(R.drawable.icon_wifi_empty));
							break;
						case 1:
							mIvWifi.setImageDrawable(mContext.getResources()
									.getDrawable(R.drawable.icon_wifi_1));
							break;
						case 2:
							mIvWifi.setImageDrawable(mContext.getResources()
									.getDrawable(R.drawable.icon_wifi_2));
							break;
						case 3:
							mIvWifi.setImageDrawable(mContext.getResources()
									.getDrawable(R.drawable.icon_wifi_3));
							break;
						case 4:
							mIvWifi.setImageDrawable(mContext.getResources()
									.getDrawable(R.drawable.icon_wifi_4));
							break;
						}
					}
				}
				mMainHandler.removeMessages(UPDATE_WIFI_RSSI);
				mMainHandler.sendEmptyMessageDelayed(UPDATE_WIFI_RSSI, 3000);
				break;
			case UPDATE_WEATHER:
				isWeatherOK = true;
				Log.d(LOGTAG, "weather ok! mWeatherUtil.weather="
						+ mWeatherUtil.weather);
				WeatherUtil.setWeatherDetailDrawable(mContext, mIconWeather,
						mWeatherUtil.weather);
				break;
			default:
				break;
			}
		}

		
	};

	private void runShortcutApp() {
		String packageName = mCurrentShortcutApkInfo.getPackagename();
		if(!isOnline()){
			if(packageName.equals(Utils.SETTING_PACKAGENAME)){
				if(RunApp(mCurrentShortcutApkInfo.getPackagename())){
					//...
				}else{
					Toast.makeText(mContext, "未安装此APP!", Toast.LENGTH_LONG).show();
				}
			}
			return;
		}

		String getEdiation = mCurrentShortcutApkInfo.getEdition();
		String localEdition = mUpdatePrefs.getString(packageName, "");
		
		boolean canUpdate = AppTool.canUpdateApk(localEdition, getEdiation);
		if(localEdition==null || localEdition.equals("")){
		    mUpdatePrefs.edit().putString(packageName, getEdiation).commit();
		}
		
		Log.e(LOGTAG, "runShortcutApp..packageName="+packageName
				+"; getEdiation="+getEdiation+"; localEdition="+localEdition
				+"; canUpdate="+canUpdate);
		
		if(canUpdate){
			mTempUpdatePrefs.edit().putString(packageName, getEdiation).commit();
			downloadApk(mCurrentShortcutApkInfo.getDownloadlink(), packageName+ ".apk");
		}else{
			if (RunApp(mCurrentShortcutApkInfo.getPackagename()) != true) {
				if(mCurrentShortcutApkInfo.getDownloadlink()!=null && isOnline()){
					downloadApk(mCurrentShortcutApkInfo.getDownloadlink(), 
							mCurrentShortcutApkInfo.getPackagename() + ".apk");
				}else{
					Toast.makeText(mContext, "未安装此APP!", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	private void changePosterLogoParams() {
		File file = getFilesDir();
		String background = file.getAbsolutePath()+"/main_background.jpg";
		Log.e(LOGTAG, "changePosterLogoParams...background path="+background+"; poster="+DeviceManager.getMainBackgroundPoster());
		downloadPic(DeviceManager.getMainBackgroundPoster(), background, UPDATE_MAIN_BACKGROUND);
	}
	
	public final synchronized void downloadPic(final String url, final String path, final int what) {

		if (url == null || url.equals("")) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				AppTool.downloadOnePic(mContext, url,
						path);
				Message msg = new Message();
				msg.what = what;
				msg.obj = path;
				mMainHandler.sendMessage(msg);
			}
		}).start();
	}
	
	public final synchronized void downloadPic(final int id) {

		if (mApkList == null || mApkList.size() < id
				|| AppTool.isEmpty(mApkList.get(id).getPoster())) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				AppTool.downloadOnePic(mContext, mApkList.get(id).getPoster(),
						mPosterPathList.get(id));
				Message msg = new Message();
				msg.what = DEVICE_APK_POSTER_DOWNLOADED;
				msg.arg1 = id;
				mMainHandler.sendMessage(msg);
			}
		}).start();
	}

	
	public void progress(String title, String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	//	progressDialog.setMessage(message);
		progressDialog.setTitle(title);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.show();
	}

	public void downloadApk(String url, String fileName) {
		try{
		progress("下载中...", "下载APP文件： " + fileName);
		String filePath = "/sdcard/" + getPackageName() + fileName;
		File file = new File(filePath);
		if (file.exists())
			file.delete();
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(url, filePath, true, false,
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
						if (progressDialog != null) {
							progressDialog.setProgress(0);
							//progressDialog.setMessage("start");
						}
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (progressDialog != null) {
							progressDialog
									.setProgress((int) ((current * 100) / total));
						}
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						if (progressDialog != null) {
							progressDialog.setMessage("下载成功!");
							Message msg = new Message();
							msg.what = DEVICE_APK_DOWNLOAD_OK;
							Bundle data = new Bundle();
							Log.d(LOGTAG,
									"responseInfo="
											+ responseInfo.result
													.getAbsolutePath());
							data.putString("file_path",
									responseInfo.result.getAbsolutePath());
							msg.setData(data);
							mMainHandler.sendMessageDelayed(msg, 2 * 1000);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (progressDialog != null) {
							progressDialog.setMessage("download failure!");
							mMainHandler.sendEmptyMessageDelayed(
									DEVICE_APK_DOWNLOAD_FAIL, 2 * 1000);
						}
					}
				});
		}catch(Exception err){
			err.printStackTrace();
		}
	}

	class LoginThread extends Thread {

		@Override
		public void run() {
			Log.d("LoginThread", "TestThread.run()");
			Log.d("LoginThread",
					"DeviceManager.version=" + DeviceManager.getVersion());
			LoginStatus mLoginStatus = DeviceManager.Login();

			if (mLoginStatus != null && mLoginStatus.status == true) {
				mMainHandler.sendEmptyMessage(DEVICE_LOGIN_OK);
			} else
				mMainHandler.sendEmptyMessage(DEVICE_LOGIN_FAIL);
		}
	}
	
	class GetMemberThread extends Thread {
		@Override
		public void run() {
			//DeviceManager.getMemoryInfoIndex();
			
			String memoryJsonData = DeviceManager.getMemoryJsonData();
			Log.d("MemoryThread", "MemoryThread.run()..memoryJsonData="+memoryJsonData);
			
			Message msg = mMainHandler.obtainMessage();
			msg.what = GET_MEMORY_JSONDATA;
			msg.obj = memoryJsonData;
			
			mMainHandler.removeMessages(GET_MEMORY_JSONDATA);
			mMainHandler.sendMessage(msg);
		}
	}
	
	class GetEPGThread extends Thread {

		@Override
		public void run() {
			Log.d("GetEPGThread", "GetEPGThread.run()");
			DeviceManager.getEPGIndex();
			changePosterLogoParams();
			parsePosterLogoParams();
			Log.d("GetEPGThread", "rollText=" + mDeviceManager.getRollText());
			mMainHandler.sendEmptyMessage(DEVICE_SHOW_ROLLTEXT);
			Log.d("GetEPGThread",
					"indexJsonData=" + mDeviceManager.getIndexJsonData());
			mMainHandler.sendEmptyMessage(DEVICE_PASER_EPG_INFO);
		}

	}

	class GetUpdateThread extends Thread {
		@Override
		public void run() {
			Log.d("GetUpdateThread", "GetEPGThread.run()");
			mUpdateInfo = DeviceManager.getApkUpdateInfo();
			if (mUpdateInfo != null) {
				Log.e("GetUpdateThread", "apkurl=" + mUpdateInfo.apkurl+"; verName=" + mUpdateInfo.verName);
				mMainHandler.removeMessages(UPDATE_LAUNCHER_APK);
				mMainHandler.sendEmptyMessageDelayed(UPDATE_LAUNCHER_APK, 10*1000);
			}
		}

	}
	
	private void registerBroadcast() {
		IntentFilter intentFilter1 = new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter1.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(receiver, intentFilter1);
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter2.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter2.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter2.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter2.addDataScheme("file");
		registerReceiver(receiver, intentFilter2);

		IntentFilter intentFilter3 = new IntentFilter(
				Intent.ACTION_PACKAGE_ADDED);
		intentFilter3.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter3.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter3.addDataScheme("package");
		registerReceiver(receiver, intentFilter3);
	}

	/**
	 */
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(LOGTAG, "receiver...action============" + action
					+"; isAddUser1="+isAddUser1+"; "
					+ "intent.getData()="+intent.getData());
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				if (AppTool.isUsbConnected())
					mIconUsb.setVisibility(View.VISIBLE);
				else
					mIconUsb.setVisibility(View.INVISIBLE);
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
					|| action.equals(Intent.ACTION_MEDIA_REMOVED)) {
				if (AppTool.isUsbConnected())
					mIconUsb.setVisibility(View.VISIBLE);
				else
					mIconUsb.setVisibility(View.INVISIBLE);
			} else if (action.equals(Intent.ACTION_PACKAGE_ADDED)
					|| action.equals(Intent.ACTION_PACKAGE_REPLACED)
					|| action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
					
				    
				    String packageName = intent.getDataString().substring(8);
				    String tempEdition = mTempUpdatePrefs.getString(packageName, "");
				    Log.e(LOGTAG, "receiver...packageName="+packageName
				    		+"; tempEdition="+tempEdition
				    		+"; before update edition="+mUpdatePrefs.getString(packageName, ""));
				    if(tempEdition !=null && !tempEdition.equals("")){
				    	mUpdatePrefs.edit().putString(packageName, tempEdition).commit();
				    }
				    
				    Log.e(LOGTAG, "receiver...after update edition = "+mUpdatePrefs.getString(packageName, ""));
					if (isAddUser1){
						SettingUtils.setApp01PackageName(mContext, packageName);
						updateView(5);
						isAddUser1 =false;
					}
					if (isAddUser2){
						SettingUtils.setApp02PackageName(mContext, packageName);
						updateView(6);
						isAddUser2 = false;
					}
					
				    if(Utils.LAUNCHER_PACKAGE_NAME.equals(intent.getData())){
					try{
						File file = new File(Utils.getRootPath()+Utils.LAUNCHER_NAME);
						if(file.exists()){
							file.delete();
						}
					}catch(Exception err){
						err.printStackTrace();
					}
				}
				  
				updateUserAPKView();
			} else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				if (AppTool.isNetworkAvailable(context)) {
					WifiManager wifiManager = (WifiManager) mContext
							.getSystemService(Context.WIFI_SERVICE);
					WifiInfo info = wifiManager.getConnectionInfo();
					if (AppTool.isWifiConnected(mContext)) {
						mIvWifi.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.icon_wifi_empty));
						mMainHandler.removeMessages(UPDATE_WIFI_RSSI);
						mMainHandler.sendEmptyMessageDelayed(UPDATE_WIFI_RSSI,
								1000);
					} else {
						mMainHandler.removeMessages(UPDATE_WIFI_RSSI);
						mIvWifi.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.icon_wifi_no));
					}
					if (AppTool.isEthernetConnected(mContext)) {
						mIvEthernet.setImageDrawable(mContext.getResources()
								.getDrawable(
										R.drawable.icon_ethernet_connecting));
						mIvEthernet.setVisibility(View.VISIBLE);
					} else {
						mIvEthernet.setVisibility(View.GONE);
						mIvEthernet.setImageDrawable(mContext.getResources()
								.getDrawable(
										R.drawable.icon_ethernet_disconnected));
					}
					if (AppTool.isNetworkAvailable(mContext)) {
						mLoginThread = new LoginThread();
						mLoginThread.start();
						mWeatherUtil = new WeatherUtil(mContext, mMainHandler);
					}
				} else {
					mMainHandler.removeMessages(UPDATE_WIFI_RSSI);
					mIvWifi.setImageDrawable(mContext.getResources()
							.getDrawable(R.drawable.icon_wifi_no));
					mIvEthernet.setVisibility(View.GONE);
					mIvEthernet
							.setImageDrawable(mContext
									.getResources()
									.getDrawable(
											R.drawable.icon_ethernet_disconnected));
				}
			}
		}
	};

}
