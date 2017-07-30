package com.beanpai.egr.shopping.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.beanpai.egr.shopping.utils.MsgWhat;
import com.beanpai.egr.shopping.utils.ScreenUtils;
import com.beanpai.egr.shopping.utils.WebServiceLoader;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.shopping.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

@SuppressLint("HandlerLeak")
public class ExchangePopupWindow extends PopupWindow {

	private static final String TAG = "BuyPopupWindow";
	
	private Context mContext;
	private View mParentView;
	private String mJsonUrl;
	private String mPosterUrl;
	private MemberInfo mMemberInfo;;
	private CommodityDetailInfo mCommdityInfo;
	
	private RelativeLayout mRlContentLayout;
	
	private WebServiceLoader mWebServiceLoader = null;
	
	@SuppressLint("HandlerLeak")
	private Handler mConHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
				case MsgWhat.GET_COMMODITY_CONTENT_RESULT:
					if (msg.obj == null) 
					{
						Common.showMessage(mContext,"no commodity details info!");
						return;
					}
					mCommdityInfo = (CommodityDetailInfo) msg.obj;
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			mRlContentLayout.removeAllViews();
			switch (msg.what)
			{
				case Common.EXCHANGE_INFORMATION://完善用户信息
					SMSVerificationView mSMSVerificationView = new SMSVerificationView(mContext, mHandler,mMemberInfo);
					mRlContentLayout.addView(mSMSVerificationView);
					break;
				case Common.SMS_VERIFICATION://短信验证
					PaymentPasswordView mPaymentPasswordView = new PaymentPasswordView(mContext, mHandler);
					mRlContentLayout.addView(mPaymentPasswordView);
					break;
				case Common.SET_PAYMENT_PASSWORD://输入支付密码完成支付
					PaymentConfirmView mPaymentConfirmView = new PaymentConfirmView(mContext, mHandler,mMemberInfo,mCommdityInfo);
					mRlContentLayout.addView(mPaymentConfirmView);
					break;
				case Common.INFORMATION_COLLECTION://信息收集完成
					ExchangeQuantityView mExchangeQuantityView = new ExchangeQuantityView(mContext, mHandler);
					mRlContentLayout.addView(mExchangeQuantityView);
					break;
				case Common.EXCHANGE_QUANTITY://确认兑换
					int mQuantity = Integer.parseInt(msg.obj.toString());
					mCommdityInfo.num = mQuantity;
					mCommdityInfo.total_fee = Integer.parseInt(mCommdityInfo.nowSalePrice) * mQuantity;
					ConfirmView mConfirmView = new ConfirmView(mContext, mHandler,mMemberInfo,mPosterUrl,mCommdityInfo);
					mRlContentLayout.addView(mConfirmView);
					break;
				case Common.CHANGE_RECEIVING_ADDRESS://修改收货地址
					ChangeReceivingAddressView mChangeReceivingAddressView = new ChangeReceivingAddressView(mContext, mHandler,mMemberInfo);
					mRlContentLayout.addView(mChangeReceivingAddressView);
					break;
				case Common.PAYMENT_CONFIRM://确认购买
					if(mMemberInfo !=  null)
					{
						if(mMemberInfo.customername != null && !mMemberInfo.customername.equals("") && mMemberInfo.mobile != null && !mMemberInfo.mobile.equals("") &&  mMemberInfo.villagename != null && !mMemberInfo.villagename.equals("") && mMemberInfo.corridorunit != null && !mMemberInfo.corridorunit.equals(""))
						{
							//SuccessView mSuccessView = new SuccessView(mContext, mHandler);
							//mRlContentLayout.addView(mSuccessView);
							mHandler.sendEmptyMessage(Common.PAYMENT_METHOD);
						}else{
							InformationView mInformationView = new InformationView(mContext,mHandler,mMemberInfo);
							mRlContentLayout.addView(mInformationView);
						}
					}else{
						InformationView mInformationView = new InformationView(mContext,mHandler,mMemberInfo);
						mRlContentLayout.addView(mInformationView);
					}
					break;
				case Common.RETRIEVE_PASSWORD: //找回密码
					RetrievePasswordView mRetrievePasswordView = new RetrievePasswordView(mContext, mHandler, mMemberInfo);
					mRlContentLayout.addView(mRetrievePasswordView);
					break;
				case Common.INPUT_VERIFICATION_CODE: //输入手机验证码
					VerificationCodeView mVerificationCodeView = new VerificationCodeView(mContext, mHandler,mMemberInfo);
					mRlContentLayout.addView(mVerificationCodeView);
					break;
				case Common.NEW_PAYMENT_PASSWORD: //输入新的支付密码
					NewPaymentPasswordView mNewPaymentPasswordView = new NewPaymentPasswordView(mContext, mHandler,mMemberInfo);
					mRlContentLayout.addView(mNewPaymentPasswordView);
					break;
				case Common.PURCHASE_COMPLETED: //购物完成
					PurchaseCompletedView mPurchaseCompletedView = new PurchaseCompletedView(mContext, mHandler,mMemberInfo);
					mRlContentLayout.addView(mPurchaseCompletedView);
					break;
				case Common.CLOSE_POPUPWINDOW: //购关闭自己
					dismiss();
					break;
				case Common.PAYMENT_METHOD://选择支付方式
					PaymentMethodView mPaymentMethodView = new PaymentMethodView(mContext, mHandler, mMemberInfo);
					mRlContentLayout.addView(mPaymentMethodView);
					break;
				case Common.QRCODE_PAY://二维码支付
					QRCodeView mQRCodeView = new QRCodeView(mContext, mHandler,mMemberInfo,mCommdityInfo);
					mRlContentLayout.addView(mQRCodeView);
					break;
				default:
					break;
			}
		}
	};
	
	public ExchangePopupWindow(Context mContext,View parent,String jsonUrl,String mPosterUrl) 
	{
		this.mContext = mContext;
		this.mJsonUrl = jsonUrl;
		this.mPosterUrl = mPosterUrl;
		this.mParentView = parent;
		mMemberInfo = new MemberInfo();
		mWebServiceLoader = new WebServiceLoader();
		init();
	}
	
	@SuppressLint("InflateParams")
	private void init()
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_main, null);
		setWidth(ScreenUtils.getScreenWidth(mContext));
		setHeight(ScreenUtils.getScreenHeight(mContext));
		setAnimationStyle(R.style.MenuPopupAnimation);
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		
		initView(view);
	}

	//初始化控件
	private void initView(View view) 
	{
		mRlContentLayout = (RelativeLayout) view.findViewById(R.id.rl_content_layout);
		
		initData();
	}
	
	//初始化数据
	private void initData() 
	{
		paserMemberInfo(DeviceManager.getMemoryJsonData());
		Log.e("initData", "mWebServiceLoader="+(mWebServiceLoader==null));
		mWebServiceLoader.getDetailCommondityContentLoader(mConHandler, mContext, mJsonUrl);
	}
	
	public void paserMemberInfo(String jsonData)
	{
		Log.d(TAG, "paserMemberInfo...jsonData=" + jsonData);
		try {
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			
			mMemberInfo.customername = epgObj.getString("customername");
			mMemberInfo.level = epgObj.getInt("level");
			mMemberInfo.mobile = epgObj.getString("mobile");
			mMemberInfo.onlineDuration = epgObj.getLong("onlineDuration");
			mMemberInfo.integral = epgObj.getInt("integral");
			mMemberInfo.paypassword = epgObj.getString("paypassword");
			mMemberInfo.villagename = epgObj.getString("villagename");
			mMemberInfo.corridorunit = epgObj.getString("corridorunit");
			
			mMemberInfo.addExchangeRecordUrl = epgObj.getString("addExchangeRecordUrl");
			mMemberInfo.seleteExchangeRecordUrl = epgObj.getString("seleteExchangeRecordUrl");
			mMemberInfo.delExchangeRecordUrl = epgObj.getString("delExchangeRecordUrl");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		initMemberInfo();
	}
	
	public void initMemberInfo()
	{
		if (mMemberInfo == null) 
		{
			return;
		}
		
		Log.e(TAG, "initMemberInfo...memberInfo.paypassword="+mMemberInfo.paypassword);
		
		mRlContentLayout.removeAllViews();
		ExchangeQuantityView mExchangeQuantityView = new ExchangeQuantityView(mContext, mHandler);
		mRlContentLayout.addView(mExchangeQuantityView);
	}
	
	/**
	 * 显示窗口
	 */
	public void show()
	{
		showAtLocation(mParentView, Gravity.CENTER, 0, 0);
	}
}