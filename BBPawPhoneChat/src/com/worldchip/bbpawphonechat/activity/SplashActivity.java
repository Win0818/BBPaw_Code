package com.worldchip.bbpawphonechat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.HXSDKHelper;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.application.MyChatHXSDKHelper;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private LinearLayout rootLayout;
	private TextView versionText;
	private boolean isFirst = true;

	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);
		rootLayout = (LinearLayout) findViewById(R.id.splash_root);

		MyApplication.getInstance().ImageAdapter(
				rootLayout,
				new int[] { R.drawable.guide_3, R.drawable.guide_3_es,
						R.drawable.guide_3_en });
		versionText = (TextView) findViewById(R.id.tv_version);
		isFirst = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, "is_frist");

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {
			public void run() {
				if (isFirst) {
					startActivity(new Intent(SplashActivity.this,
							NavigationActivity.class));
					finish();
				} else {
					if (MyChatHXSDKHelper.getInstance().isLogined()) {
						long start = System.currentTimeMillis();
						EMChatManager.getInstance().loadAllConversations();
						HXSDKHelper.getInstance().getNotifier()
								.isNotification();
						long costTime = System.currentTimeMillis() - start;
						// 等待sleeptime时长
						if (sleepTime - costTime > 0) {
							try {
								Thread.sleep(sleepTime - costTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// 进入主页面
						startActivity(new Intent(SplashActivity.this,
								MainActivity.class));
						finish();
					} else {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						startActivity(new Intent(SplashActivity.this,
								LoginActivity.class));
						finish();
					}
				}
			}
		}).start();

	}

	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}

}
