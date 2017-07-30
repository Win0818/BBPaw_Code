package com.worldchip.bbp.ect.util;

import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class SoundPlayer {
	
	private AudioManager audioManager;
	public enum PlayerState
    {
		STOP, PAUSE, PLAYING
    }
	
	private MediaPlayer mMediaPlayer;

	private PlayerState mPlayerState = PlayerState.STOP;
	
	public interface IPlayFinishListener {
		public void playFinish();
	}

	/**
	 * @param path
	 */
	public void playUrl(String url) {
		if(TextUtils.isEmpty(url)){
			return;
		}
		try {
			stop();
			Log.i("SoundPlayer", "-------------playUrl-------");
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mPlayerState = PlayerState.PLAYING;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param path
	 */
	public void playUri(Context context, Uri uri, boolean loop) {
		if(uri == null){
			return;
		}
		try {
			stop();
			if(mMediaPlayer==null){
			   mMediaPlayer = new MediaPlayer();
			}
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(context, uri);
			mMediaPlayer.setLooping(loop);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mPlayerState = PlayerState.PLAYING;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param path
	 */
	public void play(String path) {
		if( path == null ){
			return;
		}
		
		try {
			stop();
			
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			FileInputStream fis = new FileInputStream(path);
			mMediaPlayer.setDataSource(fis.getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mPlayerState = PlayerState.PLAYING;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play(Context context, int resid) {
		stop();
		mMediaPlayer = MediaPlayer.create(context, resid);
		mMediaPlayer.start();
	}
	
	/**
	 * 播放背景音乐
	 * @param context
	 * @param resid
	 * @param loop
	 */
	public void playBGSound(Context context, int resid, boolean loop) {
		stop();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer = MediaPlayer.create(context, resid);
		mMediaPlayer.setLooping(loop);
		mMediaPlayer.setVolume(0.2f, 0.2f);
		mMediaPlayer.start();
		mPlayerState = PlayerState.PLAYING;
	}

	/**
	 * @param context
	 * @param resid
	 */
	public void play(Context context, int resid,
			final IPlayFinishListener listener) {
		stop();
		mMediaPlayer = MediaPlayer.create(context, resid);
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mPlayerState = PlayerState.STOP;
				if (listener != null) {
					listener.playFinish();
				}
			}
		});
		mMediaPlayer.start();
		mPlayerState = PlayerState.PLAYING;
	}


	public void pause() {
		if (mMediaPlayer != null && mPlayerState == PlayerState.PLAYING) {
			mMediaPlayer.pause();
			mPlayerState = PlayerState.PAUSE;
		}
	}

	public void start() {
		if (mMediaPlayer != null && mPlayerState == PlayerState.PAUSE) {
			mMediaPlayer.start();
			mPlayerState = PlayerState.PLAYING;
		}
	}
	
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mPlayerState = PlayerState.STOP;
		}
	}
	
	public boolean isPlaying() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}
	
}
