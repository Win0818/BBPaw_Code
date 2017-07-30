package com.worldchip.bbp.bbpawmanager.cn.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;

public class VaccineAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
	//private Typeface mTypeFace;
	private List<VaccineInfo> mDataList;
	private boolean isEditMode = false;
	private OnAllCheckedListener mAllCheckedListener = null;
	
	public interface OnAllCheckedListener {
		public void OnAllChecked(boolean allChecked);
	}
	
    public VaccineAdapter(Context context, List<VaccineInfo> list) {
        //super(context, resource, textViewResourceId);
    	mInflater = LayoutInflater.from(context);
		//mTypeFace = Typeface.createFromAsset(context.getAssets(),"fonts/COMIC.TTF");
		mDataList = list;
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.vaccine_list_item, null);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.vaccine_item_checkbox);
			holder.checkBox.setOnCheckedChangeListener(new ItemCheckListener());
			holder.ege = (TextView) convertView.findViewById(R.id.vaccine_ege);
			holder.vaccineType = (TextView) convertView.findViewById(R.id.vaccine_type);
			holder.vaccineExplain = (TextView) convertView.findViewById(R.id.vaccine_explain);
			holder.date = (TextView) convertView.findViewById(R.id.vaccine_date);
			holder.date.setSelected(true);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		VaccineInfo info = getItem(position);
		if (info != null) {
			holder.ege.setText(info.getAgeTitle());
			holder.vaccineType.setText(info.getVaccineTypeName());
			holder.vaccineExplain.setText(info.getVaccineExplain());
			if (isEditMode) {
				holder.checkBox.setTag(position);
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.checkBox.setChecked(info.isChecked());
			} else {
				holder.checkBox.setVisibility(View.INVISIBLE);
				//holder.date.setText("");
			}
			String date = info.getDate();
			if (date.contains("-")) {
				holder.date.setText(date);
			} else {
				holder.date.setText(Common.timeStamp2Date(info.getDate(),"yyyy-MM-dd"));
			}
		}
        return convertView;
    }

    public static class ViewHolder {
    	CheckBox checkBox;
    	TextView ege;
		TextView vaccineType;
		TextView vaccineExplain;
		TextView date;
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
	public VaccineInfo getItem(int position) {
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

	public List<VaccineInfo> getDataList() {
		return mDataList;
	}

	public void setDataList(List<VaccineInfo> mDataList) {
		this.mDataList = mDataList;
	}
	
	public void setVaccineEditMode(boolean mode) {
		isEditMode = mode;
	}
	
	
	private class ItemCheckListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			// TODO Auto-generated method stub
			int position = (Integer)view.getTag();
			onCheckedChange(position, isChecked);
		}
	}
	
	private void onCheckedChange(int position, boolean isChecked) {
		if (mDataList != null) {
			VaccineInfo info = getItem(position);
			info.setChecked(isChecked);
		}
		if (!isChecked && mAllCheckedListener != null) {
			mAllCheckedListener.OnAllChecked(false);
		}
	}
	
	public void setOnAllCheckListener(OnAllCheckedListener allCheckedListener) {
		mAllCheckedListener = allCheckedListener;
	}
	
	public boolean hasChecked() {
		if (mDataList != null) {
			for (VaccineInfo info : mDataList) {
				if (info.isChecked()) {
					return true;
				}
			}
		}
		return false;
	}
}
