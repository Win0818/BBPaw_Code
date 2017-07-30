package com.worldchip.bbpawphonechat.activity;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

public class MyProtocolContentsActivity extends  Activity {
	private  WebView  mWebView;
	String assetsUri = "";
	private TextView  mIv_sign_up;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocol_contents);
		mWebView  = (WebView)findViewById(R.id.webview_compontent);	
		mWebView.getSettings().setJavaScriptEnabled(true);
		mIv_sign_up = (TextView) findViewById(R.id.chat_to_name1);
		
		MyApplication.getInstance().ImageAdapter(
				mIv_sign_up,
				new int[] { R.drawable.register_title_img,
						R.drawable.register_title_img_es,
						R.drawable.register_title_img_en });
		
		if(MyApplication.getInstance().system_local_language == 0){
			assetsUri = "file:///android_asset/www/protocol_cn.html";
		}else if(MyApplication.getInstance().system_local_language == 1){
			assetsUri = "file:///android_asset/www/protocol_es.html";
		}else {
			assetsUri = "file:///android_asset/www/protocol_en.html";
		}
		mWebView.loadUrl(assetsUri);
		findViewById(R.id.iv_protocol_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                finish();				
			}
		});
		
	}
	

}
