package com.beanpai.egr.shopping.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.beanpai.egr.shopping.adapter.CategoryListAdapter;
import com.beanpai.egr.shopping.adapter.CommodityListAdapter;
import com.beanpai.egr.shopping.entity.Category;
import com.beanpai.egr.shopping.entity.CategoryInfo;
import com.beanpai.egr.shopping.entity.Commodity;
import com.beanpai.egr.shopping.entity.CommodityInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.beanpai.egr.shopping.utils.MsgWhat;
import com.beanpai.egr.shopping.utils.Utils;
import com.beanpai.egr.shopping.utils.WebServiceLoader;
import com.egreat.devicemanger.DeviceManager;
import com.egreat.devicemanger.LoginStatus;
import com.mgle.shopping.R;

public class MainActivity extends Activity implements View.OnClickListener,View.OnFocusChangeListener,View.OnKeyListener {
	
	protected static final String TAG = "--Shopping.MainActitvit--";
	
	private int mSelectIndex = -1;
	private int mCommoditySelectIndex = -1;
	
	DeviceManager mDeviceManager = null;
	private LoginThread mLoginThread = null;
	private GetEPGThread mGetEPGThread = null;
	private WebServiceLoader mWebServiceLoader = null;
	
	private Context mContext;
	private WindowManager mWindowManager;
	
	private ProgressBar mProgressBar;
	
	private RelativeLayout mMain,mLayoutRecorder;
	private RelativeLayout mLayoutScore;
	
	private ListView mCategoryList;
	private CategoryListAdapter mCategoryAdapter;
	private ImageView mBannerImage;
	private RelativeLayout mGridviewParent;
	private RelativeLayout mContentLayout;

	private GridView mCommodityList;
	private CommodityListAdapter mCommodityAdapter;
	
	private Category mCurrentCategory;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case MsgWhat.NO_NET:
					hideLoadingProgress();
					Common.showMessage(mContext,"Net Error!");
					break;
				case MsgWhat.DEVICE_LOGIN_FAIL:
					Log.e(TAG, "login fail!");
					break;
				case MsgWhat.DEVICE_LOGIN_OK:
					Log.e(TAG, "login ok!");
					mHandler.sendEmptyMessage(MsgWhat.DEVICE_GET_EPG_INFO);
					mHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_MEMBER_INFO, 500);
					break;
				case MsgWhat.DEVICE_MEMBER_INFO:
					Log.e(TAG, "member json: " + DeviceManager.getMemoryJsonData());
					mHandler.removeMessages(MsgWhat.DEVICE_MEMBER_INFO);
					mHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_MEMBER_INFO, 30 * 60 * 1000);
					break;
				case MsgWhat.DEVICE_GET_EPG_INFO:
					mGetEPGThread = new GetEPGThread();
					mGetEPGThread.start();
					mHandler.removeMessages(MsgWhat.DEVICE_GET_EPG_INFO);
					mHandler.sendEmptyMessageDelayed(MsgWhat.DEVICE_GET_EPG_INFO, 30 * 60 * 1000);
					break;
				case MsgWhat.GET_NAV_CATEGORY:
					showLoadingProgress();
					mWebServiceLoader.getCategoryContentLoader(mHandler,mContext,Utils.WEB_SERVICE_URL + Utils.NAV_CATEGORY_URL);
					break;
				case MsgWhat.GET_NAV_CATEGORY_RESULT:
					hideLoadingProgress();
					if (msg.obj == null) 
					{
						Common.showMessage(mContext,"no nva category!");
						return;
					}
					CategoryInfo categoryInfo = (CategoryInfo) msg.obj;
					changeNavCategoryData(categoryInfo);
					break;
				case MsgWhat.GET_COMMODITY_CONTENT:
					showLoadingProgress();
					String url = (String) msg.obj;
					Log.e(TAG, "GET_COMMODITY_CONTENT...url=" + url);
					mWebServiceLoader.getCommondityContentLoader(mHandler,mContext, url);
					break;
				case MsgWhat.GET_COMMODITY_CONTENT_RESULT:
					hideLoadingProgress();
					if (msg.obj == null)
					{
						Common.showMessage(mContext,"no commodity!");
						return;
					}
					CommodityInfo commdityInfo = (CommodityInfo) msg.obj;
					changeCommodityData(commdityInfo);
					break;
			}
			super.handleMessage(msg);
		};
	};
	
	protected void onCreate(Bundle paramBundle) 
	{
		super.onCreate(paramBundle);

		setContentView(R.layout.main_layout);

		init();
		initView();
	}

	private void init() 
	{
		mContext = MainActivity.this;
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		
		mDeviceManager = DeviceManager.getInstance(mContext);
		
		mWebServiceLoader = new WebServiceLoader();
		
		IntentFilter connectIntentFilter = new IntentFilter();
		connectIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectionReceiver, connectIntentFilter);
		
		hideLoadingProgress();
	}
	
	@SuppressLint("InflateParams")
	private void initView() 
	{
		mProgressBar = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.loading_progress_layout, null);
		
		mMain = (RelativeLayout) findViewById(R.id.main);
		mLayoutRecorder = (RelativeLayout) findViewById(R.id.layout_recoder);
		mLayoutScore = (RelativeLayout) findViewById(R.id.layout_score);
		mCategoryList = (ListView) findViewById(R.id.list_category);
		
		mContentLayout = (RelativeLayout) findViewById(R.id.content_layout);
		mBannerImage = (ImageView) findViewById(R.id.img_banner);
		mGridviewParent = (RelativeLayout) findViewById(R.id.gridview_parent);
		mCommodityList = (GridView) findViewById(R.id.gridview_content);
		
		initListener();
	}

	private void initListener()
	{
		mLayoutRecorder.setOnFocusChangeListener(this);
		mLayoutScore.setOnFocusChangeListener(this);
		mCategoryList.setOnFocusChangeListener(this);
		mCommodityList.setOnFocusChangeListener(this);
		
		mLayoutRecorder.setOnClickListener(this);
		mLayoutScore.setOnClickListener(this);
		
		mCategoryList.setOnItemSelectedListener(mCategoryListSelectedListener);
		mCommodityList.setOnItemSelectedListener(mCommodityListSelectedListener);
		
		mCommodityList.setOnItemClickListener(mCommodityListClickListener);
		
		mLayoutScore.setOnKeyListener(this);
		mCategoryList.setOnKeyListener(this);
		mCommodityList.setOnKeyListener(this);
		
		mCommodityList.setFocusable(false);
		
		mCategoryList.requestFocus();
	}
	
	private OnItemSelectedListener mCategoryListSelectedListener = new OnItemSelectedListener() 
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,long id) 
		{
			Log.e("onItemSelected", "------onItemSelected="+mSelectIndex);
			if(mSelectIndex == -1 || mSelectIndex != position)
			{
				mSelectIndex = position;
				mCurrentCategory = mCategoryAdapter.getItem(position);
				if (mCurrentCategory != null && mCurrentCategory.programListUrl != null)
				{
					mHandler.removeMessages(MsgWhat.GET_COMMODITY_CONTENT);
					//mHandler.sendMessage(mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT, 0, 0,mCurrentCategory.programListUrl));
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT, 0, 0,mCurrentCategory.programListUrl), 1000);
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
	private OnItemSelectedListener mCommodityListSelectedListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,long id) 
		{
			mCommoditySelectIndex = position;
			if (mCommoditySelectIndex < 4) {
				mBannerImage.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				mGridviewParent.setLayoutParams(rl);
				rl.addRule(RelativeLayout.BELOW, R.id.img_banner);
			} else {
				mBannerImage.setVisibility(View.INVISIBLE);
				mGridviewParent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			Log.d("OnItemSelectedListener", "OnItemSelectedListener="+ mCommoditySelectIndex);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			
		}
	};
	
	private OnItemClickListener mCommodityListClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
		{
			try {
				Commodity commodity = mCommodityAdapter.getItem(position);
				
				Log.e(TAG, "mCommodityList..onItemSelected...position=" + position + "; commodity.url=" + commodity.detailUrl);
				
				Intent intent = new Intent(mContext, DeltailsActivity.class);
				intent.putExtra("json_url", commodity.detailUrl);
				intent.putExtra("poster_url", commodity.fileurl);
				startActivity(intent);
			} catch (Exception err) {
				err.printStackTrace();
				Common.showMessage(mContext,"启动失败！");
			}
		}
	};

	@SuppressLint("InlinedApi")
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo etherNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			NetworkInfo mobileNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			Log.e(TAG, "mConnectionReceiver...wifiNetInfo=" + wifiNetInfo + "; etherNetInfo=" + etherNetInfo+ "; mobileNetInfo=" + mobileNetInfo);

			if ((wifiNetInfo != null && wifiNetInfo.isConnected()) || (etherNetInfo != null && etherNetInfo.isConnected()) || (mobileNetInfo != null && mobileNetInfo.isConnected())) 
			{

				mHandler.removeMessages(MsgWhat.GET_NAV_CATEGORY);
				mHandler.sendEmptyMessage(MsgWhat.GET_NAV_CATEGORY);

				mLoginThread = new LoginThread();
				mLoginThread.start();

				Log.e(TAG, "wifi connect");
			} else {
				Log.e(TAG, "wifi unconnect");
			}
		}
	};
	
	private void showLoadingProgress()
	{
		try {
			if (mProgressBar.getParent() == null)
			{
				LayoutParams lp = new LayoutParams();
				lp.format = PixelFormat.TRANSPARENT;
				lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;

				mWindowManager.addView(mProgressBar, lp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void hideLoadingProgress()
	{
		try {
			if (mProgressBar.getParent() != null)
			{
				mWindowManager.removeView(mProgressBar);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	private void changeNavCategoryData(CategoryInfo categoryInfo) 
	{
		Log.e(TAG, "changeNavCategoryData..categoryInfo=" + categoryInfo);
		try {
			if (categoryInfo == null) 
			{
				mCategoryAdapter.changData(null);
				return;
			}

			if (categoryInfo.categorys == null || categoryInfo.categorys.size() <= 0) 
			{
				mCategoryAdapter.changData(null);
				return;
			}

			if (mCategoryAdapter == null) 
			{
				mCategoryAdapter = new CategoryListAdapter(this, categoryInfo.categorys);
			} else {
				mCategoryAdapter.changData(categoryInfo.categorys);
			}
			//mCategoryAdapter.setSelctItem(0);

			mCategoryList.setAdapter(mCategoryAdapter);
			mCategoryList.setFocusableInTouchMode(true);
			mCurrentCategory = mCategoryAdapter.getItem(0);

			if (mCurrentCategory != null && mCurrentCategory.programListUrl != null)
			{
				mHandler.removeMessages(MsgWhat.GET_COMMODITY_CONTENT);
				mHandler.sendMessage(mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT, 0, 0, mCurrentCategory.programListUrl));
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	private void changeCommodityData(CommodityInfo commdityInfo) 
	{
		Log.e(TAG, "changeCommodityData...commdityInfo=" + commdityInfo);
		if (mCommodityAdapter == null)
		{
			mCommodityAdapter = new CommodityListAdapter(this, commdityInfo.commoditys);
		} else {
			mCommodityAdapter.changData(commdityInfo.commoditys);
		}
		mCommodityList.setAdapter(mCommodityAdapter);
	}
	
	class GetEPGThread extends Thread {
		@Override
		public void run() 
		{
			Log.e("GetEPGThread", "GetEPGThread.run()");
			DeviceManager.getEPGIndex();
		}
	}
	
	class LoginThread extends Thread {
		@Override
		public void run() 
		{
			Log.e("LoginThread", "TestThread.run()");
			Log.e("LoginThread", "DeviceManager.version=" + DeviceManager.getVersion());
			
			LoginStatus mLoginStatus = DeviceManager.Login();

			if (mLoginStatus != null && mLoginStatus.status == true) 
			{
				mHandler.sendEmptyMessage(MsgWhat.DEVICE_LOGIN_OK);
			} else{
				mHandler.sendEmptyMessage(MsgWhat.DEVICE_LOGIN_FAIL);
			}
		}
	}
	
	@Override
	protected void onDestroy() 
	{
		if (mConnectionReceiver != null)
		{
			try {
				this.unregisterReceiver(mConnectionReceiver);
			} catch (IllegalArgumentException err) {
				return;
			} catch (Exception err) {
				return;
			}
		}
		super.onDestroy();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId()) 
		{
			case R.id.list_category:
				Log.e("list_category", "list_category");
				if (hasFocus) 
				{
					int state = 0;
					if(mCategoryAdapter != null && mCategoryAdapter.getSelectItemIndex() > -1){
						state = mCategoryAdapter.getSelectItemIndex();
						mCategoryAdapter.setSelctItem(-1);
					}
					final int index = state;
					Log.e("index", "index="+index);
					if (index > -1) 
					{
						mCategoryList.postDelayed(new Runnable() 
						{  
						    @Override  
						    public void run()
						    {  
						    	mCategoryList.requestFocusFromTouch();  
				    			mCategoryList.setSelection(index);  
				    			mCategoryList.setSelector(R.drawable.nav_category_selected);
						    }  
						},100);
					}else{
						mCategoryList.setSelector(new ColorDrawable(Color.TRANSPARENT));
					}
				} else {
					mCategoryList.setSelector(new ColorDrawable(Color.TRANSPARENT));
				}
				break;
			case R.id.gridview_content:
				Log.d("gridview_content", "gridview_content="+hasFocus);
				Log.d("mSelectIndex", "mSelectIndex="+mSelectIndex);
				if (hasFocus) 
				{	Log.d("gridview_content", "gridview_content="+ mCommoditySelectIndex);
					mCategoryAdapter.setSelctItem(mSelectIndex);
					
					mCommodityList.setSelector(R.drawable.application_selected);
				}else{
					mCommodityList.setSelector(new ColorDrawable(Color.TRANSPARENT));
				}
				break;
			case R.id.layout_recoder: //交易记录
				Log.e("layout_recoder", "layout_recoder="+hasFocus);
				break;
			case R.id.layout_score: //赚取乐币
				Log.e("layout_score", "layout_score="+hasFocus);
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) 
		{
			case R.id.layout_recoder: //交易记录
				
				break;
			case R.id.layout_score: //赚取乐币
				startActivityByPackageName("com.mgle.watchads");
				break;
			default:
				break;
		}
	}
	
	private boolean startActivityByPackageName(String packageName) 
	{
		Log.e(TAG, "startActivityByPackageName...packagename = " + packageName);
		try {
			PackageManager pm = this.getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(packageName);
			this.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Common.showMessage(mContext,"未安装此应用");
		}
		return false;
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) 
	{
		Log.e("onKey", "onKey");
		switch (view.getId()) 
		{
			case R.id.list_category:
				Log.e("onKey", "onKey----list_category");
				if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					mCommodityList.setFocusable(true);
					mCommodityList.requestFocus();
				}
				break;
			case R.id.gridview_content:
				Log.e("onKey", "onKey----list_category");
				if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					if((mCommoditySelectIndex % 4) == 0)
					{
						mCommodityList.setFocusable(false);
						mCategoryList.requestFocus();
					}
				}
				break;
			case R.id.layout_score: //赚取乐币
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					mSelectIndex = 0;
					if (mCurrentCategory != null && mCurrentCategory.programListUrl != null)
					{
						mHandler.removeMessages(MsgWhat.GET_COMMODITY_CONTENT);
						mHandler.sendMessage(mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT, 0, 0,mCurrentCategory.programListUrl));
					}
				}
				break;
			default:
				break;
		}
		return false;
	}
	
	@SuppressLint("NewApi")
	private void initBackground() 
	{
		try{
			File file = new File(Utils.getRootPath()+"shopping_background.jpg");
			Log.e(TAG, "initData...file.exist="+file.exists());
			if(file.exists())
			{
				Log.e(TAG, "initData...file.exist="+file.getAbsolutePath());
				mMain.setBackground(Drawable.createFromPath(file.getAbsolutePath()));
			}
		}catch(Exception err){
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