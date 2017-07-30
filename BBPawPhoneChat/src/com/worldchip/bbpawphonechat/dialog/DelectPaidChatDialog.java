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

public class DelectPaidChatDialog extends Dialog implements OnClickListener {
	private Context context;
	private Button mCancleBtn, mConfirmBtn;
	private Handler mHandler;

	public DelectPaidChatDialog(Handler handler, Context context) {
		super(context, R.style.Aleart_Dialog_Style);
		this.context = context;
		this.mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_add_lock_paid);
		mCancleBtn = (Button) findViewById(R.id.btn_dialog_add_lock_cancle);
		mConfirmBtn = (Button) findViewById(R.id.btn_dialog_add_lock_comfirm);

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.is_lock);

		MyApplication.getInstance().ImageAdapter(
				rl,
				new int[] { R.drawable.is_lock_paid_bg,
						R.drawable.is_lock_paid_bg_es,
						R.drawable.is_lock_paid_bg_en });

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

		mCancleBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_dialog_add_lock_cancle:
			dismiss();
			break;

		case R.id.btn_dialog_add_lock_comfirm:
			comfirm();
			break;
		default:
			break;
		}
	}

	private void comfirm() {
		mHandler.removeMessages(MyComment.CHAT_FRAGMWENT_DELECT_FRIEND);
		mHandler.sendEmptyMessage(MyComment.CHAT_FRAGMWENT_DELECT_FRIEND);
		dismiss();
	}

}
