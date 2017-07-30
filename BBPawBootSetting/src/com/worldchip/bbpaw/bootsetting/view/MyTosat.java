package com.worldchip.bbpaw.bootsetting.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpaw.bootsetting.R;

public class MyTosat extends Toast {

	public MyTosat(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static Toast makeText(Context context, int resId, int duration) {  
        Toast toast = new Toast(context);  
          
        //获取LayoutInflater对象  
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        //由layout文件创建一个View对象  
        View view = inflater.inflate(R.layout.custom_toast_layout, null);  
        TextView messge = (TextView)view.findViewById(R.id.toast_messge);
        messge.setText(resId);
        toast.setView(view);  
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);  
        toast.setDuration(duration);  
        return toast;  
    }  
}
