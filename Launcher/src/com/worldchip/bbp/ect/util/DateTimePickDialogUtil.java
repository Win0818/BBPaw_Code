package com.worldchip.bbp.ect.util;
  
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date;

import com.worldchip.bbp.ect.R;
 
import android.annotation.SuppressLint;
import android.app.Activity;  
import android.app.AlertDialog;  
import android.content.DialogInterface;  
import android.text.TextUtils;
import android.widget.DatePicker;  
import android.widget.DatePicker.OnDateChangedListener;  
import android.widget.EditText;  
import android.widget.LinearLayout;  
import android.widget.TimePicker;  
import android.widget.TimePicker.OnTimeChangedListener;  

/** 
17. * ����ʱ��ѡ��ؼ� ʹ�÷����� private EditText inputDate;//��Ҫ���õ�����ʱ���ı��༭�� private String 
18. * initDateTime="2012��9��3�� 14:44",//��ʼ����ʱ��ֵ �ڵ���¼���ʹ�ã� 
19. * inputDate.setOnClickListener(new OnClickListener() { 
20. *  
21. * @Override public void onClick(View v) { DateTimePickDialogUtil 
22. *           dateTimePicKDialog=new 
23. *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime); 
24. *           dateTimePicKDialog.dateTimePicKDialog(inputDate); 
25. *  
26. *           } }); 
27. *  
28. * @author 
29. */  
public class DateTimePickDialogUtil implements OnDateChangedListener {  
   private DatePicker datePicker;  
    private AlertDialog ad;  
    private String dateTime;  
    private String initDateTime;  
    private Activity activity;  
    
    /** 
40.     * ����ʱ�䵯��ѡ����캯�� 
41.     *  
42.     * @param activity 
43.     *            �����õĸ�activity 
44.     * @param initDateTime 
45.     *            ��ʼ����ʱ��ֵ����Ϊ�������ڵı��������ʱ���ʼֵ 
46.     */  
    public DateTimePickDialogUtil(Activity activity, String initDateTime) {  
        this.activity = activity;  
        this.initDateTime = initDateTime;  
  
    }  
  
    public void init(DatePicker datePicker) {  
        Calendar calendar = Calendar.getInstance();  
        if (!(null == initDateTime || "".equals(initDateTime))) {  
            calendar = this.getCalendarByInintData(initDateTime);  
        } else {  
            initDateTime = calendar.get(Calendar.YEAR) + "��"  
                    + calendar.get(Calendar.MONTH) + "��"  
                    + calendar.get(Calendar.DAY_OF_MONTH) + "�� ";  
        }  
  
        datePicker.init(calendar.get(Calendar.YEAR),  
                calendar.get(Calendar.MONTH),  
                calendar.get(Calendar.DAY_OF_MONTH), this);  
    
    }  
  
    /** 
73.     * ��������ʱ��ѡ��򷽷� 
74.     *  
75.     * @param inputDate 
76.     *            :Ϊ��Ҫ���õ�����ʱ���ı��༭�� 
77.     * @return 
78.     */  
    public AlertDialog dateTimePicKDialog(final EditText inputDate) {  
        LinearLayout dateTimeLayout = (LinearLayout) activity  
                .getLayoutInflater().inflate(R.layout.common_datetime, null);  
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);  
        init(datePicker);  
  
        ad = new AlertDialog.Builder(activity).setTitle(initDateTime)  
                .setView(dateTimeLayout)  
                .setPositiveButton("����", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        inputDate.setText(dateTime);  
                    }  
                })  
                .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                       inputDate.setText("");  
                    }  
                }).show();  
  
        onDateChanged(null, 0, 0, 0);  
        return ad;  
    }  
  
     
  
    public void onDateChanged(DatePicker view, int year, int monthOfYear,  
            int dayOfMonth) {  
        // �������ʵ��  
        Calendar calendar = Calendar.getInstance();  
  
        calendar.set(datePicker.getYear(), datePicker.getMonth(),  
                datePicker.getDayOfMonth());  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� ");  
  
        dateTime = sdf.format(calendar.getTime());  
        ad.setTitle(dateTime);  
    }  
  
    /** 
125.     * ʵ�ֽ���ʼ����ʱ��2012��07��02�� 16:45 ��ֳ��� �� �� ʱ �� ��,����ֵ��calendar 
126.     *  
127.     * @param initDateTime 
128.     *            ��ʼ����ʱ��ֵ �ַ��� 
129.     * @return Calendar 
130.     */  
    public  Calendar getCalendarByInintData(String initDateTime) {  
        Calendar calendar = Calendar.getInstance();  
  
        // ����ʼ����ʱ��2012��07��02�� 16:45 ��ֳ��� �� �� ʱ �� ��  
        String date = spliteString(initDateTime, "��", "index", "front"); // ����  
  
       String yearStr = spliteString(date, "��", "index", "front"); // ���  
        String monthAndDay = spliteString(date, "��", "index", "back"); // ����  
  
        String monthStr = spliteString(monthAndDay, "��", "index", "front"); // ��  
        String dayStr = spliteString(monthAndDay, "��", "index", "back"); // ��  
  
  
        int currentYear = Integer.valueOf(yearStr.trim()).intValue();  
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;  
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();  
  
        calendar.set(currentYear, currentMonth, currentDay);  
        return calendar;  
    }  
    public static String getDateToString(long time) {
        Date d = new Date(time);
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd��");
        return sdf.format(d);
    }
    
    @SuppressLint("SimpleDateFormat")
	public static String getDateToString(String timeStamp, String format) {
		if (TextUtils.isEmpty(timeStamp)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(timeStamp)));
	}
    /** 
159.     * ��ȡ�Ӵ� 
160.     *  
161.     * @param srcStr 
162.     *            Դ�� 
163.     * @param pattern 
164.     *            ƥ��ģʽ 
165.     * @param indexOrLast 
166.     * @param frontOrBack 
167.     * @return 
168.     */  
    public static String spliteString(String srcStr, String pattern,  
            String indexOrLast, String frontOrBack) {  
        String result = "";  
        int loc = -1;  
        if (indexOrLast.equalsIgnoreCase("index")) {  
            loc = srcStr.indexOf(pattern); // ȡ���ַ��һ�γ��ֵ�λ��  
        } else {  
            loc = srcStr.lastIndexOf(pattern); // ���һ��ƥ�䴮��λ��  
       }  
        if (frontOrBack.equalsIgnoreCase("front")) {  
            if (loc != -1)  
                result = srcStr.substring(0, loc); // ��ȡ�Ӵ�  
        } else {  
            if (loc != -1)  
                result = srcStr.substring(loc + 1, srcStr.length()); // ��ȡ�Ӵ�  
        }  
        return result;  
    }  
  
}  

