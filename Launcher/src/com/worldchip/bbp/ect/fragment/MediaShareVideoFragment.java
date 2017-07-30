package com.worldchip.bbp.ect.fragment;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.MediaShareVideoAdapter;
import com.worldchip.bbp.ect.db.VideoData;
import com.worldchip.bbp.ect.entity.VideoInfo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class MediaShareVideoFragment extends Fragment {
	private GridView mGVVideo;
	private Activity mContext;
	private List<VideoInfo> shareVideoInfos = null;
	private MediaShareVideoAdapter mVideoAdapter;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x110) {
				mVideoAdapter = new MediaShareVideoAdapter(mContext,
						shareVideoInfos);
				mGVVideo.setAdapter(mVideoAdapter);
			}
		};
	};
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_mediashare_video, null);
		mContext = this.getActivity();
		initView();
		initData();
		return view;
	}

	private void initView() {
		mGVVideo = (GridView) view.findViewById(R.id.gv_video);
	}

	private void initData() {
		// 获取右边数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					VideoData.clearShareVideoList();
					shareVideoInfos = VideoData
							.getLocalShareVideoDatas(mContext);
					mHandler.sendEmptyMessage(0x110);
				} catch (Exception e) {
					Toast.makeText(mContext, "Request exception！",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();
	}
}
