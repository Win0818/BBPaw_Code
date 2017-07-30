package com.worldchip.bbpaw.media.camera.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldchip.bbpaw.media.camera.R;


public class DMEffectsListAdapter extends BaseAdapter {
	
	private ArrayList<String> mEffectList;
	private Context mContext;
	private LayoutInflater mInflater;
	private Typeface mTypeFace;
	private int mSelectionPosition = -1;
	
	public DMEffectsListAdapter(Context context, ArrayList<String> effectList) {
		this.mContext = context;
		this.mEffectList = effectList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/COMIC.TTF");
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
            convertView = mInflater.inflate(R.layout.dm_effects_list_item, null);
            holder = new ViewHolder();
            holder.effectTitle = (TextView) convertView.findViewById(R.id.dm_effct_title);
            holder.effectBg = (ImageView) convertView.findViewById(R.id.dm_effct_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == mSelectionPosition) {// 当前选中的Item改变背景颜色
        	holder.effectBg.setBackgroundResource(R.drawable.ic_effect_select_bg);
		} else {
			holder.effectBg.setBackgroundColor(Color.TRANSPARENT);
		}
        holder.effectTitle.setText((String)getItem(position));
        holder.effectTitle.setTypeface(mTypeFace);
		return convertView;
	}

	static class ViewHolder {
		ImageView effectBg;
		TextView effectTitle;
        
    }
	
	public void setData(ArrayList<String> effectList) {
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
