package com.worldchip.activity;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.worldchip.bbpaw.videoplayer.R;

public class MainActivity extends Activity implements OnClickListener,
		OnCompletionListener, OnSeekBarChangeListener {

	private static final String TAG = "--MainActivity--";
	protected static final boolean DEBUG = false;

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private MediaPlayer mMediaPlayer;

	private boolean mIsArrayFile = false;
	private boolean isPause = false;
	private String mPath;
	public static final int PAUSE_STATE = 0;
	public static final int PLAY_STATE = 1;
	public static final int SHOW_WINDOW = 2;

	private static final int UPDATE_PROGRESSBAR = 4;
	private static final int HIDEWINDOW = 5;
	private static final int SURFACECREATED = 6;

	private int mPosition = 0;
	private float fwVideo;
	private float fhVideo;
	private int mCurrentIndex = 0;
	private List<String> mPathList = null;

	private boolean isFullScreen = false;
	private boolean isShowSikpBut = false;
	public final static int RESULT_CODE = 100;

	@SuppressLint("HandlerLeak")
	private Handler mHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PAUSE_STATE:
				mBtnPlayOrPause.setBackgroundResource(R.drawable.play);
				break;
			case PLAY_STATE:
				mBtnPlayOrPause.setBackgroundResource(R.drawable.pause);
				break;
			case SHOW_WINDOW:
				if (mMediaPlayer != null) {
					setPrograssMax(mMediaPlayer.getDuration());
				}
				showPopWindow();
				break;
			case HIDEWINDOW:
				if (mPopupWindow != null) {
					try {
						mPopupWindow.dismiss();
					} catch (java.lang.IllegalArgumentException err) {
						return;
					}
				}
				break;
			case SURFACECREATED:
				playVideo();
				break;
			case UPDATE_PROGRESSBAR:
				try {
					if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
						mMovieSeekbar.setProgress(mMediaPlayer
								.getCurrentPosition());
					}
					mHander.removeMessages(UPDATE_PROGRESSBAR);
					mHander.sendEmptyMessageDelayed(UPDATE_PROGRESSBAR, 1000);
				} catch (Exception err) {
					err.printStackTrace();
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWindowStyle();
		setContentView(R.layout.activity_main);
		registerUserReceiver();
		getPath();
		initPopWindow();
		sikpButVisibility();
		magniFication(mPath);
		initSurfaceView(fwVideo, fhVideo);
	}

	private void setWindowStyle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void getPath() {
		mPath = this.getIntent().getStringExtra("single_path");
		isShowSikpBut = this.getIntent().getBooleanExtra("showSikpBut", false);

		mIsArrayFile = false;
		if (mPath == null || mPath.equals("")) {
			mPathList = this.getIntent().getStringArrayListExtra("array_path");
			if (mPathList == null || mPathList.size() <= 0) {
				Toast.makeText(this, "no file for play!", Toast.LENGTH_LONG)
						.show();
				//this.finish();
			}
			mIsArrayFile = true;
			if (mPathList != null) {
				if (DEBUG) {
					Log.e(TAG, "mpathList=" + mPathList.toString());
				}
			}
		}
		if (mPath != null) {
			if (DEBUG) {
				Log.e(TAG, "mPath=" + mPath);
			}
		}
	}

	private void magniFication(String videoPath) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int W = mDisplayMetrics.widthPixels;
		int H = mDisplayMetrics.heightPixels;
		if (DEBUG)
			Log.e(TAG, "width:" + W + "  height:" + H);
		if (isFullScreen) {
			fwVideo = W;
			fhVideo = H;
		} else {
			if (videoPath != null && !TextUtils.isEmpty(videoPath)) {
				MediaMetadataRetriever retr = new MediaMetadataRetriever();
				retr.setDataSource(videoPath);
				String width = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
				String height = retr
						.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
				int wVideo = Integer.parseInt(width);
				int hVideo = Integer.parseInt(height);
				if (DEBUG)
					Log.e(TAG, "wVideo:" + wVideo + "  hVideo:" + hVideo);
				if (wVideo > 0 && hVideo > 0) {	
					///----ZOOM_ORIGIN_SIZE
					//fhVideo = hVideo;
					//fwVideo = wVideo;
					///----ZOOM_FULL_SCREEN_VIDEO_RATIO
					fhVideo = W * hVideo / wVideo;
                	fwVideo = H * wVideo / hVideo;
                	///----ZOOM_4R3
                	//fwVideo = H * 4 / 3;
                	//fhVideo = W * 3 / 4;
                	///----ZOOM_16R9
                	//fwVideo = H * 16 / 9;
                	//fhVideo = W * 9 / 16;
				} else {
					fwVideo = W;
					fhVideo = H;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void initSurfaceView(float width, float height) {
		mSurfaceView = (SurfaceView) findViewById(R.id.sv_video_player);
		android.view.ViewGroup.LayoutParams layoutParams = mSurfaceView
				.getLayoutParams();
		layoutParams.width = (int) width;
		layoutParams.height = (int) height;
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mPosition = mMediaPlayer.getCurrentPosition();
					mMediaPlayer.stop();
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				if (mPosition > 0) {
					playVideo();
					if (mMediaPlayer != null) {
						// Utils.IS_PLAYING = true;
						mMediaPlayer.seekTo(mPosition);
					}
					mPosition = 0;
				} else {
					mHander.sendEmptyMessage(SURFACECREATED);
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mScreenReceiver);
		unregisterReceiver(mVolumeReceiver);
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.previous:
			if (!mIsArrayFile) {
				break;
			}
			mCurrentIndex--;
			if (DEBUG) {
				Log.e(TAG, "onclick... previous is running, mCurrentIndex="
						+ mCurrentIndex);
			}
			if (mCurrentIndex < 0) {
				mCurrentIndex = 0;
				Toast.makeText(this, "This is the first ", Toast.LENGTH_LONG)
						.show();
				return;
			}
			playVideo();
			break;
		case R.id.play_or_pause:
			if (DEBUG) {
				Log.e(TAG, "onclick... play_or_pause is running");
			}
			pauseOrPlay();
			break;
		case R.id.next:
			if (!mIsArrayFile) {
				break;
			}
			mCurrentIndex++;
			if (DEBUG) {
				Log.e(TAG, "onclick... next is running, mCurrentIndex="
						+ mCurrentIndex);
			}

			if (mCurrentIndex > mPathList.size() - 1) {
				mCurrentIndex = mPathList.size() - 1;
				Toast.makeText(this, "This is the last ", Toast.LENGTH_LONG)
						.show();
				return;
			}
			playVideo();
			break;
		case R.id.imgbtn_back:
			if (DEBUG)
				Log.e(TAG, "..onClick.. imgbtn_back is clicked");
			stop();
			if (isShowSikpBut) {
				Intent back = new Intent();
				back.putExtra("back", true);
				setResult(RESULT_CODE, back);
			}
			this.finish();
			break;
		case R.id.imgbtn_sikp:
			if (DEBUG)
				Log.e(TAG, "..onClick.. imgbtn_sikp is clicked");
			stop();
			if (isShowSikpBut) {
				Intent sikp = new Intent();
				sikp.putExtra("back", false);
				setResult(RESULT_CODE, sikp);
			}
			this.finish();
			break;
		default:
			break;
		}
	}

	private void playVideo() {
		if (mIsArrayFile) {
			if (DEBUG)
				Log.e(TAG, "playVideo.. is running, mCurrentIndex ="
						+ mCurrentIndex);
			mPath = mPathList.get(mCurrentIndex);
		}
		try {
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				try {
					mMediaPlayer.release();
					mMediaPlayer = null;
				} catch (Exception err) {
					return;
				}
			}
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.reset();
			mPosition = 0;
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			// setPath();
			mMediaPlayer.setDataSource(mPath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mHander.sendEmptyMessage(UPDATE_PROGRESSBAR);
			mHander.sendEmptyMessage(PLAY_STATE);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "play error", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void setPath() {
		File file = new File(mPath);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			mMediaPlayer.setDataSource(fis.getFD());
			mMediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "play error", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void pauseOrPlay() {
		if (mMediaPlayer != null) {
			if (isPause == false) {
				mMediaPlayer.pause();
				isPause = true;
				mHander.sendEmptyMessage(PAUSE_STATE);
			} else {
				mMediaPlayer.start();
				isPause = false;
				mHander.sendEmptyMessage(PLAY_STATE);
			}
		} else {
			playVideo();
		}
	}

	@SuppressWarnings("unused")
	private void reset() {
		// 跳转到视频的最开始
		mMediaPlayer.seekTo(0);
		mMediaPlayer.start();
	}

	private void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if (isShowSikpBut) {
			stop();
			this.finish();
		}
		mHander.sendEmptyMessage(PAUSE_STATE);
		mMediaPlayer.release();
		mMediaPlayer = null;
	}

	private PopupWindow mPopupWindow;
	private LayoutInflater layoutInfalter;
	private View popWindow;
	private ImageButton mBtnPrevious;
	private ImageButton mBtnPlayOrPause;
	private ImageButton mImgbtnBack;
	private ImageButton mBtnNext;
	private ImageButton mBtnSikp;
	private SeekBar mMovieSeekbar;
	private SeekBar mVoiceSeekbar;
	private AudioManager mAudiomanage;
	private int mCurrentVolume;
	private MyVolumeReceiver mVolumeReceiver;
	private ImageView mImgSoundBg;

	@SuppressLint("InlinedApi")
	private void initPopWindow() {
		layoutInfalter = LayoutInflater.from(this);
		popWindow = layoutInfalter.inflate(R.layout.popwindowcontent, null);
		mBtnPrevious = (ImageButton) popWindow.findViewById(R.id.previous);
		mBtnPlayOrPause = (ImageButton) popWindow
				.findViewById(R.id.play_or_pause);
		mImgSoundBg = (ImageView) popWindow.findViewById(R.id.img_sounds);
		mImgbtnBack = (ImageButton) popWindow.findViewById(R.id.imgbtn_back);
		mBtnSikp = (ImageButton) popWindow.findViewById(R.id.imgbtn_sikp);
		mImgbtnBack.setOnClickListener(MainActivity.this);
		mBtnSikp.setOnClickListener(MainActivity.this);
		sikpButVisibility();
		mBtnNext = (ImageButton) popWindow.findViewById(R.id.next);
		mMovieSeekbar = (SeekBar) popWindow.findViewById(R.id.seekbar_movie);
		mVoiceSeekbar = (SeekBar) popWindow.findViewById(R.id.seekbar_voice);
		mBtnPrevious.setOnClickListener(MainActivity.this);
		mBtnPlayOrPause.setOnClickListener(MainActivity.this);
		mBtnNext.setOnClickListener(MainActivity.this);
		mMovieSeekbar.setOnSeekBarChangeListener(MainActivity.this);
		mAudiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mVoiceSeekbar.setMax(mAudiomanage
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mCurrentVolume = mAudiomanage
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mVoiceSeekbar.setProgress(mCurrentVolume);
		if (mCurrentVolume != 0) {
			mImgSoundBg.setImageResource(R.drawable.voice);
		} else {
			mImgSoundBg.setImageResource(R.drawable.mute);
		}
		registerVolumeChangeBroadcast();
		mVoiceSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				mAudiomanage
						.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
				mCurrentVolume = mAudiomanage
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				mVoiceSeekbar.setProgress(mCurrentVolume);
				if (arg1 != 0) {
					mImgSoundBg.setImageResource(R.drawable.voice);
				} else {
					mImgSoundBg.setImageResource(R.drawable.mute);
				}
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
		});
		mPopupWindow = new PopupWindow(popWindow, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, false);
	}

	private void sikpButVisibility() {
		if (isShowSikpBut) {
			mBtnSikp.setVisibility(View.VISIBLE);
		} else {
			mBtnSikp.setVisibility(View.INVISIBLE);
		}
	}

	private void setPrograssMax(int maxValue) {
		mMovieSeekbar.setMax(maxValue);
	}

	/**
	 * show popup window
	 */
	private void showPopWindow() {
		mPopupWindow.showAtLocation(
				layoutInfalter.inflate(R.layout.activity_main, null),
				Gravity.CENTER, 0, 0);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		mPosition = arg0.getProgress();
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(mPosition);
		}
	}

	/**
	 * Monitor screen click event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mHander.removeMessages(HIDEWINDOW);
		mHander.sendEmptyMessage(SHOW_WINDOW);
		mHander.sendEmptyMessageDelayed(HIDEWINDOW, 5000);
		return super.onTouchEvent(event);
	}

	/**
	 * Register when the volume changes received broadcast
	 */
	private void registerVolumeChangeBroadcast() {
		mVolumeReceiver = new MyVolumeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(mVolumeReceiver, filter);
	}

	/**
	 * When handling the volume change of interface display
	 * 
	 * @author long
	 */
	private class MyVolumeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果音量发生变化则更改seekbar的位置
			if (intent.getAction()
					.equals("android.media.VOLUME_CHANGED_ACTION")) {
				int currVolume = mAudiomanage
						.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
				if (mVoiceSeekbar != null) {
					mVoiceSeekbar.setProgress(currVolume);
				}
			}
		}
	}

	private void registerUserReceiver() {
		if (DEBUG)
			Log.e(TAG, "registerUserReceiver...");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenReceiver, filter);
	}

	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				if (DEBUG)
					Log.e(TAG, "mScreenReceiver..screen on");
				doSomethingOnScreenOn();
			} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				if (DEBUG)
					Log.e(TAG, "mScreenReceiver..screen off");
				doSomethingOnScreenOff();
			}
		}
	};

	private void doSomethingOnScreenOn() {
		if (DEBUG)
			Log.e(TAG, "doSomethingOnScreenOn");
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}

	private void doSomethingOnScreenOff() {
		if (DEBUG)
			Log.e(TAG, "doSomethingOnScreenOff..mMediaPlayer=" + mMediaPlayer);
		mHander.removeMessages(UPDATE_PROGRESSBAR);
		if (mMediaPlayer != null) {
			if (DEBUG)
				Log.e(TAG, "doSomethingOnScreenOff..will release mediaplay..");
			mMediaPlayer.stop();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (DEBUG)
				Log.e(TAG, "isShowSikpBut = " + isShowSikpBut);
			if (isShowSikpBut) {
				Intent sikp = new Intent();
				sikp.putExtra("back", true);
				setResult(RESULT_CODE, sikp);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
