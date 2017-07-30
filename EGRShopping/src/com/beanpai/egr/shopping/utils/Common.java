package com.beanpai.egr.shopping.utils;

import android.content.Context;
import android.widget.Toast;

public class Common {

	/**
	 * 用户信息
	 */
	public static final int EXCHANGE_INFORMATION = 0;
	
	/**
	 * 手机短信验证
	 */
	public static final int SMS_VERIFICATION = 1;
	
	/**
	 * 设置支付密码
	 */
	public static final int SET_PAYMENT_PASSWORD = 2;
	
	/**
	 * 信息收集完成
	 */
	public static final int INFORMATION_COLLECTION = 3;
	
	/**
	 * 兑换数量
	 */
	public static final int EXCHANGE_QUANTITY = 4;
	
	/**
	 * 修改收获地址
	 */
	public static final int CHANGE_RECEIVING_ADDRESS = 5;
	
	/**
	 * 支付确认
	 */
	public static final int PAYMENT_CONFIRM = 6;
	
	/**
	 * 找回密码
	 */
	public static final int RETRIEVE_PASSWORD = 7;
	
	/**
	 * 输入验证码
	 */
	public static final int INPUT_VERIFICATION_CODE = 8;
	
	/**
	 * 新支付密码
	 */
	public static final int NEW_PAYMENT_PASSWORD = 9;
	
	/**
	 * 购买完成
	 */
	public static final int PURCHASE_COMPLETED = 10;
	
	/**
	 * 关闭POPUPWINDOW
	 */
	public static final int CLOSE_POPUPWINDOW = 11;
	
	/**
	 * 选择支付方式
	 */
	public static final int PAYMENT_METHOD  = 12;
	
	/**
	 * 二维码 支付
	 */
	public static final int QRCODE_PAY = 13;
	
	/**
	 * 消息提示
	 */
	public static void showMessage(Context mContext,String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
}