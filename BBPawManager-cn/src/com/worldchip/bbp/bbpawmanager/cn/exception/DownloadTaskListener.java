
package com.worldchip.bbp.bbpawmanager.cn.exception;

import com.worldchip.bbp.bbpawmanager.cn.download.DownloadTask;

public interface DownloadTaskListener {

    public void updateProcess(DownloadTask task);

    public void finishDownload(DownloadTask task);

    public void preDownload(DownloadTask task);

    public void errorDownload(DownloadTask task, Throwable error);
}
