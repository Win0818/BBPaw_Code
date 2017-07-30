package com.mgle.member.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class AnimationUtils {

	public static Animation createScaleAnimation(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, long paramLong)
	  {
	    float f1 = paramFloat1;
	    float f2 = paramFloat2;
	    float f3 = paramFloat3;
	    float f4 = paramFloat4;
	    int i = 1;
	    float f5 = 0.5F;
	    ScaleAnimation localScaleAnimation = new ScaleAnimation(f1, f2, f3, f4, 1, 0.5F, i, f5);
	    AccelerateInterpolator localAccelerateInterpolator = new AccelerateInterpolator();
	    localScaleAnimation.setInterpolator(localAccelerateInterpolator);
	    localScaleAnimation.setDuration(paramLong);
	    localScaleAnimation.setFillAfter(true);
	    return localScaleAnimation;
	  }

	  public Animation createAlphaAnimation(float paramFloat1, float paramFloat2, long paramLong1, long paramLong2)
	  {
	    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
	    localAlphaAnimation.setStartOffset(100L);
	    AccelerateInterpolator localAccelerateInterpolator = new AccelerateInterpolator();
	    localAlphaAnimation.setInterpolator(localAccelerateInterpolator);
	    localAlphaAnimation.setDuration(paramLong1);
	    return localAlphaAnimation;
	  }
	
}