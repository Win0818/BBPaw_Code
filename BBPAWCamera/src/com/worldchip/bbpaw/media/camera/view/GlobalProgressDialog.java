package com.worldchip.bbpaw.media.camera.view;

import com.worldchip.bbpaw.media.camera.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class GlobalProgressDialog extends Dialog {
	private static GlobalProgressDialog customProgressDialog = null;
	
	public static GlobalProgressDialog getInstance(Context context) {
		if (customProgressDialog == null) {
			customProgressDialog = createDialog(context);
		}
		return customProgressDialog;
	}
	
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
		TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/COMIC.TTF");
		tvMsg.setTypeface(typeFace);
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
    
    public void startProgressDialog() {
		if (customProgressDialog != null) {
			customProgressDialog.show();
		}
	}

    public void stopProgressDialog() {
		if (customProgressDialog != null) {
			if (customProgressDialog.isShowing()) {
				customProgressDialog.dismiss();
			}
			customProgressDialog = null;
		}
	}
	
}