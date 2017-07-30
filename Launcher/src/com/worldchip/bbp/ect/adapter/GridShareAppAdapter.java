package com.worldchip.bbp.ect.adapter;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.AppData;
import com.worldchip.bbp.ect.entity.AppInfo;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridShareAppAdapter extends BaseAdapter {

	private List<AppInfo> dataList;
	private Activity act;
	
	public GridShareAppAdapter(Activity act,List<AppInfo> mApps) 
	{
		this.act = act;
		this.dataList = mApps;
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
		private ImageView appimage;
	}

    @Override  
    public View getView(int position, View convertView, ViewGroup parent)
    {  
    	final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_app_grid, null);
			holder.appimage = (ImageView) convertView.findViewById(R.id.appimage);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		AppInfo info = dataList.get(position);
		holder.appimage.setImageDrawable(AppData.byteToDrawable(info.getIcon())); 
        return convertView;  
    }  
}