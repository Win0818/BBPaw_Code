package com.worldchip.bbpaw.media.camera.utils;

import java.util.HashMap;
import java.util.Map;

import com.worldchip.bbpaw.media.camera.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SimpleAudioPlayer {
	
	private static SimpleAudioPlayer mInstance;
	private SoundPool mSoundPool = null;
	private Map<Integer, Integer> mSoundMap;  
	public static final int CLICK_SOUND_KEY = 1;
	
	public static SimpleAudioPlayer getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SimpleAudioPlayer(context);
		}
		return mInstance;
	}
	
	public static void initialize(Context context) {
		if (mInstance == null) {
			getInstance(context);
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public SimpleAudioPlayer(Context context) {
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		mSoundMap = new HashMap<Integer, Integer>();
		mSoundMap.put(CLICK_SOUND_KEY, mSoundPool.load(context, R.raw.click, 1));
	}
	
	public void playEffect(int soundKey) {
		mSoundPool.play(mSoundMap.get(soundKey), 1, 1, 1, 1, 1);
	}
	
}
