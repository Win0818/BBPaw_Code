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

public class HorizontalScrollView extends android.widget.HorizontalScrollView implements android.view.View.OnClickListener{

	private FrameLayout mFrameLayout;
	private BaseAdapter mBaseAdapter;
	private ImageView preImage, nextImage;
	
	public HorizontalScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public HorizontalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HorizontalScrollView(Context context)
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
		/* ����ˮƽ����������Ļ����ƫ��ֵ�� */
		int ScrollOffset = computeHorizontalScrollOffset();
		/* ���������� */
		int ScrollExtent = computeHorizontalScrollExtent();
		/* ��������ǰλ�� */
		int curScrollLoc = ScrollOffset + ScrollExtent;
		/* scrollView �Ŀɹ���ˮƽ��Χ����������ͼ�Ŀ���ܺϡ� */
		int ScrollRange = computeHorizontalScrollRange();

		/* ���ǰλ�� ��ScrollExtent �� ScrollRange ֮��,�������ߵ�View����ʾ */
		if (curScrollLoc > ScrollExtent && curScrollLoc < ScrollRange) 
		{
			if (preImage != null)
				preImage.setVisibility(View.VISIBLE);
			if (nextImage != null)
				nextImage.setVisibility(View.VISIBLE);
			return;
		}
		/* ������������� */
		if (curScrollLoc == ScrollExtent)
		{
			if (preImage != null)
				preImage.setVisibility(View.INVISIBLE);
			return;
		}
		/* �����������ұ� */
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
		LayoutParams params = new LayoutParams(mBaseAdapter.getCount() * 198, LayoutParams.MATCH_PARENT - 10);
		gridView.setLayoutParams(params);  
        gridView.setColumnWidth(183);  
        gridView.setGravity(Gravity.CENTER);
        gridView.setHorizontalSpacing(15);  
        gridView.setScrollBarStyle(View.GONE);
        gridView.setStretchMode(GridView.NO_STRETCH);  
        gridView.setNumColumns(mBaseAdapter.getCount());  
        gridView.setAdapter(mBaseAdapter);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setHorizontalScrollBarEnabled(false);
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