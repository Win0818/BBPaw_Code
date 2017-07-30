package com.worldchip.bbpawphonechat.activity;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class RegisterActivity extends Activity implements OnClickListener {

	private static final String TAG = "CHRIS";
	private EditText mEtUserName, mEtPassWord, mEtPassConfirm, mEtConfirmCode;
	private Button mBtnRegister;
	private ImageView mIvback;

	private ImageView mIvUserNameClear, mIvPswVisable, mIvPswConfrimVisable,
			mIvPswInvisable, mIvPswConfirmInvisable;
	
	private Button mBtnSendCodeRequest, mBtnSendCodeAgain, mBtnCountTiem;
	
	private RelativeLayout  mRlRegisterClearName, mRlRegistePswVisiable, mRlRegisterConfrimVisiable;

	private boolean mPswIsShow, mConfirmPswIsShow;
	private HashMap<String, String> mRegiserMap;
	private MyCount mSendCodeCount = null;
	private String  mRegisterName;
	private TextView iv_sign_up;
	private TextView tv_register_account;
	private TextView tv_register_password;
	
	private CheckBox mCheckBoxAgree;
	private ImageButton mTvBBpawProtocol;
	
	private MyEditInputListen mEditListen;
	
	private boolean accountIsCurrent, passwordIsOk,VerifyIsOk,isAgreeProtocols;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mEtUserName = (EditText) findViewById(R.id.et_register_user_name);
		mEtPassWord = (EditText) findViewById(R.id.et_register_pass_word);
		mEtPassConfirm = (EditText) findViewById(R.id.et_register_confirm);
		mEtConfirmCode = (EditText) findViewById(R.id.et_register_confirm_code);

		mBtnRegister = (Button) findViewById(R.id.btn_register);
		mIvback = (ImageView) findViewById(R.id.iv_register_back);

		mIvUserNameClear = (ImageView) findViewById(R.id.iv_register_delete_edit_name);

		mIvPswVisable = (ImageView) findViewById(R.id.iv_register_psw_visbility);
		mIvPswInvisable = (ImageView) findViewById(R.id.iv_register_psw_invisbility);

		mIvPswConfrimVisable = (ImageView) findViewById(R.id.iv_register_confirm_psw_visbility);
		mIvPswConfirmInvisable = (ImageView) findViewById(R.id.iv_register_confirm_psw_invisbility);

		mBtnSendCodeRequest = (Button) findViewById(R.id.btn_register_send_code);
		mBtnSendCodeAgain = (Button) findViewById(R.id.btn_register_send_code_again);
		mBtnCountTiem = (Button) findViewById(R.id.btn_register_show_coount_time);
		
		mRlRegisterClearName = (RelativeLayout) findViewById(R.id.rl_register_delect_username_img);
		mRlRegistePswVisiable = (RelativeLayout) findViewById(R.id.rl_register_psw_visbility);
		mRlRegisterConfrimVisiable = (RelativeLayout) findViewById(R.id.rl_register_confirm_psw_visbility);
		
		iv_sign_up=(TextView) findViewById(R.id.chat_to_name);
		tv_register_account = (TextView) findViewById(R.id.tv_register_account);
		tv_register_password = (TextView) findViewById(R.id.tv_register_password);
		
		mCheckBoxAgree = (CheckBox) findViewById(R.id.checkbox_agree_protocol);
		mTvBBpawProtocol = (ImageButton) findViewById(R.id.tv_agree_protocol_link);
		
		if(MyApplication.getInstance().system_local_language == 0){
			mTvBBpawProtocol.setBackgroundResource(R.drawable.agree_image_icon_cn);
		}else if(MyApplication.getInstance().system_local_language == 1){
			mTvBBpawProtocol.setBackgroundResource(R.drawable.agree_image_icon_es);
		}else{
			mTvBBpawProtocol.setBackgroundResource(R.drawable.agree_image_icon_en);
		}
		
		imageAdapter();
		mRegiserMap = new HashMap<String, String>();
		mEditListen = new MyEditInputListen();
		
		mBtnRegister.setOnClickListener(this);
		mIvback.setOnClickListener(this);
		mRlRegisterClearName.setOnClickListener(this);
		mRlRegistePswVisiable.setOnClickListener(this);
		mRlRegisterConfrimVisiable.setOnClickListener(this);
		mTvBBpawProtocol.setOnClickListener(this);
		mBtnSendCodeRequest.setOnClickListener(this);
		mBtnSendCodeAgain.setOnClickListener(this);

		mPswIsShow = false;
		mConfirmPswIsShow = false;
		
		mEtPassWord.setKeyListener(mEditListen);
		mEtPassConfirm.setKeyListener(mEditListen);
		
		mEtUserName.addTextChangedListener(new MyEditCourrentWatcher(mEtUserName));
		mEtPassWord.addTextChangedListener(new MyEditCourrentWatcher(mEtPassWord));
		mEtPassConfirm.addTextChangedListener(new MyEditCourrentWatcher(mEtPassConfirm));
		mEtConfirmCode.addTextChangedListener(new MyEditCourrentWatcher(mEtConfirmCode));
		mCheckBoxAgree.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				  if(accountIsCurrent && passwordIsOk && VerifyIsOk && isChecked){
						mBtnRegister.setEnabled(true);
						mBtnRegister.setClickable(true);
				 }else{
					   mBtnRegister.setEnabled(false);
					   mBtnRegister.setClickable(false);
				 }
			}
		});
		mBtnRegister.setEnabled(false);
		mBtnRegister.setClickable(false);
	  }
	
	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				tv_register_account,
				new int[] { R.drawable.register_account_txt_img,
						R.drawable.register_account_txt_img_es,
						R.drawable.register_account_txt_img_en });
		MyApplication.getInstance().ImageAdapter(
				tv_register_password,
				new int[] { R.drawable.register_psw_txt_img,
						R.drawable.register_psw_txt_img_es,
						R.drawable.register_psw_txt_img_en });
		MyApplication.getInstance().ImageAdapter(
				iv_sign_up,
				new int[] { R.drawable.register_title_img,
						R.drawable.register_title_img_es,
						R.drawable.register_title_img_en });
		
		MyApplication.getInstance().ImageAdapter(
				mBtnSendCodeRequest,
				new int[] { R.drawable.send_btn_default_01,
						R.drawable.send_btn_default_01_es,
						R.drawable.send_btn_default_01_en });
		
		MyApplication.getInstance().ImageAdapter(
				mBtnRegister,
				new int[] { R.drawable.selector_toreginster_btn,
						R.drawable.selector_btn_sure_es,
						R.drawable.selector_toreginster_btn_en });
		

		MyApplication.getInstance().ImageAdapter(
				mBtnSendCodeAgain,
				new int[] { R.drawable.send_again_btn_default_01,
						R.drawable.send_again_btn_default_01_es,
						R.drawable.send_again_btn_default_01_en });
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_register:
			register();
			break;
		case R.id.iv_register_back:
			finish();
			break;
		case R.id.rl_register_delect_username_img:
			if (mEtUserName != null) {
				mEtUserName.setText("");
			}
			break;
		case R.id.rl_register_psw_visbility:
			clickShowPsw();
			break;
		case R.id.rl_register_confirm_psw_visbility:
			clickShowConfirmPsw();
			break;
		case R.id.btn_register_send_code:
			sendVerifyCode();
			break;
		case R.id.btn_register_send_code_again:
			sendVerifyCode();
			break;
		case R.id.tv_agree_protocol_link:
			Intent  intent = new Intent(this, MyProtocolContentsActivity.class);
			startActivity(intent);
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
		mEtPassConfirm.setKeyListener(mEditListen);
	}

	private void clickShowPsw() {
		if (mPswIsShow) {
			mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
					|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mIvPswInvisable.setVisibility(View.VISIBLE);
			mPswIsShow = false;
		} else {
			mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL);
			mIvPswInvisable.setVisibility(View.GONE);
			mPswIsShow = true;
		}
		mEtPassWord.setSelection(mEtPassWord.getText().toString().length());
		mEtPassWord.setKeyListener(mEditListen);
	}

	/**
	 * 注册
	 * @param view
	 */
	public void register() {
		String st0 = getResources().getString(R.string.network_anomalies);
		String st1 = getResources().getString(R.string.User_name_cannot_be_empty);
		String st2 = getResources().getString(R.string.Password_cannot_be_empty);
		String st3 = getResources().getString(R.string.Confirm_password_cannot_be_empty);
		String st4 = getResources().getString(R.string.Two_input_password);
		String st5 = getResources().getString(R.string.please_write_confrim_code);
		String st11 = getResources().getString(R.string.register_pass_hint);
		String st22 = getResources().getString(R.string.please_agree_protocol);
		
		
		final String username = mEtUserName.getText().toString().trim();
		final String pwd = mEtPassWord.getText().toString().trim();
		final String confirm_code = mEtConfirmCode.getText().toString().trim();
		String confirm_pwd = mEtPassConfirm.getText().toString().trim();
		
		 if(username.contains("@")){
			 mRegisterName = username.replaceAll("@", "");
		  }else{
			  mRegisterName = username;
		  }
		 
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

	    if(!mCheckBoxAgree.isChecked()){
	    	Toast.makeText(this, st22 , Toast.LENGTH_LONG).show();
	    	return;
	    }
	    
	    String st6 = getResources().getString(R.string.Is_the_registered);
	    final String st7 = getResources().getString(R.string.Registered_successfully); 
	    final String st8 = getResources().getString(R.string.Registration_code_false); 
	    final String st9 = getResources().getString(R.string.User_already_exists); 
	    final String st10 = getResources().getString(R.string.Registration_failed); 
	    
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(st6);
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						mRegiserMap.put("param","register");
						mRegiserMap.put("username", username);
						mRegiserMap.put("registername", mRegisterName);
						mRegiserMap.put("person", "parent");
						mRegiserMap.put("password", pwd);
						mRegiserMap.put("code", confirm_code);
						String url = MyComment.REGISTER_HUAN_XIN;
						String result = HttpUtils.postRequest(url, mRegiserMap,RegisterActivity.this);
						if (result.equals("true")) {
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									// 保存用户名
									MyApplication.getInstance().setUserName(
											username);
									Toast.makeText(getApplicationContext(),
											st7, Toast.LENGTH_LONG).show();
									finish();
								}
							});
						}else if(result.equals("code_false")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									stopMyCount();
									initSendAgain();
									Toast.makeText(getApplicationContext(),
											st8, Toast.LENGTH_LONG).show();
								}
							});
						}else if(result.equals("user_exist")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									    stopMyCount();
									    initSendAgain();
									Toast.makeText(getApplicationContext(),
											st9, Toast.LENGTH_LONG).show();
								}
							});
						}else if(result.equals("register_false")){
							runOnUiThread(new Runnable() {
								public void run() {
									if (!RegisterActivity.this.isFinishing())
										pd.dismiss();
									    stopMyCount();
									    initSendAgain();
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
					String url = MyComment.GET_VERIFY_CODE+"&username="+username+"&purpose=1";
					try {
						String result = HttpUtils.getRequest(url,
								RegisterActivity.this);
						runOnUiThread(new Runnable() {
							public void run() {
								initSendCurrent();
								startMyCount();
								if(CommonUtils.isMobileNo(username)){
					            Toast.makeText(RegisterActivity.this, phoneCode, Toast.LENGTH_LONG).show();
								}else if(CommonUtils.isMailAddress(username)){
								Toast.makeText(RegisterActivity.this, emailCode, Toast.LENGTH_LONG).show();
								}
							}
				        });
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					runOnUiThread(new Runnable() {
						public void run() {
					        Toast.makeText(RegisterActivity.this, formatError, Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
		}else{
			Toast.makeText(RegisterActivity.this, st0, Toast.LENGTH_LONG).show();
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
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtnCountTiem.setText(millisUntilFinished / 1000 + "s");
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
	
	class MyEditInputListen extends DigitsKeyListener{

		@Override
		public int getInputType() {
			 return InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT;
		}
		
		@Override
		protected char[] getAcceptedChars() {
			char[] data = getStringData(R.string.login_only_can_input).toCharArray();  
			return data;
		}
	}
	
	 public String getStringData(int id) {
	        return getResources().getString(id);
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
		public void afterTextChanged(Editable s) {	
		  if(accountIsCurrent && passwordIsOk && VerifyIsOk && mCheckBoxAgree.isChecked()){
				mBtnRegister.setEnabled(true);
				mBtnRegister.setClickable(true);
		 }else{
			   mBtnRegister.setEnabled(false);
			   mBtnRegister.setClickable(false);
		 }
		}
	 }
	 
}
