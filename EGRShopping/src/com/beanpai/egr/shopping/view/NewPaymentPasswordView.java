package com.beanpai.egr.shopping.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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
 * 输入新的支付密码
 * @author WGQ
 */
public class NewPaymentPasswordView extends FrameLayout implements OnClickListener {

	private Context mContext;
	private Handler mHandler;
	@SuppressWarnings("unused")
	private MemberInfo mMemberInfo;
	private DeviceManager mDeviceManager = null;
	
	private String password = "";
	
	private EditText mEtNewPayPassword,mEtNewPayPasswords;
	private TextView mTvNewPayNextStep;
	
	public NewPaymentPasswordView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		
		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.new_payment_password_view,null);
		addView(localView);
		
		mEtNewPayPassword = (EditText) localView.findViewById(R.id.et_new_pay_password);
		mEtNewPayPasswords = (EditText) localView.findViewById(R.id.et_new_pay_passwords);
		
		mTvNewPayNextStep = (TextView) localView.findViewById(R.id.tv_new_pay_next_step);
		
		initListener();
	}
	
	//绑定监听事件
	private void initListener()
	{
		mTvNewPayNextStep.setOnClickListener(this);
		
		mEtNewPayPassword.requestFocus();
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId())
		{
			case R.id.tv_new_pay_next_step:
				String password = mEtNewPayPassword.getText().toString().trim();
				String passwords = mEtNewPayPasswords.getText().toString().trim();
				if(password.length() == 6 && password.equals(passwords))
				{
					
					new DataTask().execute();
				}else{
					Toast.makeText(mContext, "请输入6位数的支付密码!", Toast.LENGTH_LONG).show();
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
		
		@SuppressWarnings("static-access")
		@Override
		protected String doInBackground(Void... params) 
		{
			String result = null;
			String url = mDeviceManager.getUpdateMemberInfoUrl() + "&type=5&paypassword=" + password;
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
					Toast.makeText(mContext, "支付密码修改成功!", Toast.LENGTH_LONG).show();
					mHandler.sendEmptyMessage(Common.SET_PAYMENT_PASSWORD);
				}else{
					Toast.makeText(mContext, obj.getString("description"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}