package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FumeeView extends RelativeLayout {
	private Context mContext;
	private ImageView mYan1, mYan2, mYan3;
	private static final int DELEY_TIME = 800;
	private static final int YC_INDEX_NONE = 0;
	private static final int YC_INDEX_01 = 1;
	private static final int YC_INDEX_02 = 2;
	private static final int YC_INDEX_03 = 3;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case YC_INDEX_NONE:
				mHandler.removeMessages(YC_INDEX_NONE);
				mYan1.setVisibility(View.INVISIBLE);
				mYan2.setVisibility(View.INVISIBLE);
				mYan3.setVisibility(View.INVISIBLE);
				mHandler.sendEmptyMessageDelayed(YC_INDEX_01, DELEY_TIME);
				break;
			case YC_INDEX_01:
				mHandler.removeMessages(YC_INDEX_01);
				mYan1.setVisibility(View.VISIBLE);
				mYan2.setVisibility(View.INVISIBLE);
				mYan3.setVisibility(View.INVISIBLE);
				mHandler.sendEmptyMessageDelayed(YC_INDEX_02, DELEY_TIME);
				break;
			case YC_INDEX_02:
				mHandler.removeMessages(YC_INDEX_02);
				mYan1.setVisibility(View.VISIBLE);
				mYan2.setVisibility(View.VISIBLE);
				mYan3.setVisibility(View.INVISIBLE);
				mHandler.sendEmptyMessageDelayed(YC_INDEX_03, DELEY_TIME);
				break;
			case YC_INDEX_03:
				mHandler.removeMessages(YC_INDEX_03);
				mYan1.setVisibility(View.VISIBLE);
				mYan2.setVisibility(View.VISIBLE);
				mYan3.setVisibility(View.VISIBLE);
				mHandler.sendEmptyMessageDelayed(YC_INDEX_NONE, DELEY_TIME);
				break;
			default:
				break;
			}
		};
	};

	public FumeeView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public FumeeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public FumeeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public void initView() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.yancong_view, null);
		mYan1 = (ImageView) view.findViewById(R.id.yan1);
		mYan2 = (ImageView) view.findViewById(R.id.yan2);
		mYan3 = (ImageView) view.findViewById(R.id.yan3);
		addView(view);
		mHandler.sendEmptyMessage(1);
	}
}
