package com.worldchip.bbpaw.bootsetting.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.Utils;
import android.util.Log;
import android.view.KeyEvent;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button mNextBtn, mPassBtn;
	private MyApplication mApplication;
	private static final int FLAG_HOMEKEY_DISPATCHED = 100001; // 拦截 home按键

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mApplication = (MyApplication) this.getApplication();
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_main);
		hideSystemUI(this, true);
		mNextBtn = (Button) findViewById(R.id.next_btn);
		mNextBtn.setOnClickListener(this);
		mPassBtn = (Button) findViewById(R.id.pass_btn);
		mPassBtn.setOnClickListener(this);
		// Common.setPreferenceValue(Utils.IS_FIRST_START_KEY, "false");
		String startCount = Common.getPreferenceValue(Utils.IS_FIRST_START_KEY,
				"0");
		if (startCount.equals("0")) {
			Common.setPreferenceValue(Utils.IS_FIRST_START_KEY, "1");
		} else if (startCount.equals("1")) {
			Common.setPreferenceValue(Utils.IS_FIRST_START_KEY, "2");
		} else if (startCount.equals("2")) {
			Common.setPreferenceValue(Utils.IS_FIRST_START_KEY, "3");
		}

		//skipSetLanguage();///add modifi by xiaolp skipSetLanguage
	}

	private void skipSetLanguage() {
		Common.restPreference();
		Common.setPreferenceValue(Utils.LANGUAGE_SHARD_KEY, "en_US");
		Intent intent = new Intent(MainActivity.this, WifiListActivity.class);
		startActivity(intent);
	}

	/**
	 * 隐藏菜单
	 * 
	 * @param context
	 * @param flag
	 */
	public static void hideSystemUI(Context context, boolean flag) {
		Intent intent = new Intent();
		intent.setAction(Utils.UPDATE_SYSTEMUI);
		intent.putExtra("hide_systemui", flag);
		context.sendBroadcast(intent);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.next_btn) {
			if (mApplication.languageLocale != null) {
				setLanguage();
				Intent intent = new Intent(MainActivity.this,
						WifiListActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.language_no_select_text,
						Toast.LENGTH_SHORT).show();
			}
		} else if (arg0.getId() == R.id.pass_btn) {
			// this.finish();
			MyApplication.getInstance().exit();
		}
	}

	private void setLanguage() {
		if (mApplication.languageLocale != null) {
			Common.restPreference();
			Common.updateLanguage(mApplication.languageLocale);
			Common.setPreferenceValue(Utils.LANGUAGE_SHARD_KEY,
					mApplication.languageLocale.toString());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
