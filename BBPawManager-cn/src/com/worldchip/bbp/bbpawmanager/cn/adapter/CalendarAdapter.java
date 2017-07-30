package com.worldchip.bbp.bbpawmanager.cn.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.utils.SpecialCalendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 日历gridview中的每一个item显示的textview
 * 
 * @author  Lee
 * 
 */
public class CalendarAdapter extends BaseAdapter {
	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	private Context context;
	private String[] dayNumber = new String[42]; // 一个gridview中的日期存入此数组中
	private SpecialCalendar sc = null;
	//private Drawable drawable = null;

	private String currentYear = "";
	private String currentMonth = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1; // 用于标记当天

	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	// 系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private int mSelectionPosition = -1;
	
	public CalendarAdapter() {
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	/**
	 * 
	 * @param context
	 * @param rs
	 * @param jumpMonth 跳转月数
	 * @param jumpYear 跳转年数
	 * @param year_c 目前年份
	 * @param month_c 目前月份
	 * @param day_c 目前天
	 */
	public CalendarAdapter(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c, int day_c) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = stepYear + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
		}
		currentYear = String.valueOf(stepYear); // 得到当前的年份
		currentMonth = String.valueOf(stepMonth); // 得到本月（jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));

	}

	public CalendarAdapter(Context context, Resources rs, int year, int month, int day) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		currentYear = String.valueOf(year);// 得到跳转到的年份
		currentMonth = String.valueOf(month); // 得到跳转到的月份
		getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		String dayNum = dayNumber[position];
		textView.setText(dayNum);
		textView.setTextColor(Color.GRAY);//非当月日期字体颜色
		
		//设置当月字体
		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			textView.setTextColor(Color.BLACK);
			if (position % 7 == 0 || position % 7 == 6) {
				textView.setTextColor(Color.RED);
			}
		}

		//设置选中日期背景
		ColorDrawable selectBg = null;
		if (currentFlag == position && mSelectionPosition == -1) {
			// 设置当天的背景
			selectBg = new ColorDrawable(Color.rgb(23, 126, 214));
			textView.setTextColor(Color.WHITE);
			mSelectionPosition = currentFlag;
		} else if (mSelectionPosition == position && isCurrMount(position)) {
			selectBg = new ColorDrawable(Color.rgb(23, 126, 214));
		}
		textView.setBackgroundDrawable(selectBg);
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		getweek(year, month);
	}

	// 将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		int j = 1;
		// 得到当前月的所有日程日期(这些日期需要标记)
		for (int i = 0; i < dayNumber.length; i++) {
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				dayNumber[i] = String.valueOf((temp + i));

			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				String day = String.valueOf(i - dayOfWeek + 1); // 得到的日期
				dayNumber[i] = String.valueOf(i - dayOfWeek + 1);
				// 对于当前月才去标记当前日期
				if (sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)) {
					// 标记当前日期
					currentFlag = i;
				}
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
			} else { // 下一个月
				dayNumber[i] = String.valueOf(j);
				j++;
			}
		}
	}

	/**
	 * 点击每一个item时返回item中的日期
	 * 
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position) {
		return dayNumber[position];
	}

	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * 
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * 
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public void setSelection(int position) {
		mSelectionPosition = position;
	}
	
	public String getCurrSelectionDate() {
		String date = "";
		if (mSelectionPosition != -1) {
			if (isCurrMount(mSelectionPosition)) {
				String scheduleDay = getDateByClickItem(mSelectionPosition);
				String scheduleYear = getShowYear();
				String scheduleMonth = getShowMonth();
				return scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
			}
		}
		return date;
	}
	
	public boolean isCurrMount(int position) {
		int startPosition = getStartPositon();
		int endPosition = getEndPosition();
		if (position >= 0) {
			if (startPosition <= position + 7
					&& position <= endPosition - 7) {
				return true;
			}
		}
		return false;
	}
	
}
