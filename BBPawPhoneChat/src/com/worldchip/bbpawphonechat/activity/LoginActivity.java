package com.worldchip.bbpawphonechat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.application.MyChatHXSDKHelper;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.db.HabitDao;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.dialog.MyProgressDialog;
import com.worldchip.bbpawphonechat.entity.HabitCategory;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG = "CHRIS";
	private EditText mEtUserName, mEtPassWord;
	private Button mBtnLogin;
	private TextView mTvRegister, mTvForgetPass;
	private ImageView mIvClearEditAccount, mIvLoginPswVisiable,
			mIvLoginPswInvisiable;
	private RelativeLayout mRlLoginClearUserName, mRlLoginPswVisiable;

	private boolean mPswIsVisible = false;

	private String mEditUsername;
	private String mLoginName;
	private String currentPassword;

	private boolean progressShow;
	private TextView tv_login;
	private TextView tv_register_account;
	private TextView tv_register_password;

	private boolean accountIsCurrent, ishasPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 如果用户名密码都有，直接进入主页面
		if (MyChatHXSDKHelper.getInstance().isLogined()) {
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			return;
		}
		setContentView(R.layout.activity_login);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mTvRegister = (TextView) findViewById(R.id.btn_login_activity_register);
		mTvForgetPass = (TextView) findViewById(R.id.btn_login_activity_forget_password);
		mEtUserName = (EditText) findViewById(R.id.et_login_user_name);
		mEtPassWord = (EditText) findViewById(R.id.et_login_pass_word);
		
		mIvClearEditAccount = (ImageView) findViewById(R.id.iv_delete_edit_login_name);
		mIvLoginPswVisiable = (ImageView) findViewById(R.id.iv_login_psw_visiable);
		mIvLoginPswInvisiable = (ImageView) findViewById(R.id.iv_login_psw_invisiable);
		mRlLoginClearUserName = (RelativeLayout) findViewById(R.id.rl_login_delect_username_img);
		mRlLoginPswVisiable = (RelativeLayout) findViewById(R.id.rl_login_psw_visiable_img);

		tv_login = (TextView) findViewById(R.id.login);
		tv_register_account = (TextView) findViewById(R.id.tv_register_account);
		tv_register_password = (TextView) findViewById(R.id.tv_register_password);

		imageAdapter();

		mBtnLogin.setOnClickListener(this);
		mTvRegister.setOnClickListener(this);
		mTvForgetPass.setOnClickListener(this);
		mRlLoginClearUserName.setOnClickListener(this);
		mRlLoginPswVisiable.setOnClickListener(this);
		
		
		mBtnLogin.setEnabled(false);
		mBtnLogin.setClickable(false);
		mEtUserName.addTextChangedListener(new MyEditCourrentWatcher(mEtUserName));
		mEtPassWord.addTextChangedListener(new MyEditCourrentWatcher(mEtPassWord));

		if (MyApplication.getInstance().getUserName() != null) {
			mEtUserName.setText(MyApplication.getInstance().getUserName());
		}
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				tv_login,
				new int[] { R.drawable.login_top_title,
						R.drawable.login_top_title_es,
						R.drawable.login_top_title_en });
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
				mTvRegister,
				new int[] { R.drawable.login_register_txt_img,
						R.drawable.login_register_txt_img_es,
						R.drawable.login_register_txt_img_en });
		MyApplication.getInstance().ImageAdapter(
				mTvForgetPass,
				new int[] { R.drawable.login_forget_txt_img,
						R.drawable.login_forget_txt_img_es,
						R.drawable.login_forget_txt_img_en });
		
		MyApplication.getInstance().ImageAdapter(
				mBtnLogin,
				new int[] { R.drawable.selector_login_btn,
						R.drawable.selector_login_btn_es,
						R.drawable.selector_login_btn_en });

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			loginChat();
			break;
		case R.id.btn_login_activity_register:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_login_delect_username_img:
			mEtUserName.setText("");
			break;
		case R.id.rl_login_psw_visiable_img:
			if (mPswIsVisible) {
				mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				
				mIvLoginPswInvisiable.setVisibility(View.VISIBLE);
				mPswIsVisible = false;
			} else {
				mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_NORMAL);
				
				mIvLoginPswInvisiable.setVisibility(View.GONE);
				mPswIsVisible = true;
			}
			mEtPassWord.setSelection(mEtPassWord.getText().length());
			break;
		case R.id.btn_login_activity_forget_password:
			Intent intentForgetPsw = new Intent(this, ForgetPswActivity.class);
			startActivity(intentForgetPsw);
			break;
		default:
			break;
		}
	}

	private void loginChat() {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}
		mEditUsername = mEtUserName.getText().toString().trim();
		if (mEditUsername.contains("@")) {
			mLoginName = mEditUsername.replaceAll("@", "");
		} else {
			mLoginName = mEditUsername;
		}
		currentPassword = mEtPassWord.getText().toString().trim();
		if (TextUtils.isEmpty(mEditUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		MyApplication.currentUserNick = mEditUsername;
		progressShow = true;
		final MyProgressDialog pd = MyProgressDialog.createProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);

		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(mLoginName, currentPassword,
				new EMCallBack() {
					@Override
					public void onSuccess() {
						if (!progressShow) {
							return;
						}
						// 登陆成功，保存用户名密码
						MyApplication.getInstance().setUserName(mEditUsername);
						MyApplication.getInstance().setPassword(currentPassword);
						runOnUiThread(new Runnable() {
							public void run() {
								pd.setMessage(getString(R.string.list_is_for));
							}
						});
						try {
							EMChatManager.getInstance().loadAllConversations();
							initHabitData();
							processContactsAndGroups();
						} catch (Exception e) {
							e.printStackTrace();
							// 取好友或者群聊失败，不让进入主页面
							runOnUiThread(new Runnable() {
								public void run() {
									pd.dismiss();
									MyApplication.getInstance().logout(null);
									Toast.makeText(getApplicationContext(),
											R.string.login_failure_failed,
											Toast.LENGTH_LONG).show();
								}
							});
							return;
						}
						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
						boolean updatenick = EMChatManager.getInstance()
								.updateCurrentUserNick(
										MyApplication.currentUserNick.trim());
						if (!updatenick) {
							Log.e("LoginActivity",
									"update current user nick fail");
						}
						if (!LoginActivity.this.isFinishing())
							pd.dismiss();
						// 进入主页面
						startActivity(new Intent(LoginActivity.this,
								MainActivity.class));
						finish();
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
						if (!progressShow) {
							return;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								pd.dismiss();
								Toast.makeText(getApplicationContext(),
										getString(R.string.Login_failed),
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
	}

	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> usernames = EMContactManager.getInstance()
				.getContactUserNames();
		EMLog.d("roster", "contacts size: " + usernames.size());
		Map<String, User> userlist = new HashMap<String, User>();
		for (String username : usernames) {
			User user = new User();
			user.setUsername(username);
			setUserHearder(username, user);
			userlist.put(username, user);
		}
		MyApplication.getInstance().setContactList(userlist);
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	private int[] codeIds = { 10001, 10002, 10003, 10004, 10005, 10006, 10007,
			10008, 10009 };

	// Create Habit database
	private void initHabitData() {
		List<HabitCategory> mhaHabitCategories = new ArrayList<HabitCategory>();
		for (int i = 0; i < codeIds.length; i++) {
			HabitCategory category = new HabitCategory(codeIds[i], 0);
			mhaHabitCategories.add(category);
		}
		HabitDao habitDao = new HabitDao(this);
		habitDao.initHabitData(mhaHabitCategories);
		Log.i(TAG, "---初始化habitDataBase--------initHabitData----");
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(MyComment.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
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
			if(mEdittext == mEtUserName){
				if(CommonUtils.isMobileNo(editContent) || CommonUtils.isMailAddress(editContent)){
					accountIsCurrent = true;
				}else{
					accountIsCurrent = false;
				}
			}else if(mEdittext == mEtPassWord){
				if(editContent.length() > 0 ){
					ishasPassword = true;
				}else{
					ishasPassword = false;
				}
			}
		}
		@Override
		public void afterTextChanged(Editable s) {	
		  if(accountIsCurrent && ishasPassword ){
			   mBtnLogin.setEnabled(true);
			   mBtnLogin.setClickable(true);
		 }else{
			   mBtnLogin.setEnabled(false);
			   mBtnLogin.setClickable(false);
		 }
	  }
	}
}
