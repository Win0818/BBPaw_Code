package com.worldchip.bbpaw.media.camera.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpaw.media.camera.R;
import com.worldchip.bbpaw.media.camera.Interface.CameraInterface;
import com.worldchip.bbpaw.media.camera.utils.CameraConfig;
import com.worldchip.bbpaw.media.camera.utils.CameraManager;
import com.worldchip.bbpaw.media.camera.utils.CameraUtils;
import com.worldchip.bbpaw.media.camera.utils.Configure;
import com.worldchip.bbpaw.media.camera.utils.EffectData;
import com.worldchip.bbpaw.media.camera.utils.ImageTool;
import com.worldchip.bbpaw.media.camera.utils.LogUtil;
import com.worldchip.bbpaw.media.camera.utils.MediaRecordManager;
import com.worldchip.bbpaw.media.camera.utils.ScannerClient;
import com.worldchip.bbpaw.media.camera.utils.SimpleAudioPlayer;
import com.worldchip.bbpaw.media.camera.utils.Utils;
import com.worldchip.bbpaw.media.camera.view.CameraPreview;
import com.worldchip.bbpaw.media.camera.view.DMEffectsListAdapter;
import com.worldchip.bbpaw.media.camera.view.EffectListAdapter;
import com.worldchip.bbpaw.media.camera.view.EffectsView;
import com.worldchip.bbpaw.media.camera.view.GlobalAlertDialog;

public class CameraActivity extends Activity implements OnClickListener,
		CameraInterface, OnErrorListener, OnInfoListener {

	private static final String TAG = CameraActivity.class.getSimpleName();
	// private SurfaceView mSurfaceView;
	private CameraPreview mCameraPreview;
	private EffectsView mEffectDrawView = null;
	// private SurfaceHolder mSurfaceHolder;
	private ImageView[] mToolImageViews = new ImageView[5];
	private ImageView[] mToolImageBgViews = new ImageView[5];
	private ImageView[] mEfficacyViews = new ImageView[5];
	private ImageView[] mEfficacyBgViews = new ImageView[5];
	private ImageView mCameraConvBtn;
	private ImageView mBackBtn;
	private ImageView mCenterShutterView;
	private static final int CAMERA_MODE_INDEX = 0;
	private static final int GALLERY_INDEX = 1;
	private static final int SAVE_INDEX = 2;
	private static final int SHUTTER_INDEX = 3;
	private static final int MAGIC_INDEX = 4;

	private static final int GENERAL_EFFECT_INDEX = 0;
	private static final int DISTORTING_EFFECT_INDEX = 1;
	private static final int INDIVIDUALITY_EFFECT_INDEX = 2;
	private static final int GRAFFITI_EFFECT_INDEX = 3;
	private static final int ACCESSORIES_EFFECT_INDEX = 4;

	private int mCurrCameraFacing = CameraConfig.DEFAULT_CAMERA_FACING;
	private int mCameraMode = CameraConfig.DEFAULT_CAMERA_MODE;
	private MediaRecordManager mMediaRecordManager;
	private PopupWindow mSpecialEfficacyPop;
	private PopupWindow mEffectListPop;
	private PopupWindow mDMEffectListPop;
	private View mEffectListView;
	private View mDMEffectListView;
	private GridView mEffectGridView;
	private EffectListAdapter mEffectAdapter;
	private View mEfficacyRL;
	private View mRightToolViews;
	private boolean isEditState = false;
	
	private ImageView mSwitchCamera;
	
	private boolean isStartDefCamera = false;

	// private Bitmap mFinalPic = null;
	// private View mMainView;

	public enum SpecicalEfficacy {
		GENERAL, DISTORTING, INDIVIDUALITY, GRAFFITI, ACCESSORIES
	}

	/**
	 * CONVEXMIRROR1 凸镜效果 CONVEXMIRROR2 凹镜效果 DEFORM 变形效果
	 * 
	 * @author Administrator
	 * 
	 */
	public enum DistortingMirrorEffects {
		CONVEXMIRROR1, CONVEXMIRROR2, DEFORM
	}

	public SpecicalEfficacy mEffectMode = SpecicalEfficacy.GENERAL;
	public DistortingMirrorEffects mDistortingMirror = DistortingMirrorEffects.CONVEXMIRROR1;
	private static final int UPDATE_RECORD_TIMER = 1;
	private Timer mRecordTimer;
	private RecordTimerTask mRecordTimerTask;
	private int mRecordTimeCount = 0;
	private TextView mRecoedTimeTV;
	private DMEffectsListAdapter mDmEffectsAdapter = null;

	private MyOrientationEventListener mOrientationListener = null;
	private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
	private boolean mHasEfficacyBeforShutter = false;

	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_RECORD_TIMER:
				boolean ongoing = (mCameraMode == CameraConfig.CAMERA_VIDEO_MODE);
				if (ongoing) {
					String timeStr = String.format(
							getResources().getString(R.string.timer_format),
							mRecordTimeCount / 3600,
							(mRecordTimeCount % 3600) / 60,
							mRecordTimeCount % 60);
					mRecordTimeCount++;
					if (mRecoedTimeTV != null) {
						mRecoedTimeTV.setText(timeStr);
					}
				}
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		mMediaRecordManager = MediaRecordManager.getInstance(this);
		mMediaRecordManager.setOnInfoListener(this);
		mMediaRecordManager.setOnErrorListener(this);
		mOrientationListener = new MyOrientationEventListener(this);
		if (!new File(CameraConfig.POTOS_FILES_PATH).exists()) {
			new File(CameraConfig.POTOS_FILES_PATH).mkdirs();
		}
		Configure.init(this);
		initViews();
		initPreView();
		initEfficacyViews();
		initEffectListView();
		hideSystemUI(this, true);
	}

	/**
	 * 隐藏菜单
	 * 
	 * @param context
	 * @param flag
	 */
	public static void hideSystemUI(Context context, boolean flag) {
		Intent intent = new Intent();
		intent.setAction("cn.worldchip.www.UPDATE_SYSTEMUI");
		intent.putExtra("hide_systemui", flag);
		context.sendBroadcast(intent);
	}

	private void initViews() {
		// TODO Auto-generated method stub
		// mMainView = findViewById(R.id.camera_main_layout);
		mRightToolViews = findViewById(R.id.right_tool_views);
		mToolImageViews[CAMERA_MODE_INDEX] = (ImageView) findViewById(R.id.camera_mode);
		mToolImageViews[GALLERY_INDEX] = (ImageView) findViewById(R.id.gallery_btn);
		mToolImageViews[SAVE_INDEX] = (ImageView) findViewById(R.id.save_btn);
		//mToolImageViews[SAVE_INDEX].setEnabled(false);
		mToolImageViews[SHUTTER_INDEX] = (ImageView) findViewById(R.id.shutter_btn);
		mToolImageViews[MAGIC_INDEX] = (ImageView) findViewById(R.id.magic_btn);

		mToolImageBgViews[CAMERA_MODE_INDEX] = (ImageView) findViewById(R.id.camera_mode_bg);
		//mToolImageBgViews[GALLERY_INDEX] = (ImageView) findViewById(R.id.gallery_bg);
		mToolImageBgViews[SAVE_INDEX] = (ImageView) findViewById(R.id.save_bg);
		mToolImageBgViews[SHUTTER_INDEX] = (ImageView) findViewById(R.id.shutter_bg);
		mToolImageBgViews[MAGIC_INDEX] = (ImageView) findViewById(R.id.magic_bg);

		mCameraConvBtn = (ImageView) findViewById(R.id.camera_converion);
		if (CameraManager.isDoubleCamera()) {
			mCameraConvBtn.setVisibility(View.VISIBLE);
		} else {
			mCameraConvBtn.setVisibility(View.GONE);
		}

		mBackBtn = (ImageView) findViewById(R.id.main_back_btn);
		mCenterShutterView = (ImageView) findViewById(R.id.iv_center_shutter);
		mSwitchCamera = (ImageView) findViewById(R.id.main_switch_camera_btn);

		mToolImageViews[CAMERA_MODE_INDEX].setOnClickListener(this);
		mToolImageViews[GALLERY_INDEX].setOnClickListener(this);
		mToolImageViews[SAVE_INDEX].setOnClickListener(this);
		mToolImageViews[SHUTTER_INDEX].setOnClickListener(this);
		mToolImageViews[MAGIC_INDEX].setOnClickListener(this);
		mCameraConvBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mSwitchCamera.setOnClickListener(this);
		updateCameraMode(mCameraMode);
		mEffectDrawView = (EffectsView) findViewById(R.id.camera_effect_view);
		// mEffectDrawView.setVisibility(View.INVISIBLE);
		updateEffectMode(SpecicalEfficacy.GENERAL);
		mRecoedTimeTV = (TextView) findViewById(R.id.video_record_timer);
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/COMIC.TTF");
		mRecoedTimeTV.setTypeface(typeFace);
	}

	private void initEfficacyViews() {

		mEfficacyRL = LayoutInflater.from(this).inflate(
				R.layout.special_efficacy_pop_layout, null);

		mEfficacyViews[GENERAL_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.general_effect_img);
		mEfficacyViews[DISTORTING_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.distorting_effect_btn);
		mEfficacyViews[INDIVIDUALITY_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.individuality_effect_btn);
		mEfficacyViews[GRAFFITI_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.graffiti_effect_btn);
		mEfficacyViews[ACCESSORIES_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.accessories_effect_btn);

		mEfficacyRL.findViewById(R.id.general_effect_btn).setOnClickListener(
				this);
		mEfficacyViews[DISTORTING_EFFECT_INDEX].setOnClickListener(this);
		mEfficacyViews[INDIVIDUALITY_EFFECT_INDEX].setOnClickListener(this);
		mEfficacyViews[GRAFFITI_EFFECT_INDEX].setOnClickListener(this);
		mEfficacyViews[GRAFFITI_EFFECT_INDEX].setEnabled(false);
		mEfficacyViews[ACCESSORIES_EFFECT_INDEX].setOnClickListener(this);

		mEfficacyBgViews[GENERAL_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.general_effect_bg);
		mEfficacyBgViews[DISTORTING_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.distorting_effect_bg);
		mEfficacyBgViews[INDIVIDUALITY_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.individuality_effect_bg);
		mEfficacyBgViews[GRAFFITI_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.graffiti_effect_bg);
		mEfficacyBgViews[ACCESSORIES_EFFECT_INDEX] = (ImageView) mEfficacyRL
				.findViewById(R.id.accessories_effect_bg);

	}

	private void initEffectListView() {

		mEffectListView = LayoutInflater.from(CameraActivity.this).inflate(
				R.layout.efficacy_list_layout, null);
		mDMEffectListView = LayoutInflater.from(CameraActivity.this).inflate(
				R.layout.dm_efficacy_list_layout, null);
		mEffectGridView = (GridView) mEffectListView
				.findViewById(R.id.effect_gridview);
		mEffectAdapter = new EffectListAdapter(this, null);

	}

	@SuppressLint("NewApi")
	private void initPreView() {
		mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
		mCameraPreview.setSurfaceTextureListener(mCameraPreview);
		mCameraPreview.setCallback(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		//SimpleAudioPlayer.getInstance(getApplication()).playEffect(
		//		SimpleAudioPlayer.CLICK_SOUND_KEY);
		switch (view.getId()) {
		case R.id.camera_mode:
			if (mCameraMode == CameraConfig.CAMERA_VIDEO_MODE) {
				if (mMediaRecordManager != null
						&& mMediaRecordManager.isRecording() || isEditState) {
					showDiscardDialog();
					break;
				} else {
					updateCameraMode(CameraConfig.CAMERA_PHOTO_MODE);
				}
			} else {
				if (isEditState) {
					showDiscardDialog();
					break;
				} else {
					clearAllEffect();
					updateCameraMode(CameraConfig.CAMERA_VIDEO_MODE);
				}
			}
			onCheckedButton(CAMERA_MODE_INDEX);
			if (mSpecialEfficacyPop != null && mSpecialEfficacyPop.isShowing()) {
				mSpecialEfficacyPop.dismiss();
			}
			break;

		case R.id.gallery_btn:
			//onCheckedButton(GALLERY_INDEX);
			if (mSpecialEfficacyPop != null && mSpecialEfficacyPop.isShowing()) {
				mSpecialEfficacyPop.dismiss();
			}
			try {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(
						"com.worldchip.bbpaw.album",
						"com.worldchip.bbpaw.album.activity.PhotosListActivity"));
				intent.putExtra("files_path", CameraConfig.SAMPLE_FILE_DIR);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(this, R.string.open_gallery_error_msg,
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.save_btn:
			mHasEfficacyBeforShutter = false;
			stopPreview();
			onCheckedButton(SAVE_INDEX);
			
			mToolImageViews[SAVE_INDEX].setVisibility(View.INVISIBLE);
			mToolImageBgViews[SAVE_INDEX].setVisibility(View.INVISIBLE);
			
			if (mSpecialEfficacyPop != null && mSpecialEfficacyPop.isShowing()) {
				mSpecialEfficacyPop.dismiss();
			}
			if (mCameraMode == CameraConfig.CAMERA_PHOTO_MODE) {
				if (mEffectDrawView != null) {
					mEffectDrawView.savePic();
				}
			} else {
				if (mMediaRecordManager != null) {
					mMediaRecordManager.onUpdateGallery();
				}
			}
			startPreview();
			break;

		case R.id.shutter_btn:
			isEditState = true;
			if (mEffectDrawView != null) {
				mHasEfficacyBeforShutter = mEffectDrawView.hasEffect();
			}
			onCheckedButton(SHUTTER_INDEX);
			if (mSpecialEfficacyPop != null && mSpecialEfficacyPop.isShowing()) {
				mSpecialEfficacyPop.dismiss();
			}
			if (mCameraMode == CameraConfig.CAMERA_PHOTO_MODE) {
				mToolImageViews[SAVE_INDEX].setVisibility(View.VISIBLE);
				mToolImageBgViews[SAVE_INDEX].setVisibility(View.VISIBLE);
				mToolImageViews[SHUTTER_INDEX].setEnabled(false);
				takePhoto();
			} else {
				if (mMediaRecordManager == null)
					return;
				if (mMediaRecordManager.isRecording()) {
					mMediaRecordManager.stopVideoRecording();
					
//					mToolImageViews[SAVE_INDEX].setVisibility(View.VISIBLE);
//					mToolImageBgViews[SAVE_INDEX].setVisibility(View.VISIBLE);
					
					//mToolImageViews[SAVE_INDEX].setEnabled(true);
					// stopPreview();
					//mToolImageViews[SHUTTER_INDEX].setEnabled(false);   //by  wuyong 
					//mToolImageViews[SAVE_INDEX].setEnabled(true);
					isEditState = false;
					mEfficacyViews[GRAFFITI_EFFECT_INDEX].setEnabled(true);
					stopRecordingLight();
				} else {
					showStorageHint();
					if (Utils.getAvailableSpace() < Utils.LOW_STORAGE) {
						LogUtil.v(TAG,
								"Storage issue, ignore the start request");
						return;
					}
					mToolImageViews[SHUTTER_INDEX].setEnabled(false);
					mMediaRecordManager.initializeRecorder();
					mMediaRecordManager.startVideoRecording();
					startReordingLight();
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mToolImageViews[SHUTTER_INDEX].setEnabled(true);
						}
					}, 1200);
				}
			}
			break;

		case R.id.magic_btn:
			if (mSpecialEfficacyPop != null && mSpecialEfficacyPop.isShowing()) {
				mSpecialEfficacyPop.dismiss();
				return;
			}
			onCheckedButton(MAGIC_INDEX);
			showSpeciallyEfficacyToolPop();
			break;
		case R.id.general_effect_btn:
			updateEffectMode(SpecicalEfficacy.GENERAL);
			updateEfficacyButton(GENERAL_EFFECT_INDEX);
			break;
		case R.id.distorting_effect_btn:
			updateEffectMode(SpecicalEfficacy.DISTORTING);
			updateEfficacyButton(DISTORTING_EFFECT_INDEX);
			showDMEffectsPop(view);
			break;
		case R.id.individuality_effect_btn:
			updateEffectMode(SpecicalEfficacy.INDIVIDUALITY);
			updateEfficacyButton(INDIVIDUALITY_EFFECT_INDEX);
			showEffectListPop(SpecicalEfficacy.INDIVIDUALITY);
			break;
		case R.id.graffiti_effect_btn:
			updateEffectMode(SpecicalEfficacy.GRAFFITI);
			updateEfficacyButton(GRAFFITI_EFFECT_INDEX);
			break;
		case R.id.accessories_effect_btn:
			updateEffectMode(SpecicalEfficacy.ACCESSORIES);
			updateEfficacyButton(ACCESSORIES_EFFECT_INDEX);
			showEffectListPop(SpecicalEfficacy.ACCESSORIES);
			break;

		case R.id.camera_converion:
			if (mCurrCameraFacing == CameraConfig.CAMERA_FACING_FRONT) {
				CameraManager.getInstance(CameraActivity.this).changeCamera(
						CameraActivity.this, CameraConfig.CAMERA_FACING_BACK);
				mCurrCameraFacing = CameraConfig.CAMERA_FACING_BACK;
			} else {
				CameraManager.getInstance(CameraActivity.this).changeCamera(
						CameraActivity.this, CameraConfig.CAMERA_FACING_FRONT);
				mCurrCameraFacing = CameraConfig.CAMERA_FACING_FRONT;
			}
			break;
		case R.id.main_back_btn:
			if (!isEditState) {
				if (mCameraMode == CameraConfig.CAMERA_VIDEO_MODE) {
					if (mMediaRecordManager != null) {
						if (mMediaRecordManager.isRecording()) {
							showDiscardDialog();
						} else {
							exitApp();
						}
					}
				} else {
					exitApp();
				}
			} else {
				showDiscardDialog();
			}

			break;
		case R.id.main_switch_camera_btn:
			//startDefaultCamera();
			hideSystemUI(this, false);
			startDefaultCamera(CameraActivity.this,"com.android.camera2","com.android.camera.CameraActivity");
			break;
		}
	}

	private void updateCameraMode(int mode) {

		if (mode == CameraConfig.CAMERA_PHOTO_MODE) {
			if (mToolImageViews[CAMERA_MODE_INDEX] != null) {
				mToolImageViews[CAMERA_MODE_INDEX]
						.setImageResource(R.drawable.ic_take_photo);
			}
			if (mToolImageViews[SHUTTER_INDEX] != null) {
				mToolImageViews[SHUTTER_INDEX]
						.setImageResource(R.drawable.ic_take_photo_btn);
			}
			mToolImageViews[MAGIC_INDEX].setEnabled(true);
		} else {
			updateEffectMode(SpecicalEfficacy.GENERAL);
			updateEfficacyButton(GENERAL_EFFECT_INDEX);
			if (mToolImageViews[CAMERA_MODE_INDEX] != null) {
				mToolImageViews[CAMERA_MODE_INDEX]
						.setImageResource(R.drawable.ic_record_video);
			}
			if (mToolImageViews[SHUTTER_INDEX] != null) {
				mToolImageViews[SHUTTER_INDEX]
						.setImageResource(R.drawable.ic_record_video_btn);
			}
			mToolImageViews[MAGIC_INDEX].setEnabled(false);
		}
		mCameraMode = mode;

	}

	private void takePhoto() {
		// TODO Auto-generated method stub
		CameraManager cameraManager = CameraManager
				.getInstance(CameraActivity.this);
		Camera camera = cameraManager.getCamera();
		int cameraId = cameraManager.getCameraId();
		if (cameraId == CameraConfig.CAMERA_FACING_FRONT) {
			int orientation = 0;
			if (mOrientation == 0) {
				orientation = mOrientation;
			} else if (mOrientation == 90) {
				orientation = mOrientation - 270;
			} else if (mOrientation == 180) {
				orientation = mOrientation + 180;
			} else if (mOrientation == 270) {
				orientation = mOrientation - 90;
			}
			int jpegRotation = CameraUtils.getJpegRotation(
					cameraManager.getCameraId(), orientation);
			if (camera != null) {
				Parameters parameters = camera.getParameters();
				parameters.setRotation(jpegRotation);
				camera.setParameters(parameters);
			}
		}

		if (camera != null) {
			camera.takePicture(null, null, null, new JpegPictureCallback());
		}
	}

	private final class JpegPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] jpegData,
				android.hardware.Camera camera) {
			if (mCenterShutterView != null) {
				mCenterShutterView.setVisibility(View.INVISIBLE);
			}
			isEditState = true;
			stopPreview();
			Bitmap finalPic = null;
			// Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0,
			// jpegData.length);
			Bitmap bitmap = ImageTool.creatFinalPictureTakenBitmap(jpegData);
			Log.e("lee", "onPictureTaken bitmap weight = " + bitmap.getWidth()
					+ " height == " + bitmap.getHeight());
			//mToolImageViews[SAVE_INDEX].setEnabled(true);
			if (mEffectMode == SpecicalEfficacy.DISTORTING) {// 哈哈镜模式
				if (mDistortingMirror == DistortingMirrorEffects.CONVEXMIRROR1) {
					finalPic = ImageTool.toHahajingConvex(bitmap,
							(int) (bitmap.getHeight() / 4));
				} else if (mDistortingMirror == DistortingMirrorEffects.CONVEXMIRROR2) {
					finalPic = ImageTool.toShrink(bitmap,
							bitmap.getWidth() / 2, bitmap.getHeight() / 2);

				} else if (mDistortingMirror == DistortingMirrorEffects.DEFORM) {
					finalPic = ImageTool.toWarp(bitmap,
							(float) (bitmap.getWidth() / 2),
							(float) (bitmap.getHeight() / 2));
				}
				if (mEffectDrawView != null) {
					mEffectDrawView.drawPicEffectBitmap(finalPic);
				}
			} else {// 其他模式
				finalPic = bitmap;
			}

			if (mEffectDrawView != null) {
				mEffectDrawView.setPicBitmap(finalPic);
			}
		}
	}

	/*
	 * @Override public void surfaceChanged(SurfaceHolder holder, int format,
	 * int w, int h) { // TODO Auto-generated method stub Log.e("lee",
	 * "surfaceChanged ------------- "); new Thread(new Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * CameraManager
	 * .getInstance(CameraActivity.this).doOpenCamera(CameraActivity.this,
	 * CameraConfig.DEFAULT_CAMERA_FACING); } }).start(); }
	 * 
	 * @Override public void surfaceCreated(SurfaceHolder arg0) { // TODO
	 * Auto-generated method stub }
	 * 
	 * @Override public void surfaceDestroyed(SurfaceHolder arg0) { // TODO
	 * Auto-generated method stub Log.e("lee",
	 * "surfaceDestroyed ------------- ");
	 * CameraManager.getInstance(CameraActivity.this).doStopCamera(); }
	 */
	@Override
	public void onCameraOpened(boolean isOpen) {
		// TODO Auto-generated method stub
		Log.e("lee", "onCameraOpened --isOpen ==  " + isOpen);
		if (isOpen) {
			CameraManager.getInstance(CameraActivity.this).doStartPreview(
					mCameraPreview, this);
		} else {
			showErrorHint(getResources().getString(
					R.string.camera_connect_error));
			finish();
		}
	}

	@Override
	public void onError(String exception) {
		// TODO Auto-generated method stub
		if (exception != null) {
			showErrorHint(exception);
		}
	}

	private void showErrorHint(final String errorMessage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(CameraActivity.this, errorMessage,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	private void onCheckedButton(int selectViewIndex) {
		if (mToolImageBgViews == null)
			return;

		for (int i = 0; i < mToolImageBgViews.length; i++) {
			if (i == GALLERY_INDEX) {
				continue;
			}
			if (selectViewIndex == i) {
				mToolImageBgViews[i]
						.setBackgroundResource(R.drawable.ic_select_high_light);
			} else {
				mToolImageBgViews[i].setBackgroundDrawable(null);
			}
		}
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		// TODO Auto-generated method stub

		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			if (mMediaRecordManager != null
					&& mMediaRecordManager.isRecording())
				mMediaRecordManager.stopVideoRecording();
		} else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
			if (mMediaRecordManager != null
					&& mMediaRecordManager.isRecording())
				mMediaRecordManager.stopVideoRecording();
			// Show the toast.
			Toast.makeText(CameraActivity.this,
					R.string.video_reach_size_limit, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		mMediaRecordManager.stopVideoRecording();
		showStorageHint();
	}

	private void showStorageHint() {
		long storageSpace = Utils.getAvailableSpace();
		String errorMessage = null;
		if (storageSpace == Utils.UNAVAILABLE) {
			errorMessage = getString(R.string.no_storage);
		} else if (storageSpace == Utils.PREPARING) {
			errorMessage = getString(R.string.preparing_sd);
		} else if (storageSpace == Utils.UNKNOWN_SIZE) {
			errorMessage = getString(R.string.access_sd_fail);
		} else if (storageSpace < Utils.LOW_STORAGE) {
			errorMessage = getString(R.string.spaceIsLow_content);
		}

		if (errorMessage != null) {
			Toast.makeText(CameraActivity.this, errorMessage,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void stopPreview() {
		Camera camera = CameraManager.getInstance(CameraActivity.this)
				.getCamera();
		if (camera != null) {
			camera.stopPreview();
			mToolImageViews[SHUTTER_INDEX].setEnabled(false);
			//mToolImageViews[SAVE_INDEX].setEnabled(true);
		}
		if (mCameraMode == CameraConfig.CAMERA_PHOTO_MODE) {
			mEfficacyViews[GRAFFITI_EFFECT_INDEX].setEnabled(true);
		}

		if (mCameraPreview != null) {
			mCameraPreview.setVisibility(View.GONE);
		}
	}

	private void startPreview() {
		Camera camera = CameraManager.getInstance(CameraActivity.this)
				.getCamera();
		if (camera != null) {
			camera.startPreview();
			mCenterShutterView.setVisibility(View.VISIBLE);
			mToolImageViews[SHUTTER_INDEX].setEnabled(true);
		}
		//mToolImageViews[SAVE_INDEX].setEnabled(false);
		mCenterShutterView.setImageResource(R.drawable.ic_center_shutter);
		isEditState = false;
		if (mCameraMode == CameraConfig.CAMERA_PHOTO_MODE) {
			mEfficacyViews[GRAFFITI_EFFECT_INDEX].setEnabled(false);
			if (mEffectDrawView != null) {
				mEffectDrawView.release();
			}
		}
		if (mCameraPreview != null) {
			mCameraPreview.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示特效工具
	 */
	private void showSpeciallyEfficacyToolPop() {
		if (null == mRightToolViews)
			return;
		if (null == mEfficacyRL)
			return;
		mEfficacyRL.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int popupWidth = mEfficacyRL.getMeasuredWidth();
		int popupHeight = mEfficacyRL.getMeasuredHeight();

		if (mSpecialEfficacyPop == null) {
			mSpecialEfficacyPop = new PopupWindow(mEfficacyRL, popupWidth,
					popupHeight);
		}

		int[] location = new int[2];
		mRightToolViews.getLocationOnScreen(location);
		if (mSpecialEfficacyPop.isShowing())
			mSpecialEfficacyPop.dismiss();

		// mSpecialEfficacyPop.showAtLocation(mRightToolViews,
		// Gravity.NO_GRAVITY, (location[0] - mRightToolViews.getWidth()),
		// location[1] + (mRightToolViews.getHeight() - popupHeight)/2);

		mSpecialEfficacyPop.showAtLocation(mRightToolViews, Gravity.TOP
				| Gravity.RIGHT, Configure.SCREEN_WIDTH(this) - location[0], 0);

	}

	/**
	 * 特效list popwindow
	 */
	private void showEffectListPop(SpecicalEfficacy effect) {

		if (null == mEffectListView)
			return;

		if (mDMEffectListPop != null && mDMEffectListPop.isShowing()) {
			mDMEffectListPop.dismiss();
			mDMEffectListPop = null;
		}
		if (mEffectListPop != null && mEffectListPop.isShowing()) {
			mEffectListPop.dismiss();
			mEffectListPop = null;
		}

		if (mEffectAdapter == null) {
			mEffectAdapter = new EffectListAdapter(CameraActivity.this, null);
		}
		int[] effectResIds = null;
		if (effect == SpecicalEfficacy.INDIVIDUALITY) {
			effectResIds = EffectData.EFFECT_BACKGROUND_THUMB;
		} else if (effect == SpecicalEfficacy.ACCESSORIES) {
			effectResIds = EffectData.EFFECT_PROPS;
		}

		ArrayList<Integer> effectList = new ArrayList<Integer>();

		for (int i = 0; i < effectResIds.length; i++) {
			effectList.add(effectResIds[i]);
		}
		mEffectAdapter.setData(effectList);
		mEffectAdapter.setSeclection(-1);
		mEffectGridView.setAdapter(mEffectAdapter);
		mEffectGridView
				.setOnItemClickListener(new EffectListOnItemClickListener());
		mEffectAdapter.notifyDataSetChanged();

		PopupWindow popupWindow = creatPopupWindow(mEffectListView);

		int[] location = new int[2];
		if (mEfficacyRL != null) {
			mEfficacyRL.getLocationOnScreen(location);
		}
		if (popupWindow != null) {
			popupWindow.showAtLocation(mRightToolViews, Gravity.TOP
					| Gravity.RIGHT,
					Configure.SCREEN_WIDTH(this) - location[0], 0);
		}

	}

	/**
	 * 显示哈哈镜特效pop
	 */
	private void showDMEffectsPop(View anchor) {

		if (null == mDMEffectListView)
			return;

		if (mEffectListPop != null && mEffectListPop.isShowing()) {
			mEffectListPop.dismiss();
			mEffectListPop = null;
		}

		if (mDMEffectListPop != null && mDMEffectListPop.isShowing()) {
			mDMEffectListPop.dismiss();
			mDMEffectListPop = null;
			mDmEffectsAdapter = null;
		}

		String[] dmEffects = getResources().getStringArray(
				R.array.distorting_mirror_effects_list);
		ArrayList<String> dmEffectList = new ArrayList<String>();

		for (int i = 0; i < dmEffects.length; i++) {
			dmEffectList.add(dmEffects[i]);
		}
		mDmEffectsAdapter = new DMEffectsListAdapter(CameraActivity.this,
				dmEffectList);
		GridView dmEffectGridView = (GridView) mDMEffectListView
				.findViewById(R.id.dm_effect_gridview);

		dmEffectGridView.setAdapter(mDmEffectsAdapter);
		dmEffectGridView
				.setOnItemClickListener(new EffectListOnItemClickListener());

		if (mDMEffectListPop == null) {
			mDMEffectListPop = new PopupWindow(mDMEffectListView,
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mDMEffectListPop.setOutsideTouchable(true);
			mDMEffectListPop.setBackgroundDrawable(new ColorDrawable(0));
		}

		int[] location = new int[2];
		if (mEfficacyRL != null) {
			mEfficacyRL.getLocationOnScreen(location);
		}
		if (mDMEffectListPop != null) {
			mDMEffectListPop.showAtLocation(mRightToolViews,
					Gravity.CENTER_VERTICAL | Gravity.RIGHT,
					Configure.SCREEN_WIDTH(this) - location[0], 0);
		}
	}

	private PopupWindow creatPopupWindow(View contentView) {
		if (contentView == null)
			return null;
		contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int popupWidth = contentView.getMeasuredWidth();
		int popupHeight = contentView.getMeasuredHeight();
		if (mEffectListPop == null) {
			mEffectListPop = new PopupWindow(CameraActivity.this);
			mEffectListPop.setWidth(popupWidth);
			mEffectListPop.setHeight(popupHeight);
			mEffectListPop.setOutsideTouchable(true);
			mEffectListPop.setBackgroundDrawable(new ColorDrawable(0));
		}
		mEffectListPop.setContentView(contentView);
		return mEffectListPop;
	}

	private void updateEfficacyButton(int selectViewIndex) {
		if (mEfficacyBgViews == null)
			return;

		for (int i = 0; i < mEfficacyBgViews.length; i++) {
			if (selectViewIndex == i) {
				mEfficacyBgViews[i]
						.setBackgroundResource(R.drawable.ic_select_high_light);
				if (mToolImageViews != null) {
					mToolImageViews[MAGIC_INDEX]
							.setImageDrawable(mEfficacyViews[i].getDrawable());
				}
			} else {
				mEfficacyBgViews[i].setBackgroundDrawable(null);
			}
		}
	}

	private void exitApp() {
		// showGoldResultView();
		TranslateAnimation transAnimation = new TranslateAnimation(0, 20, 0, 0);
		transAnimation.setDuration(100);
		transAnimation.setStartOffset(0);
		transAnimation.setFillAfter(true);
		transAnimation.setInterpolator(new LinearInterpolator());
		transAnimation.setRepeatCount(6);
		transAnimation.setRepeatMode(Animation.REVERSE);
		transAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				CameraActivity.this.finish();
			}
		});
		mBackBtn.startAnimation(transAnimation);
	}

	private void updateEffectMode(SpecicalEfficacy efficacy) {

		if (mEffectDrawView != null) {
			mEffectDrawView.setCurrEffectMode(efficacy);
		}
		if (efficacy == SpecicalEfficacy.GENERAL) {
			if (mEffectDrawView != null && !isEditState) {
				mEffectDrawView.clearAllEffectDraw();
			} else {
				if (!mHasEfficacyBeforShutter) {
					mEffectDrawView.clearAllEffectDraw();
				}
			}
		} else if (efficacy == SpecicalEfficacy.GRAFFITI) {
			if (mEffectDrawView != null && isEditState) {
				mEffectDrawView.setGraffitiEnabled(true);
			}
		} else if (efficacy == SpecicalEfficacy.ACCESSORIES) {
			if (mEffectDrawView != null) {
				mEffectDrawView.creatDrawCanvas();
			}
		} else {
			if (mEffectDrawView != null) {
				mEffectDrawView.setGraffitiEnabled(false);
			}
		}
		mEffectMode = efficacy;
	}

	private void discard() {
		if (mCameraMode == CameraConfig.CAMERA_VIDEO_MODE) {
			if (mMediaRecordManager != null) {
				mMediaRecordManager.discardRecording();
			}
		} else {
			// mFinalPic = null;
			clearAllEffect();
		}
		isEditState = false;
		
		mToolImageViews[SAVE_INDEX].setVisibility(View.INVISIBLE);
		mToolImageBgViews[SHUTTER_INDEX].setVisibility(View.INVISIBLE);
		
		startPreview();
	}

	private void clearAllEffect() {
		if (mEffectDrawView != null) {
			mEffectDrawView.clearAll();
		}
		mHasEfficacyBeforShutter = false;
	}

	class EffectListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			SimpleAudioPlayer.getInstance(getApplication()).playEffect(
					SimpleAudioPlayer.CLICK_SOUND_KEY);
			if (mEffectMode == SpecicalEfficacy.ACCESSORIES) {
				if (mEffectAdapter != null) {
					mEffectAdapter.setSeclection(position);
				}
				int res = EffectData.EFFECT_PROPS[position];
				if (mEffectDrawView != null) {
					mEffectDrawView.setAccessoriesBitmap(res);
				}
			} else if (mEffectMode == SpecicalEfficacy.INDIVIDUALITY) {
				if (mEffectAdapter != null) {
					mEffectAdapter.setSeclection(position);
				}
				int res = EffectData.EFFECT_BACKGROUND[position];
				if (mEffectDrawView != null) {
					mEffectDrawView.onDrawEffectImage(res);
				}
			} else if (mEffectMode == SpecicalEfficacy.DISTORTING) {
				if (mDmEffectsAdapter != null) {
					mDmEffectsAdapter.setSeclection(position);
				}
				mDistortingMirror = getDMEffectMode(position);
			}
		}
	}

	private DistortingMirrorEffects getDMEffectMode(int position) {
		if (position == 0) {
			return DistortingMirrorEffects.CONVEXMIRROR1;
		} else if (position == 1) {
			return DistortingMirrorEffects.CONVEXMIRROR2;
		} else if (position == 2) {
			return DistortingMirrorEffects.DEFORM;
		}

		return DistortingMirrorEffects.CONVEXMIRROR1;
	}

	private void showDiscardDialog() {
		// TODO Auto-generated method stub
		GlobalAlertDialog d = new GlobalAlertDialog.Builder(this)
				.setTitle(R.string.global_alert_dialog_title)
				.setCancelable(true)
				.setMessage(getResources().getString(R.string.discard_message))
				.setNegativeButton("", new DialogInterface.OnClickListener() {// no
							// button

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton("", new DialogInterface.OnClickListener() {// yes
							// button
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mCameraMode == CameraConfig.CAMERA_VIDEO_MODE
										&& mMediaRecordManager != null) {
									if (mMediaRecordManager.isRecording()) {
										mMediaRecordManager
												.stopVideoRecording();
										//mToolImageViews[SAVE_INDEX]
										//		.setEnabled(true);
										stopRecordingLight();
									}
								}
								discard();
							}
						}).create();
		d.show();
	}

	@Override
	public void onPreviewFrame(byte[] data) {

	}

	private AnimationDrawable mReordingLightAnim = null;

	private void startReordingLight() {
		ImageView recordingLight = (ImageView) findViewById(R.id.recording_light);
		if (recordingLight != null) {
			recordingLight.setVisibility(View.VISIBLE);
			mReordingLightAnim = (AnimationDrawable) recordingLight
					.getBackground();
			mReordingLightAnim.start();
		}
		if (mRecoedTimeTV != null) {
			mRecoedTimeTV.setVisibility(View.VISIBLE);
			startRecordTimer();
		}
	}

	private void stopRecordingLight() {
		ImageView recordingLight = (ImageView) findViewById(R.id.recording_light);
		if (recordingLight != null) {
			recordingLight.setVisibility(View.GONE);
		}
		if (mReordingLightAnim != null) {
			mReordingLightAnim.stop();
			mReordingLightAnim = null;
		}
		if (mRecoedTimeTV != null) {
			stopRecordTimer();
			mRecoedTimeTV.setVisibility(View.GONE);
			mRecordTimeCount = 0;
			mRecoedTimeTV.setText("");
		}
	}

	private void startRecordTimer() {
		if (mRecordTimer != null) {
			if (mRecordTimerTask != null) {
				mRecordTimerTask.cancel(); // 将原任务从队列中移除
			}
		} else {
			mRecordTimer = new Timer(true);
		}
		mRecordTimerTask = new RecordTimerTask(); // 新建一个任务
		mRecordTimer.schedule(mRecordTimerTask, 0, 1000);
	}

	private void stopRecordTimer() {
		if (mRecordTimer != null) {
			if (mRecordTimerTask != null) {
				mRecordTimerTask.cancel(); // 将原任务从队列中移除
			}
		}
	}

	public class RecordTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(UPDATE_RECORD_TIMER);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideSystemUI(this, true);
		
		if (isStartDefCamera) {
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isStartDefCamera = false;
		
		if (mOrientationListener != null) {
			mOrientationListener.enable();
		}
		CameraManager cameraManager = CameraManager.getInstance(this);
		if (cameraManager != null && !cameraManager.isOpened()) {
			cameraManager
					.doOpenCamera(this, CameraConfig.DEFAULT_CAMERA_FACING);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ScannerClient scannerClient = ScannerClient.getInstance();
		if (scannerClient != null) {
			MediaScannerConnection scannerConnection = scannerClient
					.getScannerConnection();
			if (scannerConnection != null && scannerConnection.isConnected()) {
				scannerConnection.disconnect();
				scannerClient.setConnected(false);
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mOrientationListener != null) {
			mOrientationListener.disable();
		}
		CameraManager.getInstance(this).doStopCamera();
	}

	private class MyOrientationEventListener extends OrientationEventListener {
		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
				return;
			mOrientation = CameraUtils.roundOrientation(orientation,
					mOrientation);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	private void startDefaultCamera(Context context,String packageName,String className) {
		if (TextUtils.isEmpty(packageName))
			return;
		
		if (mOrientationListener != null) {
			mOrientationListener.disable();
		}
		CameraManager.getInstance(this).doStopCamera();
		
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(packageName, className));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			isStartDefCamera = true;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String photoPathUrl = null;

	private void startDefaultCamera() {
		if (mOrientationListener != null) {
			mOrientationListener.disable();
		}
		CameraManager.getInstance(this).doStopCamera();
		/*
		 * Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		 * startActivityForResult(intent, 2000);
		 */

		Intent intentPhote = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentPhote.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		photoPathUrl = getOriginalPhotopath();
		File out = new File(photoPathUrl);
		Uri uri = Uri.fromFile(out);
		// 获取拍照后未压缩的原图片，并保存在uri路径中
		intentPhote.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intentPhote, 2000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
			// saveResultDataPhoto(data);
			// saveResultPhoto(getBitmapFromUrl(getOriginalPhotopath()));
			if (photoPathUrl != null) {
				Utils.updateGallery(CameraActivity.this, photoPathUrl);
			}
		}
		if (mOrientationListener != null) {
			mOrientationListener.enable();
		}
		CameraManager cameraManager = CameraManager.getInstance(this);
		if (cameraManager != null && !cameraManager.isOpened()) {
			cameraManager.doOpenCamera(this, CameraConfig.DEFAULT_CAMERA_FACING);
		}
	}

	private Bitmap getBitmapFromUrl(String url) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		// 防止OOM发生
		options.inJustDecodeBounds = false;
		return bitmap;
	}

	private String getOriginalPhotopath() {
		File picFile = new File(CameraConfig.POTOS_FILES_PATH);
		if (!picFile.exists()) {
			picFile.mkdirs();
		}
		return Utils.createPicOuputFile(CameraActivity.this).toString();
		// return CameraConfig.POTOS_FILES_PATH + "originalPhoto.jpg";
	}

	private void saveResultPhoto(Bitmap bitmap) {
		if (bitmap != null) {
			Log.e("xiaolp", "Width = " + bitmap.getWidth() + "    Height = "
					+ bitmap.getHeight());
			File picOuputFile = Utils.createPicOuputFile(CameraActivity.this);
			FileOutputStream out = null;
			try {
				Log.e("xiaolp",
						"picOuputFile.getPath() = " + picOuputFile.getPath());
				out = new FileOutputStream(picOuputFile.getPath());
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				Utils.updateGallery(CameraActivity.this, picOuputFile.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveResultDataPhoto(Intent data) {
		Bundle bundle = data.getExtras();
		Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
		if (bitmap != null) {
			Log.e("xiaolp", "width = " + bitmap.getWidth() + "  height = "
					+ bitmap.getHeight());
			File picOuputFile = Utils.createPicOuputFile(CameraActivity.this);
			FileOutputStream out = null;
			try {
				Log.e("xiaolp",
						"picOuputFile.getPath() = " + picOuputFile.getPath());
				out = new FileOutputStream(picOuputFile.getPath());
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				Utils.updateGallery(CameraActivity.this, picOuputFile.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
