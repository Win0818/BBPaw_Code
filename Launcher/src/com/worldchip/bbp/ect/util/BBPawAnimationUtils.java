package com.worldchip.bbp.ect.util;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class BBPawAnimationUtils {

	private static final long GRASS_ANIM_DURATION = 300;
	
	public interface MyAnimationListener {
		public void onAnimationEnd();
	}
	
	public static void runGrassAnimation(final View view, final MyAnimationListener listener) {
		grassDownAnimation(view,new MyAnimationListener() {
			@Override
			public void onAnimationEnd() {
				// TODO Auto-generated method stub
			grassUpAnimation(view, new MyAnimationListener() {
					@Override
					public void onAnimationEnd() {
						// TODO Auto-generated method stub
						if (listener != null) {
							listener.onAnimationEnd();
						}
					}
				});
			}
		});
	}
	
	public static void grassUpAnimation(View view, final MyAnimationListener listener) {
		TranslateAnimation upAnimation = new TranslateAnimation(0, 0, 20, 0);
		upAnimation.setStartOffset(0);
		upAnimation.setFillAfter(true);
		upAnimation.setDuration(GRASS_ANIM_DURATION);
		upAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onAnimationEnd();
				}
			}
		});
		view.startAnimation(upAnimation);
	}
	
	
	public static void grassDownAnimation(View view, final MyAnimationListener listener) {
		TranslateAnimation downAnimation = new TranslateAnimation(0, 0, 0, 20);
		downAnimation.setStartOffset(0);
		downAnimation.setFillAfter(true);
		downAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onAnimationEnd();
				}
			}
		});
		downAnimation.setDuration(GRASS_ANIM_DURATION);
		view.startAnimation(downAnimation);
	}
	
	public static void runWavesDrawableAnim(View view) {
		if (view != null) {
			AnimationDrawable wavesAnim = (AnimationDrawable)view.getBackground();
			if (wavesAnim != null) {
				wavesAnim.stop();
				wavesAnim.start();
			}
		}
	}
}
