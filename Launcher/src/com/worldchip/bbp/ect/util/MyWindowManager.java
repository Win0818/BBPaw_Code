package com.worldchip.bbp.ect.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.worldchip.bbp.ect.view.FloatWindowSmallView;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MyWindowManager {

	private static FloatWindowSmallView smallWindow;


	private static LayoutParams smallWindowParams;


	public static void createSmallWindow(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); 
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (smallWindow == null) {
			smallWindow = new FloatWindowSmallView(context);
			if (smallWindowParams == null) {
				smallWindowParams = new LayoutParams();
				smallWindowParams.type = LayoutParams.TYPE_PHONE;
				smallWindowParams.format = PixelFormat.RGBA_8888;
				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				smallWindowParams.width = FloatWindowSmallView.mviewWidth;
				smallWindowParams.height = FloatWindowSmallView.mviewHeight;
				smallWindowParams.x = screenWidth;
				smallWindowParams.y = screenHeight / 2;
			}
			smallWindow.setParams(smallWindowParams);
			
			windowManager.addView(smallWindow, smallWindowParams);
		}
	}

	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); 
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	
	public static boolean isSmallWindowShowing() {
		return smallWindow != null;
	}
}
