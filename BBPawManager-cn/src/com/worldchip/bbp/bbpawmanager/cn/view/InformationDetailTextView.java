package com.worldchip.bbp.bbpawmanager.cn.view;

import com.worldchip.bbp.bbpawmanager.cn.utils.Common;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class InformationDetailTextView extends TextView {

	private Context mContext;
	public InformationDetailTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public InformationDetailTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	
	public void init(Context context) {
		mContext = context;
		setMovementMethod(LinkMovementMethod.getInstance());
		setLinksClickable(true);
	}
	
	
	
	
	public void setText(String text) {
		super.setText(text);
		CharSequence textStr = getText();
        if (textStr instanceof Spannable) {  
            int end = text.length();  
            Spannable sp = (Spannable)textStr;  
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);  
            SpannableStringBuilder style = new SpannableStringBuilder(text);  
            style.clearSpans();  
            for (URLSpan url : urls) {  
                InformContentURLSpan myURLSpan = new InformContentURLSpan(url.getURL());  
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
            }  
            setText(style); 
        }
	}
	
	private  class InformContentURLSpan extends ClickableSpan {  
		  
        private String mUrl;  
  
        InformContentURLSpan(String url) {  
            mUrl = url;  
        }  
  
        @Override  
        public void onClick(View widget) {  
        	Log.e("lee", " click links ..... url == "+mUrl);
        	Common.openBBPawBrowser(mContext, mUrl);
        }  
    }  
}
