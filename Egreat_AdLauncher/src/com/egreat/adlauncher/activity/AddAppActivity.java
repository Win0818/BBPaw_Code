package com.egreat.adlauncher.activity;

import java.util.List;

import com.egreat.adlauncher.util.AllAppGridViewAdapter;
import com.egreat.adlauncher.util.AppTool;
import com.egreat.adlauncher.util.SettingUtils;
import com.mgle.launcher.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddAppActivity extends Activity implements OnItemSelectedListener, OnItemClickListener, OnClickListener,
		OnItemLongClickListener {

	
	public String LOGTAG = "AllAppActivity";
	public Context mContext = null;

	private GridView gridview = null;
	private int gridviewSelected = -1;

	private List<ResolveInfo> mAllAppInfo = null;
	private AllAppGridViewAdapter allappAdapter = null;

	private TextView appCountText = null;

	private LinearLayout menu = null;

	private ImageButton uninstallBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.layout_addicon);

		mAllAppInfo = AppTool.getAllApps(this);
		allappAdapter = new AllAppGridViewAdapter(this, mAllAppInfo);

		gridview = (GridView) findViewById(R.id.grid_view_main);
		gridview.setAdapter(allappAdapter);
		gridview.setOnItemSelectedListener(this);
		gridview.setOnItemClickListener(this);
		gridview.setOnItemLongClickListener(this);

		appCountText = (TextView) findViewById(R.id.app_count);
		appCountText.setText(getResources().getString(R.string.app_count, allappAdapter.getCount()));

		menu = (LinearLayout) findViewById(R.id.menu);
		uninstallBtn = (ImageButton) findViewById(R.id.uninstall);
		uninstallBtn.setOnClickListener(this);
		registerBroadcast();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.grid_view_main) {
			Log.d(LOGTAG, "on grid_view_main Item Selected position=" + position);
			gridviewSelected = position;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		gridviewSelected = -1;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.grid_view_main) {
			Log.d(LOGTAG, "on grid_view_main Item Clicked position=" + position);
			ResolveInfo resloveInfo = mAllAppInfo.get(position);

			if (resloveInfo != null) {
				String activityPackageName = resloveInfo.activityInfo.packageName;
				Log.d(LOGTAG, "activityPackageName=" + activityPackageName);
				String className = resloveInfo.activityInfo.name;
				Log.d(LOGTAG, "className=" + className);

				if (MainActivity.mCurrentSetApp == MainActivity.CURRENT_SET_APP01) {
					Log.d(LOGTAG, "setApp01PackageName=" + activityPackageName);
					SettingUtils.setApp01PackageName(mContext, activityPackageName);
					SettingUtils.setApp01DefaultValue(mContext, false);
				} else if (MainActivity.mCurrentSetApp == MainActivity.CURRENT_SET_APP02) {
					Log.d(LOGTAG, "setApp02PackageName=" + activityPackageName);
					SettingUtils.setApp02PackageName(mContext, activityPackageName);
					SettingUtils.setApp02DefaultValue(mContext, false);
				}

				//Intent intent = new Intent(Intent.ACTION_MAIN);
				//intent.addCategory(Intent.CATEGORY_LAUNCHER);
				//ComponentName componentName = new ComponentName(activityPackageName, className);

				//intent.setComponent(componentName);
				//startActivity(intent);
			}
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		Log.d(LOGTAG, "v.getId=" + v.getId());
		switch (v.getId()) {

		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(LOGTAG, "onResume");
		menu.setVisibility(View.GONE);
		gridview.setFocusable(true);
		gridview.requestFocus();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

		return false;
	}

	private void registerBroadcast() {
		IntentFilter intentFilter1 = new IntentFilter();
		IntentFilter intentFilter3 = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		intentFilter3.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter3.addDataScheme("package");
		// 注册监听函数
		registerReceiver(receiver, intentFilter3);
	}

	/**
	 * 广播监听接收器，监听应用的卸载和安装
	 */
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(LOGTAG, "action============" + action);
			if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
				Log.e(LOGTAG, "install / uninstall app!!!!!!!!!!!!!!!!!!!!");
				mAllAppInfo = AppTool.getAllApps(mContext);
				allappAdapter.setDataSource(mAllAppInfo);
				allappAdapter.notifyDataSetChanged();
				menu.setVisibility(View.GONE);
				gridview.setFocusable(true);
				gridview.requestFocus();
				Log.d(LOGTAG, "allappAdapter.getCount=" + allappAdapter.getCount());
				appCountText.setText(getResources().getString(R.string.app_count, allappAdapter.getCount()));
			}
		}
	};

}
