package com.worldchip.bbpawphonechat.adapter;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.EMLog;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.ContextMenuActivity;
import com.worldchip.bbpawphonechat.activity.PhoneChatActivity;
import com.worldchip.bbpawphonechat.activity.ShowBigImageActivity;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.task.LoadImageTask;
import com.worldchip.bbpawphonechat.utils.DateUtils;
import com.worldchip.bbpawphonechat.utils.ImageCache;
import com.worldchip.bbpawphonechat.utils.ImageUtils;
import com.worldchip.bbpawphonechat.utils.SmileUtils;

public class BBPawChatMessageAdapter extends BaseAdapter{

	private final static String TAG = "CHRIS";
	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username;
	private EMConversation conversation;
	private LayoutInflater inflater;
	
	private String chatToheadUrl;
	
	private Activity activity;
	
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;

	// reference to conversation object in chatsdk
	
	//聊天信息放到一个数组里面
	EMMessage[] messages = null;

	private Context context;

	private Map<String, Timer> timers = new Hashtable<String, Timer>();
	
	/**
	 * 构造函数
	 * @param context
	 * @param username
	 */
	public BBPawChatMessageAdapter(Context context,String headUrl, String username) {
		this.username = username;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.chatToheadUrl = headUrl;
		this.conversation = EMChatManager.getInstance().getConversation(username);
	}
	
	
	
	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
			for (int i = 0; i < messages.length; i++) {
				// getMessage will set message as read status
				conversation.getMessage(i);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case HANDLER_MESSAGE_REFRESH_LIST:
				refreshList();
				break;
			case HANDLER_MESSAGE_SELECT_LAST:
				if (activity instanceof PhoneChatActivity) {
					ListView listView = ((PhoneChatActivity)activity).getListView();
					if (messages.length > 0) {
						listView.setSelection(messages.length - 1);
					}
				}
				break;
			case HANDLER_MESSAGE_SEEK_TO:
				int position = message.arg1;
				if (activity instanceof PhoneChatActivity) {
					ListView listView = ((PhoneChatActivity)activity).getListView();
					listView.setSelection(position);
				}
				break;
			default:
				break;
			}
		}
	};


	/**
	 * 获取item数
	 */
	public int getCount() {
		return messages == null ? 0 : messages.length;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}
	
	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}
	
	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

	/**
	 * 获取某一条聊天信息
	 */
	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
        return 18;
    }

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = getItem(position); 
		if (message == null) {
			return -1;
		}
		if (message.getType() == EMMessage.Type.TXT) {
			if (message.getBooleanAttribute(MyComment.MESSAGE_ATTR_IS_VOICE_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
			else if (message.getBooleanAttribute(MyComment.MESSAGE_ATTR_IS_VIDEO_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
			else
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		return -1;// invalid
	}


	/**
	 * 创建聊天内容界面
	 * @param message
	 * @param position
	 * @return
	 */
	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
		case IMAGE:
			Log.i(TAG, "-------BBPawChatMessageAdapter---图片来源方向----"+message.direct);
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
					R.layout.row_sent_picture, null);
		case VOICE:
			Log.i(TAG, "-------BBPawChatMessageAdapter---语音来源方向----"+message.direct);
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
					R.layout.row_sent_voice, null);
		default:
		 return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message,
			null) : inflater.inflate(R.layout.row_sent_message, null);
		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.IMAGE) {
				Log.e(TAG, "-------BBPawChatMessageAdapter---图片-"+message.getType());
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			}else if (message.getType() == EMMessage.Type.VOICE){
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
					holder.ivPlay = (ImageView) convertView.findViewById(R.id.iv_play_voice);
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
			  }
			}else if (message.getType() == EMMessage.Type.TXT) {
			try {
				holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
				holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
				holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
				// 这里是文字内容
				holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
			} catch (Exception e) {
			}
		}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(message.direct == EMMessage.Direct.RECEIVE){
			MyApplication.getInstance().getImageLoader().displayImage(chatToheadUrl, holder.iv_avatar,
				     MyApplication.getInstance().getDisplayOptionsHead());
		}else{
			MyApplication.getInstance().getImageLoader().displayImage(MyApplication.getInstance().getHeadImageUrl(), 
					holder.iv_avatar,
				    MyApplication.getInstance().getDisplayOptionsHead());
		}
		
		switch (message.getType()){
		// 根据消息type显示item
		case IMAGE: //图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case TXT: //文本
			 handleTextMessage(message, holder, position);
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		
		default:
			break;
		}

		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			EMMessage prevMessage = getItem(position - 1);
			if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		
		
		 if (message.direct == EMMessage.Direct.SEND) {
				View statusView = convertView.findViewById(R.id.msg_status);
				// 重发按钮点击事件
				statusView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						resendMessage(position);			
	                }
				});
			  } 

		
		
		return convertView;
	}
	
	
	/**
	 * 文本消息
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		 Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
		// 设置内容
      	//holder.tv.setText(txtBody.getMessage());
		 holder.tv.setText(span, BufferType.SPANNABLE);
		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(
						(new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
								EMMessage.Type.TXT.ordinal()), PhoneChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
				break;
			}
		}
	}
	/**
	 * 图片消息
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(
						(new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
								EMMessage.Type.IMAGE.ordinal()), PhoneChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.default_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					if(TextUtils.isEmpty(thumbRemoteUrl)&&!TextUtils.isEmpty(remotePath)){
						thumbRemoteUrl = remotePath;
					}
					String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
		}
		switch (message.status){
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(activity,
										activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0)
										.show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	/**
	 * 语音消息
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		int len = voiceBody.getLength();
		if (message.direct == EMMessage.Direct.RECEIVE) {
			setVoiceBackImageFromlenghtFrom(len, holder.iv);
		} else {
			setVoiceBackImageFromlenghtTo(len, holder.iv);
		}
		if(len>0){
			holder.tv.setText(voiceBody.getLength() + "\"");
			holder.tv.setVisibility(View.VISIBLE);
		}else{
			holder.tv.setVisibility(View.INVISIBLE);
		}
		holder.iv.setOnClickListener(new VoicePlayClickListener(message, holder.ivPlay, holder.iv_read_status, this, activity, username));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(
						(new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
								EMMessage.Type.VOICE.ordinal()), PhoneChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});
		
		if (((PhoneChatActivity)activity).playMsgId != null
				&& ((PhoneChatActivity)activity).playMsgId.equals(message
						.getMsgId())&&VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.ivPlay.setImageResource(R.drawable.voice_from_icon);
			} else {
				holder.ivPlay.setImageResource(R.drawable.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.ivPlay.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.ivPlay.setImageResource(R.drawable.talk_voice_static_single_other);
			} else {
				holder.ivPlay.setImageResource(R.drawable.talk_voice_static_single_my);
			}
		}
		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			EMLog.d(TAG, "it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				EMLog.d(TAG, "!!!! back receive");
				((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

					@Override
					public void onSuccess() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});

					}
				});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}
	/**
	 * 发送消息
	 * @param message
	 * @param holder
	 * @param position
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
		final long start = System.currentTimeMillis();
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
			@Override
			public void onSuccess() {
				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				updateSendedView(message, holder);
			}
			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	
	 
	private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
		EMLog.d(TAG, "!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if(holder.pb!=null)
		holder.pb.setVisibility(View.VISIBLE);
		if(holder.tv!=null)
		holder.tv.setVisibility(View.VISIBLE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * 发送图片
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
		try {
			String to = message.getTo();
			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");
			final long start = System.currentTimeMillis();
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
				@Override
				public void onSuccess() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(activity,
									activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				EMLog.d(TAG, "message status : " + message.status);
				if (message.status == EMMessage.Status.SUCCESS) {

				} else if (message.status == EMMessage.Status.FAIL) {
				    
				    if(message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT){
				        Toast.makeText(activity, activity.getString(R.string.send_fail), 0)
                        .show();
				    }else if(message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP){
				        Toast.makeText(activity, activity.getString(R.string.send_fail) , 0)
                        .show();
				    }else{
				        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0)
                        .show();
				    }
				}

				notifyDataSetChanged();
			}
		});
	}

	/**
	 * load image into image view
	 * 
	 * @param thumbernailPath
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
			final EMMessage message) {
		final String remote = remoteDir;
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			iv.setClickable(true); 
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EMLog.d(TAG, "image view on click");
					Intent intent = new Intent(activity, ShowBigImageActivity.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
					} else {
						ImageMessageBody body = (ImageMessageBody) message.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
				       ) {
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);
					
				}
			});
			return true;
		} else {
			new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, activity, message);
			return true;
		}

	}
	
	private void  setVoiceBackImageFromlenghtTo(int len, ImageView iv){
		  if(0<len && len <= 5){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_my3);
		  }else if( 5 < len && len <= 15){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_my2);
		  }else if(15 < len){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_my1);
		  }
	}
	
	
	private void  setVoiceBackImageFromlenghtFrom(int len, ImageView iv){
		  if(0<len && len <= 5){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_other3);
		  }else if( 5 < len && len <= 15){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_other2);
		  }else if(15 < len){
			  iv.setBackgroundResource(R.drawable.talk_voice_bar_other1);
		  }
	}
	
	
	/**
	 * 重发消息
	 */
	private void resendMessage(int position) {
		EMMessage msg = null;
		msg = conversation.getMessage(position);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;
		refreshSeekTo(position);
	}



	public static class ViewHolder {
		ImageView iv;
		ImageView ivPlay;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView iv_avatar;
		TextView tv_usernick;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
		
		TextView tvTitle;
		LinearLayout tvList;
	}

	
}