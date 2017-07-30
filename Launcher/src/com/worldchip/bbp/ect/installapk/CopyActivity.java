package com.worldchip.bbp.ect.installapk;

import com.worldchip.bbp.ect.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CopyActivity extends Activity implements ICopyEnd {

	private static final String TAG = "--CopyActivity--";
	private static final boolean DEBUG = false;
	
	private LinearLayout mCopyViewList;
	
	private TextView mInfo;
	private TextView mCopyInfo;
    private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = CopyActivity.this;
		if(Utils.mCopyFileList==null || Utils.mCopyFileList.size()<=0){
			startIdActivity();
			return;
		}
		setContentView(R.layout.copy_main);
		initView();
		copyFile();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	};
	
	private void initView(){
		mCopyViewList = (LinearLayout) findViewById(R.id.copy_view);
		mInfo  = (TextView)findViewById(R.id.info);
		mCopyInfo = (TextView)findViewById(R.id.copy_info);
	}
    
	private void startIdActivity(){
    	try{
    		Toast.makeText(mContext, "Video Copy Finished!!", Toast.LENGTH_SHORT).show();
			Intent i = new Intent();
            i.setAction(Utils.VIDEO_COPY_ACTION);
            mContext.sendBroadcast(i);
			this.finish();
    	}catch(Exception err){
    		Log.e(TAG, "startMainActivity..Exception: "+err.getMessage());
    		System.exit(0);
    	}
    }
	
   /** 
    * �����ļ�
    * @return boolean 
    */ 
   public void copyFile(){
		mInfo.setText("Total:  " + Utils.mCopyFileList.size() + "  file(s) ");
		mCopyInfo.setText("Now Copying " + Utils.mCopyFileList.size() + " file(s) ");

		for (CopyFile copyFile : Utils.mCopyFileList) {
			
			Log.e(TAG, "copyFile...copyFile =" + copyFile.mFilePath + "; copyFile.mFileExtend=" + copyFile.mFileExtend + "; copyFile.mCategory=" + copyFile.mCategory);
			CopyItemView copyItemView = new CopyItemView(this, copyFile);
			this.mCopyViewList.addView(copyItemView);
			copyItemView.start();
		}
		
		if(this.mCopyViewList.getChildCount() <=0){
        	startIdActivity();
        	return;
		}
    } 
   
	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	@Override
    protected void onDestroy() 
	{
        super.onDestroy();
    }

	public void copyEnd(CopyItemView copyItemView) {

        mCopyInfo.setText(copyItemView.getFileName() + " copy success!\n remaining " +(mCopyViewList.getChildCount()-1) + "file(s).");
        this.mCopyViewList.removeView(copyItemView);
        
		if (this.mCopyViewList.getChildCount() < 1) {
        	Toast.makeText(CopyActivity.this, " Copy completed!", Toast.LENGTH_LONG).show();
        	startIdActivity();
        	return;
        }
	};

}