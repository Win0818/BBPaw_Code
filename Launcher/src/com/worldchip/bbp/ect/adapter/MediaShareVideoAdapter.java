package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.VideoInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MediaShareVideoAdapter extends BaseAdapter {

	private DataCallback dataCallback = null;
	private Activity act;
	private List<VideoInfo> dataList;
	private List<VideoInfo> shareList = new ArrayList<VideoInfo>();

	public static interface DataCallback {
		public void onListen(List<VideoInfo> Videos);
	}

	public void setDataCallback(DataCallback listener) {
		dataCallback = listener;
	}

	public List<VideoInfo> getShareVideoData() {
		return shareList;
	}

	public MediaShareVideoAdapter(Activity act, List<VideoInfo> list) {
		this.act = act;
		dataList = list;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
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

	class Holder {
		private ImageView videoimage;
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View
					.inflate(act, R.layout.item_sharevideo_grid, null);
			holder.videoimage = (ImageView) convertView
					.findViewById(R.id.videoimage);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final VideoInfo video = dataList.get(position);
		holder.videoimage.setImageBitmap(video.getIcon());
		holder.videoimage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(
						"com.worldchip.bbpaw.videoplayer",
						"com.worldchip.activity.MainActivity"));
				intent.putExtra("single_path", dataList.get(position).getData());
				act.startActivity(intent);
			}
		});
		return convertView;
	}

	/**
	 * 添加列表项
	 * 
	 * @param item
	 */
	public void addItem(VideoInfo videoInfo) {
		dataList.add(videoInfo);
	}

	/**
	 * 删除列表项
	 */
	public void delItem(String data) {
		Iterator<VideoInfo> ite = dataList.iterator();
		while (ite.hasNext()) {
			VideoInfo videoInfo = ite.next();
			if (videoInfo.getData().equals(data)) {
				ite.remove();
				notifyDataSetChanged();
				break;
			}
		}
	}

	/**
	 * 删除视频
	 */
	public void delItem(VideoInfo videoInfo) {
		dataList.remove(videoInfo);
		notifyDataSetChanged();
	}

	/**
	 * 清除
	 */
	public void clearShareVideoList() {
		if (shareList != null) {
			shareList.clear();
		}
	}
}