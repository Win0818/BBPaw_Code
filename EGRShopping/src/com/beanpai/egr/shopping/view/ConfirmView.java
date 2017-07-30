package com.beanpai.egr.shopping.view;

import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.AsynImageLoader;
import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 确认兑换
 * @author WGQ
 */
public class ConfirmView extends FrameLayout implements View.OnClickListener {

	private Context mContext;
	private Handler mHandler;
	
	private String mPosterUrl;
	private MemberInfo mMemberInfo;
	private CommodityDetailInfo mCommdityInfo;
	
	private AsynImageLoader asynImageLoader;  
	
	private TextView mTvRealNameTextView,mTvContactPhoneTextView,mTvCommonTelephoneTextView,
					 mTvStreetAddressTextView,mTvCellNameTextView,mTvCorridorUnitTextView,
					 mTvCommodityNameTextView,mTvCommodityCountTextView,mTvCommodityMoneyTextView,
					 mTvChangeReceivingAddressTextView,mTvConfirmationExchangeTextView;
	private ImageView mImgCommoditySrcImageView;
	
	public ConfirmView(Context context,Handler mHandler,MemberInfo mMemberInfo,String mPosterUrl,CommodityDetailInfo mCommdityInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		this.mPosterUrl = mPosterUrl;
		this.mCommdityInfo = mCommdityInfo;
		asynImageLoader = new AsynImageLoader();

		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.confirm_view,null);
		addView(localView);
		
		mTvRealNameTextView = (TextView) localView.findViewById(R.id.tv_real_name);
		mTvContactPhoneTextView = (TextView) localView.findViewById(R.id.tv_contact_phone);
		mTvCommonTelephoneTextView = (TextView) localView.findViewById(R.id.tv_common_telephone);
		mTvStreetAddressTextView = (TextView) localView.findViewById(R.id.tv_street_address);
		mTvCellNameTextView = (TextView) localView.findViewById(R.id.tv_cell_name);
		mTvCorridorUnitTextView = (TextView) localView.findViewById(R.id.tv_corridor_unit);
		
		mTvChangeReceivingAddressTextView = (TextView) localView.findViewById(R.id.tv_change_receiving_address);
		
		mImgCommoditySrcImageView = (ImageView) localView.findViewById(R.id.img_commodity_src);
		
		mTvCommodityNameTextView = (TextView) localView.findViewById(R.id.tv_commodity_name);
		mTvCommodityCountTextView = (TextView) localView.findViewById(R.id.tv_commodity_count);
		mTvCommodityMoneyTextView = (TextView) localView.findViewById(R.id.tv_commodity_money);
		
		mTvConfirmationExchangeTextView = (TextView) localView.findViewById(R.id.tv_confirmation_exchange);
		
		initData();
		initListener();
	}

	private void initData() 
	{
		changeCommodityDetailData(mCommdityInfo);
		mTvRealNameTextView.setText(mMemberInfo.customername);
		mTvContactPhoneTextView.setText(mMemberInfo.mobile);
		mTvCommonTelephoneTextView.setText(mMemberInfo.mobile);
		mTvStreetAddressTextView.setText(""+mMemberInfo.linkaddress);
		mTvCellNameTextView.setText(mMemberInfo.villagename);
		mTvCorridorUnitTextView.setText(mMemberInfo.corridorunit);
	}

	//绑定监听
	private void initListener()
	{
		mTvConfirmationExchangeTextView.requestFocus();
		
		mTvChangeReceivingAddressTextView.setOnClickListener(this);
		mTvConfirmationExchangeTextView.setOnClickListener(this);
	}
	
	private void changeCommodityDetailData(final CommodityDetailInfo commdityInfo) 
	{
		if (commdityInfo == null) 
		{
			Toast.makeText(mContext, "获取商品详细信息失败！", Toast.LENGTH_LONG).show();
			return;
		}

		Log.e("mPosterUrl", "mImgCommoditySrcImageView="+(mImgCommoditySrcImageView==null));
		asynImageLoader.showImageAsyn(mImgCommoditySrcImageView, mPosterUrl, R.drawable.loading);
		
		mTvCommodityNameTextView.setText(commdityInfo.name);
		mTvCommodityNameTextView.setSelected(true);
		mTvCommodityCountTextView.setText("兑换数量：" + commdityInfo.num);
		mTvCommodityMoneyTextView.setText("合计：" + Integer.parseInt(commdityInfo.nowSalePrice) * commdityInfo.num * 100);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_change_receiving_address: //修改收获地址
				mHandler.sendEmptyMessage(Common.CHANGE_RECEIVING_ADDRESS);
				break;
			case R.id.tv_confirmation_exchange: //无需修改地址，输入支付密码，确认兑换，
				mHandler.sendEmptyMessage(Common.PAYMENT_CONFIRM);
				break;
			default:
				break;
		}
	}
}