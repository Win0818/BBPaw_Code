package com.worldchip.bbp.ect.adapter;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.db.BrowserData;
import com.worldchip.bbp.ect.entity.BrowserInfo;
import com.worldchip.bbp.ect.view.BrowserDialog;
import com.worldchip.bbp.ect.view.BrowserDialog.onBroswerCheckListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BrowserListviewAdapter extends BaseAdapter {
	private List<BrowserInfo> mList;
	private Context mContext;

	public BrowserListviewAdapter(List<BrowserInfo> list, Context context) {
		this.mList = list;
		this.mContext = context;
	}

	public void setAdapter(List<BrowserInfo> list) {
		this.mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.parentalcontrol_browser_list_content, null);
		TextView textName = (TextView) view.findViewById(R.id.tv_name);
		textName.setText(mList.get(arg0).title);

		TextView textUrl = (TextView) view.findViewById(R.id.tv_url);
		textUrl.setText(mList.get(arg0).url);
		Button btnDelete = (Button) view.findViewById(R.id.btn_delete);
		setThisOnclickListener(textName, textUrl, btnDelete);
		return view;
	}

	private void setThisOnclickListener(final TextView textName,
			final TextView textUrl, Button btnDelete) {
		textUrl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EnterBrowserActivity(textUrl.getText().toString());
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				delBrowserData(textName);

			}
		});
	}

	private void delBrowserData(TextView text) {
		BrowserDialog myDialog = new BrowserDialog(mContext, new MyCheckListener(text));
		myDialog.show();
		myDialog.setCancelable(false);
	}

	private void EnterBrowserActivity(String textUrl) {
		Intent intent = new Intent();
		intent.putExtra("address", textUrl);
		ComponentName cn = new ComponentName("com.worldchip.bbp.ect",
				"com.worldchip.bbp.ect.activity.BrowserActivity");
		intent.setComponent(cn);
		mContext.startActivity(intent);
	}

	private class MyCheckListener implements onBroswerCheckListener {
		private int id;
		private TextView textName;

		public MyCheckListener(TextView text) {
			this.textName = text;
		}

		@Override
		public void onYes() {
			// TODO Auto-generated method stub
			BrowserData.deleteBrowserInfo(mContext, textName.getText()
					.toString());
			mList = null;
			mList = BrowserData.getBrowserAddressList(mContext);
			notifyDataSetChanged();
		}

		@Override
		public void onNo() {
			// TODO Auto-generated method stub
		}

	}
}
