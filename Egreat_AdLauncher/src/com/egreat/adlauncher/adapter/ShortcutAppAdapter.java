package com.egreat.adlauncher.adapter;


import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.image.utils.ImageLoader;
import com.mgle.launcher.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortcutAppAdapter extends BaseAdapter {
	private static final String TAG = "ShortcutAppAdapter---";
	private Context mContext;
	
	private List<ApkInfo> mHotAppList;
	private int mSelectPosition;
	//private FinalBitmap mFB;
	private ImageLoader mLoader = null;
	
	public ShortcutAppAdapter(Context context, List<ApkInfo> hotApps) {
		mContext = context;
		mHotAppList  = hotApps;
		Log.e(TAG, "getCount...mHotAppList.size()="+mHotAppList.size());
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
	}

	@Override
	public int getCount() {
		
		return mHotAppList.size();
	}


	public void setSelctItem(int position) {
		mSelectPosition = position;
		notifyDataSetChanged();
	}
	
	public int getSelectedIndex(){
		return mSelectPosition;
	}

	
	@Override
	public ApkInfo  getItem(int position) {
		return mHotAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.shortcut_item_layout, null);
			viewholder.icon = (ImageView) convertView.findViewById(R.id.ItemImage);
			//viewholder.name = (TextView) convertView.findViewById(R.id.ItemName);
			viewholder.layout = (FrameLayout) convertView.findViewById(R.id.ItemLayout);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		final ApkInfo appInfo = getItem(position);
		Log.e(TAG, "appInfo.islocal="+appInfo.isLocal+"; appInfo.getPoster()="+appInfo.getPoster());
		if(appInfo.isLocal){
			viewholder.icon.setImageResource(appInfo.resId);
		}else{
			mLoader.loadImage(appInfo.getPoster(), viewholder.icon, true, false);
		}
		
		if(position==mSelectPosition){
			viewholder.icon.setBackgroundResource(R.drawable.shortcut_selected);
		}else{
			viewholder.icon.setBackgroundResource(Color.TRANSPARENT);
		}
		//viewholder.name.setText(appInfo.getName());
		return convertView;
	}

	public void changData(List<ApkInfo> hotApps) {
		updateHotApps(hotApps);
		notifyDataSetChanged();
	}

	private void updateHotApps(List<ApkInfo> hotApps) {
		mHotAppList  = hotApps;
	}

	class ViewHolder {
		private ImageView icon;
		private TextView name;
		private FrameLayout layout;
	}
}
