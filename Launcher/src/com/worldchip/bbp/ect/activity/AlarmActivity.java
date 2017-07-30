package com.worldchip.bbp.ect.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.ClockRingAdapter;
import com.worldchip.bbp.ect.db.ClockData;
import com.worldchip.bbp.ect.db.MusicData;
import com.worldchip.bbp.ect.entity.AlarmInfo;
import com.worldchip.bbp.ect.entity.MusicInfo;
import com.worldchip.bbp.ect.receiver.AlarmReceiver;
import com.worldchip.bbp.ect.util.AlarmUtil;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlarmActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private static final String TAG = "--AlarmActivity--";
	private static final int ANLBALE_CLICK=138;
	private ImageView alarm_up_hours;
	private ImageView alarm_down_hours;
	private ImageView alarm_up_minute;
	private ImageView alarm_down_minute;
	private TextView alarm_hour_one, alarm_hour_two;
	private TextView alarm_minute_one, alarm_minute_two;
	private int mAlarmHours = 0;
	private int mAlarmMinutes = 0;
	private ListView mClockSong;
	private ClockRingAdapter adapter = null;
	private List<MusicInfo> listLocalRing = null;
	private List<MusicInfo> listLocalSong = null;
	private List<MusicInfo> list = new ArrayList<MusicInfo>();
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private HashMap<Integer, Integer> mSoundMap;
    private SoundPool mSoundpool;


	private CheckBox mClockWeedMon, mClockWeedTues, mClockWeedWed,
			mClockWeedThur, mClockWeedFri, mClockWeedSat, mClockWeedSun;

	private ImageView mClcokStartCancel, mClcokDel;
	private String time = "";
	private String week = "";
	//private String musicPath = "";
	private Integer _id = -1;
	private Integer eable = 0;                                                                                                                                                                                                                                                                      
	private static final int ALARM_DATA = 1;
	private AlarmInfo mAlarmInfo = null;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ALARM_DATA:
				list.addAll(listLocalRing);
				list.addAll(listLocalSong);
				if (adapter == null) {
					adapter = new ClockRingAdapter(AlarmActivity.this, list);
					mClockSong.setAdapter(adapter);
				}
				adapter.setDatas(list);
				adapter.notifyDataSetChanged();
				break;
			case ANLBALE_CLICK:
				mClcokStartCancel.setClickable(true);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock_alarm);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			_id = bundle.getInt("_id");
			eable = bundle.getInt("eable");
		}
		
		initView();
	
		initData();
	}


	private void initView() {
	
		alarm_up_hours = (ImageView) findViewById(R.id.alarm_up_hours);
		alarm_down_hours = (ImageView) findViewById(R.id.alarm_down_hours);
		alarm_up_minute = (ImageView) findViewById(R.id.alarm_up_minute);
		alarm_down_minute = (ImageView) findViewById(R.id.alarm_down_minute);
		alarm_hour_one = (TextView) findViewById(R.id.alarm_hour_one);
		alarm_hour_two = (TextView) findViewById(R.id.alarm_hour_two);
		alarm_minute_one = (TextView) findViewById(R.id.alarm_minute_one);
		alarm_minute_two = (TextView) findViewById(R.id.alarm_minute_two);
		alarm_up_hours.setOnClickListener(this);
		alarm_down_hours.setOnClickListener(this);
		alarm_up_minute.setOnClickListener(this);
		alarm_down_minute.setOnClickListener(this);
		
		mClockSong = (ListView) findViewById(R.id.clock_song);
		mClockSong.setSelection(0);

		mClockSong.setOnItemClickListener(this);
	
		mClockWeedMon = (CheckBox) findViewById(R.id.clock_weed_add_mon);
		mClockWeedTues = (CheckBox) findViewById(R.id.clock_weed_add_tues);
		mClockWeedWed = (CheckBox) findViewById(R.id.clock_weed_add_wed);
		mClockWeedThur = (CheckBox) findViewById(R.id.clock_weed_add_thur);
		mClockWeedFri = (CheckBox) findViewById(R.id.clock_weed_add_fri);
		mClockWeedSat = (CheckBox) findViewById(R.id.clock_weed_add_sat);
		mClockWeedSun = (CheckBox) findViewById(R.id.clock_weed_add_sun);
		int resId = Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_mon", "drawable");
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		mClockWeedMon.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(bitmap), null, null, null);
		mClockWeedTues.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_tues", "drawable"))), null, null, null);
		mClockWeedWed.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_wed", "drawable"))), null, null, null);
		mClockWeedThur.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_thur", "drawable"))), null, null, null);
		mClockWeedFri.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_fri", "drawable"))), null, null, null);
		mClockWeedSat.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_sat", "drawable"))), null, null, null);
		mClockWeedSun.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), Utils.getResourcesId(MyApplication.getAppContext(), "clock_week_sun", "drawable"))), null, null, null);
		
		mClcokStartCancel = (ImageView) findViewById(R.id.clcok_alarm_save);
		mClcokDel = (ImageView) findViewById(R.id.clcok_alarm_cancel);
		mClcokDel.setImageResource(Utils.getResourcesId(MyApplication.getAppContext(),"clock_week_cancel", "drawable"));
		mClcokStartCancel.setImageResource(Utils.getResourcesId(MyApplication.getAppContext(),"clock_week_save", "drawable"));
		mClcokStartCancel.setOnClickListener(this);
		mClcokDel.setOnClickListener(this);
		
		adapter = new ClockRingAdapter(AlarmActivity.this, null);
		mClockSong.setAdapter(adapter);
		
		mSoundpool=new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap=new HashMap<Integer, Integer>();
		mSoundMap.put(1, mSoundpool.load(AlarmActivity.this, R.raw.click, 1));
		setData();
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					listLocalRing = MusicData.getLocalRing(AlarmActivity.this);
					listLocalSong = MusicData
							.getLocalMusicDatas(AlarmActivity.this);
					mHandler.sendEmptyMessage(ALARM_DATA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void setData() {

		if (_id != -1) {
			mAlarmInfo = ClockData.getByAlarmInfo(this, _id);
			int hours = mAlarmHours = mAlarmInfo.getHours();
			int minutes = mAlarmMinutes = mAlarmInfo.getMusutes();
			if (mAlarmInfo != null && mAlarmInfo.getIsdefault() == 1) {
				alarm_hour_one
						.setBackgroundResource(HttpCommon.imagesArr[hours / 10]);
				alarm_hour_two
						.setBackgroundResource(HttpCommon.imagesArr[hours % 10]);
				alarm_minute_one
						.setBackgroundResource(HttpCommon.imagesArr[minutes / 10]);
				alarm_minute_two
						.setBackgroundResource(HttpCommon.imagesArr[minutes % 10]);
				String week = mAlarmInfo.getDaysofweek();
				if (week.indexOf("2") != -1) {
					mClockWeedMon.setChecked(true);
				}
				if (week.indexOf("3") != -1) {
					mClockWeedTues.setChecked(true);
				}
				if (week.indexOf("4") != -1) {
					mClockWeedWed.setChecked(true);
				}
				if (week.indexOf("5") != -1) {
					mClockWeedThur.setChecked(true);
				}
				if (week.indexOf("6") != -1) {
					mClockWeedFri.setChecked(true);
				}
				if (week.indexOf("7") != -1) {
					mClockWeedSat.setChecked(true);
				}
				if (week.indexOf("1") != -1) {
					mClockWeedSun.setChecked(true);
				}
				if (adapter != null) {
					adapter.setSelectionRingtonePath(mAlarmInfo.getAlert());
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		try {
			switch (v.getId()) {
			case R.id.alarm_up_hours:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				mAlarmHours++;
				if (mAlarmHours > 23) {
					mAlarmHours = 0;
					alarm_hour_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_hour_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours]);
					break;
				} else if (mAlarmHours < 10) {
					alarm_hour_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_hour_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours]);
					break;
				}
				alarm_hour_one
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours / 10]);
				alarm_hour_two
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours % 10]);
				break;
			case R.id.alarm_down_hours:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				mAlarmHours--;
				if (mAlarmHours < 0) {
					mAlarmHours = 23;
				} else if (mAlarmHours < 10) {
					alarm_hour_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_hour_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours]);
					break;
				}
				alarm_hour_one
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours / 10]);
				alarm_hour_two
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmHours % 10]);
				break;
			case R.id.alarm_up_minute:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				mAlarmMinutes++;
				if (mAlarmMinutes > 59) {
					mAlarmMinutes = 0;
					alarm_minute_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_minute_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes]);
					break;
				} else if (mAlarmMinutes < 10) {
					alarm_minute_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_minute_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes]);
					break;
				}
				alarm_minute_one
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes / 10]);
				alarm_minute_two
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes % 10]);
				break;
			case R.id.alarm_down_minute:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				mAlarmMinutes--;
				if (mAlarmMinutes < 1) {
					mAlarmMinutes = 59;
				} else if (mAlarmMinutes < 10) {
					alarm_minute_one
							.setBackgroundResource(HttpCommon.imagesArr[0]);
					alarm_minute_two
							.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes]);
					break;
				}
				alarm_minute_one
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes / 10]);
				alarm_minute_two
						.setBackgroundResource(HttpCommon.imagesArr[mAlarmMinutes % 10]);
				break;
			case R.id.clcok_alarm_save:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				mClcokStartCancel.setClickable(false);
				mHandler.sendEmptyMessageDelayed(ANLBALE_CLICK, 3000);
				week = "";
				if (mClockWeedMon.isChecked()) {
					week = week + 2;
				}
				if (mClockWeedTues.isChecked()) {
					week = week + 3;
				}
				if (mClockWeedWed.isChecked()) {
					week = week + 4;
				}
				if (mClockWeedThur.isChecked()) {
					week = week + 5;
				}
				if (mClockWeedFri.isChecked()) {
					week = week + 6;
				}
				if (mClockWeedSat.isChecked()) {
					week = week + 7;
				}
				if (mClockWeedSun.isChecked()) {
					week = week + 1;
				}
				time = mAlarmHours + ":" + mAlarmMinutes;
				String musicPath = "";
				if (adapter != null) {
					musicPath = adapter.getSelectionRingtonePath();
				}
				if (_id != -1) {
					AlarmInfo oldAlarmInfo = ClockData.getByAlarmInfo(this,
							_id);
					AlarmUtil.cancleAlarm(MyApplication.getAppContext(), oldAlarmInfo);
					ContentValues values = new ContentValues();
					values.put("hours", mAlarmHours);
					values.put("musutes", mAlarmMinutes);
					values.put("daysofweek", week);
					values.put("alert", musicPath);
					boolean updataAlarmInfo = ClockData.updataAlarmInfo(this, _id, values);
					if (updataAlarmInfo && eable == 1) {
						AlarmInfo newAlarmInfo = ClockData.getByAlarmInfo(this,
								_id);
						AlarmUtil.createAlarm(MyApplication.getAppContext(), newAlarmInfo);
					}
				} else {
					if (ClockData.getAlarmInfoSize(this) <= 5) {
						ContentValues values = new ContentValues();
						values.put("hours", mAlarmHours);
						values.put("musutes", mAlarmMinutes);
						values.put("daysofweek", week);
						values.put("enabled", 0);
						values.put("alert", musicPath);
						values.put("isdefault", 1);
						ClockData.addClockAlarm(this, values);
					} else {
					}
				}
				intent = new Intent();
				intent.setClass(this, ClockActivity.class);
				this.setResult(500, intent);
				this.finish();
				break;
			case R.id.clcok_alarm_cancel:
				mSoundpool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
				intent = new Intent();
				intent.setClass(this, ClockActivity.class);
				this.setResult(500, intent);
				this.finish();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			MusicInfo musicInfo = (MusicInfo) parent.getAdapter().getItem(
					position);
			Uri mediaUri = Uri.parse(musicInfo.getData());
			 String musicPath = musicInfo.getData();
			if (adapter != null) {
				adapter.setSelectionRingtonePath(musicPath);
				adapter.notifyDataSetChanged();
			}
			
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();

			mMediaPlayer.setDataSource(AlarmActivity.this, mediaUri);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}
}