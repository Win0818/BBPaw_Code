package com.worldchip.bbp.bbpawmanager.cn.activity;


import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EyecareActivity extends Activity implements OnClickListener, OnCheckedChangeListener{

	private Fragment[] mFragments;
	private FragmentManager mFragmentManager;
	//当前显示的fragment
    private Fragment mCurrFragment;
    private RadioGroup mCleanTypeRadioGroup;
    private static final int MAX_FRAGMENT_COUNT = 2;
    private static final int SCREEN_TIME_PAGE_FRAGMENT_INDEX = 0;
    private static final int EYECARE_PAGE_FRAGMENT_INDEX = 1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eyecare_layout);
		
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mCleanTypeRadioGroup = (RadioGroup)findViewById(R.id.main_radio_group);
		mCleanTypeRadioGroup.setOnCheckedChangeListener(this);
		findViewById(R.id.back_btn).setOnClickListener(this);
		mFragments = new Fragment[MAX_FRAGMENT_COUNT]; 
		mFragmentManager = getFragmentManager();
		mFragments[SCREEN_TIME_PAGE_FRAGMENT_INDEX] = mFragmentManager.findFragmentById(R.id.screen_time_page_fragment);  
        mFragments[EYECARE_PAGE_FRAGMENT_INDEX] = mFragmentManager.findFragmentById(R.id.eyecare_page_fragment);
        ((RadioButton)findViewById(R.id.screen_time_btn)).setChecked(true);
	}

    //切换显示内容
    public void switchContent(Fragment to) {
            if (mCurrFragment != null && mCurrFragment != to) {
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    transaction.hide(mCurrFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
                    mCurrFragment = to;
            } else {
            	 FragmentTransaction transaction = mFragmentManager.beginTransaction();
                 transaction.show(to).commit();
                 mCurrFragment = to;
            }

    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
		case R.id.back_btn:
			Common.handleCreatEyecareAlarm(MyApplication.getAppContext());
			EyecareActivity.this.finish();
			break;
		
	    }  
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		if (checkedId == R.id.screen_time_btn) {
			switchContent(mFragments[SCREEN_TIME_PAGE_FRAGMENT_INDEX]);
		} else if (checkedId == R.id.eyecare_btn) {
			switchContent(mFragments[EYECARE_PAGE_FRAGMENT_INDEX]);
		}
	}


}
