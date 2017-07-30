package com.worldchip.bbpawphonechat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.MyFragmentPagerAdapter;
import com.worldchip.bbpawphonechat.application.HXSDKHelper;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.application.MyChatHXSDKHelper;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.db.InviteMessgeDao;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.dialog.MyAlertDialog;
import com.worldchip.bbpawphonechat.dialog.MyProgressDialog;
import com.worldchip.bbpawphonechat.entity.InviteMessage;
import com.worldchip.bbpawphonechat.entity.InviteMessage.InviteMesageStatus;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.fragment.CareForFragment;
import com.worldchip.bbpawphonechat.fragment.ChatSettingFragment;
import com.worldchip.bbpawphonechat.fragment.ChatSettingFragment.MyLogoutListen;
import com.worldchip.bbpawphonechat.fragment.ContactListFragment;
import com.worldchip.bbpawphonechat.fragment.HabitFragment;
import com.worldchip.bbpawphonechat.fragment.LearnLogFragment;
import com.worldchip.bbpawphonechat.task.MyRunable;
import com.worldchip.bbpawphonechat.utils.CommonUtils;
import com.worldchip.bbpawphonechat.view.SystemStatusManager;

public class MainActivity extends SlidingFragmentActivity implements
		EMEventListener {
	public static final String TAG = "MainActivity";

	private ViewPager mViewPager;
	private List<Fragment> mFragmentList;
	
	private ImageView mIvChatTab, mIvHabitTab, mIvWatchTab, mIvlearnLogTab;
	private RelativeLayout mRlChatTab, mRlHabitTab, mRlWatchTab,
			mRllearnLogTab;

	private List<ImageView> mTabImages;
	private List<RelativeLayout> mRltabs;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除

	private String mAddUserName = "";
	private String mAddNickName = "";
	
	
	private MyProgressDialog mMyprogressDialog = null;

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	private ContactListFragment chatFragment;
	private HabitFragment habitFragment;
	private CareForFragment careforFragment;
	private LearnLogFragment learnLogFragment;

	private int mCurrentIndex = 0;

	private List<User> contactsList = new ArrayList<User>();
	
	private int mHeardId = 0;
	private int urlId = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(MyComment.ACCOUNT_REMOVED,
						false)) {
			MyApplication.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false)) {
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}

		if (getIntent().getBooleanExtra("conflict", false)
				&& !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent()
				.getBooleanExtra(MyComment.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
		setTranslucentStatus() ;
		setContentView(R.layout.activity_phone_main);
		initSlidingMenu();
		initImageView();
		initData();
		
	}
	
	 // 需要setContentView之前调用
    private void setTranslucentStatus() {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             // 透明状态栏
              getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
               // 透明导航栏
                getWindow().addFlags(
                     WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    SystemStatusManager tintManager = new SystemStatusManager(this);
                   tintManager.setStatusBarTintEnabled(true);
                    // 设置状态栏的颜色 
                    tintManager.setStatusBarTintResource(R.color.wangshuang_color);
                        getWindow().getDecorView().setFitsSystemWindows(true);
    }
}
	private void initSlidingMenu() {
		// TODO Auto-generated method stub
		setBehindContentView(R.layout.left_menu_frame);
		Fragment leftMenuFragment = new ChatSettingFragment(this,
				new MyLogoutListen() {
					@Override
					public void isLogout() {
						EMChatManager.getInstance().unregisterEventListener(MainActivity.this);
						Intent intent = new Intent(MainActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
					}
				});

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
		SlidingMenu menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);

	}

	public void showLeftMenu(View view) {
		getSlidingMenu().showMenu();
	}

	private void initImageView() {
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mIvChatTab = (ImageView) findViewById(R.id.iv_main_chat_tab1);
		mIvHabitTab = (ImageView) findViewById(R.id.iv_main_habit_tab2);
		mIvWatchTab = (ImageView) findViewById(R.id.iv_main_watch_tab3);
		mIvlearnLogTab = (ImageView) findViewById(R.id.iv_main_learnlog_tab4);

		mRlChatTab = (RelativeLayout) findViewById(R.id.Rl_main_chat_tab1);
		mRlHabitTab = (RelativeLayout) findViewById(R.id.Rl_main_chat_tab2);
		mRlWatchTab = (RelativeLayout) findViewById(R.id.Rl_main_chat_tab3);
		mRllearnLogTab = (RelativeLayout) findViewById(R.id.Rl_main_chat_tab4);

		mRlChatTab.setOnClickListener(new MyTabClickListener(0));
		mRlHabitTab.setOnClickListener(new MyTabClickListener(1));
		mRlWatchTab.setOnClickListener(new MyTabClickListener(2));
		mRllearnLogTab.setOnClickListener(new MyTabClickListener(3));

		mTabImages = new ArrayList<ImageView>();
		mRltabs = new ArrayList<RelativeLayout>();

		mTabImages.add(mIvChatTab);
		mTabImages.add(mIvHabitTab);
		mTabImages.add(mIvWatchTab);
		mTabImages.add(mIvlearnLogTab);

		mRltabs.add(mRlChatTab);
		mRltabs.add(mRlHabitTab);
		mRltabs.add(mRlWatchTab);
		mRltabs.add(mRllearnLogTab);

		mFragmentList = new ArrayList<Fragment>();

		chatFragment = new ContactListFragment(this);
		habitFragment = new HabitFragment(this);
		careforFragment = new CareForFragment(this);
		learnLogFragment = new LearnLogFragment(this);

		mFragmentList.add(chatFragment);
		mFragmentList.add(habitFragment);
		mFragmentList.add(careforFragment);
		mFragmentList.add(learnLogFragment);

		mViewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), mFragmentList));
		
		mViewPager.setCurrentItem(0);
		mCurrentIndex = 0;

		mIvChatTab.setSelected(true);
		mRlChatTab.setSelected(true);

		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	//初始化数据
	private void initData() {
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		initMyContactList();
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyChatHXSDKHelper sdkHelper = (MyChatHXSDKHelper) MyChatHXSDKHelper
				.getInstance();
		sdkHelper.pushActivity(this);
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventReadAck });
		
		MyApplication.getInstance().ImageAdapter(
				mIvChatTab,
				new int[] { R.drawable.selector_main_view_tab1_src,
						R.drawable.selector_main_view_tab1_src_es,
						R.drawable.selector_main_view_tab1_src_en });
		
		MyApplication.getInstance().ImageAdapter(
				mIvHabitTab,
				new int[] { R.drawable.selector_main_view_tab2_src,
						R.drawable.selector_main_view_tab2_src_es,
						R.drawable.selector_main_view_tab2_src_en });
		MyApplication.getInstance().ImageAdapter(
				mIvWatchTab,
				new int[] { R.drawable.selector_main_view_tab3_src,
						R.drawable.selector_main_view_tab3_src_es,
						R.drawable.selector_main_view_tab3_src_en });
		MyApplication.getInstance().ImageAdapter(
				mIvlearnLogTab,
				new int[] { R.drawable.selector_main_view_tab4_src,
						R.drawable.selector_main_view_tab4_src_es,
						R.drawable.selector_main_view_tab4_src_en });
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		MyChatHXSDKHelper sdkHelper = (MyChatHXSDKHelper) MyChatHXSDKHelper
				.getInstance();
		sdkHelper.popActivity(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onDestroy();
	}

	private class MyTabClickListener implements OnClickListener {

		private int selectIndex = 0;

		public MyTabClickListener(int i) {
			selectIndex = i;
		}

		@Override
		public void onClick(View arg0) {
			mViewPager.setCurrentItem(selectIndex);
		}

	}

	private class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int item) {
			mCurrentIndex = item;
			for (int i = 0; i < mTabImages.size(); i++) {
				if (i == item) {
					mTabImages.get(i).setSelected(true);
					mRltabs.get(i).setSelected(true);
				} else {
					mTabImages.get(i).setSelected(false);
					mRltabs.get(i).setSelected(false);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MyComment.START_SCAN_ADD_FRIEND:
			mViewPager.setCurrentItem(0);
			if (resultCode == RESULT_OK) {
				//mAddUserName = data.getExtras().getString("username");
				//mAddNickName = data.getExtras().getString("nickname");
				Bundle  mBundle = data.getExtras();
				mAddUserName = mBundle.getString("username");
				mAddNickName = mBundle.getString("nickname");
				MySharePreData.SetData(this, MyComment.CHAT_SP_NAME, "chat_to",
						mAddUserName);
				alertDialogLockPaid(getString(R.string.add_this_friend),
						mAddNickName);
			}else{
			}

			break;
		default:

			break;
		}
	}

	private void alertDialogLockPaid(String title, String message) {
		final MyAlertDialog mAd = new MyAlertDialog(MainActivity.this);
		mAd.setTitle(title);
		mAd.setMessage(message);
		mAd.setPositiveButton(getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAd.dismiss();
				addContact();
			}
		});
		mAd.setNegativeButton(getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mAd.dismiss();
						Toast.makeText(MainActivity.this,
								getString(R.string.cancel), Toast.LENGTH_LONG)
								.show();
					}
				});
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact() {
		if (MyApplication.getInstance().getContactList()
				.containsKey(mAddUserName)) {
			Toast.makeText(this, R.string.this_is_your_friend,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (mMyprogressDialog == null) {
			mMyprogressDialog = MyProgressDialog.createProgressDialog(this);
			mMyprogressDialog
					.setMessage(getString(R.string.Is_sending_a_request));
			mMyprogressDialog.show();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					EMContactManager.getInstance().addContact(mAddUserName, s);
					runOnUiThread(new Runnable() {
						public void run() {
							mMyprogressDialog.dismiss();
							String s1 = getResources().getString(
									R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, 1)
									.show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							mMyprogressDialog.dismiss();
							String s2 = getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(),
									s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	// 实现ConnectionListener接口，监听连接状态
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			Toast.makeText(MainActivity.this, "环信已经登陆！", Toast.LENGTH_LONG)
					.show();
		}
		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						Toast.makeText(MainActivity.this, R.string.setting_account_exit,
								Toast.LENGTH_LONG).show();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆
						Toast.makeText(MainActivity.this, R.string.setting_account_other_place,
								Toast.LENGTH_LONG).show();
					} else {
						if (NetUtils.hasNetwork(MainActivity.this)) {
							// 连接不到聊天服务器
							Toast.makeText(MainActivity.this, R.string.setting_account_not_serve,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, R.string.setting_account_not_net,
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});
		}
	}

	/***
	 * 好友变化listener
	 */
	private class MyContactListener implements EMContactListener {
		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, User> localUsers = MyApplication.getInstance()
					.getContactList();
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = setUserHead(username);
				user.setNick(mAddNickName);
				user.setRemark_name("");
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			if (mCurrentIndex == 0)
				chatFragment.refresh();
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = MyApplication.getInstance()
					.getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			chatFragment.refresh();
		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null
						&& inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);
			chatFragment.refresh();
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		acceptInvitation(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
		saveInviteMsg(msg);
	}
	
	/**
	 * 同意好友请求或者群申请
	 */
	private void acceptInvitation(final InviteMessage msg) {
		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					EMChatManager.getInstance().acceptInvitation(msg.getFrom());
				} catch (final Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = MyApplication.getInstance().getContactList()
				.get(MyComment.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}
	
	
	/**
	 * 接收到已经删除的好友的新消息，
	 * 发送透传消息
	 * 提示对方已经删除好友
	 */
   private void  sendCmdMessage(String toname){
	 EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
	 String action="deleted";//action可以自定义，在广播接收时可以收到
	 CmdMessageBody cmdBody=new CmdMessageBody(action);
	 cmdMsg.setReceipt(toname);
	 cmdMsg.addBody(cmdBody); 
	 EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
		@Override
		public void onSuccess() {
			System.out.println("-----sendCmdMessage---onSuccess-");
		}
		
		@Override
		public void onProgress(int arg0, String arg1) {
			System.out.println("-----sendCmdMessage---onProgress-");
		}
		
		@Override
		public void onError(int arg0, String arg1) {
			System.out.println("-----sendCmdMessage---onError-");
		}
	 });
   }
	

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();
			String newMessageFrom =  message.getFrom();
			boolean noContactFlag = false;
			for (int i = 0; i < contactsList.size(); i++) {
				if(newMessageFrom.equals(contactsList.get(i).getUsername())){
					// 提示新消息
					HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
					noContactFlag = true;
					if (mCurrentIndex == 0)
						chatFragment.refreshMessage();
				}
			}
			if(!noContactFlag){
				sendCmdMessage(newMessageFrom);
			}
			break;
		}

		case EventOfflineMessage: {
			// refreshUI();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		MyApplication.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								conflictBuilder = null;
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
								finish();
							}
						});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		MyApplication.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(
							MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								accountRemovedBuilder = null;
								finish();
								startActivity(new Intent(MainActivity.this,
										LoginActivity.class));
							}
						});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				// isCurrentAccountRemoved = true;
			} catch (Exception e) {
			}

		}
	}

	/**
	 * set head
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
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
		return user;
	}

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	private void initMyContactList() {
		if (contactsList.size() > 0) {
			contactsList.removeAll(contactsList);
			contactsList.clear();
		}
		// 获取本地好友列表
		Map<String, User> users = MyApplication.getInstance().getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			if (!entry.getKey().equals(MyComment.NEW_FRIENDS_USERNAME)
					&& !entry.getKey().equals(MyComment.GROUP_USERNAME))
				contactsList.add(entry.getValue());
		}
		getheadAddressJson(contactsList);
		changeContactNickName(contactsList);
	}

	private void getheadAddressJson(final List<User> contacts) {
		if (contacts == null || !CommonUtils.isNetWorkConnected(this)) {
			return;
		}
		for (int i = 0; i < contacts.size(); i++) {
			new Thread(new MyRunable(i, contacts, MainActivity.this, true)).start();
		}
	}

	private void changeContactNickName(final List<User> contacts) {
		if (CommonUtils.isNetWorkConnected(this)) {
			if (contacts != null && contacts.size() > 0) {
				for (int i = 0; i < contacts.size(); i++) {
					new Thread(new MyRunable(i, contacts, MainActivity.this, false)).start();
				}
			}
		}
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
