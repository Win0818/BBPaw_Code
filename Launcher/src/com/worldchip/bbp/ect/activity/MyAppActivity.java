package com.worldchip.bbp.ect.activity;

import java.util.HashMap;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.GridMyAppAdapter;
import com.worldchip.bbp.ect.db.AppData;
import com.worldchip.bbp.ect.entity.AppInfo;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.image.utils.ImageLoader;
import com.worldchip.bbp.ect.image.utils.ImageLoader.Type;
import com.worldchip.bbp.ect.service.FloatWindowService;
import com.worldchip.bbp.ect.util.MyWindowManager;
import com.worldchip.bbp.ect.util.Utils;
import com.worldchip.bbp.ect.view.RoundImageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class MyAppActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {

	private RoundImageView mRoundImageView;
	private TextView mMyAppBack,mMyApp_Top;
	private TextView mMyAppName, mPalyTime;
	private GridView mMyAppGridView;
	private GridMyAppAdapter shareAppAdapter;
	private AppInfo appInfo = null;
	private HashMap<Integer, Integer> mSoundMap;
    private SoundPool mSoundpool;
    private SharedPreferences mBabyinfo;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				shareAppAdapter.delPackageName(appInfo);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myapp_main);

		// HttpCommon.hideSystemUI(MyAppActivity.this, false);

		// ��ʼ���ؼ�
		initView();
		mSoundpool=new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap=new HashMap<Integer, Integer>();
		mSoundMap.put(1, mSoundpool.load(MyAppActivity.this, R.raw.click, 1));
		// ��ʼ�����
		initData();
		// ������ķ���
		startService(new Intent(MyAppActivity.this, FloatWindowService.class));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// HttpCommon.hideSystemUI(MyAppActivity.this, true);
		super.onDestroy();
		stopService(new Intent(MyAppActivity.this, FloatWindowService.class));
		
		MyWindowManager.removeSmallWindow(getApplicationContext());
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		// ͷ��
		mBabyinfo = getSharedPreferences("babyinfo", 0);
		mRoundImageView = (RoundImageView) findViewById(R.id.myappsrc);
		if (LoginState.getInstance().isLogin()) {
		String photoUrl = mBabyinfo.getString(Utils.USER_SHARD_PHOTO_KEY, "");
		ImageLoader imageLoader = ImageLoader.getInstance(1, Type.LIFO);
		if (photoUrl != null && mRoundImageView != null) {
			imageLoader.loadImage(photoUrl, mRoundImageView, true,false);
		}
		}else{
			mRoundImageView.setImageResource(R.drawable.app_default_photo);
		}
		mMyAppBack = (TextView) findViewById(R.id.myapp_back);
		mMyAppBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
//				Intent intent = new Intent();
//				ComponentName comp = new ComponentName("com.worldchip.bbpaw.chinese.zhbx","org.cocos2dx.cpp.AppActivity");
//				intent.setComponent(comp);
//				startActivity(intent);
				finish();
			}
		});
		// ��ɫ����
		mMyAppName = (TextView) findViewById(R.id.myappname);
		// ����ʱ��
		mPalyTime = (TextView) findViewById(R.id.palytime);
		mMyApp_Top = (TextView) findViewById(R.id.myapp_right_top);
        if(Utils.getLanguageInfo(getApplicationContext())==1){
        	mMyApp_Top.setBackgroundResource(R.drawable.myapp_right_top);
        }else{
        	mMyApp_Top.setBackgroundResource(R.drawable.myapp_right_top_eng);
        }
		// �ҵ�APP
		mMyAppGridView = (GridView) findViewById(R.id.myapp_gridView);
		mMyAppGridView.setOnItemClickListener(this);
		mMyAppGridView.setOnItemLongClickListener(this);
	}

	/**
	 * ��ʼ�����
	 */
	@SuppressLint("SimpleDateFormat")
	private void initData() {
		// ��ʼ����ɫͼƬ
		StringBuilder buffer = new StringBuilder();
		String targetDir = buffer.append(Environment.getDataDirectory())
				.append("/data/").append(getPackageName())
				.append("/imagedata/").toString();
		Bitmap imageBitmap = BitmapFactory.decodeFile(targetDir
				+ "imagename.png");
		if (imageBitmap != null) {
			mRoundImageView.setImageBitmap(imageBitmap);
		}

		// ��ȡ��ɫ������
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo != null) {
			String name = userInfo.getString("name", "");
			mMyAppName.setText(name);
		}

		// ��ʼ������ʱ��
		SharedPreferences preferences = getSharedPreferences("time_info", 0);
		if (preferences != null) {
			int countdownState = preferences.getInt("countdownState", 0);
			Log.e("countdownState", "countdownState---" + (countdownState == 1));
			int time = preferences.getInt("countdown", 0);
			Log.e("time", "time---" + time);
			if (countdownState == 1) {
				MinuteCountDown mc = new MinuteCountDown(time * 1000, 1000);
				mPalyTime.setBackgroundColor(Color.TRANSPARENT);
				mc.start();
			} else {
				mPalyTime.setText("00:00");
				mPalyTime.setBackgroundResource(R.drawable.ban_time);
			}
		}

		// ��ʼ�����
		AppData.clearShareAppList();
		shareAppAdapter = new GridMyAppAdapter(MyAppActivity.this,
				AppData.getLocalShareAppDatas(MyAppActivity.this));
		mMyAppGridView.setAdapter(shareAppAdapter);
	}

	/**
	 * ����һ������ʱ���ڲ��� �Ϊ60����
	 */
	class MinuteCountDown extends CountDownTimer {
		public MinuteCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mPalyTime.setText("00:00");
			mPalyTime.setBackgroundResource(R.drawable.ban_time);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int sum_millisUntilFinished = (int) (millisUntilFinished - 1000);
			int minute = sum_millisUntilFinished / (60 * 1000);
			int minute_decade = minute / 10;
			int minute_units = minute % 10;

			int second = sum_millisUntilFinished / 1000;
			int second_decade = second % 60 / 10;
			int second_units = second % 10;

			mPalyTime.setText(minute_decade + "" + minute_units + ":"
					+ second_decade + "" + second_units);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		shareAppAdapter.setIsLongClick(true);
		shareAppAdapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
		AppInfo info = (AppInfo) parent.getAdapter().getItem(position);
		if (shareAppAdapter.getIsLongClick())// ����ɾ��
		{
			boolean b = AppData.getShareAppByData(this, info.getPackageName());
			if (b) {
				appInfo = info;
				if (appInfo != null) {
					boolean c = AppData.delShareAppData(this,
							info.getPackageName());
					if (c) {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
						shareAppAdapter.setIsLongClick(false);
					}
				}
			}
		} else {
			// ������Ӧ��APP
			PackageManager packageManager = this.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(info
					.getPackageName());
			startActivity(intent);
		}
	}
}