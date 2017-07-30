package com.worldchip.bbp.ect.adapter;

import java.io.File;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.common.BitmapCache;
import com.worldchip.bbp.ect.common.BitmapCache.ImageCallback;
import com.worldchip.bbp.ect.entity.ImageItem;
import com.worldchip.bbp.ect.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter {

	private Activity act;
	private List<ImageItem> dataList;
	private BitmapCache cache;
	private ImageCallback callback = new ImageCallback()
	{
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,Object... params)
		{
			if (imageView != null && bitmap != null)
			{
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) 
				{
					((ImageView) imageView).setImageBitmap(bitmap);
				}
			}
		}
	};

	public ImageGridAdapter(Activity act, List<ImageItem> list)
	{
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}

	@Override
	public int getCount() 
	{
		int count = 0;
		if (dataList != null) 
		{
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) 
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	class Holder 
	{
		private ImageView iv;
		@SuppressWarnings("unused")
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.iv.setTag(item.getImagePath());
		cache.displayBmp(holder.iv, item.getThumbnailPath(), item.getImagePath(),callback);
		holder.iv.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try {
					Log.e("lee", "image click ---- "+item.getImagePath());
					Uri uri = Uri.fromFile(new File(item.getImagePath())); 
	            	Intent intent = new Intent();
	            	intent.setAction("com.android.camera.action.CROP"); 
		 	        intent.setDataAndType(uri, "image/*");
		 	        intent.putExtra("crop", "true");
		 	        intent.putExtra("aspectX", 1);
		 	        intent.putExtra("aspectY", 1);
		 	        intent.putExtra("outputX", 200);
		 	        intent.putExtra("outputY", 200);
		 	        intent.putExtra("return-data", true);
		 	        act.startActivityForResult(intent, Utils.CROP_IMAGE_REQUEST_CODE);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("lee", "erro picture");
				}
				
			}
		});
		return convertView;
	}
}