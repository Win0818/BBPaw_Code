package com.worldchip.bbp.ect.fragment;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.adapter.MediaSharePicAdapter;
import com.worldchip.bbp.ect.db.PictureData;
import com.worldchip.bbp.ect.entity.PictureInfo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class MediaSharePicFragment extends Fragment {
	private GridView mGVPic;
	private View view;
	private List<PictureInfo> mPicInfoList = null;
	private Activity mContext;
	private MediaSharePicAdapter adapter;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				adapter = new MediaSharePicAdapter(mContext, mPicInfoList);
				mGVPic.setAdapter(adapter);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_mediashare_pic, null);
		mContext = this.getActivity();
		initView();
		initData();
		return view;
	}

	private void initView() {
		mGVPic = (GridView) view.findViewById(R.id.gv_pic);
	}

	private void initData() {
		// 获取右边数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PictureData.clearSharePictureList();
					mPicInfoList = PictureData
							.getLocalSharePictureDatas(mContext);
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					Toast.makeText(mContext, "Request exception！", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}).start();
	}
}
