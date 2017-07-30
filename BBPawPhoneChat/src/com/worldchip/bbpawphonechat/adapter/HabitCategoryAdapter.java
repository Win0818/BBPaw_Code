package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.HabitCategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HabitCategoryAdapter extends BaseAdapter {

	private List<HabitCategory> mHabitlisList;
	private Context mContext;
	private LayoutInflater mInflater;

	public HabitCategoryAdapter(List<HabitCategory> habitlisList,
			Context context) {
		this.mHabitlisList = habitlisList;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mHabitlisList == null ? 0 : mHabitlisList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mHabitlisList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		view = mInflater.inflate(R.layout.item_habit_category_gv, null);
		ImageView mCategoryIv = (ImageView) view
				.findViewById(R.id.item_habit_category_gv);
		ImageView mCategoryMakeIv = (ImageView) view
				.findViewById(R.id.item_habit_category_mark);
		ImageView mCategoryLockIv = (ImageView) view
				.findViewById(R.id.item_habit_islock);
		ImageView mCategoryNameIv = (ImageView) view
				.findViewById(R.id.item_habit_category_name);
		TextView mCategotyTimeTv = (TextView) view
				.findViewById(R.id.item_habit_category_time);
		if (mHabitlisList != null) {
			HabitCategory category = mHabitlisList.get(position);
			mCategoryIv.setBackgroundResource(category.getDrawableIndex());
			mCategoryNameIv.setImageResource(category.getTextIndex());
			if (category.getIsLockInt() == 1) {
				mCategoryMakeIv.setVisibility(View.VISIBLE);
				mCategoryMakeIv
						.setImageResource(R.drawable.habit_category_mark);
				mCategoryLockIv
						.setImageResource(R.drawable.habit_category_lock);
				mCategotyTimeTv.setText(category.getTime()
						+ MyApplication.getInstance().getApplicationContext()
								.getString(R.string.min));
			} else {
				mCategoryMakeIv.setVisibility(View.INVISIBLE);
				mCategoryLockIv
						.setImageResource(R.drawable.habit_category_unlock);
			}
		}
		return view;
	}

}
