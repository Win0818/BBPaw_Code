package com.mgle.member.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.egreat.devicemanger.DeviceManager;
import com.mgle.member.R;
import com.mgle.member.adapter.RecordAdapter;
import com.mgle.member.entity.Record;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.HttpUtils;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordLayout extends FrameLayout implements View.OnFocusChangeListener {

	private static final int REFRESH_TEXTVIEW_UI = 1001;
	
	private Activity mActivity;
	private Handler mMainHandler;
	
	private TextView mPageTextView;
	private ListView mRecordListView;
	
	private RecordAdapter mRecordAdapter;
	
	private Record mCurrentRecord = null;
	
	public RecordLayout(Context context,Handler mMainHandler)
	{
		super(context);
		this.mActivity = (Activity) context;
		this.mMainHandler = mMainHandler;
		
		initView();
	}
	
	//初始化
	@SuppressLint("InflateParams")
	private void initView() 
	{
		View localView = LayoutInflater.from(mActivity).inflate(R.layout.record_view,null);
		addView(localView);
		
		mPageTextView = (TextView) localView.findViewById(R.id.tv_page);
		mRecordListView = (ListView) localView.findViewById(R.id.recordlist);
		
		initListener();
		initData();
	}

	private void initListener() 
	{
		mRecordListView.setOnFocusChangeListener(this);
		mRecordListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
			{
				Toast.makeText(mActivity, "缺少获取提货验证码接口!", Toast.LENGTH_LONG).show();
			}
		});
		
		mRecordListView.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,long id) 
			{
				mCurrentRecord = (Record) parent.getAdapter().getItem(position);
				dialog();
				return true;
			}
		});
	}

	//初始化数据
	private void initData() 
	{
		new DataTask().execute();
	}
	
	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() 
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,long id) 
		{
			Log.e("onItemSelected", "onItemSelected");
			mRecordAdapter.setSelectedPosition(position);
			mRecordAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			Log.e("onNothingSelected", "onNothingSelected");
		}
	};

	@Override
	public void onFocusChange(View view, boolean hasFocus) 
	{
		switch (view.getId())
		{
			case R.id.recordlist:
				mRecordListView.setSelector(color.transparent);
				if(hasFocus)
				{
					Message message = new Message();
					message.obj = 3;
					message.what = REFRESH_TEXTVIEW_UI;
					mMainHandler.sendMessage(message);
					mRecordListView.setOnItemSelectedListener(mOnItemSelectedListener);
					mRecordListView.postDelayed(new Runnable() 
					{  
					    @Override  
					    public void run()
					    {  
					    	mRecordListView.requestFocusFromTouch();  
					    	mRecordListView.setSelection(0); 
					    	mRecordListView.setSelector(R.drawable.recordlist_style);
					    }  
					},100);
				}else{
					mRecordAdapter.setSelectedPosition(-1);
					mRecordAdapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
		}
	}
	
	private class DataTask extends AsyncTask<Void, Void, List<Record>> 
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		
		@Override
		protected List<Record> doInBackground(Void... params) 
		{
			HttpRequest request = new HttpRequest(DeviceManager.getSeleteExchangeRecordUrl());
			HttpResponse respone = HttpUtils.httpGet(request);
			Log.d("RecordLayout", "respone.getResponseCode=" + respone.getResponseCode());
			if (respone.getResponseCode() == 200) 
			{
				Log.d("RecordLayout", "respone=" + respone.getResponseBody());
				try {
					JSONArray jsonarray = new JSONArray(respone.getResponseBody());
					List<Record> records = new ArrayList<Record>();
					for (int i = 0; i < jsonarray.length(); i++) 
					{
						JSONObject oj = jsonarray.getJSONObject(i); 
						Record record = new Record();
						record.id = oj.getInt("id");
						record.recordId = oj.getString("recordId"); 
						record.name = oj.getString("name"); 
						record.currentStatus = oj.getInt("currentStatus"); 
						record.nowIntegral = oj.getInt("nowIntegral"); 
						record.totalIntegral = oj.getInt("totalIntegral"); 
						record.imageUrl = oj.getString("imageUrl"); 
						record.count = oj.getInt("count"); 
						record.sendType = oj.getInt("sendType"); 
						record.addTime = oj.getLong("addTime"); 
						record.orderUpdateUrl = oj.getString("orderUpdateUrl"); 
						record.detailUrl = oj.getString("detailUrl"); 
						records.add(record);
					}
					return records;
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<Record> records)
		{
			super.onPostExecute(records);
			mRecordAdapter = new RecordAdapter(mActivity, records);
			mRecordListView.setAdapter(mRecordAdapter);
			mPageTextView.setText("第1条/共" + mRecordAdapter.getCount() + "条");
		}
	}
	
	private void dialog() 
	{
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setMessage("确定删除吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				//new DelRecordTask().execute();
				Toast.makeText(mActivity, "删除接口有问题!", Toast.LENGTH_LONG).show();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	private class DelRecordTask extends AsyncTask<Void, Void, String> 
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			String res = "";
			String url = DeviceManager.getDelExchangeRecordUrl() + "&ids="+mCurrentRecord.recordId;
			Log.e("RecordLayout", "url="+url);
			HttpRequest request = new HttpRequest(url);
			HttpResponse respone = HttpUtils.httpGet(request);
			Log.d("RecordLayout", "respone.getResponseCode=" + respone.getResponseCode());
			if (respone.getResponseCode() == 200) 
			{
				res = respone.getResponseBody();
			}
			Log.e("RecordLayout", "res="+res);
			Toast.makeText(mActivity, "删除接口有问题!", Toast.LENGTH_LONG).show();
			return res;
		}
		
		@Override
		protected void onPostExecute(String res)
		{
			super.onPostExecute(res);
			try {
				JSONObject jsonObject = new JSONObject(res);
				int result = jsonObject.getInt("result");
				if(result == 1)
				{
					Toast.makeText(mActivity, "删除成功!", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mActivity, "删除失败!", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}