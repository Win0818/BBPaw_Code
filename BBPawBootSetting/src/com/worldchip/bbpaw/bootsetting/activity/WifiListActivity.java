package com.worldchip.bbpaw.bootsetting.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.adapter.WifiListAdapter;
import com.worldchip.bbpaw.bootsetting.bean.WifiConnectInfo;
import com.worldchip.bbpaw.bootsetting.util.BBPawWifiManager;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.WifiUtils;
import com.worldchip.bbpaw.bootsetting.view.WifiAPAlertDialog;
import com.worldchip.bbpaw.bootsetting.view.WifiConnectProgressDialog;
import com.worldchip.bbpaw.bootsetting.view.WifiConnectProgressDialog.WifiConnectResultCallback;
import android.view.KeyEvent;
import android.util.Log;

public class WifiListActivity extends Activity implements OnClickListener,
		OnItemClickListener, WifiConnectResultCallback{
	private Button mBtnBack;
	private Button mBtnNext;
	private Button mBtnPass;
	private ListView mWifiListView = null;
	private List<ScanResult> mScanResultList = null;

	private BBPawWifiManager mWifiManager = null;
	private final String TAG = "--WifiListActivity--";
	private WifiListAdapter mWifiListAdapter = null;
	private WifiAPAlertDialog mWifiAPDialog = null;
	private WifiConnectProgressDialog mConnectProgressDialog = null;
	private Timer mWifiRefreshTimer;
	private WifiRefreshTimerTask mWifiRefreshTimerTask;
	private static final int WIFI_REFRESH_DELAY_TIME = 10 * 1000;
	private static final int WIFI_REFRESH = 100;
	private static final int FLAG_HOMEKEY_DISPATCHED = 100001; //拦截 home按键
	
	private ScanResult mScanResult = null;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			   case WIFI_REFRESH:
				   searchWifi();
				   break;
					
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.wifi_list);
		MyApplication.isRegister = this.getIntent().getBooleanExtra("isRegister", false);
		if (mWifiManager == null) {
			mWifiManager = BBPawWifiManager.getInstance(MyApplication.getAppContext());
			mWifiManager.openWifi();
		}
		initView();
		initListView();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		searchWifi();
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopWifiRefreshTimer();
		stopProgressDialog();
		dismissWifiAPDialog();
	}


	private void searchWifi() {
		if (mWifiManager == null) {
			mWifiManager = BBPawWifiManager.getInstance(MyApplication.getAppContext());
		}
		mWifiManager.searchWifi();
		mScanResultList = mWifiManager.getScanrResults();
		if (mWifiListAdapter != null) {
			mWifiListAdapter.setDatas(mScanResultList);
			mWifiListAdapter.notifyDataSetChanged();
		}
	}

	

	private void initView() {
		mBtnBack = (Button) findViewById(R.id.btn_wifi_back);
		mBtnNext = (Button) findViewById(R.id.btn_wifi_next);
		mBtnPass = (Button) findViewById(R.id.btn_wifi_pass);
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnPass.setOnClickListener(this);
		startWifiRefreshTimer(WIFI_REFRESH_DELAY_TIME);
	}

	private void initListView() {
		mWifiListView = (ListView) findViewById(R.id.lv_wifi_select);
		mWifiListAdapter = new WifiListAdapter(this, mScanResultList);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_wifi_back:
			finish();
			//MyApplication.getInstance().exit();  ///add modifi by xiaolp skipSetLanguage
			break;
		case R.id.btn_wifi_next:
			showWifiAPDialog();
			break;
		case R.id.btn_wifi_pass:
			Intent intent1 = new Intent(WifiListActivity.this,
					RegisterActivity.class);
			startActivity(intent1);
			break;
		case R.id.btn_enter:
			startProgressDialog();
			break;
		case R.id.btn_cancel:
			dismissWifiAPDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if (mWifiListAdapter != null) {
			mWifiListAdapter.selection(position);
			mWifiListAdapter.notifyDataSetChanged();
			showWifiAPDialog();
		}
	}
	
	private void showWifiAPDialog() {
		// TODO Auto-generated method stub
		dismissWifiAPDialog();
		if (mWifiListAdapter != null) {
			mScanResult = mWifiListAdapter.getSelectionInfo();
			if (mScanResult != null) {
				mWifiAPDialog = new WifiAPAlertDialog.Builder(this,mScanResult).create();
				mWifiAPDialog.setOnclickListener(this);
				mWifiAPDialog.show();
			}
		}
	}
	
	
	private void dismissWifiAPDialog() {
		if (mWifiAPDialog != null) {
    		if (mWifiAPDialog.isShowing()) {
    			mWifiAPDialog.dismiss();
    		}
    		mWifiAPDialog = null;
    	}
	}
	
	private void startProgressDialog() {
		stopProgressDialog();
		String password = null;
		if (mWifiAPDialog != null) {
			password = mWifiAPDialog.getPassword();
		}
		if (password == null) {
			return;
		}
		if (mConnectProgressDialog == null) {
			mConnectProgressDialog = WifiConnectProgressDialog.createDialog(this);
		}
		if (mWifiListAdapter != null) {
			if (mScanResult == null) {
				return;
			}
			WifiConnectInfo connectInfo = new WifiConnectInfo();
			connectInfo.setSSID(mScanResult.SSID);
			if (password != null) {
				connectInfo.setPassword(password);
			}
			int securityType = WifiUtils.getSecurity(mScanResult);
			LogUtil.e(TAG, "securityType == "+securityType);
			connectInfo.setSecurityType(securityType);
			mConnectProgressDialog.setConnectInfo(connectInfo);
			mConnectProgressDialog.setWifiConnectResultCallback(this);
			mConnectProgressDialog.show();
		}
	}

	private void stopProgressDialog() {
		if (mConnectProgressDialog != null) {
			if (mConnectProgressDialog.isShowing()) {
				mConnectProgressDialog.dismiss();
			}
			mConnectProgressDialog = null;
		}
	}


	@Override
	public void onConnectComplete(boolean isConnected) {
		// TODO Auto-generated method stub
		if (isConnected) {
			searchWifi();
			goToRegister();
		}
	}
	
	private void goToRegister() {
		dissmissAllDialog();
		Intent intent = new Intent(WifiListActivity.this,
				RegisterActivity.class);
		startActivity(intent);
	}
	
	private void dissmissAllDialog() {
		stopProgressDialog();
		dismissWifiAPDialog();
	}
	
	public void startWifiRefreshTimer(long delayTime) {
		stopWifiRefreshTimer();
		if (mWifiRefreshTimer == null) {
			mWifiRefreshTimer = new Timer(true);
		}
		if (mWifiRefreshTimer != null) {
			mWifiRefreshTimerTask = new WifiRefreshTimerTask();
			mWifiRefreshTimer.schedule(mWifiRefreshTimerTask, 5000,delayTime);//5秒后以delayTime为周期重复执行
		}
	}

	public void stopWifiRefreshTimer() {
		if (mWifiRefreshTimer != null) {
			if (mWifiRefreshTimerTask != null) {
				mWifiRefreshTimerTask.cancel();
			}
		}
	}
	
	public class WifiRefreshTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mHandler!=null) {
				mHandler.sendEmptyMessage(WIFI_REFRESH);
			}
		}
	}
	
	@Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
