package com.egreat.adlauncher.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.egreat.adlauncher.activity.MainActivity.GetEPGThread;
import com.egreat.adlauncher.adapter.ApplicationListAdapter;
import com.egreat.adlauncher.adapter.CategoryListAdapter;
import com.egreat.adlauncher.adapter.HorizontalListViewAdapter;
import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.effect.ScaleAnimEffect;
import com.egreat.adlauncher.entity.Category;
import com.egreat.adlauncher.entity.CategoryInfo;
import com.egreat.adlauncher.entity.PeopleLiveInfo;
import com.egreat.adlauncher.util.AppTool;
import com.egreat.adlauncher.util.ScrollTextView;
import com.egreat.adlauncher.util.WeatherUtil;
import com.egreat.adlauncher.view.HorizontalListView;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.launcher.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PeopleLiveActivity extends Activity{

	protected static final String TAG = "PeopleLiveActivity";

	protected static final int PASER_NAV_CATEGORY = 0;
	protected static final int UPDATE_TIME = 1;
	protected static final int UPDATE_WEATHER = 2;
	protected static final int UPDATE_WIFI_RSSI = 3;
	public static final int GET_PEOPLELIVE_JSONDATA = 4;
	private static final int INIT_APPLICATION_APP = 5;
	protected static final int DEVICE_SHOW_ROLLTEXT = 6;
	
	WeatherUtil mWeatherUtil = null;
	View mCurrentView;
	
	private ScaleAnimEffect mAnimEffect = null;
	private HorizontalListView mCategoryList;
	private HorizontalListViewAdapter mCategoryAdapter;
	
	private GridView mGVApplication;
    private ApplicationListAdapter mAppAdapter;
	private ProgressBar mLoading;
	private Context mCtx;
	private WindowManager mWm;
    
	private Category mCurrentCategory;
	
	public ImageView mIconUsb = null;
	public ImageView mIconWeather = null;
	public ImageView mIvWifi = null;
	public ImageView mIvEthernet = null;
	public TextView mTime = null;
	public TextView mTitle = null;
	
	private ScrollTextView mRollingText = null;
	
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setContentView(R.layout.peoplelive_main_layout);

		init();
		initView();
		initData();
	}

	private void initData() {
		mHandler.removeMessages(PASER_NAV_CATEGORY);
		mHandler.sendEmptyMessageDelayed(PASER_NAV_CATEGORY, 500);
		
		mHandler.removeMessages(UPDATE_TIME);
		mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
		
		mHandler.removeMessages(DEVICE_SHOW_ROLLTEXT);
		mHandler.sendEmptyMessageDelayed(DEVICE_SHOW_ROLLTEXT, 2000);
	}

	private void init() {
		mCtx = PeopleLiveActivity.this;
		mAnimEffect = new ScaleAnimEffect();
		mWm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
		
		IntentFilter connectIntentFilter = new IntentFilter();
		connectIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectionReceiver, connectIntentFilter);
	}

	private void initView() {
		
		mIvWifi = (ImageView) findViewById(R.id.icon_wifi);
		mIvEthernet = (ImageView) findViewById(R.id.icon_ethernet);
		mIconWeather = (ImageView) findViewById(R.id.icon_weather);
		mIconUsb = (ImageView) findViewById(R.id.icon_usb);
		mIconUsb.setVisibility(View.GONE);
		
		mTime = (TextView) findViewById(R.id.tv_time);
		
		mTitle = (TextView)findViewById(R.id.title);
		
		String title = mCtx.getResources().getString(R.string.sub_title);
		SpannableString sp =  new  SpannableString(title); 
	    sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC),  0 , title.length() , Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	    mTitle.setText(sp);
	    
	    mRollingText = (ScrollTextView) findViewById(R.id.rolling_text);
		setRollingText("");
		
		mCategoryList = (HorizontalListView) findViewById(R.id.list_category);
		mCategoryList.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long arg3) {
				mCategoryAdapter.setSelectIndex(position);
				Log.e(TAG, "onItemSelected...position="+position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		
		mGVApplication = (GridView) findViewById(R.id.grid_application);
		mGVApplication.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Log.e(TAG, "mApplicationList onItemSelected..position="+position);
				if(mAppAdapter!=null){
					mAppAdapter.setSelctItem(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.e(TAG, "mApplicationList onNothingSelected..");
			}
		});
		
		mGVApplication.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				Log.e(TAG, "mApplicationList..onItemSelected...position="+position);
			}
		});
	}

	
	public void setRollingText(String text) {
		mRollingText.stopScroll();
		mRollingText.setText(text);
		mRollingText.init();
		mRollingText.setSpeed((float) 1.5);
		mRollingText.startScroll();
	}
	
	private void initAppData(List<ApkInfo> appList) {
		Log.e(TAG, "initAppData..appList.size= "+appList.size());
		try {
			if (mAppAdapter == null) {
				mAppAdapter = new ApplicationListAdapter(this,
						appList);
			} else {
				mAppAdapter.changeData(appList);
			}
			
			DisplayMetrics mDm = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(mDm);
	        
			int size = appList.size(); 
	        int length = (int) (mDm.widthPixels /6.857);
	        float density = mDm.density;
	        int gridviewWidth = (int) (size * (length ) * density);
	        
	        int width = size*290;
	        LinearLayout.LayoutParams params;
	        params = new LinearLayout.LayoutParams(
        			width, LinearLayout.LayoutParams.MATCH_PARENT);
	        mGVApplication.setGravity(Gravity.CENTER_HORIZONTAL);
			mGVApplication.setLayoutParams(params);
			mGVApplication.setNumColumns(size); 
			mGVApplication.setAdapter(mAppAdapter);
			mGVApplication.setSelection(0);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	
	private void changeNavCategoryData(List<Category> categorys) {
		Log.e(TAG, "changeNavCategoryData..focusId= "+"; categoryInfo="+categorys);
		try {
			if (categorys == null) {
				show("categorys is null!");
				return;
			}
			if (mCategoryAdapter == null) {
				mCategoryAdapter = new HorizontalListViewAdapter(this,
						categorys);
			} else {
				mCategoryAdapter.changeData(categorys);
			}
			
			//mCategoryList.setFocusable(true);
			mCategoryAdapter.setSelectIndex(0);
			mCategoryList.setAdapter(mCategoryAdapter);
			mCurrentCategory = mCategoryAdapter.getItem(0);
     	    if(mCurrentCategory!=null){
            	 GetPeopleLiveThread mGetPeopleLiveThread = new GetPeopleLiveThread();
            	 mGetPeopleLiveThread.start();
            }
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public void paserCategoryJsonInfo() {
		Log.d(TAG, "paserCategoryJsonInfo=" + DeviceManager.getPeopleLiveJsonData());
		List<Category> categorys = new ArrayList<Category>();
		try {
			JSONTokener jsonParser = new JSONTokener(
					DeviceManager.getPeopleLiveJsonData());
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("category");
			int sum = application.length();
			Log.d(TAG, "category sum=" + sum);
			for (int i = 0; i < sum; i++) {
				JSONObject subObject = application.getJSONObject(i);
				Category categroy = new Category();
				categroy.categoryName = subObject.getString("name");
				categroy.subCategoryUrl = subObject.getString("subCategoryUrl");
				categroy.programeListUrl = subObject.getString("programListUrl");
				categroy.hpbcUrl = subObject.getString("hpbcUrl");
				categorys.add(categroy);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		
		changeNavCategoryData(categorys);
	}
	
	private void show(String msg) {
		Toast.makeText(mCtx, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		View rootview = getWindow().getDecorView();
		int aaa = rootview.findFocus().getId();
		Log.e("lee", " curr focus == "+aaa);
		
        int index = mCategoryAdapter.getSelectedIndex(); 
		Log.e(TAG, "onKeyDown..keyCode="+keyCode+"; index="+index+"; mCategoryAdapter.size="+mCategoryAdapter.getCount());
        switch (keyCode) {  
            case KeyEvent.KEYCODE_DPAD_CENTER:
            	mCategoryAdapter.setPressedIndex(index);
            	break;
            case KeyEvent.KEYCODE_DPAD_LEFT:  
            	index --;
            	if(index >= 0){
            	   mCurrentCategory = mCategoryAdapter.getItem(index);
            	   if(mCurrentCategory!=null){
                   	  GetPeopleLiveThread mGetPeopleLiveThread = new GetPeopleLiveThread();
                   	  mGetPeopleLiveThread.start();
                   }
            	   mCategoryAdapter.setSelectIndex(index);
            	}
            	break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:  
            	index++;
            	if(index < mCategoryAdapter.getCount()){
            		mCurrentCategory = mCategoryAdapter.getItem(index);
                    
                    Log.e(TAG, "index="+index+"; mCurrentCategory="+mCurrentCategory);
                    if(mCurrentCategory!=null){
                    	GetPeopleLiveThread mGetPeopleLiveThread = new GetPeopleLiveThread();
                    	mGetPeopleLiveThread.start();
                    }
                    mCategoryAdapter.setSelectIndex(index);
            	}
            	break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            	mGVApplication.requestFocus();
            	mCategoryList.setFocusable(false);
            	mGVApplication.setFocusable(true);
            	if (mAppAdapter != null) {
            		mAppAdapter.setSelctItem(0);
            	}
            	break;
        }
		return super.onKeyDown(keyCode, event);
	}
	
	
	class GetPeopleLiveThread extends Thread {
		@Override
		public void run() {
			
			Log.d("GetGridContentThread", "mCurrentCategory.programeListUrl="+mCurrentCategory.programeListUrl);
			
			String peopleLiveJsonData = DeviceManager.getThirdCategoryJsonData(mCurrentCategory.programeListUrl);
			Log.d("GetPeopleLiveThread", "peopleLiveJsonData=" + peopleLiveJsonData);
			
			Message msg = mHandler.obtainMessage();
			msg.what = GET_PEOPLELIVE_JSONDATA;
			msg.obj = peopleLiveJsonData;
			
			mHandler.removeMessages(GET_PEOPLELIVE_JSONDATA);
			mHandler.sendMessage(msg);
			
		}

	}
	
	public void paserApplicationInfo(String jsonData){
		Log.d(TAG, "paserApplicationInfo jsonData=" + jsonData);
		List<ApkInfo> appList = new ArrayList<ApkInfo>();
		try {
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("application");
			int sum = application.length();
			Log.d(TAG, "application sum=" + sum);
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
				//Log.e(TAG, "paserApplicationInfo...name="+apk.getName()+"; poster="+apk.getPoster());
				appList.add(apk);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Message msg = mHandler.obtainMessage();
		msg.what = INIT_APPLICATION_APP;
		msg.obj = appList;
		
		mHandler.removeMessages(INIT_APPLICATION_APP);
		mHandler.sendMessage(msg);
	}
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DEVICE_SHOW_ROLLTEXT:
				setRollingText(DeviceManager.getRollText());
				break;
			case GET_PEOPLELIVE_JSONDATA:
				if(msg.obj!=null){
					String jsonData = (String)msg.obj;
					paserApplicationInfo(jsonData);
				}
				break;
			case INIT_APPLICATION_APP:
				if(msg.obj!=null){
				    List<ApkInfo> appList = (List<ApkInfo>)msg.obj;
					initAppData(appList);
				}
				break;
			case PASER_NAV_CATEGORY:
				paserCategoryJsonInfo();
				break;
			case UPDATE_TIME:
				SimpleDateFormat mSDFtime = new SimpleDateFormat("HH:mm");
				mTime.setText(mSDFtime.format(new Date(System
						.currentTimeMillis())));
				mHandler.removeMessages(UPDATE_TIME);
				mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 10 * 1000);
				break;
			case UPDATE_WEATHER:
				WeatherUtil.setWeatherDetailDrawable(mCtx, mIconWeather,
						mWeatherUtil.weather);
				break;
			case UPDATE_WIFI_RSSI:
				if (AppTool.isWifiConnected(mCtx)) {
					WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

					WifiInfo info = wifiManager.getConnectionInfo();
					if (info.getBSSID() != null) {
						int strength = WifiManager.calculateSignalLevel(
								info.getRssi(), 5);
						switch (strength) {
						case 0:
							mIvWifi.setImageDrawable(mCtx.getResources()
									.getDrawable(R.drawable.icon_wifi_empty));
							break;
						case 1:
							mIvWifi.setImageDrawable(mCtx.getResources()
									.getDrawable(R.drawable.icon_wifi_1));
							break;
						case 2:
							mIvWifi.setImageDrawable(mCtx.getResources()
									.getDrawable(R.drawable.icon_wifi_2));
							break;
						case 3:
							mIvWifi.setImageDrawable(mCtx.getResources()
									.getDrawable(R.drawable.icon_wifi_3));
							break;
						case 4:
							mIvWifi.setImageDrawable(mCtx.getResources()
									.getDrawable(R.drawable.icon_wifi_4));
							break;
						}
					}
				}
				mHandler.removeMessages(UPDATE_WIFI_RSSI);
				mHandler.sendEmptyMessageDelayed(UPDATE_WIFI_RSSI, 3000);
				break;
			}
			
		}

	};
	
	private void showOnFocusAnimation(View view) {
		mAnimEffect.setAttributs(1.0f, 1.08f, 1.0f, 1.08f, 50);
		Animation anim = mAnimEffect.createAnimation();
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});
		view.startAnimation(anim);
	}
	
	private void showLooseFocusAinimation(View view) {
		mAnimEffect.setAttributs(1.08f, 1.0f, 1.08f, 1.0f, 50);
		view.startAnimation(mAnimEffect.createAnimation());
	}

	private void showLoadingProgress() {
		try {
			if (mLoading.getParent() == null) {
				LayoutParams lp = new LayoutParams();
				lp.format = PixelFormat.TRANSPARENT;
				lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;

				mWm.addView(mLoading, lp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void hideLoadingProgress() {
		try {
			if (mLoading.getParent() != null)
				mWm.removeView(mLoading);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo etherNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			NetworkInfo mobileNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			Log.e(TAG, "mConnectionReceiver...wifiNetInfo=" + wifiNetInfo
					+ "; etherNetInfo=" + etherNetInfo + "; mobileNetInfo="
					+ mobileNetInfo);

			if ((wifiNetInfo != null && wifiNetInfo.isConnected())
					|| (etherNetInfo != null && etherNetInfo.isConnected())
					|| (mobileNetInfo != null && mobileNetInfo.isConnected())) {

				mWeatherUtil = new WeatherUtil(mCtx, mHandler);
				
				mIvWifi.setImageDrawable(mCtx.getResources()
						.getDrawable(R.drawable.icon_wifi_empty));
				mHandler.removeMessages(UPDATE_WIFI_RSSI);
				mHandler.sendEmptyMessageDelayed(UPDATE_WIFI_RSSI,1000);
				
				// connect network
				Log.e(TAG, "wifi connect");
			} else {
				Log.e(TAG, "wifi unconnect");
				// unconnect network

			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mConnectionReceiver != null) {
			try {
				this.unregisterReceiver(mConnectionReceiver);
			} catch (IllegalArgumentException err) {
				return;
			} catch (Exception err) {
				return;
			}
		}
		
       
	}

}
