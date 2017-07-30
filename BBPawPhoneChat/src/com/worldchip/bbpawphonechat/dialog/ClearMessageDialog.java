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

public class ClearMessageDialog extends Dialog implements OnClickListener {
	private Context context;
	private Button mCancleBtn, mConfirmBtn;
	private Handler mHandler;
	private RelativeLayout is_clear;

	public ClearMessageDialog(Handler handler, Context context) {
		super(context, R.style.Aleart_Dialog_Style);
		this.context = context;
		this.mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_clear_all_message);
		mCancleBtn = (Button) findViewById(R.id.btn_dialog_clear_cancle);
		mConfirmBtn = (Button) findViewById(R.id.btn_dialog_clear_comfirm);
		is_clear = (RelativeLayout) findViewById(R.id.is_clear);

		imageAdapter();
		mCancleBtn.setOnClickListener(this);
		mConfirmBtn.setOnClickListener(this);
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				is_clear,
				new int[] { R.drawable.dialog_clear_all_message_bg,
						R.drawable.dialog_clear_all_message_bg_es,
						R.drawable.dialog_clear_all_message_bg_en});
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
		case R.id.btn_dialog_clear_cancle:
			dismiss();
			break;

		case R.id.btn_dialog_clear_comfirm:
			comfirm();
			break;
		default:
			break;
		}
	}

	private void comfirm() {
		mHandler.removeMessages(MyComment.CLEAR_SOMEONE_ALL_MESSAGE);
		mHandler.sendEmptyMessage(MyComment.CLEAR_SOMEONE_ALL_MESSAGE);
		dismiss();
	}

}
