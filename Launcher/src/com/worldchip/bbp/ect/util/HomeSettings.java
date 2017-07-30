package com.worldchip.bbp.ect.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class HomeSettings  {

	private IntentFilter mHomeFilter;
	private PackageManager mPm;
	private ComponentName[] mHomeComponentSet;
	private Context mContext;

	public HomeSettings(Context context) 
	{
		mContext = context;
	}   

	public void changeHomeToLauncher() 
	{
		initHomeIntent(mContext);
		ComponentName cn = new ComponentName("com.android.launcher","com.android.launcher2.Launcher");
		try {
			Method method = mPm.getClass().getMethod("replacePreferredActivity",new Class[] { IntentFilter.class, int.class,ComponentName[].class, ComponentName.class });
			method.invoke(mPm, mHomeFilter, IntentFilter.MATCH_CATEGORY_EMPTY,mHomeComponentSet, cn);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void changeHomeToECT() 
	{
		initHomeIntent(mContext);
		ComponentName cn = new ComponentName("com.worldchip.bbp.ect","com.worldchip.bbp.ect.activity.MainActivity");
		try {
			Method method = mPm.getClass().getMethod("replacePreferredActivity",new Class[] { IntentFilter.class, int.class,ComponentName[].class, ComponentName.class });
			method.invoke(mPm, mHomeFilter, IntentFilter.MATCH_CATEGORY_EMPTY,mHomeComponentSet, cn);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void initHomeIntent(Context context) 
	{
		mPm = context.getPackageManager();
		mHomeFilter = new IntentFilter(Intent.ACTION_MAIN);
		mHomeFilter.addCategory(Intent.CATEGORY_HOME);
		mHomeFilter.addCategory(Intent.CATEGORY_DEFAULT);
		ArrayList<ResolveInfo> homeActivities = new ArrayList<ResolveInfo>();
		try {
			Method method = mPm.getClass().getMethod("getHomeActivities",new Class[] {homeActivities.getClass()});
			method.invoke(mPm, homeActivities);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		mHomeComponentSet = new ComponentName[homeActivities.size()];
		for (int i = 0; i < homeActivities.size(); i++) {
			final ResolveInfo candidate = homeActivities.get(i);
			final ActivityInfo info = candidate.activityInfo;
			ComponentName activityName = new ComponentName(info.packageName,info.name);
			mHomeComponentSet[i] = activityName;
		}
	}
}