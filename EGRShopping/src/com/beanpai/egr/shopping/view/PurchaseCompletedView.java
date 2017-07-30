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

	//��ʼ������
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.purchase_completed_view,null);
		addView(localView);
		
		mTvPurchaseCompletedNextStep = (TextView) localView.findViewById(R.id.tv_purchase_completed_next_step);
		mTvPurchaseCompletedMessage = (TextView) localView.findViewById(R.id.tv_purchase_completed_message);
		
		initListener();
	}
	
	//�󶨼���
	private void initListener() 
	{
		mTvPurchaseCompletedNextStep.setOnClickListener(this);
		
		mTvPurchaseCompletedNextStep.requestFocus();
		
		mTvPurchaseCompletedMessage.setText("�����������İ��ֻ�����"+ mMemberInfo.mobile.substring(0, 3) +"xxxx"+ mMemberInfo.mobile.substring(mMemberInfo.mobile.length()-2, mMemberInfo.mobile.length()) +"�����������֤�룬��Ҳ������������ջ����ñʼ�¼���������֤��");
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_purchase_completed_next_step: //�ر�
				mHandler.sendEmptyMessage(Common.CLOSE_POPUPWINDOW);
				break;
			default:
				break;
		}
	}
}