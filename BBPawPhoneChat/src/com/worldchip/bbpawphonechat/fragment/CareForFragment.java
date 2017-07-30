package com.worldchip.bbpawphonechat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EMServiceNotReadyException;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.entity.User;

public class CareForFragment extends Fragment implements OnClickListener {
	private static final String TAG = "CHRIS";

	private Context mContext;
	private TextView mTopBarTitle, mConnectState, mConnectName;
	protected SoundPool soundPool;
	private ImageView mMakeCall, mCancleCall, mStopCall;
	private RelativeLayout mchronometerReLayout, mConnectRelLayout;
	private ImageView mCareforHeadImage;

	private String mCareForUserName = "";
	private String mCareForUserNick = "";
	private String mCareForContactHeadUrl = "";
	private UserDao mUserDao;
	private User mControlUser = null;

	protected int outgoing;
	private int streamID;
	protected AudioManager audioManager;
	private Chronometer chronometer;

	protected CallingState callingState = CallingState.CANCED;
	private Handler handler = new Handler();

	private String strNotConnect;

	private User mCareForBaby;

	private TextView tv_minute;

	public CareForFragment(Context context) {
		this.mContext = context;
		audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
	}
	public CareForFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				MyComment.SEND_CONTROL_CHANGE_BROADCAST);
		if(mContext != null)
		mContext.registerReceiver(mContactChangeReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_carefor_tab_content,
				container, false);
		ImageView iv_head = (ImageView) view
				.findViewById(R.id.iv_top_bar_my_head);
		MyApplication
				.getInstance()
				.getImageLoader()
				.displayImage(MyApplication.getInstance().getHeadImageUrl(),
						iv_head,
						MyApplication.getInstance().getDisplayOptionsHead());

		mTopBarTitle = (TextView) view.findViewById(R.id.tv_top_bar_title);
		mMakeCall = (ImageView) view.findViewById(R.id.iv_carefor_start_btn);
		mCancleCall = (ImageView) view.findViewById(R.id.iv_carefor_cancle_btn);
		mStopCall = (ImageView) view.findViewById(R.id.iv_carefor_stop_btn);
		chronometer = (Chronometer) view.findViewById(R.id.chronometer);
		mchronometerReLayout = (RelativeLayout) view
				.findViewById(R.id.rl_chronometer);
		mConnectRelLayout = (RelativeLayout) view
				.findViewById(R.id.rl_carefor_state_and_name);
		mConnectState = (TextView) view
				.findViewById(R.id.tv_carefor_connect_state);
		mConnectName = (TextView) view
				.findViewById(R.id.tv_carefor_connect_name);
		mCareforHeadImage = (ImageView) view.findViewById(R.id.iv_carefor_head);

		tv_minute = (TextView) view
				.findViewById(R.id.tv_fragment_carefor_minute);

		mUserDao = new UserDao(mContext);


		strNotConnect = getResources().getString(
				R.string.make_call_is_not_connect);

		mTopBarTitle.setBackgroundResource(Color.TRANSPARENT);
		mTopBarTitle.setText(strNotConnect);

		MyApplication.getInstance().ImageAdapter(
				mMakeCall,
				new int[] { R.drawable.carefor_start_btn_default,
						R.drawable.carefor_start_btn_default_es,
						R.drawable.carefor_start_btn_default_en });
		MyApplication.getInstance().ImageAdapter(
				mCancleCall,
				new int[] { R.drawable.carefor_cancle_btn_default,
						R.drawable.carefor_cancle_btn_default_es,
						R.drawable.carefor_cancle_btn_default_en });

		mMakeCall.setOnClickListener(this);
		mCancleCall.setOnClickListener(this);
		mStopCall.setOnClickListener(this);
		mchronometerReLayout.setVisibility(View.INVISIBLE);
		mConnectRelLayout.setVisibility(View.INVISIBLE);

		initControlInfo();

		EMChatManager.getInstance().addVoiceCallStateChangeListener(
				callStateListener);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mTopBarTitle.setBackgroundResource(Color.TRANSPARENT);
		MyApplication.getInstance().ImageAdapter(
				tv_minute,
				new int[] { R.drawable.carefor_minutes_txt_img,
						R.drawable.carefor_minutes_txt_img_es,
						R.drawable.carefor_minutes_txt_img_en });

	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mContactChangeReceiver);
		stopCareCall();
		if (callStateListener != null)
			EMChatManager.getInstance().removeCallStateChangeListener(
					callStateListener);
		super.onDestroy();
	}

	private void initControlInfo() {
		mCareForUserName = MyComment.CONTROL_BABY_NAME;
		if (mCareForUserName != null && !mCareForUserName.equals("")) {
			mControlUser = mUserDao.getOneContact(mCareForUserName);
			mCareForContactHeadUrl = mControlUser.getHeadurl();
			if (mCareForContactHeadUrl != null
					&& !mCareForContactHeadUrl.equals("")) {
				MyApplication
						.getInstance()
						.getImageLoader()
						.displayImage(
								mCareForContactHeadUrl,
								mCareforHeadImage,
								MyApplication.getInstance()
										.getDisplayOptionsHead());
			} else {
				mCareforHeadImage
						.setImageResource(R.drawable.setting_head_default);
			}
		} else {
			mCareforHeadImage.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_carefor_start_btn:
			// 拨打语音电话
			String st1 = getResources().getString(
					R.string.not_connect_to_server);
			if (!EMChatManager.getInstance().isConnected()) {
				Toast.makeText(mContext, st1, 0).show();
			} else if (!MyComment.CONTROL_BABY_NAME.equals("")) {
				if (mControlUser != null) {
					mCareForUserNick = mControlUser.getNick();
				} else {
					mCareForUserNick = MyComment.CONTROL_BABY_NAME;
				}
				soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
				outgoing = soundPool.load(mContext, R.raw.outgoing, 1);
				handler.postDelayed(new Runnable() {
					public void run() {
						streamID = playMakeCallSounds();
					}
				}, 300);
				try {
					EMChatManager.getInstance().makeVoiceCall(
							MyComment.CONTROL_BABY_NAME);
				} catch (EMServiceNotReadyException e) {
					e.printStackTrace();
					final String st2 = getResources().getString(
							R.string.Is_not_yet_connected_to_the_server);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(mContext, st2, 0).show();
						}
					});
				}
			} else {
				String strSelectUser = getResources().getString(
						R.string.please_select_call_to_user);
				Toast.makeText(getActivity(), strSelectUser, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case R.id.iv_carefor_cancle_btn:
			stopCareCall();
			break;

		case R.id.iv_carefor_stop_btn:
			stopCareCall();
			break;

		default:
			break;
		}

	}

	private void stopCareCall() {
		if (soundPool != null) {
			soundPool.stop(streamID);
			soundPool.release();
		}
		try {
			EMChatManager.getInstance().endCall();
			mMakeCall.setVisibility(View.VISIBLE);
			mCancleCall.setVisibility(View.GONE);
			mStopCall.setVisibility(View.GONE);
			mTopBarTitle.setText(strNotConnect);
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setMicrophoneMute(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置电话监听
	 */
	EMCallStateChangeListener callStateListener = new EMCallStateChangeListener() {
		@Override
		public void onCallStateChanged(CallState callState, CallError error) {
			switch (callState) {
			case CONNECTING: // 正在连接对方
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String st1 = getResources().getString(
								R.string.Are_connected_to_each_other);
						mMakeCall.setVisibility(View.GONE);
						mCancleCall.setVisibility(View.VISIBLE);
						mStopCall.setVisibility(View.GONE);
						mConnectRelLayout.setVisibility(View.VISIBLE);
						MyApplication.getInstance().ImageAdapter(mConnectState,
										new int[] { R.drawable.carefor_start_now_txt_img,
												R.drawable.carefor_start_now_txt_img_es,
												R.drawable.carefor_start_now_txt_img_en});
					}
				});
				break;
			case CONNECTED: // 双方已经建立连接
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String st3 = getResources().getString(
								R.string.have_connected_with);
						mMakeCall.setVisibility(View.GONE);
						mCancleCall.setVisibility(View.VISIBLE);
						mStopCall.setVisibility(View.GONE);
						mTopBarTitle.setText(st3);
					}
				});
				break;
			case ACCEPTED: // 电话接通成功
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String str4 = getResources().getString(
								R.string.In_the_call);
						mTopBarTitle.setText(str4);
						mMakeCall.setVisibility(View.GONE);
						mCancleCall.setVisibility(View.GONE);
						mStopCall.setVisibility(View.VISIBLE);
						
						MyApplication
						.getInstance()
						.ImageAdapter(
								mConnectState,
								new int[] { R.drawable.carefor_already_txt_img,
										R.drawable.carefor_already_txt_img_es,
										R.drawable.carefor_already_txt_img_en});
						MyApplication
						.getInstance()
						.ImageAdapter(
								mStopCall,
								new int[] { R.drawable.carefor_stop_btn_default,
										R.drawable.carefor_cancle_btn_default_es,
										R.drawable.carefor_cancle_btn_default_en});
						
						if (mCareForUserNick.equals("")) {
							mConnectName.setText(mCareForBaby.getUsername());
						} else {
							mConnectName.setText(mCareForUserNick);
						}
						try {
							if (soundPool != null)
								soundPool.stop(streamID);
						} catch (Exception e) {
						}
						openSpeakerOn();
						// 关闭话筒音
						audioManager.setMicrophoneMute(true);
						mchronometerReLayout.setVisibility(View.VISIBLE);
						chronometer.setBase(SystemClock.elapsedRealtime());
						// 开始记时
						chronometer.start();
					}

				});
				break;
			case DISCONNNECTED:
				// 电话断了
				final CallError fError = error;
				getActivity().runOnUiThread(new Runnable() {
					private void postDelayedCloseMsg() {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mMakeCall.setVisibility(View.VISIBLE);
								mCancleCall.setVisibility(View.GONE);
								mStopCall.setVisibility(View.GONE);
								mTopBarTitle.setText(strNotConnect);
								mConnectRelLayout.setVisibility(View.INVISIBLE);
								// 开启话筒音
								audioManager.setMicrophoneMute(false);
							}
						}, 200);
					}

					@Override
					public void run() {
						chronometer.stop();
						String st2 = mContext.getResources().getString(
								R.string.The_other_party_refused_to_accept);
						String st3 = mContext.getResources().getString(
								R.string.Connection_failure);
						String st4 = mContext.getResources().getString(
								R.string.The_other_party_is_not_online);
						String st5 = mContext.getResources().getString(
								R.string.The_other_is_on_the_phone_please);
						String st6 = mContext.getResources().getString(
								R.string.The_other_party_did_not_answer_new);
						if (fError == CallError.REJECTED) {
							callingState = CallingState.BEREFUESD;
							mTopBarTitle.setText(st2);
							mMakeCall.setVisibility(View.GONE);
							mCancleCall.setVisibility(View.VISIBLE);
							mStopCall.setVisibility(View.GONE);
						} else if (fError == CallError.ERROR_TRANSPORT) {
							mTopBarTitle.setText(st3);
							mMakeCall.setVisibility(View.GONE);
							mCancleCall.setVisibility(View.VISIBLE);
							mStopCall.setVisibility(View.GONE);
						} else if (fError == CallError.ERROR_INAVAILABLE) {
							callingState = CallingState.OFFLINE;
							mTopBarTitle.setText(st4);
							mMakeCall.setVisibility(View.GONE);
							mCancleCall.setVisibility(View.VISIBLE);
							mStopCall.setVisibility(View.GONE);
						} else if (fError == CallError.ERROR_BUSY) {
							callingState = CallingState.BUSY;
							mTopBarTitle.setText(st5);
							mMakeCall.setVisibility(View.GONE);
							mCancleCall.setVisibility(View.VISIBLE);
							mStopCall.setVisibility(View.GONE);
						} else if (fError == CallError.ERROR_NORESPONSE) {
							callingState = CallingState.NORESPONSE;
							mTopBarTitle.setText(st6);
							mMakeCall.setVisibility(View.GONE);
							mCancleCall.setVisibility(View.VISIBLE);
							mStopCall.setVisibility(View.GONE);
						} else {
							postDelayedCloseMsg();
						}
					}
				});
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 播放拨号响铃
	 * 
	 * @param sound
	 * @param number
	 */
	protected int playMakeCallSounds() {
		try {
			// 最大音量
			float audioMaxVolumn = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_RING);
			// 当前音量
			float audioCurrentVolumn = audioManager
					.getStreamVolume(AudioManager.STREAM_RING);
			float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(false);
			// 播放
			int id = soundPool.play(outgoing, // 声音资源
					0.3f, // 左声道
					0.3f, // 右声道
					1, // 优先级，0最低
					-1, // 循环次数，0是不循环，-1是永远循环
					1); // 回放速度，0.5-2.0之间。1为正常速度
			return id;
		} catch (Exception e) {
			return -1;
		}
	}

	// 关闭扬声器
	protected void closeSpeakerOn() {
		try {
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn())
					audioManager.setSpeakerphoneOn(false);
				audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 打开扬声器
	protected void openSpeakerOn() {
		try {
			if (!audioManager.isSpeakerphoneOn())
				audioManager.setSpeakerphoneOn(true);
			audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	enum CallingState {
		CANCED, NORMAL, REFUESD, BEREFUESD, UNANSWERED, OFFLINE, NORESPONSE, BUSY
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private BroadcastReceiver mContactChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initControlInfo();
		}
	};

	private BroadcastReceiver mContactInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};

}
