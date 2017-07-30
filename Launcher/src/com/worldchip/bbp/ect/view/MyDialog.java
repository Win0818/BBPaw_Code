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

public class MyDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface onCheckListener {
		
		public void onYes();

		public void onNo();
	}

	private ImageButton mImgBtnYes;
	private ImageButton mImgBtnNO;
	public onCheckListener mOnCheckListener;

	public MyDialog(Context context, onCheckListener mOnCheckListener) {
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
		dialogWindow.setGravity(Gravity.CENTER);
		lp.x =20;

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
		MyDialog.this.dismiss();
	}
}
