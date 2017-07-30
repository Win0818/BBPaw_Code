package com.worldchip.bbp.ect.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.IconInfo;
import com.worldchip.bbp.ect.util.Utils;

@SuppressWarnings("deprecation")
public class ImageAdapter extends BaseAdapter {

	@SuppressLint("UseSparseArrays")
	private List<IconInfo> mImageViewList;
	private Context context;
	private int selectedPosition = -1;// 选中的位�??
	private Resources mResources;
	
	public ImageAdapter(List<IconInfo> imageViewList, Context context) {
		this.mImageViewList = imageViewList;
		this.context = context;
		mResources = context.getResources();

	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return mImageViewList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setDatas(List<IconInfo> imageViewList) {
		this.mImageViewList = imageViewList;
	}
	
	public List<IconInfo> getDatas() {
		return mImageViewList;
	}
	
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public final class ViewHolder {
		public ImageView image;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = new ImageView(context);
			holder.image = (ImageView)convertView;
			holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
			holder.image.setPadding(3, 3, 3, 3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int index = position % mImageViewList.size();
		IconInfo iconInfo = mImageViewList.get(index);
		String imageDrawbleName = Utils.GALLERY_IMAGE_NAME_LIST[iconInfo.getIndex()];
		if (selectedPosition == position) {
			//GALLERY_IMAGE_NAME_LIST
			holder.image.setBackgroundResource(Utils.getGalleryImageDrawble(context, imageDrawbleName));
			LayoutParams layoutParams = new Gallery.LayoutParams(
					(int) mResources.getDimension(R.dimen.gallery_item_selected_width), 
					(int) mResources.getDimension(R.dimen.gallery_item_selected_heigth));
			holder.image.setLayoutParams(layoutParams);
		} else {
			holder.image.setBackgroundResource(Utils.getGalleryImageDrawble(context, imageDrawbleName));
			LayoutParams layoutParams = new Gallery.LayoutParams(
					(int) mResources.getDimension(R.dimen.gallery_item_normal_width), 
					(int) mResources.getDimension(R.dimen.gallery_item_normal_heigth));
			holder.image.setLayoutParams(layoutParams);
		}
		return convertView;
	}
}