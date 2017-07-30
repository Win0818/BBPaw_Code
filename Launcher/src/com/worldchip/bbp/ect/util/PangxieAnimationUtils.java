package com.worldchip.bbp.ect.util;

import com.worldchip.bbp.ect.R;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 杞洏鍔ㄧ敾
 * @author Administrator
 *
 */
public class PangxieAnimationUtils {

	private static boolean isRunning = false;
	
	public static void startInRotateAnimation(final View view,long startOffset) {
		TranslateAnimation ta = new TranslateAnimation(0, 70, 0, 0);
		ta.setStartOffset(startOffset);
		ta.setFillAfter(true);
		ta.setDuration(2000);
		ta.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				isRunning = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				isRunning = false;
				startOutRotateAnimation(view, 1500);
			}
		});
		view.startAnimation(ta);
	}

	public static void startOutRotateAnimation(final View view,
			long startOffset) {
		TranslateAnimation ta = new TranslateAnimation(70, 0, 0, 0);
		ta.setStartOffset(startOffset);
		ta.setFillAfter(true);
		ta.setDuration(2000);
		ta.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				isRunning = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				isRunning = false;
			}
		});
		view.startAnimation(ta);
	}

	public static boolean isRunningAnimation() {
		// return isInRunning || isOutRunning;
		return isRunning;
	}

}
