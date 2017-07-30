
package com.worldchip.bbp.bbpawmanager.cn.download;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.utils.DownloadUtils;

public class DownloadService extends Service {

    private DownloadManager mDownloadManager;
    private static final String		ACTION_START = "START";
	private static final String		ACTION_STOP = "STOP";
	
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    
 // Static method to start the service
 	public static void actionStart(Context ctx) {
 		Intent i = new Intent(ctx, DownloadService.class);
 		i.setAction(ACTION_START);
 		ctx.startService(i);
 	}

 	// Static method to stop the service
 	public static void actionStop(Context ctx) {
 		Intent i = new Intent(ctx, DownloadService.class);
 		i.setAction(ACTION_STOP);
 		ctx.startService(i);
 	}
 	

    @Override
    public void onCreate() {

        super.onCreate();
        mDownloadManager = new DownloadManager(this);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

            int type = intent.getIntExtra(DownloadUtils.TYPE, -1);
            String url;

            switch (type) {
                case DownloadUtils.ACTION_DOWNLOAD_START:
                    if (!mDownloadManager.isRunning()) {
                        mDownloadManager.startManage();
                    } else {
                        mDownloadManager.reBroadcastAddAllTask();
                    }
                    break;
                case DownloadUtils.ACTION_DOWNLOAD_ADD:
                    url = intent.getStringExtra(DownloadUtils.URL);
                    if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
                        mDownloadManager.addTask(url);
                    }
                    break;
                case DownloadUtils.ACTION_DOWNLOAD_CONTINUE:
                    url = intent.getStringExtra(DownloadUtils.URL);
                    if (!TextUtils.isEmpty(url)) {
                    	Log.e("lee", "ACTION_DOWNLOAD_CONTINUE -----------"+url);
                        mDownloadManager.continueTask(url);
                    }
                    break;
                case DownloadUtils.ACTION_DOWNLOAD_DELETE:
                    url = intent.getStringExtra(DownloadUtils.URL);
                    if (!TextUtils.isEmpty(url)) {
                        mDownloadManager.deleteTask(url);
                    }
                    break;
                case DownloadUtils.ACTION_DOWNLOAD_PAUSE:
                    url = intent.getStringExtra(DownloadUtils.URL);
                    if (!TextUtils.isEmpty(url)) {
                        mDownloadManager.pauseTask(url);
                    }
                    break;
                case DownloadUtils.ACTION_DOWNLOAD_STOP:
                    mDownloadManager.close();
                    // mDownloadManager = null;
                    break;

                default:
                    break;
            }

    }
}
