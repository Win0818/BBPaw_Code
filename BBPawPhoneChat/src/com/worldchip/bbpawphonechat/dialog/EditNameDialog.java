package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;

public class EditNameDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	private Handler handler;
	
	private String currentName;
	private String changeName;
	private boolean updatenick = false;

	private EditText et_name;
	private Button btn_sure, btn_cancel;
	private RelativeLayout rl_bg;

	public EditNameDialog(Handler handler, Context context) {
		super(context, R.style.Aleart_Dialog_Style);
		this.context = context;
		this.handler = handler;

		currentName = MySharePreData.GetData(context, MyComment.CHAT_SP_NAME,
				MyComment.MY_NICK_NAME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_edit_user_name);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_dialog_edit_user_name_bg);
		btn_cancel = (Button) findViewById(R.id.btn_edit_name_cancel);
		btn_sure = (Button) findViewById(R.id.btn_edit_name_sure);
		et_name = (EditText) findViewById(R.id.et_dialog_user_name);

		imageAdapter();

		if (currentName.equals("")) {
			et_name.setText(MyApplication.getInstance().getUserName());
		} else {
			et_name.setText(currentName);
		}

		et_name.setSelection(et_name.getText().length());
		btn_cancel.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				rl_bg,
				new int[] { R.drawable.dialog_edit_user_name_bg,
						R.drawable.dialog_edit_user_name_bg_es,
						R.drawable.dialog_edit_user_name_bg_en });
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
		switch (v.getId()) {
		case R.id.btn_edit_name_sure:
			changeName = et_name.getText().toString().trim();
			if (!changeName.equals("")) {
				Message message = new Message();
				message.what = MyComment.CHANGE_NICK_NAME;
				message.obj = changeName;
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
