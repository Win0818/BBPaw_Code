package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter   extends  FragmentPagerAdapter{
	private List<Fragment>  fragmentsList; 

	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
	        super(fm);
	        this.fragmentsList = fragments;
	    }

	    @Override
	    public int getCount() {
	        return fragmentsList.size();
	    }

	    @Override
	    public Fragment getItem(int arg0) {
	        return fragmentsList.get(arg0);
	    }

	    @Override
	    public int getItemPosition(Object object) {
	        return super.getItemPosition(object);
	    }

}
