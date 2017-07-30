package com.worldchip.bbp.bbpawmanager.cn.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.adapter.CalendarAdapter;
import com.worldchip.bbp.bbpawmanager.cn.adapter.VaccineAdapter;
import com.worldchip.bbp.bbpawmanager.cn.adapter.VaccineAdapter.OnAllCheckedListener;
import com.worldchip.bbp.bbpawmanager.cn.model.VaccineInfo;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.view.CalendarDialog;
import com.worldchip.bbp.bbpawmanager.cn.view.GlobalAlertDialog;
import com.worldchip.bbp.bbpawmanager.cn.view.GlobalProgressDialog;
import com.worldchip.bbp.bbpawmanager.cn.view.MyTosat;
import com.worldchip.bbp.bbpawmanager.cn.view.VaccineAlarmDialog;

public class VaccineActivity extends Activity implements OnClickListener,OnAllCheckedListener{

	private ListView mVaccineListView;
	private GlobalProgressDialog mGlobalProgressDialog;
	private EditText mCalendarEdit;
	private GestureDetector mGestureDetector = null;
	private CalendarAdapter mCalendarAdapter = null;
	private ViewFlipper mFlipper = null;
	private GridView mGridView = null;
	private static int mJumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int mJumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	/** 每次添加gridview到viewflipper中时给的标记 */
	private int mFlag = 0;
	/** 当前的年月，现在日历顶端 */
	private TextView mCurrentMonth;
	/** 上个月 */
	private ImageView mPreMonth;
	/** 下个月 */
	private ImageView mNextMonth;
	/** 上一年 */
	private ImageView mPreYear;
	/** 下一年 */
	private ImageView mNextYear;
	
	private CalendarDialog mCalendarDialog = null;
	private VaccineAdapter mVaccineAdapter = null;
	private TextView mCalculateBtn;
	private TextView mCheckBoxTitle;
	private boolean mReCalculate = false;
	private LinearLayout mAllCheckedLL = null;
	private CheckBox mAllCheckBox = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccine_layout);
		initView();
		LoadDatas();
	}

	@SuppressLint("SimpleDateFormat")
	private void initView() {
		findViewById(R.id.vaccine_back_btn).setOnClickListener(this);
		mCalculateBtn = (TextView)findViewById(R.id.calculate_btn);
		mCalculateBtn.setOnClickListener(this);
		mCalendarEdit = (EditText)findViewById(R.id.calendar_edit);
		mCalendarEdit.setOnClickListener(this);
		mCheckBoxTitle = (TextView)findViewById(R.id.vaccine_checkbox_title);
		mAllCheckedLL = (LinearLayout)findViewById(R.id.all_checked_ll);
		mAllCheckBox = (CheckBox)findViewById(R.id.all_check_btn);
		mAllCheckBox.setOnClickListener(this);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		String currentDate = sdf.format(date); // 当期日期
		mYear = Integer.parseInt(currentDate.split("-")[0]);
		mMonth = Integer.parseInt(currentDate.split("-")[1]);
		mDay = Integer.parseInt(currentDate.split("-")[2]);
		
		mVaccineListView = (ListView) findViewById(R.id.vaccine_listview);
		mVaccineListView.setFastScrollEnabled(false);
		mVaccineAdapter = new VaccineAdapter(VaccineActivity.this, null);
		mVaccineAdapter.setOnAllCheckListener(this);
		mVaccineListView.setAdapter(mVaccineAdapter);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String language = Locale.getDefault().getLanguage();
			if (!language.contains("zh")) {
				finish();
			} 
	}

	private void initCalendar(View parentView) {
		mCurrentMonth = (TextView) parentView.findViewById(R.id.currentMonth);
		mPreMonth = (ImageView) parentView.findViewById(R.id.preMonth);
		mNextMonth = (ImageView) parentView.findViewById(R.id.nextMonth);
		mPreYear = (ImageView) parentView.findViewById(R.id.preYear);
		mNextYear = (ImageView) parentView.findViewById(R.id.nextYear);
		mPreMonth.setOnClickListener(this);
		mNextMonth.setOnClickListener(this);
		mPreYear.setOnClickListener(this);
		mNextYear.setOnClickListener(this);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		mFlipper = (ViewFlipper) parentView.findViewById(R.id.flipper);
		mFlipper.removeAllViews();
		mCalendarAdapter = new CalendarAdapter(this, getResources(), mJumpMonth, mJumpYear, mYear, mMonth, mDay);
		addGridView();
		mGridView.setAdapter(mCalendarAdapter);
		mFlipper.addView(mGridView, 0);
		updateCalendarTitle(mCurrentMonth);
	}
	
	private void LoadDatas() {
		// TODO Auto-generated method stub
		new AsyncTask<Object, Object, List<VaccineInfo>>() {
            @Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				startProgressDialog();
			}
			@Override
			protected void onCancelled() {
				// TODO Auto-generated method stub
				super.onCancelled();
				stopProgressDialog();
			}
			
			@Override
            protected List<VaccineInfo> doInBackground(final Object... params) {
				
				return Common.getVaccineDatas(MyApplication.getAppContext());
            }
            @Override
            protected void onPostExecute(List<VaccineInfo> list) {
            	stopProgressDialog();
        		if (mVaccineAdapter != null) {
        			mVaccineAdapter.setDataList(list);
        			mVaccineAdapter.notifyDataSetChanged();
        		}
            	
            }
        }.execute((Void)null);
	}
	
	
	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			mGlobalProgressDialog.dismiss();
			mGlobalProgressDialog = null;
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.calendar_edit:
			showCalendar(view);
			break;
		case R.id.nextYear: // 下一年
			getNextYear(mFlag);
			break;
		case R.id.nextMonth: // 下一个月
			getNextMonth(mFlag);
			break;
		case R.id.preMonth: // 上一个月
			getPrevMonth(mFlag);
			break;
		case R.id.preYear: // 下一年
			getPrevYear(mFlag);
			break;
		case R.id.calculate_btn:
			if (!mReCalculate) {
				calculateVaccine();
			} else {
				if (mCalendarEdit != null) {
					mCalendarEdit.setText("");
				}
				reCalculateVaccine();
			}
			updateCalculateBtnText(mReCalculate);
			break;
			
		case R.id.vaccine_back_btn:
			saveDatas();
			return;
		case R.id.all_check_btn:
			updateAllChecked(((CheckBox)view).isChecked());
			return;
		default:
			break;
		}
	}
	
	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int mFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				getNextMonth(mFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				getPrevMonth(mFlag);
				return true;
			}
			return false;
		}
	}
	/**
	 * 移动到下一个月
	 * 
	 * @param mFlag
	 */
	private void getNextMonth(int mFlag) {
		addGridView(); // 添加一个gridView
		mJumpMonth++; // 下一个月
		mCalendarAdapter = new CalendarAdapter(this, this.getResources(), mJumpMonth, mJumpYear, mYear, mMonth, mDay);
		mGridView.setAdapter(mCalendarAdapter);
		updateCalendarTitle(mCurrentMonth); // 移动到下一月后，将当月显示在头标题中
		mFlag++;
		mFlipper.addView(mGridView, mFlag);
		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		mFlipper.showNext();
		mFlipper.removeViewAt(0);
	}

	/**
	 * 移动到上一个月
	 * 
	 * @param mFlag
	 */
	private void getPrevMonth(int mFlag) {
		addGridView(); // 添加一个gridView
		mJumpMonth--; // 上一个月

		mCalendarAdapter = new CalendarAdapter(this, this.getResources(), mJumpMonth, mJumpYear, mYear, mMonth, mDay);
		mGridView.setAdapter(mCalendarAdapter);
		mFlag++;
		updateCalendarTitle(mCurrentMonth); // 移动到上一月后，将当月显示在头标题中
		mFlipper.addView(mGridView, mFlag);
		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		mFlipper.showPrevious();
		mFlipper.removeViewAt(0);
	}

	
	/**
	 * 移动到下一年
	 * 
	 * @param mFlag
	 */
	private void getNextYear(int mFlag) {
		addGridView(); // 添加一个gridView
		mJumpYear++; // 下一年
		mCalendarAdapter = new CalendarAdapter(this, this.getResources(), mJumpMonth, mJumpYear, mYear, mMonth, mDay);
		mGridView.setAdapter(mCalendarAdapter);
		updateCalendarTitle(mCurrentMonth);
		mFlag++;
		mFlipper.addView(mGridView, mFlag);
		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		mFlipper.showNext();
		mFlipper.removeViewAt(0);
	}

	/**
	 * 移动到上一年
	 * 
	 * @param mFlag
	 */
	private void getPrevYear(int mFlag) {
		addGridView(); // 添加一个gridView
		mJumpYear--; // 上一个月

		mCalendarAdapter = new CalendarAdapter(this, this.getResources(), mJumpMonth, mJumpYear, mYear, mMonth, mDay);
		mGridView.setAdapter(mCalendarAdapter);
		mFlag++;
		updateCalendarTitle(mCurrentMonth);
		mFlipper.addView(mGridView, mFlag);
		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		mFlipper.showPrevious();
		mFlipper.removeViewAt(0);
	}
	
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mGridView = new GridView(this);
		mGridView.setNumColumns(7);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return VaccineActivity.this.mGestureDetector.onTouchEvent(event);
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (mCalendarAdapter != null && mCalendarAdapter.isCurrMount(position)) {
					mCalendarAdapter.setSelection(position);
					mCalendarAdapter.notifyDataSetChanged();
				}
			}
		});
		mGridView.setLayoutParams(params);
	}
	
	private void showCalendar(View anchorView) {
		// TODO Auto-generated method stub
		View calendarView = LayoutInflater.from(this).inflate(R.layout.calendar, null);
		if (mCalendarDialog != null) {
    		if (mCalendarDialog.isShowing()) {
    			mCalendarDialog.dismiss();
    		}
    		mCalendarDialog = null;
    	}
		View calendarOkBtn = calendarView.findViewById(R.id.ok_btn);
		View calendarCancelBtn = calendarView.findViewById(R.id.cancel_btn);
		if (calendarOkBtn != null) {
			calendarOkBtn.setOnClickListener(new CalendarButtonOnclickListener());
		}
		if (calendarCancelBtn != null) {
			calendarCancelBtn.setOnClickListener(new CalendarButtonOnclickListener());
		}
		if (calendarView != null) {
			initCalendar(calendarView);
			mCalendarDialog = new CalendarDialog.Builder(this)
			.setContentView(calendarView)
			.setAnchorView(anchorView)
			.setViewWidth(350)
			.setCancelable(true).create();
			mCalendarDialog.setCanceledOnTouchOutside(false);
			mCalendarDialog.show();
		}
	}

	private class CalendarButtonOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.ok_btn:
				if (mCalendarEdit != null) {
					updateCanlendarEditView();
					mReCalculate = false;
					updateCalculateBtnText(mReCalculate);
					reCalculateVaccine();
				}
				break;
			case R.id.cancel_btn:
				
				break;
			}
			if (mCalendarDialog != null) {
				mCalendarDialog.dismiss();
			}
		}
	}
	
	public void updateCalendarTitle(TextView view) {
		if (mCalendarAdapter != null) {
			String year = mCalendarAdapter.getShowYear();
			String month = mCalendarAdapter.getShowMonth();
			if (view != null) {
				view.setText(getResources().getString(R.string.calendar_top_format_text, year, month));
			}
		}
	}
	
	private void updateCanlendarEditView() {
		if (mCalendarAdapter != null && mCalendarEdit != null) {
			String date = mCalendarAdapter.getCurrSelectionDate();
			mCalendarEdit.setText(date);
		}
	}
	
	private void calculateVaccine() {
		if (mCalendarEdit != null) {
			String date = mCalendarEdit.getText().toString();
			if (!TextUtils.isEmpty(date)) {
				if (mCheckBoxTitle != null) {
					mCheckBoxTitle.setVisibility(View.VISIBLE);
				}
				if (mAllCheckedLL != null) {
					mAllCheckedLL.setVisibility(View.VISIBLE);
				}
				if (mVaccineAdapter != null) {
					List<VaccineInfo> dataList = mVaccineAdapter.getDataList();
					mVaccineAdapter.setVaccineEditMode(true);
					Common.calculateVaccineDates(dataList, date);
					mVaccineAdapter.notifyDataSetChanged();
					mReCalculate = true;
				}
			}
		}
	}
	
	private void reCalculateVaccine() {
		if (mCheckBoxTitle != null) {
			mCheckBoxTitle.setVisibility(View.INVISIBLE);
		}
		if (mAllCheckedLL != null) {
			mAllCheckedLL.setVisibility(View.INVISIBLE);
		}
		if (mVaccineAdapter != null) {
			List<VaccineInfo> dataList = mVaccineAdapter.getDataList();
			Common.resetVaccineDates(dataList);
			mVaccineAdapter.setVaccineEditMode(false);
			mVaccineAdapter.notifyDataSetChanged();
			mReCalculate = false;
		}
		
	}
	
	private void updateCalculateBtnText(boolean reCalculate) {
		if (mCalculateBtn != null) {
			if (reCalculate) {
				mCalculateBtn.setText(getString(R.string.vaccine_recalculate_btn_text));
			} else {
				mCalculateBtn.setText(getString(R.string.vaccine_calculate_btn_text));
			}
		}
	}
	
	private void updateAllChecked(boolean isChecked) {
		if (mAllCheckBox != null) {
			mAllCheckBox.setChecked(isChecked);
		}
		changeAllVaccine(isChecked);
	}
	
	private void changeAllVaccine(boolean checkAll) {
		if (mVaccineAdapter != null) {
			List<VaccineInfo> dataList = mVaccineAdapter.getDataList();
			for (int i=0; i<dataList.size(); i++) {
				dataList.get(i).setChecked(checkAll);
			}
			mVaccineAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void OnAllChecked(boolean allChecked) {
		// TODO Auto-generated method stub
		if (mAllCheckBox != null) {
			mAllCheckBox.setChecked(allChecked);
		}
	}
	
	private void saveDatas() {
		//VaccineActivity.this.finish();
		if (mVaccineAdapter != null && mVaccineAdapter.hasChecked()) {
			if(!Common.vaccineSettingsAlreadyExists()) {
				boolean flag = Common.saveVaccineSettings(mVaccineAdapter.getDataList());
				if (flag) {
					MyTosat.makeText(MyApplication.getAppContext(), R.drawable.ic_settings_succes, Toast.LENGTH_SHORT).show();
				}
				VaccineActivity.this.finish();
			} else {
				showVaccineAlert();
			}
		} else {
			VaccineActivity.this.finish();
		}
	}
	
	private void showVaccineAlert() {
		GlobalAlertDialog vaccineAlert = new GlobalAlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.vaccine_alert_title))
		.setMessage(getResources().getString(R.string.vaccine_alert_content))
		.setCancelable(true)
		.setNegativeButton("", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
			}
		})
		.setPositiveButton("",  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Log.e("lee", "click ok----");
				if (mVaccineAdapter != null) {
					boolean resetVaccineSettings = Common.resetVaccineSettings(mVaccineAdapter.getDataList());
					if (resetVaccineSettings) {
						MyTosat.makeText(MyApplication.getAppContext(), R.drawable.ic_settings_succes, Toast.LENGTH_SHORT).show();
						VaccineActivity.this.finish();
					}
				}
			}
		})
		.create(R.layout.vaccine_alert_dialog);
		vaccineAlert.show();
	}
}
