package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 信息收集完成
 * @author WGQ
 */
public class SuccessView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	
	private TextView mTvSuccessNextStep;
	
	public SuccessView(Context context,Handler mHandler) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		
		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.success_view,null);
		addView(localView);
		
		mTvSuccessNextStep = (TextView) localView.findViewById(R.id.tv_success_next_step);
		
		initListener();
	}

	//绑定监听
	private void initListener() 
	{
		mTvSuccessNextStep.requestFocus();
		
		mTvSuccessNextStep.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_success_next_step:
				mHandler.sendEmptyMessage(Common.PAYMENT_METHOD);
				break;
			default:
				break;
		}
	}
}