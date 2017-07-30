package com.worldchip.bbp.ect.activity;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.GridViewAdapter;
import com.worldchip.bbp.ect.service.TimeTontrolService;
import com.worldchip.bbp.ect.util.Configure;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author WGQ
 */
public class PassLockActivity extends Activity implements OnClickListener {
	private static final String TAG = "--PassLockActivity--";
	private EditText mEditPassword;
	// ������
	private GridView mPasslockCounter;
	private GridViewAdapter adapter;
	private TextView mPasslockConfirm, mPasslockForgot, mPasslockCancel;
	private String editValue = "";

	private RelativeLayout mPopupWindow;
	private PopupWindow mForgetPasswordPopupWindow;
	private EditText mChangePassword, mDefaultPassword, mRetypePassword;
	// �ԺŰ�ť
	private TextView mConfirm;

	private TextView mPasslockBack;
	private ImageView mPasslockClose;
	private boolean flag = false;
	
	// ʱ��
	private LinearLayout mPasslockTimeLayout;
	private TextView mPasslockTime, mPasslockTimeSubtract,
			mPasslockTimeSetting, mPasslockTimeAdd, mPasslockTimeYes;
	private int time = 10;
	private Boolean isShowTimeSetting = false;
	// a set of
	private LinearLayout mPasslockSettingTime;
	private ImageButton imgbtnSettingConfirm;
	private ImageButton imgbtnSettingCancel;
	private LinearLayout mMainLayout;
	private boolean isChangePsw = false; 
	
	Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passlock_main);

		HttpCommon.hideSystemUI(PassLockActivity.this, true);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			flag = bundle.getBoolean("flag");
			isShowTimeSetting = bundle.getBoolean("setup");
			isChangePsw = bundle.getBoolean("isChangePsw",false);
		}
		// �ؼ���ʼ��
		initView();
		// ��ݳ�ʼ��
		initData();
	}

	/**
	 * �ؼ���ʼ��
	 */
	private void initView() {
		mMainLayout = (LinearLayout)findViewById(R.id.modify_box_layout);
		// ����
		mPasslockBack = (TextView) findViewById(R.id.passlock_back);
		mPasslockBack.setOnClickListener(this);
		mEditPassword = (EditText) findViewById(R.id.edit_password);
		mEditPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mEditPassword.setCursorVisible(true);
			}
		});
		mEditPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String passwoldEditView = mEditPassword.getText().toString()
						.trim();
				if (passwoldEditView.length() > 6) {
					mEditPassword.setText(passwoldEditView.substring(0, 6));
					mEditPassword.setSelection(6);
				}
			}
		});

		// ����
		mPasslockForgot = (TextView) findViewById(R.id.passlock_forgot);
		mPasslockForgot.setOnClickListener(this);
		// cancel
		mPasslockCancel = (TextView) findViewById(R.id.passlock_cancer);
		mPasslockCancel.setOnClickListener(this);

		// ����ʱ���ȷ��ȡ��ť
		mPasslockSettingTime = (LinearLayout) findViewById(R.id.ll_setting_time);
		imgbtnSettingCancel = (ImageButton) findViewById(R.id.imgbtn_setting_time_cancel);
		imgbtnSettingConfirm = (ImageButton) findViewById(R.id.imgbtn_setting_time_confirm);
		imgbtnSettingCancel.setOnClickListener(this);
		imgbtnSettingConfirm.setOnClickListener(this);
		// �ύ
		mPasslockConfirm = (TextView) findViewById(R.id.passlock_confirm);
		mPasslockConfirm.setOnClickListener(this);

		// ʱ��
		mPasslockTimeLayout = (LinearLayout) findViewById(R.id.passlock_time_layout);
		mPasslockTime = (TextView) findViewById(R.id.passlock_time);
		mPasslockTimeSubtract = (TextView) findViewById(R.id.passlock_time_subtract);
		mPasslockTimeSetting = (TextView) findViewById(R.id.passlock_time_setting);
		mPasslockTimeSetting.setText("10");
		mPasslockTimeAdd = (TextView) findViewById(R.id.passlock_time_add);
		mPasslockTimeYes = (TextView) findViewById(R.id.passlock_time_yes);
		mPasslockTime.setOnClickListener(this);
		mPasslockTimeSubtract.setOnClickListener(this);
		mPasslockTimeAdd.setOnClickListener(this);
		mPasslockTimeYes.setOnClickListener(this);

		// С������Ϸʱ�䵽��
		if (!flag) {
			mPasslockTime.setVisibility(View.VISIBLE);
			mPasslockBack.setVisibility(View.GONE);
		}

		// ������
		mPasslockCounter = (GridView) findViewById(R.id.passlock_counter);
		adapter = new GridViewAdapter(PassLockActivity.this);
		// �������ļ���
		mPasslockCounter.setOnItemClickListener(itemClickListener);
		if (isShowTimeSetting) {
			mPasslockCancel.setVisibility(View.VISIBLE);
			mPasslockForgot.setVisibility(View.GONE);
			mPasslockTime.setVisibility(View.GONE);
		}
	}

	/**
	 * ��ݳ�ʼ��
	 */
	private void initData() {
		mPasslockCounter.setAdapter(adapter);
	}

	
	/**
	 * �������ļ��� ���� ���ù�
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int old_length = editValue.length();
			if (position == 9) {
				editValue = "";
			} else if (position == 11) {
				if (old_length > 0) {
					editValue = editValue.substring(0, old_length - 1);
				}
			} else {
				editValue = editValue + HttpCommon.number[position];
			}
			mEditPassword.setText(editValue);
			int new_length = editValue.length();
			if (new_length > 0) {
				mEditPassword.setSelection(new_length);
			}
		}
	};

	/**
	 * �������
	 */
	private void ShowForgetPasswordPopupWindow() {
		mPopupWindow = (RelativeLayout) LayoutInflater.from(
				PassLockActivity.this).inflate(
				R.layout.patriarch_control_password_popupwindow, null);
		mForgetPasswordPopupWindow = new PopupWindow(PassLockActivity.this);

		mForgetPasswordPopupWindow.setWidth(585);
		mForgetPasswordPopupWindow.setHeight(545);
		mForgetPasswordPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mForgetPasswordPopupWindow.setFocusable(true);
		mForgetPasswordPopupWindow.setOutsideTouchable(false);
		mForgetPasswordPopupWindow.setContentView(mPopupWindow);
		mForgetPasswordPopupWindow.showAtLocation(
				findViewById(R.id.passlock_main), Gravity.CENTER, 0, -25);// ��ʾ��λ��Ϊ:����ڸ��ؼ�����
		// �ؼ���ʼ��
		mDefaultPassword = (EditText) mPopupWindow
				.findViewById(R.id.default_password);
		mChangePassword = (EditText) mPopupWindow
				.findViewById(R.id.change_password);
		mRetypePassword = (EditText) mPopupWindow
				.findViewById(R.id.retype_password);
		mPasslockClose = (ImageView) mPopupWindow
				.findViewById(R.id.passlock_close);

		mDefaultPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String mDefaultPasswordValue = mDefaultPassword.getText()
						.toString().trim();
				if (mDefaultPasswordValue.length() > 6) {
					mDefaultPassword.setText(mDefaultPasswordValue.substring(0,
							6));
					mDefaultPassword.setSelection(6);
				}
			}
		});

		mChangePassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String mChangePasswordValue = mChangePassword.getText()
						.toString().trim();
				if (mChangePasswordValue.length() > 6) {
					mChangePassword.setText(mChangePasswordValue
							.substring(0, 6));
					mChangePassword.setSelection(6);
				}
			}
		});

		mRetypePassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String mRetypePasswordValue = mRetypePassword.getText()
						.toString().trim();
				if (mRetypePasswordValue.length() > 6) {
					mRetypePassword.setText(mRetypePasswordValue
							.substring(0, 6));
					mRetypePassword.setSelection(6);
				}
			}
		});

		// �ԺŰ�ť
		mConfirm = (TextView) mPopupWindow.findViewById(R.id.confirm);
		mConfirm.setOnClickListener(this);

		mPasslockClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mForgetPasswordPopupWindow != null) {
					mForgetPasswordPopupWindow.dismiss();
					mForgetPasswordPopupWindow = null;
				}
			}
		});

		// �ر�
		mForgetPasswordPopupWindow
				.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						if (mForgetPasswordPopupWindow != null) {
							mForgetPasswordPopupWindow.dismiss();
							mForgetPasswordPopupWindow = null;
						}
						if (isChangePsw) {
							finish();
						}
						
					}
				});
	}

	@Override
	public void onClick(View view) {
		SharedPreferences password = getSharedPreferences("password_info", 1);
		switch (view.getId()) {
		case R.id.passlock_confirm:
			String passwoldValue = password.getString("password", "000000");
			String passwoldEditView = mEditPassword.getText().toString().trim();
			if (passwoldEditView.length() == 6) {
				if (passwoldEditView.equals(passwoldValue)) {
					if (!isShowTimeSetting) {
						SharedPreferences preferences = getSharedPreferences(
								"time_info", 0);
						int restart = preferences.getInt("restart", 0);

						if (restart == 1) {

							Intent intent = new Intent();
							intent.setClass(PassLockActivity.this,
									MainActivity.class);
							startActivity(intent);

							preferences.edit().putInt("restart", 0).commit();
						} else {
							Intent intent = new Intent();
							intent.setClass(PassLockActivity.this,
									PatriarchControlActivity.class);
							startActivity(intent);
						}
						if (!flag) {
							TimeTontrolService service = new TimeTontrolService();
							service.onStartTimeQuantum(false,
									PassLockActivity.this);
							service.onStartTime(true, PassLockActivity.this);
						}
						finish();
					} else {
						mPasslockTimeLayout.setVisibility(View.VISIBLE);
						mPasslockSettingTime.setVisibility(View.VISIBLE);
						mPasslockTimeYes.setVisibility(View.GONE);
					}
				} else {
					Toast.makeText(PassLockActivity.this,R.string.code_input_agen, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(PassLockActivity.this,R.string.input_six_code, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.passlock_forgot:// �������
			ShowForgetPasswordPopupWindow();
			break;
		case R.id.passlock_cancer:// cancel
			finish();
			// mPasslockTimeLayout.setVisibility(View.INVISIBLE);
			// mPasslockForgot.setVisibility(View.VISIBLE);
			// mPasslockCancel.setVisibility(View.GONE);
			isShowTimeSetting = false;

			break;
		case R.id.imgbtn_setting_time_cancel:// cancel
			finish();
			// mPasslockTimeLayout.setVisibility(View.INVISIBLE);
			// mPasslockForgot.setVisibility(View.VISIBLE);
			// mPasslockCancel.setVisibility(View.GONE);
			// mPasslockSettingTime.setVisibility(View.GONE);
			// mPasslockTimeYes.setVisibility(View.VISIBLE);
			isShowTimeSetting = false;
			break;
		case R.id.imgbtn_setting_time_confirm:// confirm

			ConfirmSettingTime();
			if (!flag) {
				TimeTontrolService service = new TimeTontrolService();
				service.onStartTimeQuantum(false, PassLockActivity.this);
				service.onStartTime(true, PassLockActivity.this);
			}
			isShowTimeSetting = false;
			finish();
			// mPasslockTimeLayout.setVisibility(View.INVISIBLE);
			// mPasslockForgot.setVisibility(View.VISIBLE);
			// mPasslockCancel.setVisibility(View.GONE);
			// mPasslockSettingTime.setVisibility(View.GONE);
			// mPasslockTimeYes.setVisibility(View.VISIBLE);
			break;

		case R.id.confirm: // ���뱣��
			String dfaultPasswordValue = mDefaultPassword.getText().toString()
					.trim();
			String changePasswordValue = mChangePassword.getText().toString()
					.trim();
			String retypePasswordValue = mRetypePassword.getText().toString()
					.trim();
			if (dfaultPasswordValue != null && !TextUtils.isEmpty(dfaultPasswordValue)) {
				if (dfaultPasswordValue.equalsIgnoreCase(Configure.ECT_REST_CODE)) {
					if (changePasswordValue.length() == 6) {
						if (retypePasswordValue.length() == 6) {
							if (retypePasswordValue.equals(changePasswordValue)) {
								password.edit().putString("password", retypePasswordValue).commit();
								Toast.makeText(PassLockActivity.this,R.string.update_password_suceecss, Toast.LENGTH_SHORT).show();
								if (mForgetPasswordPopupWindow != null) {
									mForgetPasswordPopupWindow.dismiss();
									mForgetPasswordPopupWindow = null;
								}
							} else {
								Toast.makeText(PassLockActivity.this,R.string.login_psw_error, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(PassLockActivity.this,R.string.input_six_code, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(PassLockActivity.this,R.string.input_six_code, Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(PassLockActivity.this,R.string.reset_code_error, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(PassLockActivity.this,R.string.reset_code_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.passlock_back:
			if (!isShowTimeSetting) {
				Intent intent = new Intent(PassLockActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
			finish();
			break;
		case R.id.passlock_time:
			mPasslockTimeSetting.setText("" + time);
			mPasslockTimeLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.passlock_time_subtract:
			String subtractOldTime = mPasslockTimeSetting.getText().toString();
			if (TextUtils.isEmpty(subtractOldTime)) {
				subtractOldTime = "0";
			}
			int subtractTimeValue = Integer.parseInt(subtractOldTime);
			if (subtractTimeValue > 10) {
				time = time - 10;
			} else {
				time = 10;
			}
			mPasslockTimeSetting.setText("" + time);
			break;
		case R.id.passlock_time_add:
			String addOldTime = mPasslockTimeSetting.getText().toString();
			if (TextUtils.isEmpty(addOldTime)) {
				addOldTime = "0";
			}
			int addTimeValue = Integer.parseInt(addOldTime);
			if (addTimeValue <= 110) {
				time = time + 10;
			} else {
				time = 120;
			}
			mPasslockTimeSetting.setText("" + time);
			break;
		case R.id.passlock_time_yes:
			ConfirmSettingTime();
			break;
		default:
			break;
		}
	}

	private void ConfirmSettingTime() {
		SharedPreferences preferences = getSharedPreferences("time_info", 0);
		int time = Integer.parseInt(mPasslockTimeSetting.getText().toString()
				.trim()) * 60;
		preferences.edit().putInt("restart", 1).commit();
		preferences.edit().putBoolean("isTimeCountdownOK", true).commit();
		preferences.edit().putInt("countdown", time).commit();
		preferences
				.edit()
				.putInt("time",
						Integer.parseInt(mPasslockTimeSetting.getText()
								.toString().trim())).commit();
		mPasslockTimeLayout.setVisibility(View.INVISIBLE);
		Toast.makeText(PassLockActivity.this,R.string.time_set_success, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		HttpCommon.hideSystemUI(PassLockActivity.this, true);
		mEditPassword.setText("");
		if (isChangePsw) {
			mMainLayout.setVisibility(View.GONE);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.e("lee", "ShowForgetPasswordPopupWindow --------");
					ShowForgetPasswordPopupWindow();
				}
			}, 500);
		} else {
			mMainLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		HttpCommon.hideSystemUI(PassLockActivity.this, true);
	}

	/**
	 * Monitor screen click event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// if (mForgetPasswordPopupWindow != null) {
		// Utils.CloseKeyBoard(PassLockActivity.this);

		// mDefaultPassword.setFocusable(true);
		// }
		mEditPassword.setCursorVisible(false);
		Utils.CloseKeyBoard(PassLockActivity.this, mEditPassword);
		return super.onTouchEvent(event);
	}
}