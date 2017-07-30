package com.worldchip.bbp.ect.common;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

public class SlideView {
	
	/**
	 * 滑动效果
	 * @param p1 向左滑动距离
	 * @param p2 向右滑动距离
	 */
	public static void slideview(final float p1, final float p2,final View view) {
       TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
       animation.setInterpolator(new OvershootInterpolator());
       animation.setDuration(1000);
       animation.setStartOffset(1000);
       animation.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {
           }
           
           @Override
           public void onAnimationRepeat(Animation animation) {
           }
           
           @Override
           public void onAnimationEnd(Animation animation) {
               int left = view.getLeft()+(int)(p2-p1);
               System.out.println("left-------"+left);
               int top = view.getTop();
               int width = view.getWidth();
               int height = view.getHeight();
               view.clearAnimation();
			   view.layout(left, top, left + width, top + height);
           }
       });
       view.startAnimation(animation);
    }
}