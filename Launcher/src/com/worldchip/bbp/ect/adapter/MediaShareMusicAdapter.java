package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.MusicInfo;
import com.worldchip.bbp.ect.entity.MusicInfo.MusicState;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MediaShareMusicAdapter extends BaseAdapter {

	static class Holder {
		private TextView mMusicValue;
		private ImageView mMusicPlay;
		private TextView mMusicTime;
		private LinearLayout mMusicBg;
	}

	private List<MusicInfo> mDataList;
	private Context mContext;
	private List<MusicInfo> shareList = new ArrayList<MusicInfo>();
	private String data = "";
	
	private int mSelection = -1;
	private Typeface mTypeface;



	/**
	 * 获取所有要分享的音乐
	 */
	public List<MusicInfo> getShareMusicData() {
		return shareList;
	}

	public MediaShareMusicAdapter(Activity context, List<MusicInfo> list) {
		this.mContext = context;
		mDataList = list;
		mTypeface = Typeface.createFromAsset(mContext.getAssets(), "DroidSans-Bold.ttf");
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mDataList != null) {
			count = mDataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(mContext, R.layout.share_musicplay_item, null);
			holder.mMusicValue = (TextView) convertView
					.findViewById(R.id.music_value);
			holder.mMusicTime=(TextView)convertView.findViewById(R.id.music_time);
			holder.mMusicPlay=(ImageView)convertView.findViewById(R.id.playermusic);
			holder.mMusicBg=(LinearLayout)convertView.findViewById(R.id.linerlayout_music_bg);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final MusicInfo music = mDataList.get(position);
		if (mSelection == position) {
			holder.mMusicBg.setBackgroundResource(R.drawable.musictiao_bg_checked);
		} else {
			holder.mMusicBg.setBackgroundResource(R.drawable.musictiao_bg);
		}
		MusicState state = music.getState();
		if (state == MusicState.STOP || state == MusicState.PAUSE) {
			holder.mMusicPlay.setImageResource(R.drawable.music_play);
			holder.mMusicBg.setBackgroundResource(R.drawable.musictiao_bg);
		} else {
			holder.mMusicPlay.setImageResource(R.drawable.music_pause);
			holder.mMusicBg.setBackgroundResource(R.drawable.musictiao_bg_checked);
		}
		
		String suffix = getExtensionName(music.getData());
		holder.mMusicValue.setTypeface(mTypeface);
		holder.mMusicTime.setTypeface(mTypeface);
		long minutes = (Integer.parseInt(music.getDuration()) % (1000 * 60 * 60)) / (1000 * 60);  
	    long seconds = (Integer.parseInt(music.getDuration()) % (1000 * 60)) / 1000;
		holder.mMusicValue.setText(music.getTitle() + "." + suffix);
		holder.mMusicTime.setText("0"+String.valueOf(minutes)+" : "+String.valueOf(seconds)); 
		holder.mMusicBg.setSelected(true);

		return convertView;
	}

	/**
	 * 获取指定文件的后缀名
	 * 
	 * @param filename
	 * @return
	 */
	private static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/**
	 * 删除图片
	 */
	public void delItem(MusicInfo musicInfo) {
		mDataList.remove(musicInfo);
		notifyDataSetChanged();
	}

	/**
	 * 删除列表项
	 */
	public void delItem(String data) {
		Iterator<MusicInfo> ite = mDataList.iterator();
		while (ite.hasNext()) {
			if (ite.next().getData().equals(data)) {
				ite.remove();
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * 选中列表项
	 */
	public void selectItem(String data) {
		Iterator<MusicInfo> ite = mDataList.iterator();
		while (ite.hasNext()) {
			MusicInfo info = ite.next();
			if (info.getData().equals(data)) {
				info.isSelected = false;
				notifyDataSetChanged();
				break;
			}
		}
	}

	/**
	 * 获取点击的音乐路径
	 */
	public String getMusicData() {
		return data;
	}

	/**
	 * 清除
	 */
	public void clearShareMusicList() {
		if (shareList != null) {
			shareList.clear();
		}
	}
	public void setDatas(List<MusicInfo> dataList) {
		mDataList = dataList;
	}
	
	public List<MusicInfo> getDatas() {
		return mDataList;
	}

	public int getSelection() {
		return mSelection;
	}

	public void setSelection(int mSelection) {
		this.mSelection = mSelection;
	}
	
	public void onChangeMusicData(int position, MusicState musicState) {
		if (mDataList != null && !mDataList.isEmpty()) {
			for (int i=0; i<mDataList.size(); i++) {
				MusicInfo musicInfo = mDataList.get(i);
				if (i == position) {
					musicInfo.setState(musicState);
				} else {
					musicInfo.setState(MusicState.STOP);
				}
			}
		}
	}
}