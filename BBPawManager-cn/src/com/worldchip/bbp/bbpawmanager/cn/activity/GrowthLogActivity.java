package com.worldchip.bbp.bbpawmanager.cn.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.adapter.GrowupRecordAdapter;
import com.worldchip.bbp.bbpawmanager.cn.callbak.HttpResponseCallBack;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager;
import com.worldchip.bbp.bbpawmanager.cn.json.GrowthLogJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.HttpUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.LogUtil;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.view.GlobalProgressDialog;

public class GrowthLogActivity extends Activity implements OnClickListener {

	private static final String TAG = GrowthLogActivity.class.getSimpleName();
	private ListView mGrowupListView;
	private GlobalProgressDialog mGlobalProgressDialog;
	private GrowthDataLoadCallback mCallback;
	private static final int START_LOAD = 1;
	private static final int LOAD_COMPLETE = 2;
	private GrowupRecordAdapter mGrowthAdapter = null;

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_LOAD:
				startProgressDialog();
				break;
			case LOAD_COMPLETE:
				//List<GrowthMessage> infos = DataManager
				//		.getGrowthDatas(GrowthLogActivity.this);
				if (mGrowthAdapter != null && mInfos != null) {
					mGrowthAdapter.setDataList(mInfos);
					mGrowthAdapter.notifyDataSetChanged();
				}
				stopProgressDialog();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.growup_record_layout);
		initView();
		LoadDatas();
	}

	private void initView() {
		View recordLayout = findViewById(R.id.growup_record_layout);
		recordLayout.setBackgroundResource(Common.getResourcesId(
				MyApplication.getAppContext(), "growup_record_bg", "drawable"));
		findViewById(R.id.back_btn).setOnClickListener(this);
		mGrowupListView = (ListView) findViewById(R.id.growup_listview);
		mGrowupListView.setFastScrollEnabled(false);
		mGrowthAdapter = new GrowupRecordAdapter(GrowthLogActivity.this, null);
		mGrowupListView.setAdapter(mGrowthAdapter);

	}

	private void LoadDatas() {
		// TODO Auto-generated method stub
		mCallback = new GrowthDataLoadCallback();
		// String deviceId = Common.getCpuSerial(MyApplication.getAppContext());
		String email = getEmail();
		HttpUtils.doPost(Utils.HTTP_GROWTH_REQ_URL + email, mCallback,
				Utils.HTTP_TAG_GROWTH_LOAD);
	}

	public static final String PREFER_NAME = "save_account";

	private String getEmail() {
		Context otherAppsContext1 = null;
		try {
			otherAppsContext1 = createPackageContext("com.worldchip.bbp.ect",
					Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SharedPreferences mPref = otherAppsContext1.getSharedPreferences(
				PREFER_NAME, Context.MODE_MULTI_PROCESS);
		String account = mPref.getString("account", "");
		return account;
	}

	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			if (mGlobalProgressDialog.isShowing()) {
				mGlobalProgressDialog.dismiss();
			}
			mGlobalProgressDialog = null;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.back_btn:
			GrowthLogActivity.this.finish();
			return;
		default:
			break;
		}
	}

	
	private List<GrowthMessage> mInfos = null;
	private class GrowthDataLoadCallback implements HttpResponseCallBack {

		@Override
		public void onStart(String httpTag) {
			// TODO Auto-generated method stub
			mInfos = null;
			mHandler.sendEmptyMessage(START_LOAD);
		}

		@Override
		public void onSuccess(String result, String httpTag) {
			// TODO Auto-generated method stub
			LogUtil.e(TAG, "GrowthDataLoadCallback onSuccess : " + result);
			List<GrowthMessage> infos = GrowthLogJsonParse
					.doParseJsonToBean(result);
			//DataManager.updateGrowthToDB(GrowthLogActivity.this, infos);
			mInfos = infos;
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
		}

		@Override
		public void onFailure(Exception e, String httpTag) {
			// TODO Auto-generated method stub
			if (e != null) {
				LogUtil.e(TAG, e.toString());
			}
			mInfos = null;
			mHandler.sendEmptyMessage(LOAD_COMPLETE);
		}

		@Override
		public void onFinish(int result, String httpTag) {

		}

	}

}
