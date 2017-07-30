package com.worldchip.bbp.ect.adapter;

import java.util.List;
import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.MusicInfo;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClockRingAdapter extends BaseAdapter{

	static class Holder 
	{
		 private TextView mClockSongValue;
	}

	private List<MusicInfo> dataList;
	private Activity act;
	private int selectedPosition = -1;// 选中的位置
	private String mSelectionRingtonePath = "";
	
	public ClockRingAdapter(Activity act, List<MusicInfo> list) 
	{
		this.act = act;
		this.dataList = list;
	}
	
	@Override
	public int getCount() 
	{
		int count = 0;
		if (dataList != null) 
		{
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) 
	{
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	public void setSelectedPosition(int position) 
	{  
		selectedPosition = position;  
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		
		final Holder holder;
		if (convertView == null) 
		{
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_clock_song, null);
			holder.mClockSongValue = (TextView) convertView.findViewById(R.id.clock_song_value);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		//判断是否选中
		MusicInfo info = (MusicInfo)getItem(position);
		if (info != null) {
			if (mSelectionRingtonePath != null && !TextUtils.isEmpty(mSelectionRingtonePath)) {
				if (info.getData().equals(mSelectionRingtonePath)) { 
					holder.mClockSongValue.setBackgroundResource(R.drawable.clock_song_click_on);
				} else {
					holder.mClockSongValue.setBackgroundResource(R.drawable.clock_song_click);
				}
			} else {
				if (position == 0) {
					MusicInfo musicInfo = dataList.get(0);
					if (musicInfo != null) {
						mSelectionRingtonePath = musicInfo.getData();
					}
					holder.mClockSongValue.setBackgroundResource(R.drawable.clock_song_click_on);
				} else {
					holder.mClockSongValue.setBackgroundResource(R.drawable.clock_song_click);
				}
			}
			
			if(info.getTitle().length() > 12) {
				holder.mClockSongValue.setText(info.getTitle().substring(0, 12) + "...");
			} else {
				holder.mClockSongValue.setText(info.getTitle());
			}
		}
		
		return convertView;
	} 
	
	public void setDatas(List<MusicInfo> list) {
		dataList = list;
	}

	public void setSelectionRingtonePath(String ringtonePath) {
		this.mSelectionRingtonePath = ringtonePath;
	}

	public String getSelectionRingtonePath() {
		return mSelectionRingtonePath;
	}
	
	
}