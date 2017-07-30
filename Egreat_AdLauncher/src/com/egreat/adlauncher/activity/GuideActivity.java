package com.egreat.adlauncher.activity;

import java.util.ArrayList;
import java.util.List;

import com.egreat.adlauncher.adapter.MarqueeAdapter;
import com.egreat.adlauncher.effect.ABaseTransformer;
import com.egreat.adlauncher.effect.AccordionTransformer;
import com.egreat.adlauncher.effect.BackgroundToForegroundTransformer;
import com.egreat.adlauncher.effect.CubeInTransformer;
import com.egreat.adlauncher.effect.CubeOutTransformer;
import com.egreat.adlauncher.effect.DefaultTransformer;
import com.egreat.adlauncher.effect.DepthPageTransformer;
import com.egreat.adlauncher.effect.FlipHorizontalTransformer;
import com.egreat.adlauncher.effect.FlipVerticalTransformer;
import com.egreat.adlauncher.effect.ForegroundToBackgroundTransformer;
import com.egreat.adlauncher.effect.RotateDownTransformer;
import com.egreat.adlauncher.effect.RotateUpTransformer;
import com.egreat.adlauncher.effect.StackTransformer;
import com.egreat.adlauncher.effect.TabletTransformer;
import com.egreat.adlauncher.effect.ViewPagerScroller;
import com.egreat.adlauncher.effect.ZoomInTransformer;
import com.egreat.adlauncher.effect.ZoomOutSlideTransformer;
import com.egreat.adlauncher.effect.ZoomOutTranformer;
import com.egreat.adlauncher.util.Utils;
import com.mgle.launcher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GuideActivity extends Activity {

	private static final String TAG = "--GuideActivity--";
	private Context mCtx;
	private int mIndex;
	private ViewPager mViewPager;
	private ABaseTransformer mTransformer = null;
	private ViewPagerScroller mScroller = null;

	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCtx = GuideActivity.this;

		mPrefs = this.getSharedPreferences("egreat_setup", 0);

		boolean showGuide = mPrefs.getBoolean("show_guide", true);
		if (!showGuide) {
			startMainActivity();
		} else {
			setContentView(R.layout.guide_layout);
			mIndex = 0;
			mViewPager = (ViewPager) findViewById(R.id.view_pager);
			mScroller = new ViewPagerScroller(mCtx);
			setScrollerAnimation();

			initView();
		}
	}

	private void initView() {

		ImageView imgView = null;
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		for (int i = 0; i < Utils.GUIDE_DATA.length; i++) {
			imgView = new ImageView(mCtx);
			imgView.setScaleType(ScaleType.FIT_XY);
			imgView.setBackgroundResource(Utils.GUIDE_DATA[i]);
			views.add(imgView);
		}

		
		MarqueeAdapter marquee = new MarqueeAdapter();
		marquee.setData(views);
		mScroller.initViewPagerScroll(mViewPager);
		mScroller.setScrollDuration(1000);
		mViewPager.setAdapter(marquee);
		mViewPager.setCurrentItem(mIndex);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.e(TAG,"dispatchKeyEvent...event.getKeyCode()...=" + event.getKeyCode());
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getRepeatCount() == 0) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				mIndex++;
				if (mIndex >= Utils.GUIDE_DATA.length) {
					Log.e(TAG, "will start main acitvity...");
					startMainActivity();
					return super.dispatchKeyEvent(event);
				}
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				mIndex--;
				if (mIndex <= 0) {
					mIndex = 0;
					return super.dispatchKeyEvent(event);
				}
			}
			
			mViewPager.setCurrentItem(mIndex);
			mViewPager.setPageTransformer(true, mTransformer);
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "onKeyDown...keyCode=" + keyCode);

		return super.onKeyDown(keyCode, event);
	}

	private void startMainActivity() {
		Intent intent = new Intent(mCtx, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

	public void setScrollerAnimation() {

		int index = 3;
		// Ëæ»ú0~13
		index = (int) Math.round(Math.random() * 15);
		Log.e(TAG, "index=" + index);
		switch (index) {
		case 0:
			mTransformer = new AccordionTransformer();
			break;
		case 1:
			mTransformer = new BackgroundToForegroundTransformer();
			break;
		case 2:
			mTransformer = new CubeInTransformer();
			break;
		case 3:
			mTransformer = new CubeOutTransformer();
			break;
		case 4:
			mTransformer = new DepthPageTransformer();
			break;
		case 5:
			mTransformer = new FlipHorizontalTransformer();
			break;
		case 6:
			mTransformer = new FlipVerticalTransformer();
			break;
		case 7:
			mTransformer = new ForegroundToBackgroundTransformer();
			break;
		case 8:
			mTransformer = new RotateDownTransformer();
			break;
		case 9:
			mTransformer = new RotateUpTransformer();
			break;
		case 10:
			mTransformer = new StackTransformer();
			break;
		case 11:
			mTransformer = new TabletTransformer();
			break;
		case 12:
			mTransformer = new ZoomInTransformer();
			break;
		case 13:
			mTransformer = new ZoomOutSlideTransformer();
			break;
		case 14:
			mTransformer = new ZoomOutTranformer();
			break;
		default:
			mTransformer = new CubeInTransformer();
			break;
		}

	}
}
