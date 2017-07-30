package com.egreat.adlauncher.util;

import java.util.List;
import java.util.Random;

import com.mgle.launcher.R;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllAppGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	PackageManager manager = null;

	private List<ResolveInfo> mAppInfo = null;

	public AllAppGridViewAdapter(Context c, List<ResolveInfo> appinfo) {
		mContext = c;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppInfo = appinfo;
		manager = mContext.getPackageManager();
	}

	public void setDataSource(List<ResolveInfo> appinfo) {
		mAppInfo = appinfo;
	}

	@Override
	public int getCount() {
		return mAppInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GirdViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.allapp_gridview_item, null);
			holder = new GirdViewHolder();
			holder.bg_icon = (ImageView) convertView.findViewById(R.id.icon_bg);
			holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 280);
			convertView.setLayoutParams(param);
			convertView.setTag(holder);
		} else {
			holder = (GirdViewHolder) convertView.getTag();
		}
		holder.app_icon.setImageDrawable(mAppInfo.get(position).loadIcon(manager));
		holder.app_name.setText(mAppInfo.get(position).loadLabel(manager));
		return convertView;
	}

}
