package com.beanpai.egr.shopping.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

/**
 * 选择支付方式
 * @author WGQ
 */
public class PaymentMethodView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private TextView mTvPayIntegral;
	private TextView mTvPayWinxin;
	private ImageView mTvPayImageIntegral;
	private ImageView mTvPayImageWeixin;
	public PaymentMethodView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
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
		View localView = LayoutInflater.from(mContext).inflate(R.layout.payment_method_view,null);
		addView(localView);
		
		mTvPayIntegral = (TextView) localView.findViewById(R.id.tv_payment_integral);
		mTvPayWinxin = (TextView) localView.findViewById(R.id.tv_payment_weixin);
		mTvPayImageIntegral = (ImageView) localView.findViewById(R.id.img_payment_integral);
		mTvPayImageWeixin = (ImageView) localView.findViewById(R.id.img_payment_weixin);
		
		initListener();
	}

	//绑定监听
	private void initListener()
	{
		mTvPayImageIntegral.requestFocus();
		mTvPayImageIntegral.setOnClickListener(this);
		mTvPayImageWeixin.setOnClickListener(this);
		mTvPayIntegral.setOnClickListener(this);
	}


	
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.img_payment_integral:
				mHandler.sendEmptyMessage(Common.SET_PAYMENT_PASSWORD);
				break;
			case R.id.img_payment_weixin:
				mHandler.sendEmptyMessage(Common.QRCODE_PAY);
				break;
			default:
				break;
		}
	}
}