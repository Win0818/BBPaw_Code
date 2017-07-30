package com.worldchip.bbp.ect.adapter;

import java.io.Serializable;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.ImageGridActivity;
import com.worldchip.bbp.ect.common.BitmapCache;
import com.worldchip.bbp.ect.common.BitmapCache.ImageCallback;
import com.worldchip.bbp.ect.entity.ImageBucket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomImageAdapter extends BaseAdapter{

	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private Activity context;
	/**
	 * Í¼Æ¬¼¯ÁÐ±í
	 */
	private List<ImageBucket> dataList;
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
	
	public CustomImageAdapter(Activity context, List<ImageBucket> list) 
	{
		this.context = context;
		dataList = list;
		cache = new BitmapCache();
	}
	
	@Override
	public int getCount()
	{
		return dataList.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		Holder holder;
		if (convertView == null)
		{
			holder = new Holder();
			convertView = View.inflate(context, R.layout.setting_album_custom_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.custom_image);
			holder.selected = (ImageView) convertView.findViewById(R.id.custom_isselected);
			holder.name = (TextView) convertView.findViewById(R.id.custom_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ImageBucket item = dataList.get(position);
		holder.name.setText(item.getBucketName() + "   ("+ item.getCount() +")");
		holder.selected.setVisibility(View.VISIBLE);
		if (item.getImageList() != null && item.getImageList().size() > 0)
		{
			String thumbPath = item.getImageList().get(0).getThumbnailPath();
			String sourcePath = item.getImageList().get(0).getImagePath();
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageBitmap(null);
		}
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Intent intent = new Intent(context,ImageGridActivity.class);
				intent.putExtra(EXTRA_IMAGE_LIST,(Serializable)dataList.get(position).getImageList());
				context.startActivity(intent);
				context.finish();
			}
		});
		return convertView;
	}

	class Holder 
	{
		private ImageView iv;
		private ImageView selected;
		private TextView name;
	}
}