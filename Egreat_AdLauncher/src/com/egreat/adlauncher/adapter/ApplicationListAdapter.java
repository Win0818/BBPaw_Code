package com.egreat.adlauncher.adapter;
import java.util.List;

import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.image.utils.ImageLoader;
import com.mgle.launcher.R;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApplicationListAdapter extends BaseAdapter {
	private static final String TAG = "ApplicationListAdapter---";
	private Context mCtx;
	private List<ApkInfo> mApkList;
	private ImageLoader mLoader = null;
	private int mSelectPosition = -1;

	public ApplicationListAdapter(Context context, List<ApkInfo> apkList) {
		mApkList = apkList;
		
		Log.e(TAG, "ApplicationListAdapter...apks.size="+(mApkList==null ? "0" : mApkList.size()));
		for(int i=0; i<mApkList.size(); i++){
			Log.e(TAG, "ApplicationListAdapter...name="+mApkList.get(i).getName()+"; poster="+mApkList.get(i).getPoster());
		}
		mCtx = context;
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
	}

	@Override
	public int getCount() {
		Log.i(TAG,"apks.size()= "+mApkList.size());
		return mApkList.size();
	}

	@Override
	public ApkInfo getItem(int position) {
		return mApkList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(mCtx).inflate(
					R.layout.application_item, null);
			viewholder.poster = (ImageView) convertView
					.findViewById(R.id.application_poster);
			viewholder.appLayout = (LinearLayout)convertView
					.findViewById(R.id.app_layout);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		Log.e(TAG, "mSelectPosition..="+mSelectPosition+"; position="+position);
		final ApkInfo apkInfo = mApkList.get(position);

		if(position==mSelectPosition){			
			//viewholder.poster.setLayoutParams(new LinearLayout.LayoutParams(400, 650));
			//viewholder.poster.setScaleX(1.1f);
			//viewholder.poster.setScaleY(1.4f);
			
			//viewholder.appLayout.setBackgroundResource(R.drawable.application_selected);
			//viewholder.poster.setScaleX(1.02f);
			//viewholder.poster.setScaleY(1.2f);
			//viewholder.poster.requestFocus();
		}else{
			//viewholder.poster.setLayoutParams(new LinearLayout.LayoutParams(260, 350));
			//viewholder.poster.setScaleX(1.0f);
			//viewholder.poster.setScaleY(1.0f);
			//viewholder.appLayout.setBackgroundResource(Color.TRANSPARENT);
			//viewholder.poster.setScaleX(1.0f);
			//viewholder.poster.setScaleY(1.0f);
		}
		
		//viewholder.name.setText(apkInfo.getName());
		mLoader.loadImage(apkInfo.getPoster(), viewholder.poster, true, false);
		viewholder.poster.setScaleType(ScaleType.FIT_XY);
		return convertView;
	}

	public void changeData(List<ApkInfo> apkList) {
		this.mApkList = apkList;
		notifyDataSetChanged();
	}

	public void setSelctItem(int position) {
		mSelectPosition  = position;
		notifyDataSetChanged();
	}
	
	public int getSelectedIndex(){
		return mSelectPosition;
	}
	
	class ViewHolder {
		private ImageView poster;
		private LinearLayout appLayout;
	}
}
