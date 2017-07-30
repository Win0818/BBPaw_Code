package com.beanpai.egr.shopping.utils;

import java.util.Map;

import com.beanpai.egr.shopping.entity.CategoryInfo;
import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.CommodityInfo;
import com.beanpai.egr.shopping.entity.MsgResult;
import com.beanpai.egr.shopping.webservice.HttpUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class WebServiceLoader {

	protected static final String TAG = "--WebServiceLoader--";
	protected static final boolean DEBUG = true;

	public void getCategoryContentLoader(final Handler mHandler, final Context context, final String url) {
		if (!HttpUtils.isNetworkConnected(context)) {
			mHandler.sendEmptyMessage(MsgWhat.NO_NET);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String categoryInfoJson = HttpUtils.getRequest(url, context);
						if (DEBUG)
							Log.e(TAG, "getCategoryContentLoader...url="+url+"; categoryInfoJson = " + categoryInfoJson);
						CategoryInfo categroyInfo = JsonParserUtil.parseCategoryInfo(categoryInfoJson);
						if (DEBUG)
							Log.e(TAG, "getCategoryContentLoader...categroyInfo = " + categroyInfo);
						mHandler.sendMessage(
								mHandler.obtainMessage(MsgWhat.GET_NAV_CATEGORY_RESULT, 0, 0, categroyInfo));
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(MsgWhat.GET_NAV_CATEGORY_RESULT);
					}
				}
			}).start();
		}
	}

	public void getCommondityContentLoader(final Handler mHandler, final Context context, final String url) {
		if (!HttpUtils.isNetworkConnected(context)) {
			mHandler.sendEmptyMessage(MsgWhat.NO_NET);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String commodityInfoJson = HttpUtils.getRequest(url, context);
						if (DEBUG)
							Log.e(TAG, "getCommondityContentLoader...url="+url+"; commodityInfoJson = " + commodityInfoJson);
						CommodityInfo commodityInfo = JsonParserUtil.parseCommodityInfo(commodityInfoJson);
						if (DEBUG)
							Log.e(TAG, "getCommondityContentLoader...commodityInfo = " + commodityInfo);
						mHandler.sendMessage(
								mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT_RESULT, 0, 0, commodityInfo));
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(MsgWhat.GET_COMMODITY_CONTENT_RESULT);
					}
				}
			}).start();
		}
	}

	public void getDetailCommondityContentLoader(final Handler mHandler, final Context context, final String url) {
		if (!HttpUtils.isNetworkConnected(context)) {
			mHandler.sendEmptyMessage(MsgWhat.NO_NET);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String commodityInfoJson = HttpUtils.getRequest(url, context);
						if (DEBUG)
							Log.e(TAG, "getDetailCommondityContentLoader...commodityInfoJson = " + commodityInfoJson);
						CommodityDetailInfo commodityInfo = JsonParserUtil.parseDetailCommodityInfo(commodityInfoJson);
						if (DEBUG)
							Log.e(TAG, "getDetailCommondityContentLoader...commodityInfo = " + commodityInfo);
						mHandler.sendMessage(
								mHandler.obtainMessage(MsgWhat.GET_COMMODITY_CONTENT_RESULT, 0, 0, commodityInfo));
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(MsgWhat.GET_COMMODITY_CONTENT_RESULT);
					}
				}
			}).start();
		}
	}

	public void sendMsgResult(final Handler mHandler, final Context context, final String url,
			final Map<String, String> rawParams) {
		if (!HttpUtils.isNetworkConnected(context)) {
			mHandler.sendEmptyMessage(MsgWhat.NO_NET);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String msgResult = HttpUtils.postRequest(url, rawParams, context);
						if (DEBUG)
							Log.e(TAG, "msgResult...msgResult = " + msgResult);
						MsgResult msg = JsonParserUtil.parseMsgResultInfo(msgResult);
						if (DEBUG)
							Log.e(TAG, "sendMsgResult...msg = " + msg);
						mHandler.sendMessage(mHandler.obtainMessage(MsgWhat.SEND_MSG_INFO_RESULT, 0, 0, msg));
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(MsgWhat.SEND_MSG_INFO_RESULT);
					}
				}
			}).start();
		}
	}
}
