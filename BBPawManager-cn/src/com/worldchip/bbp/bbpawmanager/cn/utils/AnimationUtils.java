package com.worldchip.bbp.bbpawmanager.cn.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

public class AnimationUtils {
	public static void searchSpreadAnim(final View animView) {
		if (animView == null)
			return;
		animView.setVisibility(View.VISIBLE);
		ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f,1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator()); 
		anim.setDuration(100);
		anim.setFillAfter(true);
		animView.startAnimation(anim);
	}
	
	public static void searchShrinkAnim(final View animView) {
		if (animView == null)
			return;
		ScaleAnimation anim = new ScaleAnimation(1.0f, 0.0f,1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f,Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator()); 
		anim.setDuration(100);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
			}
		});
		animView.startAnimation(anim);
		
	}
}
