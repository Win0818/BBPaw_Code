package com.worldchip.bbpawphonechat.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.BBPawChatMessageAdapter;
import com.worldchip.bbpawphonechat.adapter.ExpressionAdapter;
import com.worldchip.bbpawphonechat.adapter.ExpressionPagerAdapter;
import com.worldchip.bbpawphonechat.adapter.VoicePlayClickListener;
import com.worldchip.bbpawphonechat.application.HXSDKHelper;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.application.MyChatHXSDKHelper;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.dialog.ClearMessageDialog;
import com.worldchip.bbpawphonechat.utils.CommonUtils;
import com.worldchip.bbpawphonechat.utils.SmileUtils;

public class PhoneChatActivity extends Activity implements OnClickListener,
		EMEventListener {
	private static String TAG = "PhoneChatActivity";
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;

	private InputMethodManager mInputMange;
	// 聊天内容的listView适配器
	private BBPawChatMessageAdapter mMessageAdapter;
	// 聊天数据的控制
	private EMConversation mConversation;
	private String mToChatUsername = null;
	private String mChattoheadUrl = null;
	private String mChattoNick = null;

	private final int pagesize = 20;
	public String playMsgId;
	private File cameraFile;
	private VoiceRecorder voiceRecorder;
	private PowerManager.WakeLock wakeLock;

	private boolean isloading = false;
	private boolean haveMoreData = true;

	private ProgressBar loadmorePB;

	private ListView mLvChatContents;
	private Button mBtnSendMore, mBtnSendVoice, mBtnSendEmotion, mBtnArrow,
			mBtnEditText, mBtnSendMessage;

	private ImageView mIvShowClearBar;
	private RelativeLayout mRlClearBar;
	private Button mBtnClearMessage;
	private ImageView mIvBack;

	private EditText mEditMessage;
	private RelativeLayout RlEdittext;
    private RelativeLayout mRLSelectEmotions;
	private LinearLayout mBottomNomalRl;
	private ViewPager expressionViewpager;
	private List<String> reslist;
	private TextView noteDeleted;
	

	private LinearLayout mMoreLinearLayout, mPressRecordLinearLayout;
	private ImageView mIvPressRecordVoice;
	private TextView  mIvRecordVoiceText;
	private RelativeLayout mArrowRelaLayout;
	private FrameLayout mRecordVoiceFLayout;
	private AnimationDrawable mRecorderAnimationDrawable;
	private ImageView mIvVoiceRecorderAnim;

	private LinearLayout mSendMorecontentLayout;
	private LinearLayout emojiIconContainer;
	private ImageView mIvSelectPicture, mIvTakePhoto;
	private TextView mTvChatToName;

	private ClipboardManager clipboard;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyComment.CLEAR_SOMEONE_ALL_MESSAGE:
				afterClearMessage();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_chat);
		initView();
	}

	private void initView() {
		// 聊天界面的相关UI
		mLvChatContents = (ListView) findViewById(R.id.lv_chat_contents);
		mBtnSendMore = (Button) findViewById(R.id.chat_view_btn_more);
		mBtnSendVoice = (Button) findViewById(R.id.btn_to_record_voice);
		mBtnSendEmotion = (Button) findViewById(R.id.btn_select_emoticons);
		mEditMessage = (EditText) findViewById(R.id.et_sendmessage);
		mBtnSendMessage = (Button) findViewById(R.id.btn_send);
		mTvChatToName = (TextView) findViewById(R.id.chat_to_name);
		noteDeleted = (TextView) findViewById(R.id.tv_isdeleted_by_other);

		mBottomNomalRl = (LinearLayout) findViewById(R.id.ll_bottom_bar);
		mMoreLinearLayout = (LinearLayout) findViewById(R.id.ll_select_more);
		mArrowRelaLayout = (RelativeLayout) findViewById(R.id.Rl_btn_arrow);
		RlEdittext = (RelativeLayout) findViewById(R.id.rl_et_text_message);
		mRLSelectEmotions = (RelativeLayout)findViewById(R.id.rl_select_emotions);

		// 发送语音的相关按键
		mPressRecordLinearLayout = (LinearLayout) findViewById(R.id.ll_press_to_talk);
		mIvPressRecordVoice = (ImageView) findViewById(R.id.iv_press_to_record_voice);
		mIvRecordVoiceText = (TextView) findViewById(R.id.iv_press_to_record_voice_text);
		mBtnArrow = (Button) findViewById(R.id.chat_view_btn_arrow);
		mBtnEditText = (Button) findViewById(R.id.btn_to_edit_text_message);
		mRecordVoiceFLayout = (FrameLayout) findViewById(R.id.fl_recored_voice);
		mIvVoiceRecorderAnim = (ImageView) findViewById(R.id.iv_volume_icon);

		// 选择图片和拍照，表情的相关的UI
		mSendMorecontentLayout = (LinearLayout) findViewById(R.id.ll_btn_container);
		emojiIconContainer  = (LinearLayout) findViewById(R.id.ll_face_container);
		mIvSelectPicture = (ImageView) findViewById(R.id.iv_select_picture);
		mIvTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);
		loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		

		// 清空聊天记录相关
		mIvShowClearBar = (ImageView) findViewById(R.id.iv_show_clear_message_view);
		mRlClearBar = (RelativeLayout) findViewById(R.id.Rl_clear_all_message);
		mBtnClearMessage = (Button) findViewById(R.id.btn_clear_message);

		MyApplication.getInstance().ImageAdapter(
				mBtnClearMessage,
				new int[] { R.drawable.delete_all_message_btn_default,
						R.drawable.delete_all_message_btn_default_es,
						R.drawable.delete_all_message_btn_default_en });
		mIvBack = (ImageView) findViewById(R.id.back);

		mBtnSendMore.setOnClickListener(this);
		mBtnSendVoice.setOnClickListener(this);
		mBtnEditText.setOnClickListener(this);
		mBtnSendMessage.setOnClickListener(this);
		mIvShowClearBar.setOnClickListener(this);
		mBtnClearMessage.setOnClickListener(this);
		mBtnArrow.setOnClickListener(this);
		mIvBack.setOnClickListener(this);
		mBtnSendEmotion.setOnClickListener(this);
		
		mBottomNomalRl.requestFocus();
		
		

		mRecorderAnimationDrawable = (AnimationDrawable) mIvVoiceRecorderAnim
				.getBackground();

		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		mEditMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					// edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});

		mEditMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMoreLinearLayout.setVisibility(View.GONE);
				mPressRecordLinearLayout.setVisibility(View.GONE);
				mBtnArrow.setVisibility(View.GONE);
				RlEdittext.setVisibility(View.VISIBLE);
				mSendMorecontentLayout.setVisibility(View.GONE);
				mBtnEditText.setVisibility(View.GONE);
				mBtnSendVoice.setVisibility(View.VISIBLE);
				mRLSelectEmotions.setVisibility(View.VISIBLE);
				mArrowRelaLayout.setVisibility(View.GONE);
			}
		});

		// 监听文字框
		mEditMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					mBtnSendMore.setVisibility(View.GONE);
					mBtnSendMessage.setVisibility(View.VISIBLE);
				} else {
					mBtnSendMore.setVisibility(View.VISIBLE);
					mBtnSendMessage.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		/**
		 * 语音相关的初始化
		 */
		voiceRecorder = new VoiceRecorder(mHandler);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

		mIvPressRecordVoice.setOnTouchListener(new PressToSpeakListen());

		mInputMange = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		String userName = MySharePreData.GetData(this, "bbpaw_chat",
				"huanxin_name");
		String passWord = MySharePreData.GetData(this, "bbpaw_chat",
				"huanxin_password");

		initContents();
		initBottomView();
		initEmotiones();
	}

	
	private void  initEmotiones(){
		if(MyApplication.getInstance().system_local_language == 0){
			reslist = getExpressionRes(31);
		}else{
			reslist = getExpressionRes(29);
		}
		// 初始化表情viewpager
	    List<View> views = new ArrayList<View>();
	    View gv1 = getChildGridView(1);
	    View gv2 = getChildGridView(2);
	    View gv3 = getChildGridView(3);
	    View gv4 = getChildGridView(4);
		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		views.add(gv4);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));		
	}

	/**
	 * 初始化底部视图
	 */
	private void initBottomView() {
		mMoreLinearLayout.setVisibility(View.GONE);
		hideKeyboard();
	}

	/*
	 * 初始化聊天对象和相关的聊天信息
	 */
	private void initContents() {
		mToChatUsername = getIntent().getStringExtra("chatto");
		mChattoheadUrl = getIntent().getStringExtra("chattoheadurl");
		mChattoNick = getIntent().getStringExtra("nick");
		// 获取聊天对象用户名
		mTvChatToName.setText(mChattoNick);
		// 获取对话对象类
		mConversation = EMChatManager.getInstance().getConversationByType(
				mToChatUsername, EMConversationType.Chat);
		// 把此会话的未读数置为0
		mConversation.markAllMessagesAsRead();
		// 加载对话信息
		final List<EMMessage> msgs = mConversation.getAllMessages();
		int msgCount = msgs != null ? msgs.size() : 0;
		if (msgCount < mConversation.getAllMsgCount() && msgCount < pagesize) {
			String msgId = null;
			if (msgs != null && msgs.size() > 0) {
				msgId = msgs.get(0).getMsgId();
			}
			mConversation.loadMoreMsgFromDB(msgId, pagesize);
		}

		mMessageAdapter = new BBPawChatMessageAdapter(PhoneChatActivity.this,
				mChattoheadUrl, mToChatUsername);

		mLvChatContents.setAdapter(mMessageAdapter);
		mLvChatContents.setOnScrollListener(new ListScrollListener());
		mMessageAdapter.refreshSelectLast();

		mLvChatContents.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				showEditText();
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		mMessageAdapter.refresh();
		MyChatHXSDKHelper sdkHelper = (MyChatHXSDKHelper) MyChatHXSDKHelper
				.getInstance();
		sdkHelper.pushActivity(this);
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventReadAck ,
						EMNotifierEvent.Event.EventNewCMDMessage
				});
	}

	@Override
	protected void onStop() {
		Log.i("ChatActivity", "onStop");
		EMChatManager.getInstance().unregisterEventListener(this);
		MyChatHXSDKHelper sdkHelper = (MyChatHXSDKHelper) MyChatHXSDKHelper
				.getInstance();
		sdkHelper.popActivity(this);
		super.onStop();
	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) mMessageAdapter.getItem(data
						.getIntExtra("position", -1)));
				clipboard.setText(((TextMessageBody) copyMsg.getBody())
						.getMessage());
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) mMessageAdapter.getItem(data
						.getIntExtra("position", -1));
				mConversation.removeMessage(deleteMsg.getMsgId());
				mMessageAdapter.refreshSeekTo(data.getIntExtra("position",
						mMessageAdapter.getCount()) - 1);
				break;
			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists()) {
					sendPicture(cameraFile.getAbsolutePath());
				}
			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_take_photo:
			selectPicFromCamera();// 点击照相图标
			break;

		case R.id.iv_select_picture:
			selectPicFromLocal(); // 点击图片图标
			break;

		case R.id.btn_to_record_voice:
			clickBtnRecordVoice();// 点击录音小图标
			break;
		case R.id.btn_send:
			String s = mEditMessage.getText().toString();
			sendText(s);
			break;
		case R.id.chat_view_btn_more:
			clickBtnSendMoreContents();// 点击更多的加号按钮
			break;
		case R.id.btn_to_edit_text_message:
			clickBtnEditPen();
			break;
		case R.id.iv_show_clear_message_view:
			if (!mIvShowClearBar.isSelected()) {
				mIvShowClearBar.setSelected(true);
				mBottomNomalRl.setVisibility(View.GONE);
				mMoreLinearLayout.setVisibility(View.GONE);
				mRlClearBar.setVisibility(View.VISIBLE);
			} else {
				mIvShowClearBar.setSelected(false);
				mRlClearBar.setVisibility(View.GONE);
				mBottomNomalRl.setVisibility(View.VISIBLE);
				mMoreLinearLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_clear_message:
			ClearMessageDialog clearMessageDialog = new ClearMessageDialog(
					mHandler, PhoneChatActivity.this);
			clearMessageDialog.show();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.chat_view_btn_arrow:
			showEditText();
			break;
		case R.id.btn_select_emoticons:
			hideKeyboard();
			if(emojiIconContainer.getVisibility() == View.VISIBLE){
				mMoreLinearLayout.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.GONE);
				mSendMorecontentLayout.setVisibility(View.GONE);
				System.out.println("---隐藏表情Paper---");
			}else{
				mMoreLinearLayout.setVisibility(View.VISIBLE);
				mSendMorecontentLayout.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.VISIBLE);
				System.out.println("---显示表情Paper---");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 事件监听 see {@link EMNotifierEvent}
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			String username = message.getFrom();
			// 如果是当前会话的消息，刷新聊天页面
			if (username.equals(getToChatUsername())) {
				refreshUIWithNewMessage();
				HXSDKHelper.getInstance().getNotifier()
						.viberateAndPlayTone(message);
			} else {
				HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			}
			break;
		}
		case EventDeliveryAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventReadAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventOfflineMessage: {
			refreshUI();
			break;
		}
		case EventNewCMDMessage:{
			    notifyDelete();
			break;
		}
		default:
			break;
		}
	}

	private void clickBtnRecordVoice() {
		hideKeyboard();
		mMoreLinearLayout.setVisibility(View.VISIBLE);
		mPressRecordLinearLayout.setVisibility(View.VISIBLE);
		mBtnArrow.setVisibility(View.VISIBLE);
		RlEdittext.setVisibility(View.GONE);
		mSendMorecontentLayout.setVisibility(View.GONE);
		mBtnEditText.setVisibility(View.VISIBLE);
		mBtnSendVoice.setVisibility(View.GONE);
		mRLSelectEmotions.setVisibility(View.GONE);
		mArrowRelaLayout.setVisibility(View.VISIBLE);
	}

	private void clickBtnEditPen() {
		mMoreLinearLayout.setVisibility(View.GONE);
		mPressRecordLinearLayout.setVisibility(View.GONE);
		mBtnArrow.setVisibility(View.GONE);
		RlEdittext.setVisibility(View.VISIBLE);
		mSendMorecontentLayout.setVisibility(View.GONE);
		mBtnEditText.setVisibility(View.GONE);
		mBtnSendVoice.setVisibility(View.VISIBLE);
		mRLSelectEmotions.setVisibility(View.VISIBLE);
		mArrowRelaLayout.setVisibility(View.GONE);
	}

	private void clickBtnSendMoreContents() {
		hideKeyboard();
		if(mSendMorecontentLayout.getVisibility() == View.VISIBLE){
			mMoreLinearLayout.setVisibility(View.GONE);
			mSendMorecontentLayout.setVisibility(View.GONE);
			emojiIconContainer.setVisibility(View.GONE);
		}else{
		   mMoreLinearLayout.setVisibility(View.VISIBLE);
		   mPressRecordLinearLayout.setVisibility(View.GONE);
		   mBtnArrow.setVisibility(View.GONE);
		   RlEdittext.setVisibility(View.VISIBLE);
		   mSendMorecontentLayout.setVisibility(View.VISIBLE);
		   mBtnEditText.setVisibility(View.GONE);
		   mBtnSendVoice.setVisibility(View.VISIBLE);
		   emojiIconContainer.setVisibility(View.GONE);
		   mArrowRelaLayout.setVisibility(View.GONE);
		}
	}

	private void afterClearMessage() {
		EMChatManager.getInstance().clearConversation(mToChatUsername);
		mMessageAdapter.refresh();
		mIvShowClearBar.setSelected(false);
		mBottomNomalRl.setVisibility(View.VISIBLE);
		mRlClearBar.setVisibility(View.GONE);
	}

	private void showEditText() {
		mMoreLinearLayout.setVisibility(View.GONE);
		mPressRecordLinearLayout.setVisibility(View.GONE);
		mBtnArrow.setVisibility(View.GONE);
		RlEdittext.setVisibility(View.VISIBLE);
		mSendMorecontentLayout.setVisibility(View.GONE);
		mBtnEditText.setVisibility(View.GONE);
		mBtnSendVoice.setVisibility(View.VISIBLE);
		mRLSelectEmotions.setVisibility(View.VISIBLE);
		mArrowRelaLayout.setVisibility(View.GONE);
	}

	/**
	 * 按住说话listener s
	 */
	class PressToSpeakListen implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(
							R.string.Send_voice_need_sdcard_support);
					Toast.makeText(PhoneChatActivity.this, st4,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener
								.stopPlayVoice();
					Toast.makeText(PhoneChatActivity.this, R.string.btn_voice,
							Toast.LENGTH_SHORT).show();
					mRecordVoiceFLayout.setVisibility(View.VISIBLE);
					voiceRecorder.startRecording(null, mToChatUsername,
							getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					mRecordVoiceFLayout.setVisibility(View.INVISIBLE);
					Toast.makeText(PhoneChatActivity.this,
							R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (!mRecorderAnimationDrawable.isRunning()) {
					mRecorderAnimationDrawable.start();
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				if (mRecorderAnimationDrawable.isRunning()) {
					mRecorderAnimationDrawable.stop();
				}
				mRecordVoiceFLayout.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					voiceRecorder.discardRecording();

				} else {
					String st1 = getResources().getString(
							R.string.Recording_without_permission);
					String st2 = getResources().getString(
							R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(
							R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(),
									voiceRecorder
											.getVoiceFileName(mToChatUsername),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2,
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(PhoneChatActivity.this, st3,
								Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			default:
				mRecordVoiceFLayout.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * listview滑动监听listener
	 */
	private class ListScrollListener implements OnScrollListener {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading
						&& haveMoreData) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					EMMessage firstMsg = mConversation.getAllMessages().get(0);
					try {
						messages = mConversation.loadMoreMsgFromDB(
								firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							mMessageAdapter.refreshSeekTo(messages.size() - 1);
						}
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;
				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	}
	
	/**
	 * 初始化表情名字
	 * @param getSum
	 * @return
	 */
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;
	}
	
	/**
	 * 初始化表情的GridView
	 * @param i
	 * @return
	 */
	public View  getChildGridView(int i){
		View view = View.inflate(this, R.layout.expression_gridview, null);
		//ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		GridView gv = (GridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if((i*8) < reslist.size()){
		    List<String> list1 = reslist.subList((i-1)*8, i*8);
		    list.addAll(list1);
		}else{
			List<String> list2 = reslist.subList((i-1)*8, reslist.size());
			list.addAll(list2);
		}
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
	    try {		
					Class clz = Class.forName("com.worldchip.bbpawphonechat.utils.SmileUtils");
					Field field = clz.getField(filename);
					sendText(SmileUtils.getSmiledText(PhoneChatActivity.this, (String) field.get(null)).toString());
		    }catch(Exception  e){
		    	   e.printStackTrace();
		    	} 
		    }
		});
		return view;
	}
	

	/**
	 * 发送语音
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			message.setReceipt(mToChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath),
					len);
			message.addBody(body);
			mConversation.addMessage(message);
			mMessageAdapter.refreshSelectLast();
			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = mToChatUsername;
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		mConversation.addMessage(message);
		mLvChatContents.setAdapter(mMessageAdapter);
		mMessageAdapter.refreshSelectLast();
		setResult(RESULT_OK);
	}

	/**
	 * 发送文本消息
	 * @param content
	 */
	private void sendText(String content) {
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			TextMessageBody txtBody = new TextMessageBody(content);
			message.addBody(txtBody);
			message.setReceipt(mToChatUsername);
			mConversation.addMessage(message);
			mMessageAdapter.refreshSelectLast();
			mEditMessage.setText("");
			setResult(RESULT_OK);
		}
	}

	/**
	 * 根据图库图片uri发送图片
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;
			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(
					R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}
		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				MySharePreData.GetData(PhoneChatActivity.this, "bbpaw_chat",
						"huanxin_name") + System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				mInputMange.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 返回聊天列表
	 * 
	 * @return
	 */
	public ListView getListView() {
		if (mLvChatContents != null) {
			return mLvChatContents;
		}
		return null;
	}

	public String getToChatUsername() {
		if (mToChatUsername != null) {
			return mToChatUsername;
		}
		return null;
	}

	private void refreshUIWithNewMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				mMessageAdapter.refreshSelectLast();
			}
		});
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				mMessageAdapter.refresh();
			}
		});
	}

	private void notifyDelete(){
		runOnUiThread(new Runnable() {
			public void run() {
				noteDeleted.setVisibility(View.VISIBLE);
			}
		});
	}
	
}
