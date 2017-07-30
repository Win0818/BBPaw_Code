package com.worldchip.bbpawphonechat.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.CommonProblemActivity;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity;
import com.worldchip.bbpawphonechat.net.HttpUtils;

public class CommonProblemFragment extends Fragment {

	private ListView listview;

	private myAdapter adapter;

	private String result = "";
	private Context mContext;



	private List<CommonProblemEntity.ListQuestionEntity> listEntity = new ArrayList<CommonProblemEntity.ListQuestionEntity>();

	public CommonProblemFragment(Context mContext) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
	}
	public CommonProblemFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_common_problem,
				container, false);
		
		initView(view);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new GetCommonProblemTask().execute();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (listEntity != null) {
			listEntity.clear();
		}

	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		listview = (ListView) view.findViewById(R.id.list_common_problem);

		if (listEntity != null) {
			adapter = new myAdapter(listEntity, mContext);
			listview.setAdapter(adapter);
			
		}

	}

	class GetCommonProblemTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String result = null;
			String urlPath = "http://push.bbpaw.com.cn/interface/chatoperation.php?param=listcommonquestion";
			try {
				result = HttpUtils.getRequest(urlPath, mContext);
				// Log.e("返回jsonx数据", "......" + result);
				if (!"".equals(result)&&result!=null) {
					// dataList.removeAll(LearnLogsList);
					// Toast.makeText(mContext, ".......",
					// Toast.LENGTH_LONG).show();
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
				adapter = new myAdapter(listEntity, mContext);
				listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}

		}
	}

	class myAdapter extends BaseAdapter {
		private List<CommonProblemEntity.ListQuestionEntity> data;
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public myAdapter(List<CommonProblemEntity.ListQuestionEntity> data,
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
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.item_common_problem, null);
				holder.tv_problem = (TextView) convertView
						.findViewById(R.id.tv_item_common_problem);
				convertView.setTag(holder);

			}
			holder = (ViewHolder) convertView.getTag();
			holder.tv_problem.setText(position+1+"."+data.get(position).getContent());
			Log.e("data.get(position).getContent()", data.get(position)
					.getContent());
			holder.tv_problem.setClickable(true);
			holder.tv_problem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CommonProblemFragment.this
							.getActivity(), CommonProblemActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("QuestionEntity", data.get(position));
					intent.putExtra("Question", bundle);
					startActivity(intent);

				}
			});

			return convertView;
		}

		class ViewHolder {
			private TextView tv_problem;
		}

	}
}
