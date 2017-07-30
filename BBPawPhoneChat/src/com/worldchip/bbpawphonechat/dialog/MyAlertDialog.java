package com.worldchip.bbpawphonechat.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;

public class MyAlertDialog {
	Context context;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView;
	LinearLayout buttonLayout;
	private LinearLayout ll_lock;

	public MyAlertDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.setCanceledOnTouchOutside(false);
		ad.show();
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_myalertdialog_layout);
		titleView = (TextView) window.findViewById(R.id.tv_alert_dialog_title);
		messageView = (TextView) window
				.findViewById(R.id.tv_alert_dialog_message);
		buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);

		//ll_lock = (LinearLayout) window.findViewById(R.id.ll_lock_bg);
/*
		MyApplication.getInstance().ImageAdapter(
				ll_lock,
				new int[] { R.drawable.dialog_lock_bg,
						R.drawable.dialog_lock_bg_en,
						R.drawable.dialog_lock_bg_en });*/
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setMessage(int resId) {
		messageView.setText(resId);
	}

	public void setMessage(String message) {
		messageView.setText(message);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		Button button = new Button(context);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(params);

		MyApplication.getInstance().ImageAdapter(
				button,
				new int[] { R.drawable.selector_btn_sure,
						R.drawable.selector_btn_sure_es,
						R.drawable.selector_btn_sure_en });

		button.setTextColor(Color.WHITE);
		button.setTextSize(16);
		button.setOnClickListener(listener);
		buttonLayout.addView(button);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,
			final View.OnClickListener listener) {
		Button button = new Button(context);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(params);

		MyApplication.getInstance().ImageAdapter(
				button,
				new int[] { R.drawable.selector_btn_cancle,
						R.drawable.selector_btn_cancle_es,
						R.drawable.selector_btn_cancle_en });

		button.setTextColor(Color.WHITE);
		button.setTextSize(16);
		button.setOnClickListener(listener);
		if (buttonLayout.getChildCount() > 0) {
			params.setMargins(50, 0, 0, 0);
			button.setLayoutParams(params);
			buttonLayout.addView(button, 1);
		} else {
			button.setLayoutParams(params);
			buttonLayout.addView(button);
		}
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}

}
