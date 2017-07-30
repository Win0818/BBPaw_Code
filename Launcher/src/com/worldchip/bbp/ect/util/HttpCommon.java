package com.worldchip.bbp.ect.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class HttpCommon {

	public static final String UPDATE_SYSTEMUI = "cn.worldchip.www.UPDATE_SYSTEMUI";
	private static final String TAG = "--HttpCommon--";
	
	public static final String FINAL_PHOTO_TEMP_NAME = "imagename.png";
	public static final String ORIGINAL_PHOTO_TEMP_NAME = "imagenamebig.png";
	/**
	 * 闂归挓璁剧疆鏃堕棿鏁板瓧
	 */
	public static int imagesArr[] = { R.drawable.clock_alarm_number_0,
			R.drawable.clock_alarm_number_1, R.drawable.clock_alarm_number_2,
			R.drawable.clock_alarm_number_3, R.drawable.clock_alarm_number_4,
			R.drawable.clock_alarm_number_5, R.drawable.clock_alarm_number_6,
			R.drawable.clock_alarm_number_7, R.drawable.clock_alarm_number_8,
			R.drawable.clock_alarm_number_9 };

	/**
	 * 璁＄畻鍣ㄦ暟瀛�
	 */
	public static final String number[] = { "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "0", "11" };


	/**
	 * 鍒ゆ柇WIFI鏄惁鎵撳紑
	 * 
	 * @return
	 */
	public static boolean isWiFiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			// 鑾峰彇鐘舵�
			State wifi = connectivity.getNetworkInfo(
					ConnectivityManager.TYPE_WIFI).getState();
			// 鍒ゆ柇WIFI宸茶繛鎺ョ殑鏉′欢
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 鍥惧儚淇濆瓨鍒版枃浠朵腑
	 */
	public static void SavaImage(Bitmap bmp, String packName) {
		if (bmp == null) {
			return;
		}
		FileOutputStream foutput = null;
		Log.e("lee", "SavaImage == "+bmp);
		try {
			StringBuilder buffer = new StringBuilder();
			String targetDir = buffer.append(Environment.getDataDirectory())
					.append("/data/").append(packName).append("/imagedata/")
					.toString();
			File file = new File(targetDir);
			if (!file.exists()) {
				file.mkdir(); // 濡傛灉涓嶅瓨鍦ㄥ垯鍒涘缓
			}
			foutput = new FileOutputStream(file + "/" + ORIGINAL_PHOTO_TEMP_NAME);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, foutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != foutput) {
				try {
					foutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 鍥惧儚淇濆瓨鍒版枃浠朵腑
	 */
	public static void SavaFinalImage(Bitmap bmp, String packName) {
		FileOutputStream foutput = null;
		try {
			StringBuilder buffer = new StringBuilder();
			String targetDir = buffer.append(Environment.getDataDirectory())
					.append("/data/").append(packName).append("/imagedata/")
					.toString();
			File file = new File(targetDir);
			if (!file.exists()) {
				file.mkdir(); // 濡傛灉涓嶅瓨鍦ㄥ垯鍒涘缓
			}
			foutput = new FileOutputStream(file + "/" +FINAL_PHOTO_TEMP_NAME);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, foutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != foutput) {
				try {
					foutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 鑾峰彇鎸囧畾鐩綍涓嬬殑鍥剧墖
	 * 
	 * @param packName
	 * @return
	 */
	public static Bitmap getFinalImageBitmap(String packName) {
		StringBuilder buffer = new StringBuilder();
		String targetDir = buffer.append(Environment.getDataDirectory())
				.append("/data/").append(packName).append("/imagedata/")
				.toString();
		Bitmap imageBitmap = BitmapFactory.decodeFile(targetDir
				+ FINAL_PHOTO_TEMP_NAME);
		return imageBitmap;
	}
   public static String getPathImage(String packName){
	   StringBuilder buffer = new StringBuilder();
		String targetDir = buffer.append(Environment.getDataDirectory())
				.append("/data/").append(packName).append("/imagedata/")
				.toString();
		return targetDir;
   }
	/**
	 * 鑾峰彇鎸囧畾鐩綍涓嬬殑鍥剧墖
	 * 
	 * @param packName
	 * @return
	 */
	public static Bitmap getImageBitmap(String packName) {
		
		StringBuilder buffer = new StringBuilder();
		String targetDir = buffer.append(Environment.getDataDirectory()).append("/data/").append(packName).append("/imagedata/").toString();
		Log.e("lee", "getImageBitmap targetDir == "+targetDir);
		Bitmap imageBitmap = BitmapFactory.decodeFile(targetDir+ ORIGINAL_PHOTO_TEMP_NAME);
		Log.e("lee", "getImageBitmap imageBitmap == "+imageBitmap);
		Bitmap fianlBitmap = null;
		if (imageBitmap == null) {
			Resources resources = MyApplication.getAppContext().getResources();
			imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.app_default_photo);
		}
		//fianlBitmap = getCroppedRoundBitmap(imageBitmap, 50);
		return imageBitmap;
	}

	/**
	 * 鑾峰彇瑁佸壀鍚庣殑鍦嗗舰鍥剧墖
	 * @param radius
	 * 鍗婂緞
	 */
	public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius)
	{
		if (bmp == null) {
			return null;
		}
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 涓轰簡闃叉瀹介珮涓嶇浉绛夛紝閫犳垚鍦嗗舰鍥剧墖鍙樺舰锛屽洜姝ゆ埅鍙栭暱鏂瑰舰涓浜庝腑闂翠綅缃渶澶х殑姝ｆ柟褰㈠浘锟�?
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth)// 楂樺ぇ浜庡
		{
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			//y = (bmpHeight - bmpWidth) / 2;//浠庡浘鐗囨涓棿鎴彇
			// 鎴彇姝ｆ柟褰㈠浘锟�?
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,squareHeight);
		} else if (bmpHeight < bmpWidth) // 瀹藉ぇ浜庨珮
		{
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter)
		{
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,diameter, true);
		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap鍥炴敹(recycle瀵艰嚧鍦ㄥ竷锟�?鏂囦欢XML鐪嬩笉鍒版晥锟�?)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}
	
	/**
	 * 闂归挓鍒ゆ柇鏄惁鏄鏅�
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isClockNight(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		Calendar cal = Calendar.getInstance();// 褰撳墠鏃ユ湡
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 鑾峰彇灏忔椂
		int minute = cal.get(Calendar.MINUTE);// 鑾峰彇鍒嗛挓
		int minuteOfDay = hour * 60 + minute;// 浠�:00鍒嗗紑鏄埌鐩墠涓烘鐨勫垎閽熸暟
		final int end = 18 * 60;// 缁撴潫鏃堕棿 19:00鐨勫垎閽熸暟
		final int start = 6 * 60;// 璧峰鏃堕棿 7:00鐨勫垎閽熸暟
		if (strTimeFormat != null && strTimeFormat.equals("24")) {
			if (minuteOfDay < end && minuteOfDay > start) {
				return false;
			} else {
				return true;
			}
		} else {
			if (cal.get(Calendar.AM_PM) == 0) {
				if (minuteOfDay > start) {
					return false;
				} else {
					return true;
				}
			} else {
				if (minuteOfDay < start) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * 鍒ゆ柇鏄惁鏄鏅�
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNight(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		Calendar cal = Calendar.getInstance();// 褰撳墠鏃ユ湡
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 鑾峰彇灏忔椂
		int minute = cal.get(Calendar.MINUTE);// 鑾峰彇鍒嗛挓
		int minuteOfDay = hour * 60 + minute;// 浠�:00鍒嗗紑鏄埌鐩墠涓烘鐨勫垎閽熸暟
		final int end = 19 * 60;// 缁撴潫鏃堕棿 19:00鐨勫垎閽熸暟
		final int start = 7 * 60;// 璧峰鏃堕棿 7:00鐨勫垎閽熸暟
		if (strTimeFormat != null && strTimeFormat.equals("24")) {

			if (minuteOfDay < end && minuteOfDay > start) {
				return false;
			} else {

				return true;
			}
		} else {
			if (cal.get(Calendar.AM_PM) == 0) {
				if (minuteOfDay > start) {
					return false;
				} else {
					return true;
				}
			} else {
				if (minuteOfDay < start) {
					return false;
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * 闅愯棌鑿滃崟
	 * 
	 * @param context
	 * @param flag
	 */
	public static void hideSystemUI(Context context, boolean flag) {
		Intent intent = new Intent();
		intent.setAction(HttpCommon.UPDATE_SYSTEMUI);
		intent.putExtra("hide_systemui", flag);
		context.sendBroadcast(intent);
	}

	/**
	 * 闂归挓鏄熸湡杞崲
	 */
	public static int[] stringToInts(String weeks) {
		if (weeks == null || TextUtils.isEmpty(weeks)) {
			return null;
		}
		int[] n = new int[weeks.length()];
		for (int i = 0; i < weeks.length(); i++) {
			n[i] = Integer.parseInt(weeks.substring(i, i + 1));
		}
		return n;
	}

	/**
	 * 鍒ゆ柇鏄惁澶т簬褰撳墠鏃堕棿
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isThanTime(String time) {
		boolean flag = false;
		try {
			Date date = new Date();
			Log.e(TAG, date.toString());
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			String str = formatter.format(date);// 褰撳墠鏃堕棿
			Date alarmDate = formatter.parse(time);
			Date currentDate = formatter.parse(str);
			long alarmTime = alarmDate.getTime();// 闂归挓鏃堕棿鐨勬绉�
			long currentTime = currentDate.getTime();// 褰撳墠鏃堕棿鐨勬绉�
			Log.e(TAG, "alarmTime=" + alarmTime + " currentTime=" + currentTime);
			if (alarmTime > currentTime) {
				flag = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return flag;
	}

	
}