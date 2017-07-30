package com.worldchip.bbpawphonechat.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.adapter.MyViewPageAdapter;
import com.worldchip.bbpawphonechat.application.HXSDKHelper;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.application.MyChatHXSDKHelper;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;

public class NavigationActivity extends Activity implements
		OnPageChangeListener, OnClickListener {
	// 定义ViewPager对象
	private ViewPager viewPager;
	// 定义ViewPager适配器
	private MyViewPageAdapter vpAdapter;
	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	private ImageView mIvEnter;

	private static final int sleepTime = 2000;
	// 引导图片资源
	private int[] pics = new int[4];
	// 底部小点的图片
	private ImageView[] points;
	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_pages);
		initView();
		initData();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// 实例化ArrayList对象
		views = new ArrayList<View>();
		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		mIvEnter = (ImageView) findViewById(R.id.iv_navigation_enter);
		MyApplication.getInstance().ImageAdapter(
				mIvEnter,
				new int[] { R.drawable.navigation_click_enter,
						R.drawable.navigation_click_enter_es,
						R.drawable.navigation_click_enter_en });

		mIvEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enterMainAcivity();
			}
		});

		// 实例化ViewPager适配器
		vpAdapter = new MyViewPageAdapter(views);

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 定义一个布局并设置参数
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		// 初始化引导图片列表
		switch (MyApplication.getInstance().system_local_language) {
		case 0:
			int[] temp = { R.drawable.guide_1, R.drawable.guide_2,
					R.drawable.guide_3, R.drawable.guide_4 };
			pics = temp;

			break;
		case 1:
			int[] temp1 = { R.drawable.guide_1_es, R.drawable.guide_2_es,
					R.drawable.guide_3_es, R.drawable.guide_4_es };
			pics = temp1;

			break;
		case 2:
			int[] temp2 = { R.drawable.guide_1_en, R.drawable.guide_2_en,
					R.drawable.guide_3_en, R.drawable.guide_4_en };
			pics = temp2;
			break;

		default:
			break;
		}
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			// 防止图片不能填满屏幕
			iv.setScaleType(ScaleType.FIT_XY);
			// 加载图片资源
			iv.setImageResource(pics[i]);
			views.add(iv);
		}

		// 设置数据
		viewPager.setAdapter(vpAdapter);
		// 设置监听
		viewPager.setOnPageChangeListener(this);

		// 初始化底部小点
		initPoint();
	}

	/**
	 * 初始化底部小点
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

		points = new ImageView[pics.length];

		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		points[currentIndex].setEnabled(false);
	}

	/**
	 * 滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	/**
	 * 当前页面滑动时调用
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/**
	 * 新的页面被选中时调用
	 */
	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
		if (position == 3) {
			mIvEnter.setVisibility(View.VISIBLE);
		} else {
			mIvEnter.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

	private void enterMainAcivity() {
		MySharePreData.SetBooleanData(this, MyComment.CHAT_SP_NAME, "is_frist",
				false);
		finish();
		new Thread(new Runnable() {
			public void run() {
				if (MyChatHXSDKHelper.getInstance().isLogined()) {
					HXSDKHelper.getInstance().getNotifier().isNotification();
					// 进入主页面
					startActivity(new Intent(NavigationActivity.this,
							MainActivity.class));
					finish();
				} else {
					startActivity(new Intent(NavigationActivity.this,
							LoginActivity.class));
					finish();
				}
			}
		}).start();
	}

	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}
		viewPager.setCurrentItem(position);
	}

	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}
		points[positon].setEnabled(false);
		points[currentIndex].setEnabled(true);

		currentIndex = positon;
	}
}
