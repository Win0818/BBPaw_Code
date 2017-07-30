package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;

public class MyProgressDialog   extends  Dialog{
	private static final String TAG = "CHRIS";
	private Context mContext; 
	private  static MyProgressDialog  myProgressDialog = null;

	private MyProgressDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		
	}
	
   public static MyProgressDialog  createProgressDialog(Context context){
	    myProgressDialog = new MyProgressDialog(context,R.style.myProgressDialogStyle);
	    
	    myProgressDialog.setContentView(R.layout.dialog_myprogress_layout);
	    
	    myProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
	    
	    myProgressDialog.setCanceledOnTouchOutside(false);
	    
	    ImageView  dialogLoadingiv  = (ImageView) myProgressDialog.findViewById(R.id.iv_my_dialog_loading_icon);
	    
	    Animation  loadingRotateAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_chicking_spin);
		
	    LinearInterpolator lin = new LinearInterpolator();  
	    loadingRotateAnim.setInterpolator(lin);  
		dialogLoadingiv.startAnimation(loadingRotateAnim);
	    return myProgressDialog;
   }
   
   
   public MyProgressDialog  setMessage(String message){
	   TextView  dialogLoadingtv = (TextView) myProgressDialog.findViewById(R.id.tv_my_dialog_note_txt);
	   if(dialogLoadingtv != null){
	      dialogLoadingtv.setText(message);
	   }
       return  myProgressDialog;	   
   }
	

}
