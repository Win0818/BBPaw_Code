package com.mgle.member.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.egreat.devicemanger.DeviceManager;
import com.mgle.member.R;
import com.mgle.member.entity.MemberInfo;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.HttpUtils;

public class DetailsLayout extends FrameLayout implements View.OnClickListener,View.OnFocusChangeListener {

	private static final int REFRESH_TEXTVIEW_UI = 1001;
	
	private Activity mActivity;
	private Handler mMainHandler;
	private MemberInfo mMemberInfo;
	
	private EditText[] mEditText;
	private TextView mEditingTextView,mSaveTextView,mModifyTextView;

	private String username = "";
	private String mobile = "";
	private String address = "";
	private String villagename = "";
	private String corridorunit = "";
	
	@SuppressLint("HandlerLeak")
	private  Handler mHandler = new  Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					mHandler.postDelayed(runnable, 1000);
					break;
				case 1:
					mHandler.removeCallbacks(runnable);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public DetailsLayout(Activity mActivity,Handler mMainHandler) 
	{
		super((Context)mActivity);
		this.mActivity = mActivity;
		this.mMainHandler = mMainHandler;
		this.mEditText = new EditText[7];
		
		mMemberInfo = new MemberInfo();
		
		initView();
		mHandler.sendEmptyMessage(0);
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView() 
	{
		View localView = LayoutInflater.from(mActivity).inflate(R.layout.details_view,null);
		addView(localView);
		
		this.mEditText[0] = (EditText) localView.findViewById(R.id.ed_details_00);
		this.mEditText[1] = (EditText) localView.findViewById(R.id.ed_details_01);
		this.mEditText[2] = (EditText) localView.findViewById(R.id.ed_details_02);
		this.mEditText[3] = (EditText) localView.findViewById(R.id.ed_details_03);
		this.mEditText[4] = (EditText) localView.findViewById(R.id.ed_details_04);
		this.mEditText[5] = (EditText) localView.findViewById(R.id.ed_details_05);
		this.mEditText[6] = (EditText) localView.findViewById(R.id.ed_details_06);
		
		this.mEditingTextView = (TextView) localView.findViewById(R.id.editing);
		this.mModifyTextView = (TextView) localView.findViewById(R.id.modify);
		this.mSaveTextView = (TextView) localView.findViewById(R.id.save);
		
		initData();
		initListener();
	}

	private void initData() 
	{
		paserMemberInfo(DeviceManager.getMemoryJsonData());
		mEditText[0].setText(mMemberInfo.customerno);
		mEditText[1].setText(mMemberInfo.customername);
		mEditText[2].setText(mMemberInfo.mobile);
		mEditText[3].setText(mMemberInfo.phone);
		mEditText[4].setText(mMemberInfo.linkaddress);
		mEditText[5].setText(mMemberInfo.villagename);
		mEditText[6].setText(mMemberInfo.corridorunit);
	}

	private void initListener()
	{
		mModifyTextView.setOnClickListener(this);
		mModifyTextView.setOnFocusChangeListener(this);
		mSaveTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.modify:
				initModifyDate();
				break;
			case R.id.save:
				new DataTask().execute();
				initSaveData();
				break;
			default:
				break;
		}
	}

	//初始化修改信息
	private void initModifyDate()
	{
		initVisibleState();
		this.mEditText[1].requestFocus();
		mEditingTextView.setVisibility(View.VISIBLE);
		mSaveTextView.setVisibility(View.VISIBLE);
		mModifyTextView.setVisibility(View.GONE);
		mModifyTextView.setFocusable(false);
		mModifyTextView.setFocusableInTouchMode(false);
		mSaveTextView.setFocusable(true);
		mSaveTextView.setFocusableInTouchMode(true);
	}
	
	//保存信息
	private void initSaveData()
	{
		initGONEleState();
		mModifyTextView.setVisibility(View.VISIBLE);
		mModifyTextView.setFocusable(true);
		mModifyTextView.setFocusableInTouchMode(true);
		mModifyTextView.requestFocus();
		mEditingTextView.setVisibility(View.GONE);
		mSaveTextView.setFocusable(false);
		mSaveTextView.setFocusableInTouchMode(false);
		mSaveTextView.setVisibility(View.GONE);
	}
	
	private void initVisibleState()
	{
		for (int i = 0; i < this.mEditText.length; i++) 
		{
			if (i != 0 && i != 2) 
			{
				this.mEditText[i].setEnabled(true);
				this.mEditText[i].setFocusable(true);
				this.mEditText[i].setFocusableInTouchMode(true);
				this.mEditText[i].setOnFocusChangeListener(this);
				this.mEditText[i].setBackgroundResource(R.drawable.edittext_style);
			}
		}
	}
	
	private void initGONEleState()
	{
		for (int i = 0; i < this.mEditText.length; i++) 
		{
			if (i != 0 && i != 2) 
			{
				this.mEditText[i].setEnabled(false);
				this.mEditText[i].setFocusable(false);
				this.mEditText[i].setFocusableInTouchMode(false);
				this.mEditText[i].setBackgroundColor(color.transparent);
			}
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus)
	{
		switch (view.getId()) 
		{
			case R.id.ed_details_01:
				if(hasFocus)
				{
					Message message = new Message();
					message.obj = 1;
					message.what = REFRESH_TEXTVIEW_UI;
					mMainHandler.sendMessage(message);
					mEditText[1].setSelection(0);
				}
				break;
			case R.id.ed_details_03:
				if(hasFocus)
				{
					mEditText[3].setSelection(0);
				}
				break;
			case R.id.ed_details_04:
				if(hasFocus)
				{
					mEditText[4].setSelection(0);
				}
				break;
			case R.id.ed_details_05:
				if(hasFocus)
				{
					mEditText[5].setSelection(0);
				}
				break;
			case R.id.ed_details_06:
				if(hasFocus)
				{
					mEditText[6].setSelection(0);
				}
				break;
			case R.id.modify:
				if(hasFocus)
				{
					Message message = new Message();
					message.obj = 1;
					message.what = REFRESH_TEXTVIEW_UI;
					mMainHandler.sendMessage(message);
				}
				break;
			default:
				break;
		}
	}
	
	private class DataTask extends AsyncTask<Void, Void, String> 
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			username = mEditText[1].getText().toString().trim();
			mobile = mEditText[3].getText().toString().trim();
			address = mEditText[4].getText().toString().trim();
			villagename = mEditText[5].getText().toString().trim();
			corridorunit = mEditText[6].getText().toString().trim();
			String result = null;
			String url = DeviceManager.getUpdateMemberInfoUrl() + "&type=4&customername"+username+"&mobile=" + mobile + "&linkaddress"+address+"&villagename=" + villagename + "&corridorunit=" + corridorunit;
			Log.e("url", "url="+url);
			HttpRequest request = new HttpRequest(url);
			HttpResponse respone = HttpUtils.httpGet(request);
			if (respone.getResponseCode() == 200)
			{
				result = respone.getResponseBody();
			}	
			Log.e("result", "result="+result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			try {
				JSONObject dataJson = new JSONObject(result);
				int res = dataJson.getInt("result");
				if(res == 1)
				{
					Toast.makeText(mActivity, "会员更新成功!", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mActivity, dataJson.getString("description"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void paserMemberInfo(String jsonData)
	{
		Log.d("paserMemberInfo", "static_MemoryJson=" + jsonData);
		try {
			if(jsonData == null) 
			{
				return;
			}
			
			JSONObject dataJson = new JSONObject(jsonData);
			
			if(dataJson != null)
			{
				mMemberInfo.customerId = dataJson.getInt("customerId");
				mMemberInfo.customerno = dataJson.getString("customerno");
				mMemberInfo.customername = dataJson.getString("customername");
				mMemberInfo.level = dataJson.getInt("level");
				mMemberInfo.mobile = dataJson.getString("mobile");
				mMemberInfo.phone = dataJson.getString("phone");
				mMemberInfo.onlineDuration = dataJson.getLong("onlineDuration");
				mMemberInfo.integral = dataJson.getInt("integral");
				mMemberInfo.paypassword = dataJson.getString("paypassword");
				mMemberInfo.linkaddress = dataJson.getString("linkaddress");
				mMemberInfo.villagename = dataJson.getString("villagename");
				mMemberInfo.corridorunit = dataJson.getString("corridorunit");
				
				mMemberInfo.addExchangeRecordUrl = dataJson.getString("addExchangeRecordUrl");
				mMemberInfo.seleteExchangeRecordUrl = dataJson.getString("seleteExchangeRecordUrl");
				mMemberInfo.delExchangeRecordUrl = dataJson.getString("delExchangeRecordUrl");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private Runnable runnable = new Runnable()
	{
		@Override
		public void run() 
		{
			Log.e("runnable", "runnable="+DeviceManager.getMemoryJsonData());
			paserMemberInfo(DeviceManager.getMemoryJsonData());
			if(mMemberInfo != null && mMemberInfo.customerId > 0)
			{
				mHandler.sendEmptyMessage(1);
				initData(); 
			}else{
				mHandler.sendEmptyMessage(0);
			}
		}
	};
}