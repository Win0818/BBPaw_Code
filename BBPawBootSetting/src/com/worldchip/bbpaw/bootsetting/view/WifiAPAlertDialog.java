package com.worldchip.bbpaw.bootsetting.view;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.util.Common;

public class WifiAPAlertDialog extends Dialog implements TextWatcher {
	private boolean mBlockKeycodeBack = false;
	private View mContentView;
	private Button mEnterBtn;
	private Button mCancelBtn;
	private EditText mPasswordEdit;
	private Context mContext;
	public ScanResult mScanResult = null;
	private TextView mTextView;

	public WifiAPAlertDialog(Context context, ScanResult scanResult) {
		super(context, R.style.dialog_global);
		mBlockKeycodeBack = false;
		mScanResult = scanResult;
		mContext = context;
		init();
	}

	public void init() {
		mContentView = LayoutInflater.from(mContext).inflate(
				R.layout.wifi_psw_input_layout, null);
		setContentView(mContentView);
		mEnterBtn = (Button) mContentView.findViewById(R.id.btn_enter);
		mCancelBtn = (Button) mContentView.findViewById(R.id.btn_cancel);
		mPasswordEdit = (EditText) mContentView.findViewById(R.id.et_psd);
		mTextView = (TextView) findViewById(R.id.wifi_psw_text);
		mTextView.setText(mContext.getResources().getString(R.string.input_password_title_text, mScanResult.SSID));
		mContentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (mPasswordEdit != null) {
					Common.hideKeyboard(mContext, mPasswordEdit);
				}
				return false;
			}
		});
		mPasswordEdit.addTextChangedListener(this);
		validate();
	}

	public void setOnclickListener(android.view.View.OnClickListener listener) {
		if (listener != null) {
			mEnterBtn.setOnClickListener(listener);
			mCancelBtn.setOnClickListener(listener);
		}
	}
	
	public String getPassword() {
		if (mPasswordEdit != null) {
			return mPasswordEdit.getText().toString();
		}
		return null;
	}
	
	public WifiAPAlertDialog(Context context) {
		super(context);
	}

	public void setBlockKeyCodeBack(boolean key) {
		mBlockKeycodeBack = key;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mBlockKeycodeBack && (keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private boolean mBlockKeyBack = false;
		public ScanResult result = null;

		public Builder(Context context, ScanResult scanResult) {
			this.context = context;
			mBlockKeyBack = false;
			result = scanResult;
		}

		/**
		 * Create the custom dialog
		 * 
		 * @return
		 */
		public WifiAPAlertDialog create() {
			return create(result);
		}

		/**
		 * Create the custom dialog
		 * 
		 * @param style
		 *            皮肤风格
		 * @param conentView
		 *            布局文件
		 * @return
		 */
		public WifiAPAlertDialog create(ScanResult result) {
			// instantiate the dialog with the custom Theme
			final WifiAPAlertDialog dialog = new WifiAPAlertDialog(context,
					result);
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams layoutParams = dialogWindow
					.getAttributes();
			// 修改窗体宽高
			layoutParams.width = LayoutParams.MATCH_PARENT;
			layoutParams.height = LayoutParams.MATCH_PARENT;
			layoutParams.format = PixelFormat.TRANSLUCENT;
			dialogWindow.setAttributes(layoutParams);
			if (mBlockKeyBack) {
				dialog.setBlockKeyCodeBack(mBlockKeyBack);
			}
			return dialog;
		}
	}


	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		validate();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	private void validate() {
		if (mPasswordEdit == null) {
			return;
		}
		if (mPasswordEdit.length() < 8) {
			if (mEnterBtn != null) {
				mEnterBtn.setEnabled(false);
			}
		} else {
			if (mEnterBtn != null) {
				mEnterBtn.setEnabled(true);
			}
		}
	}

}
