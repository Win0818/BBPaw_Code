package com.worldchip.bbp.ect.activity;

import java.util.HashMap;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.fragment.MediaShareMusicFragment;
import com.worldchip.bbp.ect.fragment.MediaSharePicFragment;
import com.worldchip.bbp.ect.fragment.MediaShareVideoFragment;
import com.worldchip.bbp.ect.util.MusicPlayerManager;
import com.worldchip.bbp.ect.util.SoundPlayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MediaShareActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	private RadioGroup mRGMediaShare;
	private MediaShareMusicFragment mMusicFragment;
	private MediaSharePicFragment mPicFragment;
	private MediaShareVideoFragment mVideoFragment;
	private ImageButton mImgBtnBack;
	private SoundPool soundPool = null;
	private HashMap<Integer, Integer> mSoundMap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mediashare);
		initView();
		setDefaultFragment();
	}

	private void initView() {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap=new HashMap<Integer, Integer>();
		mSoundMap.put(1, soundPool.load(MediaShareActivity.this, R.raw.click, 1));
		mRGMediaShare = (RadioGroup) findViewById(R.id.rg_mediashare);
		mRGMediaShare.check(R.id.rbtn_pic);
		mImgBtnBack = (ImageButton) findViewById(R.id.imgbtn_back);
		initListener();
	}

	private void initListener() {
		mRGMediaShare.setOnCheckedChangeListener(this);
		mImgBtnBack.setOnClickListener(this);
	}

	private void setDefaultFragment() {
		//��Ƭ����
		FragmentManager fm = this.getFragmentManager();
		//��Ƭת��
		FragmentTransaction transaction = fm.beginTransaction();
		mPicFragment = new MediaSharePicFragment();
		transaction.replace(R.id.fl_content, mPicFragment);//�滻
		transaction.commit();//�滻���
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkID) {
		// TODO Auto-generated method stub
		FragmentManager fm = this.getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		soundPool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
		switch (checkID) {
		case R.id.rbtn_pic:
			
			if (mPicFragment == null) {
				mPicFragment = new MediaSharePicFragment();
			}
			transaction.replace(R.id.fl_content, mPicFragment);
			MusicPlayerManager playerManager = MusicPlayerManager.getInstance();
			SoundPlayer soundPlayer = playerManager.getSoundPlayer();
			if(soundPlayer!=null){
				if(soundPlayer.isPlaying()){
					soundPlayer.pause();
				}
			}
			break;
		case R.id.rbtn_video:
			if (mVideoFragment == null) {
				mVideoFragment = new MediaShareVideoFragment();
			}
			transaction.replace(R.id.fl_content, mVideoFragment);
			MusicPlayerManager playerManager1 = MusicPlayerManager.getInstance();
			SoundPlayer soundPlayer1 = playerManager1.getSoundPlayer();
			if(soundPlayer1!=null){
				if(soundPlayer1.isPlaying()){
					soundPlayer1.pause();
				}
			}
			break;
		case R.id.rbtn_music:
			if (mMusicFragment == null) {
				mMusicFragment = new MediaShareMusicFragment();
			}
			transaction.replace(R.id.fl_content, mMusicFragment);
			break;

		default:
			break;
		}
		transaction.commit();
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
         if(keyCode==KeyEvent.KEYCODE_BACK){
        	 MusicPlayerManager playerManager2 = MusicPlayerManager.getInstance();
 			SoundPlayer soundPlayer2 = playerManager2.getSoundPlayer();
 			if(soundPlayer2!=null){
 				if(soundPlayer2.isPlaying()){
 					soundPlayer2.pause();
 				}
 			}
    	}
    	return super.onKeyDown(keyCode, event);
    }
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.imgbtn_back) {
			soundPool.play(mSoundMap.get(1), 1, 1, 0, 0, 1);
			MusicPlayerManager playerManager = MusicPlayerManager.getInstance();
			SoundPlayer soundPlayer = playerManager.getSoundPlayer();
			if(soundPlayer!=null){
				if(soundPlayer.isPlaying()){
					soundPlayer.pause();
				}
			}
			finish();
		}
	}
}
