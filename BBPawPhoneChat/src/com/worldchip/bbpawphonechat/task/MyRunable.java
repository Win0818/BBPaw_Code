package com.worldchip.bbpawphonechat.task;

import java.io.IOException;
import java.util.List;

import android.content.Context;

import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.net.HttpUtils;

public  class  MyRunable   implements  Runnable{
	private int  i;
	private List<User> contacts;
	private Context mContext;
	private UserDao userDao;
	private boolean mIsHead;
	public MyRunable(int index, List<User>  contacts, Context  context, boolean isHead) {
		this.i = index;
		this.contacts = contacts;
		mContext = context;
		userDao = new UserDao(mContext);
		mIsHead = isHead;
	}

	@Override
	public void run() {
		if(mIsHead){
			updataHeadUrl();
		}else{
			updataNickName();
		}
		
	}
	
	private void  updataHeadUrl(){
		String headUlr = MyComment.GET_MY_HEAD_URL+ "&username="+contacts.get(i).getUsername();
		try {
			String	result = HttpUtils.getRequest(headUlr,mContext);
			String resultUrl = "";
			if(result != null){
				resultUrl = MyJsonParserUtil.parserMyheadUrl(result);
			}
			User user = contacts.get(i);
			user.setUsername(contacts.get(i).getUsername());
			user.setHeadurl(resultUrl);
			userDao.updataContactHeadUrl(user, resultUrl);
			System.out.println(contacts.get(i).getUsername()+"---getheadAddressJson--开始获取好友头像---"+resultUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updataNickName(){
		String url = MyComment.GET_CONTACT_NICK_NAME+ "&username="+ contacts.get(i).getUsername();
		try {
			String result = HttpUtils.getRequest(url, mContext);
			User user = contacts.get(i);
			user.setUsername(contacts.get(i).getUsername());
			user.setNick(result);
			userDao.updataNickName(user, result);
			System.out.println("---changeContactNickName--开始获取好友昵称---"+result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
