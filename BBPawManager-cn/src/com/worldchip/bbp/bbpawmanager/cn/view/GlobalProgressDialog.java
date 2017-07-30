package com.worldchip.bbp.bbpawmanager.cn.view;



import com.worldchip.bbp.bbpawmanager.cn.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class GlobalProgressDialog extends Dialog {
	private static GlobalProgressDialog customProgressDialog = null;
	
	public GlobalProgressDialog(Context context){
		super(context);
	}
	
	public GlobalProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static GlobalProgressDialog createDialog(Context context){
		customProgressDialog = new GlobalProgressDialog(context,R.style.GlobalProgressDialog);
		customProgressDialog.setContentView(R.layout.global_progress_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(false);
		
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public GlobalProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public GlobalProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}