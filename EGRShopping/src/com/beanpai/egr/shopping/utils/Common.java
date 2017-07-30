package com.beanpai.egr.shopping.utils;

import android.content.Context;
import android.widget.Toast;

public class Common {

	/**
	 * �û���Ϣ
	 */
	public static final int EXCHANGE_INFORMATION = 0;
	
	/**
	 * �ֻ�������֤
	 */
	public static final int SMS_VERIFICATION = 1;
	
	/**
	 * ����֧������
	 */
	public static final int SET_PAYMENT_PASSWORD = 2;
	
	/**
	 * ��Ϣ�ռ����
	 */
	public static final int INFORMATION_COLLECTION = 3;
	
	/**
	 * �һ�����
	 */
	public static final int EXCHANGE_QUANTITY = 4;
	
	/**
	 * �޸��ջ��ַ
	 */
	public static final int CHANGE_RECEIVING_ADDRESS = 5;
	
	/**
	 * ֧��ȷ��
	 */
	public static final int PAYMENT_CONFIRM = 6;
	
	/**
	 * �һ�����
	 */
	public static final int RETRIEVE_PASSWORD = 7;
	
	/**
	 * ������֤��
	 */
	public static final int INPUT_VERIFICATION_CODE = 8;
	
	/**
	 * ��֧������
	 */
	public static final int NEW_PAYMENT_PASSWORD = 9;
	
	/**
	 * �������
	 */
	public static final int PURCHASE_COMPLETED = 10;
	
	/**
	 * �ر�POPUPWINDOW
	 */
	public static final int CLOSE_POPUPWINDOW = 11;
	
	/**
	 * ѡ��֧����ʽ
	 */
	public static final int PAYMENT_METHOD  = 12;
	
	/**
	 * ��ά�� ֧��
	 */
	public static final int QRCODE_PAY = 13;
	
	/**
	 * ��Ϣ��ʾ
	 */
	public static void showMessage(Context mContext,String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
}