package com.worldchip.bbp.ect.util;

import android.content.Context;
import android.net.Uri;

public class AlarmPlayer {
	private SoundPlayer mSoundPlayer;
	private static AlarmPlayer mInstance;
	
	public static AlarmPlayer getInstance() {
		if (mInstance == null) {
			synchronized (AlarmPlayer.class) {
				mInstance = new AlarmPlayer();
			}
		}
		return mInstance;
	}
	
	public AlarmPlayer() {
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
