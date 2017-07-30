package com.mgle.member.activity;

import java.io.File;

import com.mgle.member.R;
import com.mgle.member.entity.Category;
import com.mgle.member.entity.CategoryInfo;
import com.mgle.member.entity.CommodityInfo;
import com.mgle.member.util.Common;
import com.mgle.member.util.MsgWhat;
import com.mgle.member.util.Utils;
import com.mgle.member.util.WebServiceLoader;
import com.mgle.member.view.BalanceView;
import com.mgle.member.view.DetailsLayout;
import com.mgle.member.view.PaymentLayout;
import com.mgle.member.view.RechargeView;
import com.mgle.member.view.RecordLayout;
import com.mgle.member.view.WithdrawalsView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "---MainActivity---";
	
	private static final int REFRESH_TEXTVIEW_UI = 1001;
	
	@SuppressWarnings("unused")
	private DeviceManager mDeviceManager = null;
	private LoginThread mLoginThread = null;
	private GetEPGThread mGetEPGThread = null;
	
	private String[] mArrayTitle;
	
	private LinearLayout mMemberMain;
	private FrameLayout[] mFrameLayout;
	private TextView[] mTextView;
	private RelativeLayout mRlLayoutRight;
	
	private WebServiceLoader mWebServiceLoader = null;
	private Context  mContext;
	private BalanceView mBalanceView;
	
	@SuppressLint("HandlerLeak")
	private final Handler mMainHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
				case MsgWhat.DEVICE_LOGIN_FAIL:
					Log.e("Handler", "login fail!");
					break;
				case MsgWhat.DEVICE_LOGIN_OK:
					Log.e("Handler", "login ok!");
					mMainHandler.sendEmptyMessage(MsgWhat.DEVICE_GET_EPG_INFO);
					mMainHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_MEMBER_INFO, 500);
					break;
				case MsgWhat.DEVICE_MEMBER_INFO:
					Log.e("Handler", "member json: "+DeviceManager.getMemoryJsonData());
					mMainHandler.removeMessages(MsgWhat.DEVICE_MEMBER_INFO);
					mMainHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_MEMBER_INFO,30* 60 * 1000);
					break;
				case MsgWhat.DEVICE_GET_EPG_INFO:
					mGetEPGThread = new GetEPGThread();
					mGetEPGThread.start();
					mMainHandler.removeMessages(MsgWhat.DEVICE_GET_EPG_INFO);
					mMainHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_GET_EPG_INFO,30 * 60 * 1000);
					break;
				case MsgWhat.GET_NAV_CATEGORY:
					mWebServiceLoader.getCategoryContentLoader(mMainHandler,mContext,Utils.WEB_SERVICE_URL + Utils.NAV_CATEGORY_URL);
					break;
				case MsgWhat.GET_NAV_CATEGORY_RESULT:
					if (msg.obj == null) 
					{
						Common.showMessage(mContext,"no nva category!");
						return;
					}
					CategoryInfo categoryInfo = (CategoryInfo) msg.obj;
					if (categoryInfo != null && categoryInfo.categorys != null && categoryInfo.categorys.size() > 0)
					{
						Category currentCategory = categoryInfo.categorys.get(0);
						if(currentCategory!=null && currentCategory.programListUrl !=null){
						   mMainHandler.removeMessages(MsgWhat.GET_COMMODITY_CONTENT);
						   mMainHandler.sendMessage(mMainHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT, 0, 0, currentCategory.programListUrl));
						}
					}
					break;
				case MsgWhat.GET_COMMODITY_CONTENT:
					String url = (String) msg.obj;
					Log.e(TAG, "GET_COMMODITY_CONTENT...url=" + url);
					mWebServiceLoader.getCommondityContentLoader(mMainHandler,mContext, url);
					break;
				case MsgWhat.GET_COMMODITY_CONTENT_RESULT:
					if (msg.obj == null)
					{
						Common.showMessage(mContext,"no commodity!");
						return;
					}
					CommodityInfo commdityInfo = (CommodityInfo) msg.obj;
					//changeCommodityData(commdityInfo);
					if(mBalanceView !=null){
						Log.d("WUWUWU", "-=-===========>>>>>>=====================>>>");
						mBalanceView.changeCommodityData(commdityInfo.commoditys);
					}
					break;
				case REFRESH_TEXTVIEW_UI:
					if (msg.obj != null)
					{
						int state = (Integer) msg.obj;
						refreshTextViewUI(state, Color.parseColor("#5f5d6a"));
					}
					break;
				default:
					break;
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = MainActivity.this;
		
		mDeviceManager = DeviceManager.getInstance(MainActivity.this);
		mWebServiceLoader = new WebServiceLoader();
		
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectReceiver, mIntentFilter);
		
		init();
	}
	
	//初始化
	private void init() 
	{
		mArrayTitle = getResources().getStringArray(R.array.menu_label);
		mFrameLayout = new FrameLayout[6];
		mTextView = new TextView[6];
		initView();
		initListener();
		setRightData();
	}

	//初始化控件
	private void initView() 
	{
		mMemberMain = (LinearLayout) findViewById(R.id.member_main);
		
		this.mFrameLayout[0] = (FrameLayout) findViewById(R.id.fl_menu_00);
		this.mFrameLayout[1] = (FrameLayout) findViewById(R.id.fl_menu_01);
		this.mFrameLayout[2] = (FrameLayout) findViewById(R.id.fl_menu_02);
		this.mFrameLayout[3] = (FrameLayout) findViewById(R.id.fl_menu_03);
		this.mFrameLayout[4] = (FrameLayout) findViewById(R.id.fl_menu_04);
		this.mFrameLayout[5] = (FrameLayout) findViewById(R.id.fl_menu_05);
				
		this.mTextView[0] = (TextView) findViewById(R.id.tv_menu_00);
		this.mTextView[1] = (TextView) findViewById(R.id.tv_menu_01);
		this.mTextView[2] = (TextView) findViewById(R.id.tv_menu_02);
		this.mTextView[3] = (TextView) findViewById(R.id.tv_menu_03);
		this.mTextView[4] = (TextView) findViewById(R.id.tv_menu_04);
		this.mTextView[5] = (TextView) findViewById(R.id.tv_menu_05);
		
		this.mRlLayoutRight = (RelativeLayout) findViewById(R.id.rl_layout_right);
	}
	
	//绑定监听事件
	private void initListener() 
	{
		int i = 0;
		while (true) 
		{
			int j = this.mFrameLayout.length;
			if (i >= j) 
			{
				return;
			}
			this.mFrameLayout[i].setOnFocusChangeListener(mOnFocusChangeListener);
			this.mTextView[i].setText(mArrayTitle[i]);
			i += 1;
		}
	}
	
	//绑定数据
	private void setRightData() 
	{
		//右边内容
		mBalanceView = new BalanceView(MainActivity.this,mMainHandler);
		this.mRlLayoutRight.addView(mBalanceView);
		this.mFrameLayout[0].requestFocus();
	}
	
	//焦点事件
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener()
	{
		@Override
		public void onFocusChange(View view, boolean hasFocus) 
		{
			switch (view.getId()) 
			{
				case R.id.fl_menu_00:
					Log.e("hasFocus", "fl_menu_00="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof BalanceView)
						{
							
						}else{
							mMainHandler.removeMessages(MsgWhat.GET_NAV_CATEGORY);
							mMainHandler.sendEmptyMessage(MsgWhat.GET_NAV_CATEGORY);
							mRlLayoutRight.removeAllViews();
							mBalanceView = new BalanceView(MainActivity.this,mMainHandler);
							mRlLayoutRight.addView(mBalanceView);
							
						}
						refreshTextViewUI(0, R.drawable.tv_select_bg);
					}
					break;
				case R.id.fl_menu_01:
					Log.e("hasFocus", "fl_menu_01="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof DetailsLayout)
						{
							
						}else{
							mRlLayoutRight.removeAllViews();
							DetailsLayout mDetailsLayout = new DetailsLayout(MainActivity.this,mMainHandler);
							mRlLayoutRight.addView(mDetailsLayout);
						}
						refreshTextViewUI(1,R.drawable.tv_select_bg);
					}
					break;
				case R.id.fl_menu_02:
					Log.e("hasFocus", "fl_menu_02="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof PaymentLayout)
						{
							
						}else{
							mRlLayoutRight.removeAllViews();
							PaymentLayout mPaymentLayout = new PaymentLayout(MainActivity.this,mMainHandler);
							mRlLayoutRight.addView(mPaymentLayout);
						}
						refreshTextViewUI(2,R.drawable.tv_select_bg);
					}
					break;
				case R.id.fl_menu_03:
					Log.e("hasFocus", "fl_menu_03="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof RecordLayout)
						{
							
						}else{
							mRlLayoutRight.removeAllViews();
							RecordLayout mRecordLayout = new RecordLayout(MainActivity.this,mMainHandler);
							mRlLayoutRight.addView(mRecordLayout);
						}
						refreshTextViewUI(3,R.drawable.tv_select_bg);
					}
					break;
				case R.id.fl_menu_04:
					Log.e("hasFocus", "fl_menu_04="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof RechargeView)
						{
							
						}else{
							mRlLayoutRight.removeAllViews();
							RechargeView mRechargeView = new RechargeView(MainActivity.this);
							mRlLayoutRight.addView(mRechargeView);
						}
						refreshTextViewUI(4,R.drawable.tv_select_bg);
					}
					break;
				case R.id.fl_menu_05:
					Log.e("hasFocus", "fl_menu_05="+hasFocus);
					if(hasFocus)
					{
						if(mRlLayoutRight.getChildAt(0) instanceof WithdrawalsView)
						{
							
						}else{
							mRlLayoutRight.removeAllViews();
							WithdrawalsView mWithdrawalsView = new WithdrawalsView(MainActivity.this);
							mRlLayoutRight.addView(mWithdrawalsView);
						}
						refreshTextViewUI(5,R.drawable.tv_select_bg);
					}
					break;
				default:
					break;
			}
		}
	};
	
	class LoginThread extends Thread {
		@Override
		public void run()
		{
			Log.d("LoginThread", "TestThread.run()");
			Log.d("LoginThread","DeviceManager.version=" + DeviceManager.getVersion());
			LoginStatus mLoginStatus = DeviceManager.Login();

			if (mLoginStatus != null && mLoginStatus.status == true) {
				mMainHandler.sendEmptyMessage(MsgWhat.DEVICE_LOGIN_OK);
			} else {
				mMainHandler.sendEmptyMessage(MsgWhat.DEVICE_LOGIN_FAIL);
			}
		}
	}
	
	class GetEPGThread extends Thread {
		@Override
		public void run() 
		{
			Log.d("GetEPGThread", "GetEPGThread.run()");
			DeviceManager.getEPGIndex();
		}
	}
	
	@SuppressLint("InlinedApi")
	private BroadcastReceiver mConnectReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo etherNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			NetworkInfo mobileNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			Log.e("BroadcastReceiver", "mConnectionReceiver...wifiNetInfo=" + wifiNetInfo+ "; etherNetInfo=" + etherNetInfo + "; mobileNetInfo=" + mobileNetInfo);

			if ((wifiNetInfo != null && wifiNetInfo.isConnected()) || (etherNetInfo != null && etherNetInfo.isConnected()) || (mobileNetInfo != null && mobileNetInfo.isConnected())) 
			{
				mMainHandler.removeMessages(MsgWhat.GET_NAV_CATEGORY);
				mMainHandler.sendEmptyMessage(MsgWhat.GET_NAV_CATEGORY);

				mLoginThread = new LoginThread();
				mLoginThread.start();
				
				Log.e(TAG, "wifi connect");
			} else {
				Log.e(TAG, "wifi unconnect");
			}
		}
	};
		
	@Override
	protected void onDestroy() 
	{
		if (mConnectReceiver != null) 
		{
			try {
				this.unregisterReceiver(mConnectReceiver);
			} catch (IllegalArgumentException err) {
				return;
			} catch (Exception err) {
				return;
			}
		}
		super.onDestroy();
	}
	
	private void refreshTextViewUI(int index,int color)
	{
		for(int i = 0;i < mFrameLayout.length; i++)
		{
			if(i == index)
			{
				mTextView[i].setBackgroundColor(color);
				mTextView[i].setTextColor(Color.parseColor("#FFFFFF"));
			}else{
				mTextView[i].setBackgroundColor(Color.parseColor("#00000000"));
				mTextView[i].setTextColor(Color.parseColor("#929293"));
			}
		}
	}
	
	@SuppressLint("NewApi")
	private void initBackground()
	{
		try {
			File file = new File(Utils.getRootPath() + "member_background.jpg");
			Log.e(TAG, "initData...file.exist=" + file.exists());
			if (file.exists()) {
				Log.e(TAG, "initData...file.exist=" + file.getAbsolutePath());
				mMemberMain.setBackground(Drawable.createFromPath(file.getAbsolutePath()));
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		initBackground();
	}
}