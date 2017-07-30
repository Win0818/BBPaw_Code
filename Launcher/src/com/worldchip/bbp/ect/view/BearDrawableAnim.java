package com.worldchip.bbp.ect.view;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.worldchip.bbp.ect.util.Utils;

public class BearDrawableAnim {

	private static BearDrawableAnim mInstance = null;
	private ImageView mImageView;
	
	private int mFrameNum = 0;
	private int mLastFrameNo = 0;
	private boolean isStop = false;
	private static final int  FRAME_DURATION =  30;
    private static final int RESTART_DELAY_TIME = 1000;//动画到最后一帧,延迟3秒后再重新动画
    private int mAnimGroupIndex = Utils.BEAR_ANIM_DEFAULT_INDEX;
    private int[] mFrameRess = Utils.BEAR_ANIM_DEFAULT_RES;
    private boolean mAutoAnimChange = true;
    private boolean mOnTouch = false;
    private static final int START_DEFAULT_ANIM = 100;
    private boolean isDefaultAnimMode = false;
    
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case START_DEFAULT_ANIM:
				
				break;

			default:
				break;
			}
		}
	};
	
    public static BearDrawableAnim getInstance(ImageView imageView) {
    	if (mInstance == null) {
    		synchronized (BearDrawableAnim.class) {
    			mInstance = new BearDrawableAnim(imageView);
			}
    	}
    	return mInstance;
    }
    
    public BearDrawableAnim(ImageView imageView) {
		// TODO Auto-generated constructor stub
    	mImageView = imageView;
    	startDefaultAnimTimer();
	}
	
    public synchronized void setBearAnimGroup(int index) {
    	if (index < 0) {
    		index = 0;
    	} else if (index > Utils.BEAR_ANIM_RES.length -1) {
    		//index = Utils.BEAR_ANIM_RES.length -1;
    		index = 0;
    	}
    	mFrameRess = Utils.BEAR_ANIM_RES[index];
    	mFrameNum = 0;
    	mLastFrameNo = mFrameRess.length - 1;
    	mAnimGroupIndex = index;
    }

	public void startAnimation(final int pFrameNo,final long deleyTime) {
		if (mImageView != null) {
			mFrameNum = pFrameNo;
			//int deleyTime=FRAME_DURATION;
			mImageView.postDelayed(mAnimationRunnable, deleyTime);
		}
	}
	
	
	private Runnable mAnimationRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (isStop) {
				return;
			}
			mImageView.setBackgroundResource(mFrameRess[mFrameNum]);
			if (mFrameNum != mLastFrameNo) {
				mFrameNum ++;
				startAnimation(mFrameNum, FRAME_DURATION);
			} else {
				if (isDefaultAnimMode) {
					setBearAnimGroup(Utils.BEAR_ANIM_DEFAULT_INDEX);// 播放待机动画
				} else {
					int getrRandomGroupIndex = getrRandomGroupIndex();
					setBearAnimGroup(getrRandomGroupIndex);
				}
				mFrameNum = 1;
				startAnimation(mFrameNum, RESTART_DELAY_TIME);
			}
		}
	};
	

	public void stopAnimation() {
		isStop = true;
		mAutoAnimChange = false;
		mOnTouch = false;
		mInstance = null;
		mFrameNum = 0;
		mLastFrameNo = 0;
	}
	
    private void startDefaultAnimTimer() {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (mAutoAnimChange) {
                    count = 0;
                    // 这段逻辑用于用户手动滑动时，停止进入待机动画,否则15秒进入待机动画
                    while (count < 1500) {
                        count++;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mOnTouch) {// 用戶手动滑动时，重新计时
                            count = 0;
                            isDefaultAnimMode = false;
                            if (mAnimGroupIndex == Utils.BEAR_ANIM_DEFAULT_INDEX) {
                            	int getrRandomGroupIndex = getrRandomGroupIndex();
    							setBearAnimGroup(getrRandomGroupIndex);
                            }
                        }
                    }
                    isDefaultAnimMode = true;
                }
            }
        }.start();
    }
	
	private int getrRandomGroupIndex() {

		return Integer.valueOf((int) (Math.random() * 4) + 1);
	}

	public synchronized void onTouch(boolean isTouch) {
		mOnTouch = isTouch;
	}
}
