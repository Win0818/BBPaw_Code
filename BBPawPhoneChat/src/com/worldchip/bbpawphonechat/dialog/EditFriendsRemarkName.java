package com.worldchip.bbpawphonechat.dialog;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.ChatContactsAdapter;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.entity.User;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EditFriendsRemarkName  extends  Dialog  implements  OnClickListener {
	private Context context;
	private Handler handler;
	
	private EditText et_name;
	private Button btn_sure, btn_cancel;
	private RelativeLayout rl_bg;
	private ChatContactsAdapter  mAdapter;
	
	private String friend_nick_or_remarkname;
	
	public EditFriendsRemarkName(Handler handler , Context context,ChatContactsAdapter  mAdapter) {
		super(context, R.style.Aleart_Dialog_Style);
		this.context = context;
		this.handler = handler;
		this.mAdapter = mAdapter;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_edit_user_name);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_dialog_edit_user_name_bg);
		btn_cancel = (Button) findViewById(R.id.btn_edit_name_cancel);
		btn_sure = (Button) findViewById(R.id.btn_edit_name_sure);
		et_name = (EditText) findViewById(R.id.et_dialog_user_name);
		
		int posotion = mAdapter.getmPosition();
		User  user =   (User) mAdapter.getItem(posotion);
		String username = user.getUsername();
	    String nickname = user.getNick();
	    String headUrl = user.getHeadurl();
	    String remarkname = user.getRemark_name();
	    if(remarkname != null && !remarkname.equals("")){
	    	et_name.setText(remarkname);
	    }else if(!nickname.equals("")){
	    	et_name.setText(nickname);
	    }else{
	    	et_name.setText(username);
	    }
		imageAdapter();
		
		et_name.setSelection(et_name.getText().length());
		btn_cancel.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
	}
	
	
	private void imageAdapter() {
		MyApplication.getInstance().ImageAdapter(
				rl_bg,
				new int[] { R.drawable.dialog_change_remark_name,
						R.drawable.dialog_change_remark_name,
						R.drawable.dialog_change_remark_name });
		MyApplication.getInstance().ImageAdapter(
				btn_cancel,
				new int[] { R.drawable.selector_btn_cancle,
						R.drawable.selector_btn_cancle_es,
						R.drawable.selector_btn_cancle_en });
		MyApplication.getInstance().ImageAdapter(
				btn_sure,
				new int[] { R.drawable.selector_btn_sure,
						R.drawable.selector_btn_sure_es,
						R.drawable.selector_btn_sure_en });
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_edit_name_sure:
			friend_nick_or_remarkname = et_name.getText().toString().trim();
			if (!friend_nick_or_remarkname.equals("")) {
				Message message = new Message();
				message.what = MyComment.CHANGE_FRIENDS_REMARKNAME;
				message.obj = friend_nick_or_remarkname;
				handler.sendMessage(message);
				dismiss();
			}else{
				Toast.makeText(context, R.string.change_name_null, Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btn_edit_name_cancel:
			dismiss();
			break;

		default:
			break;
		}
	}
	
	

}
