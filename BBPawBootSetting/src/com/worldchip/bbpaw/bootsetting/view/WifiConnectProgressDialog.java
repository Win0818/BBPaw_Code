package com.worldchip.bbpaw.bootsetting.view;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.activity.MyApplication;
import com.worldchip.bbpaw.bootsetting.bean.WifiConnectInfo;
import com.worldchip.bbpaw.bootsetting.util.BBPawWifiManager;
import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;

public class WifiConnectProgressDialog extends Dialog implements android.view.View.OnClickListener{
	private static final String TAG = WifiConnectProgressDialog.class.getSimpleName();
	private static WifiConnectProgressDialog customProgressDialog = null;
	private Button mCancelBtn;
	private ImageView mConnectingIV;
	private RotateAnimation mConnectingProgressAnim = null;
	private BBPawWifiManager mWifiManager = null;
	private ConnectAsyncTask mConnectAsyncTask = null;
	private WifiConnectInfo mWifiConnectInfo = null;
	private TextView mTVFirstPoint;
	private TextView mTVSecondPoint;
	private TextView mTVThirdPoint;
	// point state
    private final int NO_POINT = 0;
	private final int ONE_POINT = 1;
	private final int TWO_POINT = 2;
	private final int THREE_POINT = 3;
	private int mPointCount = 0;
	private static final int REFRESH_POINT = 100;
	private static final int CHECK_NEWORK_CONNECTED = 101;
	private WifiConnectResultCallback mCallBack = null;
	private RelativeLayout mRlPWDError;
	private LinearLayout mLlConnectView;

	/** this is the network we are currently connected to */
	public static final int CURRENT = 0;
	/** supplicant will not attempt to use this network */
	public static final int DISABLED = 1;
	/** supplicant will consider this network available for association */
	public static final int ENABLED = 2;
	
	public static final int CHECK_NETWORK_CONN_DELAY = 10000;//当进行wifi连接后，延迟10s后再查看是否连接成功.
	
	
	
	public interface WifiConnectResultCallback {
		public void onConnectComplete(boolean isConnected); 
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_POINT:
				mPointCount ++;
				if (mPointCount < 0 || mPointCount > THREE_POINT) {
					mPointCount = NO_POINT;
				}
				updatePotion(mPointCount);
				mHandler.sendEmptyMessageDelayed(REFRESH_POINT, 500);
				break;
			case CHECK_NEWORK_CONNECTED:
				boolean networkConnected = Common.isNetworkConnected(getContext());
				Log.e(TAG, " networkConnected == "+networkConnected);
				if (networkConnected) {
					connectSuccess();
				} else {
					showConnectError();
					unRegisterWifiState();
				}
				break;
			default:
				break;
			}
		};
	};
	
	public WifiConnectProgressDialog(Context context){
		super(context);
	}
	
	public WifiConnectProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static WifiConnectProgressDialog createDialog(Context context){
		customProgressDialog = new WifiConnectProgressDialog(context,R.style.dialog_global);
		customProgressDialog.setContentView(R.layout.wifi_connect);
		Window dialogWindow = customProgressDialog.getWindow();
		WindowManager.LayoutParams layoutParams = dialogWindow
				.getAttributes();
		// 修改窗体宽高
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		layoutParams.format = PixelFormat.TRANSLUCENT;
		layoutParams.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(layoutParams);
		customProgressDialog.setCancelable(false);
		return customProgressDialog;
	}
	
	public void setConnectInfo(WifiConnectInfo connectInfo) {
		mWifiConnectInfo = connectInfo;
	}
	
	public void setWifiConnectResultCallback(WifiConnectResultCallback callback) {
		mCallBack = callback;
	}
	
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	mCancelBtn = (Button)customProgressDialog.findViewById(R.id.btn_connect_cancel);
    	mCancelBtn.setOnClickListener(this);
    	mConnectingIV = (ImageView)customProgressDialog.findViewById(R.id.connecting_iv);
    	mTVFirstPoint = (TextView) findViewById(R.id.tv_first_point);
		mTVSecondPoint = (TextView) findViewById(R.id.tv_second_point);
		mTVThirdPoint = (TextView) findViewById(R.id.tv_third_point);
		mRlPWDError = (RelativeLayout) findViewById(R.id.rl_pwd_error);
		mLlConnectView = (LinearLayout) findViewById(R.id.ll_connect_view);
    	if (mWifiManager == null) {
			mWifiManager = BBPawWifiManager.getInstance(MyApplication.getAppContext());
		}
    	mWifiManager.searchWifi();
    	connectWifi(mWifiConnectInfo);
    }
 
	
    public void connectWifi(WifiConnectInfo connectInfo) {
    	mConnectAsyncTask = new ConnectAsyncTask(connectInfo);
    	mConnectAsyncTask.execute();
    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_connect_cancel:
			mHandler.removeMessages(CHECK_NEWORK_CONNECTED);
			if (mConnectAsyncTask != null && !mConnectAsyncTask.isCancelled()) {
				mConnectAsyncTask.cancel(true);
			}
			stopConnectingAnim();
			dismiss();
			break;
		}
	}
	
	private void updatePotion(int count) {
		switch (count) {
		case NO_POINT:
			mTVFirstPoint.setVisibility(View.INVISIBLE);
			mTVSecondPoint.setVisibility(View.INVISIBLE);
			mTVThirdPoint.setVisibility(View.INVISIBLE);
			break;
		case ONE_POINT:
			mTVFirstPoint.setVisibility(View.VISIBLE);
			break;
		case TWO_POINT:
			mTVSecondPoint.setVisibility(View.VISIBLE);
			break;
		case THREE_POINT:
			mTVThirdPoint.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	private void startConnectingAnim() {
		if (mConnectingIV != null) {
			if (mConnectingProgressAnim == null) {
				mConnectingProgressAnim = new RotateAnimation(0f, 720f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				mConnectingProgressAnim
						.setInterpolator(new LinearInterpolator());
				mConnectingProgressAnim.setDuration(1500);
				mConnectingProgressAnim.setRepeatCount(Integer.MAX_VALUE);
				mConnectingProgressAnim.setFillAfter(true);
			}
			stopConnectingAnim();
			mConnectingIV.startAnimation(mConnectingProgressAnim);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(REFRESH_POINT);
			}
		}
	}
	
	private void stopConnectingAnim() {
		if (mConnectingProgressAnim != null) {
			if (mConnectingProgressAnim.hasStarted()) {
				if (mConnectingIV != null) {
					mConnectingIV.clearAnimation();
				}
			}
			if (mHandler != null) {
				mHandler.removeMessages(REFRESH_POINT);
			}
		}
	}
	
	class ConnectAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		private WifiConnectInfo connectInfo = null;
		
		public ConnectAsyncTask(WifiConnectInfo info) {
			connectInfo = info;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			startConnectingAnim();
		}

		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			stopConnectingAnim();
		}


		@Override
		protected Boolean doInBackground(Void... arg0) {
			if (connectInfo == null) {
				return false;
			}
			
			if (mWifiManager == null) {
				mWifiManager = BBPawWifiManager.getInstance(MyApplication.getAppContext());
			}
			
			int netId = mWifiManager.addNetwork(mWifiManager.createWifiInfo(
					connectInfo.getSSID(), connectInfo.getPassword(), connectInfo.getSecurityType()));
			Boolean addNetwork = mWifiManager.enableNetwork(netId, true);
			LogUtil.e("lee", "addNetwork isConnect == "+addNetwork+" netId == "+netId);
			return addNetwork;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				registerWifiState();
			} else {
				showConnectError();
			}
		}
	}
	
	private void connectSuccess() {
		stopConnectingAnim();
		if (mCallBack != null) {
			mCallBack.onConnectComplete(true);
		}
		dismiss();
	}
	
	private void showConnectError() {
		stopConnectingAnim();
		if (mLlConnectView != null) {
			mLlConnectView.setVisibility(View.GONE);
		}
		if (mRlPWDError != null) {
			mRlPWDError.setVisibility(View.VISIBLE);
		}
		if (mCallBack != null) {
			mCallBack.onConnectComplete(false);
		}
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		unRegisterWifiState();
		if (mConnectAsyncTask != null && !mConnectAsyncTask.isCancelled()) {
			mConnectAsyncTask.cancel(true);
		}
	}
	
	private void registerWifiState() {
        IntentFilter mWifiFilter = new IntentFilter();
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getContext().registerReceiver(mWifiConnectReceiver, mWifiFilter);
    }
	
	
	private void unRegisterWifiState() {
        try {
        	if (mWifiConnectReceiver != null) {
            	getContext().unregisterReceiver(mWifiConnectReceiver);
            }
        }catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
	
	
	 private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {
		 
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    //Log.e(TAG, "Wifi onReceive action = " + intent.getAction());
			if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
				switch (message) {
				case WifiManager.WIFI_STATE_DISABLED:
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					 mHandler.sendEmptyMessageDelayed(CHECK_NEWORK_CONNECTED, CHECK_NETWORK_CONN_DELAY);
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					break;
				case WifiManager.WIFI_STATE_UNKNOWN:
					break;
				}
			}
		}
	};
		    
}