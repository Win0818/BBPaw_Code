package com.mgle.member.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.mgle.member.R;
import com.mgle.member.entity.Record;
import com.mgle.member.util.AsynImageLoader;
import com.mgle.member.util.Common;
import com.mgle.member.util.ScaleAnimEffect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordAdapter extends BaseAdapter {

	private static float big_x = 1.02F;
	private static float big_y = 1.02F;
	private ScaleAnimEffect animEffect = new ScaleAnimEffect();
	
	private List<Record> mRecordLisst;
	private Activity mActivity;
	private AsynImageLoader asynImageLoader; 
	
	private int selectedPosition = -1;// 选中的位置
	
	public RecordAdapter(Activity mActivity,List<Record> mRecordLisst) 
	{
		this.mActivity = mActivity;
		this.mRecordLisst = mRecordLisst;
		asynImageLoader = new AsynImageLoader();
	}
	
	@Override  
    public int getCount() 
	{  
		int count = 0;
		if(mRecordLisst != null)
		{
			count = mRecordLisst.size();
		}
		return count; 
    }  

    @Override  
    public Object getItem(int position)
    {  
        return mRecordLisst.get(position);  
    }  

    @Override  
    public long getItemId(int position) 
    {  
        return position;  
    }  
    
    public void setSelectedPosition(int position) 
	{  
		this.selectedPosition = position;  
    }
    
	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		System.out.println("position="+position);
		ViewHolder mViewHolder = null;
		if (convertView == null) 
		{
			mViewHolder = new ViewHolder();
			convertView = View.inflate(mActivity, R.layout.record_item, null);
			
			mViewHolder.mRecordItemRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.recordlayout);
			mViewHolder.mYearTextView = (TextView) convertView.findViewById(R.id.tv_year);
			mViewHolder.mTimeTextView = (TextView) convertView.findViewById(R.id.tv_time);
			mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.tv_image);
			mViewHolder.mTitleTextView = (TextView) convertView.findViewById(R.id.tv_title);
			mViewHolder.mPriceTextView0 = (TextView) convertView.findViewById(R.id.tv_price_00);
			mViewHolder.mPriceTextView1 = (TextView) convertView.findViewById(R.id.tv_price_01);
			mViewHolder.mStateTextView = (TextView) convertView.findViewById(R.id.tv_state);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Toast.makeText(mActivity, "aaa", Toast.LENGTH_LONG).show();
			}
		});
		Record record = mRecordLisst.get(position);
		SimpleDateFormat sdf_date = new SimpleDateFormat("MM-dd");
		Date dt = new Date(record.addTime * 1000);  
		String date = sdf_date.format(dt); 
		mViewHolder.mYearTextView.setText("2015");
		mViewHolder.mTimeTextView.setText(date);
		asynImageLoader.showImageAsyn(mViewHolder.mImageView, record.imageUrl, R.drawable.loading);
		mViewHolder.mTitleTextView.setText(record.name);
		mViewHolder.mTitleTextView.setSelected(true);
		/*if(position%3 == 0)
		{
			mViewHolder.mPriceTextView0.setText("￥ 5.00 x 10 = ￥ 50.0 (");
			mViewHolder.mPriceTextView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coin, 0, 0, 0);
			mViewHolder.mPriceTextView1.setText("5000)");
		}else if(position%3 == 1)
		{*/
			mViewHolder.mPriceTextView0.setText(" " + Common.getDecimalFormat(record.nowIntegral) + " * "+ record.count +" = ");
			mViewHolder.mPriceTextView0.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coin_big, 0, 0, 0);
			mViewHolder.mPriceTextView1.setText(" ￥ " + Common.getDecimalFormat(record.totalIntegral / 100));
		/*}else if(position%3 == 2){
			mViewHolder.mPriceTextView0.setText("￥ 100.0 = ");
			mViewHolder.mPriceTextView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coin, 0, 0, 0);
			mViewHolder.mPriceTextView1.setText("10300");
		}*/
		
		mViewHolder.mStateTextView.setText("待提货");
		mViewHolder.mStateTextView.setBackgroundResource(R.drawable.record_state_red);
		mViewHolder.mRecordItemRelativeLayout.setBackgroundResource(R.drawable.record_bg_line);
		//判断是否选中
		if (selectedPosition == position)
		{ 
			startBigAnimation(mViewHolder.mRecordItemRelativeLayout);
		}else{
			startSmallAnimation(mViewHolder.mRecordItemRelativeLayout);
		}
		return convertView;
	}
	
	public class ViewHolder 
	{
		private RelativeLayout mRecordItemRelativeLayout;
		private TextView mYearTextView,mTimeTextView;
		private ImageView mImageView;
		private TextView mTitleTextView,mPriceTextView0,mPriceTextView1;
		private TextView mStateTextView;
	}
	
	/**
	 * 放大
	 * @param index
	 */
	private void startBigAnimation(View view)
	{
		view.bringToFront();
		animEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 100);
		Animation anim = animEffect.createAnimation();
		view.startAnimation(anim);
	}
	
	/**
	 * 缩小
	 * @param index
	 */
	private void startSmallAnimation(View view)
	{
		animEffect.setAttributs(1.0F, 1.0F, 1.0F, 1.0F, 100);
		view.startAnimation(animEffect.createAnimation());
	}
}