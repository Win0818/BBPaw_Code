package com.worldchip.bbp.ect.activity;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.ListViewAdapter;
import com.worldchip.bbp.ect.db.BrowserData;
import com.worldchip.bbp.ect.entity.BrowserInfo;
import com.worldchip.bbp.ect.util.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

import android.widget.ListView;

public class BrowserActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private WebView mWebView;
	private ImageButton mImgbtnBack;
	private ImageButton mImgbtnForward;
	private ImageButton mImgbtnClose;
	private ImageButton mImgbtnCollection;
	private ListView mListViewUrlShow;
	private boolean isShow = false;
	private String mAddress = null;
	private boolean DEBUG = true;
	private final String TAG = "BrowserActivity";
	private List<BrowserInfo> mList;
	private static final int GO_BACK = 0;
	private static final int GO_FORWARD = 1;
	private static final int URL_COLLECTION = 2;
	private static final int ENTER = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.browser_activity);
		mList = BrowserData.getBrowserAddressList(this);
		initView();
		initListView();
		mWebView = (WebView) findViewById(R.id.webview);
		String address = this.getIntent().getStringExtra("address");
		if (address == null || address.equals("")) {
			if(Utils.getLanguageInfo(getApplicationContext())==1){
				mAddress = "http://www.bbpaw.com.cn/";
			}else{
				mAddress="http://www.bbpaw.com/";
			}
			
		} else {
			if (!address.contains("http:")) {
				mAddress = "http://" + address.trim();
			} else {
				mAddress = address.trim();
			}
			if (DEBUG) {
				Log.e(TAG, "address=" + mAddress);
			}
		}
		setWebView();
	}

	private void initView() {
		mImgbtnBack = (ImageButton) findViewById(R.id.imgbtn_back);
		mImgbtnForward = (ImageButton) findViewById(R.id.imgbtn_forward);
		mImgbtnClose = (ImageButton) findViewById(R.id.imgbtn_close);
		mImgbtnCollection = (ImageButton) findViewById(R.id.imgbtn_collection);
		mImgbtnBack.setEnabled(false);
		mImgbtnForward.setEnabled(false);
		mImgbtnBack.setOnClickListener(this);
		mImgbtnForward.setOnClickListener(this);
		mImgbtnClose.setOnClickListener(this);
		mImgbtnCollection.setOnClickListener(this);
	}

	private void initListView() {
		mListViewUrlShow = (ListView) findViewById(R.id.lv_url);
		mListViewUrlShow.setAdapter(new ListViewAdapter(BrowserActivity.this,
				mList));
		mListViewUrlShow.setOnItemClickListener(BrowserActivity.this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		try {
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.loadUrl(mAddress);
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					if (DEBUG)
						Log.e(TAG, "..shouldOverrideUrlLoading.. url=" + url);
					/*if (url.contains(mAddress)
							|| url.contains(mAddress.replace("http://", ""))) {
						view.loadUrl(url);
					} else {
						//mWebView.loadUrl(mAddress);
					}*/
					view.loadUrl(url);
					return true;
				}
				
				@Override
				public void onPageFinished(WebView view, String url) {
					// TODO Auto-generated method stub
					updateBtnState();
					super.onPageFinished(view, url);
				}
			});
			
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}

	}

	private void setAnimation() {
		if (!isShow) {
			ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1.0f, 0,
					1.0f, 1.0f, 1.0f);
			scaleAnimation.setDuration(800);
			mListViewUrlShow.setAnimation(scaleAnimation);
			isShow = false;
		} else {
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0, 1.0f,
					0, 0.5f, 0.5f);
			scaleAnimation.setDuration(800);
			mListViewUrlShow.setAnimation(scaleAnimation);
			isShow = true;
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GO_BACK:
				mWebView.goBack();

				break;
			case GO_FORWARD:
				mWebView.goForward();

				break;
			case URL_COLLECTION:
				if (!isShow) {
					mListViewUrlShow.setVisibility(View.VISIBLE);
					isShow = true;
				} else {
					mListViewUrlShow.setVisibility(View.GONE);
					isShow = false;
				}
				break;
			case ENTER:

				mListViewUrlShow.setVisibility(View.GONE);
				mWebView.loadUrl(mAddress);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.imgbtn_back:
			mHandle.sendEmptyMessage(GO_BACK);
			break;
		case R.id.imgbtn_forward:
			mHandle.sendEmptyMessage(GO_FORWARD);
			break;
		case R.id.imgbtn_close:
			this.finish();
			break;
		case R.id.imgbtn_collection:
			mHandle.sendEmptyMessage(URL_COLLECTION);
			break;

		default:
			break;
		}
	}

	/**
	 * Update button state
	 */
	private void updateBtnState() {
		if (mWebView.canGoBack()) {
			mImgbtnBack.setEnabled(true);
			if (DEBUG)
				Log.e(TAG, "..updateBtnState.. canGoBack");

		} else {
			mImgbtnBack.setEnabled(false);
			if (DEBUG)
				Log.e(TAG, "..updateBtnState.. not canGoBack");
		}
		if (mWebView.canGoForward()) {
			mImgbtnForward.setEnabled(true);
			if (DEBUG)
				Log.e(TAG, "..updateBtnState..  canGoForward");
		} else {
			mImgbtnForward.setEnabled(false);
			if (DEBUG)
				Log.e(TAG, "..updateBtnState..  not canGoForward");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
			mWebView.goBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mAddress = null;
		mAddress = "http:" + mList.get(arg2).url;
		mHandle.sendEmptyMessage(ENTER);
	}

	/**
	 * Monitor screen click event
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (isShow) {
			mHandle.sendEmptyMessage(URL_COLLECTION);
		}

		return super.onTouchEvent(event);
	}
}
