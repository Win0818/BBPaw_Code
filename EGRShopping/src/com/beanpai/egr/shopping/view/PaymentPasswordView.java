package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ֧������
 * @author WGQ
 */
public class PaymentPasswordView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	
	private EditText mEtPayPasswordEditText,mEtPayPasswordsEditText;
	private TextView mTvPayNextStepTextView;
	
	public PaymentPasswordView(Context context,Handler mHandler) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		
		initView();
	}

	//��ʼ������
	@SuppressLint("InflateParams")
	private void initView() 
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.paypwd_view,null);
		addView(localView);
		
		mEtPayPasswordEditText = (EditText) localView.findViewById(R.id.et_pay_password);
		mEtPayPasswordsEditText = (EditText) localView.findViewById(R.id.et_pay_passwords);
		
		mTvPayNextStepTextView = (TextView) localView.findViewById(R.id.tv_pay_next_step);
		
		initListener();
	}

	//���¼�
	private void initListener()
	{
		mEtPayPasswordEditText.requestFocus();
		
		mTvPayNextStepTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		String password = mEtPayPasswordEditText.getText().toString().trim();
		String passwords = mEtPayPasswordsEditText.getText().toString().trim();
		if(password.length() == 6)
		{
			if(password.equals(passwords))
			{
				mHandler.sendEmptyMessage(Common.SET_PAYMENT_PASSWORD);
			}else{
				Common.showMessage(mContext, "������������벻һ�£�");
			}
		}else{
			Common.showMessage(mContext, "������6Ϊ�����ֵ����룡");
		}
	}
}