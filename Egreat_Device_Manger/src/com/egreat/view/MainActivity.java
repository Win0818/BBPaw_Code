package com.egreat.view;

import com.egreat.devicemanger.DeviceManager;
import com.egreat.devicemanger.LoginStatus;
import com.egreat.devicemanger.R;
import com.egreat.devicemanger.R.layout;
import com.egreat.devicemanger.UpdateInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button mButton = null;

	TestThread mTestThread = null;

	Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this;

		mButton = (Button) findViewById(R.id.button1);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTestThread = new TestThread();
				mTestThread.start();
			}
		});

	}
	
	public static final int LOGIN_OK = 0;
	public static final int LOGIN_FAIL = 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_OK:
				Toast.makeText(mContext, "LOGIN OK!", Toast.LENGTH_LONG).show();
				break;
			case LOGIN_FAIL:
				Toast.makeText(mContext, "LOGIN FAIL!", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	};

	class TestThread extends Thread {

		@Override
		public void run() {
			Log.d("test thread", "TestThread.run()");
			Log.d("test thread", "DeviceManager.version=" + DeviceManager.getVersion());
			DeviceManager.getInstance(mContext);
			LoginStatus mLoginStatus = DeviceManager.Login();
			UpdateInfo mUpdateInfo = null;
			if (mLoginStatus != null && mLoginStatus.status == true) {
				mHandler.sendEmptyMessage(LOGIN_OK);
				mUpdateInfo = DeviceManager.getApkUpdateInfo();
				if (mUpdateInfo != null) {
					Log.d("test thread", "apkurl=" + mUpdateInfo.apkurl);
					Log.d("test thread", "verName=" + mUpdateInfo.verName);
				}
				DeviceManager.getEPGIndex();
				Log.d("test thread", "rollText=" + DeviceManager.getRollText());
				Log.d("test thread", "indexJsonData=" + DeviceManager.getIndexJsonData());
			}
			else
				mHandler.sendEmptyMessage(LOGIN_FAIL);
		}
	}

}
