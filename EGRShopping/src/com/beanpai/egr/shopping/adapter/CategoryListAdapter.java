package com.beanpai.egr.shopping.adapter;

import java.util.List;

import com.beanpai.egr.shopping.entity.Category;
import com.mgle.shopping.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {
	
	//private static final String TAG = "---CategoryListAdapter---";
	private Context mContext;
	private List<Category> mCategoryList;
	private LayoutInflater mInflater;
	private int mSelectPosition=-1;
	//private int mClickPosition=-1;
	
	public CategoryListAdapter(Context context, List<Category> category)
	{
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mCategoryList = category;
	}
	
	public int getSelectItemIndex()
	{
		return mSelectPosition;
	}

	public void setSelctItem(int position) 
	{
		mSelectPosition = position;
		notifyDataSetChanged();
	}

	/*public int getClickItemIndex()
	{
		return mClickPosition;
	}

	public void setClickItem(int position) 
	{
		mClickPosition = position;
		notifyDataSetChanged();
	}*/

	@Override
	public int getCount() 
	{
		return mCategoryList.size();
	}

	@Override
	public Category getItem(int position) 
	{
		return mCategoryList.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	public void changData(List<Category> category)
	{
		mCategoryList = category;
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.nav_category_item, null);
			viewHolder = new ViewHolder();
			viewHolder.categoryName = (TextView) convertView.findViewById(R.id.category_item_title);
			viewHolder.cLayout = (RelativeLayout)convertView.findViewById(R.id.context_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String categoryName = mCategoryList.get(position).categoryName;
		viewHolder.categoryName.setText(categoryName);
		
		if(position == mSelectPosition)
		{
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
		RelativeLayout cLayout;
	}
}