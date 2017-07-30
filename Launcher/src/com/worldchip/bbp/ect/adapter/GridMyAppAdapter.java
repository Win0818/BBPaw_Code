package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.AppData;
import com.worldchip.bbp.ect.entity.AppInfo;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridMyAppAdapter extends BaseAdapter {

	private List<AppInfo> dataList;
	private Activity act;
	private boolean isDbClick = false;   //是否正在长按状态
	
	public GridMyAppAdapter(Activity act,List<AppInfo> mApps) 
	{
		this.act = act;
		this.dataList = mApps;
	}
	
	/**
	 * 获取删除的APP包名
	 */
	public void delPackageName(AppInfo appInfo)
	{
		dataList.remove(appInfo);
		notifyDataSetChanged();
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
        return dataList.get(position);  
    }  

    @Override  
    public long getItemId(int position) 
    {  
        return position;  
    }  
    
    class Holder 
	{
		private ImageView myappimage;
		private ImageView selected;
	}

    @Override  
    public View getView(int position, View convertView, ViewGroup parent)
    {  
    	final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_myapp_grid, null);
			holder.myappimage = (ImageView) convertView.findViewById(R.id.myappimage);
			holder.selected = (ImageView) convertView.findViewById(R.id.myappisselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final AppInfo info = dataList.get(position);
		if(isDbClick)
		{
			holder.selected.setImageResource(R.drawable.share_apk_error);
		}else{
			holder.selected.setImageBitmap(null);
		}
		holder.myappimage.setImageDrawable(AppData.byteToDrawable(info.getIcon())); 
        return convertView;  
    }
    
    public void setIsLongClick(boolean isDbClick)
    {
    	this.isDbClick = isDbClick;
    }
    
    public boolean getIsLongClick()
    {
    	return this.isDbClick;
    }
}