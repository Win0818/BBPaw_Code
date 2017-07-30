
package com.worldchip.bbp.bbpawmanager.cn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * 
 * @ClassName Shared
 * @Description 通过 SharedPreferences 读取、存储数据
 */
public class Shared {
    private final String TAG = "Shared";

    private final static String SHARED_NAME_BASE = "settings"; // 设置

    /**
     *  私有数据，只能被应用本身访问
     */
    public final static int SHARED_MODE_PRIVATE = Build.VERSION.SDK_INT >= 14 ? 4 : Context.MODE_PRIVATE;
    /**
     *  全局数据，只读
     */
    private final static int SHARED_MODE_WORLD_READABLE = Context.MODE_WORLD_READABLE;
    /**
     *  全局数据，可写
     */
    private final static int SHARED_MODE_WORLD_WRITEABLE = Context.MODE_WORLD_WRITEABLE;

    private Context mContext;
    private static Shared mInstance = null;

    /**
     * 部分变量会频繁使用，需要缓存
     */
    private static HashMap<String, WeakReference<String>> mCacheString = new HashMap<String, WeakReference<String>>();

    private Shared(Context context) {
        mContext = context.getApplicationContext();
    }

    public static synchronized Shared getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Shared(context);
        }
        return mInstance;
    }


    /**
     * 存储key、value，默认name=SHARED_NAME_BASE、value=SHARED_MODE_PRIVATE
     * 
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        putString(SHARED_NAME_BASE, SHARED_MODE_PRIVATE, key, value, false);
    }

    /**
     * 存储key、value，默认name=SHARED_NAME_BASE、value=SHARED_MODE_PRIVATE
     * 部分值会频繁读取，需要缓存
     * @param key
     * @param value
     * @param toCache 是否需要缓存，true缓存、false不缓存
     */
    public void putString(String key, String value, boolean toCache) {
        putString(SHARED_NAME_BASE, SHARED_MODE_PRIVATE, key, value, toCache);
    }

    /**
     * 指定name、mode存储key、value
     * 
     * @param sharedName
     * @param sharedMode
     * @param key
     * @param value
     */
    public void putString(String sharedName, int sharedMode, String key, String value) {
        putString(sharedName, sharedMode, key, value, false);
    }

    /**
     * 指定name、mode存储key、value
     * 
     * @param sharedName
     * @param sharedMode
     * @param key
     * @param value
     * @param toCache
     */
    public void putString(String sharedName, int sharedMode, String key, String value, boolean toCache) {
        if (toCache) { // 缓存，方便快速读取
            if (TextUtils.isEmpty(value)) {
                mCacheString.put(sharedName + sharedMode + key, new WeakReference<String>(""));
            } else {
                mCacheString.put(sharedName + sharedMode + key, new WeakReference<String>(value));
            }
        }

        saveString(mContext.getSharedPreferences(sharedName, sharedMode), key, value);
    }

    private void saveString(SharedPreferences prefs, String key, String value) {
        Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 存储key、value，默认name=SHARED_NAME_BASE、value=SHARED_MODE_PRIVATE
     * 
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return getString(SHARED_NAME_BASE, SHARED_MODE_PRIVATE, key, defValue, false);
    }

    /**
     * 存储key、value，默认name=SHARED_NAME_BASE、value=SHARED_MODE_PRIVATE
     * 
     * @param key
     * @param defValue
     * @param toCache
     * @return
     */
    public String getString(String key, String defValue, boolean toCache) {
        return getString(SHARED_NAME_BASE, SHARED_MODE_PRIVATE, key, defValue, toCache);
    }

    public String getString(String sharedName, int sharedMode, String key, String defValue) {
        return getString(sharedName, sharedMode, key, defValue, false);
    }

    /**
     * 指定name、mode存储key、value
     * 
     * @param sharedName
     * @param sharedMode
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String sharedName, int sharedMode, String key, String defValue, boolean toCache) {
        if (mCacheString.containsKey(sharedName + sharedMode + key)) {
            WeakReference<String> ref = mCacheString.get(sharedName + sharedMode + key);
            if (ref.get() != null) {
                return ref.get();
            }
        }

        String value = mContext.getSharedPreferences(sharedName, sharedMode).getString(key, defValue);
        if (toCache) {
            if (TextUtils.isEmpty(value)) {
                mCacheString.put(sharedName + sharedMode + key, new WeakReference<String>(""));
            } else {
                mCacheString.put(sharedName + sharedMode + key, new WeakReference<String>(value));
            }
        }
        return value;
    }
    
    
    /**
     * 指定清空对应的xml文件
     * 
     * @param sharedName
     * @param sharedMode
     * @return
     */
    public void clearAllData(String sharedName, int sharedMode){
        SharedPreferences prefs = mContext.getSharedPreferences(sharedName, sharedMode);
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
    
}
