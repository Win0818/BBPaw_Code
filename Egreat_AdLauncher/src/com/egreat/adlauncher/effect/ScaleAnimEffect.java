package com.egreat.adlauncher.effect;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class ScaleAnimEffect {
	private float fromXScale;
	private float toXScale;
	private float fromYScale;
	private float toYScale;
	private long duration;
	private float fromAlpha;
	private float toAlpha;

	// private long offSetDuration;

	/**
	 * 
	 * @param fromXScale
	 *            鍒濆X杞寸缉鏀炬瘮锟�
	 * @param toXScale
	 *            鐩爣X杞寸缉鏀炬瘮锟�
	 * @param fromYScale
	 *            鍒濆Y杞寸缉鏀炬瘮锟�
	 * @param toYScale
	 *            鐩爣Y杞寸缉鏀炬瘮锟�
	 * @param duration
	 *            鍔ㄧ敾鎸佺画鏃堕棿
	 */
	public void setAttributs(float fromXScale, float toXScale,
			float fromYScale, float toYScale, long duration) {
		this.fromXScale = fromXScale;
		this.fromYScale = fromYScale;
		this.toXScale = toXScale;
		this.toYScale = toYScale;
		this.duration = duration;
	}

	public Animation createAnimation() {
		ScaleAnimation anim = new ScaleAnimation(fromXScale, toXScale,
				fromYScale, toYScale, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setFillAfter(true);
		anim.setInterpolator(new AccelerateInterpolator());
		anim.setDuration(duration);
		return anim;
	}

	public Animation alphaAnimation(float fromAlpha, float toAlpha,
			long duration, long offsetDuration) {
		AlphaAnimation anim = new AlphaAnimation(fromAlpha, toAlpha);
		anim.setDuration(duration);
		anim.setStartOffset(offsetDuration);
		anim.setInterpolator(new AccelerateInterpolator());
		return anim;
	}

}
