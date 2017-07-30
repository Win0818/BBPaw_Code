package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.User;

public class ChatContactsAdapter  extends  BaseAdapter{
	
	private static final String TAG = "CHRIS";
	private List<User>   mContactsList = null;
	private Context  mContext;
	private LayoutInflater  mInflater;
	private List<EMConversation>  mConversationList;
	private Handler  mHandler;
	private int mPosition = 0;
	
	public ChatContactsAdapter(Handler handler ,List<User>  contacts , List<EMConversation> conversationList ,Context  context){
		this.mContext  = context;
		this.mContactsList = contacts;
		this.mHandler = handler;
		this.mConversationList = conversationList;
		mInflater = LayoutInflater.from(mContext);
		
	}

	@Override
	public int getCount() {
		return mContactsList.size();
	}

	@Override
	public Object getItem(int postion) {
		return mContactsList.get(postion);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = mInflater.inflate(R.layout.item_chat_contacts_lv, null);
		ImageView  mContactheadIv = (ImageView) view.findViewById(R.id.item_chat_contact_head);
		//ImageView  mContactStateIv = (ImageView) view.findViewById(R.id.item_iv_chat_contact_state_bg);
		//ImageView  mContactDeleteIv = (ImageView) view.findViewById(R.id.item_iv_chat_contact_delete);
		TextView   mMessageNumberIv = (TextView) view.findViewById(R.id.item_chat_message_number);
		TextView   mContactNameIv = (TextView) view.findViewById(R.id.item_chat_contact_name);
		
		User user  = mContactsList.get(position);
	    String username = user.getUsername();
	    String nickname = user.getNick();
	    String headUrl = user.getHeadurl();
	    String remarkname = user.getRemark_name();
	    
	    if(remarkname != null && !remarkname.equals("")){
	    	mContactNameIv.setText(remarkname);
	    }else if(!nickname.equals("")){
	    	mContactNameIv.setText(nickname);
	    }else{
	    	mContactNameIv.setText(username);
	    }
	    mContactheadIv.setImageResource(R.color.transparent);
	    if(headUrl != null && !headUrl.equals("")){
	    	MyApplication.getInstance().getImageLoader().displayImage(headUrl, mContactheadIv,
				     MyApplication.getInstance().getDisplayOptionsHead());
	    }else{
			mContactheadIv.setImageResource(R.drawable.setting_head_default);
		}
	    
		for (int i = 0; i < mConversationList.size();i++){
			    int num = mConversationList.get(i).getUnreadMsgCount();
				EMConversation conversation = mConversationList.get(i);
				if(num != 0 && username.equals(conversation.getUserName())){
					mMessageNumberIv.setVisibility(View.VISIBLE);
					mMessageNumberIv.setText(String.valueOf(conversation.getUnreadMsgCount()));
				}
			  }
		return view;
	}

   

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	
	
  
	

  }
