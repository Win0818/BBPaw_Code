package com.worldchip.bbpawphonechat.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.easemob.util.EMLog;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.dialog.MyProgressDialog;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class ChangePassword extends Activity implements OnClickListener {
	public static final String TAG = "CHRIS";
	private EditText et_original, et_new, et_sure;
	private ImageView iv_original_invisable, iv_new_invisable,
			iv_sure_invisable;

	private RelativeLayout mRlChangePswOld, mRlChangePswNew,
			mRlChangePswNewConfrim;

	private Button btn_change_psw;

	private String mOldPassWord, mNewPassWord, mConfirmPassWord;

	private HashMap<String, String> mChangePassMap = new HashMap<String, String>();

	private String mCurrentUserName = null;

	private android.app.AlertDialog.Builder passChangeBuilder;

	private MyProgressDialog dialog;
	private ImageView iv;
	
	private boolean oldPassword, newPassword, confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		iv = (ImageView) findViewById(R.id.iv_change_psw);
		MyApplication.getInstance().ImageAdapter(
				iv,
				new int[] { R.drawable.chat_setting_change_psw,
						R.drawable.chat_setting_change_psw_es,
						R.drawable.chat_setting_change_psw_en });

		et_original = (EditText) findViewById(R.id.et_orginal_password);
		et_new = (EditText) findViewById(R.id.et_new_password);
		et_sure = (EditText) findViewById(R.id.et_sure_password);

		iv_original_invisable = (ImageView) findViewById(R.id.iv_change_old_psw_invisiable);
		iv_new_invisable = (ImageView) findViewById(R.id.iv_change_psw_new_invisiable);
		iv_sure_invisable = (ImageView) findViewById(R.id.iv_change_psw_sure_invisiable);

		mRlChangePswOld = (RelativeLayout) findViewById(R.id.rl_change_old_psw_visiable);
		mRlChangePswNew = (RelativeLayout) findViewById(R.id.rl_change_psw_new_visiable);
		mRlChangePswNewConfrim = (RelativeLayout) findViewById(R.id.rl_change_psw_sure_visiable);

		btn_change_psw = (Button) findViewById(R.id.btn_change_psw);
		MyApplication.getInstance().ImageAdapter(
				btn_change_psw,
				new int[] { R.drawable.selector_btn_sure_enable,
						R.drawable.selector_btn_sure_enable_es,
						R.drawable.selector_btn_sure_enable_en
						});

		mRlChangePswOld.setOnClickListener(this);
		mRlChangePswNew.setOnClickListener(this);
		mRlChangePswNewConfrim.setOnClickListener(this);
		btn_change_psw.setOnClickListener(this);

		btn_change_psw.setEnabled(false);
		btn_change_psw.setClickable(false);
		
		et_original.addTextChangedListener(new MyEditCourrentWatcher(et_original));
		et_new.addTextChangedListener(new MyEditCourrentWatcher(et_new));
		et_sure.addTextChangedListener(new MyEditCourrentWatcher(et_sure));
		
		dialog = MyProgressDialog.createProgressDialog(this);

		mCurrentUserName = MyApplication.getInstance().getUserName();

	}

	public void onBack(View view) {
		super.onBackPressed();
	}

	private boolean originalIsVisible = false, newIsVisible = false,
			sureIsVisible = false;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_change_old_psw_visiable:
			if (!originalIsVisible) {
				et_original.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_NUMBER_VARIATION_NORMAL);
				iv_original_invisable.setVisibility(View.GONE);
				originalIsVisible = true;
			} else {
				et_original.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				iv_original_invisable.setVisibility(View.VISIBLE);
				;
				originalIsVisible = false;
			}
			et_original.setSelection(et_original.getText().toString().length());
			break;
		case R.id.rl_change_psw_new_visiable:
			if (!newIsVisible) {
				et_new.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_NUMBER_VARIATION_NORMAL);
				iv_new_invisable.setVisibility(View.GONE);
				newIsVisible = true;
			} else {
				et_new.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				iv_new_invisable.setVisibility(View.VISIBLE);
				newIsVisible = false;
			}
			et_new.setSelection(et_new.getText().toString().length());
			break;
		case R.id.rl_change_psw_sure_visiable:
			if (!sureIsVisible) {
				et_sure.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_NUMBER_VARIATION_NORMAL);
				iv_sure_invisable.setVisibility(View.GONE);
				sureIsVisible = true;
			} else {
				et_sure.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				iv_sure_invisable.setVisibility(View.VISIBLE);
				sureIsVisible = false;
			}
			et_sure.setSelection(et_sure.getText().toString().length());
			break;
		case R.id.btn_change_psw:
			changePassWord();
			break;
		default:
			break;
		}
	}

	private void changePassWord() {
		mOldPassWord = et_original.getText().toString().trim();
		mNewPassWord = et_new.getText().toString().trim();
		mConfirmPassWord = et_sure.getText().toString().trim();
		Log.i(TAG, mOldPassWord + "-------" + mNewPassWord + "-------"
				+ mConfirmPassWord);
		String s1 = getResources().getString(R.string.please_write_old_pass);
		String s2 = getResources().getString(R.string.please_write_new_pass);
		String s3 = getResources()
				.getString(R.string.please_write_confirm_pass);
		String s4 = getResources().getString(
				R.string.please_write_change_password);
		String st11 = getResources().getString(R.string.register_pass_hint);
		
		

		if (TextUtils.isEmpty(mOldPassWord)) {
			Toast.makeText(this, s1, Toast.LENGTH_LONG).show();
			return;
		}
		
		  if(mOldPassWord.length()<6 || mOldPassWord.length()>12){
			   Toast.makeText(this, st11, Toast.LENGTH_SHORT).show();
			   et_original.requestFocus();
			   return;
		   }

		if (TextUtils.isEmpty(mNewPassWord)) {
			Toast.makeText(this, s2, Toast.LENGTH_LONG).show();
			return;
		}
		
		
		 if(mNewPassWord.length()<6 || mNewPassWord.length()>12){
			   Toast.makeText(this, st11, Toast.LENGTH_SHORT).show();
			   et_new.requestFocus();
			   return;
		   }
		
		if (TextUtils.isEmpty(mConfirmPassWord)) {
			Toast.makeText(this, s3, Toast.LENGTH_LONG).show();
			return;
		}
		
		if (!mNewPassWord.equals(mConfirmPassWord)) {
			Toast.makeText(this, s3, Toast.LENGTH_LONG).show();
			return;
		}

		if (dialog != null) {
			dialog.setMessage(s4);
			dialog.show();
		}

		MyChangePassTask changePassTask = new MyChangePassTask();
		changePassTask.execute();
	}

	class MyChangePassTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			String url = MyComment.CHANGE_HUANXIN_PASSWORD;
			mChangePassMap.put("username", mCurrentUserName);
			mChangePassMap.put("newpassword", mNewPassWord);
			mChangePassMap.put("password", mOldPassWord);
			String result = null;
			try {
				result = HttpUtils.postRequest(url, mChangePassMap,
						ChangePassword.this);
				Log.i(TAG, mCurrentUserName + "---------------修改密码---------"
						+ result);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("true")) {
				dialog.dismiss();
				Toast.makeText(ChangePassword.this, R.string.change_psw_ok, Toast.LENGTH_LONG)
						.show();
				showConflictDialog();
			} else {
				dialog.dismiss();
				Toast.makeText(ChangePassword.this, R.string.change_psw_fail,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 显示密码修改成功的dialog
	 */
	private void showConflictDialog() {
		MyApplication.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!ChangePassword.this.isFinishing()) {
			// clear up global variables
			try {
				if (passChangeBuilder == null)
					passChangeBuilder = new android.app.AlertDialog.Builder(
							ChangePassword.this);
				passChangeBuilder.setTitle(st);
				passChangeBuilder.setMessage(R.string.please_login_again);
				passChangeBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								passChangeBuilder = null;
								finish();
								startActivity(new Intent(ChangePassword.this,
										LoginActivity.class));
							}
						});
				passChangeBuilder.setCancelable(false);
				passChangeBuilder.create().show();
			} catch (Exception e) {
				EMLog.e(TAG,
						"---------color conflictBuilder error" + e.getMessage());
			}

		}
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
			if(mEdittext == et_original){
				if(editContent.length() > 0){
					oldPassword = true;
				}else{
					oldPassword = false;
				}
			}else if(mEdittext == et_new){
				if(editContent.length() >= 6 && editContent.length() <= 12){
					newPassword = true;
				}else{
					newPassword = false;
				}
			}else if(mEdittext == et_sure){
				if(editContent.length() >= 6 && editContent.length() <= 12){
					confirmPassword = true;
				}else{
					confirmPassword = false;
				}
			}
		}
		@Override
		public void afterTextChanged(Editable s) {	
		  if(oldPassword && newPassword && confirmPassword){
			  btn_change_psw.setEnabled(true);
			  btn_change_psw.setClickable(true);
		 }else{
			  btn_change_psw.setEnabled(false);
			  btn_change_psw.setClickable(false);
		 }
		}
	 }

}
