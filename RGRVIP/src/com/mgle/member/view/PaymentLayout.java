package com.mgle.member.view;

import com.mgle.member.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentLayout extends FrameLayout implements View.OnClickListener,View.OnFocusChangeListener {

	private static final int REFRESH_TEXTVIEW_UI = 1001;
	
	private Activity mActivity;
	private Handler mMainHandler;
	
	private TextView mIponeTextView,mPayPwdTextView,mAccountTextView;

	
	public PaymentLayout(Context context,Handler mMainHandler) 
	{
		super(context);
		this.mActivity = (Activity) context;
		this.mMainHandler = mMainHandler;
		
		initView();
	}

	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mActivity).inflate(R.layout.payment_view,null);
		addView(localView);
		
		mIponeTextView = (TextView) localView.findViewById(R.id.tv_update_ipone);
		mPayPwdTextView = (TextView) localView.findViewById(R.id.tv_update_paypassword);
		mAccountTextView = (TextView) localView.findViewById(R.id.tv_uptate_account);
		
		initListener();
	}

	private void initListener() 
	{
		mIponeTextView.setOnClickListener(this);
		mPayPwdTextView.setOnClickListener(this);
		mAccountTextView.setOnClickListener(this);
		
		mIponeTextView.setOnFocusChangeListener(this);
		mPayPwdTextView.setOnFocusChangeListener(this);
		mAccountTextView.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_update_ipone:
				Toast.makeText(mActivity, "mobile��phone�����ֻ����벻֪���޸���һ��!", Toast.LENGTH_LONG).show();
				break;
			case R.id.tv_update_paypassword:
				Toast.makeText(mActivity, "��֪������mobile����phone�����޸�֧������!", Toast.LENGTH_LONG).show();	
				break;
			case R.id.tv_uptate_account:
				Toast.makeText(mActivity, "û�нӿ�!", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_update_ipone:
				Toast.makeText(mActivity, "�޸���֤�ֻ�", Toast.LENGTH_LONG).show();
				if(hasFocus)
				{
					Message message = new Message();
					message.obj = 2;
					message.what = REFRESH_TEXTVIEW_UI;
					mMainHandler.sendMessage(message);
				}
				break;
			case R.id.tv_update_paypassword:
				Toast.makeText(mActivity, "�޸�֧������", Toast.LENGTH_LONG).show();	
				break;
			case R.id.tv_uptate_account:
				Toast.makeText(mActivity, "���������ʺ�", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
	}
}