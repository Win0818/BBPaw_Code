package com.worldchip.bbp.ect.activity;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.ImageGridAdapter;
import com.worldchip.bbp.ect.common.AlbumHelper;
import com.worldchip.bbp.ect.entity.ImageItem;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class ImageGridActivity extends Activity implements OnTouchListener{

	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private TextView mImageCancel;
	private LinearLayout mGridParentView;
	
	private AlbumHelper helper;
	private List<ImageItem> dataList = null;
	private ImageGridAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_image_main);
		
		HttpCommon.hideSystemUI(ImageGridActivity.this,true);
		
		try {
			//锟斤拷始锟斤拷锟截硷拷
			initView();
			//锟斤拷锟阶帮拷锟�
			initData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		//锟襟定硷拷锟斤拷
		mImageCancel.setOnTouchListener(this);
	}

	/***
	 * 锟斤拷始锟斤拷锟截硷拷
	 */
	private void initView() 
	{
		//取锟斤拷
		mImageCancel = (TextView) findViewById(R.id.image_cancel);
		mGridParentView = (LinearLayout) findViewById(R.id.imageGridParentView);
	}
	
	/**
	 * 锟斤拷锟阶帮拷锟�
	 */
	@SuppressWarnings("unchecked")
	private void initData() 
	{
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList);
		
		//锟斤拷取锟斤拷幕锟斤拷锟�
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int count = adapter.getCount();
		int width = (dm.widthPixels - 15) / 4;//锟斤拷锟�
		int windowwindth = 0;
		int size = count / 2;
		if (count % 2 == 1)
		{
			size = size + 1;
			windowwindth = width * (size + 1) + size * 5;
		}else{
			windowwindth = width * size + (size - 1) * 5;
		}
		GridView mGridView = new GridView(ImageGridActivity.this);
		LayoutParams params = new LayoutParams(windowwindth, 550);
		mGridView.setLayoutParams(params);  
		mGridView.setColumnWidth(width);  
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setHorizontalSpacing(5);  
		mGridView.setVerticalSpacing(5); 
		mGridView.setScrollBarStyle(View.GONE);
		mGridView.setStretchMode(GridView.NO_STRETCH); 
		mGridView.setVerticalScrollBarEnabled(false);
		mGridView.setHorizontalScrollBarEnabled(false);
		mGridView.setNumColumns(size); 
		mGridView.setAdapter(adapter);
		mGridParentView.addView(mGridView);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {    
		Log.e("lee", "ImageGridActivity onActivityResult ---- "+requestCode +" data == "+(data==null?"NULL":data) +" resultCode == "+resultCode);
		try {
			switch (requestCode) {
	        case Utils.CROP_IMAGE_REQUEST_CODE: 
	        	if(data == null) return;
                Bitmap bitmap = data.getParcelableExtra("data"); 
                Log.e("lee", "ImageGridActivity SavaImage to local requestCode =  "+requestCode +" bitmap == "+bitmap);
                HttpCommon.SavaImage(bitmap, getPackageName()); 
                Intent intent = new Intent();
                intent.setClass(ImageGridActivity.this, MainActivity.class);
                intent.putExtra("data", data);
                ImageGridActivity.this.setResult(Utils.LOAD_CUSTOM_IMAGE_REQUEST_CODE, intent);
                ImageGridActivity.this.finish();
	    		break;
	        default:  
	        	break;
        }
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
    	
    }

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		switch (event.getAction())
		{ 	  
	        case MotionEvent.ACTION_DOWN://锟斤拷锟斤拷
	        	mImageCancel.setBackgroundColor(Color.parseColor("#282828"));
	        	Intent intent = new Intent(ImageGridActivity.this,ImageNameActivity.class);  
				startActivity(intent);
				finish();
				break;
	        case MotionEvent.ACTION_UP://抬锟斤拷
	        	mImageCancel.setBackgroundColor(Color.parseColor("#000000"));
				break;
	        default:
	            break;
		}
		return true;
	}
}