package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MainActivity;

import android.widget.ImageView;

public class BbpawRandomAnim{

	private ImageView mImageView;
	private int[] mFrameRess;
	private int mFrameDuration;
	private int mLastFrameNo;
	private boolean isStop = false;
    private static final int RESTART_DELAY_TIME = 2000;//动画到最后一帧,延迟3秒后再重新动画
	
	public BbpawRandomAnim (ImageView imageView, int[] frameRess,
			int frameDuration) {
		mImageView = imageView;
		mFrameRess = frameRess;
		mFrameDuration = frameDuration;
		mLastFrameNo = frameRess.length - 1;
		mImageView.setBackgroundResource(mFrameRess[0]);
	}
    
	public void startAnimation(final int pFrameNo) {
		if (mImageView != null) {
		//	final int deleyTime = (pFrameNo == mLastFrameNo ? RESTART_DELAY_TIME : mFrameDuration);
			int deleyTime=mFrameDuration;
			mImageView.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isStop) {
						return;
					}
					mImageView.setBackgroundResource(mFrameRess[pFrameNo]);
					if (pFrameNo != mLastFrameNo) {
						startAnimation(pFrameNo + 1);
					} else {
					      
					}
				}
			}, deleyTime);
		}
	}

	public void stopAnimation() {
		isStop = true;
	}
	
}
