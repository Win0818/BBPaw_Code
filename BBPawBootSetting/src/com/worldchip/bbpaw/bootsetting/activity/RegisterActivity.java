package com.worldchip.bbpaw.bootsetting.activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.util.AccountRegisterUtil;
import com.worldchip.bbpaw.bootsetting.util.Common;
import com.worldchip.bbpaw.bootsetting.util.Configure;
import com.worldchip.bbpaw.bootsetting.util.HttpUtils;
import com.worldchip.bbpaw.bootsetting.util.LogUtil;
import com.worldchip.bbpaw.bootsetting.util.MD5Util;
import com.worldchip.bbpaw.bootsetting.util.Utils;
import com.worldchip.bbpaw.bootsetting.view.RegisterGlobalDialog;
import com.worldchip.bbpaw.bootsetting.view.RoundImageView;
import android.view.KeyEvent;
import android.util.Log;

public class RegisterActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private EditText mEditTextName;
	private EditText mEditTextEmail;
	private EditText mEditTextSetPWD;
	private EditText mEditTextConfirmPWD;
	private EditText mTextBirthday;
	private EditText mEditTextDay;
	private EditText mEditTextMonth;
	private EditText mEditTextYear;
	private EditText mTextGender;
	private ImageButton mImgBtnGender;
	private ImageButton mImgBtnBirthday;
	//private MySharedPreferences mMySP;
	private Button mBtnBack;
	private Button mBtnNext;
	private Button mBtnSystemPicture;
	private Button mBtnCustomAvatar;
	private RoundImageView mImgPhoto;
	private String mPhotoPath = "";
	private PopupWindow mBirthdayPop = null; 
	private PopupWindow mGenderPop = null; 
	private PopupWindow mPhonePop = null;
	private View mBirthdayPopContentView = null; 
	private View mGenderPopContentView = null; 
	private View mPhonePopContentView = null;
	private boolean isShowBirthdayPop = false;
	private boolean isShowGenderPop = false;
	private boolean isShowPhonePop = false;
	private static final int SYSTEM_IMAGE_REQUEST_CODE = 1;
	private static final int CUSTOM_IMAGE_REQUEST_CODE = 2;
	private RegisterGlobalDialog mRegisterGlobalDialog = null;
	private static final int FLAG_HOMEKEY_DISPATCHED = 100001; //拦截 home按键100001
	private ImageView mEmailPhoneSwitch;
	private int mEmailPhoneStatus = 0;
	private EditText mEditTextVerificationCode;
	private Button mBtnSendVerificationCode;
	private ImageButton mImgBtnEmail;
	private int mVerificationCode=0;
	private boolean mVerificationCodeRight = false;
	private TimeCount time;
	private int defTime = 60000;//60s
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MyApplication.getInstance().addActivity(this);
		getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.register_activity);
		//mMySP = new MySharedPreferences(this);
		initView();
		initListener();
	}

	private void initView() {
		// Button
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnNext = (Button) findViewById(R.id.btn_next);
		mImgPhoto = (RoundImageView) findViewById(R.id.iv_photo);
		mBtnSystemPicture = (Button) findViewById(R.id.btn_system_picture);
		mBtnCustomAvatar = (Button) findViewById(R.id.btn_custom_avatar);
		// about arrow
		mTextBirthday = (EditText) findViewById(R.id.et_birthday);
		mTextGender = (EditText) findViewById(R.id.et_gender);
		mImgBtnGender = (ImageButton) findViewById(R.id.imgbtn_gender);
		mImgBtnBirthday = (ImageButton) findViewById(R.id.imgbtn_birthday);
		mImgBtnEmail = (ImageButton) findViewById(R.id.imgbtn_email);
		mEditTextName = (EditText) findViewById(R.id.et_name);
		mEditTextEmail = (EditText) findViewById(R.id.et_email);
		mEmailPhoneStatus = 0;
		mEmailPhoneSwitch = (ImageView) findViewById(R.id.iv_email_picture);
		mEditTextSetPWD = (EditText) findViewById(R.id.et_set_password);
		mEditTextConfirmPWD = (EditText) findViewById(R.id.et_confirm_password);
		findViewById(R.id.register_parent_view).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				Common.hideKeyboard(RegisterActivity.this, mEditTextName);
				// TODO Auto-generated method stub
				return false;
			}
		});
		initBrithdayEditView();
		initGenderEditView();
		initPhoneVerificationView();
	}

	private void initListener() {
		// btn
		mBtnBack.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnSystemPicture.setOnClickListener(this);
		mBtnCustomAvatar.setOnClickListener(this);
		// ImageButton
		mImgBtnBirthday.setOnClickListener(this);
		mImgBtnGender.setOnClickListener(this);
		mImgBtnEmail.setOnClickListener(this);
		// edittext
		mEmailPhoneSwitch.setOnClickListener(this);
		mEditTextName.setOnFocusChangeListener(this);
		mEditTextEmail.setOnFocusChangeListener(this);
		mEditTextSetPWD.setOnFocusChangeListener(this);
		mEditTextConfirmPWD.setOnFocusChangeListener(this);
		mEditTextSetPWD.addTextChangedListener(new EditChangedListener());
		mEditTextConfirmPWD.addTextChangedListener(new EditChangedListener2());
		
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			mEmailPhoneSwitch.setClickable(true);
		} else {
			mEmailPhoneSwitch.setClickable(false);
		}
	}

	private void SaveUserInfo() {
		Common.setPreferenceValue(Utils.USERNAME_SHARD_KEY, mEditTextName.getText().toString());
		Common.setPreferenceValue(Utils.DOB_SHARD_KEY, mTextBirthday.getText().toString());
		Common.setPreferenceValue(Utils.EMAIL_SHARD_KEY, mEditTextEmail.getText().toString());
		String gender = mTextGender.getText().toString();
		if (!TextUtils.isEmpty(gender)) {
			if (gender.equals(getString(R.string.boy_text))) {
				gender = "1";
			} else if (gender.equals(getString(R.string.girl_text))) {
				gender = "0";
			}
		}
		Common.setPreferenceValue(Utils.GENDER_SHARD_KEY, gender);
		Common.setPreferenceValue(Utils.PASSWORD_SHARD_KEY, MD5Util.string2MD5(mEditTextSetPWD.getText().toString()));
		Common.setPreferenceValue(Utils.DEVICEID_SHARD_KEY, Common.getCpuSerial(MyApplication.getAppContext()));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_email_picture:
			if (mEmailPhoneStatus == 0) {
				time.cancel();
				time.onFinish();
				mEmailPhoneStatus = 1;
				mEmailPhoneSwitch.setImageResource(R.drawable.phone_picture);
				mEditTextEmail.setText("");
				mEditTextVerificationCode.setText("");
				mEditTextEmail.setHint(R.string.phone);
				mEditTextEmail.setInputType(InputType.TYPE_CLASS_PHONE);
				mImgBtnEmail.setVisibility(View.VISIBLE);
				mBtnSendVerificationCode.setBackgroundResource(R.drawable.but_send_verification);
				if (mEditTextEmail != null) {
					isShowPhonePop = true;
					showPhoneEditPop(mEditTextEmail, mPhonePopContentView,false);
				}
			}else {
				mEmailPhoneStatus = 0;
				mEmailPhoneSwitch.setImageResource(R.drawable.emai_picture);
				mEditTextEmail.setHint(R.string.e_mail);
				mEditTextEmail.setText("");
				mEditTextVerificationCode.setText("");
				mEditTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				mImgBtnEmail.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.bt_send_code_id:
			//Log.e("xiaolp","send");
			boolean isValidatePhoneNumber = AccountRegisterUtil.validatePhoneNumber(mEditTextEmail.getText().toString());
			if (isValidatePhoneNumber) {
				Log.e("xiaolp",">>>>"+mEditTextEmail.getText().toString());
				time.start();
				mEditTextVerificationCode.setText("");
				sendVerifyCode(mEditTextEmail.getText().toString());
			}else {
				showValidateErrorDialog(getString(R.string.validate_phone_number_error));
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_next:
			/*if (validateUserInfo()) {
				SaveUserInfo();
				Intent intentNext = new Intent(RegisterActivity.this,
						TermsActivity.class);
				startActivity(intentNext);
			}*/
			commitUserInfo();
			break;
		case R.id.btn_system_picture:
			Intent intentSysPic = new Intent();
			intentSysPic.setType("image/*");
			intentSysPic.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentSysPic, SYSTEM_IMAGE_REQUEST_CODE);
			hideSystemUI(RegisterActivity.this,false);
			break;
		case R.id.btn_custom_avatar:
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				try {
					Intent intentCstAvt = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					File externalDataDir = Environment.getExternalStoragePublicDirectory(
			                Environment.DIRECTORY_DCIM);
					mPhotoPath = externalDataDir.getAbsolutePath() + File.separator  + Configure.USER_TEMP_PIC_NAME;
				    File photoFile = new File(mPhotoPath);
				    if (photoFile.exists()) {
				    	photoFile.delete();
					}
				    Log.e("lee", "mUserPhotoPath == "+mPhotoPath);
					intentCstAvt.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPhotoPath)));
					startActivityForResult(intentCstAvt, CUSTOM_IMAGE_REQUEST_CODE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.imgbtn_birthday:
			if (!isShowBirthdayPop) {
				if (mTextBirthday != null) {
					isShowBirthdayPop = true;
					showBrithdayEditPop(mTextBirthday,mBirthdayPopContentView);
				}
			} else {
				hideBrithdayEditPop();
				setBirthdayText();
			}
			break;
		case R.id.imgbtn_gender:
			if (!isShowGenderPop) {
				if (mTextGender != null) {
					showGenderEditPop(mTextGender, mGenderPopContentView);
				}
			} else {
				hideGenderEditPop();
			}
			break;
		case R.id.imgbtn_email:
			if (!isShowPhonePop) {
				if (mEditTextEmail != null) {
					isShowPhonePop = true;
					showPhoneEditPop(mEditTextEmail, mPhonePopContentView,true);
				}
			}else {
				hidePhoneEditPop();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideSystemUI(RegisterActivity.this,true);
	}
	
	/**
	 * 隐藏菜单
	 * 
	 * @param context
	 * @param flag
	 */
	public static void hideSystemUI(Context context, boolean flag) {
		Intent intent = new Intent();
		intent.setAction(Utils.UPDATE_SYSTEMUI);
		intent.putExtra("hide_systemui", flag);
		context.sendBroadcast(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SYSTEM_IMAGE_REQUEST_CODE) {
			if (data != null) {
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					if (bitmap != null) {
						mImgPhoto.setImageBitmap(bitmap);
						mPhotoPath = Common.convertToFilePath(
								MyApplication.getAppContext(), uri);
					}
				} catch (Exception e) {
				}
			}
		} else if (requestCode == CUSTOM_IMAGE_REQUEST_CODE) {
			if (data == null && resultCode == Activity.RESULT_OK) {
				
	            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, null);
	    		if (bitmap != null) {
	    			mImgPhoto.setImageBitmap(bitmap);
	    		}
	        }
		}
		Common.setPreferenceValue(Utils.PHOTO_SHARD_KEY, mPhotoPath);
		LogUtil.e("lee", "Photo Path == "+mPhotoPath);
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * ��ʱ��������Ƭ���
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	private void commitUserInfo() {

		boolean isValidateName = AccountRegisterUtil.validateAccountName(mEditTextName.getText().toString());
		boolean isValidateBirthday = AccountRegisterUtil.validateBirthday(mTextBirthday.getText().toString());
		boolean isValidatePhoneNumber = AccountRegisterUtil.validatePhoneNumber(mEditTextEmail.getText().toString());
		boolean isValidateEmail = AccountRegisterUtil.validateEmail(mEditTextEmail.getText().toString());
		int validatePsw = AccountRegisterUtil.validatePassword(mEditTextSetPWD.getText().toString(), mEditTextConfirmPWD.getText().toString());
		
		if (mEmailPhoneStatus == 1) {
//			if (!isValidatePhoneNumber) {
//				showValidateErrorDialog(getString(R.string.validate_phone_number_error));
//				return;
//			}
			if (!mVerificationCodeRight) {
				showValidateErrorDialog(getString(R.string.validate_code_error));
				return;
			}
		}else if(mEmailPhoneStatus == 0){
			if (!isValidateEmail) {
				showValidateErrorDialog(getString(R.string.validate_email_error));
				return;
			} 
		}
		
		if (!isValidateName) {
			showValidateErrorDialog(getString(R.string.account_name_empty));
		} else if (!isValidateBirthday) {
			showValidateErrorDialog(getString(R.string.birghtday_error));
		}else if (validatePsw == AccountRegisterUtil.PASSWORD_ENPTY) {
			showValidateErrorDialog(getString(R.string.validate_psw_empty_error));
		} else if (validatePsw == AccountRegisterUtil.PASSWORD_NOT_MATCH) {
			showValidateErrorDialog(getString(R.string.validate_psw_match_error));
		} else if (validatePsw == AccountRegisterUtil.PASSWORD_MIN_LENGTH) {
			showValidateErrorDialog(getString(R.string.validate_psw_empty_error));
		}  else {
			SaveUserInfo();
			Intent intentNext = new Intent(RegisterActivity.this,
					TermsActivity.class);
			startActivity(intentNext);
		}
	}

	@Override
	public void onFocusChange(View view, boolean flag) {
		hideHint(view, flag);
	}

	/**
	 * ����hint
	 * 
	 * @param arg0
	 * @param hasFocus
	 */
	private void hideHint(View view, Boolean hasFocus) {
		EditText textView = (EditText) view;
		String hint;
		if (hasFocus) {
			hint = textView.getHint().toString();
			textView.setTag(hint);
			textView.setHint("");
			
		} else {
			hint = textView.getTag().toString();
			textView.setHint(hint);
		}
	}
	
	private void showBrithdayEditPop(View anchor, View popContentView) {
		if (null == anchor) return;
		hideGenderEditPop();
		Resources resources = getResources(); 
        if (mBirthdayPop == null) {
        	if (popContentView != null) {
        		int width = (int) resources.getDimension(R.dimen.birthday_pop_width);
        		int height = (int) resources.getDimension(R.dimen.birthday_pop_height);
        		mBirthdayPop = new PopupWindow(popContentView);
        		mBirthdayPop.setWidth(width);
        		mBirthdayPop.setHeight(height);
        		mBirthdayPop.setFocusable(true);
        	    //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        		mBirthdayPop.setBackgroundDrawable(new BitmapDrawable());  
        		mBirthdayPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        		mBirthdayPop.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						setBirthdayText();
						isShowBirthdayPop = false;
					}
				});
        	}
        }
        int[] locations = new int[2];  
        anchor.getLocationOnScreen(locations);  
        int x = locations[0];//获取组件当前位置的横坐标  
        int y = locations[1];//获取组件当前位置的纵坐标  
        int marginY = (int) resources.getDimension(R.dimen.brithday_edit_pop_show_y);
        mBirthdayPop.showAtLocation(anchor, Gravity.LEFT, x, marginY);
        //mBirthdayPop.showAsDropDown(anchor,0,0);
	}
	
	private void showGenderEditPop(View anchor, View popContentView) {
		if (null == anchor) return;
		hideBrithdayEditPop();
		Resources resources = getResources(); 
        if (mGenderPop == null) {
        	if (popContentView != null) {
        		int width = (int) resources.getDimension(R.dimen.birthday_pop_width);
        		int height = (int) resources.getDimension(R.dimen.birthday_pop_height);
        		mGenderPop = new PopupWindow(popContentView);
        		mGenderPop.setWidth(width);
        		mGenderPop.setHeight(height);
        		mGenderPop.setFocusable(true);
        		mGenderPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        		mGenderPop.setBackgroundDrawable(new BitmapDrawable()); 
        		mGenderPop.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						isShowGenderPop = false;
					}
				});
        	}
        }
        int[] locations = new int[2];  
        anchor.getLocationOnScreen(locations);  
        int x = locations[0];//获取组件当前位置的横坐标  
        int y = locations[1];//获取组件当前位置的纵坐标  
        int marginY = (int) resources.getDimension(R.dimen.gender_edit_pop_show_y);
        mGenderPop.showAtLocation(anchor, Gravity.LEFT, x, anchor.getHeight() + marginY);
        //mGenderPop.showAsDropDown(anchor);
        isShowGenderPop = true;
	}
	
	private void showPhoneEditPop(View anchor, View popContentView,boolean isFocusable) {
		if (null == anchor) return;
		Resources resources = getResources();
		if (mPhonePop == null) {
			if (popContentView != null) {
				int width = (int) resources.getDimension(R.dimen.birthday_pop_width);
        		int height = (int) resources.getDimension(R.dimen.birthday_pop_height);
        		mEditTextVerificationCode.setFocusable(false);
        		mPhonePop = new PopupWindow(popContentView);
        		mPhonePop.setWidth(width);
        		mPhonePop.setHeight(height);
        		mPhonePop.setBackgroundDrawable(new BitmapDrawable());
        		mPhonePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        		
        		mPhonePop.setTouchable(true);
        		//mPhonePop.setFocusable(true);
        		mPhonePop.setFocusable(true);
        		
        		mPhonePop.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						isShowPhonePop = false;
						ConfirmVerificationCode();
					}
				});
			}
		}
		//mPhonePop.setFocusable(isFocusable);
		int[] locations = new int[2];  
        anchor.getLocationOnScreen(locations);  
        int x = locations[0];//获取组件当前位置的横坐标  
        int y = locations[1];//获取组件当前位置的纵坐标  
        int marginY = (int) resources.getDimension(R.dimen.gender_edit_pop_show_y);
        mPhonePop.showAtLocation(anchor, Gravity.LEFT, x, anchor.getHeight() + marginY);
	}
	
	public static void setPopupWindowTouchModal(PopupWindow popupWindow,
            boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {

            method = PopupWindow.class.getDeclaredMethod("setTouchModal",
                    boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	protected void ConfirmVerificationCode() {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(mEditTextVerificationCode.getText().toString())) {
			if (mVerificationCode != 0) {
				if (!(mVerificationCode == Integer.parseInt(mEditTextVerificationCode.getText().toString()))) {
					showValidateErrorDialog(getString(R.string.verification_code_err));
					mEditTextVerificationCode.setText("");
					mVerificationCodeRight = false;
				}else {
					mVerificationCodeRight = true;
				}
			}
		}
	}

	private void hidePhoneEditPop() {
		if (mPhonePop != null) {
			if (mPhonePop.isShowing()) {
				mPhonePop.dismiss();
				ConfirmVerificationCode();
			}
			isShowPhonePop = false;
		}
	}
	
	private void hideBrithdayEditPop() {
		if (mBirthdayPop != null) {
			if (mBirthdayPop.isShowing()) {
				mBirthdayPop.dismiss();
			}
			//mBirthdayPop = null;
			isShowBirthdayPop = false;
		}
	}
	
	private void hideGenderEditPop() {
		if (mGenderPop != null) {
			if (mGenderPop.isShowing()) {
				mGenderPop.dismiss();
			}
			//mGenderPop = null;
			isShowGenderPop = false;
		}
	}
	
	private void initBrithdayEditView() {
		mBirthdayPopContentView = LayoutInflater.from(this).inflate(R.layout.birthday_pop_layout, null);
		mEditTextDay = (EditText) mBirthdayPopContentView.findViewById(R.id.et_day);
		mEditTextMonth = (EditText) mBirthdayPopContentView.findViewById(R.id.et_month);
		mEditTextYear = (EditText) mBirthdayPopContentView.findViewById(R.id.et_year);
		mEditTextDay.setOnFocusChangeListener(this);
		mEditTextMonth.setOnFocusChangeListener(this);
		mEditTextYear.setOnFocusChangeListener(this);
	}
	
	private void setBirthdayText() {
		 if (!TextUtils.isEmpty(mEditTextYear.getText().toString())
				&& !TextUtils.isEmpty(mEditTextMonth.getText().toString())
				&& !TextUtils.isEmpty(mEditTextDay.getText().toString())) {
			 
			String birthday = mEditTextYear.getText().toString() + "-"
					+ mEditTextMonth.getText().toString() + "-"
					+ mEditTextDay.getText().toString();
			boolean validateBirthday = AccountRegisterUtil.validateBirthday(birthday);
			if (!validateBirthday) {
				showValidateErrorDialog(getString(R.string.birghtday_error));
				mEditTextYear.setText("");
				mEditTextMonth.setText("");
				mEditTextDay.setText("");
			} else {
				mTextBirthday.setText(birthday);
			}
		}

	}
	
	private void initGenderEditView() {
		mGenderPopContentView = LayoutInflater.from(this).inflate(R.layout.gender_pop_layout, null);
		RadioGroup genderSelect = (RadioGroup) mGenderPopContentView.findViewById(R.id.rg_gender_select);
		genderSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.boy_radio) {
					mTextGender.setText(R.string.boy_text);
					hideGenderEditPop();
				} else if (checkedId == R.id.girl_radio) {
					mTextGender.setText(R.string.girl_text);
					hideGenderEditPop();
				}
			}
		});
	}
	
	private void initPhoneVerificationView(){
		mPhonePopContentView = LayoutInflater.from(this).inflate(R.layout.phone_pop_layout, null);
		mEditTextVerificationCode = (EditText) mPhonePopContentView.findViewById(R.id.et_register_code_id);
		mBtnSendVerificationCode = (Button) mPhonePopContentView.findViewById(R.id.bt_send_code_id);
		mEditTextVerificationCode.setOnFocusChangeListener(this);
		mBtnSendVerificationCode.setOnClickListener(this);
		time = new TimeCount(defTime, 1000);
	}
	

	private void showValidateErrorDialog(String message) {
		dismissValidateErrorDialog();
		if (null == message || TextUtils.isEmpty(message)) {
			return;
		}
		
		View contentView = LayoutInflater.from(this).inflate(R.layout.regist_dialog_layout, null);
		if (contentView != null) {
			TextView messgeTextView = (TextView)contentView.findViewById(R.id.messge);
			messgeTextView.setText(message);
			Button close = (Button)contentView.findViewById(R.id.close_btn);
			close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismissValidateErrorDialog();
				}
			});
			mRegisterGlobalDialog = new RegisterGlobalDialog.Builder(this)
			.setContentView(contentView)
			.setCancelable(true).create();
			mRegisterGlobalDialog.setCanceledOnTouchOutside(true);
			mRegisterGlobalDialog.show();
		}
		
	}
	
	private void dismissValidateErrorDialog() {
		if (mRegisterGlobalDialog != null) {
    		if (mRegisterGlobalDialog.isShowing()) {
    			mRegisterGlobalDialog.dismiss();
    			mRegisterGlobalDialog = null;
    		}
    	}
	}
	
	private void sendVerifyCode(final String username) {
		if(HttpUtils.isNetworkAvailable(this)){
		  new Thread(new Runnable() {
			@Override
			public void run() {
				String url = HttpUtils.GET_VERIFY_CODE+"&username="+username+"&purpose=1";
				try {
					String result = HttpUtils.getRequest(url,RegisterActivity.this);
					Log.e("xiaolp","result = "+result);
					if (result != null) {
						JsonParse(result);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		}else{
			showValidateErrorDialog(getString(R.string.not_network));
		}
	}

	private void JsonParse(String json) {
		try {
			JSONObject person = new JSONObject(json);
			boolean state = person.getBoolean("state");
			Log.e("xiaolp","state = "+state);
			if (state) {
				mVerificationCode = person.getInt("captcha_code");
				Log.e("xiaolp","mVerificationCode = "+mVerificationCode);
			}else {
				mVerificationCode = 0;
			}
		} catch (JSONException e) {
			mVerificationCode = 0;
			e.printStackTrace();
		}
	}
	
	class TimeCount extends CountDownTimer {
		 
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
 
        @Override
        public void onTick(long millisUntilFinished) {
        	mBtnSendVerificationCode.setBackgroundResource(R.drawable.time_out);
        	mBtnSendVerificationCode.setClickable(false);
        	mBtnSendVerificationCode.setTextColor(Color.WHITE);
        	mBtnSendVerificationCode.setText(millisUntilFinished / 1000+"\t"+getResources().getString(R.string.sec));
        }
 
        @Override
        public void onFinish() {
        	mBtnSendVerificationCode.setText("");
        	mBtnSendVerificationCode.setBackgroundResource(R.drawable.but_resend_verification);
            mBtnSendVerificationCode.setClickable(true);
        }
    }
	
	class EditChangedListener implements TextWatcher {
		private CharSequence temp;//监听前的文本  
	    private int editStart;//光标开始位置  
	    private int editEnd;//光标结束位置  
	    private final int charMaxNum = 16;
		
		@Override
		public void afterTextChanged(Editable s) {
			/** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */  
           editStart = mEditTextSetPWD.getSelectionStart();  
           editEnd = mEditTextSetPWD.getSelectionEnd();  
           if (temp.length() > charMaxNum) {
        	   showValidateErrorDialog(getString(R.string.validate_psw_empty_error));
               s.delete(editStart - 1, editEnd);  
               int tempSelection = editStart;  
               mEditTextSetPWD.setText(s);  
               mEditTextSetPWD.setSelection(tempSelection);  
           }
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
	
	class EditChangedListener2 implements TextWatcher {
		private CharSequence temp;//监听前的文本  
	    private int editStart;//光标开始位置  
	    private int editEnd;//光标结束位置  
	    private final int charMaxNum = 16;
		
		@Override
		public void afterTextChanged(Editable s) {
			/** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */  
           editStart = mEditTextConfirmPWD.getSelectionStart();  
           editEnd = mEditTextConfirmPWD.getSelectionEnd();  
           if (temp.length() > charMaxNum) { 
        	   showValidateErrorDialog(getString(R.string.validate_psw_empty_error));
               s.delete(editStart - 1, editEnd);  
               int tempSelection = editStart;  
               mEditTextConfirmPWD.setText(s);  
               mEditTextConfirmPWD.setSelection(tempSelection);  
           }
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
