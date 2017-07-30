package com.worldchip.bbp.bbpawmanager.cn.view;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.adapter.HelpPagerAdapter;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;

public class HelpView extends Dialog {

    private HelpPagerAdapter mAdapter;  
    //View数组  
    private ArrayList<View> mHelpPagerViews;  
    //ViewPager  
    private HelpViewPager mHelpViewPager;  
	private Context mContext;
	private boolean mAutoScroll = true;
	private boolean mOnTouch = false;
	private static final int PAGER_SCROLL_NEXT = 100;
	private int mPosition = 0;
	private int mCurrDot = -1;
	private ImageView[] mDotViews;
	private static final int PAGER_COUNT = Utils.HELP_PAGER_VIEW_RES.length;
	 
	public interface HelpViewListener {
		public void onBeginTouch();
		public void onEndTouch();
		public void onClose();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case PAGER_SCROLL_NEXT:
				if (mHelpViewPager!=null) {
					mHelpViewPager.setCurrentItem(mPosition % PAGER_COUNT);
				}
				break;

			default:
				break;
			}
		}
	};
	
	public HelpView(Context context) {
		super(context, R.style.HelpViewGalleryStyle);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		init();
	}

	

	private void init() {
		// TODO Auto-generated method stub
		mHelpPagerViews =new ArrayList<View>();  
        for(int i=0; i< PAGER_COUNT;i++)  
        {  
            ImageView imageView = new ImageView(mContext);
            imageView.setBackgroundResource((Utils.HELP_PAGER_VIEW_RES[i]));
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mHelpPagerViews.add(imageView);  
        }
        
        mHelpViewPager=(HelpViewPager)findViewById(R.id.help_viewPager);
        LayoutParams layoutParams = mHelpViewPager.getLayoutParams();
        layoutParams.width = (int) (Configure.getScreenWidth(mContext) * 0.85f);
        layoutParams.height = (int) (Configure.getScreenHeight(mContext) * 0.85f);
        mHelpViewPager.setLayoutParams(layoutParams);
        
        mHelpViewPager.setListener(new HelpViewListener() {
			@Override
			public void onBeginTouch() {
				// TODO Auto-generated method stub
				mOnTouch = true;
			}

			@Override
			public void onEndTouch() {
				// TODO Auto-generated method stub
				mOnTouch = false;
			}
			
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				dismiss();
			}
			
		});
        mHelpViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				mPosition = position;
				int index = position % PAGER_COUNT;
				setCurPoint(index);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub
				if (mCurrDot == -1) {//第一次显示时，需要单独处理
					int index = position % PAGER_COUNT;
					setCurPoint(index);
	            }
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        mAdapter=new HelpPagerAdapter(mHelpPagerViews);  
        mHelpViewPager.setAdapter(mAdapter);  
        mHelpViewPager.setOffscreenPageLimit(3);//预缓存3个item
        mHelpViewPager.setCurrentItem(0);
        startAutoScroll();
        initDotView();
	}
	
	private void setCurPoint(int curPostion) {
		if (mDotViews!= null && mDotViews.length >0) {
			mDotViews[curPostion].setImageResource(R.drawable.dot_show);
			if (curPostion != mCurrDot && mCurrDot != -1) {
				mDotViews[mCurrDot].setImageResource(R.drawable.dot_hide);
			}
			mCurrDot = curPostion;
		}
	}

	 private void initDotView() {
	        LinearLayout layout = (LinearLayout) findViewById(R.id.dot_views);
	        if (mDotViews == null) {
	        	mDotViews = new ImageView[PAGER_COUNT];
	            for (int i = 0; i < PAGER_COUNT; i++) {
	                ImageView imageView = new ImageView(mContext);
	                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	                imageView.setImageResource(R.drawable.dot_hide);
	                layout.addView(imageView);
	                mDotViews[i] = imageView;
	            }
	        }
	    }
	 
    private void startAutoScroll() {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (mAutoScroll) {
                    count = 0;
                    // 这段逻辑用于用户手动滑动时，停止自动滚动,否则5秒切一次图
                    while (count < 50) {
                        count++;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (mOnTouch) {// 用戶手动滑动时，停止自动滚动
                            count = 0;
                        }
                    }
                    mPosition++;
                    Message msg = new Message();
                    msg.what = PAGER_SCROLL_NEXT;
                    msg.arg1 = mPosition;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		mAutoScroll = false;
	}
	
}
