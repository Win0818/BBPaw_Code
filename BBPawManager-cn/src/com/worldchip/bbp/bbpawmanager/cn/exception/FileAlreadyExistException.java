
package com.worldchip.bbp.bbpawmanager.cn.exception;

import com.worldchip.bbp.bbpawmanager.cn.download.DownloadException;

public class FileAlreadyExistException extends DownloadException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FileAlreadyExistException(String message) {

        super(message);
    }

}
