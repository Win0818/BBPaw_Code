package com.worldchip.bbp.ect.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.util.Utils;

public class StoreOrChargeActivity extends Activity implements OnClickListener {

	private Button storeButton;
	private Button chargeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_charge_layout);
		initView();
	}

	private void initView() {
		storeButton = (Button) findViewById(R.id.store_button);
		storeButton.setBackgroundResource(Utils.getResourcesId(MyApplication
				.getAppContext(), "data_usb_style", "drawable"));
		
		chargeButton = (Button) findViewById(R.id.charge_button);
		chargeButton.setBackgroundResource(Utils.getResourcesId(MyApplication
				.getAppContext(), "charge_usb_style", "drawable"));
		storeButton.setOnClickListener(this);
		chargeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.store_button:
			
			// 存储数据，挂载SD卡到电脑
			break;
		case R.id.charge_button:
			startActivity(new Intent(StoreOrChargeActivity.this,
					ChargingActivity.class));// 跳转到充电界面
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		StoreOrChargeActivity.this.finish();
	}
}
