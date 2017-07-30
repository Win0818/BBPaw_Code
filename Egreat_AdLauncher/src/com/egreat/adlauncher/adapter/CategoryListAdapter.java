package com.egreat.adlauncher.adapter;

import java.util.LinkedList;
import java.util.List;

import com.egreat.adlauncher.entity.Category;
import com.mgle.launcher.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {
	private static final String TAG = "---CategoryListAdapter---";
	private Context mContext;
	private List<Category> mCategoryList;
	private LayoutInflater mInflater;
	private int mSelectPosition;

	public CategoryListAdapter(Context context, List<Category> category) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mCategoryList = category;
	}

	public void setSelctItem(int position) {
		mSelectPosition = position;
		notifyDataSetChanged();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.nav_category_item, null);
			viewHolder = new ViewHolder();
			viewHolder.categoryName = (TextView) convertView.findViewById(R.id.category_item_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String categoryName = mCategoryList.get(position).categoryName;
		viewHolder.categoryName.setText(categoryName);
		viewHolder.categoryName.setTag(mCategoryList.get(position));
		
		if(position==mSelectPosition){
			viewHolder.categoryName.setBackgroundResource(R.drawable.nav_hover);
		}else{
			viewHolder.categoryName.setBackgroundResource(Color.TRANSPARENT);
		}
		return convertView;
	}

	public class ViewHolder {
		TextView categoryName;
	}
}
