package com.worldchip.bbpawphonechat.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.LearnLogAdapter;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.db.UserDao;
import com.worldchip.bbpawphonechat.entity.LearnLog;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.net.HttpUtils;

public class LearnLogFragment extends Fragment {
	private static final String TAG = "CHRIS";

	private Context mContext;
	private ListView mLearnLogListView;
	private List<LearnLog> LearnLogsList;
	private LearnLogAdapter adapter;
	private ImageView mNote;
	private String deviceid;

	private TextView mTopTv;
	private ImageView mTopIv;

	private String mCareForUserName = "";
	private String mCareForUserNick = "";
	private String mCareForContactHeadUrl = "";
	private UserDao mUserDao;
	private User mControlUser = null;

	public LearnLogFragment(Context context) {
		this.mContext = context;
	}

	public LearnLogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				MyComment.SEND_CONTROL_CHANGE_BROADCAST);
		if(mContext != null)
		mContext.registerReceiver(mContactChangeReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_learn_log_tab_content,
				container, false);
		ImageView iv_head = (ImageView) view
				.findViewById(R.id.iv_top_bar_my_head);
		MyApplication
				.getInstance()
				.getImageLoader()
				.displayImage(MyApplication.getInstance().getHeadImageUrl(),
						iv_head,
						MyApplication.getInstance().getDisplayOptionsHead());

		mUserDao = new UserDao(mContext);

		mLearnLogListView = (ListView) view.findViewById(R.id.lv_learn_log);
		mNote = (ImageView) view.findViewById(R.id.iv_learn_log_null_note);

		mTopTv = (TextView) view.findViewById(R.id.tv_top_bar_title);
		mTopIv = (ImageView) view.findViewById(R.id.iv_top_bar_title);

		mTopTv.setVisibility(View.GONE);
		LearnLogsList = new ArrayList<LearnLog>();
		adapter = new LearnLogAdapter(LearnLogsList, mContext);
		mLearnLogListView.setAdapter(adapter);
		deviceid = MyComment.CONTROL_BABY_NAME;
		initControlInfo();
		return view;
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
	}

	class MyGetLearnLogTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			String result = null;
			String urlPath = MyComment.GET_CONTROL_LEARN_DATA_URL + deviceid;
			try {
				result = HttpUtils.getRequest(urlPath, mContext);
				Log.e(TAG, "......" + result);
				if (result != null && LearnLogsList != null) {
					LearnLogsList.removeAll(LearnLogsList);
					LearnLogsList = MyJsonParserUtil.parserLearnLog(result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			initView();
			if(LearnLogsList != null && LearnLogsList.size() > 0 ){
			   for (int i = 0; i < LearnLogsList.size(); i++) {
				   String head_url = LearnLogsList.get(i).getApkIcon();
			    	MyApplication.getInstance().getImageLoader().loadImage(head_url, null);
			   }
			}
		}
	}

	private void initControlInfo() {
		mCareForUserName = MyComment.CONTROL_BABY_NAME;
		if (mCareForUserName != null && !mCareForUserName.equals("")) {
			mControlUser = mUserDao.getOneContact(mCareForUserName);
			if (mControlUser != null) {
				mCareForContactHeadUrl = mControlUser.getHeadurl();
				if (mCareForContactHeadUrl != null
						&& !mCareForContactHeadUrl.equals("")) {
					MyApplication
							.getInstance()
							.getImageLoader()
							.displayImage(
									mCareForContactHeadUrl,
									mTopIv,
									MyApplication.getInstance()
											.getDisplayOptionsHead());
				} else {
					mTopIv.setImageResource(R.drawable.setting_head_default);
				}
				deviceid = mControlUser.getUsername();
				if (!deviceid.equals("") || deviceid != null) {
					MyApplication.getInstance().ImageAdapter(
							mNote,
							new int[] { R.drawable.learn_log_null_note,
									R.drawable.learn_log_null_note_es,
									R.drawable.learn_log_null_note_en });
					MyGetLearnLogTask getLearnLogTask = new MyGetLearnLogTask();
					getLearnLogTask.execute();
				} 
			}
		} else {
			mTopIv.setVisibility(View.GONE);
			mLearnLogListView.setVisibility(View.GONE);
			MyApplication
					.getInstance()
					.ImageAdapter(
							mNote,
							new int[] {
									R.drawable.learn_log_please_select_paid,
									R.drawable.learn_log_please_select_paid_es,
									R.drawable.learn_log_please_select_paid_en });

			mNote.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {
		if (LearnLogsList != null) {
			if (LearnLogsList.size() != 0) {
				mLearnLogListView.setVisibility(View.VISIBLE);
				mNote.setVisibility(View.GONE);
				//adapter = new LearnLogAdapter(LearnLogsList, mContext);
				//mLearnLogListView.setAdapter(adapter);
				adapter.setmLearnLogesList(LearnLogsList);
				adapter.notifyDataSetChanged();
			} else {
				mLearnLogListView.setVisibility(View.GONE);
				mNote.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(mContactChangeReceiver);
		Log.d(TAG, "LearnLogFragment-----onDestroy");
		super.onDestroy();
	}

	private BroadcastReceiver mContactChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initControlInfo();
		}
	};

}
