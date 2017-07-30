package com.mgle.member.view;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egreat.devicemanger.DeviceManager;
import com.mgle.member.R;
import com.mgle.member.entity.Commodity;
import com.mgle.member.entity.MemberInfo;
import com.mgle.member.util.Common;
import com.mgle.member.util.ImageLoader;
import com.mgle.member.util.ScaleAnimEffect;

public class BalanceView extends FrameLayout implements View.OnFocusChangeListener, View.OnClickListener {

	private static final int REFRESH_TEXTVIEW_UI = 1001;

	private static final String TAG = "--BalanceView--";
	
	private static float big_x = 1.05F;
	private static float big_y = 1.05F;
	
	private ScaleAnimEffect animEffect = new ScaleAnimEffect();
	
	private Activity mActivity;
	private Handler mMainHandler;
	private MemberInfo mMemberInfo;
	
	private TextView mTvLbTotalTextView,mTvLbMoreTextView,mTvMoreExchangeTextView;
	private FrameLayout[] mFrameLayout;
	private ImageView[] mImageView;
	private TextView[] mTvRmbNowPriceTextView;
	private TextView[] mTvLbPriceTextView;
	private TextView[] mTvLbOriginalPriceTextView;
	private TextView[] mTvDiscountTextViews;
	private LinearLayout[] mLabelLayout;
	private ImageLoader mLoader = null;
	private DecimalFormat mFormat;
	private DecimalFormat mFormat2;
	private DecimalFormat mFormat3;
	
	//private Context mContext;
	
	@SuppressLint("HandlerLeak")
	private  Handler mHandler = new  Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					mHandler.postDelayed(runnable, 1000);
					break;
				case 1:
					mHandler.removeCallbacks(runnable);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public BalanceView(Context context,Handler mMainHandler) 
	{
		super(context);
		this.mActivity = (Activity) context;
		this.mMainHandler = mMainHandler;
		this.mFrameLayout = new FrameLayout[4];
		this.mImageView = new ImageView[4];
		this.mTvRmbNowPriceTextView = new TextView[4];
		this.mTvLbPriceTextView = new TextView[4];
		this.mTvLbOriginalPriceTextView = new TextView[4];
		this.mTvDiscountTextViews = new TextView[4];
		this.mLabelLayout = new LinearLayout[4];
		mMemberInfo = new MemberInfo();
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		
		mFormat = new DecimalFormat("###,###.00");
		mFormat2 = new DecimalFormat("###,###");
		mFormat3 = new DecimalFormat("0.0");
		initView();
		mHandler.sendEmptyMessage(0);
	}

	@SuppressLint("InflateParams")
	private void initView() 
	{
		View localView = LayoutInflater.from(mActivity).inflate(R.layout.balance_view,null);
		addView(localView);
		
		mTvLbTotalTextView  = (TextView) localView.findViewById(R.id.tv_lb_total);
		mTvLbMoreTextView = (TextView) localView.findViewById(R.id.tv_lb_more);
		
		mFrameLayout[0] = (FrameLayout) localView.findViewById(R.id.fl_balance_00);
		mFrameLayout[1] = (FrameLayout) localView.findViewById(R.id.fl_balance_01);
		mFrameLayout[2] = (FrameLayout) localView.findViewById(R.id.fl_balance_02);
		mFrameLayout[3] = (FrameLayout) localView.findViewById(R.id.fl_balance_03);
		
		mImageView[0] = (ImageView) localView.findViewById(R.id.img_src_00);
		mImageView[1] = (ImageView) localView.findViewById(R.id.img_src_01);
		mImageView[2] = (ImageView) localView.findViewById(R.id.img_src_02);
		mImageView[3] = (ImageView) localView.findViewById(R.id.img_src_03);
		
		mTvRmbNowPriceTextView[0] = (TextView) localView.findViewById(R.id.tv_rmb_now_price_00);
		mTvRmbNowPriceTextView[1] = (TextView) localView.findViewById(R.id.tv_rmb_now_price_01);
		mTvRmbNowPriceTextView[2] = (TextView) localView.findViewById(R.id.tv_rmb_now_price_02);
		mTvRmbNowPriceTextView[3] = (TextView) localView.findViewById(R.id.tv_rmb_now_price_03);
		
		mTvLbPriceTextView[0] = (TextView) localView.findViewById(R.id.tv_lb_price_00);
		mTvLbPriceTextView[1] = (TextView) localView.findViewById(R.id.tv_lb_price_01);
		mTvLbPriceTextView[2] = (TextView) localView.findViewById(R.id.tv_lb_price_02);
		mTvLbPriceTextView[3] = (TextView) localView.findViewById(R.id.tv_lb_price_03);
		
		mTvLbOriginalPriceTextView[0] = (TextView) localView.findViewById(R.id.tv_lb_original_price_00);
		mTvLbOriginalPriceTextView[1] = (TextView) localView.findViewById(R.id.tv_lb_original_price_01);
		mTvLbOriginalPriceTextView[2] = (TextView) localView.findViewById(R.id.tv_lb_original_price_02);
		mTvLbOriginalPriceTextView[3] = (TextView) localView.findViewById(R.id.tv_lb_original_price_03);
		
		mTvDiscountTextViews[0] = (TextView) localView.findViewById(R.id.tv_discount_00);
		mTvDiscountTextViews[1] = (TextView) localView.findViewById(R.id.tv_discount_01);
		mTvDiscountTextViews[2] = (TextView) localView.findViewById(R.id.tv_discount_02);
		mTvDiscountTextViews[3] = (TextView) localView.findViewById(R.id.tv_discount_03);
		
		mLabelLayout[0] = (LinearLayout) localView.findViewById(R.id.label_layout_00);
		mLabelLayout[1] = (LinearLayout) localView.findViewById(R.id.label_layout_01);
		mLabelLayout[2] = (LinearLayout) localView.findViewById(R.id.label_layout_02);
		mLabelLayout[3] = (LinearLayout) localView.findViewById(R.id.label_layout_03);
		
		mTvMoreExchangeTextView  = (TextView) localView.findViewById(R.id.tv_more_exchange);
		
		initListener();
		initData();
	}

	//绑定事件
	private void initListener() 
	{
		mTvLbMoreTextView.setOnClickListener(this);
		mTvMoreExchangeTextView.setOnClickListener(this);
		int i = 0;
		while (true) 
		{
			int j = this.mFrameLayout.length;
			if (i >= j) 
			{
				return;
			}
			this.mFrameLayout[i].setOnFocusChangeListener(this);
			this.mFrameLayout[i].setOnClickListener(this);
			i += 1;
		}
	}

	//初始化数据
	private void initData() 
	{
		paserMemberInfo(DeviceManager.getMemoryJsonData());
		if(mMemberInfo != null)
		{
			mTvLbTotalTextView.setText(Common.getDecimalFormat(mMemberInfo.integral));
		}
	}
	
	public void changeCommodityData(List<Commodity> commoditys) 
	{
		float nowIntegral = 0.0f;
		float integral = 0.0f;
		float nowSalePrice = 0.0f;
		
		Log.e(TAG, "changeCommodityData..commoditys=" + commoditys);
		try {
			if (commoditys == null || commoditys.size() <= 0) 
			{
				return;
			}
			for(int i = 0; i < mImageView.length; i++){
				if(commoditys.size() > i){
					//Log.d("WUWUWU", "-------++++++++----->>>>");
					nowSalePrice = Float.parseFloat(commoditys.get(i).nowSalePrice);
					integral = Float.parseFloat(commoditys.get(i).integral);
					nowIntegral = Float.parseFloat(commoditys.get(i).nowIntegral);
			       mLoader.loadImage(commoditys.get(i).fileurl, mImageView[i], true, false);
			       
			       mTvRmbNowPriceTextView[i].setText("￥" + mFormat.format(nowSalePrice));
			       mTvLbPriceTextView[i].setText(mFormat2.format(nowIntegral) + " /");
			       mTvLbOriginalPriceTextView[i].setText(mFormat2.format(integral) + "");
			       mTvLbOriginalPriceTextView[i].getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			      
			       if (integral >= 0 && nowIntegral >= 0 && integral > nowIntegral)
					{
						float lableValue = (nowIntegral / integral) * 10;
						mLabelLayout[i].setVisibility(View.VISIBLE);
						mTvDiscountTextViews[i].setText(mFormat3.format(lableValue));
					} else {
						mLabelLayout[i].setVisibility(View.INVISIBLE);
					}
			       
				}
			}
		
		
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public void paserMemberInfo(String jsonData)
	{
		Log.d("paserMemberInfo", "static_MemoryJson=" + jsonData);
		try {
			if(jsonData == null) 
			{
				return;
			}
			
			JSONObject dataJson = new JSONObject(jsonData);
			
			if(dataJson != null)
			{
				mMemberInfo.customerId = dataJson.getInt("customerId");
				mMemberInfo.customerno = dataJson.getString("customerno");
				mMemberInfo.customername = dataJson.getString("customername");
				mMemberInfo.level = dataJson.getInt("level");
				mMemberInfo.mobile = dataJson.getString("mobile");
				mMemberInfo.onlineDuration = dataJson.getLong("onlineDuration");
				mMemberInfo.integral = dataJson.getInt("integral");
				mMemberInfo.paypassword = dataJson.getString("paypassword");
				mMemberInfo.villagename = dataJson.getString("villagename");
				mMemberInfo.corridorunit = dataJson.getString("corridorunit");
				
				mMemberInfo.addExchangeRecordUrl = dataJson.getString("addExchangeRecordUrl");
				mMemberInfo.seleteExchangeRecordUrl = dataJson.getString("seleteExchangeRecordUrl");
				mMemberInfo.delExchangeRecordUrl = dataJson.getString("delExchangeRecordUrl");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
			case R.id.tv_lb_more:
				startActivityByPackageName("com.mgle.watchads");
				break;
			case R.id.tv_more_exchange:
				startActivityByPackageName("com.mgle.shopping");
				//Toast.makeText(mActivity, "兑换更多的商品", Toast.LENGTH_LONG).show();
				break;
			case R.id.fl_balance_00:
				break;
			case R.id.fl_balance_01:
				break;
			case R.id.fl_balance_02:
				break;
			case R.id.fl_balance_03:
			default:
				break;
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId()) 
		{
			case R.id.fl_balance_00:
			case R.id.fl_balance_01:
			case R.id.fl_balance_02:
			case R.id.fl_balance_03:
				if(hasFocus)
				{
					Message message = new Message();
					message.obj = 0;
					message.what = REFRESH_TEXTVIEW_UI;
					mMainHandler.sendMessage(message);
					startBigAnimation(view);
				}else {
					startSmallAnimation(view);
				}
				break;
			default:
				break;
		}
		
	}
	
	/**
	 * 放大
	 * @param index
	 */
	private void startBigAnimation(View view)
	{
		view.bringToFront();
		animEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 100);
		Animation anim = animEffect.createAnimation();
		view.startAnimation(anim);
	}
	
	/**
	 * 缩小
	 * @param index
	 */
	private void startSmallAnimation(View view)
	{
		animEffect.setAttributs(big_x, 1.0F, big_y, 1.0F, 100);
		view.startAnimation(animEffect.createAnimation());
	}
	
	private boolean startActivityByPackageName(String packageName) 
	{
		try {
			PackageManager pm = mActivity.getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(packageName);
			mActivity.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mActivity, "未安装此应用", Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	private Runnable runnable = new Runnable()
	{
		@Override
		public void run() 
		{
			Log.e("runnable", "runnable="+DeviceManager.getMemoryJsonData());
			paserMemberInfo(DeviceManager.getMemoryJsonData());
			if(mMemberInfo != null && mMemberInfo.customerId > 0)
			{
				mHandler.sendEmptyMessage(1);
				initData(); 
			}else{
				mHandler.sendEmptyMessage(0);
			}
		}
	};
}