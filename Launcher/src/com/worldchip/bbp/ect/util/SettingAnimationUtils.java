package com.worldchip.bbp.ect.util;

import com.worldchip.bbp.ect.R;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SettingAnimationUtils {
    private static boolean isRunning = false;
	private static final int START_ANIM_DURATIONS= 250;//草堆放大时间
	private static int  count = 0;
	
     public static void startAnimation(final ImageView view,final long startOffset) {
    	view.setBackgroundResource(R.drawable.ic_grass);
 		ScaleAnimation ta = new ScaleAnimation(1.0f,1.13f, 1.0f, 1.13f,1.0f, 1.0f);
 		if (count == 2) {
 			ta.setStartOffset(startOffset);
 			count = 0;
 		}
 		//ta.setStartOffset(startOffset);//延时�?定时间开始动�?
 		//ta.setFillAfter(true);//停留在播放完动画界面
 		ta.setDuration(START_ANIM_DURATIONS);
 		ta.setAnimationListener(new AnimationListener() {
 			@Override
 			public void onAnimationStart(Animation arg0) {
 				isRunning = true;
 			}
 			@Override
 			public void onAnimationRepeat(Animation arg0) {}
 			@Override
 			public void onAnimationEnd(Animation arg0) {
 				 count++;
 				if (count < 3) {
 					startSuoxiaoAnimation( view,startOffset);
 				}
 			}
 		});
 		view.startAnimation(ta);
 	}
 	public static void startSuoxiaoAnimation(final ImageView view,final long startOffset) {
 		view.setBackgroundResource(R.drawable.ic_grass_2);
 		ScaleAnimation ta = new ScaleAnimation(1.13f, 1.0f, 1.13f, 1.0f);
 		//ta.setFillAfter(true);//停留在播放完动画界面
 		
 		ta.setDuration(START_ANIM_DURATIONS);
 		ta.setAnimationListener(new AnimationListener() {
 			@Override
 			public void onAnimationStart(Animation arg0) {
 			}
 			@Override
 			public void onAnimationRepeat(Animation arg0) {
 			}
 			@Override
 			public void onAnimationEnd(Animation arg0) {
 				startAnimation(view,startOffset);
 			}
 		});
 		view.startAnimation(ta);
 	}
 	public static boolean isRunningAnimation() {
 		return isRunning;
 	}

 }

