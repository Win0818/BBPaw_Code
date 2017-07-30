package com.worldchip.bbp.ect.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HorizontalScrollViewCostom extends android.widget.HorizontalScrollView implements android.view.View.OnClickListener{

	private FrameLayout mFrameLayout;
	private BaseAdapter mBaseAdapter;
	private ImageView preImage, nextImage;
	
	public HorizontalScrollViewCostom(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public HorizontalScrollViewCostom(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HorizontalScrollViewCostom(Context context)
	{
		super(context);
		init();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) 
	{
		super.onScrollChanged(l, t, oldl, oldt);
		resetImageView();
	}

	private void resetImageView() 
	{
		/* 计算水平方向滚动条的滑块的偏移值。 */
		int ScrollOffset = computeHorizontalScrollOffset();
		/* 滚动条长度 */
		int ScrollExtent = computeHorizontalScrollExtent();
		/* 滚动条当前位置 */
		int curScrollLoc = ScrollOffset + ScrollExtent;
		/* scrollView 的可滚动水平范围是所有子视图的宽度总合。 */
		int ScrollRange = computeHorizontalScrollRange();

		/* 如果当前位置 在ScrollExtent 和 ScrollRange 之间,左右两边的View都显示 */
		if (curScrollLoc > ScrollExtent && curScrollLoc < ScrollRange) 
		{
			if (preImage != null)
				preImage.setVisibility(View.VISIBLE);
			if (nextImage != null)
				nextImage.setVisibility(View.VISIBLE);
			return;
		}
		/* 如果滚动到最左边 */
		if (curScrollLoc == ScrollExtent)
		{
			if (preImage != null)
				preImage.setVisibility(View.INVISIBLE);
			return;
		}
		/* 如果滚动到最右边 */
		if (curScrollLoc >= ScrollRange)
		{
			if (nextImage != null)
				nextImage.setVisibility(View.INVISIBLE);
			return;
		}

	}

	private void init() 
	{
		mFrameLayout = new FrameLayout(getContext());
		mFrameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mFrameLayout);
	}

	private void buildItemView() 
	{
		if (mBaseAdapter == null)
			return;
		LinearLayout linearLayout = new LinearLayout(getContext());
		GridView gridView = new GridView(getContext());
		LayoutParams params = new LayoutParams(mBaseAdapter.getCount() * 200, LayoutParams.MATCH_PARENT - 15);
		gridView.setLayoutParams(params);  
        gridView.setColumnWidth(183);  
        gridView.setGravity(Gravity.CENTER);
        gridView.setHorizontalSpacing(15);  
        gridView.setScrollBarStyle(View.GONE);
        gridView.setStretchMode(GridView.NO_STRETCH);  
        gridView.setNumColumns(mBaseAdapter.getCount()); 
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setHorizontalScrollBarEnabled(false);
        gridView.setAdapter(mBaseAdapter);
        linearLayout.addView(gridView);
		mFrameLayout.addView(linearLayout);
	}

	public void setAdapter(BaseAdapter baseAdapter)
	{
		if (baseAdapter == null)
			return;
		mBaseAdapter = baseAdapter;
		if(mBaseAdapter.getCount() <= 3)
		{
			nextImage.setVisibility(View.INVISIBLE);
		}
		mBaseAdapter.registerDataSetObserver(new DataSetObserver() 
		{
			@Override
			public void onChanged() 
			{
				buildItemView();
				super.onChanged();
			}
		});
		mBaseAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) 
	{
		if (v.getId() == preImage.getId()) 
		{
			fling(-800);
			return;
		}
		if (v.getId() == nextImage.getId()) 
		{
			fling(800);
			return;
		}
	}

	public void setImageView(ImageView preImage, ImageView nextImage)
	{
		this.preImage = preImage;
		this.nextImage = nextImage;
		preImage.setVisibility(View.INVISIBLE);
		if (preImage != null) 
		{
			preImage.setOnClickListener(this);
		}
		if (nextImage != null) {
			nextImage.setOnClickListener(this);
		}
	}
}