package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.VideoData;
import com.worldchip.bbp.ect.entity.VideoInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridVideoAdapter extends BaseAdapter {

	private DataCallback dataCallback = null;
	private Activity act;
	private List<VideoInfo> dataList;
	private List<VideoInfo> shareList = new ArrayList<VideoInfo>();
	private Handler mHandler;
	private String data = "";

	public static interface DataCallback {
		public void onListen(List<VideoInfo> Videos);
	}

	public void setDataCallback(DataCallback listener) {
		dataCallback = listener;
	}

	/**
	 * 获取选中分享的所有视频
	 * 
	 * @return
	 */
	public List<VideoInfo> getShareVideoData() {
		return shareList;
	}

	public GridVideoAdapter(Activity act, List<VideoInfo> list, Handler mHandler) {
		this.act = act;
		this.dataList = list;
		this.mHandler = mHandler;
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
			convertView = View.inflate(act, R.layout.item_video_grid, null);
			holder.videoimage = (ImageView) convertView
					.findViewById(R.id.videoimage);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.videoisselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final VideoInfo video = dataList.get(position);
		holder.videoimage.setImageBitmap(video.getIcon());
		if (video.isSelected) {
			holder.selected.setImageResource(R.drawable.setting_select_image);
		} else {
			holder.selected.setImageResource(-1);
		}
		Log.e("video", "video-----" + position);
		holder.videoimage.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub

				if (video.isSelected) {
					video.isSelected = false;
					holder.selected.setImageResource(-1);
					shareList.remove(video);
					boolean a = VideoData.delShareVideoData(act,
							video.getData());
					if (a) {
						data = video.getData();
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				} else {
					video.isSelected = true;
					holder.selected
							.setImageResource(R.drawable.setting_select_image);
					shareList.add(video);
				}
				if (dataCallback != null) {
					dataCallback.onListen(shareList);
				}

				return true;
			}
		});
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
	 * 选中列表项
	 */
	public void selectItem(String data) {
		Iterator<VideoInfo> ite = dataList.iterator();
		while (ite.hasNext()) {
			VideoInfo info = ite.next();
			if (info.getData().equals(data)) {
				info.isSelected = false;
				notifyDataSetChanged();
				break;
			}
		}
	}

	/**
	 * 获取点击的图片
	 */
	public String getVideoData() {
		return data;
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