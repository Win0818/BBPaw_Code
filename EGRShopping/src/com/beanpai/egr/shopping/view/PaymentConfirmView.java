package com.beanpai.egr.shopping.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.secret.AesUserPass;
import com.beanpai.egr.shopping.utils.Common;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.shopping.R;
import com.umeng.common.Log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.HttpUtils;

/**
 * 支付确认
 * @author WGQ
 */
public class PaymentConfirmView extends FrameLayout implements OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	private CommodityDetailInfo mCommdityInfo;
	
	private EditText mEtConfirmPasswordPayEditText;
	private TextView mTvConfirmPayNextStep,mTvRetrievePassword;
	
	public PaymentConfirmView(Context context,Handler mHandler,MemberInfo mMemberInfo,CommodityDetailInfo mCommdityInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		this.mCommdityInfo = mCommdityInfo;
		
		initView();
	}
	
	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.payment_confirm_view,null);
		addView(localView);
		
		mEtConfirmPasswordPayEditText = (EditText) localView.findViewById(R.id.et_confirm_password_pay);
		
		mTvConfirmPayNextStep = (TextView) localView.findViewById(R.id.tv_confirm_pay_next_step);
		mTvRetrievePassword = (TextView) localView.findViewById(R.id.tv_retrieve_password);
		
		initListener();
	}

	//绑定监听
	private void initListener()
	{
		mTvConfirmPayNextStep.setOnClickListener(this);
		mTvRetrievePassword.setOnClickListener(this);
		
		mEtConfirmPasswordPayEditText.requestFocus();
	}
	
	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_confirm_pay_next_step: //确认支付,购买完成
				String password = mEtConfirmPasswordPayEditText.getText().toString().trim();
				AesUserPass aesUserPass = new AesUserPass();
				Log.e("AesUserPass", "mMemberInfo.paypassword="+mMemberInfo.paypassword+";   encrypt"+aesUserPass.encrypt(password));
				if(mMemberInfo.paypassword != null && mMemberInfo.paypassword.equals(aesUserPass.encrypt(password)))
				{
					new DataTask().execute();
				}else{
					Toast.makeText(mContext, "支付密码不正确!", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.tv_retrieve_password: //找回支付密码
				mHandler.sendEmptyMessage(Common.RETRIEVE_PASSWORD);
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
			String result = null;
			String url = mMemberInfo.addExchangeRecordUrl + "?memberid=" + DeviceManager.getCustomerId() +"&usertoken="+DeviceManager.getToken()+ "&type=5&commodityid=" + mCommdityInfo.id+"&ExchangeNum="+mCommdityInfo.num;
			Log.e("doInBackground", "doInBackground="+url);
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
					Toast.makeText(mContext, "创建兑换商品的订单成功!", Toast.LENGTH_LONG).show();
					mHandler.sendEmptyMessage(Common.PURCHASE_COMPLETED);
				}else{
					Toast.makeText(mContext, obj.getString("description"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}