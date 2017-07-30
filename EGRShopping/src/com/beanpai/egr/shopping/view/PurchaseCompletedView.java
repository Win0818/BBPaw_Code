package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PurchaseCompletedView extends FrameLayout implements OnClickListener {

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	
	private TextView mTvPurchaseCompletedNextStep,mTvPurchaseCompletedMessage;
	
	public PurchaseCompletedView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
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
		View localView = LayoutInflater.from(mContext).inflate(R.layout.purchase_completed_view,null);
		addView(localView);
		
		mTvPurchaseCompletedNextStep = (TextView) localView.findViewById(R.id.tv_purchase_completed_next_step);
		mTvPurchaseCompletedMessage = (TextView) localView.findViewById(R.id.tv_purchase_completed_message);
		
		initListener();
	}
	
	//绑定监听
	private void initListener() 
	{
		mTvPurchaseCompletedNextStep.setOnClickListener(this);
		
		mTvPurchaseCompletedNextStep.requestFocus();
		
		mTvPurchaseCompletedMessage.setText("我们已向您的绑定手机号码"+ mMemberInfo.mobile.substring(0, 3) +"xxxx"+ mMemberInfo.mobile.substring(mMemberInfo.mobile.length()-2, mMemberInfo.mobile.length()) +"发送了提货验证码，您也可以用相机拍照或者用笔记录下来提货验证码");
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_purchase_completed_next_step: //关闭
				mHandler.sendEmptyMessage(Common.CLOSE_POPUPWINDOW);
				break;
			default:
				break;
		}
	}
}