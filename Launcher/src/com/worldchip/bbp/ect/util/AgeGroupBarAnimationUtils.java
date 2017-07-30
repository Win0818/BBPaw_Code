package com.worldchip.bbp.ect.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;

/**
 * 杞洏鍔ㄧ敾
 * @author Administrator
 *
 */
public class AgeGroupBarAnimationUtils {

	private static boolean isRunning = false;

	/**
	 * 杞洏鍑烘潵鍔ㄧ�?
	 */
	public static void startInRotateAnimation(final ViewGroup viewGroup,
			long startOffset) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			viewGroup.getChildAt(i).setEnabled(true); 
		}

		RotateAnimation anim = new RotateAnimation(180f, 0f,
				Animation.RELATIVE_TO_SELF, 0f, // x杞翠笂鐨勫��?
				Animation.RELATIVE_TO_SELF, 0.5f); // y杞翠笂鐨勫��?
		anim.setDuration(500); // 涓�娆�?�姩鐢荤殑浜嬩欢
		anim.setStartOffset(startOffset);
		anim.setFillAfter(true); // 鍔ㄧ敾鍋滄鍦ㄦ渶鍚庣姸鎬�
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isRunning = true;
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isRunning = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		viewGroup.setVisibility(View.VISIBLE);
		viewGroup.startAnimation(anim);
	}

	/**
	 * 杞洏鏀跺洖鍔ㄧ�?
	 * 
	 * @param viewGroup
	 * @param startOffset
	 */
	public static void startOutRotateAnimation(final ViewGroup viewGroup,
			long startOffset) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			viewGroup.getChildAt(i).setEnabled(false);
		}
		
		RotateAnimation anim = new RotateAnimation(0f, 180f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(500);
		anim.setStartOffset(startOffset);
		anim.setFillAfter(true);

		anim.setAnimationListener(new AnimationListener() {

			/**
			 * 褰撳姩鐢诲紑濮嬫�?
			 */
			@Override
			public void onAnimationStart(Animation animation) {
				isRunning = true;
				
			}

			/**
			 * 褰撳姩鐢荤粨鏉熸�?
			 */
			@Override
			public void onAnimationEnd(Animation animation) {
				isRunning = false;
				viewGroup.setVisibility(View.GONE);
			}

			/**
			 * 褰撳姩鐢诲紑濮嬩箣鍓�?
			 */
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		viewGroup.startAnimation(anim);
	}

	/**
	 * 鑾峰彇鍔ㄧ敾鏄惁姝ｅ湪鎵ц
	 * 
	 * @return
	 */
	public static boolean isRunningAnimation() {
		// return isInRunning || isOutRunning;
		return isRunning;
	}

}
