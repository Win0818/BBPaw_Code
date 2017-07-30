package com.beanpai.egr.shopping.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.shopping.R;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.HttpUtils;

/**
 * 修改收货地址
 * @author WGQ
 */
public class ChangeReceivingAddressView extends FrameLayout implements View.OnFocusChangeListener, View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	private DeviceManager mDeviceManager = null;
	
	private TextView mTvReceiptRealName,mTvReceiptContactPhone,mTvReceiptCommonTelephone;
	private RelativeLayout mRlReceiptStreetAddress,mRlReceiptCellName,mRlReceiptCorridorUnit;
	private EditText mEtReceiptStreetAddress,mEtReceiptCellName,mEtReceiptCorridorUnit;
	private TextView mTvReceiptSvaeTextView;
	
	private String username = "";
	private String mobile = "";
	private String address = "";
	private String villagename = "";
	private String corridorunit = "";
	
	public ChangeReceivingAddressView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		mDeviceManager = DeviceManager.getInstance(mContext);
		
		initView();
	}
	
	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.change_receiving_address_view,null);
		addView(localView);
		
		mTvReceiptRealName = (TextView) localView.findViewById(R.id.tv_receipt_real_name);
		mTvReceiptContactPhone = (TextView) localView.findViewById(R.id.tv_receipt_contact_phone);
		mTvReceiptCommonTelephone = (TextView) localView.findViewById(R.id.tv_receipt_common_telephone);
		
		mRlReceiptStreetAddress = (RelativeLayout) localView.findViewById(R.id.rl_receipt_street_address);
		mRlReceiptCellName = (RelativeLayout) localView.findViewById(R.id.rl_receipt_cell_name);
		mRlReceiptCorridorUnit = (RelativeLayout) localView.findViewById(R.id.rl_receipt_corridor_unit);
		
		mEtReceiptStreetAddress = (EditText) localView.findViewById(R.id.et_receipt_street_address);
		mEtReceiptCellName = (EditText) localView.findViewById(R.id.et_receipt_cell_name);
		mEtReceiptCorridorUnit = (EditText) localView.findViewById(R.id.et_receipt_corridor_unit);
		
		mTvReceiptSvaeTextView = (TextView) localView.findViewById(R.id.tv_receipt_svae);
		
		initData();
		initListener();
	}
		
	private void initData() 
	{
		mTvReceiptRealName.setText(mMemberInfo.customername);
		mTvReceiptContactPhone.setText(mMemberInfo.mobile);
		mTvReceiptCommonTelephone.setText(mMemberInfo.mobile);
		mEtReceiptStreetAddress.setText("" + mMemberInfo.linkaddress);
		mEtReceiptCellName.setText(mMemberInfo.villagename);
		mEtReceiptCorridorUnit.setText(mMemberInfo.corridorunit);
	}

	//绑定监听
	private void initListener()
	{
		mEtReceiptStreetAddress.setOnFocusChangeListener(this);
		mEtReceiptCellName.setOnFocusChangeListener(this);
		mEtReceiptCorridorUnit.setOnFocusChangeListener(this);
		
		mTvReceiptSvaeTextView.setOnClickListener(this);
		
		mEtReceiptStreetAddress.requestFocus();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId()) 
		{
			case R.id.et_receipt_street_address:
				if(hasFocus)
				{
					mRlReceiptStreetAddress.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlReceiptStreetAddress.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_receipt_cell_name:
				if(hasFocus)
				{
					mRlReceiptCellName.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlReceiptCellName.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_receipt_corridor_unit:
				if(hasFocus)
				{
					mRlReceiptCorridorUnit.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlReceiptCorridorUnit.setBackgroundColor(color.transparent);
				}
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
			case R.id.tv_receipt_svae://保存
				username = mTvReceiptRealName.getText().toString().trim();
				mobile = mTvReceiptContactPhone.getText().toString().trim();
				address = mEtReceiptStreetAddress.getText().toString().trim();
				villagename = mEtReceiptCellName.getText().toString().trim();
				corridorunit = mEtReceiptCorridorUnit.getText().toString().trim();
				new DataTask().execute();
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
		
		@SuppressWarnings("static-access")
		@Override
		protected String doInBackground(Void... params) 
		{
			String result = null;
			String url = mDeviceManager.getUpdateMemberInfoUrl() + "&type=4&mobile=" + mobile + "&villagename=" + villagename + "&corridorunit=" + corridorunit;
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
				JSONTokener jsonParser = new JSONTokener(result);
				JSONObject obj = (JSONObject) jsonParser.nextValue();
				int res = Integer.parseInt(obj.getString("result"));
				if(res == 1)
				{
					Toast.makeText(mContext, "信息更新成功!", Toast.LENGTH_LONG).show();
					mHandler.sendEmptyMessage(Common.PAYMENT_CONFIRM);
				}else{
					Toast.makeText(mContext, obj.getString("description"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}