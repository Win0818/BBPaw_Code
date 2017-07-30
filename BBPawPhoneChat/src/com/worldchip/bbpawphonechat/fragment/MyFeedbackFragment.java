package com.worldchip.bbpawphonechat.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.MyFeedbackActivity;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity;
import com.worldchip.bbpawphonechat.net.HttpUtils;

public class MyFeedbackFragment extends Fragment {
	private ListView list_my_feedback;
	private Context context;

	private MyAdapter myAdapter;

	private List<CommonProblemEntity.ListQuestionEntity> listEntity = new ArrayList<CommonProblemEntity.ListQuestionEntity>();
	private TextView tv_head;

	public MyFeedbackFragment(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public MyFeedbackFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_feedback, container,
				false);
		initView(view);
		return view;
	}

	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new GetCommonProblemTask().execute();
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		tv_head = (TextView) view.findViewById(R.id.tv_head);
		list_my_feedback = (ListView) view.findViewById(R.id.list_my_feedback);
		if (listEntity != null) {
			tv_head.setVisibility(View.GONE);
			myAdapter = new MyAdapter(listEntity, context);
			list_my_feedback.setAdapter(myAdapter);

		}

	}

	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (listEntity != null) {
			listEntity.clear();
		}
	}

	class MyAdapter extends BaseAdapter {
		private List<CommonProblemEntity.ListQuestionEntity> data;

		// private Context mContext;
		private LayoutInflater mLayoutInflater;

		public MyAdapter(List<CommonProblemEntity.ListQuestionEntity> data,
				Context mContext) {
			// TODO Auto-generated constructor stub
			this.data = data;
			this.mLayoutInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return data.size();

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ListQuestionEntity entity = data.get(position);
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.item_my_feedback, null);
				holder.tv_my_feedback = (TextView) convertView
						.findViewById(R.id.tv_item_my_feedback);
				holder.iv_my_feedback_state = (ImageView) convertView
						.findViewById(R.id.iv_item_myFeedback_state);
				holder.tv_my_feedback_time = (TextView) convertView
						.findViewById(R.id.tv_item_my_feedback_time);
				convertView.setTag(holder);

			}
			holder = (ViewHolder) convertView.getTag();

			holder.tv_my_feedback.setText(position+1+"."+entity.getContent());
			holder.tv_my_feedback_time.setText(entity.getCommit_time());

			holder.iv_my_feedback_state
					.setBackgroundResource(Color.TRANSPARENT);

			if (entity.getIsReplied()) {
				holder.iv_my_feedback_state
						.setBackgroundResource(R.drawable.my_feedback_replied);
				if (entity.getIsSolved()) {
					holder.iv_my_feedback_state
							.setBackgroundResource(R.drawable.my_feedback_resolved);
				}
			}

			holder.tv_my_feedback.setClickable(true);
			holder.tv_my_feedback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MyFeedbackFragment.this
							.getActivity(), MyFeedbackActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("QuestionEntity", entity);
					intent.putExtra("Question", bundle);
					startActivity(intent);

				}
			});

			return convertView;
		}

		class ViewHolder {
			private TextView tv_my_feedback;
			private ImageView iv_my_feedback_state;
			private TextView tv_my_feedback_time;
		}

	}

	class GetCommonProblemTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			String result = null;
			String urlPath = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=listquestiontype&parent_account="
					+ MyApplication.getInstance().getUserName();
			try {
				result = HttpUtils.getRequest(urlPath, context);

				if (!"".equals(result)&&result!=null) {
					listEntity = MyJsonParserUtil.parserCommonProblem(result);
					if (listEntity == null) {
						Log.e("返回解析数据", "...listEntity空的...");

					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			initView();
		}

		private void initView() {
			// TODO Auto-generated method stub
			if (listEntity != null) {

				myAdapter = new MyAdapter(listEntity, context);
				list_my_feedback.setAdapter(myAdapter);
				myAdapter.notifyDataSetChanged();
			}

		}
	}

}
