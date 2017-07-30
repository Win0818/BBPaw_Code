package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.User;

public class SelectControlbabyAdapter  extends BaseAdapter{
	private static final String TAG = "CHRIS";
	private List<User>   mContactsList;
	private Context  mContext;
	private LayoutInflater  mInflater;
	
	
	public SelectControlbabyAdapter(List<User> contacts ,Context  context){
		this.mContext  = context;
		this.mContactsList = contacts;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mContactsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mContactsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		view = mInflater.inflate(R.layout.item_contacts_listview, null);
		ImageView  mContactheadIv = (ImageView) view.findViewById(R.id.item_contacts_headbg);
		TextView   mContactNameIv = (TextView) view.findViewById(R.id.item_contacts_name);
		if(mContactsList != null){
			User user  = mContactsList.get(position);
		  if(user != null){
			String username = user.getUsername();
			String nickname = user.getNick();
			String headurl  = user.getHeadurl();
			String remark_name = user.getRemark_name();
			
			if(remark_name != null && !remark_name.equals("")){
				mContactNameIv.setText(remark_name);
			}else if(!nickname.equals("")){
				mContactNameIv.setText(nickname);
			}else{
				mContactNameIv.setText(username);
			}
			if(headurl != null && !headurl.equals("")){
				MyApplication.getInstance().getImageLoader().displayImage(headurl, mContactheadIv,
					     MyApplication.getInstance().getDisplayOptionsHead());
				}else{
					mContactheadIv.setImageResource(R.drawable.setting_head_default);
			}
		  }
		}
		return view;
	}



	

}
