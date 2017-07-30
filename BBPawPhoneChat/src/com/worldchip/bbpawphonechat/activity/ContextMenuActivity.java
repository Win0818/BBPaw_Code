package com.worldchip.bbpawphonechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easemob.chat.EMMessage;
import com.worldchip.bbpawphonechat.R;

public class ContextMenuActivity extends  Activity{
	
	private int mPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		mPosition = getIntent().getIntExtra("position", -1);
		if(type == EMMessage.Type.TXT.ordinal()){
			setContentView(R.layout.context_menu_for_text);
		}else if (type == EMMessage.Type.LOCATION.ordinal()) {
		    setContentView(R.layout.context_menu_for_location);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
		    setContentView(R.layout.context_menu_for_image);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
		    setContentView(R.layout.context_menu_for_voice);
		} else if (type == EMMessage.Type.VIDEO.ordinal()) {
			setContentView(R.layout.context_menu_for_video);
		}
		setFinishOnTouchOutside(true);
	}
	
	public void copy(View view){
		setResult(PhoneChatActivity.RESULT_CODE_COPY, new Intent().putExtra("position", mPosition));
		finish();
	}
	public void delete(View view){
		setResult(PhoneChatActivity.RESULT_CODE_DELETE, new Intent().putExtra("position", mPosition));
		finish();
	}
	/*public void forward(View view){
		setResult(PhoneChatActivity.RESULT_CODE_FORWARD, new Intent().putExtra("position", mPosition));
		finish();
	}*/
/*	
	public void open(View v){
	    setResult(PhoneChatActivity.RESULT_CODE_OPEN, new Intent().putExtra("position", mPosition));
        finish();
	}
	public void download(View v){
	    setResult(PhoneChatActivity.RESULT_CODE_DWONLOAD, new Intent().putExtra("position", mPosition));
        finish();
	}
	public void toCloud(View v){
	    setResult(PhoneChatActivity.RESULT_CODE_TO_CLOUD, new Intent().putExtra("position", mPosition));
        finish();
	}*/
	 
}

