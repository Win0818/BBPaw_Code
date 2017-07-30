package com.egreat.adlauncher.service;

import java.io.File;

import com.egreat.adlauncher.util.AppTool;
import com.egreat.adlauncher.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author guofq
 * 20130528
 */
public class BootReceiver extends BroadcastReceiver {
	
    private static final String TAG = "-------##BootReceiver##------------";
    private SharedPreferences mPrefs;
    
	@Override
    public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();

		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
		    Log.e(TAG, "ACTION_BOOT_COMPLETED...will start idle activity!");
		    
		    mPrefs = context.getSharedPreferences("egreat_setup", 0);
		    String currentVersion = AppTool.getVersion(context);
		    String newVersion = mPrefs.getString("new_version", currentVersion);
		    boolean canUpdate = AppTool.canUpdate(currentVersion, newVersion);
		    Log.e(TAG, "ACTION_BOOT_COMPLETED...currentVersion="+currentVersion
		    		+"; newVersion="+newVersion+"; canUpdate="+canUpdate);
		    if(canUpdate){
		       startUpdate(context);
		    }
		}
    }
	
	private void startUpdate(Context context) {
		try{
			File file = new File(Utils.getRootPath()+Utils.LAUNCHER_NAME);
			Log.e(TAG, "startUpdate...file.exist="+file.exists());
			if(file.exists()){
				installApk(context, file);
			}
		}catch(Exception err){
			err.printStackTrace();
		}
	}

	public void installApk(Context context, final File file) {
		try{
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(),
					PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = null;
			if (info != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()),
						"application/vnd.android.package-archive");
				context.startActivity(intent);
			} else {
				//Toast.makeText(context, "start app error! application is null!", Toast.LENGTH_LONG).show();
				if (file.exists()){
					file.delete();
				}
			}
		}catch(Exception err){
			err.printStackTrace();
		}
	}
}