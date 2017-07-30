package com.beanpai.egr.shopping.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.image.utils.DownloadImgUtils;
import com.beanpai.egr.shopping.image.utils.ImageLoader;
import com.beanpai.egr.shopping.utils.MsgWhat;
import com.beanpai.egr.shopping.utils.Utils;
import com.beanpai.egr.shopping.utils.WebServiceLoader;
import com.beanpai.egr.shopping.view.ExchangePopupWindow;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class DeltailsActivity extends Activity {

	private static final String TAG = "--DeltailsActivity--";

	private static final boolean DEBUG = false;

	private WebServiceLoader mWebServiceLoader = null;

	private ImageView mImgDetail;
	private ImageView mImgPoster;
	private Button mBtnExchange;

	private TextView mNowSalePrice;
	private TextView mNowIntegral;
	private TextView mIntegral;
	private TextView mCommodityName;
	private TextView mLabel;
	private LinearLayout mLabelLayout;
	private RelativeLayout mUpDownTip;
	private LinearLayout mMainLayout;

	private ImageLoader mLoader = null;
	private String mJsonUrl;
	private String mPosterUrl;

	private DisplayMetrics mDm;
	private ImageView mBar;
	private ProgressBar mLoading;
	private Context mCtx;
	private WindowManager mWm;
	private LayoutParams mBarLp;
	private DisplayMetrics mDisplayMetrics;
	private RelativeLayout mClayout;

	protected int mScreenHeight;
	protected int mScreenWidth;
	private BitmapRegionDecoder mDecoder;
	private final Rect mRect = new Rect();
	protected int mShowHeight;
	protected int mStartY;
	protected int mImgHeight;

	private DecimalFormat mFormat;
	private DecimalFormat mFormat2;
	private DecimalFormat mFormat3;
	private Bitmap mCurrentBitmap;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setContentView(R.layout.commdity_detail_layout);

		mCtx = DeltailsActivity.this;
		mDm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDm);

		mUpDownTip = (RelativeLayout) LayoutInflater.from(mCtx).inflate(R.layout.up_down_tip_layout, null);

		mLoading = (ProgressBar) LayoutInflater.from(mCtx).inflate(R.layout.loading_progress_layout, null);
		mWm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
		mWebServiceLoader = new WebServiceLoader();
		mDisplayMetrics = new DisplayMetrics();

		// test:
		// http://101.200.173.180:8180/cms/minzhengapi/getCommodityDetail.action?id=582
		mJsonUrl = getIntent().getStringExtra("json_url");
		mPosterUrl = getIntent().getStringExtra("poster_url");
		Log.e(TAG, "onCreate..mJsonUrl=" + mJsonUrl + "; mPosterUrl=" + mPosterUrl);
		// mJsonUrl =
		// "http://101.200.173.180:8180/cms/minzhengapi/getCommodityDetail.action?id=582";
		// mPosterUrl
		// ="http://101.200.173.180:8081/uploadImage/img_1441187585312.jpg";
		initView();
		initData();
	}

	private void initView() {
		mImgDetail = (ImageView) findViewById(R.id.img_detail);
		mImgPoster = (ImageView) findViewById(R.id.img_poster);
		mMainLayout = (LinearLayout) findViewById(R.id.main);

		mCommodityName = (TextView) findViewById(R.id.txt_name);
		mLabel = (TextView) findViewById(R.id.txt_label);
		mNowSalePrice = (TextView) findViewById(R.id.txt_now_sale_price);
		mNowIntegral = (TextView) findViewById(R.id.txt_now_integral);
		mIntegral = (TextView) findViewById(R.id.txt_integral);
		mLabelLayout = (LinearLayout) findViewById(R.id.layout_label);

		mBtnExchange = (Button) findViewById(R.id.btn_exchange);
		mBtnExchange.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Log.e("onClick", "onClick="+mMainLayout+";   mJsonUrl="+mJsonUrl+";  mPosterUrl="+mPosterUrl);
				Toast.makeText(mCtx, "Exchange Now!", Toast.LENGTH_LONG).show();
				
				ExchangePopupWindow mExchangePopupWindow = new ExchangePopupWindow(DeltailsActivity.this,mMainLayout,mJsonUrl,mPosterUrl);
				mExchangePopupWindow.show();
			}
		});
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		mClayout = (RelativeLayout) findViewById(R.id.c_layout);

		mBar = new ImageView(mCtx);
		mBar.setBackgroundColor(Color.TRANSPARENT);
		mBar.setImageResource(R.drawable.bar);
	}

	private void initData() 
	{
		mFormat = new DecimalFormat("###,###.00");
		mFormat2 = new DecimalFormat("###,###");
		mFormat3 = new DecimalFormat("0.0");

		IntentFilter connectIntentFilter = new IntentFilter();
		connectIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectionReceiver, connectIntentFilter);

		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		mScreenHeight = mDisplayMetrics.heightPixels;
		mScreenWidth = mDisplayMetrics.widthPixels;
		Utils.UP_DOWN_STEP = mScreenHeight / 10;
		// Utils.IMG_RECT_WIDTH = (int) (mScreenWidth*0.65);

		// InputStream inputStream =
		// getResources().openRawResource(R.drawable.test);
		// loadImage(inputStream);
		showLoadingProgress();
		mLoader.loadImage(mPosterUrl, mImgPoster, true, false);
		mWebServiceLoader.getDetailCommondityContentLoader(mHandler, mCtx, mJsonUrl);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (DEBUG)
			Log.e(TAG, "onKeyDown...keyCode=" + keyCode + "; mStartY=" + mStartY + "; mShowHeight=" + mShowHeight
					+ "; mImgHeight=" + mImgHeight + "; mScreenHeight=" + mScreenHeight + "; mImgDetail.isFocused()="
					+ mImgDetail.isFocused() + "; mBtnExchange.isFocused()=" + mBtnExchange.isFocused());
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (!mImgDetail.isFocused()) {
				mBar.setVisibility(View.VISIBLE);
				showTipProgress();
				mImgDetail.requestFocus();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (!mBtnExchange.isFocused()) {
				hideTipProgress();
				mBar.setVisibility(View.INVISIBLE);
				mBtnExchange.requestFocus();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (!mImgDetail.isFocused()) {
				break;
			}
			if (mShowHeight < mScreenHeight) {
				break;
			} else if (mShowHeight >= mImgHeight) {
				break;
			}
			int step = Utils.UP_DOWN_STEP;
			if (mImgHeight - mShowHeight < Utils.UP_DOWN_STEP) {
				step = mImgHeight - mShowHeight;
			}
			mShowHeight += step;
			mStartY += step;
			mRect.set(0, mStartY, Utils.IMG_RECT_WIDTH, mShowHeight);
			if (mCurrentBitmap != null) {
				mCurrentBitmap.recycle();
			}
			mCurrentBitmap = mDecoder.decodeRegion(mRect, null);
			mImgDetail.setImageBitmap(mCurrentBitmap);

			updateBarPosition();
			// Log.e(TAG, "onKeyDown...x="+mBar.getX()+"; y="+y);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (!mImgDetail.isFocused()) {
				break;
			}
			if (mStartY <= 0) {
				break;
			}

			if (mShowHeight < mScreenHeight) {
				break;
			}
			int stepUp = Utils.UP_DOWN_STEP;
			if (mStartY - Utils.UP_DOWN_STEP < 0) {
				stepUp = mStartY - Utils.UP_DOWN_STEP;
			}
			mShowHeight -= stepUp;
			mStartY -= stepUp;
			if (mStartY < 0) {
				break;
			}
			mRect.set(0, mStartY, Utils.IMG_RECT_WIDTH, mShowHeight);
			if (mCurrentBitmap != null) {
				mCurrentBitmap.recycle();
			}
			mCurrentBitmap = mDecoder.decodeRegion(mRect, null);
			mImgDetail.setImageBitmap(mCurrentBitmap);

			updateBarPosition();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgWhat.NO_NET:
				hideLoadingProgress();
				break;
			case MsgWhat.HIDE_TIP_PROGRESS:
				hideTipProgress();
				break;
			case MsgWhat.LOAD_IMAGE:
				hideLoadingProgress();
				if (msg.obj == null) {
					Log.e(TAG, "Load_image msg.obj is null!");
					return;
				}
				File file = new File(msg.obj.toString());
				InputStream is;
				try {
					is = new FileInputStream(file);
					loadImage(is);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				break;
			case MsgWhat.GET_COMMODITY_CONTENT_RESULT:
				hideLoadingProgress();
				if (msg.obj == null) {
					show("no commodity details info!");
					return;
				}
				CommodityDetailInfo commdityInfo = (CommodityDetailInfo) msg.obj;
				changeCommodityDetailData(commdityInfo);
				break;
			}
		}

		private void show(String string) {
			Toast.makeText(mCtx, string, Toast.LENGTH_LONG).show();
		}

	};

	private void changeCommodityDetailData(final CommodityDetailInfo commdityInfo) {
		if (commdityInfo == null) {
			Toast.makeText(mCtx, "获取商品详细信息失败！", Toast.LENGTH_LONG).show();
			return;
		}

		showDetailInfo(commdityInfo);
		if (commdityInfo.detailUrlList == null || commdityInfo.detailUrlList.size() < 1) {
			Toast.makeText(mCtx, "获取商品详细信息失败！简介为空。", Toast.LENGTH_LONG).show();
			return;
		}
		String url = commdityInfo.detailUrlList.get(0);
		Log.e(TAG, "changeCommodityDetailData...url=" + url);
		try {
			// mLoader.loadImage(url, mImgDetail, true, false);
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			String path = Utils.getCachePath() + File.separator + fileName;
			Log.d(TAG, "changeCommodityDetailData...url=" + url + "; path=" + path + "; fileName=" + fileName);
			File file = new File(path);
			if (file.exists()) {
				loadImage(new FileInputStream(file));
				return;
			}

			showLoadingProgress();
			GetImageThread thread = new GetImageThread();
			thread.fileName = fileName;
			thread.url = url;
			thread.start();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void showDetailInfo(CommodityDetailInfo commodity) {

		float nowIntegral = 0.0f;
		float integral = 0.0f;
		float nowSalePrice = 0.0f;

		mCommodityName.setText(commodity.name);
		try {
			nowIntegral = 330; //Float.parseFloat(commodity.nowIntegral);
			integral = Float.parseFloat(commodity.integral);
			nowSalePrice = Float.parseFloat(commodity.nowSalePrice);
			
			mNowSalePrice.setText("￥" + mFormat.format(nowSalePrice));
			mNowIntegral.setText(mFormat2.format(nowIntegral) + " /");
			mIntegral.setText(mFormat2.format(integral) + "");
			mIntegral.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			if (integral > 0 && nowIntegral > 0 && integral > nowIntegral)
			{
				float lableValue = (nowIntegral / integral) * 10;
				mLabelLayout.setVisibility(View.VISIBLE);
				mLabel.setText(mFormat3.format(lableValue));
			} else {
				mLabelLayout.setVisibility(View.INVISIBLE);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	class GetImageThread extends Thread {

		public String url;
		public String fileName;

		@Override
		public void run() {
			Log.d("GetImageThread", "url=" + url + "; fileName=" + fileName);
			String path = DownloadImgUtils.downloadImg(url, fileName);
			Message msg = mHandler.obtainMessage();
			msg.obj = path;
			msg.what = MsgWhat.LOAD_IMAGE;
			mHandler.sendMessage(msg);
		}

	}

	@SuppressLint("NewApi")
	private void loadImage(InputStream is) {
		try {
			mDecoder = BitmapRegionDecoder.newInstance(is, true);

			showTipProgress();

			mImgDetail.post(new Runnable() {

				@SuppressLint("NewApi")
				@Override
				public void run() {
					mRect.set(0, 0, Utils.IMG_RECT_WIDTH, mScreenHeight);

					Bitmap bm = mDecoder.decodeRegion(mRect, null);
					mImgDetail.setImageBitmap(bm);

					Log.e(TAG,
							"loadImage..mRect.getWidth = " + mRect.width() + "; bm.width=" + bm.getWidth()
									+ "; bm.height=" + bm.getHeight() + "; cLayout.width=" + mClayout.getWidth()
									+ "; mImgDetail.Layout=" + mImgDetail.getWidth() + "; mScreenHeight="
									+ mScreenHeight + "; mScreenWidth=" + mScreenWidth);
					mShowHeight = mScreenHeight;
					mStartY = 0;

					mImgHeight = mDecoder.getHeight();

					showBar();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showBar() {
		try {
			if (mBar.getParent() == null) {
				mBarLp = new LayoutParams();
				mBarLp.format = PixelFormat.TRANSPARENT;
				mBarLp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				mBarLp.flags = mBarLp.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
				mBarLp.width = LayoutParams.WRAP_CONTENT;
				mBarLp.height = LayoutParams.WRAP_CONTENT;
				mBarLp.gravity = Gravity.TOP;
				mBarLp.x = 265;
				mBarLp.y = 0;
				mWm.addView(mBar, mBarLp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void updateBarPosition() {
		try {
			int y = (mStartY * mScreenHeight) / mImgHeight;
			// mBarLp.x= x;
			mBarLp.y = y;
			mWm.updateViewLayout(mBar, mBarLp);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void hideBar() {
		try {
			if (mBar.getParent() != null)
				mWm.removeView(mBar);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void showTipProgress() {
		try {
			if (mUpDownTip.getParent() == null) {
				LayoutParams lp = new LayoutParams();
				lp.format = PixelFormat.TRANSPARENT;
				lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				lp.x = 358;
				lp.y = 0;

				mHandler.removeMessages(MsgWhat.HIDE_TIP_PROGRESS);
				mHandler.sendEmptyMessageDelayed(MsgWhat.HIDE_TIP_PROGRESS, 2 * 1000);

				mWm.addView(mUpDownTip, lp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void hideTipProgress() {
		try {
			if (mUpDownTip.getParent() != null)
				mWm.removeView(mUpDownTip);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void showLoadingProgress() {
		try {
			if (mLoading.getParent() == null) {
				LayoutParams lp = new LayoutParams();
				lp.format = PixelFormat.TRANSPARENT;
				lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;

				mWm.addView(mLoading, lp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void hideLoadingProgress() {
		try {
			if (mLoading.getParent() != null)
				mWm.removeView(mLoading);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	@SuppressLint("InlinedApi")
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo etherNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			NetworkInfo mobileNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			// Log.e(TAG, "mConnectionReceiver...wifiNetInfo=" + wifiNetInfo
			// + "; etherNetInfo=" + etherNetInfo + "; mobileNetInfo="
			// + mobileNetInfo);

			if ((wifiNetInfo != null && wifiNetInfo.isConnected())
					|| (etherNetInfo != null && etherNetInfo.isConnected())
					|| (mobileNetInfo != null && mobileNetInfo.isConnected())) {
				// connect network
				Log.e(TAG, "wifi connect");
			} else {
				Log.e(TAG, "wifi unconnect");
				// unconnect network
			}
		}
	};

	@Override
	protected void onDestroy() 
	{
		if (mConnectionReceiver != null) 
		{
			try {
				this.unregisterReceiver(mConnectionReceiver);
				hideBar();
				hideLoadingProgress();
			} catch (IllegalArgumentException err) {
				return;
			} catch (Exception err) {
				return;
			}
		}
		super.onDestroy();
	}
}