package com.worldchip.bbpawphonechat.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;

public class CommonUtils {
	private static final String TAG = "CHRIS";

	/**
	 * 检测Sdcard是否存在
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	
	/**
     * 根据消息内容和消息类型获取消息内容提示
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct == EMMessage.Direct.RECEIVE) {
                //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                digest = getString(context, R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                digest = getString(context, R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息
            digest = getString(context, R.string.picture);
            break;
        case VOICE:// 语音消息
            digest = getString(context, R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = getString(context, R.string.video);
            break;
        case TXT: // 文本消息
            if(!message.getBooleanAttribute(MyComment.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            }else{
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = getString(context, R.string.voice_call) + txtBody.getMessage();
            }
            break;
        case FILE: //普通文件消息
            digest = getString(context, R.string.file);
            break;
        default:
            System.err.println("error, unknow type");
            return "";
        }

        return digest;
    }
    
    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }
	
	
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	public static String getFilePathFromUri(Uri uri) {
		String fileName = null;
		if (uri != null){
			if (uri.getScheme().toString().compareTo("content") == 0) {
				String[] columns = { Media.DATA };
				String whereStr = Media._ID
						+ "="
						+ uri.toString().substring(
								uri.toString().lastIndexOf("/") + 1);
				Log.d(TAG, "getFilePathFromUri...uri=" + uri + "; whereStr="
						+ whereStr);
				Cursor cursor = MyApplication.applicationContext.getContentResolver().query(
						Media.EXTERNAL_CONTENT_URI, columns, whereStr, null,
						null);
				if (cursor != null && cursor.moveToFirst()) {
					fileName = cursor.getString(cursor
							.getColumnIndexOrThrow(Media.DATA));
					cursor.close();
				}
			}
		} else if (uri.getScheme().compareTo("file") == 0) {
			fileName = uri.toString().replace("file://", "");
			if (!fileName.startsWith("/mnt")) {
				fileName += "/mnt";
			}
		}
		return fileName;
	}

	public static Bitmap getSimpleBitmipFromUri(String uri, View view) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(uri, options);
		options.inSampleSize = calculateInSampleSize(options, view.getWidth(),
				view.getHeight());
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(uri, options);
		return bitmap;
	}

	
	// calucate size
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}  
	
	
	public  static boolean isMobileNo(String mobiles) {
		Pattern p = Pattern.compile("^[1][0-9]{10}$");
		Matcher m = p.matcher(mobiles);
		Log.i(TAG, "-----isMobileNo--------" + m.matches());
		boolean isMobiles = m.matches();
		return isMobiles;
	}
	
	
	public static boolean isMailAddress(String mailAddress){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
				"(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = pattern.matcher(mailAddress);
		Log.i(TAG, "-----isMailAddress--------" + m.matches());
		return m.matches();
	}
	
	
	public  static String getVersionName(Context context) throws Exception{
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
   }

}
