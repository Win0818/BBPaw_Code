package com.worldchip.bbp.ect.util;


import com.worldchip.bbp.ect.util.SoundPlayer.IPlayFinishListener;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class MusicPlayerManager {
	private SoundPlayer mSoundPlayer;
	private static MusicPlayerManager mInstance;
	private String mCurrPalyPath = "";
	
	public static MusicPlayerManager getInstance() {
		if (mInstance == null) {
			synchronized (MusicPlayerManager.class) {
				mInstance = new MusicPlayerManager();
			}
		}
		return mInstance;
	}
	
	public MusicPlayerManager() {
		mSoundPlayer = new SoundPlayer();
	}
	
	
	/**
	 * @param 播放系统声音
	 */
	public void playByUri(Context context, Uri uri, boolean loop) {
		if (mSoundPlayer != null) {
			mSoundPlayer.playUri(context, uri, loop);
		}
	}
	
	/**
	 * 播放网络音频
	 */
	public void playByUrl(String url) {
		if (mSoundPlayer != null) {
			mSoundPlayer.playUrl(url);
		}
	}
	
	public void playByResId(Context context,int resId, IPlayFinishListener listener) {
		if (mSoundPlayer != null) {
			mSoundPlayer.play(context, resId, listener);
		}
	}
	
	public void play(String path) {
		if (mSoundPlayer != null) {
			if (!TextUtils.isEmpty(path) || !mCurrPalyPath.equals(path)) {
				mCurrPalyPath = path;
				mSoundPlayer.play(path);
			}
		}

	}
	public void pause(){
		if(mSoundPlayer!=null){
			mSoundPlayer.pause();
		}
	}
	
	public void start(){
		if(mSoundPlayer!=null){
			mSoundPlayer.start();
		}
	}
	
	public void stopPlay() {
		if (mSoundPlayer != null) {
			mSoundPlayer.stop();
		}
	}
	
	public String getCurrPalyPath() {
		return mCurrPalyPath;
	}

	public void setCurrPalyPath(String mCurrPalyPath) {
		this.mCurrPalyPath = mCurrPalyPath;
	}

	public SoundPlayer getSoundPlayer() {
		return mSoundPlayer;
	}

	
}
