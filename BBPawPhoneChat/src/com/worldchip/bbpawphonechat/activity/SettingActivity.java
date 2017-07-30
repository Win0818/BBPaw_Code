package com.worldchip.bbpawphonechat.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.HXSDKHelper;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;

public class SettingActivity extends Activity implements OnClickListener {
	private static final String TAG = "CHRIS";

	private Button mBtnshakeOpen, mBtnvoiceOpen, mBtnnotiOpen, mBtnreceiveOpen,
			mBtnwifiOpen;
	//private MySlipButton  mBtnshakeOpen;
	
	private Button mBtnshakeClose, mBtnvoiceClose, mBtnnotiClose,
			mBtnreceiveClose, mBtnwifiClose;

	private AudioManager audio;
	private int volume;
	private boolean isNotifi, isShake, isSound, isExitMessage, isWifiLoad;

	private EMChatOptions chatOptions;

	private ImageView iv, iv_voice, iv_shake, iv_sound,
			iv_notifaction_sentence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initViewBtn();
	}

	private void initView() {
		// TODO Auto-generated method stub
		iv = (ImageView) findViewById(R.id.iv_setting);
		iv_voice = (ImageView) findViewById(R.id.iv_setteing_voice);
		iv_shake = (ImageView) findViewById(R.id.iv_setteing_shake);
		iv_sound = (ImageView) findViewById(R.id.iv_setteing_sound);
		iv_notifaction_sentence = (ImageView) findViewById(R.id.iv_setting_notifaction_sentence);

		imageAdapter();

		mBtnshakeOpen = (Button) findViewById(R.id.btn_shake_open);
		mBtnvoiceOpen = (Button) findViewById(R.id.btn_sound_open);
		mBtnnotiOpen = (Button) findViewById(R.id.btn_isnoti_open);

		mBtnshakeClose = (Button) findViewById(R.id.btn_shake_close);
		mBtnvoiceClose = (Button) findViewById(R.id.btn_sound_close);
		mBtnnotiClose = (Button) findViewById(R.id.btn_isnoti_close);

		chatOptions = EMChatManager.getInstance().getChatOptions();

		mBtnshakeOpen.setOnClickListener(this);
		mBtnvoiceOpen.setOnClickListener(this);
		mBtnnotiOpen.setOnClickListener(this);

		mBtnshakeClose.setOnClickListener(this);
		mBtnvoiceClose.setOnClickListener(this);
		mBtnnotiClose.setOnClickListener(this);

	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(iv,
				new int[] { R.drawable.setting, R.drawable.setting_es,
						R.drawable.setting_en });
		MyApplication.getInstance().ImageAdapter(iv_voice,
				new int[] { R.drawable.voice, R.drawable.voice_es,
						R.drawable.voice_en });
		MyApplication.getInstance().ImageAdapter(iv_shake,
				new int[] { R.drawable.shake, R.drawable.shake_es,
						R.drawable.shake_en });
		MyApplication.getInstance().ImageAdapter(iv_sound,
				new int[] { R.drawable.sound, R.drawable.sound_es,
						R.drawable.sound_en });
		MyApplication.getInstance().ImageAdapter(iv_notifaction_sentence,
				new int[] { R.drawable.setting_notifaction_sentence,
						R.drawable.setting_notifaction_sentence_es,
						R.drawable.setting_notifaction_sentence_en });

	}

	public void onBack(View view) {
		super.onBackPressed();

	}

	private void initViewBtn() {
		isShake = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, MyComment.IS_SHAKE);
		isNotifi = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, MyComment.IS_NOTIFICATION);
		isExitMessage = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, MyComment.IS_EXIT_MESSAGE);
		isSound = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, MyComment.IS_VOICE);
		isWifiLoad = MySharePreData.GetBooleanTrueData(this,
				MyComment.CHAT_SP_NAME, MyComment.IS_WIFI_UPDATA);

		initbtnVisable(isShake, mBtnshakeOpen, mBtnshakeClose);
		
		initbtnVisable(isNotifi, mBtnnotiOpen, mBtnnotiClose);
		// initbtnVisable(isExitMessage, mBtnreceiveOpen, mBtnreceiveClose);
		initbtnVisable(isSound, mBtnvoiceOpen, mBtnvoiceClose);
		// initbtnVisable(isWifiLoad, mBtnwifiOpen, mBtnwifiClose);
	}

	private void initbtnVisable(boolean isOpen, Button openBtn, Button closeBtn) {
		if (isOpen) {
			openBtn.setVisibility(View.VISIBLE);
			closeBtn.setVisibility(View.GONE);
		} else {
			openBtn.setVisibility(View.GONE);
			closeBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_shake_open:
			setShakeNotify(false);
			break;
		case R.id.btn_shake_close:
			setShakeNotify(true);
			break;
		case R.id.btn_sound_open:
			setSoundNotify(false);
			break;
		case R.id.btn_sound_close:
			setSoundNotify(true);
			break;
		case R.id.btn_isnoti_open:
			setNotifycation(false);
			break;
		case R.id.btn_isnoti_close:
			setNotifycation(true);
			break;
		default:
			break;
		}

	}

	private void setShakeNotify(boolean isShake) {
		MySharePreData.SetBooleanData(this, MyComment.CHAT_SP_NAME,
				MyComment.IS_SHAKE, isShake);
		chatOptions.setNoticedByVibrate(isShake);
		EMChatManager.getInstance().setChatOptions(chatOptions);
		HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(isShake);
		initbtnVisable(isShake, mBtnshakeOpen, mBtnshakeClose);
		Log.i(TAG, "---------setShake--------" + isShake);
	}

	private void setSoundNotify(boolean isSound) {
		MySharePreData.SetBooleanData(this, MyComment.CHAT_SP_NAME,
				MyComment.IS_VOICE, isSound);
		chatOptions.setNoticeBySound(isSound);
		EMChatManager.getInstance().setChatOptions(chatOptions);
		HXSDKHelper.getInstance().getModel().setSettingMsgSound(isSound);
		initbtnVisable(isSound, mBtnvoiceOpen, mBtnvoiceClose);
		Log.i(TAG, "---------------setSoundNotify-----------------" + isSound);
	}

	private void setNotifycation(boolean isNotidfyCation) {
		MySharePreData.SetBooleanData(this, MyComment.CHAT_SP_NAME,
				MyComment.IS_NOTIFICATION, isNotidfyCation);
		HXSDKHelper.getInstance().getNotifier().isNotification();
		initbtnVisable(isNotidfyCation, mBtnnotiOpen, mBtnnotiClose);
		Log.i(TAG, "---------------setNotifycation-----------------"
				+ isNotidfyCation);
	}
	
}
