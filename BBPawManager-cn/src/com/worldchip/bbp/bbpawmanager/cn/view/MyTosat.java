package com.worldchip.bbp.bbpawmanager.cn.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

public class MyTosat extends Toast {

	public MyTosat(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static Toast makeText(Context context, int resId, int duration) {  
        Toast toast = new Toast(context);  
          
        //获取LayoutInflater对象  
       // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        //由layout文件创建一个View对象  
       // View layout = inflater.inflate(R.layout.newtoast, null);  
          
        //实例化ImageView和TextView对象  
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);  
        toast.setView(imageView);  
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);  
        toast.setDuration(duration);  
        return toast;  
    }  
}
