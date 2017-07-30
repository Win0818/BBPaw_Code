package com.egreat.adlauncher.adapter;

import java.util.List;

import com.egreat.adlauncher.entity.Category;
import com.mgle.launcher.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HorizontalListViewAdapter extends BaseAdapter{
	private static final String TAG = "HorizontalListViewAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private int mSelectIndex = -1;
	private int mPressedIndex = -1;
	
	private List<Category> mCategoryList;
	
	public HorizontalListViewAdapter(Context context, List<Category> categoryList){
		this.mContext = context;
		mCategoryList = categoryList;
		mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.nav_category_item, null);
			holder.mLayout=(LinearLayout)convertView.findViewById(R.id.layout);
			holder.mTitle=(TextView)convertView.findViewById(R.id.category_item_title);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		if(position == mSelectIndex){
			//convertView.setSelected(true);
			//holder.mTitle.setBackgroundResource(R.drawable.app_category2_selected);
			holder.mTitle.setBackgroundResource(R.drawable.nav_category_selected);
			holder.mTitle.requestFocus();
		}else{
			//convertView.setSelected(false);
			holder.mTitle.setBackgroundResource(Color.TRANSPARENT);
		}
		
		if(position == mPressedIndex){
			holder.mLayout.setBackgroundResource(R.drawable.nav_category_pressed);
		}else{
			holder.mLayout.setBackgroundResource(Color.TRANSPARENT);
		}
		
		holder.mTitle.setText(mCategoryList.get(position).categoryName);

		return convertView;
	}

	private static class ViewHolder {
		private TextView mTitle ;
		private LinearLayout mLayout;
	}

	public void changeData(List<Category> categoryList){
		mCategoryList = categoryList;
		notifyDataSetChanged();
	}
	public void setSelectIndex(int i){
		mSelectIndex = i;
		Log.e(TAG, "setSelectIndex...mSelectIndex="+mSelectIndex);
		notifyDataSetChanged();
	}
	
	public void setPressedIndex(int i){
		mPressedIndex = i;
		Log.e(TAG, "setEnableIndex...mPressedIndex="+mPressedIndex);
		notifyDataSetChanged();
	}
	
	public int getSelectedIndex(){
		return mSelectIndex;
	}
}