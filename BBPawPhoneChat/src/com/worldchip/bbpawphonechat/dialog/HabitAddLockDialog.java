package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;

public class HabitAddLockDialog extends Dialog implements OnClickListener {
	private Context context;
	private Button mCancleBtn, mConfirmBtn;
	private Handler mHandler;
	private RelativeLayout rl_is_lock;

	public HabitAddLockDialog(Handler handler, Context context) {
		super(context, R.style.Aleart_Dialog_Style);
		this.context = context;
		this.mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_habit_start_lock);
		mCancleBtn = (Button) findViewById(R.id.btn_dialog_habit_lock_cancle);
		mConfirmBtn = (Button) findViewById(R.id.btn_dialog_habit_lock_comfirm);
		rl_is_lock = (RelativeLayout) findViewById(R.id.is_lock);
		imageAdapter();

		mCancleBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				rl_is_lock,
				new int[] { R.drawable.habit_is_lock_dialog_bg,
						R.drawable.habit_is_lock_dialog_bg_es,
						R.drawable.habit_is_lock_dialog_bg_en });
		MyApplication.getInstance().ImageAdapter(
				mCancleBtn,
				new int[] { R.drawable.selector_btn_cancle,
						R.drawable.selector_btn_cancle_es,
						R.drawable.selector_btn_cancle_en });
		MyApplication.getInstance().ImageAdapter(
				mConfirmBtn,
				new int[] { R.drawable.selector_btn_sure,
						R.drawable.selector_btn_sure_es,
						R.drawable.selector_btn_sure_en });

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

	private void comfirm() {
		mHandler.removeMessages(MyComment.SEND_CODE_OPEN_BBPAW);
		mHandler.sendEmptyMessage(MyComment.SEND_CODE_OPEN_BBPAW);
	}

}
