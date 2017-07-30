package com.worldchip.bbpaw.media.camera.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.worldchip.bbpaw.media.camera.R;

public class EffectListAdapter extends BaseAdapter {
	
	private ArrayList<Integer> mEffectList;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mSelectionPosition = -1;
	
	public EffectListAdapter(Context context, ArrayList<Integer> effectList) {
		this.mContext = context;
		this.mEffectList = effectList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mEffectList == null) {
			return 0;
		}
		return mEffectList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mEffectList == null) {
			return null;
		}
		return mEffectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.effect_list_item, null);
            holder = new ViewHolder();
            holder.penPic = (ImageView) convertView.findViewById(R.id.effct_pic);
            holder.penPicBg = (ImageView) convertView.findViewById(R.id.effct_pic_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.penPic.setImageResource((Integer)getItem(position));
        if (position == mSelectionPosition) {// 当前选中的Item改变背景颜色
        	holder.penPicBg.setBackgroundResource(R.drawable.ic_effect_select_bg);
		} else {
			holder.penPicBg.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}

	static class ViewHolder {
        ImageView penPic;
        ImageView penPicBg;
        
    }
	
	public void setData(ArrayList<Integer> effectList) {
		if (null != mEffectList) {
			mEffectList.clear();
		}
		mEffectList = effectList;
	}
	
	
   public void setSeclection(int position) {
	   mSelectionPosition = position;
	   notifyDataSetChanged();
   }
   
}
