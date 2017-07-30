package com.worldchip.bbp.ect.activity;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.CustomImageAdapter;
import com.worldchip.bbp.ect.adapter.HorizontalScrollView;
import com.worldchip.bbp.ect.adapter.HorizontalScrollViewCostom;
import com.worldchip.bbp.ect.adapter.SystemImageAdapter;
import com.worldchip.bbp.ect.common.AlbumHelper;
import com.worldchip.bbp.ect.entity.ImageBucket;
import com.worldchip.bbp.ect.util.HttpCommon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;

public class ImageNameActivity extends Activity implements OnClickListener{

	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private ImageView mImageBack;
	private HorizontalScrollView mHorizontalScrollViewSystem;
	private HorizontalScrollViewCostom mHorizontalScrollViewCostom;
	
	private AlbumHelper helper;
	private List<ImageBucket> dataList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_album_main);
		
		HttpCommon.hideSystemUI(ImageNameActivity.this,true);
		
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		try {
			initializeView();
			
			initData();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//initializeView();
		
		//initData();
		mImageBack.setOnClickListener(this);
	}

	private void initializeView() 
	{
		mImageBack = (ImageView) findViewById(R.id.setting_image_back);
		//SYSTEM
		mHorizontalScrollViewSystem = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview_system);
		mHorizontalScrollViewSystem.setImageView((ImageView) findViewById(R.id.iv_system_pre),(ImageView) findViewById(R.id.iv_system_next));
		//COSTOM
		mHorizontalScrollViewCostom = (HorizontalScrollViewCostom) findViewById(R.id.horizontal_scrollview_costom);
		mHorizontalScrollViewCostom.setImageView((ImageView) findViewById(R.id.iv_costom_pre), (ImageView) findViewById(R.id.iv_costom_next));
	}
	
	private void initData() 
	{
		//system
		mHorizontalScrollViewSystem.setAdapter(new SystemImageAdapter(ImageNameActivity.this));
		dataList = helper.getImagesBucketList(true);	
		Log.e("lee", "ImageNameActivity initData == "+dataList.size() +" dataList index 0 count == "+dataList.get(0).getCount());
		mHorizontalScrollViewCostom.setAdapter(new CustomImageAdapter(ImageNameActivity.this, dataList));
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId()) 
		{
			case R.id.setting_image_back:
				//Intent intent = new Intent();     
				//intent.setClass(ImageNameActivity.this, MainActivity.class);
				//ImageNameActivity.this.setResult(500, intent);
				ImageNameActivity.this.finish();
				break;
			default:
				break;
		}
	}
}