package com.worldchip.bbp.bbpawmanager.cn.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.callbak.PasswordValidateListener;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;

public class PasswordInputDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context mContext = null;
	private static PasswordInputDialog passwordInputDialog = null;
	private static final int PASSWORD_POINT_COUNT = 6;
	private static final int PASSWORD_KEY_COUNT = 10;
	private ImageView[] mPasswordPoint = null;
	private View[] mPswKeys = null;
	private int mPawHaveInputCount = 0;
	private boolean mInputEnable = true;
	private StringBuffer mPswInputSB = new StringBuffer();
	private static final int REST_INPUT = 100;
	private static final String TAG = "--PasswordInputDialog--";
	private PasswordValidateListener mListener = null;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REST_INPUT:
				mPawHaveInputCount = 0;
				updatePwsPoint();
				mInputEnable = true;
				break;

			default:
				break;
			}
		}

	};

	public PasswordInputDialog(Context context) {
		super(context);
		mContext = context;
	}

	public PasswordInputDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public static PasswordInputDialog createDialog(Context context) {
		passwordInputDialog = new PasswordInputDialog(context,
				R.style.password_dialog_style);
		passwordInputDialog.setContentView(R.layout.enter_password_layout);
		passwordInputDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		passwordInputDialog.setCancelable(false);

		return passwordInputDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		initView();
	}

	private void initView() {
		if (passwordInputDialog == null) {
			return;
		}
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.password_key_00:
		case R.id.password_key_01:
		case R.id.password_key_02:
		case R.id.password_key_03:
		case R.id.password_key_04:
		case R.id.password_key_05:
		case R.id.password_key_06:
		case R.id.password_key_07:
		case R.id.password_key_08:
		case R.id.password_key_09:
			if (mInputEnable) {
				increaseInput((Integer) view.getTag());
			}
			break;
		case R.id.password_cancel_btn:
			dismissDialog(false);
			break;
		case R.id.modify_password_tv:
			try {
				Intent modifyPswIntent = new Intent();
				modifyPswIntent.setComponent(new ComponentName(
						"com.worldchip.bbp.ect",
						"com.worldchip.bbp.ect.activity.PassLockActivity"));
				modifyPswIntent.putExtra("isChangePsw", true);
				mContext.startActivity(modifyPswIntent);
				dismissDialog(false);
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.start_app_error,
						Toast.LENGTH_SHORT).show();
				Log.e(TAG, e.getStackTrace().toString());
			}
			break;
		}
	}

	private synchronized void increaseInput(Integer num) {
		if (num == null)
			return;
		mInputEnable = false;
		mPawHaveInputCount = mPawHaveInputCount + 1;
		if (mPswInputSB != null) {
			mPswInputSB.append(num);
		}
		updatePwsPoint();
	}

	private void updatePwsPoint() {
		if (mPasswordPoint != null) {
			for (int i = 0; i < mPasswordPoint.length; i++) {
				if (i < mPawHaveInputCount) {
					mPasswordPoint[i]
							.setImageResource(R.drawable.ic_password_input);
				} else {
					mPasswordPoint[i]
							.setImageResource(R.drawable.ic_password_normal);
				}
			}
		}
		validatePassword();
	}

	@SuppressLint("NewApi")
	private void validatePassword() {
		if (mPawHaveInputCount == PASSWORD_POINT_COUNT) {
			String pwd = Common.getUserPassword(mContext);
			Log.e(TAG, "validatePassword..pwd=" + pwd);

			if (pwd == null || pwd.equals("")) {
				pwd = "000000";
			}
			if (mPswInputSB != null && mPswInputSB.toString().equals(pwd)) {
				dismissDialog(true);
				mInputEnable = true;
			} else {
				mPswInputSB.delete(0, mPswInputSB.length());
				Toast.makeText(mContext, R.string.input_psw_error,
						Toast.LENGTH_SHORT).show();
				mHandler.sendEmptyMessageDelayed(REST_INPUT, 500);
			}
		} else {
			mInputEnable = true;
		}
	}

	private void dismissDialog(boolean success) {
		if (passwordInputDialog != null) {
			passwordInputDialog.dismiss();
			passwordInputDialog = null;
		}
		if (mListener != null) {
			mListener.onValidateComplete(success);
		}
	}

	public void setListener(PasswordValidateListener listener) {
		mListener = listener;
	}
}