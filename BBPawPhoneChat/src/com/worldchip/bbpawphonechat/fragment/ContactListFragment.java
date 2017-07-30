package com.worldchip.bbpawphonechat.fragment;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.CaptureActivity;
import com.worldchip.bbpawphonechat.activity.MainActivity;
import com.worldchip.bbpawphonechat.activity.PhoneChatActivity;
import com.worldchip.bbpawphonechat.adapter.ChatContactsAdapter;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.db.InviteMessgeDao;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.dialog.DelectPaidChatDialog;
import com.worldchip.bbpawphonechat.dialog.EditFriendsRemarkName;
import com.worldchip.bbpawphonechat.dialog.MyProgressDialog;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.utils.Configure;

public class ContactListFragment extends Fragment implements OnClickListener {
	private static final String TAG = "ContactListFragment";

	private Context mContext;
	private ListView mContactsListView;
	private List<User> contactsList;
	private ChatContactsAdapter adapter;
	private ImageView mNote;
	private TextView mTvScanAddFriend;
	private Class<MainActivity> mActivity;

	private String chatToheadUrl = null;
	private String chattoName = null;

	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private int mChosePosition;

	private List<String> urls;
	private int urlId;
	private UserDao userDao;
	
	private PopupWindow  mPopupWindow;
	private View mPopupView;
	private int mScreenHeight;
	private int mScreenWidth;
	private int xPosition;
	private TextView  mTvRemarkName, mTvDelectFriends,mTvCancle;
	

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyComment.CHAT_FRAGMWENT_DELECT_FRIEND:
				mChosePosition = adapter.getmPosition();
				User tobeDeleteUser = (User) adapter.getItem(mChosePosition);
				deleteContact(tobeDeleteUser);
				InviteMessgeDao dao = new InviteMessgeDao(getActivity());
				dao.deleteMessage(tobeDeleteUser.getUsername());
				refresh();
				break;
			case MyComment.CHANGE_FRIENDS_REMARKNAME:
				User cUser = (User) adapter.getItem(mChosePosition);
				String remarkName = (String) msg.obj;
				userDao.updataRemarkName(cUser, remarkName);
				initMyContactList();
				break;
			default:
				break;
			}
		};
	};

	public ContactListFragment(Context context) {
		this.mContext = context;
	}
	
	public  ContactListFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		conversationList.addAll(loadConversationsWithRecentChat());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chat_tab_content,
				container, false);
		ImageView iv_head = (ImageView) view
				.findViewById(R.id.iv_top_bar_my_head);
		mContactsListView = (ListView) view.findViewById(R.id.lv_chat_contacts);
		mNote = (ImageView) view.findViewById(R.id.iv_null_contacts_note);
		mTvScanAddFriend = (TextView) view.findViewById(R.id.tv_top_bar_title);
		//mTvScanAddFriend.setBackground(null);
		userDao = new UserDao(mContext);
		contactsList = new ArrayList<User>();
		mTvScanAddFriend.setOnClickListener(this);

		MyApplication
				.getInstance()
				.getImageLoader()
				.displayImage(MyApplication.getInstance().getHeadImageUrl(),
						iv_head,
						MyApplication.getInstance().getDisplayOptionsHead());
		
		initMyContactList();
		initControlBabyData();
		
		LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = layoutInflater.inflate(R.layout.popup_window_layout, null);
		mPopupWindow  = new PopupWindow(mPopupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mScreenHeight = Configure.getScreenHeight(mContext);
		mScreenWidth = Configure.getScreenWidth(mContext);
		
		xPosition = mScreenWidth/2 - 150;
		System.out.println(xPosition+"---mScreenWidth---"+mScreenWidth+"-----"+mPopupView.getWidth()/2);
		
		mTvRemarkName = (TextView) mPopupView.findViewById(R.id.tv_change_remarkname);
		mTvDelectFriends = (TextView) mPopupView.findViewById(R.id.tv_delect_contact);
		mTvCancle = (TextView) mPopupView.findViewById(R.id.tv_pupopwindow_cancle);
		mTvRemarkName.setOnClickListener(this);
		mTvDelectFriends.setOnClickListener(this);
		mTvCancle.setOnClickListener(this);
		
		
		mContactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				User userToChat = (User) adapter.getItem(position);
				if (userToChat.getHeadurl() != null) {
					chatToheadUrl = userToChat.getHeadurl();
				}
				
				if(userToChat.getRemark_name() != null && !userToChat.getRemark_name().equals("")){
					chattoName = userToChat.getRemark_name();
				}else if (!userToChat.getNick().equals("")) {
					chattoName = userToChat.getNick();
				}else {
					chattoName = userToChat.getUsername();
				}
				
				Intent intent = new Intent(mContext, PhoneChatActivity.class);
				intent.putExtra("chatto", userToChat.getUsername());
				intent.putExtra("nick", chattoName);
				intent.putExtra("chattoheadurl", chatToheadUrl);
				startActivity(intent);
			}
		});
		
		mContactsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
					@SuppressLint("NewApi")
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						View parentView = mContactsListView.getChildAt(position);
					    mChosePosition = position;
						adapter.setmPosition(mChosePosition);
						View  view = adapter.getView(position, arg1, arg0);
						mPopupWindow.showAsDropDown(parentView, xPosition, -50);
						return true;
					}
				});
		
		initMyContactList();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		initMyContactList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_top_bar_title:
			Intent intent = new Intent(mContext, CaptureActivity.class);
			getActivity().startActivityForResult(intent,
					MyComment.START_SCAN_ADD_FRIEND);
			break;
		case R.id.tv_change_remarkname:
			popupDismiss();
			EditFriendsRemarkName dialog  = new EditFriendsRemarkName(handler, mContext,adapter);
			dialog.show();
			break;
		case R.id.tv_pupopwindow_cancle:
			popupDismiss();
			break;
		case R.id.tv_delect_contact:
			popupDismiss();
			DelectPaidChatDialog delectLockPaidDialog = new DelectPaidChatDialog(
					handler, getActivity());
			delectLockPaidDialog.show();
			break;
		default:
			break;
		}
	}

	private void popupDismiss() {
		if(mPopupWindow != null && mPopupView != null){
			if(mPopupWindow.isShowing()){
				mPopupWindow.dismiss();
			}
		}
	}

	// 刷新好友列表
	public void refresh() {
		try {
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					initMyContactList();
					initControlBabyData();
					mContext.sendBroadcast(new Intent(
							MyComment.SEND_CONTACT_INFO_BROADCAST));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshMessage() {
		try {
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					updataConversation();
					adapter.notifyDataSetChanged();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷新聊天对象列表
	 */
	private void updataConversation() {
		if (conversationList != null) {
			conversationList.removeAll(conversationList);
			conversationList.addAll(loadConversationsWithRecentChat());
		}
	}

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	private void initMyContactList() {
		if (contactsList != null) {
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
		setContactsListView();
	}

	private void initControlBabyData(){
		if (contactsList.size() <= 0) {
			MySharePreData.SetBooleanData(mContext, MyComment.CHAT_SP_NAME,
					"show_select", false);
			MySharePreData.SetData(mContext, MyComment.CHAT_SP_NAME,
					"control_to", "");
			MyComment.CONTROL_BABY_NAME = "";
		} else if (contactsList.size() >= 1) {
			MySharePreData.SetBooleanData(mContext, MyComment.CHAT_SP_NAME,
					"show_select", true);
			MySharePreData.SetData(mContext, MyComment.CHAT_SP_NAME,
					"control_to", contactsList.get(0).getUsername());
			MyComment.CONTROL_BABY_NAME = contactsList.get(0).getUsername();
		}

	}

	@SuppressLint("NewApi")
	private void setContactsListView() {
		if (contactsList.size() == 0) {
			MyApplication.getInstance().ImageAdapter(
					mNote,
					new int[] { R.drawable.chat_scan_txt_note,
							R.drawable.chat_scan_txt_note_es,
							R.drawable.chat_scan_txt_note_en });

		} else {
			MyApplication.getInstance().ImageAdapter(
					mNote,
					new int[] { R.drawable.chat_fragment_top_note,
							R.drawable.chat_fragment_top_note_es,
							R.drawable.chat_fragment_top_note_en });
			if (adapter != null) {
				adapter = null;
			}
			adapter = new ChatContactsAdapter(handler, contactsList,
					conversationList, mContext);
			mContactsListView.setAdapter(adapter);
		}
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final MyProgressDialog pd = MyProgressDialog
				.createProgressDialog(mContext);
		if (pd != null) {
			pd.setMessage(st1);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(
							tobeDeleteUser.getUsername());
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getUsername());
					MyApplication.getInstance().getContactList()
							.remove(tobeDeleteUser.getUsername());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							if (pd != null)
								pd.dismiss();
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							if (pd != null)
								pd.dismiss();
							Toast.makeText(getActivity(), st2, 1).show();
						}
					});

				}

			}
		}).start();
	}

}
