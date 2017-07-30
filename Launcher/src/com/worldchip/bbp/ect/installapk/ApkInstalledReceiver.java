package com.worldchip.bbp.ect.installapk;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.worldchip.bbp.ect.R; 

public class ApkInstalledReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.i("---ApkInstalledReceiver--", "action="+action+"; isMainActivityStarted="+Utils.isMainActivityStarted);
		if (Intent.ACTION_PACKAGE_ADDED.equals(action) && Utils.isMainActivityStarted) {
			Utils.installedApksCount++;
			String packageName = intent.getDataString().substring(8);
			String str=Utils.installedApksCount+" of "+Utils.preInstallApksCount+" app installed, the name is "+packageName+".apk";
			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
			Log.i("--ApkInstalledReceiver", "str="+str);
			if(Utils.preInstallApksCount == Utils.installedApksCount){
				//Toast.makeText(context, "All apps installed successed!", Toast.LENGTH_SHORT).show();
				Log.i("--ApkInstalledReceiver--", "all apps installed successed!");
				String tips = context.getString(R.string.install_app_tips);
				/*if (com.worldchip.bbp.ect.util.Utils.getLanguageInfo(context) == com.worldchip.bbp.ect.util.Utils.ENG_LANGUAGE_INDEX) {
					tips = R.string.install_app_tips;
				} else {
				}*/
				showDialogInBroadcastReceiver(context, tips);
				/*Intent i = new Intent();
                i.setAction(Utils.INSTALL_APK_ACTION);
				context.sendBroadcast(i);*/
			}
		}	
		 if (action.equals(Intent.ACTION_MEDIA_EJECT)) {       	
        	Intent i = new Intent();
            i.setAction(Utils.INSTALL_APK_ACTION);
			context.sendBroadcast(i);  
         }
	}
	private void showDialogInBroadcastReceiver(Context context,String message){
        Builder builder = new Builder(context);  
        //builder.setIcon(R.drawable.ic_launcher);  
        builder.setTitle("Install");  
        builder.setMessage(message);  
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
            @Override 
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }  
        });  
        Dialog dialog=builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();  
    }
}
