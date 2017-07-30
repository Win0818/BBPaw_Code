package com.worldchip.bbp.ect.activity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.worldchip.bbp.ect.R;

public class PictureAllScreen extends Activity {
	private  String mPth;
	private ImageView mPicture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getpath();
		setWindowStyle();
		setContentView(R.layout.pitureallscrenn_layout);
		mPicture=(ImageView)findViewById(R.id.sv_picture_player);
		
		//Bitmap mBitmap = readBitMap();
		mPicture.setImageBitmap(BitmapFactory.decodeFile(mPth));
		//mPicture.setImageBitmap(mBitmap);
		
	}
	public  Bitmap readBitMap(){  
		Log.d("PictureAllScreen", "--------+++++++---readBitMap()--->");
	    BitmapFactory.Options opt = new BitmapFactory.Options();  
	    opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	    opt.inPurgeable = true;  
	    opt.inInputShareable = true; 
	    InputStream stream = null;
	       //获取资源图片  
	    try {
			 stream = new FileInputStream(new File(mPth));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return BitmapFactory.decodeStream(stream,null,opt);  
	  
	}  

	private void getpath() {
		mPth=this.getIntent().getStringExtra("singless_path");		
	}

	private void setWindowStyle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		this.finish();
		return super.onTouchEvent(event);
	}
}
