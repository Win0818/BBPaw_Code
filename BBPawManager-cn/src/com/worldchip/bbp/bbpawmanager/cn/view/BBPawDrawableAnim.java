package com.worldchip.bbp.bbpawmanager.cn.view;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * @author lidaochang
 * 20150909
 */
public class BBPawDrawableAnim {

    private static final long DEFAULT_FRAME_DURATION =  50;
    private static final int UPDATE_ANIM_VIEW = 100;
    private int[] mFrameRess = null;
    private ImageView mAnimView = null;
    private Timer mAnimTimer;
	private AnimTimerTask mAnimTimerTask;
    private long mFrameDuration = DEFAULT_FRAME_DURATION;
    private int mFrameNum = 0;
	
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//msg.what
			switch (msg.what) {
			case UPDATE_ANIM_VIEW:
				updateAnimView();
				break;

			default:
				break;
			}
		}
	};
	
    public BBPawDrawableAnim(ImageView imageView) {
		// TODO Auto-generated constructor stub
    	mAnimView = imageView;
	}
	
	public void setDrawableAnimResource(int[] res) {
		mFrameRess = res;
	}

	public void setDrawableAnimFrameDuration(long duration) {
		mFrameDuration = duration;
	}
	
	private int getFramesCount() {
		if (mFrameRess != null) {
			return mFrameRess.length;
		}
		return 0;
	}
	
	
	public void startAnimation() {
		
		if (mFrameRess == null || mFrameRess.length <= 0 || mAnimView == null) {
			return;
		}
		if (mAnimTimer != null) {
			if (mAnimTimerTask != null) {
				mAnimTimerTask.cancel(); // 将原任务从队列中移除
			}
		} else {
			mAnimTimer = new Timer(true);
		}
		mAnimTimerTask = new AnimTimerTask(); // 新建一个任务
		mAnimTimer.schedule(mAnimTimerTask, 0, mFrameDuration);
	}
	
	public void stopAnimation() {
		if (mAnimTimer != null) {
			if (mAnimTimerTask != null) {
				mAnimTimerTask.cancel(); // 将原任务从队列中移除
			}
		}
		mFrameNum = 0;
	}
	
	public class AnimTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(UPDATE_ANIM_VIEW);
			//if (mAnimView != null) {
				//mAnimView.post(mAnimationRunnable);
			//}
		}
	}
	
	private void updateAnimView() {
		if (mAnimView != null) {
			mAnimView.setImageResource(mFrameRess[mFrameNum]);
		}
		if (mFrameNum != getFramesCount() - 1) {
			mFrameNum ++;
		} else {
			mFrameNum = 0;
		}
	}
	
	/*private Runnable mAnimationRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateAnimView();
		}
	};*/
}
