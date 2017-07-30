package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

public class EditboxView extends PopupWindow{
	private Context mContext;
	public EditboxView(Context context){
		mContext=context;
		initView(mContext);
	}
	private void initView(Context context) {
        View view=LayoutInflater.from(context).inflate(R.layout.bb_edit_box_layout,null);
        setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
	}
 
}
