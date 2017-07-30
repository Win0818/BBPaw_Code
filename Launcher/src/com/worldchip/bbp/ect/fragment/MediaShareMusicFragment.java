package com.worldchip.bbp.ect.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.adapter.MediaShareMusicAdapter;
import com.worldchip.bbp.ect.db.MusicData;
import com.worldchip.bbp.ect.entity.MusicInfo;
import com.worldchip.bbp.ect.entity.MusicInfo.MusicState;
import com.worldchip.bbp.ect.util.MusicPlayerManager;
import com.worldchip.bbp.ect.util.SoundPlayer;

public class MediaShareMusicFragment extends Fragment {
	private View view;
	private Activity act;
	private ListView mListViewMusic;
	private List<MusicInfo> mMusicList = null;
	
	private MusicPlayerManager mPlayerManager = null; //MusicPlayerManager.getInstance();
	private SoundPlayer soundPlayer;
	private MediaShareMusicAdapter mMusicAdapter;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x120) {
				if (mMusicAdapter != null) {
					mMusicAdapter.setDatas(mMusicList);
					mMusicAdapter.notifyDataSetChanged();
				}
				
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_mediashare_music, null);
		act = this.getActivity();
		mListViewMusic = (ListView) view.findViewById(R.id.lv_music);
		mMusicAdapter = new MediaShareMusicAdapter(getActivity(), null);
		mListViewMusic.setAdapter(mMusicAdapter);
		mListViewMusic.setOnItemClickListener(new MusicOnItemClickListener());
		mAudioManager = (AudioManager) act.getSystemService(Context.AUDIO_SERVICE);
		mPlayerManager = MusicPlayerManager.getInstance();
		
		initData();
		return view;
	}

	private void initData() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MusicData.clearShareMusicList();
					mMusicList = MusicData.getLocalShareMusicDatas(act);
					mHandler.sendEmptyMessage(0x120);
				} catch (Exception e) {
					Toast.makeText(act, "Request exception "+ e.toString(), Toast.LENGTH_LONG)
							.show();
					e.printStackTrace();
				}
			}
		}).start();
	}

	AudioManager  mAudioManager ; 
	
	private class MusicOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (mMusicAdapter != null) {
				List<MusicInfo> datas = mMusicAdapter.getDatas();
				if (datas != null && !datas.isEmpty()) {
					MusicInfo musicInfo = datas.get(position);
					if (musicInfo != null) {
						int result = mAudioManager.requestAudioFocus(afChangeListener,
							    // Use the music stream.
								  AudioManager.STREAM_MUSIC,
							      AudioManager.AUDIOFOCUS_GAIN);
						Log.d("BBPawAlarmDialog", "-----------playAlarmRingtone------------->result: " + result +
							"-------AUDIOFOCUS_REQUEST_GRANTED"	+AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
					    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					    	Log.d("BBPawAlarmDialog", "-----------playAlarmRingtone------------->");
					    	playMusic(musicInfo, position);
					     // Start playback. // 开始播放音乐文件
					    }
					}
				}
			 }
		}
	}
	
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			 soundPlayer = mPlayerManager.getSoundPlayer();
			 if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				 Log.d("MediaShareMusic...", "-----------afChangeListener----------AUDIOFOCUS_LOSS--->");
				 if(mPlayerManager!=null){
					 if (soundPlayer.isPlaying()) {
						 mPlayerManager.pause();
					 }
				 }
			 }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){
				 if(mPlayerManager!=null){
					 if (!soundPlayer.isPlaying()) {
						 mPlayerManager.start();
						 Log.d("MediaShareMusic...", "-----------afChangeListener--------AUDIOFOCUS_GAIN----->");
					 }
				 }
			 }
		}
	};
	private void playMusic(MusicInfo musicInfo, int position) {
		if(mPlayerManager==null){
			mPlayerManager = MusicPlayerManager.getInstance();
		}
		String currPalyPath = mPlayerManager.getCurrPalyPath();
		String musicPath = musicInfo.getData();
		if (mPlayerManager != null) {
			if (TextUtils.isEmpty(musicPath)) {
				return;
			}
			if (TextUtils.isEmpty(currPalyPath) || !currPalyPath.equals(musicPath)) {
				mMusicAdapter.onChangeMusicData(position, MusicState.PLAYING);
				mPlayerManager.play(musicPath);
			} else {
				soundPlayer = mPlayerManager.getSoundPlayer();
				if (soundPlayer != null) {
					if (soundPlayer.isPlaying()) {
						mMusicAdapter.onChangeMusicData(position, MusicState.PAUSE);
						mPlayerManager.pause();
					} else {
						mMusicAdapter.onChangeMusicData(position, MusicState.PLAYING);
						mPlayerManager.start();
					}
				}
			}
		}
		mMusicAdapter.setSelection(position);
		mMusicAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mAudioManager.abandonAudioFocus(afChangeListener);
		Log.d("Wing", "--abandonAudioFocus--afChangeListener");
		super.onDestroy();
	}
}
