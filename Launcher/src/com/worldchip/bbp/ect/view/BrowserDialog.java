package com.worldchip.bbp.ect.view;


import com.worldchip.bbp.ect.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class BrowserDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface onBroswerCheckListener {
		
		public void onYes();

		public void onNo();
	}

	private ImageButton mImgBtnYes;
	private ImageButton mImgBtnNO;
	public onBroswerCheckListener mOnCheckListener;

	public BrowserDialog(Context context, onBroswerCheckListener mOnCheckListener) {
		super(context,R.style.Dialog_global);
		this.mOnCheckListener = mOnCheckListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mydialog_content);
		Window dialogWindow = this.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		lp.x = 250;

		lp.width = 380;
		lp.height = 200;
		dialogWindow.setAttributes(lp);
		mImgBtnYes = (ImageButton) findViewById(R.id.yes);
		mImgBtnNO = (ImageButton) findViewById(R.id.no);
		mImgBtnYes.setOnClickListener(this);
		mImgBtnNO.setOnClickListener(this);
	}

	private void ondraw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.yes) {
			mOnCheckListener.onYes();

		} else if (arg0.getId() == R.id.no) {
			mOnCheckListener.onNo();

		}
		BrowserDialog.this.dismiss();
	}
}
