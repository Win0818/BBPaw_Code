package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 完善用户信息
 * @author WGQ
 */
public class InformationView extends FrameLayout implements View.OnClickListener,View.OnFocusChangeListener{

	private Context mContext;
	private Handler mHandler;
	private MemberInfo mMemberInfo;
	
	private RelativeLayout mRlEdRealNameLayout,mRlEdContactPhoneLayout,mRlEdCommonTelephoneLayout,
	 					   mRlEdStreetAddressLayout,mRlEdCellNameLayout,mRlEdCorridorUnitLayout;
	private EditText mEtRealNameEditText,mEtContactPhoneEditText,mEtCommonTelephoneEditText,
					 mEtStreetAddressEditText,mEtCellNameEditText,mEtCorridorUnitEditText;
	private TextView mTvNextStepTextView;
	
	public InformationView(Context context,Handler mHandler,MemberInfo mMemberInfo) 
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
		View localView = LayoutInflater.from(mContext).inflate(R.layout.information_view,null);
		addView(localView);
		
		mRlEdRealNameLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_real_name);
		mRlEdContactPhoneLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_contact_phone);
		mRlEdCommonTelephoneLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_common_telephone);
		mRlEdStreetAddressLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_street_address);
		mRlEdCellNameLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_cell_name);
		mRlEdCorridorUnitLayout = (RelativeLayout) localView.findViewById(R.id.rl_ed_corridor_unit);
		
		mEtRealNameEditText = (EditText) localView.findViewById(R.id.et_real_name);
		mEtContactPhoneEditText = (EditText) localView.findViewById(R.id.et_contact_phone);
		mEtCommonTelephoneEditText = (EditText) localView.findViewById(R.id.et_common_telephone);
		mEtStreetAddressEditText = (EditText) localView.findViewById(R.id.et_street_address);
		mEtCellNameEditText = (EditText) localView.findViewById(R.id.et_cell_name);
		mEtCorridorUnitEditText = (EditText) localView.findViewById(R.id.et_corridor_unit);
		
		mTvNextStepTextView = (TextView) localView.findViewById(R.id.tv_next_step);
		
		setTextData();
		initListener();
	}

	//初始化数据
	private void setTextData() 
	{
		mEtRealNameEditText.setText(mMemberInfo.customername);
		mEtContactPhoneEditText.setText(mMemberInfo.mobile);
		mEtCommonTelephoneEditText.setText(mMemberInfo.mobile);
		mEtStreetAddressEditText.setText("" + mMemberInfo.linkaddress);
		mEtCellNameEditText.setText(mMemberInfo.villagename);
		mEtCorridorUnitEditText.setText(mMemberInfo.corridorunit);
	}

	//绑定监听事件
	private void initListener()
	{
		mEtRealNameEditText.setOnFocusChangeListener(this);
		mEtContactPhoneEditText.setOnFocusChangeListener(this);
		mEtCommonTelephoneEditText.setOnFocusChangeListener(this);
		mEtStreetAddressEditText.setOnFocusChangeListener(this);
		mEtCellNameEditText.setOnFocusChangeListener(this);
		mEtCorridorUnitEditText.setOnFocusChangeListener(this);
		
		mTvNextStepTextView.setOnClickListener(this);
		
		mEtRealNameEditText.requestFocus();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId())
		{
			case R.id.et_real_name:
				if(hasFocus)
				{
					mRlEdRealNameLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdRealNameLayout.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_contact_phone:
				if(hasFocus)
				{
					mRlEdContactPhoneLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdContactPhoneLayout.setBackgroundColor(color.transparent);
				}		
				break;
			case R.id.et_common_telephone:
				if(hasFocus)
				{
					mRlEdCommonTelephoneLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdCommonTelephoneLayout.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_street_address:
				if(hasFocus)
				{
					mRlEdStreetAddressLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdStreetAddressLayout.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_cell_name:
				if(hasFocus)
				{
					mRlEdCellNameLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdCellNameLayout.setBackgroundColor(color.transparent);
				}
				break;
			case R.id.et_corridor_unit:
				if(hasFocus)
				{
					mRlEdCorridorUnitLayout.setBackgroundResource(R.drawable.edit_focus_bg);
				}else{
					mRlEdCorridorUnitLayout.setBackgroundColor(color.transparent);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.tv_next_step:
				mHandler.sendEmptyMessage(Common.EXCHANGE_INFORMATION);
				break;
			default:
				break;
		}
	}
}