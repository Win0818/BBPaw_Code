package com.worldchip.bbp.ect.activity;

import com.worldchip.bbp.ect.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class StartAlert extends Activity {
	private LinearLayout mLLStartAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock_alert_start);
		initView();
		startAlertMusic();
	}

	private void initView() {
		mLLStartAlert = (LinearLayout) findViewById(R.id.ll_alert_start);
		mLLStartAlert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
	}

	private void startAlertMusic() {

	}
}
