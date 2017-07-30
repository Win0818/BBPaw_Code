package com.worldchip.bbpaw.bootsetting.activity;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.Configure;
import com.worldchip.bbpaw.bootsetting.util.HttpResponseCallBack;
import com.worldchip.bbpaw.bootsetting.util.HttpUtils;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.PictureUploadUtils;
import com.worldchip.bbpaw.bootsetting.util.Utils;
import com.worldchip.bbpaw.bootsetting.view.GlobalProgressDialog;
import com.worldchip.bbpaw.bootsetting.view.MyTosat;

public class TermsActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private static final String TAG = TermsActivity.class.getSimpleName();
	private Button mBtnBack, mBtnNext, mBtnEnter, mBtnCancel;
	private RadioGroup mRg;
	private Boolean isAgree = true;
	private LinearLayout mLlTerms;
	private RelativeLayout mRlInquire;
	private GlobalProgressDialog mGlobalProgressDialog;
	private static final int START_PHOTO_UPLOAD = 100;
	private static final int REGISTER_COMPLETE = 102;
	private static final int PHOTO_UPLOAD_COMPLETE = 101;
	private WebView mUserTermsContent;
	private static final int REGIETER_RESULT_NOTWORK = 0;
	private static final int REGIETER_RESULT_SUCCESS = 1;
	private static final int REGIETER_RESULT_FAILURE = 2;
	private static final int FLAG_HOMEKEY_DISPATCHED = 100001; //拦截 home按键
	
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_PHOTO_UPLOAD:
				startProgressDialog();
				break;
			case PHOTO_UPLOAD_COMPLETE:
				String imageUrl ="";
				if (msg.obj != null) {
					imageUrl = (String)msg.obj;
				}
				commitRegister(imageUrl);
				break;
			case REGISTER_COMPLETE:
				int result = msg.arg1;
				int messageResId = -1;
				stopProgressDialog();
				if (MyApplication.isRegister) {
					switch (result) {
					case REGIETER_RESULT_NOTWORK:
						messageResId = R.string.not_network;
						break;
					case REGIETER_RESULT_SUCCESS:
						messageResId = R.string.register_sucees;
						break;
					case REGIETER_RESULT_FAILURE:
						messageResId = R.string.register_failure;
						break;
					}
				} else {
					messageResId = R.string.setup_sucees;
				}
				if (messageResId != -1) {
					MyTosat.makeText(TermsActivity.this, messageResId, Toast.LENGTH_SHORT).show();
				}
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						finishSetup();
					}
				}, 2000);
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.terms_activity);
		initView();
		initListener();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnNext = (Button) findViewById(R.id.btn_next);
		mBtnEnter = (Button) findViewById(R.id.enter);
		mBtnCancel = (Button) findViewById(R.id.cancel);
		mRg = (RadioGroup) findViewById(R.id.rg);
		mLlTerms = (LinearLayout) findViewById(R.id.ll_terms);
		mRlInquire = (RelativeLayout) findViewById(R.id.rl_inquire);
		
		mUserTermsContent = (WebView)(findViewById(R.id.terms_content));
		WebSettings settings = mUserTermsContent.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("utf-8") ; 
		//settings.setBuiltInZoomControls(true);// 设置是否可缩放
		mUserTermsContent.setBackgroundColor(0); // 设置背景色  
		String language = Locale.getDefault().getLanguage();
		LogUtil.e(TAG, "language == "+language);
		if (language.contains("zh")) {
			mUserTermsContent.loadUrl(Configure.USER_TERMS_HTML_PATH_CN);
		} else {
			mUserTermsContent.loadUrl(Configure.USER_TERMS_HTML_PATH_ENG);
		}
			
	}

	private void initListener() {
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnEnter.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mRg.setOnCheckedChangeListener(this);
		mRg.check(R.id.rb_agree);
		
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_next:
			Common.setPreferenceValue(Utils.TERMS_ENABLE_SHARD_KEY, (isAgree ? "1" : "0"));
			if (isAgree) {
				uploadUserPhoto();
			} else {
				mLlTerms.setVisibility(View.GONE);
				mRlInquire.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.enter://不同意协议按钮
			Message msg = new Message();
			msg.what = REGISTER_COMPLETE;
			msg.arg1 = REGIETER_RESULT_FAILURE;
			mHandler.sendMessage(msg);
			break;
		case R.id.cancel:
			mRlInquire.setVisibility(View.GONE);
			mLlTerms.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	private void uploadUserPhoto() {
		mHandler.sendEmptyMessage(START_PHOTO_UPLOAD);
		if (!Common.isNetworkConnected(MyApplication.getAppContext())) {
			//goToBBPawLauncher();
			Message msg = new Message();
			msg.what = REGISTER_COMPLETE;
			msg.arg1 = REGIETER_RESULT_NOTWORK;
			mHandler.sendMessage(msg);
			return;
		}
		// TODO Auto-generated method stub
		String photoPath = Common.getPreferenceValue(Utils.PHOTO_SHARD_KEY, "");
		if (photoPath == null || TextUtils.isEmpty(photoPath)) {
			commitRegister("");
			return;
		}
		LogUtil.e(TAG, "commitRegister photoPath == "+photoPath);
		PictureUploadUtils.doPost(photoPath, new HttpResponseCallBack() {
			@Override
			public void onSuccess(String result, String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "upload -- onSuccess == "+result);
				Message msg = new Message();
	        	msg.what = PHOTO_UPLOAD_COMPLETE;
	        	msg.obj = result;
	        	mHandler.sendMessage(msg);
			}
			
			@Override
			public void onStart(String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "upload -- onStart ==>>> ");
				//mHandler.sendEmptyMessage(START_PHOTO_UPLOAD);
			}
			
			@Override
			public void onFinish(int result, String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "upload -- onFinish ==>>> "+result);
			}
			
			@Override
			public void onFailure(Exception e, String httpTag) {
				// TODO Auto-generated method stub
				if (e != null) {
					LogUtil.e(TAG, "upload -- onFailure ==>> "+e.toString());
	            }
				Message msg = new Message();
	        	msg.what = PHOTO_UPLOAD_COMPLETE;
	        	mHandler.sendMessage(msg);
			}
		}, "upload");
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.rb_agree:
			isAgree = true;
			break;
		case R.id.rb_disagree:
			isAgree = false;
			break;
		}
	}
	
	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			if (mGlobalProgressDialog.isShowing()) {
				mGlobalProgressDialog.dismiss();
			}
			mGlobalProgressDialog = null;
		}
	}
	
	private void commitRegister(String photoUrl) {
		String reqUrl = generateRegisterReqUrl(photoUrl);
		LogUtil.e(TAG, " Register reqUrl == "+reqUrl);
		HttpUtils.doPost(reqUrl, new HttpResponseCallBack() {
			
			@Override
			public void onSuccess(String result, String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "Register -- onSuccess ==>>>"+result);
				Message msg = new Message();
				msg.what = REGISTER_COMPLETE;
				if (TextUtils.isEmpty(result) || result.trim().equals("200")) {
					LogUtil.e(TAG, "Register -- show Toast ==>>>");
					msg.arg1 = REGIETER_RESULT_SUCCESS;
					//Common.setPreferenceValue(Utils.IS_FIRST_START_KEY, "false");
				} else if(result != null || result.equals("100")){
					msg.arg1 = REGIETER_RESULT_FAILURE;
				}
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onStart(String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "Register -- onStart ==>>> ");
			}
			
			@Override
			public void onFinish(int result, String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "Register -- onFinish ==>>> ");
				//finishSetup();
			}
			
			@Override
			public void onFailure(Exception e, String httpTag) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "Register -- onFailure ==>>> "+e.toString());
				Message msg = new Message();
				msg.what = REGISTER_COMPLETE;
				msg.arg1 = REGIETER_RESULT_FAILURE;
				mHandler.sendMessage(msg);
			}
		}, "register");
		
	}
	
	private void finishSetup() {
		// TODO Auto-generated method stub
		//goToBBPawLauncher();
		MyApplication.getInstance().exit();
	}

	private String generateRegisterReqUrl(String photoUrl) {
		StringBuilder reqUrl = new StringBuilder(Configure.USER_REGISTER_REQ_URL);
		String userName = Common.getPreferenceValue(Utils.USERNAME_SHARD_KEY, "");
		String gender = Common.getPreferenceValue(Utils.GENDER_SHARD_KEY, "");
		String dob = Common.getPreferenceValue(Utils.DOB_SHARD_KEY, "");
		String email = Common.getPreferenceValue(Utils.EMAIL_SHARD_KEY, "");
		String password = Common.getPreferenceValue(Utils.PASSWORD_SHARD_KEY, "");
		String deviceId = Common.getPreferenceValue(Utils.DEVICEID_SHARD_KEY, "");
		String language = Common.getPreferenceValue(Utils.LANGUAGE_SHARD_KEY, "");
		String coc = Common.getPreferenceValue(Utils.TERMS_ENABLE_SHARD_KEY, "");
		if (!TextUtils.isEmpty(userName)) {
			reqUrl.append("username="+userName);
		}
		if (!TextUtils.isEmpty(gender)) {
			reqUrl.append("&gender="+gender);
		}
		if (!TextUtils.isEmpty(dob)) {
			reqUrl.append("&dob="+dob);
		}
		if (!TextUtils.isEmpty(email)) {
			reqUrl.append("&email="+email);
		}
		if (!TextUtils.isEmpty(password)) {
			reqUrl.append("&password="+password);
		}
		if (!TextUtils.isEmpty(deviceId)) {
			reqUrl.append("&deviceId="+deviceId);
		}
		if (!TextUtils.isEmpty(language)) {
			reqUrl.append("&language="+language);
		}else {
			reqUrl.append("&language="+Locale.getDefault().getLanguage() + "_"
					+ Locale.getDefault().getCountry());
		}
		if (!TextUtils.isEmpty(coc)) {
			reqUrl.append("&coc="+coc);
		}
		if (!TextUtils.isEmpty(photoUrl)) {
			reqUrl.append("&photoUrl="+photoUrl);
		}
		return reqUrl.toString();
	}
	
	@SuppressLint("NewApi")
	private void finishSetupWizard() { 
        // Add a persistent setting to allow other apps to know the device has been provisioned. 
        Settings.Global.putInt(MyApplication.getAppContext().getContentResolver(), "device_provisioned" , 1); 
        Settings.Secure.putInt(MyApplication.getAppContext().getContentResolver(), "user_setup_complete", 1); 
        
        PackageManager pm = getPackageManager(); 
        ComponentName name = new ComponentName(this, MainActivity.class); 
        pm. setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP); 
	}
	
	@Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
