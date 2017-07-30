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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 短信验证
 * @author WGQ
 */
public class SMSVerificationView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	
	private Map<String ,String> rawParams = null;
	private WebServiceLoader mWebServiceLoader = null;
	
	private EditText mEtVerifyCodeEditText;
	private TextView mTvReSendingTextView,mTvSmsNextStepTextView;
	
	@SuppressLint("HandlerLeak")
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
	
	public SMSVerificationView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
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
		View localView = LayoutInflater.from(mContext).inflate(R.layout.sms_view,null);
		addView(localView);
		
		mEtVerifyCodeEditText = (EditText) localView.findViewById(R.id.et_verify_code);
		mTvReSendingTextView = (TextView) localView.findViewById(R.id.tv_re_sending);
		mTvSmsNextStepTextView = (TextView) localView.findViewById(R.id.tv_sms_next_step);
		
		initData();
		initListener();
	}

	private void initData() 
	{
		mEtVerifyCodeEditText.setText(mMemberInfo.mobile);
		
		sendMessage(mMemberInfo.mobile);
		rawParams = new HashMap<String, String>();
	}

	//绑定事件
	private void initListener() 
	{
		mEtVerifyCodeEditText.requestFocus();
		
		mTvReSendingTextView.setOnClickListener(this);
		mTvSmsNextStepTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId())
		{
			case R.id.tv_re_sending://重新发送验证码
				sendMessage(mMemberInfo.mobile);
				break;
			case R.id.tv_sms_next_step://下一步
				mHandler.sendEmptyMessage(Common.SMS_VERIFICATION);
				break;
			default:
				break;
		}
	}
	
	private void sendMessage(String phoneNum)
	{
		Random random = new Random();
		int x = random.nextInt(8999);
	    String number = "" + (x + 1000);
		rawParams.clear();
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
