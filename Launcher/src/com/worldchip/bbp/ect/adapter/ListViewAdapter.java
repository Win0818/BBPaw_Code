package com.worldchip.bbp.ect.adapter;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.entity.BrowserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<BrowserInfo> mList;

	public ListViewAdapter(Context context, List<BrowserInfo> list) {
		this.mList = list;
		this.mContext = context;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.listview_content, null);
		TextView tv = (TextView) view.findViewById(R.id.tv);

		tv.setText(mList.get(arg0).url + "");
		return view;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0).url;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

}
