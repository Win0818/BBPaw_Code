package com.worldchip.bbp.ect.installapk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.ect.R;


public class InstallApkActivity extends Activity {

	protected static final String TAG = "--guofq--MainActivity";
	protected static final String PATH = "/mnt/extsd/BBPAW_APP"; // // MTPS"; //JUNIOR";
	private TextView chipIdTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                        WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        Utils.isMainActivityStarted=true;
        Utils.preInstallApksCount=0;
		Utils.installedApksCount=0;
        setContentView(R.layout.intallapk_activity_main);
        chipIdTextView = (TextView) findViewById(R.id.textView_chipid);
        initView();
        IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Utils.VIDEO_COPY_ACTION);
        mIntentFilter.addAction(Utils.INSTALL_APK_ACTION);
		registerReceiver(mCopyVideoAndInstallApkReceiver, mIntentFilter);
		
        final File file = new File(PATH);
        Utils.mCopyFileList.clear();
        Log.e(TAG, "file.exist "+file.exists());
		if(!file.exists()){
			//Toast.makeText(this, "Do not found BBPAW_APP dir!",Toast.LENGTH_SHORT).show();
			this.finish();	
			return; 
		}else{
			//dialog(file);
			// FindAllAPKFile(file);
			startCopyVideo(file);
		}
	}
    
    private void initView() {
    	String cpuIdOne, cpuIdTwo, cpuIdThree, cpuIdFour;
    	final String cpuId = com.worldchip.bbp.ect.util.Utils.getCpuSerial(getApplicationContext());
    	cpuIdOne = cpuId.substring(0, 4);
    	cpuIdTwo = cpuId.substring(4, 6);
    	cpuIdThree = cpuId.substring(6, 11);
    	cpuIdFour = cpuId.substring(11);
    	chipIdTextView.setText("CHIP ID: " + cpuIdOne + "  " + cpuIdTwo + "  " + cpuIdThree + "  " + cpuIdFour);
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				save(cpuId);
			}
		}).start(); ;
    	
    }

    public void save(String string) {
    	FileOutputStream outStream;
    	OutputStreamWriter writer = null ;
    	try 
    	  {
    	   outStream = new FileOutputStream("/mnt/sdcard/BBPaw/"+"CpuId.txt",false);
    	   writer = new OutputStreamWriter(outStream,"utf-8");
    	   Log.d("Wing", "CPUID: " + string);
    	   writer.write(string);
    	   writer.flush();
    	   outStream.close();
    	  } 
    	  catch (Exception e)
    	  {
    	     Log.e("m", "file write error");
    	  } finally{
    			  try {
    				  if (writer != null){
    					  writer.close();
    				  }//记得关闭
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
    		  } 
    	  }
    }
    
	protected void dialog(final File file) {
		AlertDialog.Builder builder = new Builder(InstallApkActivity.this);
	    builder.setMessage("Do you want to install applications?");
	    builder.setTitle("App Install Tool");
	    builder.setPositiveButton("Install", new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
	           dialog.dismiss();
	           FindAllAPKFile(file);
		    }
	    });
        builder.setNegativeButton("Cancel", new OnClickListener() {

		    public void onClick(DialogInterface dialog, int which) {
		       dialog.dismiss();
		       InstallApkActivity.this.finish();
		    }
        });
        builder.create().show();
	}
	@Override
	protected void onStop() {
		super.onStop();
		//this.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Utils.isMainActivityStarted=false;
		Utils.preInstallApksCount=0;
		Utils.installedApksCount=0;
		unregisterReceiver(mCopyVideoAndInstallApkReceiver);
		this.finish();
		super.onDestroy();
	}

	public void FindAllAPKFile(File file) {
		//Log.e(TAG, "file " + file);
		Toast.makeText(this, "Scanning apps and copying...",Toast.LENGTH_SHORT).show();
		if (file.isFile()) {
			String name_s = file.getName();
			String apk_path = null;
			try {
				if (name_s.endsWith(".apk")) {
					apk_path = file.getAbsolutePath();
					Intent intent = new Intent("android.intent.action.VIEW.HIDE");
					intent.setDataAndType(Uri.parse("file://" + apk_path),
							"application/vnd.android.package-archive");
					intent.putExtra("apk_name", name_s);
					Utils.preInstallApksCount++;
					if(true)
						Log.i(TAG, "Utils.preInstallApkCount="+Utils.preInstallApksCount);
					startActivity(intent);
				}
			} catch (Exception e) {
				Log.d("Wing", "Apk installs false");
			}
		} else {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File file_str : files) {
					FindAllAPKFile(file_str);
				}
			}
		}
	}
	
	private void startCopyVideo(File file) {
		Utils.getCopyFileList(Utils.mCopyFileList,file);
		if(Utils.mCopyFileList==null || Utils.mCopyFileList.size()<=0){
			Toast.makeText(this, "Do not found video file(s)!",Toast.LENGTH_SHORT).show();
			FindAllAPKFile(file);
			return;
		}
		
		Intent intent = new Intent(InstallApkActivity.this, CopyActivity.class);
		startActivity(intent);
	}
	
	//by wuyong
    private BroadcastReceiver mCopyVideoAndInstallApkReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
			      String action = intent.getAction();
	            if (action.equals(Utils.INSTALL_APK_ACTION)) { 
	            	Utils.isMainActivityStarted = false;
	            	//InstallApkActivity.this.finish();
	            	/*try {
						Intent mIntent = new Intent();
						intent.putExtra("isRegister", true);
						ComponentName comp = new ComponentName(
								"com.worldchip.bbpaw.bootsetting",
								"com.worldchip.bbpaw.bootsetting.activity.MainActivity");
						mIntent.setComponent(comp);
						mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(mIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}*/
	            	InstallApkActivity.this.finish();
	            }
            
                if (action.equals(Utils.VIDEO_COPY_ACTION)) {  
                	File file = new File(PATH);
                	FindAllAPKFile(file);
               }
           }	
    }; 
}
