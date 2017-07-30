package com.mgle.member.view;

import com.mgle.member.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;

public class WithdrawalsView extends FrameLayout implements OnFocusChangeListener, OnClickListener {

	private Context mContext;
	
	public WithdrawalsView(Context context)
	{
		super(context);
		this.mContext = context;
		
		initView();
	}

	@SuppressLint("InflateParams")
	private void initView() 
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.withdrawals_view,null);
		addView(localView);
	}

	@Override
	public void onClick(View view)
	{

	}

	@Override
	public void onFocusChange(View view, boolean hasFocus)
	{

	}
}