package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 输入手机验证码
 * @author WGQ
 */
public class VerificationCodeView extends FrameLayout implements OnClickListener {

	private Context mContext;
	private Handler mHandler;
	@SuppressWarnings("unused")
	private MemberInfo mMemberInfo;
	
	private EditText mEtIponeCodeEditText;
	private TextView mTvCodeNextStepTextView;
	
	public VerificationCodeView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
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
		View localView = LayoutInflater.from(mContext).inflate(R.layout.input_verification_code_view,null);
		addView(localView);
		
		mEtIponeCodeEditText = (EditText) localView.findViewById(R.id.et_ipone_code);
				
		mTvCodeNextStepTextView = (TextView) localView.findViewById(R.id.tv_code_next_step);
		
		initListener();
	}

	//绑定监听
	private void initListener() 
	{
		mTvCodeNextStepTextView.setOnClickListener(this);
		
		mEtIponeCodeEditText.requestFocus();
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) 
		{
			case R.id.tv_code_next_step://确认
				String code = mEtIponeCodeEditText.getText().toString().trim();
				@SuppressWarnings("static-access")
				SharedPreferences sp = mContext.getSharedPreferences("code", mContext.MODE_PRIVATE);  
				if(sp.getString("code", "").equals(code))
				{
					mHandler.sendEmptyMessage(Common.NEW_PAYMENT_PASSWORD);
				}else{
					Toast.makeText(mContext, "验证码不正确!", Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
		}
	}
}