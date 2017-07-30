package com.worldchip.bbp.ect.adapter;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.util.HttpCommon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private Context context;  
    private LayoutInflater mInflater;
    private int selectedPosition = -1;// 选中的位置
   
	public GridViewAdapter(Context context) 
	{
		this.context = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override  
    public int getCount() 
	{  
        return HttpCommon.number.length;  
    }  

    @Override  
    public Object getItem(int position)
    {  
        return HttpCommon.number[position];  
    }  

    @Override  
    public long getItemId(int position) 
    {  
        return position;  
    }  
    
    public void setSelectedPosition(int position) 
    {  
		selectedPosition = position;  
    }

    @Override  
    public View getView(int position, View convertView, ViewGroup parent)
    {  
        if (convertView == null) 
        {
     	    mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.patriarch_control_passlock_counter_item, null);       
        }
        String number = HttpCommon.number[position];
        if(selectedPosition == position && position != 9 && position != 11)
		{
        	((TextView) convertView).setText(number);
		}else{
			if(position == 9)
			{
				convertView.setBackgroundResource(R.drawable.passlock_delete_all);
			}else if(position == 10){
				((TextView) convertView).setText(number);
			}else if(position == 11){
				convertView.setBackgroundResource(R.drawable.passlock_delete_one);
			}else{
				((TextView) convertView).setText(number);
			}
		}
        return convertView;   
    }  
}