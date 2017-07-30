package com.worldchip.bbpawphonechat.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.SelectControlbabyAdapter;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.entity.User;

public class SelectControlBabyDialog extends Dialog {
	protected static final String TAG = "CHRIS";
	private Context  mContext;
	private Handler  mHandler;
	private ListView  mSelectbabyListView;
	private List<User>  controlSelecteList;
	private SelectControlbabyAdapter controlbabyAdapter;
	private int mHeardId = 0;
	
	public SelectControlBabyDialog(Handler handler,Context context) {
		super(context,R.style.Aleart_Dialog_Style);
		this.mContext=context;
		this.mHandler = handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_baby_list);
		mSelectbabyListView = (ListView) findViewById(R.id.lv_select_baby_control);
		controlSelecteList  = new ArrayList<User>();
		mSelectbabyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				User user = (User) controlbabyAdapter.getItem(position);
                //MyApplication.getInstance().setmContolbabyUser(user);
				MySharePreData.SetData(mContext, MyComment.CHAT_SP_NAME, "control_to", user.getUsername());
				Message  message = new Message();
				message.obj = user;
			    message.what = MyComment.SELECTED_CONTROL_BABY;
				mHandler.sendMessage(message);
				}
		});
		getContactList();
	}
	
	/**
	 *获取联系人列表，并过滤掉黑名单和排序
	 */
	private void getContactList() {
		controlSelecteList.clear();
		//获取本地好友列表
		Map<String, User> users = MyApplication.getInstance().getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			if (!entry.getKey().equals(MyComment.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(MyComment.GROUP_USERNAME))
				controlSelecteList.add(entry.getValue());
		}
		controlbabyAdapter = new SelectControlbabyAdapter(controlSelecteList,mContext);
		mSelectbabyListView.setAdapter(controlbabyAdapter);
	}
	
}
