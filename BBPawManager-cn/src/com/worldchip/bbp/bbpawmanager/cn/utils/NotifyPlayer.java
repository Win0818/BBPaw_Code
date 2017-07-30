package com.worldchip.bbp.bbpawmanager.cn.utils;

import android.content.Context;
import android.net.Uri;

public class NotifyPlayer {
	private SoundPlayer mSoundPlayer;
	private static NotifyPlayer mInstance;
	
	public static NotifyPlayer getInstance() {
		if (mInstance == null) {
			synchronized (NotifyPlayer.class) {
				mInstance = new NotifyPlayer();
			}
		}
		return mInstance;
	}
	
	public NotifyPlayer() {
		mSoundPlayer = new SoundPlayer();
	}
	
	
	/**
	 * @param 播放系统声音
	 */
	public void playByUri(Context context, Uri uri,boolean loop) {
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
	
	public void stopPlay() {
		if (mSoundPlayer != null) {
			mSoundPlayer.stop();
		}
	}
	
}
