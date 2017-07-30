package com.worldchip.bbp.bbpawmanager.cn.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageLoader;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageLoader.Type;
import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;

public class GrowupRecordAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
	private Typeface mTypeFace;
	private List<GrowthMessage> mDataList;
	private ImageLoader mImageLoader;
	
    public GrowupRecordAdapter(Context context, List<GrowthMessage> list) {
        //super(context, resource, textViewResourceId);
    	mInflater = LayoutInflater.from(context);
    	mTypeFace = Typeface.createFromAsset(context.getAssets(),"font/changcheng.TTF");
		mDataList = list;
		mImageLoader = ImageLoader.getInstance(3, Type.LIFO);
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.growup_record_item, null);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon);
			holder.sectionView = (LinearLayout) convertView
					.findViewById(R.id.item_time_title);
			holder.section_tv = (TextView) convertView
					.findViewById(R.id.section_tv);
			holder.study_time_tv = (TextView) convertView.findViewById(R.id.study_time_tv);
			holder.study_time_tv.setTypeface(mTypeFace);
			holder.study_content_tv = (TextView) convertView
					.findViewById(R.id.study_content_tv);
			holder.study_content_tv.setTypeface(mTypeFace);
			convertView.setTag(holder);
			TextView studyTimeTitle = (TextView)convertView.findViewById(R.id.study_time_title);
			TextView studyContentTitle = (TextView)convertView.findViewById(R.id.study_content_title);
			studyTimeTitle.setTypeface(mTypeFace);
			studyContentTitle.setTypeface(mTypeFace);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
        View growupView = convertView.findViewById(R.id.growup_item_layout);
        growupView.setTag(position);
        GrowthMessage messge = getItem(position);
        mImageLoader.loadImage(messge.getIconPath(), holder.icon, true, true);
        //第一条数据，肯定显示时间标题
        if (position == 0) {
        	holder.sectionView.setVisibility(View.VISIBLE);
        	holder.section_tv.setText(messge.getDate());
        	holder.study_time_tv.setText(messge.getStudyTime());
        	holder.study_content_tv.setText(messge.getStudyConetnt());
        } else {
        	// 本条数据和上一条数据的时间戳相同，时间标题不显示
        	if (messge.getDate().equals(mDataList.get(position - 1).getDate())) {
        		holder.sectionView.setVisibility(View.GONE);
        		holder.section_tv.setText("");
        		holder.study_time_tv.setText(messge.getStudyTime());
        		holder.study_content_tv.setText(messge.getStudyConetnt());
        	} else {
        		holder.sectionView.setVisibility(View.VISIBLE);
        		holder.section_tv.setText(messge.getDate());
        		holder.study_time_tv.setText(messge.getStudyTime());
        		holder.study_content_tv.setText(messge.getStudyConetnt());
        	}
        }
        return convertView;
    }

    public static class ViewHolder {
    	LinearLayout sectionView;
    	ImageView icon;
		TextView section_tv;
		TextView study_time_tv;
		TextView study_content_tv;
	}
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mDataList != null) {
			return mDataList.size();
		}
		return 0;
	}

	@Override
	public GrowthMessage getItem(int position) {
		// TODO Auto-generated method stub
		if (mDataList != null) {
			return mDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setDataList(List<GrowthMessage> list) {
		mDataList = list;
	}
}
