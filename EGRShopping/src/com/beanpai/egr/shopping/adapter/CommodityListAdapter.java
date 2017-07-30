package com.beanpai.egr.shopping.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.beanpai.egr.shopping.entity.Commodity;
import com.beanpai.egr.shopping.image.utils.ImageLoader;
import com.mgle.shopping.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommodityListAdapter extends BaseAdapter {

	private static final String TAG = "CommodityListAdapter---";
	private Context mCtx;
	private List<Commodity> mCommoditys;
	private ImageLoader mLoader = null;
	private DecimalFormat mFormat;
	private DecimalFormat mFormat2;
	private DecimalFormat mFormat3;
	private int mSelectPosition=-1;
	
	public CommodityListAdapter(Context context, List<Commodity> commoditys) 
	{
		mCommoditys = commoditys;

		Log.e(TAG, "CommodityListAdapter...mCommoditys.size=" + (mCommoditys == null ? "0" : mCommoditys.size()));
		mCtx = context;
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		mFormat = new DecimalFormat("###,###.00");
		mFormat2 = new DecimalFormat("###,###");
		mFormat3 = new DecimalFormat("0.0");
	}

	@Override
	public int getCount()
	{
		Log.i(TAG, "mCommoditys.size()= " + mCommoditys.size());
		return mCommoditys.size();
	}

	@Override
	public Commodity getItem(int position)
	{
		return mCommoditys.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	public int getSelectItemIndex()
	{
		return mSelectPosition;
	}

	public void setSelctItem(int position) 
	{
		mSelectPosition = position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewholder = null;
		if (convertView == null)
		{
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.commodity_item, null);
			viewholder.poster = (ImageView) convertView.findViewById(R.id.commodity_item_poster);
			viewholder.nowSalePrice = (TextView) convertView.findViewById(R.id.txt_now_sale_price);
			viewholder.nowIntegral = (TextView) convertView.findViewById(R.id.txt_now_integral);
			viewholder.label = (TextView) convertView.findViewById(R.id.txt_label);
			viewholder.integral = (TextView) convertView.findViewById(R.id.txt_integral);

			viewholder.labelLayout = (LinearLayout) convertView.findViewById(R.id.layout_label);
			viewholder.name = (TextView) convertView.findViewById(R.id.txt_name);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		Commodity commodity = mCommoditys.get(position);

		float nowIntegral = 0.0f;
		float integral = 0.0f;
		float nowSalePrice = 0.0f;
		
		viewholder.poster.setImageResource(R.drawable.loading);
		mLoader.loadImage(commodity.fileurl, viewholder.poster, true, false);
		viewholder.name.setText(commodity.name);
		try {
			nowIntegral = Float.parseFloat(commodity.nowIntegral);
			integral = Float.parseFloat(commodity.integral);
			nowSalePrice = Float.parseFloat(commodity.nowSalePrice);
			
			viewholder.nowSalePrice.setText("гд" + mFormat.format(nowSalePrice));
			viewholder.nowIntegral.setText(mFormat2.format(nowIntegral) + " /");
			viewholder.integral.setText(mFormat2.format(integral) + "");
			viewholder.integral.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			if (integral >= 0 && nowIntegral >= 0 && integral > nowIntegral)
			{
				float lableValue = (nowIntegral / integral) * 10;
				viewholder.labelLayout.setVisibility(View.VISIBLE);
				viewholder.label.setText(mFormat3.format(lableValue));
			} else {
				viewholder.labelLayout.setVisibility(View.INVISIBLE);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		/*if(position == mSelectPosition)
		{
			startBigAnimation(convertView);
		}else{
			startSmallAnimation(convertView);
		}*/
		return convertView;
	}

	public void changData(List<Commodity> commoditys) 
	{
		Log.e(TAG, "changData...commoditys=" + commoditys.size());
		this.mCommoditys = commoditys;
		notifyDataSetChanged();
	}

	class ViewHolder {
		private ImageView poster;
		private TextView nowIntegral;
		private TextView integral;
		private TextView nowSalePrice;
		
		private TextView label;
		private LinearLayout labelLayout;
		private TextView name;
	}
}