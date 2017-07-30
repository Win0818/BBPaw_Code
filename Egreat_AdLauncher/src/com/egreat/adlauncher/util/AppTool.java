package com.egreat.adlauncher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.egreat.adlauncher.db.ApkInfo;
import com.mgle.launcher.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

public class AppTool {
	private static final String TAG = "AppTool";

	/**
	 * 获取所有应用，并过滤掉不需要显示的应用
	 * 
	 * @return allApp 所有应用集合
	 */
	public static List<ResolveInfo> getAllApps(Context mContext) {
		List<ResolveInfo> allApp = new ArrayList<ResolveInfo>();
		// 先获取需要过滤掉的应用
		ArrayList<String> filterList = filterAppParse(mContext);
		PackageManager packageManager = mContext.getPackageManager();
		// 所有应用列表
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> tempAppList = packageManager.queryIntentActivities(mainIntent, 0);
		allApp.addAll(tempAppList);
		int size = tempAppList.size();
		Log.e(TAG, "start=all app size==" + size);
		if (filterList != null) {
			for (String name : filterList) {
				for (ResolveInfo resolveInfo : tempAppList) {
					if (name.equals(resolveInfo.activityInfo.packageName)) {
						allApp.remove(resolveInfo);
						break;
					}
				}
			}
		}
		Log.e(TAG, "END ALL APP SIZE ====" + allApp.size());
		return allApp;
	}

	/**
	 * 解析需要过滤的XML文件
	 * 
	 * @param is
	 *            文件流
	 */
	private static ArrayList<String> filterAppParse(Context context) {
		// 过滤的应用列表
		ArrayList<String> filterList = new ArrayList<String>();
		if (context != null) {
			InputStream fis = context.getResources().openRawResource(R.raw.filter_apps);
			// 开始解析XML文件(DOM解析)
			if (fis != null) {
				DocumentBuilderFactory xmlparser = DocumentBuilderFactory.newInstance();
				DocumentBuilder xmlDOC;
				Document doc = null;
				try {
					xmlDOC = xmlparser.newDocumentBuilder();
					doc = xmlDOC.parse(fis);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				NodeList listItem = doc.getElementsByTagName("Application");
				for (int i = 0; i < listItem.getLength(); i++) {
					if (listItem.item(i).hasChildNodes()) {
						NodeList list = listItem.item(i).getChildNodes();
						String name = "";
						for (int j = 0; j < list.getLength(); j++) {
							String nodeName = list.item(j).getNodeName();
							String nodeText = list.item(j).getTextContent();
							if (nodeName.equalsIgnoreCase("PackageName")) {
								name = nodeText;

							}
						}
						filterList.add(name);
					}
				}
			}
		}
		return filterList;
	}

	/**
	 * 保存图片到sdcard
	 * 
	 * @param b
	 *            图片
	 * @param strFileName
	 *            图片名字
	 * @param folderName
	 *            图片所在的文件夹名字
	 */
	public static void savePic(Bitmap b, String folderName, String strFileName) {
		String filePath = isExistsFilePath(folderName);
		FileOutputStream fos = null;
		Log.e(TAG, strFileName + "===filePath==" + filePath);
		File file = new File(filePath, strFileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			Log.e(TAG, "savePic by sdcard");
			file.createNewFile();
		} catch (Exception e) {
			Log.e(TAG, "savePic by smb");
			e.printStackTrace();
			file = new File("/mnt/smb/" + strFileName);
			if (file.exists()) {
				Log.e(TAG, "File exist by smb ");
				file.delete();
			}
			Log.e(TAG, "savePic by smb ");
			try {
				file.createNewFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		try {
			fos = new FileOutputStream(file);
			if (null != fos && null != b) {
				// 格式
				b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取缓存文件夹目录
	 * 
	 * @return filePath
	 */
	public static String isExistsFilePath(String name) {
		String filePath = "";
		try {
			filePath = getSDPath() + "/" + name;
			File file = new File(filePath);
			if (!file.exists()) {
				// 建立文件夹
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 获取sd卡的缓存路径， 一般在卡中sdCard就是这个目录
	 * 
	 * @return SDPath
	 */
	private static String getSDPath() {
		File sdDir = null;
		String path = "";
		try {
			// 判断sd卡是否存在
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if (sdCardExist) {
				// 获取根目录
				sdDir = Environment.getExternalStorageDirectory();
				path = sdDir.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * @param context
	 *            上下文
	 * @return ture 网络可用 ; false 网络不可用
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].isAvailable() && info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int isInChinese(Context context) {
		int isInChinese = 1;
		String language = context.getResources().getConfiguration().locale.getLanguage();
		if ("zh".equals(language)) {
			isInChinese = 0;
		}
		return isInChinese;

	}

	/**
	 * 获取xml数据
	 * 
	 * @return result xml数据
	 */
	public static String getXmlStr(String urlstr) {
		Log.d(TAG, "urlstr=" + urlstr);
		urlstr = urlstr.replace(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().closeIdleConnections(20, TimeUnit.SECONDS);
		String result = "";
		try {
			URL url = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);// 设置连接超时
			conn.setRequestMethod("GET");// 以get方式发起请求
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
			int retcode = conn.getResponseCode();
			Log.d(TAG, "retcode=" + retcode);
			if (retcode == 200) {
				InputStream is = conn.getInputStream();// 得到网络返回的输入流
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
				is.close();
				result = builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		Log.d(TAG, "result============" + result);
		return result;
	}

	public static void downloadOnePic(Context context, String imgPath, String folderName, int nameIndex) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imgPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}
				Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				if (null != bitmap) {
					String[] tempArray = imgPath.split("\\.");
					String tempName = tempArray[tempArray.length - 1];
					if (tempName.endsWith("jpg") || tempName.endsWith("gif") || tempName.endsWith("bmp") || tempName.endsWith("png")) {
						AppTool.savePic(bitmap, folderName, nameIndex + "." + tempName);
					} else {
						AppTool.savePic(bitmap, folderName, nameIndex + ".jpg");
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "download ad fail!!!!!!!!!!");
			//((LauncherOverseasActivity) context).setIsFirstAdThread(true);
			e.printStackTrace();
		} finally {
			try {
				conn.disconnect();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void downloadOnePic(Context context, String imgUrl, String filePath) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(imgUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}
				Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				if (null != bitmap) {
					File file = new File(filePath);
					if (file.exists()) {
						file.delete();
					}
					FileOutputStream fos = null;
					try {
						file.createNewFile();
						fos = new FileOutputStream(file);
						if (null != fos && null != bitmap) {
							bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							fos.flush();
							fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "download pic fail!!!!!!!!!!");
			e.printStackTrace();
		} finally {
			try {
				conn.disconnect();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 将Drawable类型转换为Bitmap
	 * 
	 * @param drawable
	 *            Drawable对象
	 * @return bitmap对象
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static String getVersion(Context context){
		try {
	        PackageManager manager = context.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
	        String version = info.versionName;
	        Log.e(TAG, "getVersion..version="+version);
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		return "1.00";
	}
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            要判断的字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(Object obj) {
		if (null == obj || "".equals(obj)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前机器中是否有额外的存储设备插入
	 * 
	 * @param mContext
	 *            上下文
	 * @return 是否存在额外的存储设备
	 */
	public static boolean isHasUsb(Context mContext) {
		boolean uCardIsExist = false;
		try {
			StorageManager mStorageManager = ((StorageManager) mContext.getSystemService(Activity.STORAGE_SERVICE));
			Method mMethodGetPaths = null;
			try {
				mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths", new Class[0]);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			String[] paths = ((String[]) mMethodGetPaths.invoke(mStorageManager, new Object[0]));
			if (paths.length > 1) {
				uCardIsExist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uCardIsExist;
	}

	/**
	 * 根据包名判断系统中是否存在该应用
	 * 
	 * @param packageName
	 *            包名
	 * @param mContext
	 *            上下文
	 * @return isExisted 是否存在
	 */
	public static boolean isAppExisted(String packageName, Context mContext) {
		boolean isExisted = false;
		try {
			ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName, 0);
			if (!isEmpty(info)) {
				isExisted = true;
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "the app is not existed int the system");
		}
		return isExisted;
	}

	public static String getSdcardDir() {
		File sdDir = null;
		sdDir = Environment.getExternalStorageDirectory();
		return sdDir.getPath();
	}

	public static String getAdStoragePath() {
		return getSdcardDir() + "/Klife/ad/";
	}

	public static String getNewsStoragePath() {
		return getSdcardDir() + "/Klife/news/";
	}

	public static String urlToFileName(String url) {
		String[] tempArray = url.split("\\.");
		String suffixName = tempArray[tempArray.length - 1];
		String path = stringToMD5(url) + "." + suffixName;
		return path;
	}

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
	}

	/** 
	 * 将字符串转成MD5值 
	 *  
	 * @param string 
	 * @return 
	 */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}

	//递归删除文件及文件夹  
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	public static int compareVersion(String s1, String s2) {
		if (s1 == null && s2 == null)
			return 0;
		else if (s1 == null)
			return -1;
		else if (s2 == null)
			return 1;

		String[] arr1 = s1.split("[^a-zA-Z0-9]+"), arr2 = s2.split("[^a-zA-Z0-9]+");

		int i1, i2, i3;

		for (int ii = 0, max = Math.min(arr1.length, arr2.length); ii <= max; ii++) {
			if (ii == arr1.length)
				return ii == arr2.length ? 0 : -1;
			else if (ii == arr2.length)
				return 1;

			try {
				i1 = Integer.parseInt(arr1[ii]);
			} catch (Exception x) {
				i1 = Integer.MAX_VALUE;
			}

			try {
				i2 = Integer.parseInt(arr2[ii]);
			} catch (Exception x) {
				i2 = Integer.MAX_VALUE;
			}

			if (i1 != i2) {
				return i1 - i2;
			}
			i3 = arr1[ii].compareTo(arr2[ii]);

			if (i3 != 0)
				return i3;
		}
		return 0;
	}

	public static boolean runCommand(String command) {

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);
			Log.i("command", "The Command is : " + command);
			process.waitFor();
		} catch (Exception e) {
			Log.w("Exception ", "Unexpected error - " + e.getMessage());
			return false;
		} finally {
			try {
				process.destroy();
			} catch (Exception e) {
				Log.w("Exception ", "Unexpected error - " + e.getMessage());
			}
		}
		return true;
	}

	public static String getSystemProperty(String key, String defValue) {
		try {
			Class clz = Class.forName("android.os.SystemProperties");
			Method get = clz.getDeclaredMethod("get", String.class, String.class);
			return String.valueOf(get.invoke(clz, key, defValue));
		} catch (Exception e) {
			e.printStackTrace();
			return defValue;
		}
	}

	public static String getConfigFromFile(String filepath, String defValue) {
		String str = "";
		try {
			BufferedReader localBufferedReader = new BufferedReader(new FileReader(filepath));
			str = localBufferedReader.readLine();
			str.replace(str, "\n");
			str.replace(str, "\r");
			localBufferedReader.close();
		} catch (Exception e) {
			return defValue;
		}
		return str;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			State state = wifiInfo.getState();
			if (State.CONNECTED == state) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isEthernetConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (networkInfo != null)
			return networkInfo.isConnected();
		else
			return false;
	}
	
	public static boolean isUsbConnected() {
		try {
			String str = getStringFromFile(new File("/proc/partitions"));
			if (str != null) {
				if (str.contains("sda1") || str.contains("sdb1") || str.contains("sdc1") || str.contains("sdd1") || str.contains("sde1")
						|| str.contains("sda2") || str.contains("sdb2") || str.contains("sdc2") || str.contains("sdd2")
						|| str.contains("sde2") || str.contains("sda3") || str.contains("sdb3") || str.contains("sdc3")
						|| str.contains("sdd3") || str.contains("sde3") || str.contains("sda4") || str.contains("sdb4")
						|| str.contains("sdc4") || str.contains("sdd4") || str.contains("sde4") || str.contains("sda5")
						|| str.contains("sdb5") || str.contains("sdc5") || str.contains("sdd5") || str.contains("sde5"))
					return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static String getStringFromFile(File paramFile) throws FileNotFoundException, IOException {
		BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramFile));
		StringBuilder localStringBuilder = new StringBuilder();
		while (true) {
			String str = localBufferedReader.readLine();
			if (str == null) {
				localBufferedReader.close();
				return localStringBuilder.toString();
			}
			localStringBuilder.append(str);
		}
	}

	public static List<ApkInfo> getShortcutApp(Context mContext) {
		List<ApkInfo> list = new ArrayList<ApkInfo>();
		for(int i=0; i<Utils.PACKAGE_NAME_ARRAY.length; i++){
			ApkInfo apk = new ApkInfo();
			apk.isLocal = true;
			apk.resId = Utils.SHORTCUT_DATA[i];
			apk.setPackagename(Utils.PACKAGE_NAME_ARRAY[i]);
			list.add(apk);
		}
		return list;
	}

	public static boolean canUpdateApk(String currentVersion, String targetVersion) {
		Log.e(TAG, "canUpdateApk..currentVersion="+currentVersion+"; targetVersion="+targetVersion);
		if(currentVersion==null || currentVersion.equals("")){
			return false;
		}
		/*if(targetVersion==null || targetVersion.equals("") || targetVersion.equals("-1")){
			return false;
		}*/
		currentVersion = currentVersion.toLowerCase().replace("v", "").replace(".", "");
		targetVersion = targetVersion.toLowerCase().replace("v", "").replace(".", "");
		try{
			double cVersion = Double.parseDouble(currentVersion);
			double tVersion =Double.parseDouble(targetVersion);
			Log.e(TAG, "canUpdateApk...currentVersion="+currentVersion+"; targetVersion="+targetVersion);
			if(tVersion > cVersion){
				return true;
			}
		}catch(Exception err){
			err.printStackTrace();
		}
		return false;
	}
	
	public static boolean canUpdate(String currentVersion, String targetVersion) {
		Log.e(TAG, "canUpdate..currentVersion="+currentVersion+"; targetVersion="+targetVersion);
		if(currentVersion==null || currentVersion.equals("")
		|| targetVersion == null || targetVersion.equals("")){
			return false;
		}
		
		currentVersion = currentVersion.toLowerCase().replace("v", "").replace(".", "");
		targetVersion = targetVersion.toLowerCase().replace("v", "").replace(".", "");
		try{
			int cVersion = Integer.parseInt(currentVersion);
			int tVersion =Integer.parseInt(targetVersion);
			Log.e(TAG, "canUpdate...currentVersion="+currentVersion+"; targetVersion="+targetVersion);
			if(tVersion > cVersion){
				return true;
			}
		}catch(Exception err){
			err.printStackTrace();
		}
		return false;
	}

}
