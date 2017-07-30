package com.beanpai.egr.shopping.view;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.beanpai.egr.shopping.utils.Common;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 兑换数量
 */
public class ExchangeQuantityView extends FrameLayout implements View.OnClickListener,View.OnKeyListener {

	private Context mContext;
	private Handler mHandler;
	
	private EditText mEtExchangeQuantityEditText;
	private TextView mTvQuantityNextStepTextView;
	
	public ExchangeQuantityView(Context context,Handler mHandler) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		
		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.quantity_view,null);
		addView(localView);
		
		mEtExchangeQuantityEditText = (EditText) localView.findViewById(R.id.et_exchange_quantity);
		
		mTvQuantityNextStepTextView = (TextView) localView.findViewById(R.id.tv_quantity_next_step);
		
		initListener();
	}

	//绑定事件
	private void initListener() 
	{
		mTvQuantityNextStepTextView.requestFocus();
		
		mEtExchangeQuantityEditText.setOnKeyListener(this);
		mTvQuantityNextStepTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId())
		{
			case R.id.tv_quantity_next_step://确认兑换
				Message mMessage = new Message();
				mMessage.what = Common.EXCHANGE_QUANTITY;
				mMessage.obj =  mEtExchangeQuantityEditText.getText().toString().trim();
				mHandler.sendMessage(mMessage);
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) 
	{
		Log.e("onKey", "onKey");
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_DPAD_LEFT:
				Log.e("onKey", "onKey---LEFT="+mEtExchangeQuantityEditText.isFocusableInTouchMode());
				if(mEtExchangeQuantityEditText.isFocusable() && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					int mQuantity = Integer.parseInt(mEtExchangeQuantityEditText.getText().toString().trim());
					if(mQuantity > 1)
					{
						mQuantity = mQuantity - 1;
					}else{
						mQuantity = 1;
					}
					mEtExchangeQuantityEditText.setText("" + mQuantity);
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				Log.e("onKey", "onKey---RIGHT="+mEtExchangeQuantityEditText.isFocusable());
				if(mEtExchangeQuantityEditText.isFocusable() && event.getAction() == KeyEvent.ACTION_DOWN)
				{
					int mQuantity = Integer.parseInt(mEtExchangeQuantityEditText.getText().toString().trim());
					if(mQuantity < 100)
					{
						mQuantity = mQuantity + 1;
					}else{
						mQuantity = 100;
					}
					mEtExchangeQuantityEditText.setText("" + mQuantity);
				}
				break;
			default:
				break;
		}
		return false;
	}
}