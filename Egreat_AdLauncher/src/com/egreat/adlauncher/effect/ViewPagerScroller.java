package com.egreat.adlauncher.effect;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ViewPagerScroller extends Scroller {

	private int mScrollDuration = 2000;

	public void setScrollDuration(int i) {
		// TODO Auto-generated method stub
		this.mScrollDuration = i;
	}

	public ViewPagerScroller(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ViewPagerScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
		// TODO Auto-generated constructor stub
	}

	public ViewPagerScroller(Context context, Interpolator interpolator,
			boolean flywheel) {
		super(context, interpolator, flywheel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		// TODO Auto-generated method stub
		super.startScroll(startX, startY, dx, dy,mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		// TODO Auto-generated method stub
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	public void initViewPagerScroll(ViewPager viewPager) {

		try {

			Field mScroller = ViewPager.class.getDeclaredField("mScroller");

			mScroller.setAccessible(true);

			mScroller.set(viewPager, this);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
