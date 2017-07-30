package com.worldchip.bbpawphonechat.adapter;

import java.util.List;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.Contact;
import com.worldchip.bbpawphonechat.entity.HabitCategory;
import com.worldchip.bbpawphonechat.entity.LearnLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LearnLogAdapter extends BaseAdapter {

	private List<LearnLog> mLearnLogesList;
	private Context mContext;
	private LayoutInflater mInflater;

	public LearnLogAdapter(List<LearnLog> learnLog, Context context) {
		this.mContext = context;
		this.mLearnLogesList = learnLog;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mLearnLogesList == null ? 0 : mLearnLogesList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mLearnLogesList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHold hold;
		if (view == null) {
			hold = new ViewHold();
			view = mInflater.inflate(R.layout.item_learn_log_lv, null);
			hold.mIvLearnDate = (TextView) view
					.findViewById(R.id.item_tv_learn_date);
			hold.mIvLearntime = (TextView) view
					.findViewById(R.id.item_tv_learn_time);
			hold.mIvLearnLogContent = (TextView) view
					.findViewById(R.id.item_tv_learn_content);
			hold.mApkIconIv = (ImageView) view.findViewById(R.id.iv_learn_apk_icon);
			view.setTag(hold);
		} else {
			hold = (ViewHold) view.getTag();
		}
		if (mLearnLogesList != null) {
			LearnLog learnLog = mLearnLogesList.get(position);
			hold.mIvLearnDate.setText(learnLog.getLearnDate());
			hold.mIvLearntime.setText(learnLog.getLearnTime());
			hold.mIvLearnLogContent.setText(learnLog.getLearnContent());
			MyApplication.getInstance().getImageLoader().displayImage(learnLog.getApkIcon(), hold.mApkIconIv
					,MyApplication.getInstance()
					.getDisplayOptionsHead());
		}
		return view;
	}

	private class ViewHold {
		private TextView  mIvLearnDate, mIvLearntime, mIvLearnLogContent;
		private ImageView mApkIconIv;
	}

	public List<LearnLog> getmLearnLogesList() {
		return mLearnLogesList;
	}

	public void setmLearnLogesList(List<LearnLog> mLearnLogesList) {
		this.mLearnLogesList = mLearnLogesList;
	}
	
	
}
