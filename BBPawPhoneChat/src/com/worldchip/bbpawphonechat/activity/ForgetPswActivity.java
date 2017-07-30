package com.worldchip.bbpawphonechat.activity;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.RegisterActivity.MyEditCourrentWatcher;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class ForgetPswActivity extends Activity implements OnClickListener {
	private static final String TAG = "CHRIS";
	private EditText mEtUserName, mEtPassWord, mEtPassConfirm, mEtConfirmCode;
	private Button mBtnChangePsw;
	private ImageView mIvback;

	private ImageView mIvUserNameClear, mIvPswVisable, mIvPswConfrimVisable,
			mIvPswInvisable, mIvPswConfirmInvisable;
	private Button mBtnSendCodeRequest, mBtnSendCodeAgain, mBtnCountTiem;

	private RelativeLayout  mRlForgetClearName, mRlForgetPswVisiable, mRlForgetConfrimVisiable;
	
	private boolean mPswIsShow, mConfirmPswIsShow;
	private HashMap<String, String> mRegiserMap;

	private MyCount mSendCodeCount = null;
	private TextView tv_forget_psw;
	private TextView tv_register_account;
	private TextView tv_register_password;
	
	private boolean accountIsCurrent, passwordIsOk,VerifyIsOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_psw);
		mEtUserName = (EditText) findViewById(R.id.et_forget_psw_user_name);
		mEtPassWord = (EditText) findViewById(R.id.et_forget_psw_new);
		mEtPassConfirm = (EditText) findViewById(R.id.et_forget_psw_confirm);
		mEtConfirmCode = (EditText) findViewById(R.id.et_forget_psw_confirm_code);

		mBtnChangePsw = (Button) findViewById(R.id.btn_change_psw_commit);
		mIvback = (ImageView) findViewById(R.id.iv_forget_psw_back);

		mIvUserNameClear = (ImageView) findViewById(R.id.iv_forget_psw_delete_edit_name);

		mIvPswVisable = (ImageView) findViewById(R.id.iv_forget_psw_new_visbility);
		mIvPswInvisable = (ImageView) findViewById(R.id.iv_forget_psw_new_invisbility);

		mIvPswConfrimVisable = (ImageView) findViewById(R.id.iv_forget_psw_confirm_psw_visbility);
		mIvPswConfirmInvisable = (ImageView) findViewById(R.id.iv_forget_psw_confirm_psw_invisbility);

		mBtnSendCodeRequest = (Button) findViewById(R.id.btn_forget_psw_send_code);
		mBtnSendCodeAgain = (Button) findViewById(R.id.btn_forget_psw_send_code_again);
		mBtnCountTiem = (Button) findViewById(R.id.btn_forget_psw_show_coount_time);

		mRlForgetClearName = (RelativeLayout) findViewById(R.id.rl_forget_psw_delete_edit_name);
		mRlForgetPswVisiable = (RelativeLayout) findViewById(R.id.rl_forget_psw_new_visbility);
		mRlForgetConfrimVisiable = (RelativeLayout) findViewById(R.id.rl_forget_psw_confirm_psw_visbility);
		
		tv_forget_psw=(TextView)findViewById(R.id.tv_forget_psw);
		tv_register_account = (TextView) findViewById(R.id.tv_forget_psw_account);
		tv_register_password = (TextView) findViewById(R.id.tv_forget_psw_password);
		
		imageAdpter();
		
		
		mRegiserMap = new HashMap<String, String>();
		mBtnChangePsw.setOnClickListener(this);
		mIvback.setOnClickListener(this);
		
		mRlForgetClearName.setOnClickListener(this);
		mRlForgetPswVisiable.setOnClickListener(this);
		mRlForgetConfrimVisiable.setOnClickListener(this);
		
		mBtnSendCodeRequest.setOnClickListener(this);
		mBtnSendCodeAgain.setOnClickListener(this);

		mPswIsShow = false;
		mConfirmPswIsShow = false;
		
		mBtnChangePsw.setEnabled(false);
		mBtnChangePsw.setClickable(false);
		mEtUserName.addTextChangedListener(new MyEditCourrentWatcher(mEtUserName));
		mEtPassWord.addTextChangedListener(new MyEditCourrentWatcher(mEtPassWord));
		mEtConfirmCode.addTextChangedListener(new MyEditCourrentWatcher(mEtConfirmCode));
		
		
		if (MyApplication.getInstance().getUserName() != null) {
			mEtUserName.setText(MyApplication.getInstance().getUserName());
			mEtUserName.setSelection(mEtUserName.getText().toString().length());
		}

	}

	private void imageAdpter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				tv_register_account,
				new int[] { R.drawable.register_account_txt_img,
						R.drawable.register_account_txt_img_es,
						R.drawable.register_account_txt_img_en });
		MyApplication.getInstance().ImageAdapter(
				tv_register_password,
				new int[] { R.drawable.register_psw_txt_img,
						R.drawable.register_psw_txt_img_en,
						R.drawable.register_psw_txt_img_en });
		MyApplication.getInstance().ImageAdapter(
				tv_forget_psw,
				new int[] { R.drawable.forget_psw_title_img,
						R.drawable.forget_psw_title_img_es,
						R.drawable.forget_psw_title_img_en });
		
		MyApplication.getInstance().ImageAdapter(
				mBtnSendCodeRequest,
				new int[] { R.drawable.send_btn_default_01,
						R.drawable.send_btn_default_01_es,
						R.drawable.send_btn_default_01_en });
		
		MyApplication.getInstance().ImageAdapter(
				mBtnChangePsw,
				new int[] { R.drawable.selector_btn_sure_enable,
						R.drawable.selector_btn_sure_enable_es,
						R.drawable.selector_btn_sure_enable_en });
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_change_psw_commit:
			changePswCommit();
			break;
		case R.id.iv_forget_psw_back:
			finish();
			break;
		case R.id.rl_forget_psw_delete_edit_name:
			if (mEtUserName != null) {
				mEtUserName.setText("");
			}
			break;
		case R.id.rl_forget_psw_new_visbility:
			clickShowPsw();
			break;
		case R.id.rl_forget_psw_confirm_psw_visbility:
			clickShowConfirmPsw();
			break;
		case R.id.btn_forget_psw_send_code:
			sendVerifyCode();
			initSendCurrent();
			startMyCount();
			break;
		case R.id.btn_forget_psw_send_code_again:
			startMyCount();
			sendVerifyCode();
			initSendCurrent();
			break;

		default:
			break;
		}
	}

	private void clickShowConfirmPsw(){
		if (mConfirmPswIsShow) {
			mEtPassConfirm.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mIvPswConfirmInvisable.setVisibility(View.VISIBLE);
			mConfirmPswIsShow = false;
		} else {
			mEtPassConfirm.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL);
			mIvPswConfirmInvisable.setVisibility(View.GONE);
			mConfirmPswIsShow = true;
		}
		mEtPassConfirm.setSelection(mEtPassConfirm.getText().toString().length());
	}

	private void clickShowPsw() {
		if (mPswIsShow) {
			mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mIvPswInvisable.setVisibility(View.VISIBLE);
			mPswIsShow = false;
		} else {
			mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL);
			mIvPswInvisable.setVisibility(View.GONE);
			mPswIsShow = true;
		}
		mEtPassWord.setSelection(mEtPassWord.getText().toString().length());
	}

	/**
	 * 忘记密码
	 * @param view
	 */
	public void changePswCommit() {
		String st0 = getResources().getString(R.string.network_anomalies);
		String st1 = getResources().getString(R.string.User_name_cannot_be_empty);
		String st2 = getResources().getString(R.string.Password_cannot_be_empty);
		String st3 = getResources().getString(R.string.Confirm_password_cannot_be_empty);
		String st4 = getResources().getString(R.string.Two_input_password);
		String st5 = getResources().getString(R.string.please_write_confrim_code);
		String st11 = getResources().getString(R.string.register_pass_hint);
		
		final String username = mEtUserName.getText().toString().trim();
		final String pwd = mEtPassWord.getText().toString().trim();
		final String confirm_code = mEtConfirmCode.getText().toString().trim();
		String confirm_pwd = mEtPassConfirm.getText().toString().trim();

		if(!CommonUtils.isNetWorkConnected(this)){
			Toast.makeText(this, st0, Toast.LENGTH_SHORT).show();
			return ; 
		}
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			mEtUserName.requestFocus();
			return;
		} 
		
	   if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, st2, Toast.LENGTH_SHORT).show();
			mEtPassWord.requestFocus();
			return;
		} 
	   
	   if(pwd.length()<6 || pwd.length()>12){
		   Toast.makeText(this, st11, Toast.LENGTH_SHORT).show();
		   mEtPassWord.requestFocus();
		   return;
	   }
	   
	   
	   if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, st3, Toast.LENGTH_SHORT).show();
			mEtPassConfirm.requestFocus();
			return;
		} 
	   
	    if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, st4, Toast.LENGTH_SHORT).show();
			return;
		} 
	    
	    if (TextUtils.isEmpty(confirm_code)) {
			Toast.makeText(this, st5, Toast.LENGTH_LONG).show();
			return;
		}

	    String st6 = getResources().getString(R.string.forget_psw_change_now);
	    final String st7 = getResources().getString(R.string.forget_psw_change_success); 
	    final String st8 = getResources().getString(R.string.Registration_code_false); 
	    final String st9 = getResources().getString(R.string.User_is_not_exists); 
	    final String st10 = getResources().getString(R.string.forget_psw_change_failed); 
	    
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(st6);
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						mRegiserMap.put("param","edit_password");
						mRegiserMap.put("username", username);
						mRegiserMap.put("password", pwd);
						mRegiserMap.put("code", confirm_code);
						String url = MyComment.FORGET_PSW_CHANGE;
						String result = HttpUtils.postRequest(url, mRegiserMap,
								ForgetPswActivity.this);
						Log.i(TAG, "----------changePswCommit---------"+result);
						if (result.equals("true")) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (!ForgetPswActivity.this.isFinishing())
										pd.dismiss();
									// 保存用户名
									MyApplication.getInstance().setUserName(
											username);
									Toast.makeText(getApplicationContext(),
											st7, Toast.LENGTH_LONG).show();
									finish();
								}
							});
					        System.out.println("-------------修改密码---------" + result);
						}else if(result.equals("code_false")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!ForgetPswActivity.this.isFinishing())
										pd.dismiss();
									Toast.makeText(getApplicationContext(),
											st8, Toast.LENGTH_LONG).show();
								}
							});
						}else if(result.equals("username_not_exist")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!ForgetPswActivity.this.isFinishing())
										pd.dismiss();
									Toast.makeText(getApplicationContext(),
											st9, Toast.LENGTH_LONG).show();
								}
							});
						}else if(result.equals("false")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!ForgetPswActivity.this.isFinishing())
										pd.dismiss();
									Toast.makeText(getApplicationContext(),
											st10, Toast.LENGTH_LONG).show();
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	/*
	 * send Verify code
	 */
	private void sendVerifyCode() {
		String st0 = getResources().getString(R.string.network_anomalies);
		final String username = mEtUserName.getText().toString().trim();
		final String phoneCode = getResources().getString(R.string.get_phone_code);
		final String emailCode = getResources().getString(R.string.get_email_code);
		final String formatError = getResources().getString(R.string.username_format_error);
		if(CommonUtils.isNetWorkConnected(this)){
		   new Thread(new Runnable() {
			@Override
			public void run() {
				if (CommonUtils.isMobileNo(username) || CommonUtils.isMailAddress(username)) {
					String url = MyComment.GET_VERIFY_CODE+"&username="+username+"&purpose=2";
					try {
						String result = HttpUtils.getRequest(url,
								ForgetPswActivity.this);
						System.out.println(result+ "-----sendVerifyCode--------");
						runOnUiThread(new Runnable() {
							public void run() {
								initSendCurrent();
								startMyCount();
								if(CommonUtils.isMobileNo(username)){
					            Toast.makeText(ForgetPswActivity.this, phoneCode, Toast.LENGTH_LONG).show();
								}else if(CommonUtils.isMailAddress(username)){
								Toast.makeText(ForgetPswActivity.this, emailCode, Toast.LENGTH_LONG).show();
								}
							}
				        });
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					runOnUiThread(new Runnable() {
						public void run() {
					        Toast.makeText(ForgetPswActivity.this, formatError, Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
		}else{
			Toast.makeText(ForgetPswActivity.this, st0, Toast.LENGTH_LONG).show();
		}
	}

	
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			initSendAgain();
			stopMyCount();
			Log.i(TAG, "-------onFinish---------");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtnCountTiem.setText(millisUntilFinished / 1000 + "s");
			Log.i(TAG, "-------onTick---------");
		}

	}

	private void stopMyCount() {
		if (mSendCodeCount != null) {
			mSendCodeCount.cancel();
			mSendCodeCount = null;
		}
	}

	private void startMyCount() {
		stopMyCount();
		if (mSendCodeCount == null) {
			mSendCodeCount = new MyCount(1000 * 60, 1000);
			mSendCodeCount.start();
		}
	}
	

	private void initSendAgain() {
		mBtnCountTiem.setVisibility(View.GONE);
		mBtnSendCodeRequest.setVisibility(View.GONE);
		mBtnSendCodeAgain.setVisibility(View.VISIBLE);
	}
   
	private void initSendCurrent(){
		mBtnCountTiem.setVisibility(View.VISIBLE);
		mBtnSendCodeRequest.setVisibility(View.GONE);
		mBtnSendCodeAgain.setVisibility(View.GONE);
	}

	class  MyEditCourrentWatcher  implements  TextWatcher{
		 private EditText mEdittext; 
		 
		 public MyEditCourrentWatcher(EditText  editText){
			 mEdittext = editText;
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String editContent = s.toString().trim();
			if(mEdittext == mEtUserName){
				if(CommonUtils.isMobileNo(editContent) || CommonUtils.isMailAddress(editContent)){
					accountIsCurrent = true;
				}else{
					accountIsCurrent = false;
				}
			}else if(mEdittext == mEtPassWord){
				if(editContent.length() >= 6 && editContent.length() <= 12){
					passwordIsOk = true;
				}else{
					passwordIsOk = false;
				}
			}else if(mEdittext == mEtConfirmCode){
				if(editContent.length() > 0){
					VerifyIsOk = true;
				}else{
					VerifyIsOk = false;
				}
			}
		}
		@Override
		public void afterTextChanged(Editable s){	
		  if(accountIsCurrent && passwordIsOk && VerifyIsOk){
			  mBtnChangePsw.setEnabled(true);
			  mBtnChangePsw.setClickable(true);
		 }else{
			  mBtnChangePsw.setEnabled(false);
			  mBtnChangePsw.setClickable(false);
		  }
		}
	 }
	
	
}
