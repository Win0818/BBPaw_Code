package com.worldchip.bbp.bbpawmanager.cn.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


public class HelpPagerAdapter extends PagerAdapter{

	private ArrayList<View> mViews;//存放View的ArrayList  
	
    /* 
     * ViewAdapter构�?函数 
     */  
    public HelpPagerAdapter(ArrayList<View> Views)  
    {  
        this.mViews=Views; 
    }  
  
      
    /* 
     * 返回View的个�?
     */  
    @Override  
    public int getCount()   
    {  
        if(mViews!=null)  
        {  
            return mViews.size();  
        }  
        return 0;  
    }  
  
    /* 
     * �?��View 
     */  
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object)   
    {  
        container.removeView(mViews.get(position));  
    }  
  
    /* 
     * 初始�?
     */  
    @Override  
    public Object instantiateItem(ViewGroup container, int position)   
    {  
    	//Log.e("lee", " instantiateItem position == "+position);
    	View view = mViews.get(position);
        container.addView(view);  
        return view;  
    }  
      
    /* 
     * 判断View是否来自Object 
     */  
    @Override  
    public boolean isViewFromObject(View view, Object object)   
    {  
        return (view==object);  
    }  
    
}
