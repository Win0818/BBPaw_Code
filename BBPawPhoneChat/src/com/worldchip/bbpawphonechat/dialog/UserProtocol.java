package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.worldchip.bbpawphonechat.R;

public class  UserProtocol  extends  Dialog{

	public UserProtocol(Context context) {
		super(context, R.style.Aleart_Dialog_Style1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_user_protocol);
	}
	

}
