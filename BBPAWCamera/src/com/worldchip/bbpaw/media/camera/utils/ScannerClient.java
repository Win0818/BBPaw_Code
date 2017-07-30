package com.worldchip.bbpaw.media.camera.utils;
import java.util.ArrayList;

import com.worldchip.bbpaw.media.camera.activity.MyApplication;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class ScannerClient implements MediaScannerConnectionClient {

	private ArrayList<String> mPaths = new ArrayList<String>();
	private MediaScannerConnection mScannerConnection;
	private  boolean mConnected;
	private Object mLock = new Object();
    private static ScannerClient mInstance;
    
	public static ScannerClient getInstance()
	{
		if (mInstance == null)
		{
			synchronized (ScannerClient.class)
			{
				if (mInstance == null)
				{
					mInstance = new ScannerClient(MyApplication.getAppContext());
				}
			}
		}
		return mInstance;
	}
	
    public ScannerClient(Context context) {
        mScannerConnection = new MediaScannerConnection(context, this);
    }

    public void scanPath(String path) {
        synchronized (mLock) {
            if (mConnected) {
                mScannerConnection.scanFile(path, null);
            } else {
                mPaths.add(path);
                mScannerConnection.connect();
            }
        }
    }

    @Override
    public void onMediaScannerConnected() {
        synchronized (mLock) {
            mConnected = true;
            if (!mPaths.isEmpty()) {
                for (String path : mPaths) {
                    mScannerConnection.scanFile(path, null);
                }
                mPaths.clear();
            }
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
    	Log.e("ScannerClient", "onScanCompleted " + path + " ------->>>>>");
    }

	public MediaScannerConnection getScannerConnection() {
		return mScannerConnection;
	}

	public void setConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}
    
}
