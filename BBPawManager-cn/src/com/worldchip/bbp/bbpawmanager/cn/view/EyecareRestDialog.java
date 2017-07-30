package com.worldchip.bbp.bbpawmanager.cn.view;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.PasswordValidateListener;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.ScaleAnimEffect;
import com.worldchip.bbp.bbpawmanager.cn.utils.SoundPlayer;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class EyecareRestDialog extends Dialog implements android.view.View.OnClickListener{

	private static EyecareRestDialog mEyecareRestDialog = null;
	private int[] mDrawableRes = null;
	private ImageView mSensorDrawableAnimView = null;
	private ImageView mUnlockBtn = null;
	private BBPawDrawableAnim mDrawableAnim = null;
	private long mDismissDialogDelay = -1;
	private static final int DISMISS_DIALOG = 101;
	private SoundPlayer mSoundPlayer;
	private ScaleAnimEffect mAnimEffect = new ScaleAnimEffect();
	private static float big_x = 1.08F;
	private static float big_y = 1.08F;
	private boolean mGotoUnlockEnable = true;
	private PasswordInputDialog mPasswordInputDialog = null;
	private boolean isShowing = false;
	private static final int FLAG_HOMEKEY_DISPATCHED = 100001; //拦截 home按键
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//msg.what
			switch (msg.what) {
			case DISMISS_DIALOG:
				dismissEyecareDialogAndCreateNextAlarm();
				break;

			default:
				break;
			}
		}
	};

	public EyecareRestDialog(Context context) {
		super(context);
	}

	public EyecareRestDialog(Context context, int theme) {
		super(context, theme);
	}

	public static EyecareRestDialog createDialog(Context context) {
		Log.e("lee", "createDialog ---------");
		mEyecareRestDialog = new EyecareRestDialog(context,
				R.style.eyecare_rest_dialog_style);
		mEyecareRestDialog.setContentView(R.layout.eyecare_rest_dialog);
		Window dialogWindow = mEyecareRestDialog.getWindow();
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialogWindow.setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		mEyecareRestDialog.setCancelable(false);
		return mEyecareRestDialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/*
		 * if (keyCode == KeyEvent.KEYCODE_BACK) { dismissEyecareDialog();
		 * return true; }
		 */
		Log.e("lee", "onKeyDown ---------");
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		Log.e("lee", "onAttachedToWindow ---------");
		//getWindow().addFlags(0x80000000);
		super.onAttachedToWindow();
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e("lee", "dispatchKeyEvent ---------");
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (!isShowing) {
			isShowing = true;
			initView();
		}
	}

	private void initView() {
		if (mEyecareRestDialog == null) {
			return;
		}
		mSensorDrawableAnimView = (ImageView) mEyecareRestDialog
				.findViewById(R.id.anim_view);
		mUnlockBtn = (ImageView) mEyecareRestDialog
				.findViewById(R.id.unlock_btn);
		mUnlockBtn.setOnClickListener(this);
		playSound();
		startDrawableAnim(mSensorDrawableAnimView);
	}

	public void setSensorDrawableAnim(int[] res) {
		mDrawableRes = res;
	}

	private void startDrawableAnim(ImageView drawableAnimView) {
		if (drawableAnimView != null) {
			if (mDrawableAnim != null) {
				mDrawableAnim.stopAnimation();
			}
			mDrawableAnim = new BBPawDrawableAnim(drawableAnimView);
			if (mDrawableRes != null) {
				mDrawableAnim.setDrawableAnimResource(mDrawableRes);
			} else {
				mDrawableAnim.setDrawableAnimResource(null);
			}
			mDrawableAnim.setDrawableAnimFrameDuration(Configure.EYECARE_DRAWABLE_ANIM_DURATION);
			mDrawableAnim.startAnimation();
			if (mDismissDialogDelay > 0) {
				mHandler.sendEmptyMessageDelayed(DISMISS_DIALOG, mDismissDialogDelay);
			}
		}
	}

	private void stopDrawableAnim() {
		if (mDrawableAnim != null) {
			mDrawableAnim.stopAnimation();
			mDrawableAnim = null;
		}
	}

	private void playSound() {
		if (mSoundPlayer == null) {
			mSoundPlayer = new SoundPlayer();
		}
		mSoundPlayer.play(MyApplication.getAppContext(), R.raw.time, null);
	}
	
	private void stopSound() {
		if (mSoundPlayer != null) {
			mSoundPlayer.stop();
		}
	}
	
	public void showEyecareDialog() {
		if (mEyecareRestDialog != null) {
			sendStopSensorSer(false);
			mEyecareRestDialog.show();
		}
	}

	public void dismissEyecareDialogAndCreateNextAlarm() {
		dismissEyecareDialog();
		Common.handleCreatEyecareAlarm(MyApplication.getAppContext());
	}
	public void dismissEyecareDialog() {
		mHandler.removeMessages(DISMISS_DIALOG);
		stopSound();
		stopDrawableAnim();
		if (mEyecareRestDialog != null && mEyecareRestDialog.isShowing()) {
			mEyecareRestDialog.dismiss();
			sendStopSensorSer(true);
			mEyecareRestDialog = null;
		}
	}

	public void setDismissDialogDelay(long seconds) {
		Log.e("lee", "setDismissDialogDelay == "+seconds);
		mDismissDialogDelay = seconds;
	}
	
	public static void sendStopSensorSer(boolean flag) {
		Intent intent = new Intent();
		intent.setAction("com.worldchip.bbp.sensor.SWITCH");
		intent.putExtra("switch", flag);
		MyApplication.getAppContext().sendBroadcast(intent);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		isShowing = false;
		mGotoUnlockEnable = true;
		dismissPasswordInputDialog();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.unlock_btn:
			if (mGotoUnlockEnable) {
				mGotoUnlockEnable = false;
				goToUnlock(view);
			}
			break;

		default:
			break;
		}
	}

	private void goToUnlock(View view) {
		if (view != null) {
			mAnimEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 200);
			Animation anim = mAnimEffect.createAnimation();
			anim.setFillBefore(true);
			anim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {}
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					showPasswordInputView();
				}
			});
			view.startAnimation(anim);
		}
		
	}
	
	
	private void showPasswordInputView() {
		dismissPasswordInputDialog();
		mPasswordInputDialog = PasswordInputDialog.createDialog(getContext());
		Window dialogWindow = mPasswordInputDialog.getWindow();
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		View view = dialogWindow.findViewById(R.id.modify_password_tv);
		if (view != null) {
			view.setVisibility(View.GONE);
		}
		mPasswordInputDialog.setListener(new PasswordValidateListener() {
			@Override
			public void onValidateComplete(boolean success) {
				// TODO Auto-generated method stub
				mGotoUnlockEnable = true;
				if (success) {
					if(Common.validationCurrTime2UseTime()) {
						dismissEyecareDialogAndCreateNextAlarm();
					} else {
						Common.saveEyecareSettingsPreferences(Utils.EYECARE_SCREEN_TIME_PRE_KEY, "false");
						dismissEyecareDialog();
					}
				}
			}
		});
		mPasswordInputDialog.show();
	}
	
	private void dismissPasswordInputDialog() {
		if (mPasswordInputDialog != null) {
			if (mPasswordInputDialog.isShowing()) {
				mPasswordInputDialog.dismiss();
			}
			mPasswordInputDialog = null;
		}
	}
}