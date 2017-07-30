package com.worldchip.bbp.bbpawmanager.cn.activity;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.HttpResponseCallBack;
import com.worldchip.bbp.bbpawmanager.cn.callbak.PasswordValidateListener;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager;
import com.worldchip.bbp.bbpawmanager.cn.json.HomePageJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.model.HomePageInfo;
import com.worldchip.bbp.bbpawmanager.cn.push.MessagePushService;
import com.worldchip.bbp.bbpawmanager.cn.push.PushMessageContents;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.HttpUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.LogUtil;
import com.worldchip.bbp.bbpawmanager.cn.utils.ScaleAnimEffect;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.view.PasswordInputDialog;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private Context mCtx;

	private TextView mNoticeTitleTV;
	private TextView mInformUnReadCount;
	private static final int START_APP_NOT_FOUND_ERROR = 0;
	private static final int START_APP_ERROR = 1;
	private HomePageDataLoadCallback mCallback;
	private static final int REFRESH_NOTICE_BOARD = 100;
	private static final int REFRESH_UNREAD_COUNT = 101;
	private ScaleAnimEffect mAnimEffect = new ScaleAnimEffect();
	private static float big_x = 1.08F;
	private static float big_y = 1.08F;
	private WebView mContentWebView;
	private MainPushMessageReceiver mPushMessageReceiver = null;
	private PasswordInputDialog mPasswordInputDialog = null;
	private ImageView mVaccineMenu, mEyecareMenu;
	boolean isShowPswDialog = true;
	private int mScreenWidth, mScreenHeight;
	
	private boolean isAbroad = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case START_APP_NOT_FOUND_ERROR:
				Toast.makeText(MainActivity.this, R.string.activity_not_found,
						Toast.LENGTH_SHORT).show();
				break;
			case START_APP_ERROR:
				Toast.makeText(MainActivity.this, R.string.start_app_error,
						Toast.LENGTH_SHORT).show();
				break;
			case REFRESH_NOTICE_BOARD:
				HomePageInfo noticeBoardDatas = DataManager
						.getNoticeBoardDatas(mCtx);
				updateNoticeBoard(noticeBoardDatas);
				break;
			case REFRESH_UNREAD_COUNT:
				int count = DataManager.getInformUnReadCount(mCtx);
				updateInformUnReadCountView(count);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		if (isAbroad) {
			setContentView(R.layout.activity_main_large);
		} else {
			setContentView(R.layout.activity_main);
		}
		mCtx = this;
		showPasswordInputView();
		initView();
	}

	/**
	 * 隐藏菜单
	 * 
	 * @param context
	 * @param flag
	 */
	public static void hideSystemUI(Context context, boolean flag) {
		Intent intent = new Intent();
		intent.setAction(Utils.UPDATE_SYSTEMUI);
		intent.putExtra("hide_systemui", flag);
		context.sendBroadcast(intent);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		if (!isAbroad) {
			mNoticeTitleTV = (TextView) findViewById(R.id.parent_title);
			mContentWebView = (WebView) findViewById(R.id.content);
			WebSettings settings = mContentWebView.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setDefaultTextEncodingName("utf-8");
			mContentWebView.setBackgroundColor(0); // 设置背景色
			mContentWebView.setOnLongClickListener(new OnLongClickListener() {// 屏蔽长按
						@Override
						public boolean onLongClick(View v) {
							return true;
						}
					});
			mContentWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Common.openBBPawBrowser(view.getContext(), url);
					return true;
				}
			});
		}
		mInformUnReadCount = (TextView) findViewById(R.id.main_message_count);
		if (!isAbroad) {
			mEyecareMenu = (ImageView) findViewById(R.id.main_eyecare);
			mEyecareMenu.setOnClickListener(this);
			findViewById(R.id.main_growup_record).setOnClickListener(this);
			mVaccineMenu = (ImageView) findViewById(R.id.main_vaccine);
			mVaccineMenu.setOnClickListener(this);
		}
		findViewById(R.id.main_message).setOnClickListener(this);
		findViewById(R.id.main_manage).setOnClickListener(this);
		findViewById(R.id.main_settings).setOnClickListener(this);
		findViewById(R.id.main_back_btn).setOnClickListener(this);

		// 注册推送消息监听
		if (!Common.isPushServiceRunning()) {
			MessagePushService.actionStart(getApplicationContext());
		}
		mPushMessageReceiver = new MainPushMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Utils.RECEIVE_PUSH_MESSAGE_ACTION);
		registerReceiver(mPushMessageReceiver, filter);

		mCallback = new HomePageDataLoadCallback();
	}

	public void updateInformUnReadCountView(int count) {
		if (mInformUnReadCount != null) {
			if (count > 0) {
				mInformUnReadCount.setVisibility(View.VISIBLE);
				mInformUnReadCount.setText(String.valueOf(count));
			} else {
				mInformUnReadCount.setVisibility(View.GONE);
			}
		}
	}

	/*
	 * @Override public void update(String event, String messageType) { if
	 * (event.equals(PushMessageManager.RECEIVE_MESSAGE_EVENT)) { if
	 * (messageType.equals(PushMessageContents.NOTICE_MESSAGE_MAIN_TYPE)) {
	 * HomePageInfo noticeBoardDatas = DataManager.getNoticeBoardDatas(mCtx);
	 * Message msg = new Message(); msg.what = REFRESH_NOTICE_BOARD; msg.obj =
	 * noticeBoardDatas; mHandler.sendMessage(msg); } else if
	 * (messageType.equals(PushMessageContents.INFORMATION_MAIN_TYPE) ||
	 * messageType.equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)){
	 * mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT); } } }
	 */

	private class MainPushMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Utils.RECEIVE_PUSH_MESSAGE_ACTION)) {
				String messageType = intent
						.getStringExtra(Utils.RECEIVE_PUSH_MESSAGE_TYPE_KEY);
				if (!TextUtils.isEmpty(messageType)) {
					if (messageType
							.equals(PushMessageContents.NOTICE_MESSAGE_MAIN_TYPE)) {
						HomePageInfo noticeBoardDatas = DataManager
								.getNoticeBoardDatas(mCtx);
						Message msg = new Message();
						msg.what = REFRESH_NOTICE_BOARD;
						msg.obj = noticeBoardDatas;
						mHandler.sendMessage(msg);
					} else if (messageType
							.equals(PushMessageContents.INFORMATION_MAIN_TYPE)
							|| messageType
									.equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)) {
						mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
					}
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateMenu();
		hideSystemUI(mCtx, true);
		String language = Locale.getDefault().getLanguage() + "_"
				+ Locale.getDefault().getCountry();
		HttpUtils.doPost(
				Utils.HTTP_HOME_PAGE_REQ_URL + "&language=" + language,
				mCallback, Utils.HTTP_TAG_HOMEPAGE_LOAD);
		if (mHandler != null) {
			mHandler.sendEmptyMessage(REFRESH_UNREAD_COUNT);
		}
	}

	private void updateMenu() {
		String language = Locale.getDefault().getLanguage();
		if (mVaccineMenu != null) {
			if (language.contains("zh")) {
				mVaccineMenu.setVisibility(View.VISIBLE);
			} else {
				mVaccineMenu.setVisibility(View.GONE);
			}
		}
		if (mEyecareMenu != null) {
			if (mScreenWidth == 1024) {
				mEyecareMenu.setVisibility(View.GONE);
			} else {
				mEyecareMenu.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (mPushMessageReceiver != null) {
				unregisterReceiver(mPushMessageReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_eyecare:
			Intent eyecareIntent = new Intent();
			eyecareIntent
					.setComponent(new ComponentName(
							"com.worldchip.bbp.bbpawmanager.cn",
							"com.worldchip.bbp.bbpawmanager.cn.activity.EyecareActivity"));
			startActivity(view, eyecareIntent);
			break;
		case R.id.main_growup_record:
			startActivity(view, new Intent(MainActivity.this,
					GrowthLogActivity.class));
			break;
		case R.id.main_vaccine:
			startActivity(view, new Intent(MainActivity.this,
					VaccineActivity.class));
			break;
		case R.id.main_message:
			startActivity(view, new Intent(MainActivity.this,
					InformationActivity.class));
			break;
		case R.id.main_manage:
			Intent manageIntent = new Intent();
			manageIntent.setComponent(new ComponentName(
					"com.worldchip.bbp.ect",
					"com.worldchip.bbp.ect.activity.PatriarchControlActivity"));
			startActivity(view, manageIntent);
			break;
		case R.id.main_settings:
			Intent settingsIntent = new Intent();
			settingsIntent.setComponent(new ComponentName(
					"com.android.settings", "com.android.settings.Settings"));
			startActivity(view, settingsIntent);
			break;
		case R.id.main_back_btn:
			MainActivity.this.finish();
			return;

		default:
			break;
		}
	}

	private class HomePageDataLoadCallback implements HttpResponseCallBack {

		@Override
		public void onStart(String httpTag) {
		}

		@Override
		public void onSuccess(String result, String httpTag) {
			HomePageInfo info = HomePageJsonParse.doParseJsonToBean(result);
			DataManager.updateNoticeBoardToDB(mCtx, info);
			mHandler.sendEmptyMessage(REFRESH_NOTICE_BOARD);
		}

		@Override
		public void onFailure(Exception e, String httpTag) {
			if (e != null) {
				LogUtil.e(TAG, e.toString());
			}
			mHandler.sendEmptyMessage(REFRESH_NOTICE_BOARD);
		}

		@Override
		public void onFinish(int result, String httpTag) {

		}
	}

	private void updateNoticeBoard(HomePageInfo info) {
		int count = DataManager.getInformUnReadCount(this);
		updateInformUnReadCountView(count);
		if (info != null) {
			if (mNoticeTitleTV != null) {
				mNoticeTitleTV.setText(info.getTitle());
			}
			if (!isAbroad) {
				LogUtil.e(TAG, "mContentWebView == " + info.getContent());
				mContentWebView.loadDataWithBaseURL(null, info.getContent(),
						"html/text", "utf-8", null);
			}
		} else {
			String language = Locale.getDefault().getLanguage();
			LogUtil.e(TAG, "language == " + language);
			if (!isAbroad) {
				if (language.contains("zh")) {
					mContentWebView.loadUrl(Configure.DEFAULT_NOTICE_CN);
				} else {
					mContentWebView.loadUrl(Configure.DEFAULT_NOTICE_ENG);
				}
			}
		}
	}

	private void startActivity(final View view, final Intent intent) {

		mAnimEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 200);
		Animation anim = mAnimEffect.createAnimation();
		anim.setFillBefore(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (intent != null) {
					try {
						mCtx.startActivity(intent);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(START_APP_NOT_FOUND_ERROR);
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(START_APP_ERROR);
					}
				}
			}
		});
		view.startAnimation(anim);
	}

	private void showPasswordInputView() {
		if (mPasswordInputDialog != null) {
			if (mPasswordInputDialog.isShowing()) {
				mPasswordInputDialog.dismiss();
				mPasswordInputDialog = null;
			}
		}
		mPasswordInputDialog = PasswordInputDialog.createDialog(mCtx);
		mPasswordInputDialog.setListener(new PasswordValidateListener() {
			@Override
			public void onValidateComplete(boolean success) {
				if (!success) {
					MainActivity.this.finish();
				}
			}
		});
		mPasswordInputDialog.show();
	}
}
