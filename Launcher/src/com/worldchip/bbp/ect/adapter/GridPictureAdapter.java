package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.common.BitmapCache;
import com.worldchip.bbp.ect.common.BitmapCache.ImageCallback;
import com.worldchip.bbp.ect.db.PictureData;
import com.worldchip.bbp.ect.entity.PictureInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridPictureAdapter extends BaseAdapter {

	private DataCallback dataCallback = null;
	private Activity act;
	private List<PictureInfo> dataList;
	private List<PictureInfo> shareList = new ArrayList<PictureInfo>();
	private Handler mHandler;
	private BitmapCache cache;
	private String data = null;
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
	
	public static interface DataCallback
	{
		public void onListen(List<PictureInfo> Pictures);
	}
	
	public void setDataCallback(DataCallback listener)
	{
		dataCallback = listener;
	}
	
	public List<PictureInfo> getSharePictureData()
	{
		return shareList;
	}

	public GridPictureAdapter(Activity act, List<PictureInfo> list, Handler mHandler)
	{
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
		this.mHandler = mHandler;
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
		private ImageView pictureimage;
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_share_image_grid_left, null);
			holder.pictureimage = (ImageView) convertView.findViewById(R.id.pictureleftimage);
			holder.selected = (ImageView) convertView.findViewById(R.id.pictureleftisselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final PictureInfo picture = dataList.get(position);
		holder.pictureimage.setTag(picture.getData());
		cache.displayBmp(holder.pictureimage, null, picture.getData(),callback);
		if(picture.isSelected)
		{
			holder.selected.setImageResource(R.drawable.setting_select_image);
		}else{
			holder.selected.setImageResource(-1);
		}
		
		holder.pictureimage.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(picture.isSelected)
				{
					picture.isSelected = false;
					holder.selected.setImageResource(-1);
					shareList.remove(picture);
					boolean a = PictureData.delSharePictureData(act, picture.getData());
					if(a)
					{
						data = picture.getData();
						Message message = Message.obtain(mHandler, 2);
						message.sendToTarget();
					}
				}else{
					picture.isSelected = true;
					holder.selected.setImageResource(R.drawable.setting_select_image);
					shareList.add(picture);
				}
				if(dataCallback != null)
				{
					dataCallback.onListen(shareList);
				}
			}
		});
		//holder.pictureimage.setOn
		return convertView;
	} 
	
	/**
	 * 删除图片
	 */
	public void delItem(PictureInfo pictureInfo)
	{
		dataList.remove(pictureInfo);
		notifyDataSetChanged();
	}
	
   /**
    * 删除列表项
    */
    public void delItem(String data) 
    {  
	    Iterator<PictureInfo> ite = dataList.iterator();
	    while(ite.hasNext())
	    {
		    if(ite.next().getData().equals(data))
		    {
			    ite.remove();  
			    notifyDataSetChanged(); 
		    }
	    }
    }
    
    /**
     * 选中列表项
     */
     public void selectItem(String data) 
     {  
 	    Iterator<PictureInfo> ite = dataList.iterator();
 	    while(ite.hasNext())
 	    {
 	    	PictureInfo info = ite.next();
 		    if(info.getData().equals(data))
 		    {
 		    	info.isSelected = false;  
 			    notifyDataSetChanged(); 
 			    break;
 		    }
 	    }
     }
	
	/**
	 * 获取点击的图片
	 */
	public String getPictureData()
	{
		return data;
	}
	
	/**
	 * 清除
	 */
	public void clearSharePictureList()
	{
		if (shareList != null)
		 {
			shareList.clear();
		 }
	}
}