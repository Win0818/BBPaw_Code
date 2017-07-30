package com.beanpai.egr.shopping.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.entity.MsgResult;
import com.beanpai.egr.shopping.secret.MD5Util;
import com.beanpai.egr.shopping.utils.Common;
import com.beanpai.egr.shopping.utils.MsgWhat;
import com.beanpai.egr.shopping.utils.Utils;
import com.beanpai.egr.shopping.utils.WebServiceLoader;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 找回密码
 * @author WGQ
 */
public class RetrievePasswordView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	
	private WebServiceLoader mWebServiceLoader = null;
	
	private EditText mEtRetrievePasswordIpone;@SuppressLint("HandlerLeak")
	private Handler mSMSHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
				case MsgWhat.NO_NET:
				Log.e("SMSVerificationView", "Net Error!");
					break;
				case MsgWhat.SEND_MSG_INFO_RESULT:
					if (msg.obj == null) 
					{
						Log.e("SMSVerificationView", "SEND_MSG_INFO_RESULT OBJ null!");
						return;
					}
					MsgResult result = (MsgResult) msg.obj;
					Log.e("SMSVerificationView", "SEND_MSG_INFO_RESULT..result="+result);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	private TextView mTvRetrievePasswordNextStep;
	
	
	
	public RetrievePasswordView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		mWebServiceLoader = new WebServiceLoader();
		
		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.retrieve_password_view,null);
		addView(localView);
		
		mEtRetrievePasswordIpone = (EditText) localView.findViewById(R.id.et_retrieve_pwd_ipone);
				
		mTvRetrievePasswordNextStep = (TextView) localView.findViewById(R.id.tv_retrieve_password_next_step);
		
		initListener();
	}

	//绑定监听
	private void initListener() 
	{
		mTvRetrievePasswordNextStep.setOnClickListener(this);
		
		mEtRetrievePasswordIpone.requestFocus();
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) 
		{
			case R.id.tv_retrieve_password_next_step://确认
				String mobile = mEtRetrievePasswordIpone.getText().toString().trim();
				if(mMemberInfo.mobile.equals(mobile))
				{
					sendMessage(mMemberInfo.mobile);
					mHandler.sendEmptyMessage(Common.INPUT_VERIFICATION_CODE);
				}else{
					Toast.makeText(mContext, "手机号码不正确!", Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}
	}
	
	private void sendMessage(String phoneNum)
	{
		@SuppressWarnings("static-access")
		SharedPreferences sp = mContext.getSharedPreferences("code", mContext.MODE_PRIVATE);
		Random random = new Random();
		int x = random.nextInt(8999);
	    String number = "" + (x + 1000);
	    sp.edit().putString("code", number).commit();
	    Map<String ,String> rawParams = new HashMap<String, String>();
		rawParams.put("account", Utils.SEND_MSG_ACCOUNT);
		String pwd =  MD5Util.string2MD5(Utils.SEND_MSG_PASSWORD);
		rawParams.put("password",Utils.SEND_MSG_PASSWORD);
		rawParams.put("mobile", mMemberInfo.mobile);
		rawParams.put("content","您的验证码是："+ number +"。请不要把验证码泄露给其他人。");
		Log.e("SMSVerificationView", "sendMessage..account="+Utils.SEND_MSG_ACCOUNT
			+"; pwd="+Utils.SEND_MSG_PASSWORD+"; md5 pwd="+pwd
			+"; phoneNum="+phoneNum+"; msg=您的验证码是："+number+"。请不要把验证码泄露给其他人。");
		mWebServiceLoader.sendMsgResult(mSMSHandler,mContext, Utils.SEND_MSG_URL, rawParams);
	}
}