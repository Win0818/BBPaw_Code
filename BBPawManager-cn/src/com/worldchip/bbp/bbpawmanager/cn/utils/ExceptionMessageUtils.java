package com.worldchip.bbp.bbpawmanager.cn.utils;

import org.apache.http.conn.ConnectTimeoutException;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.exception.NoMemoryException;

import android.accounts.NetworkErrorException;
import android.content.Context;

public class ExceptionMessageUtils {

	
	public static String getExceptionMessage(Context context, Throwable throwable) {
		String exceptionMessage ="";
		if (throwable != null) {
			if (throwable instanceof NetworkErrorException) {
				exceptionMessage = context.getString(R.string.network_error_msg);
			} else if (throwable instanceof NoMemoryException) {
				exceptionMessage = context.getString(R.string.no_memory_error_msg);
			} else if(throwable instanceof ConnectTimeoutException) {
				exceptionMessage = context.getString(R.string.network_error_msg);
			}
		}
		return exceptionMessage;
	}
}
