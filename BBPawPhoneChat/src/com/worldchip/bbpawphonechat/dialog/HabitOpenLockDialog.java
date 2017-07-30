package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.comments.MyComment;

public class HabitOpenLockDialog extends Dialog implements  OnClickListener{
	private Context context;
	private Button  mCancleBtn , mConfirmBtn;
	private Handler  mHandler;
	public HabitOpenLockDialog(Handler handler,Context context) {
		super(context,R.style.Aleart_Dialog_Style);
		this.context=context;
		this.mHandler = handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_habit_start_lock);
        mCancleBtn = (Button) findViewById(R.id.btn_dialog_habit_lock_cancle);
        mConfirmBtn = (Button) findViewById(R.id.btn_dialog_habit_lock_comfirm);
		mCancleBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
	}
	  
	 

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_dialog_habit_lock_cancle:
			dismiss();
			break;

		case R.id.btn_dialog_habit_lock_comfirm:
            comfirm();			
			break;
		default:
			break;
		}
	}
	
	private   void  comfirm(){
		 mHandler.removeMessages(MyComment.SEND_CODE_OPEN_BBPAW);
		 mHandler.sendEmptyMessage(MyComment.SEND_CODE_OPEN_BBPAW);
	}
	
	
	
	
	
}
