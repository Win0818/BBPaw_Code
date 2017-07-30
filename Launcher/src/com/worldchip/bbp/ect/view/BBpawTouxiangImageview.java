package com.worldchip.bbp.ect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BBpawTouxiangImageview extends ImageView {
	private Context mContext;

	public BBpawTouxiangImageview(Context context) {
		super(context);
		mContext = context;
	}

	public BBpawTouxiangImageview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public BBpawTouxiangImageview(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect(200, 200, 200, 200);
		Paint paint = new Paint();
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(2);
		canvas.drawRect(rect, paint);

	}

}
