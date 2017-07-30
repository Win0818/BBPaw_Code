package com.egreat.adlauncher.adapter;

import java.util.ArrayList;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MarqueeAdapter extends PagerAdapter {
	
	private ArrayList<ImageView> mPageViews = new ArrayList<ImageView>();
	
	/**
	 * @param mPageViews
	 */
	public void setData(ArrayList<ImageView> mPageViewsList)
	{
		if (mPageViews != null && !mPageViews.isEmpty()) {
			mPageViews.clear();
		}
		this.mPageViews = mPageViewsList;
	}
	@Override  
    public int getCount()
	{  
        return mPageViews.size();  
    }  

    @Override  
    public boolean isViewFromObject(View arg0, Object arg1)
    {  
        return arg0 == arg1;  
    }  

    @Override  
    public int getItemPosition(Object object) 
    {  
        return super.getItemPosition(object);  
    }  

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
    	View view2 = mPageViews.get(position);
    	emptyImageDrawble(view2);
    	view.removeView(view2);
    }
    
	private void emptyImageDrawble(View view) {
		if (view != null && view instanceof ImageView) {
			((ImageView) view).setImageDrawable(null);
		}
	}
    
    @Override  
    public Object instantiateItem(ViewGroup view, int position) {  
    	view.addView(mPageViews.get(position));  
        return mPageViews.get(position);  
    }  

    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1)
    {  

    }  

    @Override  
    public Parcelable saveState() 
    {  
        return null;  
    }  

    @Override  
    public void startUpdate(View arg0) 
    {  

    }  

    @Override  
    public void finishUpdate(View arg0)
    {  

    } 
}