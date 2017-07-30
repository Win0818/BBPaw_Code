package com.egreat.adlauncher.adapter;

import java.util.List;
import com.egreat.adlauncher.entity.Category;
import com.mgle.launcher.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppCategoryListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Category> mCategoryList;
	private LayoutInflater mInflater;
	private int mSelectPosition = -1;

	public AppCategoryListAdapter(Context context, List<Category> category) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mCategoryList = category;
	}

	public void setSelctItem(int position) {
		mSelectPosition = position;
		notifyDataSetChanged();
	}
	
	public int getSelectItemIndex()
	{
		return mSelectPosition;
	}
	
	@Override
	public int getCount() {
		return mCategoryList.size();
	}

	@Override
	public Category getItem(int position) {
		return mCategoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void changData(List<Category> category) {
		mCategoryList = category;
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.app_nav_category_item, null);
			viewHolder.categoryName = (TextView) convertView.findViewById(R.id.category_item_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String categoryName = mCategoryList.get(position).categoryName;
		viewHolder.categoryName.setText(categoryName);
		viewHolder.categoryName.setTag(mCategoryList.get(position));
		
		if(position == mSelectPosition){
			viewHolder.categoryName.setBackgroundResource(R.drawable.nav_category_pressed);
			viewHolder.categoryName.setTextSize(32);
			viewHolder.categoryName.setTextColor(Color.WHITE);
		}else{
			viewHolder.categoryName.setBackgroundResource(Color.TRANSPARENT);
			viewHolder.categoryName.setTextSize(28);
			viewHolder.categoryName.setTextColor(Color.GRAY);
		}
		return convertView;
	}

	public class ViewHolder {
		TextView categoryName;
		RelativeLayout layout;
	}

}
