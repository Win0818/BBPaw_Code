package com.worldchip.bbp.ect.view;

import android.util.Log;
import android.widget.ImageView;

public class PlanetDrawableAnim {

	private ImageView mImageView;
	private int[] mFrameRess;
	private int mFrameDuration;
	private int mLastFrameNo;
	private boolean isStop = false;

	public PlanetDrawableAnim(ImageView imageView, int[] frameRess,
			int frameDuration) {
		mImageView = imageView;
		mFrameRess = frameRess;
		mFrameDuration = frameDuration;
		mLastFrameNo = frameRess.length - 1;
		mImageView.setBackgroundResource(mFrameRess[0]);
	}

	public void startAnimation(final int pFrameNo) {
		if (mImageView != null) {
			mImageView.postDelayed(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					if (isStop) {
						return;
					}
					mImageView.setBackgroundResource(mFrameRess[pFrameNo]);
					if (pFrameNo == mLastFrameNo) {
						startAnimation(0);
					} else {
						startAnimation(pFrameNo + 1);
					}
				}
			}, mFrameDuration);
		}
	}

	public void stopAnimation() {
		isStop = true;
	}
	
}
