
package com.worldchip.bbp.bbpawmanager.cn.utils;


import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogUtil {

    private static final boolean DBG = Configure.DEBUG;

    private static FileOutputStream sDBGOutputStream = null;

    private LogUtil() {

    }

    /**
     * Send a {@link #VERBOSE} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (DBG) {
            Log.v(tag, msg);
        }
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (DBG) {
            Log.v(tag, msg, tr);
        }
    }

    /**
     * Send a {@link #DEBUG} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (DBG) {
            Log.d(tag, msg);
        }
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (DBG) {
            Log.d(tag, msg, tr);
        }
    }

    /**
     * Send an {@link #INFO} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (DBG) {
            Log.i(tag, msg);
        }
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (DBG) {
            Log.i(tag, msg, tr);
        }
    }

    /**
     * Send a {@link #WARN} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (DBG) {
            Log.w(tag, msg);
        }
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (DBG) {
            Log.w(tag, msg, tr);
        }
    }

    /*
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    public static void w(String tag, Throwable tr) {
        if (DBG) {
            Log.w(tag, tr);
        }
    }

    /**
     * Send an {@link #ERROR} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        // System.out.print(tag+":"+msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    /**
     * Write a log message to trace file under the sdcard root directory.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public synchronized static void f(String tag, String msg) {
        if (DBG) {
            toFile(Log.DEBUG, tag, msg);
        }
    }

    /**
     * Write a log message to trace file under the sdcard root directory, and
     * print a log by cmd line.
     * 
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public synchronized static void f(int priority, String tag, String msg) {
        if (DBG) {
            toFile(priority, tag, msg);
        }
    }

    /**
     * Write a log message to trace file under the sdcard root directory.
     * 
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    private synchronized static void toFile(int priority, String tag, String msg) {
        if (sDBGOutputStream == null) {
            initDBGOutputStream();
        }

        if (sDBGOutputStream != null) {
            try {
                Time time = new Time();
                time.setToNow();
                String msgdata = time.format2445() + " " + "/" + tag + " " + msg + "\r\n";
                sDBGOutputStream.write(msgdata.getBytes("UTF-8"));
                sDBGOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    sDBGOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                sDBGOutputStream = null;
            }
        }

    }

    private static void initDBGOutputStream() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "trace.txt");
            try {
                sDBGOutputStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     * @Override protected void finalize() throws Throwable { if
     * (sDBGOutputStream != null) { try { sDBGOutputStream.close(); } catch
     * (IOException e1) { e1.printStackTrace(); } } super.finalize(); }
     */

    public static void toast(Context ctx, String msg) {
        if (DBG) {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        }
    }
    
}
