
package com.worldchip.bbp.bbpawmanager.cn.download;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.exception.DownloadTaskListener;
import com.worldchip.bbp.bbpawmanager.cn.utils.DownloadUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.ExceptionMessageUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.NetworkUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.StorageUtils;

public class DownloadManager extends Thread {

    private static final int MAX_TASK_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

    private Context mContext;

    private TaskQueue mTaskQueue;
    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mPausingTasks;

    private Boolean isRunning = false;

    public DownloadManager(Context context) {

        mContext = context;
        mTaskQueue = new TaskQueue();
        mDownloadingTasks = new ArrayList<DownloadTask>();
        mPausingTasks = new ArrayList<DownloadTask>();
    }

    public void startManage() {

        isRunning = true;
        this.start();
        //checkUncompleteTasks();保留该接口,后面下载管理用到
    }

    public void close() {

        isRunning = false;
        pauseAllTask();
        this.stop();
    }

    public boolean isRunning() {

        return isRunning;
    }

    @Override
    public void run() {

        super.run();
        while (isRunning) {
            DownloadTask task = mTaskQueue.poll();
            mDownloadingTasks.add(task);
            task.execute();
        }
    }

    public void addTask(String url) {
    	Log.e("lee", "addTask --------------------------");
        if (!StorageUtils.isSDCardPresent()) {
            Toast.makeText(mContext, R.string.no_sdcard_text, Toast.LENGTH_LONG).show();
            return;
        }

        if (!StorageUtils.isSdCardWrittenable()) {
            Toast.makeText(mContext, R.string.sdcard_unread_text, Toast.LENGTH_LONG).show();
            return;
        }

        if (getTotalTaskCount() >= MAX_TASK_COUNT) {
            Toast.makeText(mContext, R.string.task_full_text, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            addTask(newDownloadTask(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void addTask(DownloadTask task) {

        broadcastAddTask(task.getUrl());

        mTaskQueue.offer(task);

        if (!this.isAlive()) {
            this.startManage();
        }
    }

    private void broadcastAddTask(String url) {

        broadcastAddTask(url, false);
    }

    /**
     * 该接口留给下载管理使用
     * @param url
     * @param isInterrupt
     */
    private void broadcastAddTask(String url, boolean isInterrupt) {

        /*Intent nofityIntent = new Intent(DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION));
        nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
        nofityIntent.putExtra(MyIntents.URL, url);
        nofityIntent.putExtra(MyIntents.IS_PAUSED, isInterrupt);
        mContext.sendBroadcast(nofityIntent);*/
    }

    public void reBroadcastAddAllTask() {

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            broadcastAddTask(task.getUrl(), task.isInterrupt());
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            broadcastAddTask(task.getUrl());
        }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            broadcastAddTask(task.getUrl());
        }
    }

    public boolean hasTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task.getUrl().equals(url)) {
                return true;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
        }
        return false;
    }

    public DownloadTask getTask(int position) {

        if (position >= mDownloadingTasks.size()) {
            return mTaskQueue.get(position - mDownloadingTasks.size());
        } else {
            return mDownloadingTasks.get(position);
        }
    }

    public int getQueueTaskCount() {
        return mTaskQueue.size();
    }

    public int getDownloadingTaskCount() {

        return mDownloadingTasks.size();
    }

    public int getPausingTaskCount() {

        return mPausingTasks.size();
    }

    public int getTotalTaskCount() {

        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
    }

    public void checkUncompleteTasks() {

        List<String> urlList = DownloadUtils.getURLArray(mContext);
        if (urlList.size() >= 0) {
            for (int i = 0; i < urlList.size(); i++) {
            	Log.e("lee", "checkUncompleteTasks --------------------------");
                addTask(urlList.get(i));
            }
        }
    }

    public synchronized void pauseTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                pauseTask(task);
            }
        }
    }

    public synchronized void pauseAllTask() {

        DownloadTask task;

        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            mTaskQueue.remove(task);
            mPausingTasks.add(task);
        }

        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null) {
                pauseTask(task);
            }
        }
    }

    public synchronized void deleteTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                //File file = new File(StorageUtils.FILE_ROOT
                  //      + NetworkUtils.getFileNameFromUrl(task.getUrl()));
            	File tempFile = task.getTempFile();
                if (tempFile != null && tempFile.exists())
                	tempFile.delete();
                task.onCancelled();
                DownloadUtils.clearURL(mContext, mDownloadingTasks.indexOf(task));
                mDownloadingTasks.remove(task);
                return;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mTaskQueue.remove(task);
            }
        }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mPausingTasks.remove(task);
            }
        }
    }

    public synchronized void continueTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
            	Log.e("lee", "continueTask -----------"+url);
                continueTask(task);
            }

        }
    }

    public synchronized void pauseTask(DownloadTask task) {

        if (task != null) {
            task.onCancelled();

            // move to pausing list
            String url = task.getUrl();
            try {
                mDownloadingTasks.remove(task);
                task = newDownloadTask(url);
                mPausingTasks.add(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void continueTask(DownloadTask task) {

        if (task != null) {
        	Log.e("lee", "continueTask -----------");
            mPausingTasks.remove(task);
            mTaskQueue.offer(task);
        }
    }

    public synchronized void completeTask(DownloadTask task) {
        if (mDownloadingTasks.contains(task)) {
            DownloadUtils.clearURL(mContext, mDownloadingTasks.indexOf(task));
            mDownloadingTasks.remove(task);
            Intent nofityIntent = new Intent(DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION);
            nofityIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_COMPLETE);
            nofityIntent.putExtra(DownloadUtils.URL, task.getUrl());
            File downloadFile = task.getFile();
            if (downloadFile != null) {
            	nofityIntent.putExtra(DownloadUtils.FILE_PATH, downloadFile.getAbsolutePath());
            }
            mContext.sendBroadcast(nofityIntent);
        }
    }

    /**
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private DownloadTask newDownloadTask(String url) throws MalformedURLException {

        DownloadTaskListener taskListener = new DownloadTaskListener() {

            @Override
            public void updateProcess(DownloadTask task) {
                Intent updateIntent = new Intent(DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION);
                updateIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_PROCESS);
                updateIntent.putExtra(DownloadUtils.PROCESS_PROGRESS, task.getDownloadPercent() + "");
                updateIntent.putExtra(DownloadUtils.URL, task.getUrl());
                mContext.sendBroadcast(updateIntent);
            }

            @Override
            public void preDownload(DownloadTask task) {

                DownloadUtils.storeURL(mContext, mDownloadingTasks.indexOf(task), task.getUrl());
            }

            @Override
            public void finishDownload(DownloadTask task) {

                completeTask(task);
            }

            @Override
            public void errorDownload(DownloadTask task, Throwable error) {
                if (error != null) {
                	String exceptionMessage = ExceptionMessageUtils.getExceptionMessage(mContext, error);
                	if (!TextUtils.isEmpty(exceptionMessage)) {
                		Toast.makeText(mContext, exceptionMessage, Toast.LENGTH_SHORT).show();
                	} else {
                		Toast.makeText(mContext, R.string.download_unknown_error_msg, Toast.LENGTH_SHORT).show();
                	}
                }
                 Intent errorIntent = new Intent(DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION);
                 errorIntent.putExtra(DownloadUtils.TYPE, DownloadUtils.ACTION_DOWNLOAD_ERROR);
                 errorIntent.putExtra(DownloadUtils.URL, task.getUrl());
                 mContext.sendBroadcast(errorIntent);
                 if (mDownloadingTasks.contains(task)) {
                     DownloadUtils.clearURL(mContext, mDownloadingTasks.indexOf(task));
                     mDownloadingTasks.remove(task);
                 }
            }
        };
        
        try {
            StorageUtils.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DownloadTask(mContext, url, StorageUtils.FILE_ROOT, taskListener);
    }

    /**
     * A obstructed task queue
     * 
     */
    private class TaskQueue {
        private Queue<DownloadTask> taskQueue;

        public TaskQueue() {

            taskQueue = new LinkedList<DownloadTask>();
        }

        public void offer(DownloadTask task) {

            taskQueue.offer(task);
        }

        public DownloadTask poll() {

            DownloadTask task = null;
            while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null) {
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public DownloadTask get(int position) {

            if (position >= size()) {
                return null;
            }
            return ((LinkedList<DownloadTask>) taskQueue).get(position);
        }

        public int size() {

            return taskQueue.size();
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {

            return taskQueue.remove(get(position));
        }

        public boolean remove(DownloadTask task) {

            return taskQueue.remove(task);
        }
    }

}
