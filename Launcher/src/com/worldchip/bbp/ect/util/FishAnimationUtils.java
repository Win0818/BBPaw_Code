package com.worldchip.bbp.ect.util;


import com.worldchip.bbp.ect.R;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class FishAnimationUtils {

	private static boolean isRunning = false;
	
	public static final int BASE_DURATION = 50;//水滴显示时间
	public static final int START_NEXT_ANIMATION_OFFSET = 1000;//下一次小鱼移动延迟时�?
	public static final int BACK_ANIM_DURATION = 1000;//小鱼返回动画时间
	public static final int START_ANIM_DURATION = 1000;//小鱼移动时间
	private static AnimationDrawable mFishJumpDownAnim = null;
	
	public static void startAnimation(final ImageView view,final ViewGroup viewGroup,long startOffset) {
		view.setBackgroundResource(R.drawable.ic_fish_02);
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -30);
		ta.setStartOffset(startOffset);//延时�?定时间开始动�?
		ta.setFillAfter(true);//停留在播放完动画界面
		ta.setDuration(START_ANIM_DURATION);
		ta.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				isRunning = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				isRunning = false;
				startWaterAnimation(viewGroup, view);
			}
		});
		view.startAnimation(ta);
	}

	public static void startWaterAnimation(final ViewGroup viewGroup,final ImageView view) {
		if (viewGroup == null)
			return;
			AnimationSet animationSet = new AnimationSet(false);
			int childCount = viewGroup.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = viewGroup.getChildAt(i);
				childView.setVisibility(View.VISIBLE);
				if (childView != null) {
					long duration = 0;
					long startOffset = 0;
					int repeatCount = 0;
					switch (childView.getId()) {
					case R.id.water_01:
						duration = BASE_DURATION;
			 			break;
					case R.id.water_02:
						startOffset = BASE_DURATION;
						duration = BASE_DURATION;
						break;
					case R.id.water_03:
						duration = BASE_DURATION;
						startOffset = BASE_DURATION * 2;
						break;
					case R.id.water_04:
						duration = 3000;
						break;
					}
					Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);  
					alphaAnimation.setDuration(duration); 
					alphaAnimation.setStartOffset(startOffset);
					alphaAnimation.setFillAfter(true);
					alphaAnimation.setRepeatCount(repeatCount);
					alphaAnimation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}
						@Override
						public void onAnimationRepeat(Animation animation) {}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							Animation alphaAnimation = new AlphaAnimation(1.0f, 0f);  
							if (childView.getId() == R.id.water_04) {
								alphaAnimation.setDuration(2000); 
								alphaAnimation.setFillAfter(true);
								childView.setAnimation(alphaAnimation);
								int childCount = viewGroup.getChildCount();
								for (int i = 0; i < childCount; i++) {
									View view = viewGroup.getChildAt(i);
									if (view.getId() != R.id.water_04) {
										view.clearAnimation();
										view.setVisibility(View.GONE);
									}
								}
							} 
						}
					});
					childView.setAnimation(alphaAnimation);
					animationSet.addAnimation(alphaAnimation);
				}
			}
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					startBackAnimation(view, viewGroup, 50);
				}
			});
			viewGroup.startAnimation(animationSet);
	}
	
	public static void startBackAnimation(final ImageView view,
			final ViewGroup viewGroup, long startOffset) {
		view.setBackgroundResource(R.anim.fish_jump_down_anim);
		if (mFishJumpDownAnim == null) {
			mFishJumpDownAnim=(AnimationDrawable)view.getBackground();
		}
		
		TranslateAnimation ta = new TranslateAnimation(0, 0, -30, 0);
		ta.setStartOffset(startOffset);
		ta.setFillAfter(true);
		ta.setDuration(BACK_ANIM_DURATION);
		ta.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				if (mFishJumpDownAnim != null) {
					mFishJumpDownAnim.start();
				}
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (mFishJumpDownAnim != null) {
					mFishJumpDownAnim.stop();
				}
				//startAnimation(view, viewGroup, START_NEXT_ANIMATION_OFFSET);
			}
		});
		view.startAnimation(ta);
	}

	public static boolean isRunningAnimation() {
		return isRunning;
	}

}
