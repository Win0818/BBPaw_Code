package com.worldchip.bbp.ect.db;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.bbp.ect.entity.AppInfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.util.Base64;
import android.util.Log;

public class AppData {

	private static List<AppInfo> shareApps = null;

	/**
	 * 清除
	 */
	public static void clearShareAppList() {
		if (shareApps != null) {
			shareApps.clear();
		}
	}

	/**
	 * 添加APP
	 * 
	 * @param context
	 * @param values
	 */
	public static void addShareApp(Context context, ContentValues values) {
		DBHelper helper = new DBHelper(context);
		helper.insertValues(DBSqlBuilder.BBP_APP_TABLE, values);
		Log.e("addShareApp", "success");
	}

	/**
	 * 根据包名获取所对应的APP
	 */
	public static AppInfo getAppInfo(Context context, String name) {
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_APP_TABLE,
				"where packageName = '" + name + "' order by _id desc");
		if (cursor != null) {
			while (cursor.moveToFirst()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String packageName = cursor.getString(cursor
						.getColumnIndex("packageName"));
				String icon = cursor.getString(cursor.getColumnIndex("icon"));
				cursor.close();
				return new AppInfo(id, packageName, icon);
			}
		}
		return null;
	}

	/**
	 * 获取所APP
	 */
	public static List<AppInfo> getLocalShareAppDatas(Context context) {
		if (shareApps != null && shareApps.size() > 0) {
			return shareApps;
		}
		shareApps = new ArrayList<AppInfo>();
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursor(DBSqlBuilder.BBP_APP_TABLE,
				"order by _id desc");
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String packageName = cursor.getString(cursor
					.getColumnIndex("packageName"));
			String icon = cursor.getString(cursor.getColumnIndex("icon"));
			AppInfo appInfo = null;
			if (checkBrowser(packageName, context)) {
				appInfo = new AppInfo(id, packageName, icon);
				shareApps.add(appInfo);
			} else {
				delShareAppId(context, id);
			}
		}
		cursor.close();
		return shareApps;
	}

	/**
	 * 取消分享
	 * 
	 * @param context
	 * @param data
	 * @return
	 */
	public static void delShareAppId(Context context, long id) {
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_CLOCk_TABLE, id);
		Log.e("delShareAppId", "falg：" + falg);
	}

	/**
	 * 取消分享
	 * 
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean delShareAppData(Context context, String packageName) {
		DBHelper helper = new DBHelper(context);
		boolean falg = helper.deleteRow(DBSqlBuilder.BBP_APP_TABLE,
				"packageName='" + packageName + "'");
		Log.e("delShareAppData", "falg：" + falg);
		return falg;
	}

	/**
	 * 判断该APP是否已经分享
	 * 
	 * @param context
	 * @param data
	 * @return
	 */
	public static boolean getShareAppByData(Context context, String packageName) {
		DBHelper helper = new DBHelper(context);
		Cursor cursor = helper.getCursorBySql("select count(*) from '"
				+ DBSqlBuilder.BBP_APP_TABLE + "' where packageName = '"
				+ packageName + "' order by _id desc");
		if (cursor != null) {
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			if (count > 0) {
				cursor.close();
				return true;
			}
		}
		return false;
	}

	public static List<String> getForbitPackageList() {
		List<String> pks = new ArrayList<String>();
		pks.add("com.android.settings");
		pks.add("com.android.browser");
		pks.add("com.android.camera2");
		pks.add("com.android.deskclock");
		pks.add("com.android.providers.downloads.ui");
		pks.add("com.softwinner.explore");
		pks.add("com.google.android.gms");
		return pks;
	}

	/**
	 * 获取系统所有APP
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getLocalAppDatas(Context context) {
		List<AppInfo> items = new ArrayList<AppInfo>();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mApps = context.getPackageManager()
				.queryIntentActivities(mainIntent, 0);
		AppInfo appInfo = null;
		List<String> pkgs = getForbitPackageList();

		for (ResolveInfo app : mApps) {
			System.out.println("packageName" + app.activityInfo.packageName);
			appInfo = new AppInfo();
			appInfo.setPackageName(app.activityInfo.packageName);
			appInfo.setIcon(drawableToByte(app.activityInfo.loadIcon(context
					.getPackageManager())));
			appInfo.isSelected = AppData.getShareAppByData(context,
					app.activityInfo.packageName);
			if (pkgs.toString().contains(app.activityInfo.packageName)) {
				continue;
			}
			items.add(appInfo);
		}
		return items;
	}

	/**
	 * 判断指定应用是否存在
	 */
	public static boolean checkBrowser(String packageName, Context context) {
		try {
			if (packageName == null || "".equals(packageName)) {
				return false;
			}
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			Log.e("ApplicationInfo", "info:" + info);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 将Drawable转换为String
	 */
	public synchronized static String drawableToByte(Drawable drawable) {
		if (drawable != null) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			int size = bitmap.getWidth() * bitmap.getHeight() * 4;

			// 创建一个字节数组输出流,流的大小为size
			ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
			// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			// 将字节数组输出流转化为字节数组byte[]
			byte[] imagedata = baos.toByteArray();

			String icon = Base64.encodeToString(imagedata, Base64.DEFAULT);
			return icon;
		}
		return null;
	}

	/**
	 * 将String转换为Drawable
	 */
	public synchronized static Drawable byteToDrawable(String icon) {
		byte[] img = Base64.decode(icon.getBytes(), Base64.DEFAULT);
		Bitmap bitmap;
		if (img != null) {
			bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(bitmap);
			return drawable;
		}
		return null;
	}

	/**
	 * 判断事都是系统APP
	 * 
	 * @param pInfo
	 * @return
	 */
	public static boolean isSystemApp(String packageName, Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_ACTIVITIES);
			if ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}